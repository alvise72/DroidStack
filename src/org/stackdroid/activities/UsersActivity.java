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
		  ((TextView)promptsView.findViewById(R.id.keystoneHostname)).setText(U.getIdentityHostname());
		  ((TextView)promptsView.findViewById(R.id.keystoneURL)).setText(U.getIdentityEndpoint());
		  ((TextView)promptsView.findViewById(R.id.novaURL)).setText(U.getNovaEndpoint());
		  ((TextView)promptsView.findViewById(R.id.neutronURL)).setText(U.getNeutronEndpoint());
		  ((TextView)promptsView.findViewById(R.id.glanceURL)).setText(U.getGlanceEndpoint());
		  ((TextView)promptsView.findViewById(R.id.cinder1URL)).setText(U.getCinder1Endpoint());
		  ((TextView)promptsView.findViewById(R.id.cinder2URL)).setText(U.getCinder2Endpoint());
		  ((TextView)promptsView.findViewById(R.id.SSL)).setText(U.useSSL() ? getString(R.string.YES) : "No");
		  //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/y H:mm:ss");
		  Date d = new Date(U.getTokenExpireTime() * 1000);
		  String hd = d.toString();
		  ((TextView)promptsView.findViewById(R.id.tokenExpiration)).setText(hd);
		  ((TextView)promptsView.findViewById(R.id.verifyServerCert)).setText(U.getVerifyServerCert() ? getString(R.string.YES) : "No");

		  ((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new UsersActivity.OkUserInfoListener());
		  alertDialogUserInfo.setCanceledOnTouchOutside(false);
		  alertDialogUserInfo.setCancelable(false);
		  alertDialogUserInfo.show();
		  /*
		  ScrollView sv = new ScrollView( UsersActivity.this );
		  TextView t1 = new TextView( UsersActivity.this );
		  t1.setText(getString(R.string.USERNAME)+": ");
		  t1.setTypeface( null, Typeface.BOLD );
		  TextView t2 = new TextView( UsersActivity.this );
		  t2.setText("   " + U.getUserName());
		  TextView t3 = new TextView( UsersActivity.this );
		  t3.setText(getString(R.string.PROJECTNAME)+": ");
		  t3.setTypeface(null, Typeface.BOLD);
		  TextView t4 = new TextView( UsersActivity.this );
		  t4.setText("   "+U.getTenantName());
		  TextView t5 = new TextView( UsersActivity.this );
		  
		  t5.setText("Keystone hostname:");
		  t5.setTypeface(null, Typeface.BOLD);
		  TextView t6 = new TextView( UsersActivity.this );
		  t6.setText("   " + U.getIdentityHostname() );
		  
		  TextView t7 = new TextView( UsersActivity.this );
		  t7.setText("Keystone URL:");
		  t7.setTypeface(null, Typeface.BOLD);
		  TextView t8 = new TextView( UsersActivity.this );
		  t8.setText("   "+U.getIdentityEndpoint());
		  
		  TextView t9 = new TextView( UsersActivity.this );
		  t9.setText("Nova URL:");
		  t9.setTypeface(null, Typeface.BOLD);
		  TextView t10 = new TextView( UsersActivity.this );
		  t10.setText("   "+U.getNovaEndpoint());
		  
		  TextView t11 = new TextView( UsersActivity.this );
		  t11.setText("Neutron URL:");
		  t11.setTypeface(null, Typeface.BOLD);
		  TextView t12 = new TextView( UsersActivity.this );
		  t12.setText("   " + (U.getNeutronEndpoint()!=null ? U.getNeutronEndpoint() : "N/A"));
		  
		  TextView t13 = new TextView( UsersActivity.this );
		  t13.setText("Glance URL:");
		  t13.setTypeface(null, Typeface.BOLD);
		  TextView t14 = new TextView( UsersActivity.this );
		  t14.setText("   " + (U.getGlanceEndpoint() != null ? U.getGlanceEndpoint(): "N/A"));
		  
		  TextView t15 = new TextView( UsersActivity.this );
		  t15.setText("Cinder v1 URL:");
		  t15.setTypeface(null, Typeface.BOLD);
		  TextView t16 = new TextView( UsersActivity.this );
		  t16.setText("   " + (U.getCinder1Endpoint()!=null ? U.getCinder1Endpoint() : "N/A") );

		  TextView t17 = new TextView( UsersActivity.this );
		  t17.setText("Cinder v2 URL:");
		  t17.setTypeface(null, Typeface.BOLD);
		  TextView t18 = new TextView( UsersActivity.this );
		  t18.setText("   " + (U.getCinder2Endpoint()!=null ? U.getCinder2Endpoint() : "N/A") );
	
		  TextView t19 = new TextView( UsersActivity.this );
		  t19.setText("SSL:");
		  t19.setTypeface(null, Typeface.BOLD);
		  TextView t20 = new TextView( UsersActivity.this );
		  t20.setText("   "+(U.useSSL() ? UsersActivity.this.getString(R.string.YES) : "no"));
		  

		  TextView t21 = new TextView( UsersActivity.this );
		  t21.setText(getString(R.string.TOKENEXPIRATION)+":");
		  t21.setTypeface(null, Typeface.BOLD);
		  TextView t22 = new TextView( UsersActivity.this );
		  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/y H:mm:ss");
		  
		  t22.setText("   " + sdf.format(new Date(U.getTokenExpireTime() * 1000)));

		  TextView t23 = new TextView( UsersActivity.this );
		  t23.setText(getString(R.string.VERIFYSERVERCERT)+":");
		  t23.setTypeface(null, Typeface.BOLD);
		  TextView t24 = new TextView (UsersActivity.this );
		  //Log.d("USERSACTIVITY", "U.getVerifyServerCert()=" + U.getVerifyServerCert());
		  t24.setText("   " + (U.getVerifyServerCert() ? UsersActivity.this.getString(R.string.YES) : "no"));

		  TextView t25 = null;
		  TextView t26 = null;
		  if(U.getVerifyServerCert()) {
			  t25 = new TextView(UsersActivity.this);

			  t25.setText("CA File:");
			  t25.setTypeface(null, Typeface.BOLD);
			  t26 = new TextView(UsersActivity.this);
			  t26.setText("   " + U.getCAFile());
		  }

		  TextView t27 = null;
		  TextView t28 = null;
		  if(U.getVerifyServerCert()) {
			  t27 = new TextView(UsersActivity.this);
			  t27.setText("CA Issuer:");
			  t27.setTypeface(null, Typeface.BOLD);

			  t28 = new TextView(UsersActivity.this);
			  try {
				  t28.setText("   " + ((X509Certificate) (CertificateFactory.getInstance("X.509")).generateCertificate(new FileInputStream(U.getCAFile()))).getIssuerX500Principal().getName());
			  } catch(CertificateException ce) {t28.setText("");}
			  catch (FileNotFoundException e) {t28.setText("");}
		  }

		  LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
		  sv.setLayoutParams( lp );
		  LinearLayout l = new LinearLayout(UsersActivity.this);
		  l.setLayoutParams( lp );
		  l.setOrientation( LinearLayout.VERTICAL );
		  int paddingPixel = 8;
		  float density = Utils.getDisplayDensity( UsersActivity.this );
		  int paddingDp = (int)(paddingPixel * density);
		  l.setPadding(paddingDp, 0, 0, 0);
		  l.addView(t1);
		  l.addView( t2 );
		  l.addView( t3 );
		  l.addView( t4 );
		  l.addView( t5 );
		  l.addView( t6 );
		  l.addView( t7 );
		  l.addView( t8 );
		  l.addView( t9 );
		  l.addView( t10 );
		  l.addView( t11 );
		  l.addView( t12 );
		  l.addView( t13 );
		  l.addView( t14 );
		  l.addView( t15 );
		  l.addView( t16 );
		  l.addView( t17 );
		  l.addView( t18 );
		  l.addView( t19 );
		  l.addView( t20 );
		  l.addView( t21 );
		  l.addView( t22 );
		  l.addView( t23 );
		  l.addView( t24 );
		  if(t25!=null && t26!=null) {
			  l.addView(t25);
			  l.addView(t26);
		  }
		  if(t27!=null && t28!=null) {
			  l.addView(t27);
			  l.addView(t28);
		  }

		  sv.addView(l);
		  Utils.alertInfo(sv, UsersActivity.this.getString(R.string.USERINFO), UsersActivity.this);
		  */
		  //Utils.alertTitle(info, UsersActivity.this.getString(R.string.USERINFO), 12, UsersActivity.this);
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
