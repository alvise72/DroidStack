package org.stackdroid.activities;

import java.io.IOException;

import android.os.Bundle; 
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.EditText;
import android.widget.CheckBox;
import android.app.Activity;
import android.app.ProgressDialog;
import android.text.InputType;
import android.view.WindowManager;
import android.view.View;

import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.comm.RESTClient;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.R;

public class UserAddActivity extends Activity {

    private org.stackdroid.utils.CustomProgressDialog progressDialogWaitStop = null;

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
    setContentView( org.stackdroid.R.layout.useradd );
    progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
    progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
    progressDialogWaitStop.setCancelable(false);
    progressDialogWaitStop.setCanceledOnTouchOutside(false);
    String last_endpoint = Utils.getStringPreference("LAST_ENDPOINT", "", this);
    String last_tenant   = Utils.getStringPreference("LAST_TENANT", "", this);
    String last_username = Utils.getStringPreference("LAST_USERNAME", "", this);
    String last_password = Utils.getStringPreference("LAST_PASSWORD", "", this);
    boolean usessl       = Utils.getBoolPreference("LAST_USESSL", false, this);
    boolean showPWD      = Utils.getBoolPreference("LAST_SHOWPWD", false, this);
    ((EditText)findViewById(R.id.endpointET)).setText( last_endpoint );
    ((EditText)findViewById(R.id.tenantnameET)).setText( last_tenant );
    ((EditText)findViewById(R.id.usernameET)).setText( last_username );
    ((EditText)findViewById(R.id.passwordET)).setText( last_password );
    ((CheckBox)findViewById(R.id.usesslCB)).setChecked( usessl );
    ((CheckBox)findViewById(R.id.checkBoxPWD)).setChecked( showPWD );
    
    EditText pwd = (EditText)this.findViewById(R.id.passwordET);
    CheckBox showpwd = (CheckBox)this.findViewById(R.id.checkBoxPWD);
    if(showpwd.isChecked() == false) {
    	pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		pwd.setSelection(pwd.getText().length());
    }
    else
    	pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
      Utils.putStringPreference("LAST_ENDPOINT", ((EditText)findViewById(R.id.endpointET)).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_TENANT",   ((EditText)findViewById(R.id.tenantnameET)  ).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_USERNAME", ((EditText)findViewById(R.id.usernameET)).getText().toString().trim(), this);
      Utils.putStringPreference("LAST_PASSWORD", ((EditText)findViewById(R.id.passwordET)).getText().toString().trim(), this);     
      Utils.putBoolPreference("LAST_USESSL", ((CheckBox)findViewById(R.id.usesslCB)).isChecked( ), this);
      Utils.putBoolPreference("LAST_SHOWPWD", ((CheckBox)findViewById(R.id.checkBoxPWD)).isChecked(), this);
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
    EditText endpointET = (EditText)findViewById(org.stackdroid.R.id.endpointET);
    EditText tenantET   = (EditText)findViewById(org.stackdroid.R.id.tenantnameET);
    EditText usernameET = (EditText)findViewById(org.stackdroid.R.id.usernameET);
    EditText passwordET = (EditText)findViewById(org.stackdroid.R.id.passwordET);
    CheckBox usesslET   = (CheckBox)findViewById(org.stackdroid.R.id.usesslCB);
    
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
  public void showPWD( View v ) {
	CheckBox showpwd = (CheckBox)v;
	EditText pwd = (EditText)this.findViewById(R.id.passwordET);
	if(showpwd.isChecked()==false) {
		pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		pwd.setSelection(pwd.getText().length());
	} else {
		pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
  public void reset( View v ) {
    EditText endpointET = (EditText)findViewById(org.stackdroid.R.id.endpointET);
    EditText tenantET   = (EditText)findViewById(org.stackdroid.R.id.tenantnameET);
    EditText usernameET = (EditText)findViewById(org.stackdroid.R.id.usernameET);
    EditText passwordET = (EditText)findViewById(org.stackdroid.R.id.passwordET);
    CheckBox usesslET   = (CheckBox)findViewById(org.stackdroid.R.id.usesslCB);
    
    endpointET.setText("");
    tenantET.setText("");
    usernameET.setText("");
    passwordET.setText("");
    usesslET.setChecked( false );
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
    protected void completeUserAdd( User U ) {
    	
    	
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
    protected class AsyncTaskRequestToken extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage  = null;
     	private  boolean  hasError      = false;
     	private  String   jsonBuf       = null;
	
     	private String endpoint = null;
     	private String password = null;
     	private boolean usessl;
     	private User U = null;
     	
     	@Override
     	protected Void doInBackground( String... args ) 
     	{
     		
     		endpoint = args[0];
     		String tenant   = args[1];
     		String username = args[2];
     		password = args[3];
     		String s_usessl = args[4];
     		
     		usessl = Boolean.parseBoolean( s_usessl );
     		
     		try {
     			jsonBuf = RESTClient.requestToken( usessl, (usessl ? "https://" : "http://") + endpoint + ":5000/v2.0/tokens", tenant, username, password );
     			//UserAddActivity.this.completeUserAdd( jsonBuf, password, endpoint, usessl );
     			if(jsonBuf == null || jsonBuf.length()==0) {
     				hasError = true;
     				errorMessage = "Server's response buffer is NULL or empty!";
     				return null;
     			}
     			
     			U = ParseUtils.parseUser( jsonBuf );
     			U.setPassword(password);
     			U.setSSL( usessl );
     			
     				
     		} catch(Exception e) {
     			errorMessage = e.getMessage();
     			hasError = true;
     		}
     		return null;
     	}
	
     	@Override
     		protected void onPostExecute( Void v ) {
     		super.onPostExecute( v );
	    
     		if(hasError) {	
     			UserAddActivity.this.progressDialogWaitStop.dismiss( );
     			Utils.alert( errorMessage, UserAddActivity.this );
 				UserAddActivity.this.progressDialogWaitStop.dismiss( );
 				return;
     		}
     		// se metto questo in doInBackgroud genera il problema di Looper.prepare()
     		Utils.alert(getString(R.string.ADDSUCCESS), UserAddActivity.this);
     		try {
     			U.toFile( Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
     		} catch(IOException ioe) {
     			;
     		}
     		UserAddActivity.this.progressDialogWaitStop.dismiss( );
     		//UserAddActivity.this.completeUserAdd( U );
     	}
    }
}
