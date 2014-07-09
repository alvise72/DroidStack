package org.openstack.activities;

import android.os.Bundle; 
import android.os.AsyncTask;
import android.os.Environment;

import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.util.Log;

import android.app.Activity;
import android.app.ProgressDialog;

import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;
import org.openstack.utils.Flavor;
import org.openstack.utils.KeyPair;

import org.openstack.comm.RESTClient;
import org.openstack.views.SecGroupView;
import org.openstack.utils.CustomProgressDialog;
import org.openstack.utils.SubNetwork;
import org.openstack.utils.SecGroup;
import org.openstack.utils.Network;
import org.openstack.utils.User;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.openstack.R;

import org.openstack.views.UserView;

public class ImageLaunchActivity extends Activity implements OnClickListener {

    private boolean requesting_token = false;
    private org.openstack.utils.CustomProgressDialog progressDialogWaitStop = null;
    private Hashtable<String, Flavor> flavorTable = null;
    private ArrayAdapter<String> spinnerNetworksArrayAdapter = null;
    private ArrayAdapter<String> spinnerFlavorsArrayAdapter  = null;
    private ArrayAdapter<String> spinnerKeypairsArrayAdapter = null;
    private Spinner spinnerNetworks  = null;
    private Spinner spinnerFlavors   = null;
    private Spinner spinnerKeypairs  = null;

    private Network networks[] = null;
    private Flavor flavors[] = null;
    private KeyPair keypairs[] = null;
    private SecGroup secgroups[] = null;

    private LinearLayout options = null;

    private Hashtable<String, Boolean> selectedSecgroups = null;

    private User currentUser = null;

    private Bundle bundle = null;
    
    private String imageID = null;

    @Override
    public void onClick( View v ) {
	if(v instanceof CheckBox) {
	    SecGroupView s = (SecGroupView)v;
	    if(s.isChecked()) {
		selectedSecgroups.remove( s.getSecGroup().getID() );
		selectedSecgroups.put( s.getSecGroup().getID(), new Boolean(true) );
	    } else {
		selectedSecgroups.remove(s.getSecGroup().getID());
		selectedSecgroups.put( s.getSecGroup().getID(), new Boolean(false) );
	    }
// 	    String ID = ((SecGroupView)v).getSecGroup().getID();
// 	    String name = ((SecGroupView)v).getSecGroup().getName();
// 	    Toast t = Toast.makeText(this, name+" - "+ID, Toast.LENGTH_SHORT);
// 	    t.show();
//	    Utils.alert(selectedSecgroups.toString( ), this);
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( org.openstack.R.layout.launchimage );

    bundle = getIntent( ).getExtras( );
    imageID = bundle.getString("IMAGEID");

    progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
    progressDialogWaitStop.setMessage( "Please wait. Connecting to remote server..." );
    
    spinnerNetworks = (Spinner) findViewById(R.id.networkSP);
    spinnerFlavors = (Spinner)findViewById(R.id.flavorSP);
    spinnerKeypairs = (Spinner)findViewById(R.id.keypairSP);
    //spinnerSecgroups = (Spinner)findViewById(R.id.);

    options = (LinearLayout)findViewById( R.id.optionLayer );
    
    progressDialogWaitStop.show();
    currentUser = User.fromFileID( Utils.getStringPreference("SELECTEDUSER", "", this) );
    AsyncTaskGetOptions task = new AsyncTaskGetOptions();
    task.execute( currentUser );

//    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors);
//    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    spinner.setAdapter(spinnerArrayAdapter);
//    spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nets);
//    spinner.setAdapter(spinnerArrayAdapter);

    //    adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
    /*
     *
     * Spawn a task collecting flavors, keypairs, networks
     * the task finishes with populating the spinner with its result
     *
     */ 

    // String last_endpoint = Utils.getStringPreference("LAST_ENDPOINT", "", this);
    // String last_tenant   = Utils.getStringPreference("LAST_TENANT", "", this);
    // String last_username = Utils.getStringPreference("LAST_USERNAME", "", this);
    // String last_password = Utils.getStringPreference("LAST_PASSWORD", "", this);
    // boolean usessl       = Utils.getBoolPreference("LAST_USESSL", false, this);
    // ((EditText)findViewById(R.id.endpointET)).setText( last_endpoint );
    // ((EditText)findViewById(R.id.tenantnameET)).setText( last_tenant );
    // ((EditText)findViewById(R.id.usernameET)).setText( last_username );
    // ((EditText)findViewById(R.id.passwordET)).setText( last_password );
    // ((CheckBox)findViewById(R.id.usesslCB)).setChecked( usessl );
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
      // Utils.putStringPreference("LAST_ENDPOINT", ((EditText)findViewById(R.id.endpointET)).getText().toString().trim(), this);
      // Utils.putStringPreference("LAST_TENANT",   ((EditText)findViewById(R.id.tenantnameET)  ).getText().toString().trim(), this);
      // Utils.putStringPreference("LAST_USERNAME", ((EditText)findViewById(R.id.usernameET)).getText().toString().trim(), this);
      // Utils.putStringPreference("LAST_PASSWORD", ((EditText)findViewById(R.id.passwordET)).getText().toString().trim(), this);     
      // Utils.putBoolPreference("LAST_USESSL", ((CheckBox)findViewById(R.id.usesslCB)).isChecked( ), this);
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
  public void launch( View v ) {

      int i = spinnerNetworks.getSelectedItemPosition( );
      int j = spinnerFlavors.getSelectedItemPosition( );
      int k = spinnerKeypairs.getSelectedItemPosition( );

      String instanceName = ((EditText)findViewById(R.id.vmnameET)).getText().toString();
      int count = Integer.parseInt( ((EditText)findViewById(R.id.countET)).getText().toString() );

      currentUser = User.fromFileID( Utils.getStringPreference("SELECTEDUSER", "", this) );
      AsyncTaskLaunch task = new AsyncTaskLaunch();

      Set<String> setSecgroups = selectedSecgroups.keySet();
      String[] arraySecgroups = new String[selectedSecgroups.size()];
      setSecgroups.toArray( arraySecgroups );
      
      task.execute( instanceName, 
		    imageID,
		    keypairs[k].getName(), 
		    flavors[j].getID(),
		    ""+count, 
		    networks[i].getID(),
		    Utils.join( arraySecgroups, "," ) );

    // EditText endpointET = (EditText)findViewById(org.openstack.R.id.endpointET);
    // EditText tenantET   = (EditText)findViewById(org.openstack.R.id.tenantnameET);
    // EditText usernameET = (EditText)findViewById(org.openstack.R.id.usernameET);
    // EditText passwordET = (EditText)findViewById(org.openstack.R.id.passwordET);
    // CheckBox usesslET   = (CheckBox)findViewById(org.openstack.R.id.usesslCB);
    
    // String  endpoint = endpointET.getText().toString().trim();
    // String  tenant   = tenantET.getText().toString().trim();
    // String  username = usernameET.getText().toString().trim();
    // String  password = passwordET.getText().toString().trim();
    // boolean usessl   = usesslET.isChecked();
    
    // if( endpoint.length()==0 ) {
    //   Utils.alert("Please fill the endpoint field.", this);
    //   return;
    // }
    // if( tenant.length()==0 ) {
    //   Utils.alert("Please fill the tenant field.", this);
    //   return;
    // }
    // if( username.length()==0 ) {
    //   Utils.alert("Please fill the username field.", this);
    //   return;
    // }
    // if( password.length()==0 ) {
    //   Utils.alert("Please fill the password field.", this);
    //   return;
    // }
    

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
    // protected void completeUserAdd( String jsonResponse, String password, String endpoint, boolean usessl ) {
    // 	if(jsonResponse == null || jsonResponse.length()==0) {
    // 	    return;
    // 	}
    // 	try {
    // 	    User U = ParseUtils.parseUser( jsonResponse );
    // 	    U.setPassword(password);
    // 	    U.setEndpoint(endpoint);
    // 	    U.setSSL( usessl );
    // 	    //Utils.userToFile( U );
    // 	    U.toFile( );
    // 	    Utils.alert("SUCCESS !\nYou can add another user or go back to the list of users", this);
    // 	} catch(Exception e) {
    // 	    Utils.alert("ERROR: "+e.getMessage(), this);
    // 	} 	
    // }
  
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
    // protected class AsyncTaskLaunchImage extends AsyncTask<String, Void, Void>
    // {
    //  	private  String   errorMessage  = null;
    // 	private  boolean  hasError      = false;
    // 	private  String   jsonBuf       = null;
	
    // 	private String endpoint = null;
    // 	private String password = null;
    // 	private boolean usessl;
	
    // 	protected Void doInBackground( String... args ) 
    // 	{
    // 	    endpoint = args[0];
    // 	    String tenant   = args[1];
    // 	    String username = args[2];
    // 	    password = args[3];
    // 	    String s_usessl = args[4];
	    
    // 	    usessl = Boolean.parseBoolean( s_usessl );
	    
    // 	    try {
    // 		jsonBuf = RESTClient.requestToken( endpoint, tenant, username, password, usessl );
    // 	    } catch(Exception e) {
    // 		errorMessage = e.getMessage();
    // 		hasError = true;
    // 		//    	     return "";
    // 		//return;
    // 	    }
    // 	    return null;
    // 	    //    	   return jsonBuf;
    // 	}
	
    // 	@Override
    // 	protected void onPreExecute() {
    // 	    super.onPreExecute();
    // 	    requesting_token = true;
    // 	}
	
    // 	@Override
    // 	    protected void onPostExecute( Void v ) {
    // 	    super.onPostExecute( v );
	    
    // 	    if(hasError) {	
    // 		ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
    // 		Utils.alert( errorMessage, ImageLaunchActivity.this );
    // 		requesting_token = false;
    // 		//ACTIVITY.progressDialogWaitStop.dismiss( );
    // 		ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
    // 		return;
    // 	    }
	    
    // 	    requesting_token = false; // questo non va spostato da qui a
    // 	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
    // 	    //	    ImageLaunchActivity.this.completeUserAdd( jsonBuf, password, endpoint, usessl );
    // 	}
    // }

    
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
    protected class AsyncTaskGetOptions extends AsyncTask<User, Void, Void>
    {
     	private  String   errorMessage  = null;
	private  boolean  hasError      = false;
	private  String   jsonBufFlavor = null;
	private  String   jsonBufNetwork= null;
	private  String   jsonBufSubnet = null;
	private  String   jsonBufKeypairs = null;
	private  String   jsonBufSecgroups = null;

	@Override
	protected Void doInBackground( User... u ) 
	{
	    User U = u[0];
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		try {
		    String jsonBuf = RESTClient.requestToken( U.getEndpoint(),
							      U.getTenantName(),
							      U.getUserName(),
							      U.getPassword(),
							      U.useSSL() );
		    String  pwd = U.getPassword();
		    String  edp = U.getEndpoint();
		    boolean ssl = U.useSSL();
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		    U.toFile( ); // to save new token+expiration

		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return null;
		}
	    }

	    try {
		jsonBufFlavor    = RESTClient.requestFlavors( U.getEndpoint( ), U.getToken( ), U.getTenantID( ), U.getTenantName( ) );
		jsonBufNetwork   = RESTClient.requestNetworks( U.getEndpoint( ), U.getToken(), U.getTenantName( ) );
		jsonBufSubnet    = RESTClient.requestSubNetworks( U.getEndpoint( ), U.getToken(), U.getTenantName( ) );
		jsonBufKeypairs  = RESTClient.requestKeypairs( U.getEndpoint(), U.getTenantID(), U.getTenantName(), U.getToken( ) );
		jsonBufSecgroups = RESTClient.requestSecgroups( U.getEndpoint(), U.getTenantID(), U.getTenantName(), U.getToken( ) );
	    } catch(Exception e) {
		errorMessage = e.getMessage();
		hasError = true;
		return null;
	    }
	    
	    return null;//jsonBuf;
	}
	
	@Override
	    protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    if(hasError) {
 		Utils.alert( errorMessage, ImageLaunchActivity.this );
 		//downloading_image_list = false;
 		ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    //downloading_image_list = false; // questo non va spostato da qui a
	    try {
		networks = ParseUtils.parseNetwork( jsonBufNetwork, jsonBufSubnet );
		String[] netNames = new String[networks.length];
		for(int i = 0; i<networks.length; ++i)
		    netNames[i] = networks[i].getName();
		spinnerNetworksArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item, netNames);
		spinnerNetworksArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNetworks.setAdapter(spinnerNetworksArrayAdapter);

		Hashtable<String, Flavor> flavorTable = ParseUtils.parseFlavors( jsonBufFlavor );
		Collection<Flavor> collFlav = flavorTable.values();
		flavors = new Flavor[collFlav.size()];
		collFlav.toArray(flavors);
		String flavorNames[] = new String[flavors.length];
		for(int i = 0; i<flavors.length; ++i)
		    flavorNames[i] = flavors[i].getName();
		spinnerFlavorsArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,flavorNames );
		spinnerFlavorsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFlavors.setAdapter(spinnerFlavorsArrayAdapter);

		keypairs = ParseUtils.parseKeypair( jsonBufKeypairs );
		String [] keypairNames = new String[keypairs.length];
		for(int i =0; i< keypairs.length; ++i)
		    keypairNames[i] = keypairs[i].getName();

		spinnerKeypairsArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,keypairNames );
		spinnerKeypairsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeypairs.setAdapter(spinnerKeypairsArrayAdapter);

		secgroups = ParseUtils.parseSecgroup( jsonBufSecgroups );
		String[] secgroupNames = new String[secgroups.length];
		selectedSecgroups = new Hashtable();
		for(int i =0; i< secgroups.length; ++i) {
		    SecGroupView sgv = new SecGroupView( secgroups[i],ImageLaunchActivity.this );
		    sgv.setOnClickListener( ImageLaunchActivity.this );
		    options.addView( sgv );
		    selectedSecgroups.put( sgv.getSecGroup( ).getID(), new Boolean(sgv.isChecked( )) );
		}


// 		spinnerSecgroupsArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,secgroupNames );
// 		spinnerSecgroupsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// 		spinnerSecgroups.setAdapter(spinnerSecgroupsArrayAdapter);

		
		
	    } catch(ParseException pe) {
		Utils.alert("ImageLaunchActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
			    ImageLaunchActivity.this);
	    }
	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
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
    protected class AsyncTaskLaunch extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage  = null;
	private  boolean  hasError      = false;
	private  String jsonBuf         = null;

	@Override
	protected Void doInBackground( String... args ) 
	{
	    //	    Log.d("DROIDSTACK", Utils.join(args,","));
	    Log.d("IMAGELAUNCH", "args.size="+args.length);
	    Log.d("IMAGELAUNCH", "args[0]="+args[0]);
	    Log.d("IMAGELAUNCH", "args[1]="+args[1]);
	    Log.d("IMAGELAUNCH", "args[2]="+args[2]);
	    Log.d("IMAGELAUNCH", "args[3]="+args[3]);
	    Log.d("IMAGELAUNCH", "args[4]="+args[4]);
	    Log.d("IMAGELAUNCH", "args[5]="+args[5]);
	    Log.d("IMAGELAUNCH", "args[6]="+args[6]);
	    
	    User U = ImageLaunchActivity.this.currentUser;
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		try {
		    String jsonBuf = RESTClient.requestToken( U.getEndpoint(),
							      U.getTenantName(),
							      U.getUserName(),
							      U.getPassword(),
							      U.useSSL() );
		    String  pwd = U.getPassword();
		    String  edp = U.getEndpoint();
		    boolean ssl = U.useSSL();
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		    U.toFile( ); // to save new token+expiration
		} catch(Exception e) {
		    //		    Log.d("DROIDSTACK", "Launch2: "+e.getMessage());
		    errorMessage = e.getMessage();
		    hasError = true;
		    return null;
		}
	    }

	    try {
		//		Log.d("DROIDSTACK", "Launch4: args-4="+args[4]);
		jsonBuf = RESTClient.requestInstanceCreation( U.getEndpoint(),
							      U.getTenantID(),
							      U.getTenantName(),
							      U.getToken(),
							      args[0],
							      args[1],
							      args[2],
							      args[3],
							      Integer.parseInt(args[4]),
							      args[5],
							      args[6] );
	    } catch(Exception e) {
		//		Log.d("DROIDSTACK", "Launch3: "+e.getMessage());
		errorMessage = e.getMessage();
		hasError = true;
		return null;
	    }
	    
	    return null;//jsonBuf;
	}
	
	@Override
	    protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    if(hasError) {
 		Utils.alert( "Launch: "+errorMessage, ImageLaunchActivity.this );
 		//downloading_image_list = false;
 		ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    //	    Log.d("DROIDSTACK", "onPostExecute, jsonBuf="+jsonBuf);
		
	    // } catch(ParseException pe) {
	    // 	Utils.alert("ImageLaunchActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
	    // 		    ImageLaunchActivity.this);
	    // }
	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
	}
    }
}
