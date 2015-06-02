package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.Button;
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
import org.stackdroid.comm.OSClient;
//import org.stackdroid.comm.ServerErrorException;
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
import org.stackdroid.utils.Router;
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

public class NeutronRouterActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogDeleteRouter    = null;
	private Vector<Router>		 routers					= null;
	private AlertDialog 		 alertDialogCreateRouter;

	/**
	 *
	 *
	 *
	 *
	 */
	protected class DeleteRouterListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			//NeutronRouterActivity.this.progressDialogWaitStop.show( );
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        return true;
    }

	/**
	 *
	 *
	 *
	 *
	 */
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
                
        return super.onOptionsItemSelected( item );
    }

	/**
	 *
	 *
	 *
	 *
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.routerlist );

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
        (new AsyncTaskOSListRouters()).execute();
        //(Toast.makeText(this, getString(R.string.TOUCHNETTOVIEWINFO), Toast.LENGTH_LONG)).show();
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
    public void update(View v) {
    	progressDialogWaitStop.show();
        (new AsyncTaskOSListRouters()).execute( );
    }

	/**
	 *
	 *
	 *
	 *
	 */
    private void refreshView( String jsonBufRouter ) throws ParseException {
    	((LinearLayout)findViewById(R.id.routerLayout)).removeAllViews();
    	/*if(routers==null || routers.size()==0) {
    		Utils.alert(getString(R.string.NOROUTERAVAIL), this);	
    		return;
    	}*/
		Vector<Router> vr = Router.parse(jsonBufRouter);
		Iterator<Router> rit = vr.iterator();
    	while(nit.hasNext()) {
    		Router r = rit.next();
    		((LinearLayout)findViewById( R.id.routerLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.routerLayout)).addView( space );
			((LinearLayout)findViewById(R.id.routerLayout)).addView( new RouterView( r ));
		}
    }

    //  ASYNC TASKS.....

	/**
	 *
	 *
	 *
	 *
	 */
    protected class AsyncTaskOSListRouters extends AsyncTask<Void, Void, Void> {
    	private String jsonBufNet, jsonBufSubnet, jsonBufRouter;
    	private String errorMessage;
    	private boolean hasError = false;
    	
    	@Override
    	protected Void doInBackground( Void... v ) 
    	{
    		OSClient osc = OSClient.getInstance(U);
    		
    	    try {
    	    	//jsonBufNet 		 = osc.requestNetworks( );
    	    	//jsonBufSubnet    = osc.requestSubNetworks( );
    	    	jsonBufRouter    = osc.requestRouters( );
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
     	    	Utils.alert( errorMessage, NeutronRouterActivity.this );
     	    	NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
     	    	return;
     	    }

    	    NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
    	    NeutronRouterActivity.this.refreshView( jsonBufRouter );
    	}
    }
}
