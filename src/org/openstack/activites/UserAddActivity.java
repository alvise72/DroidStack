package org.openstack.activities;

import android.os.Bundle;
import android.os.AsyncTask;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.util.Log;

import android.app.Activity;
import android.app.ProgressDialog;

import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;

import java.io.IOException;

import org.openstack.comm.RESTClient;
import org.openstack.utils.CustomProgressDialog;
import org.openstack.utils.User;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class UserAddActivity extends Activity {

    private boolean requesting_token = false;
    private org.openstack.utils.CustomProgressDialog progressDialogWaitStop = null;
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( org.openstack.R.layout.useradd );
    progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
    progressDialogWaitStop.setMessage( "Please wait. Connecting to remote server..." );
  }
  
  
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
  public void onResume( ) {
    super.onResume( );
  }
 
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
  @Override
  public void onPause( ) {
    super.onPause( );
  } 
  
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */    
    @Override
    public void onDestroy( ) {
      //ACTIVITY = null;
      super.onDestroy( );
      progressDialogWaitStop.dismiss();
    }
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */  
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
    
    progressDialogWaitStop.show();

    AsyncTaskRequestToken task = new AsyncTaskRequestToken();
    task.execute(endpoint,tenant,username,password,""+usessl);
    
    String jsonResponse = null;
    try {
      jsonResponse = task.get( ); // attende la fine del task senza congelare la UI
    } catch(InterruptedException ie) {
      Utils.alert( "ERROR: " + ie.getMessage(), this );
    } catch(ExecutionException ee ) {
      Utils.alert( "ERROR: " + ee.getMessage(), this );
    }
    if(jsonResponse == null || jsonResponse.length()==0) {
      Utils.alert( "ERROR: Unknown", this );
      return;
    }
    
    try {
      User U = ParseUtils.getToken( jsonResponse );
      U.setPassword(password);
      U.setEndpoint(endpoint);
      U.toFile( U.getUserName( ) );
      
      //final Set<String> users = Utils.getStringSetPreference("USERS", null, this);
      // Iterator<String> it = users.iterator();
//       Vector<String> newUsers = new Vector();
//       while( it.hasNext( ) ) {
//       	newUsers.add( it.next() );
//       }
//       newUsers.add( Base64.encodeBytes( U.serialize() ) );
      
      //User tmp = User.deserialize(  Base64.encodeBytes( U.serialize() ).getBytes( ) );
      
      //Utils.putStringSetPreference( "USERS", newUsers, this );
      
    } catch(Exception pe) {
      Utils.alert("ERROR: "+pe.getMessage(), this);
    } 
  }
  
    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    protected class AsyncTaskRequestToken extends AsyncTask<String, Void, String>
    {
     	private  String   errorMessage  = null;
	private  boolean  hasError      = false;
	private  String   jsonBuf       = null;
	
	protected String doInBackground( String... args ) 
	{
	   String endpoint = args[0];
	   String tenant   = args[1];
	   String username = args[2];
	   String password = args[3];
	   String s_usessl = args[4];
	   
	   boolean usessl = Boolean.parseBoolean( s_usessl );
	   
   	   try {
             jsonBuf = RESTClient.requestToken( endpoint, tenant, username, password, usessl );
     	   } catch(IOException e) {
	     errorMessage = e.getMessage();
	     hasError = true;
    	     return "";
    	   }
      
    	   return jsonBuf;
	}
	
	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    requesting_token = true;
	}
	
// 	@Override
// 	    protected void onProgressUpdate() {
// 	    super.onProgressUpdate();
// 	}
// 	
// 	@Override
// 	    protected void onCancelled() {
// 	    super.onCancelled();
// 	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, UserAddActivity.this );
 		requesting_token = false;
 		//ACTIVITY.progressDialogWaitStop.dismiss( );
		UserAddActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    requesting_token = false; // questo non va spostato da qui a
	    UserAddActivity.this.progressDialogWaitStop.dismiss( );
	    //ACTIVITY.progressDialogWaitStop.dismiss( );
	    //ACTIVITY.showImageList( jsonBuf );
	}
    }
}
