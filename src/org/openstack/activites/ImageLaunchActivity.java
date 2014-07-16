package org.openstack.activities;

import android.os.Bundle; 
import android.os.AsyncTask;
//import android.os.Environment;

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
import android.view.inputmethod.InputMethodManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
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
import org.openstack.views.NetworkView;

import org.openstack.utils.CustomProgressDialog;
import org.openstack.utils.SubNetwork;
import org.openstack.utils.SecGroup;
import org.openstack.utils.Network;
import org.openstack.utils.User;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.apache.http.conn.util.InetAddressUtils;

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
    private LinearLayout networksL = null;


    HashSet<String> selectedSecgroups = null;
    HashSet<String> selectedNetworks = null;

    private User currentUser = null;

    private Bundle bundle = null;
    
    private String imageID = null;

    private Button launchButton = null;

    @Override
    public void onClick( View v ) {
	if(v instanceof SecGroupView) {
	    SecGroupView s = (SecGroupView)v;
	    if(s.isChecked())
		selectedSecgroups.add( s.getSecGroup().getID() );
	    else
		selectedSecgroups.remove(s.getSecGroup().getID());
	    return;
	}
	
	if(v instanceof NetworkView) {
	    NetworkView nv = (NetworkView)v;
	    if(nv.isChecked())
		selectedNetworks.add( nv.getNetwork().getID() );
	    else
		selectedNetworks.remove(nv.getNetwork().getID());
	    return;
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
      setTitle("Launch Nova Instance");
      bundle = getIntent( ).getExtras( );
      imageID = bundle.getString("IMAGEID");
      
      progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( "Please wait. Connecting to remote server..." );
      
      //    spinnerNetworks = (Spinner) findViewById(R.id.networkSP);
      spinnerFlavors = (Spinner)findViewById(R.id.flavorSP);
      spinnerKeypairs = (Spinner)findViewById(R.id.keypairSP);
      
      options = (LinearLayout)findViewById( R.id.secgroupsLayer );
      networksL = (LinearLayout)findViewById( R.id.networksLayer );
      
      progressDialogWaitStop.show();
      currentUser = User.fromFileID( Utils.getStringPreference("SELECTEDUSER", "", this), Utils.getStringPreference("FILESDIR","",this) );
      
      selectedSecgroups = new HashSet();
      selectedNetworks = new HashSet();
      
      launchButton = (Button)findViewById( R.id.launchButton );
      
      AsyncTaskGetOptions task = new AsyncTaskGetOptions();
      task.execute( currentUser );
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
    //EditText myEditText = (EditText) findViewById(R.id.vmnameET);  
    // InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
    // imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.vmnameET)).getWindowToken(), 0);
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
     * http://stackoverflow.com/questions/18799216/how-to-make-a-edittext-box-in-a-dialog
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

      if(((EditText)findViewById(R.id.vmnameET)).getText().toString().length()==0) {
	  Utils.alert(getString(R.string.MUSTSETNAME) , this);
	  return;
      }

      if(selectedNetworks.size()==0) {
	  Utils.alert(getString(R.string.MUSTSELECTNET) , this);
	  return;
      }

      int j = spinnerFlavors.getSelectedItemPosition( );
      int k = spinnerKeypairs.getSelectedItemPosition( );

      String instanceName = ((EditText)findViewById(R.id.vmnameET)).getText().toString();
      int count = Integer.parseInt( ((EditText)findViewById(R.id.countET)).getText().toString() );

      progressDialogWaitStop.show();
      currentUser = User.fromFileID( Utils.getStringPreference("SELECTEDUSER", "", this), Utils.getStringPreference("FILESDIR","",this) );
      AsyncTaskLaunch task = new AsyncTaskLaunch();

      String adminPass = null;
      if( ((EditText)findViewById(R.id.passwordET)).getText().toString().length()!= 0)
	  adminPass = ((EditText)findViewById(R.id.passwordET)).getText().toString();
      
      task.execute( instanceName, 
		    imageID,
		    keypairs[k].getName(), 
		    flavors[j].getID(),
		    ""+count, 
		    Utils.join( selectedSecgroups, "," ),
		    Utils.join( selectedNetworks, "," ),
		    null,
		    adminPass);
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
    protected class AsyncTaskGetOptions extends AsyncTask<User, Void, Void>
    {
     	private  String   errorMessage     = null;
	private  boolean  hasError         = false;
	private  String   jsonBufFlavor    = null;
	private  String   jsonBufNetwork   = null;
	private  String   jsonBufSubnet    = null;
	private  String   jsonBufKeypairs  = null;
	private  String   jsonBufSecgroups = null;
	User U = null;

	@Override
	protected Void doInBackground( User... u ) 
	{
	    //	    ImageLaunchActivity.this.launchButton.setEnabled(false);
	    U = u[0];
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
		    U.toFile( Utils.getStringPreference("FILESDIR","",ImageLaunchActivity.this) ); // to save new token+expiration

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
		jsonBufSecgroups = RESTClient.requestSecGroups( U.getEndpoint(), U.getTenantID(), U.getTenantName(), U.getToken( ) );
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
		networks = ParseUtils.parseNetworks( jsonBufNetwork, jsonBufSubnet );
		String[] netNames = new String[networks.length];
		for(int i = 0; i<networks.length; ++i) {
		    netNames[i] = networks[i].getName();

		    if(U.getTenantID().compareTo( networks[i].getTenantID() )!=0) {
			if(networks[i].isShared()==false)
			    continue;
		    }
		    
		    NetworkView nv = new NetworkView( networks[i], ImageLaunchActivity.this );
		    nv.setOnClickListener( ImageLaunchActivity.this );
		    networksL.addView( nv );
		}

		// spinnerNetworksArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item, netNames);
		// spinnerNetworksArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spinnerNetworks.setAdapter(spinnerNetworksArrayAdapter);

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

		keypairs = ParseUtils.parseKeyPairs( jsonBufKeypairs );
		String [] keypairNames = new String[keypairs.length];
		for(int i =0; i< keypairs.length; ++i)
		    keypairNames[i] = keypairs[i].getName();

		spinnerKeypairsArrayAdapter = new ArrayAdapter<String>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,keypairNames );
		spinnerKeypairsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeypairs.setAdapter(spinnerKeypairsArrayAdapter);

		secgroups = ParseUtils.parseSecGroups( jsonBufSecgroups );
		String[] secgroupNames = new String[secgroups.length];
		
		for(int i =0; i< secgroups.length; ++i) {
		    SecGroupView sgv = new SecGroupView( secgroups[i], ImageLaunchActivity.this );
		    sgv.setOnClickListener( ImageLaunchActivity.this );
		    options.addView( sgv );
		    if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
		}

	    } catch(ParseException pe) {
		Utils.alert("ImageLaunchActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
			    ImageLaunchActivity.this);
	    }
	    //	    ImageLaunchActivity.this.launchButton.setEnabled(true);
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
	    // Log.d("IMAGELAUNCH", "args.size="+args.length);
	    // Log.d("IMAGELAUNCH", "args[0]="+args[0]);
	    // Log.d("IMAGELAUNCH", "args[1]="+args[1]);
	    // Log.d("IMAGELAUNCH", "args[2]="+args[2]);
	    // Log.d("IMAGELAUNCH", "args[3]="+args[3]);
	    // Log.d("IMAGELAUNCH", "args[4]="+args[4]);
	    // Log.d("IMAGELAUNCH", "args[5]="+args[5]);
	    // Log.d("IMAGELAUNCH", "args[6]="+args[6]);
	    //ImageLaunchActivity.this.launchButton.setEnabled(false);
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
		    U.toFile( Utils.getStringPreference("FILESDIR","",ImageLaunchActivity.this) ); // to save new token+expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return null;
		}
	    }

	    try {
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
							      args[6],
							      args[7],
							      args[8],
							      Utils.getStringPreference("FILESDIR","",ImageLaunchActivity.this));
	    } catch(Exception e) {
		e.printStackTrace( );
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
 		// ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
		// ImageLaunchActivity.this.launchButton.setEnabled(true);
 		// return;
 	    }
	    
	    //	    Log.d("DROIDSTACK", "onPostExecute, jsonBuf="+jsonBuf);
		
	    // } catch(ParseException pe) {
	    // 	Utils.alert("ImageLaunchActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
	    // 		    ImageLaunchActivity.this);
	    // }
	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
	    //	    ImageLaunchActivity.this.launchButton.setEnabled(true);
	}
    }
}
