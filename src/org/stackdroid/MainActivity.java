package org.stackdroid;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.stackdroid.R;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.NotExistingFileException;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.activities.FloatingIPActivity;
import org.stackdroid.activities.NeutronActivity;
import org.stackdroid.activities.NeutronNetworkActivity;
import org.stackdroid.activities.NeutronRouterActivity;
import org.stackdroid.activities.SecGrpActivity;
import org.stackdroid.activities.UsersActivity;
import org.stackdroid.activities.ServersActivity;
import org.stackdroid.activities.OSImagesActivity;
import org.stackdroid.activities.OverViewActivity;
import org.stackdroid.activities.VolumesActivity;


public class MainActivity extends Activity
{
    private int    SCREENW 		= 0;
    private String selectedUser = null;

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
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        SCREENW = dm.widthPixels;
        int density = (int)this.getResources().getDisplayMetrics().density;
        
        Configuration.getInstance().setValue( "DISPLAYDENSITY", ""+density );
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
    }

    private void disableButtons( ) {
      ((Button)this.findViewById(R.id.GLANCE)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.NOVA)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.NEUTRON)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.CINDER)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.SECG)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.FIPS)).setEnabled(false);
  	  ((Button)this.findViewById(R.id.OVERVIEW)).setEnabled(false);
    }
    
    private void enableButtons( ) {
      ((Button)this.findViewById(R.id.GLANCE)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.NOVA)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.NEUTRON)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.CINDER)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.SECG)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.FIPS)).setEnabled(true);
  	  ((Button)this.findViewById(R.id.OVERVIEW)).setEnabled(true);
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
      
      if( !this.isExternalStorageWritable() ) {

    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setMessage( getString(R.string.NOEXTSTORAGEWRITABLE));//"External storage is not writable ! This App cannot work." );
    	  builder.setCancelable(false);
	    
    	  DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int id) {
    			  finish( );
    		  }
	      };

	      builder.setPositiveButton("OK", yesHandler );
	        
	      AlertDialog alert = builder.create();
	      alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	      alert.setCancelable(false);
	      alert.setCanceledOnTouchOutside(false);
	      alert.show();
	      return;
      }
      
      if( !Utils.internetOn( this ) ) {

    	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	  builder.setMessage( getString(R.string.NOINTERNETCONNECTION) );
    	  builder.setCancelable(false);
	    
    	  DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
    		  public void onClick(DialogInterface dialog, int id) {
    			  finish( );
    		  }
	      };

	      builder.setPositiveButton("OK", yesHandler );
	        
	      AlertDialog alert = builder.create();
	      alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	      alert.setCancelable(false);
	      alert.setCanceledOnTouchOutside(false);
	      alert.show();
	      return;
      }
      
      File file = new File(Environment.getExternalStorageDirectory() + "/DroidStack" );
      file.mkdirs( );
      Configuration.getInstance().setValue( "FILESDIR", file.getPath() );
      (new File(Environment.getExternalStorageDirectory() + "/DroidStack/users" )).mkdirs( );
      selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
      User U = null;
      //Log.d("MAIN", "Selected user="+selectedUser);
      if(selectedUser.length()!=0) {  
 
    	  try {
    		  U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
    		  if(U==null) {
          		Utils.alert(getString(R.string.RECREATEUSERS), this);
          		disableButtons( );
          		return;
          	  }
    		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
    		  enableButtons( );
        	  
    	  } catch(ClassNotFoundException cnfe) {
    		  Utils.putStringPreference("SELECTEDUSER", "", this);
    		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
        	  disableButtons( );
        	  (new File(Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) + "/users/" + selectedUser )).delete();
        	  return;
    	  }
    	    catch(NotExistingFileException nf) {
    		  Utils.putStringPreference("SELECTEDUSER", "", this);
    		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
        	  disableButtons( );
        	  return;
          } catch(IOException ioe) {
    		  Utils.alert("ERROR: "+ioe.getMessage() + "\n\n"+getString(R.string.RECREATEUSERS), this);
    		  disableButtons( );
        	  return;
    	  } catch(Exception e) {
    		  Utils.alert("ERROR: "+e.getMessage(), this );
    		  disableButtons( );
    		  return;
    	  }
    	  
    	  if(U.hasGlance()==false) {
        	  ((Button)this.findViewById(R.id.GLANCE)).setEnabled(false);
          }
          
          if(U.hasNova()==false) {
        	  ((Button)this.findViewById(R.id.NOVA)).setEnabled(false);
          }
          
          if(U.hasNeutron()==false) {
        	  ((Button)this.findViewById(R.id.NEUTRON)).setEnabled(false);
          }
          
          if(!U.hasCinder1() && !U.hasCinder2()) {
        	  ((Button)this.findViewById(R.id.CINDER)).setEnabled(false);
          }
    	  
      } else {
    	  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
    	  disableButtons( );
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
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
    	Class<?> c = (Class<?>)VolumesActivity.class;
    	Intent I = new Intent( MainActivity.this, c );
    	startActivity(I);
    }

    /**
     *
     *
     *
     *
     */
    public void neutron( View v ) {
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
    	Class<?> c = (Class<?>)NeutronNetworkActivity.class;
    	Intent I = new Intent( MainActivity.this, c );
    	startActivity( I );
    }

    /**
     *
     *
     *
     *
     */
    public void routers( View v ) {
    	if(selectedUser.length()==0) {
    	    Utils.alert( getString(R.string.NOUSERSELECTED) , this);
    	    return;
    	}
    	Class<?> c = (Class<?>)NeutronRouterActivity.class;
    	Intent I = new Intent( MainActivity.this, c );
    	startActivity( I );
    }

    /**
     *
     *
     *
     *
     */
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
