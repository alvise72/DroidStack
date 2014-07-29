package org.openstack.activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.app.Activity;
import android.view.MenuItem;
import android.view.Menu;
import android.os.AsyncTask;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;
import org.openstack.R;
import org.openstack.utils.FloatingIP;
import org.openstack.utils.User;
import org.openstack.utils.Quota;
import org.openstack.utils.Utils;
import org.openstack.utils.Server;
import org.openstack.utils.Flavor;
import org.openstack.utils.SecGroup;
import org.openstack.utils.CustomProgressDialog;
import org.openstack.comm.*;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
//import android.util.Pair;

public class OverViewActivity extends Activity {

    //    Bundle bundle = null;
    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;
    
    /**
     *
     *
     *
     */
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
        return true;
    }
    
     public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
        
        if( id == Menu.FIRST ) { 
	    //            Utils.customAlert(  );
	    progressDialogWaitStop.show();
	    AsyncTaskQuota task = new AsyncTaskQuota();
	    task.execute(U);
            return true;
        }
	return super.onOptionsItemSelected( item );
    }

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.overview );

	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
	String selectedUserID = Utils.getStringPreference("SELECTEDUSER", "", this);
	setTitle(getString(R.string.USAGEOVERVIEW));
	try {
	    U = User.fromFileID( selectedUserID, Utils.getStringPreference("FILESDIR","",this) );
	    setTitle(getString(R.string.USAGEOVERVIEW) + " " + U.getUserName() + " ("+U.getTenantName()+")");
	    progressDialogWaitStop.show();
	    AsyncTaskQuota task = new AsyncTaskQuota();
	    task.execute(U);
	}  catch(RuntimeException re) {
	    Utils.alert("OverViewActivity.onCreate: " + re.getMessage(), this );
	}
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
  
    private void refreshView( Quota Q,
			      Vector<Server> servers, 
			      Hashtable<String, Flavor> flavors,
			      Vector<FloatingIP> fips, 
			      SecGroup[] secgs ) 
    {
	
	Iterator<Server> it = servers.iterator();
	int totMem = 0;
	int totVCPU = 0;
	int totInstances = 0;
	while( it.hasNext( ) ) {
	    Server S = it.next( );
	    Flavor F = flavors.get( S.getFlavorID( ) );
	    totMem = F.getRAM( );
	    totVCPU = F.getVCPU( );
	    totInstances++;
	}

	((TextView)findViewById(R.id.vmusageTV)).setText("" + totInstances );
	((TextView)findViewById(R.id.vmusageMAXTV)).setText("/" + Q.getMaxInstances() );
	((ProgressBar)findViewById(R.id.vmusagePB)).setMax( Q.getMaxInstances() );
	((ProgressBar)findViewById(R.id.vmusagePB)).setProgress( totInstances);
	
	((TextView)findViewById(R.id.cpuusageTV)).setText("" + totVCPU );
	((TextView)findViewById(R.id.cpuusageMAXTV)).setText("/" + Q.getMaxCPU() );
	((ProgressBar)findViewById(R.id.cpuusagePB)).setMax( Q.getMaxCPU() );
	((ProgressBar)findViewById(R.id.cpuusagePB)).setProgress( totVCPU );
	
	((TextView)findViewById(R.id.ramusageTV)).setText("" + totMem );
	((TextView)findViewById(R.id.ramusageMAXTV)).setText("/" + Q.getMaxRAM( ) );
	((ProgressBar)findViewById(R.id.ramusagePB)).setMax( Q.getMaxRAM( ) );
	((ProgressBar)findViewById(R.id.ramusagePB)).setProgress( totMem );

	((TextView)findViewById(R.id.fipusageTV)).setText("" + (fips!=null ? fips.size() : 0) );
	((TextView)findViewById(R.id.fipusageMAXTV)).setText("/" + Q.getMaxFloatingIP( ) );
	((ProgressBar)findViewById(R.id.fipusagePB)).setMax( Q.getMaxFloatingIP( ) );
	((ProgressBar)findViewById(R.id.fipusagePB)).setProgress( fips!=null ? fips.size() : 0 );
	
	((TextView)findViewById(R.id.segusageTV)).setText("" + (secgs != null ? secgs.length : 0) );
	((TextView)findViewById(R.id.segusageMAXTV)).setText("/" + Q.getMaxSecurityGroups( ) );
	((ProgressBar)findViewById(R.id.segusagePB)).setMax( Q.getMaxSecurityGroups( ) );
	((ProgressBar)findViewById(R.id.segusagePB)).setProgress( secgs != null ? secgs.length : 0 );
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
    protected class AsyncTaskQuota extends AsyncTask<User, String, String>
    {
     	private  String   errorMessage     = null;
	private  boolean  hasError         = false;
	private  String   jsonBuf          = null;
	private  String   jsonBufQuota     = null;
	private  String   jsonBufFIPs      = null;
	private  String   jsonBufSecgs     = null;
	private  String   jsonBufferFlavor = null;
	User U = null;

	@Override
	protected String doInBackground(User... u ) 
	{
	    U = u[0];
	    if(U.getTokenExpireTime() <= Utils.now() + 5) {
		try {
		    jsonBuf = RESTClient.requestToken( U.getEndpoint(),
						       U.getTenantName(),
						       U.getUserName(),
						       U.getPassword(),
						       U.useSSL() );
		    String  pwd = U.getPassword();
		    String  edp = U.getEndpoint();
		    boolean ssl = U.useSSL();
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		    U.toFile( Utils.getStringPreference("FILESDIR","",OverViewActivity.this) );//to save new token + expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    try {
		jsonBufQuota = RESTClient.requestQuota( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
		jsonBuf = RESTClient.requestServers( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
		//servers = parseServers( jsonBuf );
		//Iterator<String> it = servers.iterator();
		//while( it.hasNext( ) ) {
		//    Flavor
		//}
		jsonBufFIPs = RESTClient.requestFloatingIPs( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
		jsonBufSecgs = RESTClient.requestSecGroups( U.getEndpoint(), U.getTenantID(), U.getTenantName(), U.getToken() );
		jsonBufferFlavor = RESTClient.requestFlavors( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
	    } catch(Exception e) {
		errorMessage = e.getMessage();
		hasError = true;
		return "";
	    }
	    
	    return jsonBuf;
	}
	
	@Override
	    protected void onPreExecute() {
	    super.onPreExecute();
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, OverViewActivity.this );
 		OverViewActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    
	    try {
		
		OverViewActivity.this.refreshView( ParseUtils.parseQuota( jsonBufQuota ),
						   ParseUtils.parseServers( jsonBuf ), 
						   ParseUtils.parseFlavors( jsonBufferFlavor ),
						   ParseUtils.parseFloatingIP(jsonBufFIPs),
						   ParseUtils.parseSecGroups(jsonBufSecgs ) );
	    } catch(ParseException pe) {
		Utils.alert( pe.getMessage( ), OverViewActivity.this );
	    }
	    OverViewActivity.this.progressDialogWaitStop.dismiss( );
	}
    }
}
