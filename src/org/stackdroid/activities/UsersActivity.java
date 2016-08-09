package org.stackdroid.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Typeface;
import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;

import org.stackdroid.R;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.stackdroid.views.UserView;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.NotExistingFileException;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.ImageButtonWithView;

public class UsersActivity extends Activity {

	private AlertDialog 		 alertDialogUserInfo	    = null;


	//__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.users );
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
    refreshUserViews();
    if(Utils.getStringPreference("SELECTEDUSER","",this).length()==0) {
	Toast t = Toast.makeText(this, getString(R.string.TOUCHUSERTOSELECT), Toast.LENGTH_SHORT) ;
	t.show( );
    }
  }
  
  //__________________________________________________________________________________
  public void addUser( View v ) {
    Class<?> c = (Class<?>)UserAddActivity.class;
    Intent I = new Intent( UsersActivity.this, c );
    startActivity( I );  
  }

  //__________________________________________________________________________________
  protected class UserDeleteListener implements OnClickListener {
	  @Override
	  public void onClick( View v ) { 
		  String filenameToDelete = ((ImageButtonWithView)v).getUserView( ).getUser().getFilename();
			
			(new File(Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) + "/users/"+filenameToDelete)).delete();
			String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", UsersActivity.this);
			if(selectedUser.compareTo(filenameToDelete)==0)
			    Utils.putStringPreference( "SELECTEDUSER", "", UsersActivity.this);
			
			refreshUserViews();
			return;
	  }
  }

  //__________________________________________________________________________________
  protected class UserSelectedListener implements OnClickListener {
	  @Override
	  public void onClick( View v ) {
		  //Log.d("USERS", "SELECTEDUSER="+((TextViewWithView)v).getUserView().getUser().getFilename());
		  Utils.putStringPreference("SELECTEDUSER", ((TextViewWithView)v).getUserView().getUser().getFilename(), UsersActivity.this);
		  refreshUserViews();
	  }
  }

	//__________________________________________________________________________________
	protected class OkUserInfoListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			alertDialogUserInfo.dismiss();
		}
	}

//__________________________________________________________________________________
  protected class UserInfoListener implements OnClickListener {
	  @Override
	  public void onClick( View v ) {
		  User U = ((ImageButtonWithView)v).getUserView( ).getUser();

		  LayoutInflater li = LayoutInflater.from(UsersActivity.this);

		  View promptsView = li.inflate(R.layout.my_dialog_user_info, null);

		  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UsersActivity.this);

		  alertDialogBuilder.setView(promptsView);

		  alertDialogBuilder.setTitle(getString(R.string.USERINFO));
		  alertDialogUserInfo = alertDialogBuilder.create();

		  ((TextView)promptsView.findViewById(R.id.userName)).setText(U.getUserName());
		  ((TextView)promptsView.findViewById(R.id.projectName)).setText(U.getTenantName());
		  
		  String IP = "";
		  if(U.getIdentityIP( ).length()>0)
		    IP = "\n(" + U.getIdentityIP( ) + ")";

		  ((TextView)promptsView.findViewById(R.id.keystoneHostname)).setText(U.getIdentityHostname() + IP );
		  ((TextView)promptsView.findViewById(R.id.keystoneURL)).setText(U.getIdentityEndpoint());
		  ((TextView)promptsView.findViewById(R.id.keystoneUseV3)).setText(U.useV3( ) ? R.string.YES : R.string.NO);
		  
		  ((TextView)promptsView.findViewById(R.id.novaURL)).setText(U.getNovaEndpoint());
		  ((TextView)promptsView.findViewById(R.id.neutronURL)).setText(U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER(  ) );
		  ((TextView)promptsView.findViewById(R.id.glanceURL)).setText(U.getGlanceEndpoint() + "/" + U.getGlanceEndpointAPIVER() );
		  ((TextView)promptsView.findViewById(R.id.cinder1URL)).setText(U.getCinder1Endpoint());
		  ((TextView)promptsView.findViewById(R.id.cinder2URL)).setText(U.getCinder2Endpoint());
		  ((TextView)promptsView.findViewById(R.id.SSL)).setText(U.useSSL() ? getString(R.string.YES) : "No");

		  ((TextView)promptsView.findViewById(R.id.verifyServerCert)).setText(U.getVerifyServerCert() ? getString(R.string.YES) : "No");

		  ((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new UsersActivity.OkUserInfoListener());
		  alertDialogUserInfo.setCanceledOnTouchOutside(false);
		  alertDialogUserInfo.setCancelable(false);
		  alertDialogUserInfo.show();
		  
	  }
  }

    //__________________________________________________________________________________
    private void refreshUserViews( ) {
    	File[] users = (new File( Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) + "/users/")).listFiles();
    	if(users==null) {
    		Utils.alert("UsersActivity.refreshUserViews: " 
    				+ Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) 
    				+ "/users/" 
    				+ " exists but it is not a directory !", this);
    		return;
    	}
    
	((LinearLayout)findViewById(R.id.userLayout)).removeAllViews();
	UserView lastUV = null;
	for(int i = 0; i<users.length; ++i) {
	    User U = null;
	    try {
	    	//Log.d("USERS", "handling file ["+users[i].getName( )+"]");
	    	if(users[i].getName( ).matches("[a-zA-Z0-9]+\\.[a-zA-Z0-9]+\\.[-]?[0-9]+")) {
	    		//Log.d("USERS", "OK file ["+users[i].getName( )+"]");
	    		U = User.fromFileID( users[i].getName( ), Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
	    		Log.d("UsersActivity.refreshUserViews", "glance api ver="+U.getGlanceEndpointAPIVER() + " - neutron api ver="+U.getNeutronEndpointAPIVER( ) );
	    		if(U==null) {
	    			Utils.alert(getString(R.string.RECREATEUSERS), this);
	    			return;
	    		}
	    	} else {
	    		continue;
	    	}
	    }  catch(ClassNotFoundException cnfe) {
  		  Utils.putStringPreference("SELECTEDUSER", "", this);
  		  (new File(Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) + "/users/" + users[i].getName( ))).delete();
      	  return;
  	  	} catch(NotExistingFileException nf) {
  		  Utils.putStringPreference("SELECTEDUSER", "", this);
  		  return;
        } catch(IOException ioe) {
  		  Utils.alert("ERROR: "+ioe.getMessage() + "\n\n"+getString(R.string.RECREATEUSERS), this);
  		  return;
  	    } catch(Exception e) {
  		  Utils.alert("ERROR: "+e.getMessage(), this );
  		  return;
  	    } 
	    
	    UserView uv = new UserView ( U, 
	    							 new UsersActivity.UserDeleteListener(), 
	    							 new UsersActivity.UserSelectedListener(),
	    							 new UsersActivity.UserInfoListener(), 
	    							 this );
	    ((LinearLayout)findViewById(R.id.userLayout)).addView( uv );
	    View space = new View( this );
	    space.setMinimumHeight(10);
	    ((LinearLayout)findViewById(R.id.userLayout)).addView( space );
	    
	    if( uv.getUser().getFilename().compareTo(Utils.getStringPreference("SELECTEDUSER","",this))==0 )
	    	uv.setSelected( );
	    else
	    	uv.setUnselected( );
	    lastUV = uv;
	}
	if(users.length==1) {
		if(lastUV!=null) {
			lastUV.setSelected( );
			Utils.putStringPreference("SELECTEDUSER",lastUV.getUser().getFilename(),this);
		}
	}
	
    }
}
