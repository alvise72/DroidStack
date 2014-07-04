package org.openstack.activities;

import android.os.Bundle;
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
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
	super.onResume( );
	refreshOSImagesViews();
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
	    User U = User.fromFile( ID );
	    ...
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
}
