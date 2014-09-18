package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;

import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.comm.OSClient;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;



import org.stackdroid.R;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;
import org.stackdroid.views.VolumeView;
import org.stackdroid.utils.ImageButtonWithView;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;

public class NeutronActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogDeleteNetwork   = null;
	private Vector<Network>		 networks					= null;
	//private EditText 			 volname				    = null;
	//private EditText 			 volsize				    = null;
	//private Vector<Server> 		 servers         		    = null;
	//private Spinner 			 serverSpinner				= null;
	//private AlertDialog 		 alertDialogSelectServer    = null;
	//private ArrayAdapter<Server> spinnerServersArrayAdapter = null;
	
	//private String 				 currentVolToAttach			= null;
	//private String				 currentSrvToAttach			= null;
	//public String currentVolToDetach;
    
    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        //menu.add(GROUP, 1, order++, getString(R.string.MENUDELETEALLVOL) ).setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }
    
    public void update(View v) {
    	progressDialogWaitStop.show();
		//(new AsyncTaskListVolumes()).execute( );
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
        } catch(Exception re) {
        	Utils.alert("NeutronActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
        	((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
			((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
        progressDialogWaitStop.show();
        //(new AsyncTaskListVolumes()).execute( );
    }
    
    
    /**
    *
    *
    *
    *
    */
    public void createNetwork( View v ) {
    	
    	
        
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

    /**
     *
     *
     *
     *
     */
    private void refreshView( ) {
    	((LinearLayout)findViewById(R.id.volumeLayout)).removeAllViews();
    	if(networks.size()==0) {
    		Utils.alert(getString(R.string.NONETAVAIL), this);	
    		return;
    	}
    	/*
    	Iterator<Volume> vit = volumes.iterator();
    	while(vit.hasNext()) {
    		Volume v = vit.next();
    		//Log.d("VOLUMES", "V="+v.tostring());
    		
    									   
    		//((LinearLayout)findViewById( R.id.volumeLayout) ).addView( vv );
    		((LinearLayout)findViewById( R.id.volumeLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.volumeLayout)).addView( space );
    	}
    	*/
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
