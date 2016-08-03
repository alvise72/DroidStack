package org.stackdroid.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
//import java.util.ArrayList;

import android.os.Bundle; 
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Button;
import android.app.Activity;
import android.app.ProgressDialog;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.view.View;
import android.content.Intent;

import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.comm.RESTClient;
import org.stackdroid.comm.OSClient;
import org.stackdroid.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserAddActivity extends Activity {

    private org.stackdroid.utils.CustomProgressDialog progressDialogWaitStop = null;
    
    private boolean m_validcafile = false;
    private boolean m_useV3 = false;
    private static final int FILE_CHOOSER = 11;
    
    
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
	
	String last_endpoint     = Utils.getStringPreference("LAST_ENDPOINT", "", this);
	String last_tenant       = Utils.getStringPreference("LAST_TENANT", "", this);
	String last_username     = Utils.getStringPreference("LAST_USERNAME", "", this);
	String last_password     = Utils.getStringPreference("LAST_PASSWORD", "", this);
	boolean usessl           = Utils.getBoolPreference("LAST_USESSL", false, this);
	boolean showPWD          = Utils.getBoolPreference("LAST_SHOWPWD", false, this);
	boolean verifyservercert = Utils.getBoolPreference("LAST_VERIFYSERVERCERT", false, this);
	String last_cafile       = Utils.getStringPreference("LAST_CAFILE", "", this);
	boolean useV3		 = Utils.getBoolPreference("LAST_USEV3", false, this);
	
	((EditText)findViewById(R.id.endpointET)).setText( last_endpoint );
	((EditText)findViewById(R.id.tenantnameET)).setText(last_tenant);
	((EditText)findViewById(R.id.usernameET)).setText(last_username);
	((EditText)findViewById(R.id.passwordET)).setText(last_password);
	((CheckBox)findViewById(R.id.usesslCB)).setChecked(usessl);
	((CheckBox)findViewById(R.id.checkBoxPWD)).setChecked(showPWD);
	((CheckBox)findViewById(R.id.useV3)).setChecked(useV3);
	
	
	
	if(!usessl) {
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setChecked(false);
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setEnabled(false);
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(false);
	    ((TextView)findViewById(R.id.CAFILE)).setText("");
	} else {
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setEnabled(true);
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setChecked(verifyservercert);
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(verifyservercert);
	    ((TextView)findViewById(R.id.CAFILE)).setText(last_cafile);
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(true);
	}
	
	boolean verifyserverca = ((CheckBox)findViewById(R.id.verifyServerCertCB)).isEnabled() && ((CheckBox)findViewById(R.id.verifyServerCertCB)).isChecked();
	
	if(usessl && verifyserverca)
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(true);
	else
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(false);
	
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
	super.onResume();
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
	super.onPause();
	boolean usessl = ((CheckBox) findViewById(R.id.usesslCB)).isChecked();
	Utils.putStringPreference("LAST_ENDPOINT", ((EditText) findViewById(R.id.endpointET)).getText().toString().trim(), this);
	Utils.putStringPreference("LAST_TENANT", ((EditText) findViewById(R.id.tenantnameET)).getText().toString().trim(), this);
	Utils.putStringPreference("LAST_USERNAME", ((EditText)findViewById(R.id.usernameET)).getText().toString().trim(), this);
	Utils.putStringPreference("LAST_PASSWORD", ((EditText)findViewById(R.id.passwordET)).getText().toString().trim(), this);
	Utils.putBoolPreference("LAST_USESSL", usessl, this);
	Utils.putBoolPreference("LAST_SHOWPWD", ((CheckBox) findViewById(R.id.checkBoxPWD)).isChecked(), this);
	Utils.putBoolPreference("LAST_USEV3", ((CheckBox) findViewById(R.id.useV3)).isChecked(), this);
	if(usessl) {
	    Utils.putBoolPreference("LAST_VERIFYSERVERCERT", ((CheckBox) findViewById(R.id.verifyServerCertCB)).isChecked(), this);
	    Utils.putStringPreference("LAST_CAFILE", ((TextView) findViewById(R.id.CAFILE)).getText().toString(), this);
	} else {
	    Utils.putBoolPreference("LAST_VERIFYSERVERCERT", false, this);
	    Utils.putStringPreference("LAST_CAFILE", "", this);
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
    @Override
    public void onDestroy( ) {
	super.onDestroy();
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
	EditText endpointET = (EditText)findViewById(R.id.endpointET);
	EditText tenantET   = (EditText)findViewById(R.id.tenantnameET);
	EditText usernameET = (EditText)findViewById(R.id.usernameET);
	EditText passwordET = (EditText)findViewById(R.id.passwordET);
	CheckBox usesslET   = (CheckBox)findViewById(R.id.usesslCB);
	CheckBox verifyServerCert = (CheckBox)findViewById(R.id.verifyServerCertCB);
	TextView CAFile = (TextView)findViewById(R.id.CAFILE);
	
	if(!Utils.isValid(new File(CAFile.getText().toString()))) {
	    verifyServerCert.setChecked(false);
	    CAFile.setText("");
	}
	
	String  endpoint = endpointET.getText().toString().trim();
	String  tenant   = tenantET.getText().toString().trim();
	String  username = usernameET.getText().toString().trim();
	String  password = passwordET.getText().toString().trim();
	boolean usessl   = usesslET.isChecked();
	m_useV3          = ((CheckBox)findViewById(R.id.useV3)).isChecked();
	
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
	task.execute(endpoint,tenant,username,password,""+usessl,""+verifyServerCert.isChecked(),CAFile.getText().toString());
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
    	((EditText)findViewById(org.stackdroid.R.id.endpointET)).setText("");
    	((EditText)findViewById(org.stackdroid.R.id.tenantnameET)).setText("");
    	((EditText)findViewById(org.stackdroid.R.id.usernameET)).setText("");
    	((EditText)findViewById(org.stackdroid.R.id.passwordET)).setText("");
    	((CheckBox)findViewById(org.stackdroid.R.id.usesslCB)).setChecked(false);
    	((CheckBox)findViewById(org.stackdroid.R.id.verifyServerCertCB)).setChecked( false );
    	((Button)findViewById(org.stackdroid.R.id.selectCABT)).setEnabled(false);
        ((TextView)findViewById(R.id.CAFILE)).setText("");
        ((CheckBox)findViewById(R.id.useV3)).setChecked(false);
        m_validcafile=false;
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
    public void toggleSelectCA( View v ) {
	((Button)(findViewById(R.id.selectCABT))).setEnabled(((CheckBox) v).isChecked());
	((TextView)findViewById(R.id.CAFILE)).setText("");
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
    public void toggleV3( View v ) {
	if( ((CheckBox)v).isChecked())
	    m_useV3 = true;
	else
	    m_useV3 = false;
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
    public void toggleUseSSL( View v ) {
	CheckBox ssl = (CheckBox)v;
	if(!ssl.isChecked()) {
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setChecked(false);
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setEnabled(false);
	    //((CheckBox)findViewById(R.id.useV3)).setEnabled(false);
	    ((Button)findViewById(R.id.selectCABT)).setEnabled(false);
	    ((TextView)findViewById(R.id.CAFILE)).setText("");
	    m_validcafile=false;
	} else {
	    ((CheckBox)findViewById(R.id.verifyServerCertCB)).setEnabled(true);
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
    public void selectCA( View v ) {
	Intent intent = new Intent(this, FileChooser.class);
	intent.putStringArrayListExtra("filterFileExtension", null);
	startActivityForResult(intent, FILE_CHOOSER);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	m_validcafile=false;
	if(data!=null) {
	    String result=data.getStringExtra("fileSelected");
	    if(!Utils.isValid(new File(result))) {
		((TextView)findViewById(R.id.CAFILE)).setText("EXPIRED or unreadable/corrupted CA File");
		((CheckBox)findViewById(org.stackdroid.R.id.verifyServerCertCB)).setChecked(false);
		((Button)findViewById(R.id.selectCABT)).setEnabled(false);
		m_validcafile = false;
	    } else {
		((TextView) findViewById(R.id.CAFILE)).setText(result);
		m_validcafile = true;
	    }
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
    protected class AsyncTaskRequestToken extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage  = null;
     	private  boolean  hasError      = false;
     	private  Pair<String,String>   jsonBuf_Token       = null;
	
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
	    String s_verifyServerCert = args[5];
	    String s_CAFile = args[6];
	    
	    usessl = Boolean.parseBoolean( s_usessl );
	    boolean verifyServerCert = Boolean.parseBoolean(s_verifyServerCert);
            String caissuer = "";
            if(verifyServerCert)
		try {
		    caissuer = ((X509Certificate)(CertificateFactory.getInstance("X.509")).generateCertificate(new FileInputStream( s_CAFile ))).getIssuerX500Principal().getName();
		} catch(CertificateException ce) {
		    hasError = true;
		    errorMessage = ce.getMessage();
		    return null;
		} catch(FileNotFoundException fnfe) {
		    hasError = true;
		    errorMessage = fnfe.getMessage();
		    return null;
		}
	    try {
                if(!endpoint.matches(".+:[0-9]+$")) {
		    endpoint = endpoint + ":5000";
                }
   		Log.d("USERADD", "endpoint="+endpoint);
                if(usessl)
		    if(verifyServerCert) {
			if (RESTClient.checkServerCert("https://" + endpoint + (m_useV3 ? "/v3" : "/v2.0/tokens"), caissuer) == false) {
			    hasError = true;
			    errorMessage = "Server certificate's issuer doesn't match the CA [" + caissuer + "]";
			    return null;
			}
		    }
		String payload = "";
		if(!m_useV3){
		    payload = "{\"auth\": {\"tenantName\": \""
			+ tenant 
			+ "\", \"passwordCredentials\": {\"username\": \"" 
			+ username + "\", \"password\": \"" 
			+ password + "\"}}}";
		} else
		    payload = "{ \"auth\": { \"identity\": { \"methods\": [\"password\"],\"password\": { \"user\": { \"name\": \"" + username + "\",\"domain\": { \"id\": \"default\" }, \"password\": \"" + password + "\" } } }, \"scope\": { \"project\": { \"name\": \"" + tenant + "\", \"domain\": { \"id\": \"default\" } }}}}";
		
		endpoint = (usessl ? "https://" : "http://") + endpoint + (m_useV3 ? "/v3/auth/tokens" : "/v2.0/tokens");
		
		//Log.v("UserAddActivity.AsyncTaskRequestToken.doInBackground", "endpoint=["+endpoint+"]");
		
		jsonBuf_Token = RESTClient.requestToken( usessl, endpoint , payload );
		
                if(jsonBuf_Token == null || jsonBuf_Token.first.length()==0) {
		    hasError = true;
		    errorMessage = "Server's response buffer is NULL or empty!";
		    return null;
		}
		    if (jsonBuf_Token.first.length() > 1000) {
		      //Log.v("UserAddActivity.doInBackgroud", "jsonBuf.length = " + jsonBuf.length());
		      int chunkCount = jsonBuf_Token.first.length() / 1000;     // integer division
		      for (int i = 0; i <= chunkCount; i++) {
		      int max = 1000 * (i + 1);
		      if (max >= jsonBuf_Token.first.length()) {
		      //Log.v("UserAddActivity.doInBackgroud", "chunk " + i + " of " + chunkCount + ":" + jsonBuf.substring(1000 * i));
		      } else {
		      //Log.v("UserAddActivity.doInBackgroud", "chunk " + i + " of " + chunkCount + ":" + jsonBuf.substring(1000 * i, max));
		      }
		      }
		      } else {
		      //Log.v("UserAddActivity.doInBackgroud", jsonBuf.toString());
		      }
		      //Log.d("UserAddActivity.doInBackgroud", "JSON=["+jsonString+"]" );
		       
		//     		Log.v("UserAddActivity.doInBackgroud", "jsonBuf="+jsonBuf);
		U = User.parse( jsonBuf_Token.first, m_useV3, jsonBuf_Token.second );
		
		U.setPassword(password);
		U.setSSL(usessl);
                U.setVerifyServerCert(verifyServerCert);
                U.setCAFile(s_CAFile);
     		
     		String jsonBufGlanceAPIVer = OSClient.getInstance( U ).getCurrentAPIVersion( U.getGlanceEndpoint( ) ); 
     		String jsonBufNeutronAPIVer = OSClient.getInstance( U ).getCurrentAPIVersion( U.getNeutronEndpoint( ) ); 
     		
     		String _glanceAPI = ParseUtils.getCurrentAPI(jsonBufGlanceAPIVer);
     		String _neutronAPI = ParseUtils.getCurrentAPI(jsonBufNeutronAPIVer);
     		
     		String glanceAPI = _glanceAPI != null ? _glanceAPI :  U.getGlanceEndpoint( ) + "/v2";
     		String neutronAPI = _neutronAPI != null ? _neutronAPI :  U.getGlanceEndpoint( ) + "/v2";
     		
     		if(U.useSSL( ) ) {
     			Log.v("UserAddActivity", "FIX TO SSL");
     			if(glanceAPI.startsWith("http://"))
     			  glanceAPI = "https://" + glanceAPI.substring(7, glanceAPI.length());
     			if(neutronAPI.startsWith("http://"))
     			  neutronAPI = "https://" + neutronAPI.substring(7, neutronAPI.length());
     			
     			//glanceAPI.replaceAll("http:", "https:");
     			//neutronAPI.replace("http:", "https:");
     		}
     		
     		//Log.d("UserAddActivity", "GLANCE API VER=[" + glanceAPI + "]");
     		//Log.d("UserAddActivity", "NEUTRON API VER=[" + neutronAPI + "]");
     		U.setGlanceEndpoint( glanceAPI );
     		U.setNeutronEndpoint( neutronAPI );
     		
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
     	}
    }
}
