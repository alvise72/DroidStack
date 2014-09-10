package org.stackdroid.activities;

import android.os.Bundle; 
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View;

import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.IPAddressKeyListener;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Flavor;
import org.stackdroid.utils.KeyPair;
import org.stackdroid.comm.OSClient;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.utils.EditTextNamed;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.Network;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;
import org.stackdroid.R;

public class ImageLaunchActivity extends Activity implements OnClickListener {

    private org.stackdroid.utils.CustomProgressDialog progressDialogWaitStop = null;
    private ArrayAdapter<Flavor> spinnerFlavorsArrayAdapter  = null;
    private ArrayAdapter<KeyPair> spinnerKeypairsArrayAdapter = null;
    private Spinner spinnerFlavors   = null;
    private Spinner spinnerKeypairs  = null;
    private LinearLayout options = null;
    private LinearLayout networksL = null;
    HashSet<String> selectedSecgroups = null;
    private User U = null;
    private Bundle bundle = null;
    private String imageID = null;
    private String imageNAME = null;
    private Hashtable<String, EditTextNamed> mappingNetEditText = null;
    private Hashtable<String, String> selectedNetworks = null;

    
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
	    if(nv.isChecked()) {
		String netID = nv.getNetwork().getID();
		mappingNetEditText.get( netID ).setEnabled(true);
	    }
	    else {
		String netID = nv.getNetwork().getID();
		mappingNetEditText.get( netID ).setEnabled(false);
	    }
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
      setContentView( org.stackdroid.R.layout.launchimage );
      
      bundle = getIntent( ).getExtras( );
      imageID = bundle.getString("IMAGEID");
      imageNAME = bundle.getString("IMAGENAME");
      setTitle(getString(R.string.LAUNCHIMAGE)+ " "+imageNAME);
      
      progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
      progressDialogWaitStop.setCancelable(false);
      progressDialogWaitStop.setCanceledOnTouchOutside(false);
      
      spinnerFlavors = (Spinner)findViewById(R.id.flavorSP);
      spinnerKeypairs = (Spinner)findViewById(R.id.keypairSP);
      
      options = (LinearLayout)findViewById( R.id.secgroupsLayer );
      networksL = (LinearLayout)findViewById( R.id.networksLayer );
      
      String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      try {
  	    U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
  	  } catch(Exception re) {
  	    Utils.alert("ImageLaunchActivity.onCreate: "+re.getMessage(), this );
  	    return;
  	  }
      if(selectedUser.length()!=0)
  		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
  	  else
  		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
      //Log.d("IMAGELAUNCH", "User="+U);
      
  	  /*if(selectedUser.length()!=0)
  		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
  		else
  	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
        */
      
      progressDialogWaitStop.show();
      
      selectedSecgroups = new HashSet<String>();
      
      mappingNetEditText = new Hashtable<String, EditTextNamed>();
      selectedNetworks = new Hashtable<String, String>();

      (new AsyncTaskGetOptions()).execute( );
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
	  String instanceName = ((EditText)findViewById(R.id.vmnameET)).getText().toString();
	  instanceName.trim();
      if(instanceName.length()==0) {
    	  Utils.alert(getString(R.string.MUSTSETNAME) , this);
    	  return;
      }

      if(mappingNetEditText.size()==0) {
    	  Utils.alert(getString(R.string.MUSTSELECTNET) , this);
    	  return;
      }

      //String instanceName = ((EditText)findViewById(R.id.vmnameET)).getText().toString();
      int count = Integer.parseInt( ((EditText)findViewById(R.id.countET)).getText().toString() );

      selectedNetworks.clear();
      Iterator<String> it = mappingNetEditText.keySet().iterator();
      
      while(it.hasNext()) {
	    String netID = it.next();
	    if(mappingNetEditText.get( netID ).isEnabled()==false)
	      continue;
	    String netIP = mappingNetEditText.get( netID ).getText().toString();
	    //Log.d("IMAGELAUNCH", "netIP="+netIP);
	    if(netIP!=null && netIP.length()!=0 && count>1) {
	    	Utils.alert(getString(R.string.NOCUSTOMIPWITHMOREVM), this);
	    	return;
	    }
	    selectedNetworks.put( netID, netIP );
      }
      
      
      it = mappingNetEditText.keySet().iterator();
      while(it.hasNext()) {
	    String netID = it.next();
	    if(mappingNetEditText.get( netID ).isEnabled()==false)
	      continue;
	    String netIP = selectedNetworks.get( netID );
	    if(netIP.length()!=0 && InetAddressUtils.isIPv4Address(netIP) == false) {
		    Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + netIP, this);
		    return;
	    }
	  }
      progressDialogWaitStop.show();
      
      KeyPair kp = (KeyPair)spinnerKeypairs.getSelectedItem();
      String kpName = "";
      if(kp!=null)
    	  kpName = kp.getName();
      Flavor flv = (Flavor)this.spinnerFlavors.getSelectedItem();
      
      (new AsyncTaskLaunch()).execute( instanceName, 
    		  						   imageID, 
    		  						   kpName, 
    		  						   flv.getID(), 
    		  						   "" + count, 
    		  						   Utils.join( selectedSecgroups, "," ) );
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
  	private void updateNetworkList(Vector<Network> nets) {
	  Iterator<Network> nit = nets.iterator();
	  while(nit.hasNext()) {
		  Network net = nit.next();
		  //Log.d("LAUNCH", "Handling network "+net);
		  
		  // do not show a net owned by someone else and which is not shared (ex. the external one)
		  if(U.getTenantID().compareTo( net.getTenantID() )!=0) {
				if(net.isShared()==false) {
				    //Log.d("LAUNCH", "skipping...");
					continue;
				}
		  }
		  NetworkView nv = new NetworkView( net, ImageLaunchActivity.this );
		  nv.setOnClickListener( ImageLaunchActivity.this );
		  networksL.addView( nv );
		  EditTextNamed etIP = new EditTextNamed(  ImageLaunchActivity.this, nv );
		  etIP.setKeyListener(IPAddressKeyListener.getInstance());
		    
		  TextView tv = new TextView( ImageLaunchActivity.this );
		  tv.setText(getString(R.string.SPECIFYOPTIP));
		  networksL.addView( tv );
		  networksL.addView( etIP );
		  etIP.setEnabled(false);
		  mappingNetEditText.put( nv.getNetwork().getID(), etIP );
	  }
  	}

	public void updateFlavorList(Vector<Flavor> flavs) {
		spinnerFlavorsArrayAdapter = new ArrayAdapter<Flavor>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,flavs.subList(0,flavs.size()) );
		spinnerFlavorsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFlavors.setAdapter(spinnerFlavorsArrayAdapter);
	}

	public void updateSecGroupList(Vector<SecGroup> secgs) {
		Iterator<SecGroup> sit = secgs.iterator();
		while(sit.hasNext()) {
			SecGroupView sgv = new SecGroupView( sit.next(), ImageLaunchActivity.this );
			sgv.setOnClickListener( ImageLaunchActivity.this );
			options.addView( sgv );
			if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
		}
	}

	public void updateKeyPairList(Vector<KeyPair> keys) {
		spinnerKeypairsArrayAdapter = new ArrayAdapter<KeyPair>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,keys.subList(0, keys.size()) );
		spinnerKeypairsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeypairs.setAdapter(spinnerKeypairsArrayAdapter);
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
    protected class AsyncTaskGetOptions extends AsyncTask<Void, Void, Void>
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	private  String   jsonBufFlavor    = null;
     	private  String   jsonBufNetwork   = null;
     	private  String   jsonBufSubnet    = null;
     	private  String   jsonBufKeypairs  = null;
     	private  String   jsonBufSecgroups = null;

     	@Override
     	protected Void doInBackground( Void ... v ) 
     	{
     		OSClient osc = OSClient.getInstance(U);

	    try {
	    	jsonBufFlavor    = osc.requestFlavors( );
	    	jsonBufNetwork   = osc.requestNetworks( );
	    	jsonBufSubnet    = osc.requestSubNetworks( );
	    	jsonBufKeypairs  = osc.requestKeypairs( );
	    	jsonBufSecgroups = osc.requestSecGroups( );
	    } catch(Exception e) {
	    	errorMessage = e.getMessage();
	    	hasError = true;
	    	return null;
	    }
	    
	    return null;
	}
	
	@Override
	protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    if(hasError) {
 		Utils.alert( errorMessage, ImageLaunchActivity.this );
 		ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    try {
	    	//Log.d("LAUNCH", "jsonBufNetwork="+jsonBufNetwork);
	    	Vector<Network> nets   = ParseUtils.parseNetworks( jsonBufNetwork, jsonBufSubnet );
	    	Vector<Flavor> flavs   = ParseUtils.parseFlavors( jsonBufFlavor );
	    	Vector<KeyPair> keys   = ParseUtils.parseKeyPairs( jsonBufKeypairs );
	    	Vector<SecGroup> secgs = ParseUtils.parseSecGroups( jsonBufSecgroups );
	    	
	    	//String[] netNames = new String[nets.size()];

	    	updateNetworkList( nets );
	    	updateFlavorList( flavs );
	    	updateKeyPairList( keys );
	    	updateSecGroupList( secgs );
	    	
/*	    	Iterator<Network> netit = nets.iterator();
	    	int i = 0;
	    	while(netit.hasNext()) {
	    		Network net = netit.next( );
	    		netNames[i] = net.getName( );
	    	
			if(U.getTenantID().compareTo( net.getTenantID() )!=0) {
				if(net.isShared()==false)
				    continue;
			    }
			    
			    NetworkView nv = new NetworkView( net, ImageLaunchActivity.this );
			    nv.setOnClickListener( ImageLaunchActivity.this );
			    networksL.addView( nv );
			    EditTextNamed etIP = new EditTextNamed(  ImageLaunchActivity.this, nv );
			    etIP.setKeyListener(IPAddressKeyListener.getInstance());
			    
			    TextView tv = new TextView(  ImageLaunchActivity.this );
			    tv.setText(getString(R.string.SPECIFYOPTIP));
			    networksL.addView( tv );
			    networksL.addView( etIP );
			    etIP.setEnabled(false);
			    mappingNetEditText.put( nv.getNetwork().getID(), etIP );
			    i++;
	    	}

		Vector<Flavor> flavors = ParseUtils.parseFlavors( jsonBufFlavor );
		
		spinnerFlavorsArrayAdapter = new ArrayAdapter<Flavor>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,flavors.subList(0,flavors.size()) );
		spinnerFlavorsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFlavors.setAdapter(spinnerFlavorsArrayAdapter);

		keypairs = ParseUtils.parseKeyPairs( jsonBufKeypairs );
		
		spinnerKeypairsArrayAdapter = new ArrayAdapter<KeyPair>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,keypairs.subList(0, keypairs.size()) );
		spinnerKeypairsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKeypairs.setAdapter(spinnerKeypairsArrayAdapter);

		secgroups = ParseUtils.parseSecGroups( jsonBufSecgroups );
		
		Iterator<SecGroup> sit = secgroups.iterator();
		while(sit.hasNext()) {
			SecGroupView sgv = new SecGroupView( sit.next(), ImageLaunchActivity.this );
			sgv.setOnClickListener( ImageLaunchActivity.this );
			options.addView( sgv );
			if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
		}*/
		
	    } catch(ParseException pe) {
	    	Utils.alert("ImageLaunchActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), ImageLaunchActivity.this);
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
     	private  String  errorMessage  = null;
	    private  boolean hasError      = false;

	@Override
	protected Void doInBackground( String... args ) 
	{
	    OSClient osc = OSClient.getInstance( U );

	    

	    try {
		   osc.requestInstanceCreation( args[0],
				   						args[1],
				   						args[2],
				   						args[3],
				   						Integer.parseInt(args[4]),
				   						args[5],
				   						ImageLaunchActivity.this.selectedNetworks );
	    } catch(Exception e) {
		   e.printStackTrace( );
		   errorMessage = e.getMessage();
		   hasError = true;
		   return null;
	    }
	    
	    return null;
	}
	
	@Override
	protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	      if(hasError) {
 		    Utils.alert( errorMessage, ImageLaunchActivity.this );
 	      } else Utils.alert(getString(R.string.IMAGELAUNCHED), ImageLaunchActivity.this);
	    
	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
	    
	}
    }

}
