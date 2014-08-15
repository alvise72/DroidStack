package org.droidstack.activities;

import android.os.Bundle;
//import android.os.Environment;


import android.widget.LinearLayout;
/*import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button; */
import android.widget.Toast;

import android.content.Intent;
/*
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.net.Uri;

import android.util.Log;
import android.util.DisplayMetrics;

import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.ActivityManager;
*/
import android.app.Activity;

import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.view.Gravity;
import android.view.View;

//import java.io.IOException;

//import java.util.Iterator;
//import java.util.Vector;
//import java.util.Set;

import org.droidstack.R;
import org.droidstack.utils.User;
//import org.droidstack.utils.UserException;
import org.droidstack.utils.Utils;
/*
import org.droidstack.utils.Base64;
import org.droidstack.comm.RESTClient;
import org.droidstack.parse.ParseUtils;
import org.droidstack.parse.ParseException;
*/
import java.io.File;

import org.droidstack.views.UserView;
//import org.droidstack.utils.Named;
//import org.droidstack.utils.ImageViewNamed;
import org.droidstack.utils.TextViewNamed;
import org.droidstack.utils.ImageButtonNamed;
import org.droidstack.utils.LinearLayoutNamed;

//import android.graphics.Typeface;
//import android.graphics.Color;

public class UsersActivity extends Activity implements OnClickListener {

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.users );
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
    refreshUserViews();
    if(Utils.getStringPreference("SELECTEDUSER","",this).length()==0) {
	Toast t = Toast.makeText(this, getString(R.string.TOUCHUSERTOSELECT), Toast.LENGTH_SHORT) ;
	t.show( );
    }
  }
  
  //__________________________________________________________________________________
  public void addUser( View v ) {
    Class<?> c = (Class<?>)UserAddActivity.class;
    Intent I = new Intent( UsersActivity.this, c );
    startActivity( I );  
  }

  //__________________________________________________________________________________
    public void onClick( View v ) { 
	if(v instanceof ImageButtonNamed) {
	    if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_DELETE_USER ) {
		String filenameToDelete = ((ImageButtonNamed)v).getUserView( ).getFilename();
		
		(new File(Utils.getStringPreference("FILESDIR", "", this) + "/users/"+filenameToDelete)).delete();
		String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
		if(selectedUser.compareTo(filenameToDelete)==0)
		    Utils.putStringPreference( "SELECTEDUSER", "", this);
		
		refreshUserViews();
		return;
	    }
	    if(((ImageButtonNamed)v).getType( ) == ImageButtonNamed.BUTTON_MODIFY_USER ) {
		Utils.alert( getString(R.string.NOTIMPLEMENTED) , this);
		return;
	    }
	}

	if(v instanceof TextViewNamed) {
	    //String selectedUser = ((TextViewNamed)v).getUserView().getFilename();

	    Utils.putStringPreference("SELECTEDUSER", ((TextViewNamed)v).getUserView().getFilename(), this);
	    
	    refreshUserViews();

	    return;
	}

	if(v instanceof LinearLayoutNamed) {
	    Utils.putStringPreference("SELECTEDUSER", ((LinearLayoutNamed)v).getUserView().getFilename(), this);
	    refreshUserViews();
	}
	
    }

    //__________________________________________________________________________________
    private void refreshUserViews( ) {
	File[] users = (new File(Utils.getStringPreference("FILESDIR", "", this) + "/users/")).listFiles();
	if(users==null) {
	    Utils.alert("UsersActivity.refreshUserViews: " 
			+ Utils.getStringPreference("FILESDIR", "", this) 
			+ "/users/" 
			+ " exists but it is not a directory !", this);
	    return;
	}
	    
	// TODO: should we filter here ?

	((LinearLayout)findViewById(R.id.userLayout)).removeAllViews();

	for(int i = 0; i<users.length; ++i) {
	    User U = null;
	    try {
		
		U = User.fromFileID( users[i].getName( ), Utils.getStringPreference("FILESDIR","",this) );
		
	    } catch(Exception e) {
		Utils.alert("ERROR: " + e.getMessage(), this);
		continue;
	    }
	    
	    UserView uv = new UserView ( U, this );
	    ((LinearLayout)findViewById(R.id.userLayout)).addView( uv );
	    View space = new View( this );
	    space.setMinimumHeight(10);
	    ((LinearLayout)findViewById(R.id.userLayout)).addView( space );
	    
	    if( uv.getFilename().compareTo(Utils.getStringPreference("SELECTEDUSER","",this))==0 )
		uv.setSelected( );
	    else
		uv.setUnselected( );
	}
    }
}