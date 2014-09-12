package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.activities.SecGrpActivity.AsyncTaskCreateSecGroup;
import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.NotFoundException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.R;
import org.stackdroid.utils.ButtonNamed;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.IPAddressKeyListener;
import org.stackdroid.utils.LinearLayoutNamed;
import org.stackdroid.utils.SimpleNumberKeyListener;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.Flavor;
import org.stackdroid.views.ServerView;
import org.stackdroid.utils.TextViewNamed;
import org.stackdroid.utils.ImageButtonNamed;

import android.graphics.Typeface;
import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;


public class ServersActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User 				 U 						= null;
	public 	String 				 serverID 				= null;
	private String 				 serverid 				= null;
    
	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ServerDeleteClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
			// Delete the server
			final String serverid = ((ImageButtonNamed)v).getServerView( ).getServer().getID();

			AlertDialog.Builder builder = new AlertDialog.Builder(ServersActivity.this);
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
			alert.getWindow( ).setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			alert.setCancelable(false);
			alert.setCanceledOnTouchOutside(false);
			alert.show();

		}
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ServerSnapClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
		   	serverid  = ((ImageButtonNamed)v).getServerView().getServer().getID();
		    	
		   	final AlertDialog.Builder alert = new AlertDialog.Builder(ServersActivity.this);
	        alert.setMessage(getString(R.string.INPUTSNAPNAME));
	        final EditText input = new EditText(ServersActivity.this);
	        alert.setView(input);
	        alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog,int whichButton) {
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
	         alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    
	                }
	         });
	         alert.setCancelable(false);
	         //alert.setCanceledOnTouchOutside(false);
	         AlertDialog dia = alert.create();
	         dia.setCancelable(false);
	         dia.setCanceledOnTouchOutside(false);
	         dia.show( );
		    	
		     return;
		    }
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ServerInfoClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
		    Server s = null;
			if( v instanceof TextViewNamed )
				s = ((TextViewNamed)v).getServerView( ).getServer( );
			if( v instanceof ServerView )
				s = ((ServerView)v).getServer( );
			if( v instanceof LinearLayoutNamed )
				s = ((LinearLayoutNamed)v).getServerView( ).getServer();
			
			String[] secgrps = s.getSecurityGroupNames( );

			TextView tv1 = new TextView(ServersActivity.this);
			tv1.setText("Instance name:");
			tv1.setTypeface( null, Typeface.BOLD );
			TextView tv2 = new TextView(ServersActivity.this);
			tv2.setText(s.getName());
			TextView tv3 = new TextView(ServersActivity.this);
			tv3.setText("Status:");
			tv3.setTypeface( null, Typeface.BOLD );
			TextView tv4 = new TextView(ServersActivity.this);
			tv4.setText(s.getStatus() + " ("+ (s.getTask()!=null && s.getTask().length()!=0 ? s.getTask() : "None") + ")");
			TextView tv5 = new TextView(ServersActivity.this);
			tv5.setText("Flavor: ");
			tv5.setTypeface( null, Typeface.BOLD );
			TextView tv6 = new TextView(ServersActivity.this);
			tv6.setText( s.getFlavor( ).getFullInfo() );
			TextView tv7 = new TextView(ServersActivity.this);
			tv7.setText("Fixed IP(s):");
			tv7.setTypeface( null, Typeface.BOLD );
			TextView[] tv8_privip = null;
			if(s.getPrivateIP().length==0) {
				tv8_privip = new TextView[1];
				tv8_privip[0] = new TextView(ServersActivity.this);
				tv8_privip[0].setText( "None" );
			} else {
				tv8_privip = new TextView[s.getPrivateIP().length];
				for(int i = 0; i<s.getPrivateIP().length; i++) {
					tv8_privip[i] = new TextView(ServersActivity.this);
					tv8_privip[i].setText( s.getPrivateIP()[i] );
				}
			}

			TextView tv9 = new TextView(ServersActivity.this);
			tv9.setText("Floating IP(s):");
			tv9.setTypeface( null, Typeface.BOLD );
			TextView[] tv10_pubip = null;
			if(s.getPublicIP().length==0) {
				tv10_pubip =new TextView[1];
				tv10_pubip[0] = new TextView(ServersActivity.this);
				tv10_pubip[0].setText( "None" );
			} else {
				tv10_pubip = new TextView[s.getPublicIP().length];
				for(int i = 0; i<s.getPublicIP().length; i++) {
					tv10_pubip[i] = new TextView(ServersActivity.this);
					tv10_pubip[i].setText( s.getPublicIP( )[i]  );
				}
			}
			TextView tv11 = new TextView( ServersActivity.this );
			tv11.setText("Key name:");
			tv11.setTypeface( null, Typeface.BOLD );
			TextView tv12 = new TextView( ServersActivity.this );
			tv12.setText( s.getKeyName( ).length() != 0 ? s.getKeyName( ) : "None" );
			TextView tv13 = new TextView( ServersActivity.this );
			tv13.setText("Security groups:");
			tv13.setTypeface( null, Typeface.BOLD );
			TextView tv14 = new TextView( ServersActivity.this );
			if(secgrps != null && secgrps.length!=0)
				tv14.setText( Utils.join(s.getSecurityGroupNames(),", ") );
			else
				tv14.setText( "None" );
			TextView tv15 = new TextView( ServersActivity.this );
			tv15.setText("Hosted by:");
			tv15.setTypeface( null, Typeface.BOLD );
			TextView tv16 = new TextView( ServersActivity.this );
			tv16.setText( s.getComputeNode( ) );
	    
	    
			ScrollView sv = new ScrollView(ServersActivity.this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
			sv.setLayoutParams( lp );
			LinearLayout l = new LinearLayout(ServersActivity.this);
			l.setLayoutParams( lp );
			l.setOrientation( LinearLayout.VERTICAL );
			int paddingPixel = 8;
			float density = Utils.getDisplayDensity( ServersActivity.this );
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
			for(int i = 0; i<tv8_privip.length; ++i) {
				l.addView(tv8_privip[i]);
				tv8_privip[i].setPadding(paddingDp, 0, 0, 0);
			}
			l.addView( tv9 );
			for(int i = 0; i<tv10_pubip.length; ++i) {
				l.addView(tv10_pubip[i]);
				tv10_pubip[i].setPadding(paddingDp, 0, 0, 0);
			}
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
			Utils.alertInfo( sv, "Instance information: "+name, ServersActivity.this );
		}
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ConsoleLogClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
			serverID = ((ButtonNamed)v).getServerView().getServer().getID();
			
			final AlertDialog.Builder alert = new AlertDialog.Builder(ServersActivity.this);
	        alert.setMessage(getString(R.string.INPUTNUMLOGLINES));
	        final EditText input = new EditText(ServersActivity.this);
	        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE);
	        input.setKeyListener(SimpleNumberKeyListener.getInstance());
	        //input.setTransformationMethod(TransormationMethod.);
	        
	        alert.setView(input);
	        alert.setPositiveButton("Ok",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	if(input.getText().toString().trim().length()==0)
	                    		return;
	                    	int num = Integer.parseInt(input.getText().toString().trim());
	                    	if(num==0) {
	                    		return;
	                    	}
	                        ServersActivity.this.progressDialogWaitStop.show();
	                        (new ServersActivity.AsyncTaskOSLogServer()).execute( input.getText().toString().trim() );
	                    }
	                });
	        alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                        return;
	                    }
	                });
	        alert.setCancelable(false);
	        AlertDialog dia = alert.create();
	        dia.setCancelable(false);
	        dia.setCanceledOnTouchOutside(false);
	        dia.show( );
		}
	}
	
	/**
	 * 
	 * @author dorigoa
	 *
	 */
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUDELETEALL) ).setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }

    public void update(View v) {
    	progressDialogWaitStop.show();
		(new AsyncTaskOSListServers()).execute( );
    }
    
	/**
	 * 
	 * @author dorigoa
	 *
	 */
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
	      
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  		builder.setMessage( getString(R.string.AREYOUSURETODELETEVMS));
	  		builder.setCancelable(false);
	  	    
	  		DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
	  			public void onClick(DialogInterface dialog, int id) {
	  				progressDialogWaitStop.show();
	  				AsyncTaskDeleteServer task = new AsyncTaskDeleteServer();
	  				int numChilds = ((LinearLayout)findViewById(R.id.serverLayout)).getChildCount();
	  				String[] listedServers = new String[numChilds];
	  				for(int i = 0; i < numChilds; ++i) {
	  				    View sv = ((LinearLayout)findViewById(R.id.serverLayout)).getChildAt(i);
	  				    if(sv instanceof ServerView)
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
	  		alert.setCancelable(false);
	  		alert.setCanceledOnTouchOutside(false);
	  		alert.show();
	    	
	    	
		
		return true;
	    }
        }
	return super.onOptionsItemSelected( item );
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    private void deleteNovaInstance( String serverid ) {
    	progressDialogWaitStop.show();
    	AsyncTaskDeleteServer task = new AsyncTaskDeleteServer();
    	String[] ids = new String[1];
    	ids[0] = serverid;
    	task.execute( ids ) ;
    	return;
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.serverlist );
	
    	//listedServers = new HashSet();

    	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
        progressDialogWaitStop.setCancelable(false);
        progressDialogWaitStop.setCanceledOnTouchOutside(false);
        String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
        try {
        	U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
        } catch(Exception re) {
        	Utils.alert("ServersActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
        progressDialogWaitStop.show();
        (new AsyncTaskOSListServers()).execute( );
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    @Override
    public void onDestroy( ) {
    	super.onDestroy( );
    	progressDialogWaitStop.dismiss();
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    private void refreshView( Vector<Server> servers, Vector<Flavor> flavors ) {
    	((LinearLayout)findViewById(R.id.serverLayout)).removeAllViews();
    	if(servers.size()==0) {
    		Utils.alert(getString(R.string.NOINSTANCEAVAIL), this);	
    		return;
    	}
	
    	Hashtable<String, Flavor> flavHash = new Hashtable<String, Flavor>();
    	Iterator<Flavor> fit = flavors.iterator();
    	while( fit.hasNext( ) ) {
    		Flavor f = fit.next();
    		flavHash.put( f.getID(), f );
    	}
	
    	Iterator<Server> it = servers.iterator();
	
    	while(it.hasNext()) {
    		Server s = it.next();
    		Flavor F = flavHash.get( s.getFlavorID( ) );
    		if( F != null)
    			s.setFlavor( F );
    		ServerView sv = new ServerView(s, new ServersActivity.ServerInfoClickListener(),
    										  new ServersActivity.ConsoleLogClickListener(),
    										  new ServersActivity.ServerDeleteClickListener(),
    										  new ServersActivity.ServerSnapClickListener(),
    										  this);
    		((LinearLayout)findViewById( R.id.serverLayout) ).addView( sv );
    		((LinearLayout)findViewById( R.id.serverLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.serverLayout)).addView( space );
    	}
    }













    //  ASYNC TASKS.....


    
  //__________________________________________________________________________________
    protected class AsyncTaskCreateSnapshot extends AsyncTask<String, String, String>
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
     	@Override
     	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		String snapname = v[1];
		    OSClient osc = OSClient.getInstance( U );

     		try {
     			osc.createInstanceSnapshot( serverid, snapname );
     		} catch(Exception e) {
     			errorMessage = e.getMessage();
     			hasError = true;
     			return "";
     		}
	    
     		return "";
     	}
	
	@Override
	protected void onPostExecute( String result ) {
	    super.onPostExecute(result);
	    
 	    if(hasError) {
 	    	Utils.alert( errorMessage, ServersActivity.this );
 	    	ServersActivity.this.progressDialogWaitStop.dismiss( );
 	    	return;
 	    }
		ServersActivity.this.progressDialogWaitStop.dismiss( );
		Utils.alert(ServersActivity.this.getString(R.string.SNAPCREATED), ServersActivity.this);
	}
    }







    
    //__________________________________________________________________________________
    protected class AsyncTaskOSListServers extends AsyncTask<Void, String, String>
    {
     	private  String   errorMessage     = null;
	private  boolean  hasError         = false;
	private  String   jsonBuf          = null;
	private  String   jsonBufferFlavor = null;
	//private  String   username         = null;

	@Override
	protected String doInBackground( Void... v ) 
	{
	      OSClient osc = OSClient.getInstance( U );

	    

	      try {
	    	  jsonBuf 			= osc.requestServers( );
	    	  jsonBufferFlavor  = osc.requestFlavors( );
	      } catch(Exception e) {
	    	  errorMessage = e.getMessage();
	    	  hasError = true;
	    	  return "";
	      }
	    
	      return jsonBuf;
	}
	
		@Override
	    protected void onPostExecute( String result ) {
			super.onPostExecute(result);
	    
			if(hasError) {
				Utils.alert( errorMessage, ServersActivity.this );
				ServersActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
	    
			try {
				Vector<Server> servers = ParseUtils.parseServers( jsonBuf );
				ServersActivity.this.refreshView( servers, ParseUtils.parseFlavors( jsonBufferFlavor ) );
			} catch(ParseException pe) {
				Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), ServersActivity.this );
			}
			ServersActivity.this.progressDialogWaitStop.dismiss( );
		}
    }
    
    //__________________________________________________________________________________
    protected class AsyncTaskDeleteServer extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	private  String[] serverids        = null;
     	private  boolean  not_found        = false;
     	@Override
     	protected Void doInBackground(String... args ) 
     	{
     		serverids = args[0].split(",");
     		OSClient osc = OSClient.getInstance( U );


     		try {
     			not_found = false;
     			for(int i = 0; i<serverids.length; ++i) {
     				try {
     					osc.deleteInstance( serverids[i] );
     				} catch(NotFoundException nfe) {
     					Log.d("SERVERSACT",nfe.getMessage());
     					not_found = true;
     				}
     			}
     		} catch(Exception e) {
     			errorMessage = e.getMessage();
     			hasError = true;
     			return null;
     		}
	    
     		return null;
     	}
	
     	@Override
     	protected void onPostExecute( Void v ) {
     		super.onPostExecute(v);
     		
     		if(not_found==true) {
     			Utils.alert(ServersActivity.this.getString(R.string.SOMEDELETIONFAILED), ServersActivity.this );
     			ServersActivity.this.progressDialogWaitStop.dismiss( );
     			return;
     		}
     		if(hasError==true)
     			Utils.alert( errorMessage, ServersActivity.this );
     		else {
     			Utils.alert(getString(R.string.DELETEDINSTSANCES), ServersActivity.this );
     			(new AsyncTaskOSListServers()).execute( );
     		}
     		ServersActivity.this.progressDialogWaitStop.dismiss( );
     	}
   }
    
  //__________________________________________________________________________________
    protected class AsyncTaskOSLogServer extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	private  String   jsonBuf          = null;
     	//private  int	  maxnumlines      = 0;

	@Override
	protected Void doInBackground( String... v ) 
	{
	    OSClient osc = OSClient.getInstance( U );
	    int maxnumlines = Integer.parseInt(v[0]);
	    try {
		  jsonBuf = osc.requestServerLog( ServersActivity.this.serverID, maxnumlines );
	    } catch(Exception e) {
		  errorMessage = e.getMessage();
		  hasError = true;
		  //return;
	    }
	    //return jsonBuf;
		return null;
	}
	
	@Override
	    protected void onPostExecute( Void v ) {
	    super.onPostExecute( v );
	    
 	    if(hasError) {
 		  Utils.alert( errorMessage, ServersActivity.this );
 		  ServersActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
	    
	    try {
		  String consoleLog = ParseUtils.parseServerConsoleLog( jsonBuf );
		  Utils.alertTitle(consoleLog,"Console Log", 8.0f, ServersActivity.this);
	    } catch(ParseException pe) {
		  Utils.alert("ServersActivity.AsyncTaskOSLogServer.onPostExecute: "+pe.getMessage( ), ServersActivity.this );
	    }
	    ServersActivity.this.progressDialogWaitStop.dismiss( );
	}
    }
}
