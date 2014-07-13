package org.openstack.activities;

import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;


import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.DialogInterface;


import android.net.Uri;

import android.util.Log;
import android.util.DisplayMetrics;

import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager;
import android.app.Activity;

import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.MenuItem;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import java.io.File;

import org.openstack.comm.RESTClient;
//import org.openstack.comm.RuntimeException;
import org.openstack.comm.NotFoundException;
import org.openstack.comm.NotAuthorizedException;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;
import org.openstack.utils.CustomProgressDialog;

import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Named;
import org.openstack.utils.Server;
import org.openstack.utils.OSImage;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.utils.TextViewNamed;
import org.openstack.utils.UserException;
import org.openstack.utils.ImageViewNamed;
import org.openstack.utils.ImageButtonNamed;
import org.openstack.utils.LinearLayoutNamed;

import android.graphics.Typeface;
import android.graphics.Color;

public class OSImagesActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private String ID = null;
    User U = null;

    
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
	    if(U==null) {
		Utils.alert("An error occurred recovering User from sdcard. Try to go back and return to this activity.", this);
	    } else {
		progressDialogWaitStop.show();
		AsyncTaskOSListImages task = new AsyncTaskOSListImages();
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
	setContentView( R.layout.osimagelist );
	//bundle = getIntent().getExtras();
	//OS = (ArrayList<OSImage>)bundle.getSerializable("OSIMAGES");
	String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	try {
	    U = User.fromFileID( selectedUser );
	} catch(RuntimeException re) {
	    Utils.alert("OSImagesActivity: "+re.getMessage(), this );
	    return;
	}
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
	progressDialogWaitStop.show();
	AsyncTaskOSListImages task = new AsyncTaskOSListImages();
	task.execute( U );
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
    public void onClick( View v ) { 

	if(v instanceof ImageButtonNamed) {

	    if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_DELETE_IMAGE ) {
		ID = ((ImageButtonNamed)v).getOSImageView( ).getOSImage().getID();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( "Are you sure to delete this image ?" );
		builder.setCancelable(false);
	    
		DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			    deleteGlanceImage( ID );
			}
		    };

		DialogInterface.OnClickListener noHandler = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			    dialog.cancel( );
			}
		    };

		builder.setPositiveButton("Yes", yesHandler );
		builder.setNegativeButton("No", noHandler );
            
		AlertDialog alert = builder.create();
		alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
					    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		alert.show();
	    }
	}

	if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_LAUNCH_IMAGE ) {
		ID = ((ImageButtonNamed)v).getOSImageView( ).getOSImage().getID();
		Class<?> c = (Class<?>)ImageLaunchActivity.class;
		Intent I = new Intent( OSImagesActivity.this, c );
		I.putExtra( "IMAGEID", ID );
		startActivity( I );
	}
    }

    private  void deleteGlanceImage( String ID ) {
	progressDialogWaitStop.show();
	AsyncTaskOSDelete task = new AsyncTaskOSDelete();
	task.execute(U.getEndpoint(), 
		     U.getTenantName(), 
		     U.getUserName(), 
		     U.getPassword(), 
		     ""+U.useSSL(), 
		     ""+U.getTokenExpireTime(), 
		     U.getToken(), 
		     ID );
    }

    //__________________________________________________________________________________
    private void refreshView( Vector<OSImage> OS ) {
	Iterator<OSImage> sit = OS.iterator();
	((LinearLayout)findViewById(R.id.osimagesLayout)).removeAllViews();
	while( sit.hasNext( )) {
	    OSImage os = sit.next();
	    ((LinearLayout)findViewById(R.id.osimagesLayout)).addView( new OSImageView(os, this) );
	    View space = new View( this );
	    space.setMinimumHeight(10);
	    ((LinearLayout)findViewById(R.id.osimagesLayout)).addView( space );
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
    protected class AsyncTaskOSDelete extends AsyncTask<String, String, String>
    {
     	private  String   errorMessage  =  null;
	private  boolean  hasError      =  false;
	private  String   jsonBuf       = null;
	
	protected String doInBackground(String... u ) 
	{
	    String endpoint   = u[0];
	    String tenantname = u[1];
	    String username   = u[2];
	    String password   = u[3];
	    boolean usessl    = Boolean.parseBoolean(u[4]);
	    long   expire     = Integer.parseInt(u[5]);
	    String imagetodel = u[7];
	    User newUser = null;
	    String token = u[6];
	    //Log.d("AsyncTaskOSDelete.doInBackground", "endpoint="+endpoint+", tenantname="+tenantname+", username="+username+", password="+password+", userssl="+usessl+", expire="+expire+", imagetodel="+imagetodel);
	    if(expire <= Utils.now() + 5) {
		try {
		    jsonBuf = RESTClient.requestToken( endpoint,
						       tenantname,
						       username,
						       password,
						       usessl );
		    // String  pwd = U.getPassword();
		    // String  edp = U.getEndpoint();
		    // boolean ssl = U.useSSL();
		    U = ParseUtils.parseUser( jsonBuf );
		    U.setEndpoint( endpoint );
		    U.setPassword( password );
		    U.setSSL( usessl );
		    U.toFile( ); // to save the new token+expiration
		    token = U.getToken();
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    try {
		RESTClient.deleteGlanceImage( endpoint, token, imagetodel );
		jsonBuf = RESTClient.requestImages( U.getEndpoint(), U.getToken() );
	    } catch(RuntimeException e) {
		errorMessage = "Runtime: " + e.getMessage();
		hasError = true;
		return "";
	    } catch(NotFoundException nfe) {
		errorMessage = "NotFound: " + nfe.getMessage();
		hasError = true;
		return "";
	    } // catch(NotAuthorizedException nfe) {
	    // 	errorMessage = "NotAuthorized: " + nfe.getMessage();
	    // 	hasError = true;
	    // 	return "";
	    // }
	    
	    return jsonBuf;
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, OSImagesActivity.this );
		OSImagesActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    try {
		Vector<OSImage> OS = ParseUtils.parseImages(jsonBuf);
		OSImagesActivity.this.refreshView( OS );
	    } catch(ParseException pe) {
		Utils.alert("OSImagesActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
			    OSImagesActivity.this);
	    }

	    OSImagesActivity.this.progressDialogWaitStop.dismiss( );
	    //OSImagesActivity.this.refreshView( jsonBuf );
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
		    User newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setPassword( pwd );
		    newUser.setEndpoint( edp );
		    newUser.setSSL( ssl );
		    U = newUser;
		    U.toFile( ); 
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
	    
	    //downloading_image_list = true;
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, OSImagesActivity.this );
 		//downloading_image_list = false;
 		OSImagesActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    //downloading_image_list = false; // questo non va spostato da qui a
	    try {
		Vector<OSImage> OS = ParseUtils.parseImages(jsonBuf);
		OSImagesActivity.this.refreshView( OS );
	    } catch(ParseException pe) {
		Utils.alert("OSImagesActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
			    OSImagesActivity.this);
	    }
	    OSImagesActivity.this.progressDialogWaitStop.dismiss( );
	    //OSImagesActivity.this.refreshView( jsonBuf );
	}
    }
}
