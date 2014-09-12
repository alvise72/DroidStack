package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.Menu;
import android.view.WindowManager;
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
import org.stackdroid.utils.ImageButtonNamed;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.ListSecGroupView;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;


public class SecGrpActivity extends Activity implements OnClickListener {

    private CustomProgressDialog progressDialogWaitStop = null;
    private User U = null;
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        //menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
        return true;
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
        
        return super.onOptionsItemSelected( item );
    }

    public void update( View v ) {
    	this.update( );
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void createSecGroup( View v ) {
    	//Utils.alert(getString(R.string.NOTIMPLEMENTED), this);
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getString(R.string.INPUTSSECNAME));
        final EditText input = new EditText(this);
        
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String secgrpName = input.getText().toString();
                        secgrpName = secgrpName.trim();
                        if(secgrpName.length()==0) {
                        	Utils.alert(getString(R.string.NOEMPTYNAME), SecGrpActivity.this);
                        	return;
                        }
                        SecGrpActivity.this.progressDialogWaitStop.show();
                        (new AsyncTaskCreateSecGroup()).execute(secgrpName, "");
                    }
                });
        alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        
                    }
                });
        alert.setCancelable(false);
        AlertDialog dia = alert.create();
        dia.setCancelable(false);
        dia.setCanceledOnTouchOutside(false);
        dia.show( );
        //alert.create().show();
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     */
    @Override
    public void onClick( View v ) {
	if(v instanceof ImageButtonNamed) {
		
		ImageButtonNamed bt = (ImageButtonNamed)v;
		
	    if( bt.getType() == ImageButtonNamed.BUTTON_DELETE_SECGRP ) {
	    	
	    	final String secgrpID = bt.getListSecGroupView().getSecGroup().getID();
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage( getString(R.string.AREYOUSURETODELETESECGRP));
			builder.setCancelable(false);
		    
			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					progressDialogWaitStop.show( );
			    	(new AsyncTaskDeleteSecGroups( )).execute( secgrpID );
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
	    	
	    }
	    
	    if( bt.getType() == ImageButtonNamed.BUTTON_EDIT_SECGRP ) {
	    	Class<?> c = (Class<?>)EditSecGroupActivity.class;
	        Intent I = new Intent( SecGrpActivity.this, c );
	        SecGroup sv = bt.getListSecGroupView().getSecGroup();
	        I.putExtra( "SECGRPNAME", sv.getName());
	        I.putExtra( "SECGRPDESC", sv.getDescription());
	        I.putExtra( "SECGRPID", sv.getID());
	        startActivity( I );
	    	return;
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
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView( R.layout.secgrplist );
	
	  progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
      progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
      progressDialogWaitStop.setCancelable(false);
      progressDialogWaitStop.setCanceledOnTouchOutside(false);
	  String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
	  try {
	    U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
	  } catch(Exception re) {
	    Utils.alert("ServersActivity.onCreate: " + re.getMessage(), this );
	    return;
	  }
	  if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
		
	  this.update( );
    }
    
    private void update( ) {
    	progressDialogWaitStop.show();
  	  	(new AsyncTaskSecGroups()).execute( );
    }
 
    /**
     * 
     * 
     * 
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
     * 
     * 
     * 
     */
    private void refreshView( Vector<SecGroup> secgrps ) {
    	((LinearLayout)findViewById(R.id.secgrpLayout)).removeAllViews();
    	if(secgrps.size()==0) {
    		Utils.alert(getString(R.string.NOSECGRPSAVAIL), this);	
    		return;
    	}
    	
    	Iterator<SecGroup> it = secgrps.iterator();
	
    	while(it.hasNext()) {
    		SecGroup s = it.next();
    		ListSecGroupView sgv = new ListSecGroupView(s, this);
    		((LinearLayout)findViewById( R.id.secgrpLayout) ).addView( sgv );
    		((LinearLayout)findViewById( R.id.secgrpLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.secgrpLayout)).addView( space );
    	}
    }













    //  ASYNC TASKS.....
    
  //__________________________________________________________________________________
    protected class AsyncTaskCreateSecGroup extends AsyncTask<String, String, String>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    //private  String   jsonBuf          = null;
	    //private  String   jsonBufferFlavor = null;
	
	    @Override
	    protected String doInBackground( String... v ) 
	    {
	      String secgrpName = v[0];
	      String desc       = v[1];
	      OSClient osc = OSClient.getInstance(U);
	      
	      try {
	    	  osc.createSecGroup( secgrpName, desc);
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
	    		Utils.alert( errorMessage, SecGrpActivity.this );
	    		SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    	SecGrpActivity.this.update( );
	  }
    }









    
    //__________________________________________________________________________________
    protected class AsyncTaskSecGroups extends AsyncTask<Void, String, String>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	    //private  String   jsonBufferFlavor = null;
	
	    @Override
	    protected String doInBackground( Void... v ) 
	    {
	    	
		      OSClient osc = OSClient.getInstance( U );

		      try {
		    	  jsonBuf = osc.requestSecGroups( );
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
	    		Utils.alert( errorMessage, SecGrpActivity.this );
	    		SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    
	    	try {
	    		Vector<SecGroup> secgrps = ParseUtils.parseSecGroups( jsonBuf );
	    		SecGrpActivity.this.refreshView( secgrps );
	  	 } catch(ParseException pe) {
	  		 Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: " + pe.getMessage( ), SecGrpActivity.this );
	  	 }
	    	SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	    }
    }
    
    
    
    
    
    
  //__________________________________________________________________________________
    protected class AsyncTaskDeleteSecGroups extends AsyncTask<String, String, String>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	
	    @Override
	    protected String doInBackground( String... v ) 
	    {
	      String secgrpID = v[0];
	      OSClient osc = OSClient.getInstance( U );

	      try {
	    	  osc.deleteSecGroup( secgrpID );
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
	    		Utils.alert( errorMessage, SecGrpActivity.this );
	    		SecGrpActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    	SecGrpActivity.this.update( );
	    }
    }	
}
