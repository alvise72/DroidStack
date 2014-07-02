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
import android.content.pm.ActivityInfo;

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

public class ServersActivity extends Activity implements OnClickListener {

    private Bundle bundle = null;
    private ArrayList<Server> S = null;

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.serverlist );
	bundle = getIntent().getExtras();
	S = (ArrayList<Server>)bundle.getSerializable("SERVERS");
// 	Iterator<Server> sit = S.iterator();
// 	while(sit.hasNext()) {
// 	    Server s = sit.next();
// 	    Utils.alert(s.toString(), this );
// 	}
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
	super.onResume( );
	refreshServerViews();
    }
 
    //__________________________________________________________________________________
    @Override
    public void onPause( ) {
	super.onPause( );
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
	Iterator<Server> sit = S.iterator();
	((LinearLayout)findViewById(R.id.serverLayout)).removeAllViews();
	while( sit.hasNext( )) {
	    Server s = sit.next();
	    ((LinearLayout)findViewById(R.id.serverLayout)).addView( new ServerView(s, this) );
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
