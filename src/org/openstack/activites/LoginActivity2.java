package org.openstack.activities;

//import android.webkit.WebView;

import android.os.Bundle;
import android.os.Environment;


import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.net.Uri;

import android.util.Log;
import android.util.DisplayMetrics;

import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.app.Activity;

import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import java.io.IOException;

import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.UserException;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;
import org.openstack.comm.RESTClient;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import java.io.File;

import org.openstack.utils.UserView;
import org.openstack.utils.Named;
import org.openstack.utils.ImageViewNamed;
import org.openstack.utils.TextViewNamed;
import org.openstack.utils.ImageButtonNamed;

public class LoginActivity2 extends Activity implements OnClickListener {

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.login2 );
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
    
    refreshUserViews();
  }
  
  //__________________________________________________________________________________
  public void addUser( View v ) {
    Class<?> c = (Class<?>)UserAddActivity.class;
    Intent I = new Intent( LoginActivity2.this, c );
    startActivity( I );  
  }

  //__________________________________________________________________________________
  @Override
    public void onPause( ) {
      super.onPause( );
    } 

  //__________________________________________________________________________________
    public void onClick( View v ) { 
	if(v instanceof ImageButtonNamed) {
	    if(((ImageButtonNamed)v).getType( ) == Named.BUTTON_DELETE_USER ) {
		String usernameToDelete = ((ImageButtonNamed)v).getExtras( );
		(new File(Environment.getExternalStorageDirectory() + "/AndroStack/users/"+usernameToDelete)).delete();
		String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
		if(selectedUser.compareTo(usernameToDelete)==0)
		    Utils.putStringPreference( "SELECTEDUSER", "", this);
		    
		refreshUserViews();
		return;
	    }
	    if(((ImageButtonNamed)v).getType( ) == Named.BUTTON_MODIFY_USER ) {
		Utils.alert("Not implemented yet." , this);
		return;
	    }
	}

	if(v instanceof TextViewNamed) {
	    String selectedUser = ((TextViewNamed)v).getExtras();
	    Utils.putStringPreference("SELECTEDUSER", selectedUser, this);
	    Toast t = Toast.makeText(this, "Selected user: "+selectedUser, Toast.LENGTH_SHORT);
	    t.setGravity( Gravity.CENTER, 0, 0 );
	    t.show();
	    return;
	}
    }

    //__________________________________________________________________________________
    private void refreshUserViews( ) {
	File[] users = (new File(Environment.getExternalStorageDirectory() + "/AndroStack/users/")).listFiles();
	LinearLayout usersL = (LinearLayout)findViewById(R.id.userLayout);
	usersL.removeAllViews();
	for(int i = 0; i<users.length; ++i) {
	    User U = null;
	    try {
		U = Utils.userFromFile( users[i].toString() );
		//Utils.alert(U.toString(), this );
	    } catch(Exception e) {
		Utils.alert("ERROR: " + e.getMessage(), this);
		continue;
	    }
	    UserView uv = new UserView ( U, this );
	    usersL.addView( uv );
	}
    }
}
