package org.openstack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;

import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;

import android.content.Intent;
import android.content.Context;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

import android.util.Log;

import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.res.Configuration;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import org.openstack.R;

import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Named;
import org.openstack.utils.OSImage;
import org.openstack.utils.UserException;
import org.openstack.utils.CustomProgressDialog;

import org.openstack.comm.*;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
import org.openstack.utils.Flavor;
import org.openstack.utils.Server;
import org.openstack.utils.Quota;

import org.openstack.activities.UsersActivity;
import org.openstack.activities.ServersActivity;
import org.openstack.activities.OSImagesActivity;
import org.openstack.activities.OverViewActivity;
import org.openstack.utils.CustomProgressDialog;

import java.util.concurrent.ExecutionException;



public class MainActivity extends Activity //implements OnClickListener
{
    private Hashtable<String, OSImage> osimages = null;
    private CustomProgressDialog progressDialogWaitStop = null;
    private int SCREENH = 0;
    private int SCREENW = 0;
    private static boolean downloading_image_list = false;
    private static boolean downloading_quota_list = false;
    private static boolean downloading_server_list = false;

    private String selectedUser;

    /**
     *
     *
     *
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	
	setContentView(R.layout.main);
	
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );

	WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        SCREENH = d.getHeight();
	SCREENW = d.getWidth();
	Utils.createDir( Environment.getExternalStorageDirectory() + "/AndroStack/users/" );

	Button su = (Button)findViewById( R.id.LOGIN );
	Button ov = (Button)findViewById( R.id.OVERVIEW );
	Button gl = (Button)findViewById( R.id.GLANCE );
	Button no = (Button)findViewById( R.id.NOVA );

	LayoutParams lp = new LinearLayout.LayoutParams((SCREENW-6)/2, (SCREENH-14)/6);
	su.setLayoutParams( lp );
	ov.setLayoutParams( lp );
	gl.setLayoutParams( lp );
	no.setLayoutParams( lp );
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
    @Override
    public void onResume( ) {
      super.onResume( );
      
      if( !Utils.internetOn( this ) )
        Utils.alert( "The device is NOT connected to Internet. This App cannot work.", this );
      
//       String osimage = Utils.getStringPreference("SELECTED_OSIMAGE", "", this);
//       if(osimage.length() != 0) {
      
//         String message = "Name: \""+osimage+"\""
// 	    + "\nSize: "   + osimages.get(osimage).getSize()/1048576 + " MBytes"
// 	    + "\nFormat: " + osimages.get(osimage).getFormat();
	
//         Utils.putStringPreference("SELECTED_OSIMAGE", "", this);
//       }

      selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      if(selectedUser.length()!=0) {
	  try {
	      User u = Utils.userFromFile( Environment.getExternalStorageDirectory() + "/AndroStack/users/"+selectedUser );
	      Toast t = Toast.makeText(this, "Current user: "+u.getUserName() + " (" + u.getTenantName() + ")", Toast.LENGTH_SHORT);
	      t.setGravity( Gravity.CENTER, 0, 0 );
	      t.show();
	  } catch(Exception e) {
	      Utils.alert("ERROR: "+e.getMessage(), this );
	      return;
	  }
      }
    }
    
    /**
     *
     *
     *
     *
     */
    public void login( View v ) {
      Class<?> c = (Class<?>)UsersActivity.class;
      Intent I = new Intent( MainActivity.this, c );
      startActivity( I );
    }
    
    /**
     *
     *
     *
     *
     */
    public void overview( View v ) {
	if(selectedUser.length()==0) {
	    Utils.alert(getString(R.string.NOUSERSELECTED), this);
	    return;
	}
	User U = null;
	try {
	    U = Utils.userFromFile( Environment.getExternalStorageDirectory() + "/AndroStack/users/"+selectedUser );
	} catch(Exception e) {
	    Utils.alert("ERROR: "+e.getMessage( ), this);
	    return;
	}
	progressDialogWaitStop.show();
	downloading_quota_list = true;
	AsyncTaskQuota task = new AsyncTaskQuota();
	task.execute(U);
    }

    /**
     *
     *
     *
     *
     */
    private void showQuotas( String jsonResponse, User U ) {
	Quota q = null;
	try {
	    q = ParseUtils.parseQuota( jsonResponse );
	} catch(ParseException pe) {
	    Utils.alert("MainActivity.overview - ERROR: " + pe.getMessage( ), this );
	    return;
	}
	
	Class<?> c = (Class<?>)OverViewActivity.class;
	Intent I = new Intent( MainActivity.this, c );
	I.putExtra("MAXVM",    q.getMaxInstances());
	I.putExtra("MAXRAM",   q.getMaxRAM());
	I.putExtra("MAXSECG",  q.getMaxSecurityGroups());
	I.putExtra("MAXFIP",   q.getMaxFloatingIP());
	I.putExtra("MAXCPU",   q.getMaxCPU());
	I.putExtra("CURRVM",   q.getCurrentInstances());
	I.putExtra("CURRRAM",  q.getCurrentRAM());
	I.putExtra("CURRSECG", q.getCurrentSecurityGroups());
	I.putExtra("CURRFIP",  q.getCurrentFloatingIP());
	I.putExtra("CURRCPU",  q.getCurrentCPU());
	I.putExtra("INFOUSER", U.getUserName()+" ("+U.getTenantName()+")");

	startActivity( I );
    }
    
    /**
     *
     *
     *
     *
     */
    public void list_glance( View v ) {
	if(selectedUser.length()==0) {
	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
	    return;
	}

	progressDialogWaitStop.show();
	downloading_image_list = true;
      
	User U = null;
	try {
	    U = Utils.userFromFile( Environment.getExternalStorageDirectory() + "/AndroStack/users/"+selectedUser );
	} catch(Exception e) {
	    Utils.alert("ERROR: "+e.getMessage( ), this);
	    return;
	}

	AsyncTaskOSListImages task = new AsyncTaskOSListImages();
	task.execute(U);

    }
    
    /**
     *
     *
     *
     *
     */  
    private void showImageList( String jsonBuf ) {
    
	Vector<OSImage> osimages = null;
	try {
	    osimages = ParseUtils.parseImages( jsonBuf.toString( ) );
	} catch(ParseException pe) {
	    Utils.alert( pe.getMessage( ), this );
	    return;
	}

 	Class<?> c = (Class<?>)OSImagesActivity.class;
 	Intent I = new Intent( MainActivity.this, c );
	
	I.putExtra("OSIMAGES", osimages );
	startActivity(I);
    } 
    
    
    /**
     *
     *
     *
     *
     */
    public void list_vm( View v ) {
	if(selectedUser.length()==0) {
	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
	    return;
	}

	progressDialogWaitStop.show();
	downloading_image_list = true;
      
	User U = null;
	try {
	    U = Utils.userFromFile( Environment.getExternalStorageDirectory() + "/AndroStack/users/"+selectedUser );
	} catch(Exception e) {
	    Utils.alert("ERROR: "+e.getMessage( ), this);
	    return;
	}

	AsyncTaskOSListServers task = new AsyncTaskOSListServers();
	task.execute(U);

    }

    /**
     *
     *
     *
     *
     */
    public void showServerList( String jsonBuffer, String jsonBufferFlavor, String username ) 
    {
	Vector<Server> servers = null;
	try {
	    servers = ParseUtils.parseServers( jsonBuffer, username );
	    Hashtable<String, Flavor> flavors = ParseUtils.parseFlavors( jsonBufferFlavor );

	    Iterator<Server> it = servers.iterator();
	    while(it.hasNext()) {
		Server s = it.next();
		Flavor F = flavors.get( s.getFlavorID( ) );
		if( F != null)
		    s.setFlavor( F );
	    }

	} catch( ParseException pe ) {
	    Utils.alert("ERROR: "+pe.getMessage( ), this);
	}

	if(servers!=null) {
	    Class<?> c = (Class<?>)ServersActivity.class;
	    Intent I = new Intent( MainActivity.this, c );
	    I.putExtra("SERVERS", servers );//StringArrayListExtra("SERVERS", 
	    startActivity(I);
	} else {
	    Utils.alert("Vector<Server> servers is NULL !!", this );
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
    protected class AsyncTaskOSListImages extends AsyncTask<User, String, String>
    {
     	private  String   errorMessage  =  null;
	private  boolean  hasError      =  false;
	private  String   jsonBuf       = null;
	
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
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    try {
		jsonBuf = RESTClient.requestImages( U.getEndpoint(), U.getToken() );
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
	    
	    downloading_image_list = true;
	}
	
	@Override
	    protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);
	}
	
	@Override
	    protected void onCancelled() {
	    super.onCancelled();
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, MainActivity.this );
 		downloading_image_list = false;
 		MainActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    downloading_image_list = false; // questo non va spostato da qui a
	    MainActivity.this.progressDialogWaitStop.dismiss( );
	    MainActivity.this.showImageList( jsonBuf );
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
	    
	    downloading_quota_list = true;
	}
	
	@Override
	    protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);
	}
	
	@Override
	    protected void onCancelled() {
	    super.onCancelled();
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, MainActivity.this );
 		downloading_quota_list = false;
 		MainActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    downloading_quota_list = false; // questo non va spostato da qui a
	    MainActivity.this.progressDialogWaitStop.dismiss( );
	    MainActivity.this.showQuotas( jsonBuf, U );
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
	private  String   jsonBufForFlavor = null;
	private  String   username      = null;
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
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    username = U.getUserName();

	    try {
		jsonBuf = RESTClient.requestServers( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
		jsonBufForFlavor = RESTClient.requestFlavors( U.getEndpoint(), U.getToken(), U.getTenantID(), U.getTenantName() );
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
	    
	    downloading_image_list = true;
	}
	
	@Override
	    protected void onProgressUpdate(String... values) {
	    super.onProgressUpdate(values);
	}
	
	@Override
	    protected void onCancelled() {
	    super.onCancelled();
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, MainActivity.this );
 		downloading_server_list = false;
 		MainActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    downloading_server_list = false; // questo non va spostato da qui a
	    MainActivity.this.progressDialogWaitStop.dismiss( );
	    MainActivity.this.showServerList( jsonBuf, jsonBufForFlavor, username );
	}
    }
}
