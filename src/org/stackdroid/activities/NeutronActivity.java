package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;

import java.util.Iterator;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;
import org.stackdroid.comm.OSClient;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;



import org.stackdroid.R;
import org.stackdroid.utils.CIDRAddressKeyListener;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.SimpleNumberKeyListener;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.NetworkListView;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;

public class NeutronActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogDeleteNetwork   = null;
	private Vector<Network>		 networks					= null;
	private AlertDialog alertDialogCreateNetwork;
	//private EditText netname;
	private EditText cidrNet, cidrMask;
	
	protected class DeleteNetworkListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			Utils.alert( getString(R.string.NOTIMPLEMENTED), NeutronActivity.this );
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
        	Utils.alert("NeutronActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
        	((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
			((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
        progressDialogWaitStop.show();
        (new AsyncTaskOSListNetworks()).execute( );
    }
    
    //__________________________________________________________________________________	
    protected class CreateNetworkClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			//String netnameS = netname.getText().toString().trim();
			/*if(netnameS.length()==0) {
				Utils.alert(NeutronActivity.this.getString(R.string.NOEMPTYNAME), NeutronActivity.this);
				netname.requestFocus();
				return;
			}*/
			String netAddr = cidrNet.getText().toString().trim();
			if(netAddr.length()!=0 && InetAddressUtils.isIPv4Address(netAddr) == false) {
			    Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + netAddr, NeutronActivity.this);
			    cidrNet.requestFocus();
			    return;
		    }
			String netMask = cidrMask.getText().toString().trim();
			if(netMask.length()==0 || Integer.parseInt(netMask)>32 || Integer.parseInt(netMask)<0) {
				Utils.alert(getString(R.string.INCORRECTMASKFORMAT), NeutronActivity.this);
				cidrMask.requestFocus();
			    return;
			}
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
    	alertDialogBuilder.setTitle(getString(R.string.CREATENETWORK) );
    	alertDialogCreateNetwork = alertDialogBuilder.create();
    	final Button mButton = (Button)promptsView.findViewById(R.id.myButtonCreateNet);
        final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCreateNetCancel);
        cidrNet = (EditText)promptsView.findViewById(R.id.cidrNetET);
        cidrNet.setKeyListener( SimpleNumberKeyListener.getInstance( ) );
        cidrMask = (EditText)promptsView.findViewById(R.id.cidrMaskET);
        
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
    		((LinearLayout)findViewById(R.id.networkLayout)).addView( new NetworkListView(n, new NeutronActivity.DeleteNetworkListener(), this) );
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
     	    	Utils.alert( errorMessage, NeutronActivity.this );
     	    	NeutronActivity.this.progressDialogWaitStop.dismiss( );
     	    	return;
     	    }
    	    
    	    try {
    	    	NeutronActivity.this.networks = ParseUtils.parseNetworks(jsonBufNet, jsonBufSubnet);
    	    	NeutronActivity.this.refreshView( );
    	    } catch(ParseException pe) {
    	    	Utils.alert("NeutronActivity.AsyncTaskOSListNetworks.onPostExecute: "+pe.getMessage( ), NeutronActivity.this );
    	    }
    	    NeutronActivity.this.progressDialogWaitStop.dismiss( );
    	}
    }
}
