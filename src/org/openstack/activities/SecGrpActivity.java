package org.openstack.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.Menu;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;

import java.util.Iterator;
import java.util.Vector;

import org.openstack.comm.RESTClient;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;



import org.openstack.R;
import org.openstack.utils.ImageButtonNamed;
import org.openstack.utils.SecGroup;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.views.ListSecGroupView;





import android.graphics.Typeface;
import android.os.AsyncTask;

import org.openstack.utils.CustomProgressDialog;


public class SecGrpActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;
    
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
        		this.update( );
        		return true;
        	}
        }
        
        return super.onOptionsItemSelected( item );
    }

    public void createSecGroup( View v ) {
    	Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
    }
    
    //__________________________________________________________________________________
    @Override
    public void onClick( View v ) {
	if(v instanceof ImageButtonNamed) {
	    if( ((ImageButtonNamed)v).getType() == ImageButtonNamed.BUTTON_DELETE_SECGRP ) {
	    	
	    	final String secgrpID = ((ImageButtonNamed)v).getSecGroupView().getSecGroup().getID();
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage( getString(R.string.AREYOUSURETODELETESECGRP));
			builder.setCancelable(false);
		    
			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					progressDialogWaitStop.show( );
			    	(new AsyncTaskDeleteSecGroups( )).execute( secgrpID );
				}
			};

			DialogInterface.OnClickListener noHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				    dialog.cancel( );
				}
			    };

			builder.setPositiveButton(getString(R.string.YES), yesHandler );
			builder.setNegativeButton(getString(R.string.NO), noHandler );
	            
			AlertDialog alert = builder.create();
			alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
						    			WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			alert.show();
	    	
	    }
	    
	    if( ((ImageButtonNamed)v).getType() == ImageButtonNamed.BUTTON_EDIT_SECGRP ) {
	    	Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
	    	return;
	    }
	    
	}
    }

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView( R.layout.secgrplist );
	
	  progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
	
	  String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	  try {
	    U = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this) );
	  } catch(RuntimeException re) {
	    Utils.alert("ServersActivity.onCreate: " + re.getMessage(), this );
	    return;
	  }
	  if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
	  this.update( );
    }
    
    private void update( ) {
    	progressDialogWaitStop.show();
  	  	(new AsyncTaskSecGroups()).execute( );
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
    private void refreshView( Vector<SecGroup> secgrps ) {
	((LinearLayout)findViewById(R.id.secgrpLayout)).removeAllViews();
	if(secgrps.size()==0) {
	  Utils.alert(getString(R.string.NOSECGRPSAVAIL), this);	
	  return;
	}
	Iterator<SecGroup> it = secgrps.iterator();
	
	while(it.hasNext()) {
		SecGroup s = it.next();
	    ListSecGroupView sgv = new ListSecGroupView(s, this);
	    ((LinearLayout)findViewById( R.id.secgrpLayout) ).addView( sgv );
	    ((LinearLayout)findViewById( R.id.secgrpLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
	    View space = new View( this );
	    space.setMinimumHeight(10);
	    ((LinearLayout)findViewById(R.id.secgrpLayout)).addView( space );
	}
    }













    //  ASYNC TASKS.....











    
    //__________________________________________________________________________________
    protected class AsyncTaskSecGroups extends AsyncTask<Void, String, String>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	    //private  String   jsonBufferFlavor = null;
	
	    @Override
	    protected String doInBackground( Void... v ) 
	    {
	      if(U.getTokenExpireTime() <= Utils.now() + 5) {
		    try {
		      String _jsonBuf = RESTClient.requestToken( U.useSSL() ,
		    										   U.getEndpoint(),
		    										   U.getTenantName(),
		    										   U.getUserName(),
		    										   U.getPassword() );
		      String  pwd = U.getPassword();
		      String  edp = U.getEndpoint();
		      boolean ssl = U.useSSL();
		      U = ParseUtils.parseUser( _jsonBuf );
		      U.setPassword( pwd );
		      U.setEndpoint( edp );
		      U.setSSL( ssl );
		      U.toFile( Utils.getStringPreference("FILESDIR","",SecGrpActivity.this) );// to save new token + expiration
		    } catch(Exception e) {
		      errorMessage = e.getMessage();
		      hasError = true;
		      return "";
		    }
	      }

	    

	    try {
		  jsonBuf = RESTClient.requestSecGroups( U );
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
 		  Utils.alert( errorMessage, SecGrpActivity.this );
 		  SecGrpActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
	    
	    try {
		  Vector<SecGroup> secgrps = ParseUtils.parseSecGroups( jsonBuf );
		  //Hashtable<String, Flavor> flavors = ParseUtils.parseFlavors( jsonBufferFlavor );
		  SecGrpActivity.this.refreshView( secgrps );
	    } catch(ParseException pe) {
		  Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), SecGrpActivity.this );
	    }
	    SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }
    
    
    
    
    
    
  //__________________________________________________________________________________
    protected class AsyncTaskDeleteSecGroups extends AsyncTask<String, String, String>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	    //private  String   jsonBufferFlavor = null;
	
	    @Override
	    protected String doInBackground( String... v ) 
	    {
	      String secgrpID = v[0];
	      if(U.getTokenExpireTime() <= Utils.now() + 5) {
		    try {
		      String _jsonBuf = RESTClient.requestToken( U.useSSL() ,
		    										   U.getEndpoint(),
		    										   U.getTenantName(),
		    										   U.getUserName(),
		    										   U.getPassword() );
		      String  pwd = U.getPassword();
		      String  edp = U.getEndpoint();
		      boolean ssl = U.useSSL();
		      U = ParseUtils.parseUser( _jsonBuf );
		      U.setPassword( pwd );
		      U.setEndpoint( edp );
		      U.setSSL( ssl );
		      U.toFile( Utils.getStringPreference("FILESDIR","",SecGrpActivity.this) );// to save new token + expiration
		    } catch(Exception e) {
		      errorMessage = e.getMessage();
		      hasError = true;
		      return "";
		    }
	      }

	    

	    try {
		  RESTClient.deleteSecGroup(U, secgrpID);
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
 		  Utils.alert( errorMessage, SecGrpActivity.this );
 		  SecGrpActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
 	    SecGrpActivity.this.update( );
//	    SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }
}
