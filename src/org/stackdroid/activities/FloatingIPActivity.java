package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.View.OnClickListener; 
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.comm.OSClient;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.R;
import org.stackdroid.utils.FloatingIP;
import org.stackdroid.utils.ImageButtonNamed;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.FloatingIPView;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;

public class FloatingIPActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;
    private Vector<Network> networks = null;
    private ArrayAdapter<Network> spinnerNetworksArrayAdapter  = null;
    private Spinner spinnerNetworks;
    private String pool = null;
	private String fip_to_release_ID = null;
	private Vector<Server> servers = null;
	private ArrayAdapter<Server> spinnerServersArrayAdapter  = null;
	private Spinner serverSpinner = null;
	private AlertDialog alertDialogSelectServer = null;
	private String fipToAssociate = null;
	private FloatingIP selectedFIPObj = null;
	
    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu( menu );
        int order = Menu.FIRST;
        int GROUP = 0;
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
	    return true;
    }
    
    //__________________________________________________________________________________
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
        
        if( id == Menu.FIRST ) { 
	      if(U==null) {
		    Utils.alert("An error occurred recovering User from sdcard. Try to go back and return to this activity.", this);
	      } else {
		    progressDialogWaitStop.show();
		    (new AsyncTaskFIPList()).execute( );
		    return true;
	      }
        }
		  
  	    return super.onOptionsItemSelected( item );
    }    
    
    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView( R.layout.floatingip );
	  
	  progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
	
	  String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	  try {
	    U = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this), this );
	  } catch(RuntimeException re) {
	    Utils.alert("FloatingIPActivity.onCreate: "+re.getMessage(), this );
	    return;
	  }
	  if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
      
	  spinnerNetworks = (Spinner)findViewById(R.id.extnetSP);
	  progressDialogWaitStop.show();
	  loadFIP();
    }
    
    private void loadFIP() {
    	
    	(new AsyncTaskFIPList()).execute();
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
	  super.onResume( );
    }
 
    /**
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

    //__________________________________________________________________________________
    private void refreshView( Vector<FloatingIP> fips, Vector<Network> nets ) {
      spinnerNetworksArrayAdapter = new ArrayAdapter<Network>(FloatingIPActivity.this, android.R.layout.simple_spinner_item, nets.subList(0, nets.size()));
  	  spinnerNetworksArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	  spinnerNetworks.setAdapter(spinnerNetworksArrayAdapter);
  	  
	  ((LinearLayout)findViewById(R.id.fipLayout)).removeAllViews();
	  if(fips.size()==0) {
		  Utils.alert(getString(R.string.NOTFIPAVAIL),this);
	  } else {
	    Iterator<FloatingIP> it = fips.iterator();
	    while(it.hasNext()) {
		  FloatingIP fip = it.next();
	      ((LinearLayout)findViewById( R.id.fipLayout) ).addView( new FloatingIPView( fip, this ) );
	      ((LinearLayout)findViewById( R.id.fipLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
	      View space = new View( this );
	      space.setMinimumHeight(10);
	      ((LinearLayout)findViewById(R.id.fipLayout)).addView( space );
	    }
	  }
	  
	  
    }

    //__________________________________________________________________________________
    public void allocateFIP( View v ) {
    	Network selectedNet = (Network)spinnerNetworks.getSelectedItem();
    	if(selectedNet == null) {
    		Utils.alert(getString(R.string.NONETSELECTED), this);
    		return; 
    	}
    	if(selectedNet.isExt()==false) {
	    Utils.alert(getString(R.string.FIPONLYFROMEXTERNAL), this);
	    return;
    	}
    	pool = selectedNet.getID();
    	progressDialogWaitStop.show();
    	(new AsyncTaskFIPAllocate()).execute();
    }

    
    //__________________________________________________________________________________
    protected class AsyncTaskFIPList extends AsyncTask<Void, Void, Void>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
	  private  String   jsonBuf          = null;
	  private  String   jsonBufServers	 = null;
	  private  String   jsonBufNetworks  = null;
	  private  String   jsonBufSubNets   = null;

	@Override
	protected Void doInBackground( Void ... v ) 
	{
	    OSClient osc = OSClient.getInstance(U);

	    try {
		  jsonBuf         = osc.requestFloatingIPs( );
		  jsonBufServers  = osc.requestServers( );
		  jsonBufNetworks = osc.requestNetworks( );
		  jsonBufSubNets  = osc.requestSubNetworks( );
	    } catch(Exception e) {
		  errorMessage = e.getMessage();
		  hasError = true;
	  	  //return "";
	    }
		return null;
	    
	    //return jsonBuf;
	}
	
	  @Override
	  protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    
 	    if(hasError) {
 		  Utils.alert( errorMessage, FloatingIPActivity.this );
 		  FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
	    
	    try {
	    	Vector<FloatingIP> fips = ParseUtils.parseFloatingIP(jsonBuf);
	    	Vector<Server> servers = ParseUtils.parseServers( jsonBufServers );
	    	networks = ParseUtils.parseNetworks( jsonBufNetworks, jsonBufSubNets );
	    	Iterator<Server> it = servers.iterator();
	    	Hashtable<String,String> mappingServerIDName = new Hashtable<String,String>();
	    	while( it.hasNext( ) ) {
	    	  Server S = it.next();
	    	  mappingServerIDName.put( S.getID(), S.getName() );
	    	}
	    	Iterator<FloatingIP> fipit = fips.iterator();
	    	while( fipit.hasNext() ) {
	    		FloatingIP fip = fipit.next();
	    	    fip.setServerName( mappingServerIDName.get(fip.getServerID()) );	
	    	}
	    	FloatingIPActivity.this.refreshView(fips, networks);
	    } catch(ParseException pe) {
		  Utils.alert("FloatingIPActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), FloatingIPActivity.this );
	    }
	    FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }







    
    //__________________________________________________________________________________
    protected class AsyncTaskFIPDeassociate extends AsyncTask<String, Void, Void>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
      private  String   floatingip       = null;
      private  String   serverid         = null;
      
	@Override
	protected Void doInBackground( String ... ip_serverid ) 
	{
		floatingip = ip_serverid[0];
		serverid   = ip_serverid[1];
		OSClient osc = OSClient.getInstance(U);
		
		try {
		  osc.requestReleaseFloatingIP( floatingip, serverid );	    
		} catch(Exception e) {
		  errorMessage = e.getMessage();
		  hasError = true;
	  	  //return "";
	    }
	    //return "";
		return null;
	}
	
	  @Override
	  protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    
 	    if(hasError) {
 		  Utils.alert( errorMessage, FloatingIPActivity.this );
 		  FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
 	    Utils.alert( getString(R.string.FIPDISSOCIATED), FloatingIPActivity.this );
	    //FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	    FloatingIPActivity.this.loadFIP();
	  }
    }
    
    
    
    
    
    
    
    
  //__________________________________________________________________________________
    protected class AsyncTaskFIPAssociate extends AsyncTask<String, Void, Void>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
      private  String   floatingip       = null;
      private  String   serverid         = null;
      
	@Override
	protected Void doInBackground( String... ip_serverid ) 
	{
		floatingip = ip_serverid[0];
		serverid   = ip_serverid[1];
		OSClient osc = OSClient.getInstance(U);
		
	    
	    try {
		  osc.requestFloatingIPAssociate(floatingip, serverid);	    
		} catch(Exception e) {
		  errorMessage = e.getMessage();
		  hasError = true;
	  	  //return "";
	    }
	    //return "";
		return null;
	}
	
	  @Override
	  protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    
 	    if(hasError) {
 		  Utils.alert( errorMessage, FloatingIPActivity.this );
 		  FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
 	    Utils.alert( getString(R.string.FIPASSOCIATED), FloatingIPActivity.this );
 	    //FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	    FloatingIPActivity.this.loadFIP();
	  }
    }
    
    
    
    
    
    
    
    
    
    
    






    //__________________________________________________________________________________
    protected class AsyncTaskFIPAllocate extends AsyncTask<String, String, String>
    {
	private  String   errorMessage     = null;
	private  boolean  hasError         = false;
      
	@Override
	protected String doInBackground( String... ip_serverid ) 
	{
		OSClient osc = OSClient.getInstance(U);
		
		try {
			osc.requestFloatingIPAllocation( pool );	    
	    } catch(Exception e) {
	    	errorMessage = e.getMessage();
	    	hasError = true;
	    	return "";
	    }
	    return "";
	}
	
	@Override
	protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
		Utils.alert( errorMessage, FloatingIPActivity.this );
		FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
		return;
 	    }
 	    Utils.alert( getString(R.string.FIPALLOCATED), FloatingIPActivity.this );
	    //FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	    FloatingIPActivity.this.loadFIP();
	}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  //__________________________________________________________________________________
    protected class AsyncTaskFIPRelease extends AsyncTask<String, String, String>
    {
	private  String   errorMessage     = null;
	private  boolean  hasError         = false;
      
	@Override
	protected String doInBackground( String... ip_serverid ) 
	{
		OSClient osc = OSClient.getInstance(U);

	    try {
	    	osc.requestFloatingIPRelease( fip_to_release_ID );	    
	    } catch(Exception e) {
	    	errorMessage = e.getMessage();
	    	hasError = true;
	    	return "";
	    }
	    return "";
	}
	
	@Override
	protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
		Utils.alert( errorMessage, FloatingIPActivity.this );
		FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
		return;
 	    }
 	    Utils.alert( getString(R.string.FIPRELEASED), FloatingIPActivity.this );
	    //FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	    FloatingIPActivity.this.loadFIP();
	}
    }
    
    
    
    
    
    
    
    




    @Override
    public void onClick(View v) {
	
    	if(v instanceof ImageButtonNamed) {
    		if(((ImageButtonNamed)v).getType()==ImageButtonNamed.BUTTON_DISSOCIATE_IP) {
    			FloatingIP FIP = ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP();
    			String fip = FIP.getIP();
    			if(FIP.isAssociated()==false) {
    				Utils.alert(getString(R.string.FIPNOTASSOCIATED), this);
    				return;
    			}
    			String serverid= FIP.getServerID();
    			
    			progressDialogWaitStop.show();
    			AsyncTaskFIPDeassociate task = new AsyncTaskFIPDeassociate();
    			task.execute( fip, serverid );
    		}
	    
    		if(((ImageButtonNamed)v).getType()==ImageButtonNamed.BUTTON_RELEASE_IP) {
    			String serverid= ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getServerID();
    			if(serverid==null || serverid.length()==0 || serverid.compareTo("null") == 0) {
    				fip_to_release_ID  = ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getID();
    				progressDialogWaitStop.show();
    				AsyncTaskFIPRelease task = new AsyncTaskFIPRelease();
    				task.execute();	    
    			} else {
    				Utils.alert(getString(R.string.CANNOTRELEASEASSOCIATEDFIP), this);
    				return;
    			}
    		}
	    
    		if(((ImageButtonNamed)v).getType()==ImageButtonNamed.BUTTON_ASSOCIATE_IP) {
    			selectedFIPObj  = ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP();
    			if(selectedFIPObj.isAssociated()) {
    				final String fip = selectedFIPObj.getIP();
    				
    				AlertDialog.Builder builder = new AlertDialog.Builder(this);
    				builder.setMessage( getString(R.string.ALREADYASSOCIATEDWARN));
    				builder.setCancelable(false);
    			    
    				DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						associateFIP(fip);
    					}
    				    };

    				DialogInterface.OnClickListener noHandler = new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    					    dialog.cancel( );
    					}
    				    };

    				builder.setPositiveButton(getString(R.string.CONTINUE), yesHandler );
    				builder.setNegativeButton(getString(R.string.CANCEL), noHandler );
    		            
    				AlertDialog alert = builder.create();
    				alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
    							    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    				alert.show();
    				
    			} else associateFIP(selectedFIPObj.getIP());
    		}
    		
    	}
  	 }	
    
    private void associateFIP( String fip ) {
    	fipToAssociate = fip;
		this.progressDialogWaitStop.show( );
		(new AsyncTaskOSListServers()).execute( );
    }
  //__________________________________________________________________________________
    protected class AsyncTaskOSListServers extends AsyncTask<Void, String, String>
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	private  String   jsonBuf          = null;
     	private  String   jsonBufferFlavor = null;

	@Override
	protected String doInBackground( Void... v ) 
	{
		OSClient osc = OSClient.getInstance(U);
		
	    try {
	    	jsonBuf 		 = osc.requestServers( );
	    	jsonBufferFlavor = osc.requestFlavors( );
	    } catch(Exception e) {
	    	errorMessage = e.getMessage();
	    	hasError = true;
	    	return "";
	    }
	    
	    return jsonBuf;
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, FloatingIPActivity.this );
 		FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    try {
	    	FloatingIPActivity.this.servers = ParseUtils.parseServers( jsonBuf );
	    	FloatingIPActivity.this.pickAServerToAssociateFIP();
	    } catch(ParseException pe) {
	    	Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), FloatingIPActivity.this );
	    }
	    FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	}
    }












public void pickAServerToAssociateFIP() {
	//Log.d("FLOATING","server count="+servers.size());
	if(servers.size()==0) {
		Utils.alert(getString(R.string.NOSERVERTOASSOCIATEFIP), this);
		return;
	}
	spinnerServersArrayAdapter = new ArrayAdapter<Server>(FloatingIPActivity.this, android.R.layout.simple_spinner_item,servers.subList(0,servers.size()) );
	spinnerServersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
	
	LayoutInflater li = LayoutInflater.from(this);

    View promptsView = li.inflate(R.layout.my_dialog_layout, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    alertDialogBuilder.setView(promptsView);

    // set dialog message

    alertDialogBuilder.setTitle(getString(R.string.PICKASERVERTOASSOCIATEFIP) + " "+this.fipToAssociate);
    //    alertDialogBuilder.setIcon(android.R.drawable.ic_launcher);
    // create alert dialog
    alertDialogSelectServer = alertDialogBuilder.create();

    serverSpinner = (Spinner) promptsView.findViewById(R.id.mySpinner);
    serverSpinner.setAdapter(spinnerServersArrayAdapter);
    final Button mButton = (Button) promptsView.findViewById(R.id.myButton);
	//final Button mButtonCancel = (Button) promptsView.findViewById(R.id.myButtonCancel);
    mButton.setOnClickListener(this);
    //mButton.setOnItemSelectedListener( this );
    // show it
    alertDialogSelectServer.show();
    alertDialogSelectServer.setCanceledOnTouchOutside(false);
}

//	@Override
//	public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
/*		Toast.makeText(parent.getContext(), "Clicked : " +
					   parent.getItemAtPosition(pos).toString(), 
					   Toast.LENGTH_LONG).show();*/
//		Server S = (Server)this.serverSpinner.getItemAtPosition(pos);
//		Utils.alert("Selected "+S.getName(), this);
//	}

/*    @Override
    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }*/
}
