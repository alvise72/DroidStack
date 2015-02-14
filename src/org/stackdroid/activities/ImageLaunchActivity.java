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
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View;

import org.stackdroid.utils.CheckBoxWithView;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.EditTextWithView;
import org.stackdroid.utils.IPv4AddressKeyListener;
import org.stackdroid.utils.IPv6AddressKeyListener;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.SubnetUtils;
import org.stackdroid.utils.SubnetUtils.SubnetInfo;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Flavor;
import org.stackdroid.utils.KeyPair;
import org.stackdroid.comm.OSClient;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.Network;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;
import org.stackdroid.MainActivity;
import org.stackdroid.R;

public class ImageLaunchActivity extends Activity {

    private org.stackdroid.utils.CustomProgressDialog progressDialogWaitStop 	  = null;
    private ArrayAdapter<Flavor> 					  spinnerFlavorsArrayAdapter  = null;
    private ArrayAdapter<KeyPair> 					  spinnerKeypairsArrayAdapter = null;
    private Spinner 								  spinnerFlavors   			  = null;
    private Spinner 								  spinnerKeypairs  			  = null;
    private LinearLayout 							  options 					  = null;
    private LinearLayout 							  networksL 				  = null;
    HashSet<String> 							 	  selectedSecgroups 		  = null;
    private User 									  U 						  = null;
    private Bundle 									  bundle 					  = null;
    private String 									  imageID 					  = null;
    private String 									  imageNAME 				  = null;
    private Hashtable<Pair<String,String>, String> 	  selectedNetworks 			  = null;
    private Vector<Network> 						  networks 					  = null;
    private Vector<NetworkView>						  netViewList				  = null;
    Hashtable<String, String> 						  netids 					  = null;
    
    protected class SecGroupListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		SecGroupView s = (SecGroupView)v;
    	    if(s.isChecked())
    		selectedSecgroups.add( s.getSecGroup().getID() );
    	    else
    		selectedSecgroups.remove(s.getSecGroup().getID());
    	    return;
    	}
    }

    protected class NetworkViewListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		CheckBoxWithView cb = (CheckBoxWithView)v;
    		NetworkView nv = cb.getNetworkView();
    		Log.d("IMAGELAUNCH", "NetworkID="+nv.getNetwork().getID());
    		
    		if(cb.isChecked() && netids.containsKey(nv.getNetwork().getID())) {
    			cb.setChecked(false);
    			Utils.alert(getString(R.string.ALREADYCHOOSENNET) + ": "+nv.getNetwork().getName(), ImageLaunchActivity.this);
    			return;
    		}
    		
    		if(cb.isChecked()) {
    			netids.put(nv.getNetwork().getID(), "1");
    		}
    		if(!cb.isChecked()) {
    			netids.remove(nv.getNetwork().getID());
    		}
    		
    		if(cb.isChecked() && nv.getSubNetwork().getIPVersion().compareTo("4")==0) {
    			nv.getNetworkIP().setEnabled(true);
    			//netids.put(nv.getNetwork().getID(), "1");
    			return;
    		}
    		if(!cb.isChecked() && nv.getSubNetwork().getIPVersion().compareTo("4")==0) {
    			nv.getNetworkIP().setEnabled(false);
    			//netids.remove(nv.getNetwork().getID());
    			return;
    		}
    	}
    }
    
    public ImageLaunchActivity( ) {
    	netViewList = new Vector<NetworkView>( );
    	netids = new Hashtable<String, String>();
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
      setContentView( org.stackdroid.R.layout.imagelaunch );
      
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
  	    if(U==null) {
  	    	Utils.alert(getString(R.string.RECREATEUSERS), this);
  	    	return;
  	    }
  	  } catch(Exception re) {
  	    Utils.alert("ImageLaunchActivity.onCreate: "+re.getMessage(), this );
  	    return;
  	  }
      if(selectedUser.length()!=0)
  		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
  	  else
  		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
      
      progressDialogWaitStop.show();
      
      selectedSecgroups = new HashSet<String>();
      
      selectedNetworks = new Hashtable<Pair<String,String>, String>();

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
 
      int count = Integer.parseInt( ((EditText)findViewById(R.id.countET)).getText().toString() );

      Iterator<NetworkView> nvit = netViewList.iterator();
      /*int netcount = 0;
      while(nvit.hasNext()) {
    	  NetworkView nv = nvit.next();
    	  if(nv.isChecked()) netcount++;
      }*/
      if(count==0) {
    	  Utils.alert(getString(R.string.MUSTSELECTNET) , this);
    	  return;
      }
      
      selectedNetworks.clear();
      nvit = netViewList.iterator();
      
      while(nvit.hasNext()) {
    	  NetworkView nv = nvit.next();
    	  if(nv.isChecked()) {
    		  String netIP = "";
    		  if(nv.getSubNetwork().getIPVersion().compareTo("4") == 0) {
    			  netIP = nv.getNetworkIP().getText().toString().trim();
    			  if(netIP != null && netIP.length()!=0 && count>1) {
    				  Utils.alert(getString(R.string.NOCUSTOMIPWITHMOREVM), this);
    	    		  return;
    			  }
    			  if(netIP != null && netIP.length()!=0 && InetAddressUtils.isIPv4Address(netIP) == false) {
    				  Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + netIP, this);
    				  return;
    			  }
    			  if(netIP != null && netIP.length()!=0) { // Let's check only if the user specified the custom IP
    			    	SubnetUtils su = null;
    			    	SubNetwork sn = nv.getSubNetwork();
    			    	su = new SubnetUtils( sn.getAddress() ); // let's take only the first one
    			    	SubnetInfo si = su.getInfo();
    			    	if(!si.isInRange(netIP)) {
    			    		Utils.alert("IP "+netIP+" "+getString(R.string.NOTINRANGE) + " "+sn.getAddress(), this);
    			    		return;
    			    	}
    			    }
    		  }
    		  
    		  Pair<String,String> net_subnet = new Pair<String,String>( nv.getNetwork().getID(), nv.getSubNetwork().getID() );
    		  if(netIP==null) netIP = "";
    		  selectedNetworks.put(net_subnet, netIP);
    		  
    	  }
      }
      
      KeyPair kp = (KeyPair)spinnerKeypairs.getSelectedItem();
      String kpName = "";
      if(kp!=null)
    	  kpName = kp.getName();
      Flavor flv = (Flavor)this.spinnerFlavors.getSelectedItem();
      progressDialogWaitStop.show();
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
		  if(U.getTenantID().compareTo( net.getTenantID() )!=0) {
				if(net.isShared()==false) {
					continue;
				}
		  }
		  
		  Iterator<SubNetwork> subnetsIT = net.getSubNetworks().iterator();
		  while(subnetsIT.hasNext()) {
			  SubNetwork sn = subnetsIT.next();
			  NetworkView nv = new NetworkView( net, sn, new ImageLaunchActivity.NetworkViewListener(), IPv4AddressKeyListener.getInstance(), getString(R.string.SPECIFYOPTIP), ImageLaunchActivity.this );
			  networksL.addView( nv );
			  netViewList.add(nv);
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
	public void updateFlavorList(Vector<Flavor> flavs) {
		spinnerFlavorsArrayAdapter = new ArrayAdapter<Flavor>(ImageLaunchActivity.this, android.R.layout.simple_spinner_item,flavs.subList(0,flavs.size()) );
		spinnerFlavorsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFlavors.setAdapter(spinnerFlavorsArrayAdapter);
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
	public void updateSecGroupList(Vector<SecGroup> secgs) {
		Iterator<SecGroup> sit = secgs.iterator();
		while(sit.hasNext()) {
			SecGroupView sgv = new SecGroupView( sit.next(), new ImageLaunchActivity.SecGroupListener(),ImageLaunchActivity.this );
			sgv.setOnClickListener( new ImageLaunchActivity.SecGroupListener() );
			options.addView( sgv );
			if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
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
	    	networks               = Network.parse( jsonBufNetwork, jsonBufSubnet );
	    	Vector<Flavor> flavs   = Flavor.parse( jsonBufFlavor );
	    	Vector<KeyPair> keys   = KeyPair.parse( jsonBufKeypairs );
	    	Vector<SecGroup> secgs = SecGroup.parse( jsonBufSecgroups );
	    	
	    	Iterator<Network> nit = networks.iterator();
	    	/*while(nit.hasNext()) {
	    		Network net = nit.next( );
	    		nethashes.put( net.getID(), net );
	    	}*/
	    	
	    	updateNetworkList( networks );
	    	updateFlavorList( flavs );
	    	updateKeyPairList( keys );
	    	updateSecGroupList( secgs );
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
		   osc.createInstance( args[0],
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
 	      } else {
 	    	  ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
 	    	  //Utils.alert(getString(R.string.IMAGELAUNCHED), ImageLaunchActivity.this);
 	    	  Class<?> c = (Class<?>)ServersActivity.class;
 	          Intent I = new Intent( ImageLaunchActivity.this, c );
 	          startActivity(I);
 	      }
	    
	    ImageLaunchActivity.this.progressDialogWaitStop.dismiss( );
	    
	}
    }

}
