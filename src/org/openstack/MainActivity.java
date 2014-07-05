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
//import org.openstack.utils.Quota;

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
      
      selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      if(selectedUser.length()!=0) {
	  try {
	      User u = User.fromFileID( selectedUser );
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
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
	Class<?> c = (Class<?>)OverViewActivity.class;
	Intent I = new Intent( MainActivity.this, c );
	startActivity( I );
    }

    /**
     *
     *
     *
     *
     */
    public void glance( View v ) {
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
	Class<?> c = (Class<?>)OSImagesActivity.class;
 	Intent I = new Intent( MainActivity.this, c );
	startActivity(I);
    }
    
    /**
     *
     *
     *
     *
     */
    public void nova( View v ) {
	if(selectedUser.length()==0) {
	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
	    return;
	}

	progressDialogWaitStop.show();
	downloading_image_list = true;
      
	// User U = null;
	// try {
	//     U = User.fromFileID( selectedUser );
	// } catch(Exception e) {
	//     Utils.alert("ERROR: "+e.getMessage( ), this);
	//     return;
	// }
	Class<?> c = (Class<?>)ServersActivity.class;
	Intent I = new Intent( MainActivity.this, c );
	startActivity(I);
	
	//	AsyncTaskOSListServers task = new AsyncTaskOSListServers();
	//task.execute(U);

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


    



}
