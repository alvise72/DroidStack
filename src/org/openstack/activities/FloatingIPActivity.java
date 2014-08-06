package org.openstack.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.openstack.comm.RESTClient;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
import org.openstack.R;
import org.openstack.utils.FloatingIP;
import org.openstack.utils.ImageButtonNamed;
import org.openstack.utils.Server;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.views.FloatingIPView;

import android.os.AsyncTask;

import org.openstack.utils.CustomProgressDialog;

public class FloatingIPActivity extends Activity implements OnClickListener {

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
		    progressDialogWaitStop.show();
		    AsyncTaskFIPList task = new AsyncTaskFIPList();
	  	    task.execute( );
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
	    U = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this) );
	  } catch(RuntimeException re) {
	    Utils.alert("FloatingIPActivity.onCreate: "+re.getMessage(), this );
	    return;
	  }
	  if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
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
	  if(fips.size()==0) {
		  Utils.alert(getString(R.string.NOTFIPAVAIL),this);
		  return;
	  }
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













    //  ASYNC TASKS.....











    
    //__________________________________________________________________________________
    protected class AsyncTaskFIPList extends AsyncTask<Void, String, String>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
	  private  String   jsonBuf          = null;
	  private  String   jsonBufServers	 = null;

	@Override
	protected String doInBackground( Void... v ) 
	{
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		try {
		    String _jsonBuf = RESTClient.requestToken( U.useSSL(), U.getEndpoint(),
						       U.getTenantName(),
						       U.getUserName(),
						       U.getPassword()
						        );
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
		  jsonBuf = RESTClient.requestFloatingIPs( U );
		  jsonBufServers = RESTClient.requestServers( U );
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
	    	Vector<FloatingIP> fips = ParseUtils.parseFloatingIP(jsonBuf);
	    	Vector<Server> servers = ParseUtils.parseServers( jsonBufServers );
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
	    	FloatingIPActivity.this.refreshView(fips);
	    } catch(ParseException pe) {
		  Utils.alert("FloatingIPActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), FloatingIPActivity.this );
	    }
	    FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }







    
    //__________________________________________________________________________________
    protected class AsyncTaskFIPRelease extends AsyncTask<String, String, String>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
      private  String   floatingip       = null;
      private  String   serverid         = null;
      
	@Override
	protected String doInBackground( String... ip_serverid ) 
	{
		floatingip = ip_serverid[0];
		serverid   = ip_serverid[1];
		
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		  try {
		    String _jsonBuf = RESTClient.requestToken( U.useSSL(),
		    										   U.getEndpoint(),
						       						   U.getTenantName(),
						       						   U.getUserName(),
						       						   U.getPassword());
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
		  RESTClient.requestReleaseFloatingIP(U, floatingip, serverid );	    
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
	    
	    FloatingIPActivity.this.progressDialogWaitStop.dismiss( );
	  }
    }
    
    
    
    





	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v instanceof ImageButtonNamed) {
			if(((ImageButtonNamed)v).getType()==ImageButtonNamed.BUTTON_DISSOCIATE_IP) {
			    String fip = ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getIP();
			    String serverid= ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getServerID();
			    //Log.d("FIPACTIVITY", "serverid="+serverid);
			    if(serverid==null || serverid.length()==0 || serverid.compareTo("null") == 0) {
				//Log.d("FIPACTIVITY", "serverid is null or zero");
				Utils.alert(getString(R.string.FIPNOTASSOCIATED), this);
				return;
			    }
			    progressDialogWaitStop.show();
			    AsyncTaskFIPRelease task = new AsyncTaskFIPRelease();
			    task.execute( fip, serverid );
			}
		
			if(((ImageButtonNamed)v).getType()==ImageButtonNamed.BUTTON_RELEASE_IP) {
			    //String fip = ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getIP();
			    String serverid= ((ImageButtonNamed)v).getFloatingIPView().getFloatingIP().getServerID();
			    //Log.d("FIPACTIVITY", "serverid="+serverid);
			    if(serverid==null || serverid.length()==0 || serverid.compareTo("null") == 0) {
				// TODO: delete
			    } else {
				Utils.alert(getString(R.string.CANNOTRELEASEASSOCIATEDFIP), this);
				return;
			    }
			}
		}
	}
}
