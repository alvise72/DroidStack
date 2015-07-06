package org.stackdroid.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;
import java.util.Iterator;
import java.util.Vector;
import org.apache.http.conn.util.InetAddressUtils;
import org.stackdroid.comm.OSClient;//import org.stackdroid.comm.ServerErrorException;
import org.stackdroid.comm.ServerException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.R;
import org.stackdroid.utils.IPAllocationPool;
import org.stackdroid.utils.CIDRAddressKeyListener;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.IPv4AddressKeyListener;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.SimpleNumberKeyListener;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.NetworkListView;
import org.stackdroid.views.ServerView;
import android.os.AsyncTask;
import org.stackdroid.utils.CustomProgressDialog;

public class NeutronNetworkActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogDeleteNetwork   = null;
	private Vector<Network>		 networks					= null;
	private AlertDialog 		 alertDialogCreateNetwork;
	private EditText 			 cidrNet, cidrMask, netname, DNS;
	private EditText startIP;
	private EditText endIP;
	private EditText gatewayIP;
	private CheckBox useDHCP;

    //__________________________________________________________________________________
	protected class DeleteNetworkListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			//Utils.alert( getString(R.string.NOTIMPLEMENTED), NeutronActivity.this );
			NeutronNetworkActivity.this.progressDialogWaitStop.show( );
			String netID = ((ImageButtonWithView) v).getNetworkListView( ).getNetwork().getID();
			(new NeutronNetworkActivity.AsyncTaskOSDeleteNetwork()).execute(netID, "true");
		}
	}

    //__________________________________________________________________________________
	protected class InfoNetworkListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			Network N = null;
			if(v instanceof LinearLayoutWithView) { 
				N = ((LinearLayoutWithView)v).getNetworkListView().getNetwork();
			}
			if(v instanceof TextViewWithView) { 
				N = ((TextViewWithView)v).getNetworkListView().getNetwork();
			}
			
			
			TextView tv1 = new TextView(NeutronNetworkActivity.this);
			tv1.setText("Network name:");
			tv1.setTypeface( null, Typeface.BOLD );
			TextView tv2 = new TextView(NeutronNetworkActivity.this);
			tv2.setText(N.getName());
			TextView tv3 = new TextView(NeutronNetworkActivity.this);
			tv3.setText("Shared:");
			tv3.setTypeface( null, Typeface.BOLD );
			TextView tv4 = new TextView(NeutronNetworkActivity.this);
			tv4.setText( N.isShared( ) ? NeutronNetworkActivity.this.getString(R.string.YES) : NeutronNetworkActivity.this.getString(R.string.NO));
			TextView tv5 = new TextView(NeutronNetworkActivity.this);
			tv5.setText("External: ");
			tv5.setTypeface( null, Typeface.BOLD );
			TextView tv6 = new TextView(NeutronNetworkActivity.this);
			tv6.setText( N.isExt() ? NeutronNetworkActivity.this.getString(R.string.YES) : NeutronNetworkActivity.this.getString(R.string.NO));
	    
			
			
			Vector<TextView> subnetinfo = new Vector<TextView>();
			
			if(N.getSubNetworks().size()>0) {
				Iterator<SubNetwork> it = N.getSubNetworks().iterator();
				while(it.hasNext()) {
					SubNetwork sn = it.next();
					TextView t = new  TextView(NeutronNetworkActivity.this);
					String subnetname = sn.getName();
					if(subnetname.trim().length()==0)
						subnetname = "No name";
					t.setText( "- "+subnetname );
					t.setTypeface( null, Typeface.BOLD );
					t.setPadding(5, 0,0,0);
					TextView tt = new  TextView(NeutronNetworkActivity.this);
					tt.setText("IPv"+sn.getIPVersion());
					tt.setPadding(15, 0,0,0);
					TextView ttt = new  TextView(NeutronNetworkActivity.this);
					ttt.setText(sn.getAddress());
					ttt.setPadding(15, 0,0,0);
					TextView tttt = new TextView(NeutronNetworkActivity.this);
					tttt.setText("DNS: " + Utils.join(sn.getDNS(), ", "));
					tttt.setPadding(15, 0,0,0);
					IPAllocationPool[] pools = sn.getIPAllocationPools();
					String allocPool[] = new String[pools.length];
					for(int i = 0; i<pools.length; i++) {
						allocPool[i] = pools[i].getStartIP() + "-" + pools[i].getEndIP();
					}
					TextView ttttt = null;

					subnetinfo.add(t);
					subnetinfo.add(tt);
					subnetinfo.add(ttt);
					subnetinfo.add(tttt);
					if(allocPool.length>0) {
						ttttt = new TextView(NeutronNetworkActivity.this);
						ttttt.setText("IP pool: "+Utils.join(allocPool, "\n"));
						ttttt.setPadding(15, 0,0,0);
						subnetinfo.add(ttttt);
					}
				}
			}
			
			ScrollView sv = new ScrollView(NeutronNetworkActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
			sv.setLayoutParams( lp );
			LinearLayout l = new LinearLayout(NeutronNetworkActivity.this);
			l.setLayoutParams( lp );
			l.setOrientation( LinearLayout.VERTICAL );
			int paddingPixel = 8;
			float density = Utils.getDisplayDensity( NeutronNetworkActivity.this );
			int paddingDp = (int)(paddingPixel * density);
			l.setPadding(paddingDp, 0, 0, 0);
			l.addView( tv1 );
			l.addView( tv2 );
			tv2.setPadding(paddingDp, 0, 0, 0);
			l.addView( tv3 );
			l.addView( tv4 );
			tv4.setPadding(paddingDp, 0, 0, 0);
			l.addView( tv5 );
			l.addView( tv6 );
			tv6.setPadding(paddingDp, 0, 0, 0);
			
			if(subnetinfo.size()>0) {
				TextView tv7 = new TextView(NeutronNetworkActivity.this);
				tv7.setText(getString(R.string.SUBNETS)+":");
				tv7.setTypeface( null, Typeface.BOLD );
				l.addView(tv7);
				Iterator<TextView> subnetinfoIT = subnetinfo.iterator();
				while(subnetinfoIT.hasNext()) {
					l.addView(subnetinfoIT.next());
				}
			}
			sv.addView(l);
			String name;
			if(N.getName().length()>=16)
				name = N.getName().substring(0,14) + "..";
			else
				name = N.getName();
			Utils.alertInfo( sv, getString(R.string.NETWORKINFO)+" " +name, NeutronNetworkActivity.this );
		}
	}
	
    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        return true;
    }
    
    //__________________________________________________________________________________
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
                
        return super.onOptionsItemSelected( item );
    }
    
    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.networklist );

    	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
        progressDialogWaitStop.setCancelable(false);
        progressDialogWaitStop.setCanceledOnTouchOutside(false);
        String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
        try {
        	U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
        	if(U==null) {
        		Utils.alert(getString(R.string.RECREATEUSERS), this);
        		return;
        	}
        } catch(Exception re) {
        	Utils.alert("NeutronRouterActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
        	((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
			((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
        progressDialogWaitStop.show();
        (new AsyncTaskOSListNetworks()).execute( );
        (Toast.makeText(this, getString(R.string.TOUCHNETTOVIEWINFO), Toast.LENGTH_LONG)).show();
    }
    
    //__________________________________________________________________________________	
    protected class CreateNetworkClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String netnameS = netname.getText().toString().trim();
			if(netnameS.length()==0) {
				Utils.alert(NeutronNetworkActivity.this.getString(R.string.NOEMPTYNAME), NeutronNetworkActivity.this);
				netname.requestFocus();
				return;
			}
			String netAddr = cidrNet.getText().toString().trim();
			if(netAddr.length()!=0 && InetAddressUtils.isIPv4Address(netAddr) == false) {
			    Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + netAddr, NeutronNetworkActivity.this);
			    cidrNet.requestFocus();
			    return;
		    }
			String netMask = cidrMask.getText().toString().trim();
			if(netMask.length()==0 || Integer.parseInt(netMask)>32 || Integer.parseInt(netMask)<0) {
				Utils.alert(getString(R.string.INCORRECTMASKFORMAT), NeutronNetworkActivity.this);
				cidrMask.requestFocus();
			    return;
			}
			String startIPAddr = startIP.getText().toString().trim();
			if( startIPAddr.length() == 0) {
				Utils.alert(getString(R.string.NOTALLOWEDSTARTIPEMPTY), NeutronNetworkActivity.this);
				startIP.requestFocus();
			    return;
			}
			String endIPAddr = endIP.getText().toString().trim();
			if( endIPAddr.length() == 0) {
				Utils.alert(getString(R.string.NOTALLOWEDENDIPEMPTY), NeutronNetworkActivity.this);
				startIP.requestFocus();
			    return;
			}
			if(InetAddressUtils.isIPv4Address(startIPAddr) == false) {
				Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + startIPAddr, NeutronNetworkActivity.this);
			    startIP.requestFocus();
			    return;
			}
			if(InetAddressUtils.isIPv4Address(endIPAddr) == false) {
				Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + endIPAddr, NeutronNetworkActivity.this);
			    endIP.requestFocus();
			    return;
			}
			String gatewayIPAddr = gatewayIP.getText( ).toString( ).trim( );
			if( endIPAddr.length() == 0) {
				Utils.alert(getString(R.string.NOTALLOWEDGATEWAYIPEMPTY), NeutronNetworkActivity.this);
				gatewayIP.requestFocus();
			    return;
			}
			String use_dhcp = "" + useDHCP.isChecked();

			
			//alertDialogCreateNetwork.dismiss();
			NeutronNetworkActivity.this.progressDialogWaitStop.show( );
			(new AsyncTaskOSCreateNetwork()).execute(netnameS, netAddr + "/" + netMask, DNS.getText().toString().trim(), startIPAddr, endIPAddr, gatewayIPAddr, use_dhcp );
		}
    	
    }
    
    //__________________________________________________________________________________
    protected class CreateNetworkCancelClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			alertDialogCreateNetwork.dismiss();
		}
    	
    }

    
    /**
    *
    *
    *
    *
    */
    public void createNetwork( View v ) {
    	LayoutInflater li = LayoutInflater.from(this);
    	View promptsView = li.inflate(R.layout.my_dialog_create_network, null);
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    	alertDialogBuilder.setView(promptsView);
    	alertDialogBuilder.setTitle(getString(R.string.CREATENETWORK) + " (IPv4)" );
    	alertDialogCreateNetwork = alertDialogBuilder.create();
    	final Button mButton = (Button)promptsView.findViewById(R.id.myButtonCreateNet);
        final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCreateNetCancel);
        netname 	= (EditText)promptsView.findViewById(R.id.netnameET);
        cidrNet 	= (EditText)promptsView.findViewById(R.id.cidrNetET);
        cidrNet.setKeyListener( SimpleNumberKeyListener.getInstance( ) );
        cidrMask 	= (EditText)promptsView.findViewById(R.id.cidrMaskET);
        startIP 	= (EditText)promptsView.findViewById(R.id.startIPET);
        startIP.setKeyListener(IPv4AddressKeyListener.getInstance());
        endIP 		= (EditText)promptsView.findViewById(R.id.endIPET);
        endIP.setKeyListener(IPv4AddressKeyListener.getInstance());
        gatewayIP   = (EditText)promptsView.findViewById(R.id.gatewayIPET);
        gatewayIP.setKeyListener(IPv4AddressKeyListener.getInstance());
		useDHCP     = (CheckBox)promptsView.findViewById(R.id.useDHCP);
		//useDHCP.setKeyListener();
        
        DNS 		= (EditText)promptsView.findViewById(R.id.dnsET);
        DNS.setKeyListener(IPv4AddressKeyListener.getInstance());
        mButton.setOnClickListener(new CreateNetworkClickListener());
        mButtonCancel.setOnClickListener(new CreateNetworkCancelClickListener());
        alertDialogCreateNetwork.setCanceledOnTouchOutside(false);
        alertDialogCreateNetwork.setCancelable(false);
        alertDialogCreateNetwork.show();
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

    public void update(View v) {
    	progressDialogWaitStop.show();
        (new AsyncTaskOSListNetworks()).execute( );
    }
    
    /**
     *
     *
     *
     *
     */
    private void refreshView( ) {
    	((LinearLayout)findViewById(R.id.networkLayout)).removeAllViews();
    	if(networks.size()==0) {
    		Utils.alert(getString(R.string.NONETAVAIL), this);	
    		return;
    	}
    	
    	Iterator<Network> nit = networks.iterator();
    	while(nit.hasNext()) {
    		Network n = nit.next();
    		((LinearLayout)findViewById( R.id.networkLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.networkLayout)).addView( space );
    		((LinearLayout)findViewById(R.id.networkLayout)).addView( new NetworkListView(n,
    																					  new NeutronNetworkActivity.InfoNetworkListener(),
    																					  new NeutronNetworkActivity.DeleteNetworkListener(), this) );
    	}
    }


    //  ASYNC TASKS.....


    /**
	 * 
	 * 
	 *
	 */
    protected class AsyncTaskOSListNetworks extends AsyncTask<Void, Void, Void> {
    	private String jsonBufNet, jsonBufSubnet;
    	private String errorMessage;
    	private boolean hasError = false;
    	
    	@Override
    	protected Void doInBackground( Void... v ) 
    	{
    		OSClient osc = OSClient.getInstance(U);
    		
    	    try {
    	    	jsonBufNet 		 = osc.requestNetworks();
    	    	jsonBufSubnet    = osc.requestSubNetworks();
    	    } catch(ServerException se) {
    	    	errorMessage = ParseUtils.parseNeutronError(se.getMessage());
    	    	hasError = true;
    	    } catch(Exception e) {
    	    	errorMessage = e.getMessage();
    	    	hasError = true;
    	    }
    	    return null;
    	}
    	
    	@Override
    	protected void onPostExecute( Void v ) {
    	    super.onPostExecute(v);
    	    
     	    if(hasError) {
     	    	Utils.alert( errorMessage, NeutronNetworkActivity.this );
     	    	NeutronNetworkActivity.this.progressDialogWaitStop.dismiss( );
     	    	return;
     	    }
    	    
    	    try {
    	    	NeutronNetworkActivity.this.networks = Network.parse(jsonBufNet, jsonBufSubnet);
    	    	NeutronNetworkActivity.this.refreshView( );
    	    } catch(ParseException pe) {
    	    	Utils.alert("NeutronRouterActivity.AsyncTaskOSListNetworks.onPostExecute: "+pe.getMessage( ), NeutronNetworkActivity.this );
    	    }
    	    NeutronNetworkActivity.this.progressDialogWaitStop.dismiss( );
    	}
    }
    

    /**
	 * 
	 * 
	 *
	 */
    protected class AsyncTaskOSCreateNetwork extends AsyncTask<String, Void, Void> {
    	private String jsonBufNet;
    	private String errorMessage;
    	private boolean hasError = false;
    	private String netID;
    	@Override
    	protected Void doInBackground( String... v ) 
    	{
    		OSClient osc = OSClient.getInstance(U);
    		String netname   = v[0];
    		String CIDR      = v[1];
    		String DNS	     = v[2];
    		String startIP   = v[3];
    		String endIP     = v[4];
    		String gatewayIP = v[5];
			boolean use_dhcp = Boolean.parseBoolean(v[6]);
			//Log.d("OSC", "v[6]=" + v[6]);
    		//Log.d("OSC", "use_dhcp=" + use_dhcp);
    	    try {
    	    	jsonBufNet 		 = osc.createNetwork(netname, false);
    	    	netID 		     = ParseUtils.parseSingleNetwork(jsonBufNet);
    	    	osc.createSubnetwork(netID, CIDR, DNS, startIP, endIP, gatewayIP, use_dhcp);
    	    } catch(ServerException se) {
    	    	errorMessage = ParseUtils.parseNeutronError(se.getMessage());
    	    	hasError = true;
    	    } catch(Exception e) {
    	    	errorMessage = e.getMessage();
    	    	hasError = true;
    	    }
    	    return null;
    	}
    	
    	@Override
    	protected void onPostExecute( Void v ) {
    	    super.onPostExecute(v);
    	    
     	    if(hasError) {
     	    	Utils.alert( errorMessage, NeutronNetworkActivity.this );
     	    	NeutronNetworkActivity.this.progressDialogWaitStop.dismiss( );
     	    	(new NeutronNetworkActivity.AsyncTaskOSDeleteNetwork()).execute( netID, "false" );
     	    	return;
     	    }
    	    Utils.alert(getString(R.string.NETWORKCREATED), NeutronNetworkActivity.this );
    	    NeutronNetworkActivity.this.alertDialogCreateNetwork.dismiss();
    	    (new NeutronNetworkActivity.AsyncTaskOSListNetworks()).execute();
    	}
    }
    
    /**
	 * 
	 * 
	 *
	 */
    protected class AsyncTaskOSDeleteNetwork extends AsyncTask<String, Void, Void> {
    	private String errorMessage;
    	private boolean hasError = false;
    	boolean showMessage;
    	
    	@Override
    	protected Void doInBackground( String... v ) 
    	{
    		OSClient osc = OSClient.getInstance(U);
    		String netID = v[0];
    		showMessage = Boolean.parseBoolean(v[1]);
    		
    	    try {
    	    	osc.deleteNetwork(netID);
    	    } catch(ServerException se) {
    	    	errorMessage = ParseUtils.parseNeutronError(se.getMessage());
    	    	hasError = true;
    	    } catch(Exception e) {
    	    	errorMessage = e.getMessage();
    	    	hasError = true;
    	    }
    	    return null;
    	}
    	
    	@Override
    	protected void onPostExecute( Void v ) {
    	    super.onPostExecute(v);
    	    
     	    if(hasError) {
     	    	Utils.alert( errorMessage, NeutronNetworkActivity.this );
     	    	NeutronNetworkActivity.this.progressDialogWaitStop.dismiss( );
     	    	if(NeutronNetworkActivity.this.alertDialogCreateNetwork!=null)
     	    		NeutronNetworkActivity.this.alertDialogCreateNetwork.dismiss();
     	    	NeutronNetworkActivity.this.progressDialogWaitStop.dismiss();
     	    	return;
     	    }
     	    if(showMessage==true)
     	    	Utils.alert(getString(R.string.NETWORKDELETED), NeutronNetworkActivity.this );
    	    (new NeutronNetworkActivity.AsyncTaskOSListNetworks()).execute();
    	}
    }
}
