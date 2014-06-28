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

public class LoginActivity2 extends Activity {

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
}
