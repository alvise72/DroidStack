package org.openstack.activities;

//import android.webkit.WebView;

import android.os.Bundle;
import android.os.Environment;

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

public class LoginActivity2 extends Activity {

  //private final ListView userListview = null;
  
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
    try {
      User u = Utils.userFromFile( Environment.getExternalStorageDirectory() + "/AndroStack/users/admin" );
      Utils.alert(u.toString(), this );
    } catch(Exception ex) {
      Utils.alert(ex.getMessage( ), this);
    }
    //Set<String> users = Utils.getStringSetPreference( "USERS", null, this );
    //if(users == null) return;
    //Iterator<String> it = users.iterator();
//     while( it.hasNext()) {
//       String userSER = it.next( );
//       try {
//         User U = User.deserialize( userSER.getBytes( ) );
//         Utils.alert( U.toString( ), this );
//       } catch(UserException ue ) {
//         Utils.alert( ue.getMessage( ), this );
//       }
//     }
    
    
//     String selected_ca_file = Utils.getStringPreference("SELECTED_CA_FILE", null, this);
//     if(selected_ca_file != null) 
//     {
//       ((TextView)findViewById(R.id.showCAFile)).setText( selected_ca_file );
//     }
//     
//     
// 

    
    

  }
  
  //__________________________________________________________________________________
//   public void enableSSL(View v) {
//     toggleSSL( v );
//   }

  //__________________________________________________________________________________
//   private void toggleSSL( View v ) {
//     boolean checked = ((CheckBox)v).isChecked( );
//     
//     Utils.putBoolPreference("USESSL", checked, this );
//     
//     if(!checked) {
//       ((Button)findViewById(R.id.browseCAFILE)).setVisibility(View.INVISIBLE);
//       ((TextView)findViewById(R.id.showCAFile)).setVisibility(View.INVISIBLE);
//     }
//     else  {
//       ((Button)findViewById(R.id.browseCAFILE)).setVisibility(View.VISIBLE);
//       ((TextView)findViewById(R.id.showCAFile)).setVisibility(View.VISIBLE);
//     }
//   }
  
  //__________________________________________________________________________________
//   public void browseCAFile( View v) {
//     Class<?> c = (Class<?>)CAFileExploreActivity.class;
//     Intent I = new Intent( LoginActivity.this, c );
//     startActivity( I );
//   }
  
  //__________________________________________________________________________________
//   public void authorize( View v ) {
//     if( !Utils.internetOn( this ) )
//           Utils.alert( "ERROR: The device is not connected to Internet. This App cannot work.", this );
// 	  
//     String endpoint = (((EditText)findViewById(R.id.endpointE)).getText( ).toString( )).trim( );
//     String tenant   = (((EditText)findViewById(R.id.tenantE)).getText( ).toString( )).trim( );
//     String username = (((EditText)findViewById(R.id.usernameE)).getText( ).toString( )).trim( );
//     String password = (((EditText)findViewById(R.id.passwordE)).getText( ).toString( )).trim( );
//     boolean usessl  = ((CheckBox)findViewById(R.id.ssl)).isChecked( );
//     
//     if(endpoint.length() == 0 ) {
//       Toast t = Toast.makeText(this, "You must provide the endpoint in the form of <hostname>", Toast.LENGTH_LONG);
//       t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//       t.show( );
//       return;
//     }
//     if(tenant.length() == 0) {
//       //Log.d("Login.authorize", "tenant empty!");
//       Toast t = Toast.makeText(this, "You must provide the tenant name", Toast.LENGTH_LONG);
//       t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//       t.show( );
//       return;
//     }
//     if(username.length() == 0) {
//       Toast t = Toast.makeText(this, "You must provide the username", Toast.LENGTH_LONG);
//       t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//       t.show( );
//       return;
//     }
//     if(password.length() == 0) {
//       Toast t = Toast.makeText(this, "You must provide the password", Toast.LENGTH_LONG);
//       t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//       t.show( );
//       return;
//     }
//     if(usessl) {
//       String cafile = Utils.getStringPreference("SELECTED_CA_FILE", null, this);
//       if(cafile == null || cafile.length()==0) {
//         Toast t = Toast.makeText(this, "You've chosen SSL, but haven't provided the CA File", Toast.LENGTH_LONG);
//         t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
//         t.show( );
//         return;
//       }
//     }
//     
//     String jsonResponse = null;
//     try {
// 	jsonResponse = RESTClient.requestToken( endpoint, tenant, username, password, usessl );
//     } catch(IOException e) {
// 	Utils.alert( "IOException: " + e.getMessage( ), this );
// 	return;
//     }
// 
//     try {
// 	User U = ParseUtils.getToken( jsonResponse );
// 	U.setPassword(password);
// 	U.setEndpoint(endpoint);
// 	Utils.putStringPreference( "USER", Base64.encodeBytes( U.serialize() ), this );
// 	Utils.alert("SUCCESS!\nYou can now go back and interact with OpenStack...", this);
//     } catch(ParseException pe) {
// 	Utils.alert( "ParseException: "+pe.getMessage( ), this );
// 	return;
//     } // catch(IOException ioe) {
// // 	Utils.alert( "IOException: "+ioe.getMessage( ), this );
// //     }
//   }
  
  public void addUser( View v ) {
    Class<?> c = (Class<?>)UserAddActivity.class;
    Intent I = new Intent( LoginActivity2.this, c );
    startActivity( I );  
  }


  @Override
    public void onPause( ) {
      super.onPause( );
    } 
    
  //__________________________________________________________________________________
//   public void makeTExpire( View v ) {
//     Utils.putLongPreference( "TOKEN_EXPIRATION", Utils.now( ), this );
//     Utils.alert("ERROR: Token is now expired", this);
//   }
}
