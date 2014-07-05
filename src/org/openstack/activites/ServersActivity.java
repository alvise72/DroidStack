package org.openstack.activities;

import android.os.Bundle;
import android.os.Environment;

import android.widget.ProgressBar;
import android.widget.LinearLayout;
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
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.app.Activity;

import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import java.io.IOException;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import java.io.File;

import org.openstack.comm.RESTClient;
//import org.openstack.comm.RuntimeException;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;



import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Named;
import org.openstack.utils.Server;
import org.openstack.utils.Flavor;
import org.openstack.utils.Base64;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.utils.TextViewNamed;
import org.openstack.utils.UserException;
import org.openstack.utils.ImageViewNamed;
import org.openstack.utils.ImageButtonNamed;
import org.openstack.utils.LinearLayoutNamed;

import android.graphics.Typeface;
import android.graphics.Color;

import android.os.AsyncTask;
import org.openstack.utils.CustomProgressDialog;

public class ServersActivity extends Activity implements OnClickListener {

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
	    if(U==null) {
		Utils.alert("An error occurred recovering User from sdcard. Try to go back and return to this activity.", this);
	    } else {
		progressDialogWaitStop.show();
		AsyncTaskOSListServers task = new AsyncTaskOSListServers();
		task.execute( U );
		return true;
	    }
        }
	return super.onOptionsItemSelected( item );
    }

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.serverlist );
	
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
	
	String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	try {
	    U = User.fromFileID( selectedUser );
	} catch(RuntimeException re) {
	    Utils.alert("ServerssActivity: "+re.getMessage(), this );
	    return;
	}

	progressDialogWaitStop.show();
	AsyncTaskOSListServers task = new AsyncTaskOSListServers();
	task.execute( U );
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
	super.onResume( );
	//refreshServerViews();
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
    public void onClick( View v ) { 
// 	if(v instanceof ImageButtonNamed) {
// 	    Log.d("LoginActivity2.onClick", "TYPE="+((ImageButtonNamed)v).getType( ));
// 	    if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_DELETE_USER ) {
// 		String filenameToDelete = ((ImageButtonNamed)v).getUserView( ).getFilename();
		
// 		(new File(Environment.getExternalStorageDirectory() + "/AndroStack/users/"+filenameToDelete)).delete();
// 		String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
// 		if(selectedUser.compareTo(filenameToDelete)==0)
// 		    Utils.putStringPreference( "SELECTEDUSER", "", this);
		
// 		refreshUserViews();
// 		return;
// 	    }
// 	    if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_MODIFY_USER ) {
// 		Utils.alert("Not implemented yet." , this);
// 		return;
// 	    }
// 	}

// 	if(v instanceof TextViewNamed) {
// 	    //String selectedUser = ((TextViewNamed)v).getUserView().getFilename();

// 	    Utils.putStringPreference("SELECTEDUSER", ((TextViewNamed)v).getUserView().getFilename(), this);
	    
// 	    refreshUserViews();

// 	    return;
// 	}

// 	if(v instanceof LinearLayoutNamed) {
// 	    Utils.putStringPreference("SELECTEDUSER", ((LinearLayoutNamed)v).getUserView().getFilename(), this);
// 	    refreshUserViews();
// 	}
	
    }

    //__________________________________________________________________________________
    private void refreshView( Vector<Server> servers, Hashtable<String, Flavor> flavors ) {

	Iterator<Server> it = servers.iterator();
	while(it.hasNext()) {
	    Server s = it.next();
	    Flavor F = flavors.get( s.getFlavorID( ) );
	    if( F != null)
		s.setFlavor( F );
	    ((LinearLayout)findViewById(R.id.serverLayout)).addView( new ServerView(s, this) );
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
    protected class AsyncTaskOSListServers extends AsyncTask<User, String, String>
    {
     	private  String   errorMessage  =  null;
	private  boolean  hasError      =  false;
	private  String   jsonBuf       = null;
	private  String   jsonBufferFlavor = null;
	private  String   username      = null;

	@Override
	protected String doInBackground(User... u ) 
	{
	    User U = u[0];
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
		    U = ParseUtils.parseUser( jsonBuf );
		    U.setPassword( pwd );
		    U.setEndpoint( edp );
		    U.setSSL( ssl );
		    U.toFile();// to save new token + expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    username = U.getUserName();

	    try {
		jsonBuf = RESTClient.requestServers( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
		jsonBufferFlavor = RESTClient.requestFlavors( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
	    } catch(Exception e) {
		errorMessage = e.getMessage();
		hasError = true;
		return "";
	    }
	    
	    return jsonBuf;
	}
	
	// @Override
	//     protected void onPreExecute() {
	//     super.onPreExecute();
	    
	//     //downloading_image_list = true;
	// }
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, ServersActivity.this );
 		//downloading_server_list = false;
 		ServersActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    //downloading_server_list = false; // questo non va spostato da qui a
	    try {
		Vector<Server> servers = ParseUtils.parseServers( jsonBuf, username );
		Hashtable<String, Flavor> flavors = ParseUtils.parseFlavors( jsonBufferFlavor );
		ServersActivity.this.refreshView( servers, flavors );
	    } catch(ParseException pe) {
		Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), ServersActivity.this );
	    }
	    ServersActivity.this.progressDialogWaitStop.dismiss( );
	}
    }
}
