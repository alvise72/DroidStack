package org.openstack.activities;

//import android.webkit.WebView;

import android.os.Bundle;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

//import android.net.Uri;

import android.util.Log;
//import android.util.DisplayMetrics;

//import android.app.ActivityManager.MemoryInfo;
//import android.app.AlertDialog;
//import android.app.ActivityManager;
import android.app.Activity;

import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

//import java.io.IOException;

//import java.util.Vector;

//import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;
// import org.openstack.comm.RESTClient;
// import org.openstack.parse.ParseUtils;
// import org.openstack.parse.ParseException;

public class UserAddActivity extends Activity {

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( org.openstack.R.layout.useradd );
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
  }
  
  @Override
  public void onPause( ) {
    super.onPause( );
  } 
  
  public void add( View v ) {
    EditText endpointET = (EditText)findViewById(org.openstack.R.id.endpointET);
    EditText tenantET   = (EditText)findViewById(org.openstack.R.id.tenantnameET);
    EditText usernameET = (EditText)findViewById(org.openstack.R.id.usernameET);
    EditText passwordET = (EditText)findViewById(org.openstack.R.id.passwordET);
    CheckBox usesslET   = (CheckBox)findViewById(org.openstack.R.id.usesslCB);
    
    String  endpoint = endpointET.getText().toString().trim();
    String  tenant   = tenantET.getText().toString().trim();
    String  username = usernameET.getText().toString().trim();
    String  password = passwordET.getText().toString().trim();
    boolean usessl   = usesslET.isChecked();
    
    if( endpoint.length()==0 ) {
      Utils.alert("Please fill the endpoint field.", this);
      return;
    }
    if( tenant.length()==0 ) {
      Utils.alert("Please fill the tenant field.", this);
      return;
    }
    if( username.length()==0 ) {
      Utils.alert("Please fill the username field.", this);
      return;
    }
    if( password.length()==0 ) {
      Utils.alert("Please fill the password field.", this);
      return;
    }
    
    
    
     
//      User U = new User( username.getText().toString().trim(),
//      			tenant.gettext().toString().trim(),
			
  }
}
