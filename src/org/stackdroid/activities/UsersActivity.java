package org.stackdroid.activities;

import android.os.Bundle;
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.stackdroid.views.UserView;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.NotExistingFileException;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.ImageButtonWithView;

public class UsersActivity extends Activity {

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
  protected class UserInfoListener implements OnClickListener {
	  @Override
	  public void onClick( View v ) {
		  User U = ((ImageButtonWithView)v).getUserView( ).getUser();
		  ScrollView sv = new ScrollView( UsersActivity.this );
		  TextView t1 = new TextView( UsersActivity.this );
		  t1.setText("Username:");
		  t1.setTypeface( null, Typeface.BOLD );
		  TextView t2 = new TextView( UsersActivity.this );
		  t2.setText("   " + U.getUserName());
		  TextView t3 = new TextView( UsersActivity.this );
		  t3.setText("Tenant:");
		  t3.setTypeface(null, Typeface.BOLD);
		  TextView t4 = new TextView( UsersActivity.this );
		  t4.setText("   "+U.getTenantName());
		  TextView t5 = new TextView( UsersActivity.this );
		  
		  t5.setText("Identity hostname:");
		  t5.setTypeface(null, Typeface.BOLD);
		  TextView t6 = new TextView( UsersActivity.this );
		  t6.setText("   " + U.getIdentityHostname() );
		  
		  TextView t7 = new TextView( UsersActivity.this );
		  t7.setText("Identity endpoint:");
		  t7.setTypeface(null, Typeface.BOLD);
		  TextView t8 = new TextView( UsersActivity.this );
		  t8.setText("   "+U.getIdentityEndpoint());
		  
		  TextView t9 = new TextView( UsersActivity.this );
		  t9.setText("Compute endpoint:");
		  t9.setTypeface(null, Typeface.BOLD);
		  TextView t10 = new TextView( UsersActivity.this );
		  t10.setText("   "+U.getNovaEndpoint());
		  
		  TextView t11 = new TextView( UsersActivity.this );
		  t11.setText("Network endpoint:");
		  t11.setTypeface(null, Typeface.BOLD);
		  TextView t12 = new TextView( UsersActivity.this );
		  t12.setText("   " + (U.getNeutronEndpoint()!=null ? U.getNeutronEndpoint() : "N/A"));
		  
		  TextView t13 = new TextView( UsersActivity.this );
		  t13.setText("Image endpoint:");
		  t13.setTypeface(null, Typeface.BOLD);
		  TextView t14 = new TextView( UsersActivity.this );
		  t14.setText("   " + (U.getGlanceEndpoint() != null ? U.getGlanceEndpoint(): "N/A"));
		  
		  TextView t15 = new TextView( UsersActivity.this );
		  t15.setText("Cinder v1 endpoint:");
		  t15.setTypeface(null, Typeface.BOLD);
		  TextView t16 = new TextView( UsersActivity.this );
		  t16.setText("   " + (U.getCinder1Endpoint()!=null ? U.getCinder1Endpoint() : "N/A") );

		  TextView t17 = new TextView( UsersActivity.this );
		  t17.setText("Cinder v2 endpoint:");
		  t17.setTypeface(null, Typeface.BOLD);
		  TextView t18 = new TextView( UsersActivity.this );
		  t18.setText("   " + (U.getCinder2Endpoint()!=null ? U.getCinder2Endpoint() : "N/A") );
	
		  TextView t19 = new TextView( UsersActivity.this );
		  t19.setText("SSL:");
		  t19.setTypeface(null, Typeface.BOLD);
		  TextView t20 = new TextView( UsersActivity.this );
		  t20.setText("   "+(U.useSSL() ? UsersActivity.this.getString(R.string.YES) : "no"));
		  

		  TextView t21 = new TextView( UsersActivity.this );
		  t21.setText("Token expiration:");
		  t21.setTypeface(null, Typeface.BOLD);
		  TextView t22 = new TextView( UsersActivity.this );
		  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/y H:mm:ss");
		  
		  t22.setText("   "+sdf.format(new Date(U.getTokenExpireTime()*1000)) );
		  
		  
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
		  l.addView( t1 );
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
		  
		  sv.addView(l);
		  Utils.alertInfo(sv, UsersActivity.this.getString(R.string.USERINFO), UsersActivity.this);
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
	    		//Log.d("USERS", "BAD file ["+users[i].getName( )+"]");
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
