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
import android.view.Gravity;
import android.view.View;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import java.io.File;

import org.openstack.comm.RESTClient;
import org.openstack.comm.RuntimeException;
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
    private Bundle bundle = null;
    private ArrayList<OSImage> OS = null;
    private String ID = null;

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.osimagelist );
	bundle = getIntent().getExtras();
	OS = (ArrayList<OSImage>)bundle.getSerializable("OSIMAGES");
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
	super.onResume( );
	refreshOSImagesViews();
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
	
    }

    private  void deleteGlanceImage( String ID ) {
	try {
	    User U = User.fromFileID( Utils.getStringPreference("SELECTEDUSER","",this) );
	    progressDialogWaitStop.show();
	    AsyncTaskOSDelete task = new AsyncTaskOSDelete();
	    task.execute(U.getEndpoint(), U.getTenantName(), U.getUserName(), U.getPassword(), ""+U.useSSL(), ""+U.getTokenExpireTime(), ID );
	} catch(RuntimeException re) {
	    Utils.alert(re.getMessage(), this );
	}
    }

    //__________________________________________________________________________________
    private void refreshOSImagesViews( ) {
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
	    String imagetodel = u[6];
	    User newUser = null;
	    Log.d("AsyncTaskOSDelete.doInBackground", "endpoint="+endpoint+", tenantname="+tenantname+", username="+username+", password="+password+", userssl="+usessl+", expire="+expire+", imagetodel="+imagetodel);
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
		    newUser = ParseUtils.parseUser( jsonBuf );
		    newUser.setEndpoint( endpoint );
		    newUser.setPassword( password );
		    newUser.setSSL( usessl );
		    //U = newUser;
		    newUser.toFile( ); // to save the new token+expiration
		} catch(Exception e) {
		    errorMessage = e.getMessage();
		    hasError = true;
		    return "";
		}
	    }

	    try {
		jsonBuf = RESTClient.requestImages( newUser.getEndpoint(), newUser.getToken() );
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
	    
	    //	    downloading_image_list = true;
	}
	
	@Override
	    protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 		Utils.alert( errorMessage, OSImagesActivity.this );
		OSImagesActivity.this.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    OSImagesActivity.this.progressDialogWaitStop.dismiss( );
	    OSImagesActivity.this.refreshOSImagesViews();
	    //OSImagesActivity.this.showImageList( jsonBuf );
	}
    }
}
