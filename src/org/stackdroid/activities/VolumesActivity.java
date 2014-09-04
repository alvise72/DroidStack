package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;

import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.comm.OSClient;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;



import org.stackdroid.R;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;
import org.stackdroid.views.VolumeView;
import org.stackdroid.utils.ImageButtonNamed;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;

public class VolumesActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User 				 U 						= null;
	//public 	String 				 serverID 				= null;
	//private String 				 serverid 				= null;
    
    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
        menu.add(GROUP, 2, order++, getString(R.string.MENUDELETEALL) ).setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }
    
    //__________________________________________________________________________________
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
        
        if( id == Menu.FIRST ) { 
        	if(U==null) {
        		Utils.alert("An error occurred recovering User from sdcard. Try to go back and return to this activity.", this);
        	} else {
        		progressDialogWaitStop.show();
        		(new AsyncTaskListVolumes()).execute( );
        		//AsyncTaskOSListServers task = new AsyncTaskOSListServers();
        		//task.execute( );
        		return true;
        	}
        }

        if( id == Menu.FIRST+1 ) { 
/*	      if(U==null) {
		    Utils.alert("An error occurred recovering User from sdcard. Try to go back and return to this activity.", this);
	      } else {
	      
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  		builder.setMessage( getString(R.string.AREYOUSURETODELETEVMS));
	  		builder.setCancelable(false);
	  	    
	  		DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
	  			public void onClick(DialogInterface dialog, int id) {
	  				progressDialogWaitStop.show();
	  				AsyncTaskDeleteServer task = new AsyncTaskDeleteServer();
	  				int numChilds = ((LinearLayout)findViewById(R.id.volumeLayout)).getChildCount();
	  				String[] listedVolumes = new String[numChilds];
	  				for(int i = 0; i < numChilds; ++i) {
	  				    View vv = ((LinearLayout)findViewById(R.id.volumeLayout)).getChildAt(i);
	  				    if(vv instanceof VolumeView)
	  					listedServers[i] = ((ServerView)sv).getServer().getID();
	  				}
	  				task.execute( Utils.join(listedServers, ",") ) ;
	  			}
	  		    };

	  		DialogInterface.OnClickListener noHandler = new DialogInterface.OnClickListener() {
	  			public void onClick(DialogInterface dialog, int id) {
	  			    dialog.cancel( );
	  			}
	  		    };

	  		builder.setPositiveButton(getString(R.string.YES), yesHandler );
	  		builder.setNegativeButton(getString(R.string.NO), noHandler );
	              
	  		AlertDialog alert = builder.create();
	  		alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
	  					    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	  		alert.show();
	    	
	    	
		
		return true;*/
	    }
        
	return super.onOptionsItemSelected( item );
    }

    //__________________________________________________________________________________
    @Override
    public void onClick( View v ) {
	if(v instanceof ImageButtonNamed) {
		ImageButtonNamed bt = (ImageButtonNamed)v;
		
		if(bt.getType() == ImageButtonNamed.BUTTON_ATTACHDETACH_VOlUME) {
			Volume V = bt.getVolumeView().getVolume();
			if(V.isAttached()) {
				// DETACH
				return;
			}
			// ATTACH
			
		}
/*	    if( ((ImageButtonNamed)v).getType() == ImageButtonNamed.BUTTON_DELETE_SERVER ) {
		// Delete the server
		final String serverid = ((ImageButtonNamed)v).getServerView( ).getServer().getID();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( getString(R.string.AREYOUSURETODELETEVM));
		builder.setCancelable(false);
	    
		DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			    deleteNovaInstance( serverid );
			}
		    };

		DialogInterface.OnClickListener noHandler = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			    dialog.cancel( );
			}
		    };

		builder.setPositiveButton(getString(R.string.YES), yesHandler );
		builder.setNegativeButton(getString(R.string.NO), noHandler );
            
		AlertDialog alert = builder.create();
		alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
					    WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		alert.show();

		
	    }
	    if( ((ImageButtonNamed)v).getType() == ImageButtonNamed.BUTTON_SNAP_SERVER ) {
	    	//Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
	    	
	    	serverid  = ((ImageButtonNamed)v).getServerView().getServer().getID();
	    	
	    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.INPUTSNAPNAME));
            final EditText input = new EditText(this);
            alert.setView(input);
            alert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int whichButton) {
                            String snapname = input.getText().toString();
                            snapname = snapname.trim();
                            if(snapname==null || snapname.length()==0) {
                            	Utils.alert(getString(R.string.NOEMPTYNAME), ServersActivity.this);
                            } else {
                              //button.setText(newCateg);
                              ServersActivity.this.progressDialogWaitStop.show();
                              (new AsyncTaskCreateSnapshot( )).execute(serverid, snapname);
                            }
                        }
                    });
            AlertDialog build = alert.create();
            build.show();
	    	
	    	
	    	
	    	
	    	return;
	    }
	}
	
	if(v instanceof TextViewNamed || v instanceof ServerView) {
	    Server s = null;
	    if( v instanceof TextViewNamed )
		s = ((TextViewNamed)v).getServerView( ).getServer( );
	    if( v instanceof ServerView )
		s = ((ServerView)v).getServer( );

	    String[] secgrps = s.getSecurityGroupNames( );

	    TextView tv1 = new TextView(this);
	    tv1.setText("Instance name:");
	    tv1.setTypeface( null, Typeface.BOLD );
	    TextView tv2 = new TextView(this);
	    tv2.setText(s.getName());
	    TextView tv3 = new TextView(this);
	    tv3.setText("Status:");
	    tv3.setTypeface( null, Typeface.BOLD );
	    TextView tv4 = new TextView(this);
	    tv4.setText(s.getStatus() + " ("+ (s.getTask()!=null && s.getTask().length()!=0 ? s.getTask() : "None") + ")");
	    TextView tv5 = new TextView(this);
	    tv5.setText("Flavor: ");
	    tv5.setTypeface( null, Typeface.BOLD );
	    TextView tv6 = new TextView(this);
	    tv6.setText( s.getFlavor( ).getFullInfo() );//.getName() + " (" + (int)(s.getFlavor( ).getDISK()) + "GB, " +s.getFlavor( ).getVCPU( )+ " CPU, " + s.getFlavor( ).getRAM( ) + "MB RAM)" );
	    TextView tv7 = new TextView(this);
	    tv7.setText("Fixed IP(s):");
	    tv7.setTypeface( null, Typeface.BOLD );
	    TextView[] tv8_privip = null;
	    if(s.getPrivateIP().length==0) {
		tv8_privip = new TextView[1];
		tv8_privip[0] = new TextView(this);
		tv8_privip[0].setText( "None" );
	    } else {
		tv8_privip = new TextView[s.getPrivateIP().length];
		for(int i = 0; i<s.getPrivateIP().length; i++) {
		    tv8_privip[i] = new TextView(this);
		    tv8_privip[i].setText( s.getPrivateIP()[i] );
		}
	    }

	    TextView tv9 = new TextView(this);
	    tv9.setText("Floating IP(s):");
	    tv9.setTypeface( null, Typeface.BOLD );
	    TextView[] tv10_pubip = null;
	    if(s.getPublicIP().length==0) {
		tv10_pubip =new TextView[1];
		tv10_pubip[0] = new TextView(this);
		tv10_pubip[0].setText( "None" );
	    } else {
		tv10_pubip = new TextView[s.getPublicIP().length];
		for(int i = 0; i<s.getPublicIP().length; i++) {
		    tv10_pubip[i] = new TextView(this);
		    tv10_pubip[i].setText( s.getPublicIP( )[i]  );
		}
	    }
	    TextView tv11 = new TextView( this );
	    tv11.setText("Key name:");
	    tv11.setTypeface( null, Typeface.BOLD );
	    TextView tv12 = new TextView( this );
	    tv12.setText( s.getKeyName( ).length() != 0 ? s.getKeyName( ) : "None" );
	    TextView tv13 = new TextView( this );
	    tv13.setText("Security groups:");
	    tv13.setTypeface( null, Typeface.BOLD );
	    TextView tv14 = new TextView( this );
	    if(secgrps != null && secgrps.length!=0)
		tv14.setText( Utils.join(s.getSecurityGroupNames(),", ") );
	    else
		tv14.setText( "None" );
	    TextView tv15 = new TextView( this );
	    tv15.setText("Hosted by:");
	    tv15.setTypeface( null, Typeface.BOLD );
	    TextView tv16 = new TextView( this );
	    tv16.setText( s.getComputeNode( ) );
	    
	    
	    ScrollView sv = new ScrollView(this);
	    LinearLayout.LayoutParams lp 
		= new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
	    sv.setLayoutParams( lp );
	    LinearLayout l = new LinearLayout(this);
	    l.setLayoutParams( lp );
	    l.setOrientation( LinearLayout.VERTICAL );
	    int paddingPixel = 8;
	    float density = Utils.getDisplayDensity( this );
	    int paddingDp = (int)(paddingPixel * density);
	    l.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv1 );
	    l.addView( tv2 );
	    tv2.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv3 );
	    l.addView( tv4 );
	    tv4.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv5 );
	    l.addView( tv6 );
	    tv6.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv7 );
	    //l.addView( tv8 );
	    for(int i = 0; i<tv8_privip.length; ++i) {
		l.addView(tv8_privip[i]);
		tv8_privip[i].setPadding(paddingDp, 0, 0, 0);
	    }
	    l.addView( tv9 );
	    for(int i = 0; i<tv10_pubip.length; ++i) {
		l.addView(tv10_pubip[i]);
		tv10_pubip[i].setPadding(paddingDp, 0, 0, 0);
	    }
	    //l.addView( tv10 );
	    //tv10.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv11 );
	    l.addView( tv12 );
	    tv12.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv13 );
	    tv14.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv14 );
	    l.addView( tv15 );
	    tv16.setPadding(paddingDp, 0, 0, 0);
	    l.addView( tv16 );
	    sv.addView(l);
	    String name;
	    if(s.getName().length()>=16)
		name = s.getName().substring(0,14) + "..";
	    else
		name = s.getName();
	    Utils.alertInfo( sv, "Instance information: "+name, this );
	    
	}
	if(v instanceof ButtonNamed ) {
		serverID = ((ButtonNamed)v).getServerView().getServer().getID();
		//Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
		progressDialogWaitStop.show();
		ServersActivity.AsyncTaskOSLogServer task = new ServersActivity.AsyncTaskOSLogServer();
		task.execute( );
		//return;
	}
    }

    private void deleteNovaInstance( String serverid ) {
	progressDialogWaitStop.show();
	AsyncTaskDeleteServer task = new AsyncTaskDeleteServer();
	String[] ids = new String[1];
	ids[0] = serverid;
	task.execute( ids ) ;*/
	return;
    }
    }
    
    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView( R.layout.volumelist );
	
	//listedServers = new HashSet();

	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
	
	String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	try {
	    U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
	} catch(RuntimeException re) {
	    Utils.alert("VolumesActivity.onCreate: "+re.getMessage(), this );
	    return;
	}
	if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
	progressDialogWaitStop.show();
	(new AsyncTaskListVolumes()).execute( );
    }
    
    //__________________________________________________________________________________
    @Override
    public void onResume( ) {
    	super.onResume( );
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
    private void refreshView( Vector<Volume> volumes ) {
    	((LinearLayout)findViewById(R.id.volumeLayout)).removeAllViews();
    	if(volumes.size()==0) {
    		Utils.alert(getString(R.string.NOVOLUMEAVAIL), this);	
    		return;
    	}
    	Iterator<Volume> vit = volumes.iterator();
    	while(vit.hasNext()) {
    		Volume v = vit.next();
    		VolumeView vv = new VolumeView(v, this);
    		((LinearLayout)findViewById( R.id.volumeLayout) ).addView( vv );
    		((LinearLayout)findViewById( R.id.volumeLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.volumeLayout)).addView( space );
    	}
    }


    //  ASYNC TASKS.....


    //__________________________________________________________________________________
    protected class AsyncTaskListVolumes extends AsyncTask< Void, Void, Void >
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	private  String   jsonBufVols      = null;
     	private  String   jsonBufServers   = null;
     	
     	@Override
     	protected Void doInBackground( Void ... v ) 
     	{
  	      OSClient osc = OSClient.getInstance( U );

     		try {
     			jsonBufVols		= osc.requestVolumes( );
     			jsonBufServers	= osc.requestServers( );
     		} catch(Exception e) {
     			errorMessage = e.getMessage();
     			hasError = true;
     		}
			return null;
     	}
	
     	@Override
	    protected void onPostExecute( Void v ) {
     		super.onPostExecute( v );
	    
     		if(hasError) {
     			Utils.alert( errorMessage, VolumesActivity.this );
     			VolumesActivity.this.progressDialogWaitStop.dismiss( );
     			return;
     		}
	    
     		try {
     			Vector<Volume> volumes = ParseUtils.parseVolumes( jsonBufVols, jsonBufServers );
     			VolumesActivity.this.refreshView( volumes );
     		} catch(ParseException pe) {
     			Log.e("VOLUMES", jsonBufVols);
     			Utils.alert("VolumesActivity.AsyncTaskListVolumes.onPostExecute - Error parsing json: "+pe.getMessage( ), VolumesActivity.this );
     		} 
     		VolumesActivity.this.progressDialogWaitStop.dismiss( );
     	}
    }

}
