package org.stackdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.stackdroid.R;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.activities.FloatingIPActivity;
import org.stackdroid.activities.SecGrpActivity;
import org.stackdroid.activities.UsersActivity;
import org.stackdroid.activities.ServersActivity;
import org.stackdroid.activities.OSImagesActivity;
import org.stackdroid.activities.OverViewActivity;

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
        String versionName = null;
        try {
        	versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch(NameNotFoundException e) {
        	versionName="N/A";
        }
        
        Utils.putStringPreference( "VERSIONNAME", versionName, this );
        setContentView(R.layout.main);
        this.setTitle("DroidStack v "+versionName);
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
        int density = (int)this.getResources().getDisplayMetrics().density;
        //Utils.putIntegerPreference("SCREENH", SCREENH, this);
        Utils.putIntegerPreference("DISPLAYDENSITY", density, this);
	
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

    /**
     *
     *
     *
     *
     */
    public void volumes( View v ) {
    	Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
    }

    /**
     *
     *
     *
     *
     */
    public void neutron( View v ) {
    	Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
    }
}
