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
import org.openstack.utils.Base64;
import org.openstack.utils.UserException;
import org.openstack.utils.OpenStackImage;
import org.openstack.utils.CustomProgressDialog;

import org.openstack.comm.RESTClient;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import org.openstack.activities.LoginActivity2;
import org.openstack.activities.OSImagesExploreActivity;
import org.openstack.activities.OverViewActivity;

import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity //implements OnClickListener
{
    private static MainActivity ACTIVITY = null;
    private Hashtable<String, org.openstack.utils.OpenStackImage> osimages = null;
    private org.openstack.utils.CustomProgressDialog progressDialogWaitStop = null;
    private int SCREENH = 0;
    private int SCREENW = 0;
    private static boolean downloading_image_list = false;
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
	ACTIVITY = this;
	
	setContentView(R.layout.main);
	
        if( !Utils.internetOn( this ) )
          Utils.alert( "The device is not connected to Internet. This App cannot work.", this );
	  
	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( "Please wait: connecting to remote server..." );

	WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        SCREENH = d.getHeight();
	SCREENW = d.getWidth();
	Utils.createDir( Environment.getExternalStorageDirectory() + "/AndroStack/users/" );
    }
    
    /**
     *
     *
     *
     *
     */
    @Override
    public void onDestroy( ) {
      ACTIVITY = null;
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
      
      String osimage = Utils.getStringPreference("SELECTED_OSIMAGE", "", this);
      if(osimage.length() != 0) {
      
        String message = "Name: \""+osimage+"\""
	    + "\nSize: "   + osimages.get(osimage).getSize()/1048576 + " MBytes"
	    + "\nFormat: " + osimages.get(osimage).getFormat();
	
        Utils.putStringPreference("SELECTED_OSIMAGE", "", this);
      }

      selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      if(selectedUser.length()!=0) {
	  Toast t = Toast.makeText(this, "Current user: "+selectedUser, Toast.LENGTH_SHORT);
	  t.setGravity( Gravity.CENTER, 0, 0 );
	  t.show();
      }
    }
    
    /**
     *
     *
     *
     *
     */
    public void login( View v ) {
      Class<?> c = (Class<?>)LoginActivity2.class;
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
	    Utils.alert("You haven't yet selected any user. Please touch on 'Select User' button...", this);
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
    public void list_glance( View v ) {
	if(selectedUser.length()==0) {
	    Utils.alert("You haven't yet selected any user. Please touch on 'Select User' button...", this);
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
        String jsonResponse = null;
	try {
	    jsonResponse = task.get( ); // attende la fine del task senza congelare la UI
	} catch(InterruptedException ie) {
	    Utils.alert( "ERROR: " + ie.getMessage(), this );
	} catch(ExecutionException ee ) {
	    Utils.alert( "ERROR: " + ee.getMessage(), this );
	}
	if(jsonResponse == null || jsonResponse.length()==0) {
	    Utils.alert( "ERROR: Unknown", this );
	    return;
	}
    }
    
    /**
     *
     *
     *
     *
     */  
    private void showImageList( String jsonBuf ) {
    
	Hashtable<String, OpenStackImage> result = null;
	try {
	    result = ParseUtils.getImages( jsonBuf.toString( ) );
	} catch(ParseException pe) {
	    Utils.alert( pe.getMessage( ), this );
	    return;
	}

	Class<?> c = (Class<?>)OSImagesExploreActivity.class;
	Intent I = new Intent( MainActivity.this, c );
	ArrayList<String> imageNames = null;
	if(result!=null) {
	    osimages = result;
	    Set<String> keys = result.keySet();
	    Iterator<String> it = keys.iterator();
	    if(keys.isEmpty() == true) {
		Utils.alert("No image", this);
		return;
	    }
	    imageNames = new ArrayList<String>();
	    while( it.hasNext( ) ) {
		String image = it.next();
		imageNames.add( image );
	    }
      
	    if(imageNames.size() > 0) {
		I.putStringArrayListExtra("OSIMAGELIST", imageNames);
		startActivity( I );
	    }
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
	    } catch(IOException e) {
		errorMessage = e.getMessage();
		hasError = true;
		return "";
	    }
	    
	    return "";
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
 		Utils.alert( errorMessage, ACTIVITY );
 		downloading_image_list = false;
 		ACTIVITY.progressDialogWaitStop.dismiss( );
 		return;
 	    }
	    
	    downloading_image_list = false; // questo non va spostato da qui a
	    ACTIVITY.progressDialogWaitStop.dismiss( );
	    ACTIVITY.showImageList( jsonBuf );
	}
    }
}
