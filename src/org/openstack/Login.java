package org.openstack;

import android.app.*;
import android.widget.*;
import android.webkit.WebView;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.*;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import android.os.*;
import android.view.View.OnClickListener;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.app.ActivityManager.MemoryInfo;
import android.content.pm.ActivityInfo;
import android.app.ActivityManager;
import android.app.Activity;

//import java.util.Calendar;
import java.io.IOException;

import java.util.Vector;

public class Login extends Activity {

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.login );
    
     if(Utils.getBoolPreference("USESSL", false, this ) ) {
       ((CheckBox)findViewById(R.id.ssl)).setChecked( true );
     } else {
	((CheckBox)findViewById(R.id.ssl)).setChecked( false );
     }
    
    toggleSSL( findViewById(R.id.ssl) );
    
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
    
    String selected_ca_file = Utils.getStringPreference("SELECTED_CA_FILE", null, this);
    if(selected_ca_file != null) 
    {
      ((TextView)findViewById(R.id.showCAFile)).setText( selected_ca_file );
    }
    
    

    String last_endpoint = Utils.getStringPreference("LAST_ENDPOINT", "", this);
    String last_tenant   = Utils.getStringPreference("LAST_TENANT", "", this);
    String last_username = Utils.getStringPreference("LAST_USERNAME", "", this);
    String last_password = Utils.getStringPreference("LAST_PASSWORD", "", this);

    ((EditText)findViewById(R.id.endpointE)).setText( last_endpoint );
    ((EditText)findViewById(R.id.tenantE)).setText( last_tenant );
    ((EditText)findViewById(R.id.usernameE)).setText( last_username );
    ((EditText)findViewById(R.id.passwordE)).setText( last_password );

    if(last_password.length()!=0 &&
       last_tenant.length()!=0 &&
       last_username.length()!=0 &&
       last_endpoint.length()!=0) {
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    
    

  }
  
  //__________________________________________________________________________________
  public void enableSSL(View v) {
    toggleSSL( v );
  }

  //__________________________________________________________________________________
  private void toggleSSL( View v ) {
    boolean checked = ((CheckBox)v).isChecked( );
    
    Utils.putBoolPreference("USESSL", checked, this );
    
    if(!checked) {
      ((Button)findViewById(R.id.browseCAFILE)).setVisibility(View.INVISIBLE);
      ((TextView)findViewById(R.id.showCAFile)).setVisibility(View.INVISIBLE);
    }
    else  {
      ((Button)findViewById(R.id.browseCAFILE)).setVisibility(View.VISIBLE);
      ((TextView)findViewById(R.id.showCAFile)).setVisibility(View.VISIBLE);
    }
  }
  
  //__________________________________________________________________________________
  public void browseCAFile( View v) {
    Class<?> c = (Class<?>)FileExplore.class;
    Intent I = new Intent( Login.this, c );
    startActivity( I );
  }
  
  //__________________________________________________________________________________
  public void authorize( View v ) {
    if( !Utils.internetOn( this ) )
          Utils.alert( "ERROR: The device is not connected to Internet. This App cannot work.", this );
	  
    String endpoint = (((EditText)findViewById(R.id.endpointE)).getText( ).toString( )).trim( );
    String tenant   = (((EditText)findViewById(R.id.tenantE)).getText( ).toString( )).trim( );
    String username = (((EditText)findViewById(R.id.usernameE)).getText( ).toString( )).trim( );
    String password = (((EditText)findViewById(R.id.passwordE)).getText( ).toString( )).trim( );
    boolean usessl  = ((CheckBox)findViewById(R.id.ssl)).isChecked( );
    
    if(endpoint.length() == 0 ) {
      Toast t = Toast.makeText(this, "You must provide the endpoint in the form of <hostname>", Toast.LENGTH_LONG);
      t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
      t.show( );
      return;
    }
    if(tenant.length() == 0) {
      //Log.d("Login.authorize", "tenant empty!");
      Toast t = Toast.makeText(this, "You must provide the tenant name", Toast.LENGTH_LONG);
      t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
      t.show( );
      return;
    }
    if(username.length() == 0) {
      Toast t = Toast.makeText(this, "You must provide the username", Toast.LENGTH_LONG);
      t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
      t.show( );
      return;
    }
    if(password.length() == 0) {
      Toast t = Toast.makeText(this, "You must provide the password", Toast.LENGTH_LONG);
      t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
      t.show( );
      return;
    }
    if(usessl) {
      String cafile = Utils.getStringPreference("SELECTED_CA_FILE", null, this);
      if(cafile == null || cafile.length()==0) {
        Toast t = Toast.makeText(this, "You've chosen SSL, but haven't provided the CA File", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        t.show( );
        return;
      }
    }
    
    String jsonResponse = null;
    try {
	jsonResponse = RESTClient.requestToken( endpoint, tenant, username, password, usessl );
    } catch(IOException e) {
	Utils.alert( "ERROR: " + e.getMessage( ), this );
	return;
    }
    try {
	User U = ParseUtils.getToken( jsonResponse );
	Log.d("Login", U.toString( ) );
	U.setPassword(password);
	String S = new String(U.serialize());
	Utils.alert( "SER: "+S, this );
	Utils.putStringPreference( "TOKEN_STRING", U.getToken(), this );
	Utils.putLongPreference( "TOKEN_EXPIRATION", U.getTokenExpireTime( ), this );
	Utils.putStringPreference( "TENANT_ID", U.getTenantID( ), this );
	Utils.alert("SUCCESS!\nYou can now go back and interact with OpenStack...", this);
    } catch(ParseException pe) {
	Utils.alert( "ERROR: "+pe.getMessage( ), this );
	return;
    } 
  }
  



  @Override
    public void onPause( ) {
      super.onPause( );
      Utils.putStringPreference("LAST_ENDPOINT", ((EditText)findViewById(R.id.endpointE)).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_TENANT",   ((EditText)findViewById(R.id.tenantE)  ).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_USERNAME", ((EditText)findViewById(R.id.usernameE)).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_PASSWORD", ((EditText)findViewById(R.id.passwordE)).getText().toString().trim(), this);     
    } 
    
  //__________________________________________________________________________________
  public void makeTExpire( View v ) {
    Utils.putLongPreference( "TOKEN_EXPIRATION", Utils.now( ), this );
    Utils.alert("ERROR: Token is now expired", this);
  }
}
