package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.stackdroid.utils.Server;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;
import org.stackdroid.views.VolumeView;
import org.stackdroid.utils.ImageButtonNamed;

import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;

public class VolumesActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogCreateVolume    = null;
	private EditText 			 volname				    = null;
	private EditText 			 volsize				    = null;
	private Vector<Server> 		 servers         		    = null;
	private Spinner 			 serverSpinner				= null;
	private AlertDialog 		 alertDialogSelectServer    = null;
	private ArrayAdapter<Server> spinnerServersArrayAdapter = null;
	
	private String 				 currentVolToAttach			= null;
	private String				 currentSrvToAttach			= null;
    
    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
        //menu.add(GROUP, 2, order++, getString(R.string.MENUDELETEALL) ).setIcon(android.R.drawable.ic_menu_delete);
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
        	Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
	    }
        
	return super.onOptionsItemSelected( item );
    }
    
    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.volumelist );

    	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
        progressDialogWaitStop.setCancelable(false);
        progressDialogWaitStop.setCanceledOnTouchOutside(false);
        String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
        try {
        	U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
        } catch(Exception re) {
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
    
    /**
     * 
     * @author dorigoa
     * Is called when the user click on Confirm button of the Volume Creation window
     *
     */
    protected class CreateVolumeClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		String volumeName = volname.getText().toString().trim();
    		if(volumeName.length()==0) {
    			Utils.alert(getString(R.string.NOEMPTYNAME), VolumesActivity.this);
    			return;
    		}
    		int volumeSize;
    		try {
    			volumeSize = Integer.parseInt(volsize.getText().toString().trim());
    		} catch(NumberFormatException e) {
    			Utils.alert(getString(R.string.VOLSIZEMUSTBENUMERIC), VolumesActivity.this);
    			return;
    		}
    		if(volumeSize==0) {
    			Utils.alert(getString(R.string.NOZEROVOLSIZE), VolumesActivity.this);
    			return;
    		}
    		alertDialogCreateVolume.dismiss();
    		VolumesActivity.this.progressDialogWaitStop.show( );
    		(new VolumesActivity.AsyncTaskCreateVolume()).execute( volumeName, volsize.getText().toString().trim() );
    	}
    }

    /**
     * 
     * @author dorigoa
     * Is called when the user click on Cancel button of the Volume Creation window
     */
    protected class CreateVolumeCancelClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		alertDialogCreateVolume.dismiss();
    		return;
    	}
    }
    /**
     * 
     * @author dorigoa
     * Is called when the user click on Confirm button of the server selection windows (to attach the current volume)
     */
    protected class ConfirmButtonHandler implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		Server S = (Server)serverSpinner.getSelectedItem();
    		//Volume V = ((ImageButtonNamed)v).getVolumeView().getVolume();
    		alertDialogSelectServer.dismiss();
    		VolumesActivity.this.progressDialogWaitStop.show( );
    		(new VolumesActivity.AsyncTaskAttachVolume()).execute( currentVolToAttach, S.getID() );
    	}
    }

    /**
     * 
     * @author dorigoa
     * Is called when the user click on Cancel button of the server selection windows (to attach the current volume)
     */
    protected class CancelButtonHandler implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		VolumesActivity.this.alertDialogSelectServer.dismiss( );
    	}
    }
    
    /**
     * 
     * 
     * 
     * 
     */
    protected class AttachVolClickListener implements OnClickListener {
		@Override
    	public void onClick( View v ) {
    		ImageButtonNamed bt = (ImageButtonNamed)v;
    		Volume V = bt.getVolumeView().getVolume();
    		if(V.isAttached()) {
    			// DETACH
    			Utils.alert(VolumesActivity.this.getString(R.string.ALREADYATTACHED), VolumesActivity.this);
    			return;
    		}
    		if(servers.size()==0) {
    			Utils.alert(getString(R.string.NOSERVERTOATTACHVOL), VolumesActivity.this);
    			return;
    		}
    		
    		currentVolToAttach = V.getID();
    		
    		spinnerServersArrayAdapter = new ArrayAdapter<Server>(VolumesActivity.this, android.R.layout.simple_spinner_item,servers.subList(0,servers.size()) );
    		spinnerServersArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		
    		
    		LayoutInflater li = LayoutInflater.from(VolumesActivity.this);

    	    View promptsView = li.inflate(R.layout.my_dialog_layout_volattach, null);

    	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VolumesActivity.this);

    	    alertDialogBuilder.setView(promptsView);

    	    alertDialogBuilder.setTitle(getString(R.string.PICKASERVERTOATTACHVOL) + " " + V.getID());
    	    alertDialogSelectServer = alertDialogBuilder.create();

    	    serverSpinner = (Spinner) promptsView.findViewById(R.id.mySpinnerVol);
    	    serverSpinner.setAdapter(spinnerServersArrayAdapter);
    	    final Button mButton = (Button) promptsView.findViewById(R.id.myButtonVol);
    	    final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCancelVol);
    		//final Button mButtonCancel = (Button) promptsView.findViewById(R.id.myButtonCancel);
    	    mButton.setOnClickListener(new VolumesActivity.ConfirmButtonHandler());
    	    mButtonCancel.setOnClickListener(new VolumesActivity.CancelButtonHandler());
    	    alertDialogSelectServer.setCanceledOnTouchOutside(false);
    	    alertDialogSelectServer.setCancelable(false);
    	    alertDialogSelectServer.show();
    	}

    		
    }
    

    /**
     * 
     * 
     * 
     * 
     */
    protected class DetachVolClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		ImageButtonNamed bt = (ImageButtonNamed)v;
    		Volume V = bt.getVolumeView().getVolume();
    		if(!V.isAttached()) {
    			Utils.alert(VolumesActivity.this.getString(R.string.ALREADYDETACHED), VolumesActivity.this);
    			return;
    		}
    		VolumesActivity.this.progressDialogWaitStop.show( );
    		(new VolumesActivity.AsyncTaskDetachVolume()).execute( V.getID(), V.getAttachedServerID() );
    	}
    }

    /**
     * 
     * 
     * 
     * 
     */
    protected class DeleteVolClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		final Volume V = ((ImageButtonNamed)v).getVolumeView().getVolume();
    		//Utils.alert(VolumesActivity.this.getString(R.string.NOTIMPLEMENTED), VolumesActivity.this);
    		//Log.d("VOLUMES", "Vol="+V);
    		if(V.isAttached()) {
    			Utils.alert(VolumesActivity.this.getString(R.string.CANNOTDELETEATTACHEDVOL), VolumesActivity.this);
    			return;
    		}
    		AlertDialog.Builder builder = new AlertDialog.Builder(VolumesActivity.this);
			builder.setMessage( getString(R.string.AREYOUSURETODELETEVOL));
			builder.setCancelable(false);
			    
			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				    //deleteNovaInstance( serverid );
					VolumesActivity.this.progressDialogWaitStop.show( );
					(new VolumesActivity.AsyncTaskDeleteVolume()).execute( V.getID() );
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
    *
    *
    *
    */
    public void createVolume( View v ) {
    	
    	LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.my_dialog_create_volume, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle(getString(R.string.CREATEVOLUME) );

        alertDialogCreateVolume = alertDialogBuilder.create();

        final Button mButton = (Button)promptsView.findViewById(R.id.myButtonCreateVol);
        final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCreateVolCancel);
        mButton.setOnClickListener(new CreateVolumeClickListener());
        mButtonCancel.setOnClickListener(new CreateVolumeCancelClickListener());
        volname = (EditText)promptsView.findViewById(R.id.volumenameET);
        volsize = (EditText)promptsView.findViewById(R.id.volumesizeET);
        alertDialogCreateVolume.setCanceledOnTouchOutside(false);
        alertDialogCreateVolume.setCancelable(false);
        alertDialogCreateVolume.show();
        
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

    /**
     *
     *
     *
     *
     */
    private void refreshView( Vector<Volume> volumes ) {
    	((LinearLayout)findViewById(R.id.volumeLayout)).removeAllViews();
    	if(volumes.size()==0) {
    		Utils.alert(getString(R.string.NOVOLUMEAVAIL), this);	
    		return;
    	}
    	Iterator<Volume> vit = volumes.iterator();
    	while(vit.hasNext()) {
    		Volume v = vit.next();
    		//Log.d("VOLUMES", "V="+v.tostring());
    		VolumeView vv = new VolumeView(v,
    									   new VolumesActivity.AttachVolClickListener(), 
    									   new VolumesActivity.DetachVolClickListener(), 
    									   new VolumesActivity.DeleteVolClickListener(), 
    									   this);
    									   
    		((LinearLayout)findViewById( R.id.volumeLayout) ).addView( vv );
    		((LinearLayout)findViewById( R.id.volumeLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.volumeLayout)).addView( space );
    	}
    }


    //  ASYNC TASKS.....


    /**
     *
     *
     *
     *
     */
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
     			servers = ParseUtils.parseServers(jsonBufServers);
     			VolumesActivity.this.refreshView( volumes );
     		} catch(ParseException pe) {
     			
     			Utils.alert("VolumesActivity.AsyncTaskListVolumes.onPostExecute - Error parsing json: "+pe.getMessage( ), VolumesActivity.this );
     		} 
     		VolumesActivity.this.progressDialogWaitStop.dismiss( );
     	}
    }

    /**
    *
    *
    *
    *
    */
   protected class AsyncTaskCreateVolume extends AsyncTask< String, Void, Void >
   {
    	private  String   errorMessage     = null;
    	private  boolean  hasError         = false;
    	
    	@Override
    	protected Void doInBackground( String ... v ) 
    	{
    		OSClient osc = OSClient.getInstance( U );
    		try {
    			osc.createVolume( v[0], Integer.parseInt( v[1] ) );
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
    		
    		Utils.alert(VolumesActivity.this.getString(R.string.VOLUMECREATED), VolumesActivity.this );
    		(new AsyncTaskListVolumes()).execute( );
    		//VolumesActivity.this.progressDialogWaitStop.dismiss( );
    	}
   }
   

   /**
   *
   *
   *
   *
   */
  protected class AsyncTaskDeleteVolume extends AsyncTask< String, Void, Void >
  {
   	private  String   errorMessage     = null;
   	private  boolean  hasError         = false;
   	
   	@Override
   	protected Void doInBackground( String ... v ) 
   	{
   		OSClient osc = OSClient.getInstance( U );
   		try {
   			osc.deleteVolume( v[0] );
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
   		
   		Utils.alert(VolumesActivity.this.getString(R.string.VOLUMEDELETED), VolumesActivity.this );
   		(new AsyncTaskListVolumes()).execute( );
   		//VolumesActivity.this.progressDialogWaitStop.dismiss( );
   	}
  }
  
  

  /**
  *
  *
  *
  *
  */
 protected class AsyncTaskAttachVolume extends AsyncTask< String, Void, Void >
 {
  	private  String   errorMessage     = null;
  	private  boolean  hasError         = false;
  	
  	@Override
  	protected Void doInBackground( String ... v ) 
  	{
  		OSClient osc = OSClient.getInstance( U );
  		try {
  			osc.volumeAttach( v[0], v[1] );
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
  		
  		Utils.alert(VolumesActivity.this.getString(R.string.VOLUMEATTACHED), VolumesActivity.this );
  		(new AsyncTaskListVolumes()).execute( );
  		//VolumesActivity.this.progressDialogWaitStop.dismiss( );
  	}
 }
 

 

 /**
 *
 *
 *
 *
 */
protected class AsyncTaskDetachVolume extends AsyncTask< String, Void, Void >
{
 	private  String   errorMessage     = null;
 	private  boolean  hasError         = false;
 	
 	@Override
 	protected Void doInBackground( String ... v ) 
 	{
 		OSClient osc = OSClient.getInstance( U );
 		try {
 			osc.volumeDetach( v[0], v[1] );
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
 		
 		Utils.alert(VolumesActivity.this.getString(R.string.VOLUMEDETACHED), VolumesActivity.this );
 		(new AsyncTaskListVolumes()).execute( );
 		//VolumesActivity.this.progressDialogWaitStop.dismiss( );
 	}
}
}
