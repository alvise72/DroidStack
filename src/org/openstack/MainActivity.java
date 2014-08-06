package org.openstack;

import android.app.Activity;
//import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.os.Bundle;
//import android.os.AsyncTask;

import android.content.Intent;
//import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.LayoutInflater;
import android.view.WindowManager;
//import android.view.Display;
//import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;

//import android.util.Log;

import android.widget.LinearLayout;
import android.widget.TextView;
//import android.widget.Toast;

//import java.util.Hashtable;




import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.activities.FloatingIPActivity;
import org.openstack.activities.SecGrpActivity;
import org.openstack.activities.UsersActivity;
import org.openstack.activities.ServersActivity;
import org.openstack.activities.OSImagesActivity;
import org.openstack.activities.OverViewActivity;

//import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity
{
//    private int SCREENH = 0;
    private int SCREENW = 0;
//    private static boolean downloading_image_list = false;
//    private static boolean downloading_quota_list = false;
//    private static boolean downloading_server_list = false;

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
	
	Utils.createDir( getFilesDir( ) + "/DroidStack/users" );
    Utils.putStringPreference( "FILESDIR", getFilesDir( ) + "/DroidStack", this );
	
/*	WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        //SCREENH = d.getHeight();
	SCREENW = d.getWidth();
	*/
	DisplayMetrics dm = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(dm);
	SCREENW = dm.widthPixels;
	//Utils.putIntegerPreference("SCREENH", SCREENH, this);
	//Utils.putIntegerPreference("SCREENW", SCREENW, this);

	
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
      //      progressDialogWaitStop.dismiss();
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
      
      LinearLayout first_left = (LinearLayout)findViewById( R.id.first_left );
      LinearLayout first_right = (LinearLayout)findViewById( R.id.first_right );
      LinearLayout second_left = (LinearLayout)findViewById( R.id.second_left );
      LinearLayout second_right = (LinearLayout)findViewById( R.id.second_right );
      LinearLayout third_left = (LinearLayout)findViewById( R.id.third_left );
      LinearLayout third_right = (LinearLayout)findViewById( R.id.third_right );

      LayoutParams lp = first_left.getLayoutParams();

      lp.width = SCREENW/2;
	
      first_left.setLayoutParams( lp );
      first_right.setLayoutParams( lp );
      second_left.setLayoutParams( lp );
      second_right.setLayoutParams( lp );
      third_left.setLayoutParams( lp );
      third_right.setLayoutParams( lp );
	
      if( !Utils.internetOn( this ) ) {

	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setMessage( "The device is NOT connected to Internet. This App cannot work." );
	  builder.setCancelable(false);
	    
	  DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int id) {
		      finish( );
		  }
	      };

	  builder.setPositiveButton("OK", yesHandler );
	        
	  AlertDialog alert = builder.create();
	  alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
				      WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	  alert.show();

      }
      
      selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      if(selectedUser.length()!=0) {

	  

	  try {
	      User u = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this) );
	      
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+u.getUserName() + " (" + u.getTenantName() + ")"); 
	  } catch(Exception e) {
	      Utils.alert("ERROR: "+e.getMessage(), this );
	      return;
	  }
      } else {
	  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
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
    public void floatingip( View v ) {
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
	    Class<?> c = (Class<?>)FloatingIPActivity.class;
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

	Class<?> c = (Class<?>)ServersActivity.class;
	Intent I = new Intent( MainActivity.this, c );
	startActivity(I);
	
    }

    /**
     *
     *
     *
     *
     */
    public void secgroups( View v ) {
	  //Utils.alert("NOTIMPLEMENTED", this);
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
    	Class<?> c = (Class<?>)SecGrpActivity.class;
    	Intent I = new Intent( MainActivity.this, c );
    	startActivity(I);
    }
}
