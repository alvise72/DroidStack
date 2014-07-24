package org.openstack.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.content.DialogInterface;
import android.app.ProgressDialog;
//import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
//import android.view.MenuItem;
//import android.view.Menu;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;



//import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.openstack.comm.RESTClient;
//import org.openstack.comm.NotFoundException;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
import org.openstack.R;
import org.openstack.utils.FloatingIP;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
//import org.openstack.utils.Server;
//import org.openstack.utils.Flavor;
import org.openstack.views.FloatingIPView;
//import org.openstack.views.ServerView;
//import org.openstack.utils.TextViewNamed;
//import org.openstack.utils.ImageButtonNamed;



//import android.graphics.Typeface;
import android.os.AsyncTask;

import org.openstack.utils.CustomProgressDialog;

public class FloatingIPActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;
    
    
    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView( R.layout.floatingip );
	
	  progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
	
	  String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	  try {
	    U = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this) );
	  } catch(RuntimeException re) {
	    Utils.alert("FloatingIPActivity.onCreate: "+re.getMessage(), this );
	    return;
	  }

	  progressDialogWaitStop.show();
	  AsyncTaskFIPList task = new AsyncTaskFIPList();
  	  task.execute( );
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
    private void refreshView( Vector<FloatingIP> fips ) {
	  ((LinearLayout)findViewById(R.id.fipLayout)).removeAllViews();
	  Iterator<FloatingIP> it = fips.iterator();
	  while(it.hasNext()) {
		  FloatingIP fip = it.next();
	      FloatingIPView fv = new FloatingIPView( fip, this);
	    ((LinearLayout)findViewById( R.id.fipLayout) ).addView( fv );
	    ((LinearLayout)findViewById( R.id.fipLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
	    View space = new View( this );
	    space.setMinimumHeight(10);
	    ((LinearLayout)findViewById(R.id.fipLayout)).addView( space );
	  }
    }













    //  ASYNC TASKS.....











    
    //__________________________________________________________________________________
    protected class AsyncTaskFIPList extends AsyncTask<Void, String, String>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
	  private  String   jsonBuf          = null;
	  //private  String   jsonBufferFlavor = null;
	//private  String   username         = null;

	@Override
	protected String doInBackground( Void... v ) 
	{
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		try {
		    String _jsonBuf = RESTClient.requestToken( U.getEndpoint(),
						       U.getTenantName(),
						       U.getUserName(),
						       U.getPassword(),
						       U.useSSL() );
		    String  pwd = U.getPassword();
		    String  edp = U.getEndpoint();
		    boolean ssl = U.useSSL();
		    U = ParseUtils.parseUser( _jsonBuf );
		    U.setPassword( pwd );
		    U.setEndpoint( edp );
		    U.setSSL( ssl );
		    U.toFile( Utils.getStringPreference("FILESDIR","",FloatingIPActivity.this) );// to save new token + expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    

	    try {
		  jsonBuf = RESTClient.requestFloatingIPs(U.getEndpoint(), U.getToken(),U.getTenantID(),U.getTenantName());
		  //jsonBufferFlavor = RESTClient.requestFlavors( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
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
	    	Vector<FloatingIP> fips = ParseUtils.parseFloatingIPs(jsonBuf);
	    	
		  //Vector<Server> servers = ParseUtils.parseServers( jsonBuf );
		  //Hashtable<String, Flavor> flavors = ParseUtils.parseFlavors( jsonBufferFlavor );
		  //FloatingIPActivity.this.refreshView( servers, flavors );
	    	FloatingIPActivity.this.refreshView(fips);
	    } catch(ParseException pe) {
		  Utils.alert("FloatingIPActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), FloatingIPActivity.this );
	    }
	    FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }













	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
    
}
