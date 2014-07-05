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
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;



import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Named;
import org.openstack.utils.Server;
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

    //private Bundle bundle = null;
    //private ArrayList<Server> S = null;
    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.serverlist );
	//	bundle = getIntent().getExtras();
	//S = (ArrayList<Server>)bundle.getSerializable("SERVERS");
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );
	
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
    private void refreshServerViews( ) {
	// Iterator<Server> sit = S.iterator();
	// ((LinearLayout)findViewById(R.id.serverLayout)).removeAllViews();
	// while( sit.hasNext( )) {
	//     Server s = sit.next();
	//     ((LinearLayout)findViewById(R.id.serverLayout)).addView( new ServerView(s, this) );
	// }

// 	File[] users = (new File(Environment.getExternalStorageDirectory() + "/AndroStack/users/")).listFiles();
// 	LinearLayout usersL = (LinearLayout)findViewById(R.id.userLayout);
// 	usersL.removeAllViews();

// 	for(int i = 0; i<users.length; ++i) {
// 	    User U = null;
// 	    try {
		
// 		U = Utils.userFromFile( users[i].toString() );
		
// 	    } catch(Exception e) {
// 		Utils.alert("ERROR: " + e.getMessage(), this);
// 		continue;
// 	    }
	    
// 	    UserView uv = new UserView ( U, this );
// 	    usersL.addView( uv );
// 	    if( uv.getFilename().compareTo(Utils.getStringPreference("SELECTEDUSER","",this))==0 )
// 		uv.setSelected( );
// 	    else
// 		uv.setUnselected( );
// 	}
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
	    
	    //downloading_image_list = true;
	}
	
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
	    ServersActivity.this.progressDialogWaitStop.dismiss( );
	    //ServersActivity.this.showServerList( jsonBuf, jsonBufForFlavor, username );
	}
    }
}
