package org.openstack.activities;

import android.os.Bundle;

import android.widget.ProgressBar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.net.Uri;

import android.util.Log;
import android.util.DisplayMetrics;

import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager;
import android.app.Activity;

//import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.view.MenuItem;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import android.os.AsyncTask;

//import android.graphics.Bitmap;

import java.io.IOException;

import java.util.Vector;

import org.openstack.R;
import org.openstack.utils.Quota;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;
import org.openstack.comm.RESTClient;
//import org.openstack.comm.RuntimeException;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
import org.openstack.utils.CustomProgressDialog;

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
            Utils.alert( "Not implemented yet" ,this );
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
	    U = User.fromFileID( selectedUserID );
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
  
    private void refreshView( String jsonBuf ) {
	try {
	    Quota Q = ParseUtils.parseQuota( jsonBuf );
	    ((TextView)findViewById(R.id.vmusageTV)).setText("" + Q.getCurrentInstances() );
	    ((TextView)findViewById(R.id.vmusageMAXTV)).setText("/" + Q.getMaxInstances() );
	    ((ProgressBar)findViewById(R.id.vmusagePB)).setMax( Q.getMaxInstances() );
	    ((ProgressBar)findViewById(R.id.vmusagePB)).setProgress( Q.getCurrentInstances() );

	    ((TextView)findViewById(R.id.cpuusageTV)).setText("" + Q.getCurrentCPU() );
	    ((TextView)findViewById(R.id.cpuusageMAXTV)).setText("/" + Q.getMaxCPU() );
	    ((ProgressBar)findViewById(R.id.cpuusagePB)).setMax( Q.getMaxCPU() );
	    ((ProgressBar)findViewById(R.id.cpuusagePB)).setProgress( Q.getCurrentCPU() );
    
	    ((TextView)findViewById(R.id.ramusageTV)).setText("" + Q.getCurrentRAM( ) );
	    ((TextView)findViewById(R.id.ramusageMAXTV)).setText("/" + Q.getMaxRAM( ) );
	    ((ProgressBar)findViewById(R.id.ramusagePB)).setMax( Q.getMaxRAM( ) );
	    ((ProgressBar)findViewById(R.id.ramusagePB)).setProgress( Q.getCurrentRAM( ) );

	    ((TextView)findViewById(R.id.fipusageTV)).setText("" + Q.getCurrentFloatingIP( ) );
	    ((TextView)findViewById(R.id.fipusageMAXTV)).setText("/" + Q.getMaxFloatingIP( ) );
	    ((ProgressBar)findViewById(R.id.fipusagePB)).setMax( Q.getMaxFloatingIP( ) );
	    ((ProgressBar)findViewById(R.id.fipusagePB)).setProgress( Q.getCurrentFloatingIP( ) );
    
	    ((TextView)findViewById(R.id.segusageTV)).setText("" + Q.getCurrentSecurityGroups( ) );
	    ((TextView)findViewById(R.id.segusageMAXTV)).setText("/" + Q.getMaxSecurityGroups( ) );
	    ((ProgressBar)findViewById(R.id.segusagePB)).setMax( Q.getMaxSecurityGroups( ) );
	    ((ProgressBar)findViewById(R.id.segusagePB)).setProgress( Q.getCurrentSecurityGroups( ) );
	} catch(ParseException pe) {
	    Utils.alert("OverViewActivity.refreshView:" + pe.getMessage( ), this );
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
    protected class AsyncTaskQuota extends AsyncTask<User, String, String>
    {
     	private  String   errorMessage  =  null;
	private  boolean  hasError      =  false;
	private  String   jsonBuf       = null;
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
		    U.toFile( );//to save new token + expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    try {
		jsonBuf = RESTClient.requestQuota( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
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
	    
	    //downloading_quota_list = true;
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, OverViewActivity.this );
 		//downloading_quota_list = false;
 		OverViewActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    // downloading_quota_list = false; // questo non va spostato da qui a
	    OverViewActivity.this.progressDialogWaitStop.dismiss( );
	    OverViewActivity.this.refreshView( jsonBuf );
	    //	    OverViewActivity.this.showQuotas( jsonBuf, U );
	}
    }
}
