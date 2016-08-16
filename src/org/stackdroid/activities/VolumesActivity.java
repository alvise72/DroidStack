package org.stackdroid.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Typeface;
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
import org.stackdroid.utils.GetView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.OSImage;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.VolumeView;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.comm.commands.Command;

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
	private AlertDialog 		 alertDialogVolumeInfo	    = null;


	private String 				 currentVolToAttach			= null;
	//private String				 currentSrvToAttach			= null;
	public String currentVolToDetach;


	//__________________________________________________________________________________
	protected class OkImageServerListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			if(alertDialogVolumeInfo!=null)
				alertDialogVolumeInfo.dismiss();
		}
	}

    //__________________________________________________________________________________
    public boolean onCreateOptionsMenu( Menu menu ) {
        
        super.onCreateOptionsMenu( menu );
        
        int order = Menu.FIRST;
        int GROUP = 0;
                
        menu.add(GROUP, 0, order++, getString(R.string.MENUHELP)    ).setIcon(android.R.drawable.ic_menu_help);
        menu.add(GROUP, 1, order++, getString(R.string.MENUDELETEALLVOL) ).setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }
    
    public void update(View v) {
    	progressDialogWaitStop.show();
		(new AsyncTaskListVolumes()).execute( );
    }
    
    //__________________________________________________________________________________
    public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
        
        if( id == Menu.FIRST ) { 
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
        	if(U==null) {
        		Utils.alert(getString(R.string.RECREATEUSERS), this);
        		return;
        	}
        } catch(Exception re) {
        	Utils.alert("VolumesActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
        	((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
			((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
        //(Toast.makeText(this, getString(R.string.TOUCHUVOLTOVIEWINFO), Toast.LENGTH_LONG)).show();
        progressDialogWaitStop.show();
        (new AsyncTaskListVolumes()).execute( );
    }

	//__________________________________________________________________________________
    protected class InfoVolumeClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		
    		Volume V = (((GetView)v).getVolumeView()).getVolume();

			LayoutInflater li = LayoutInflater.from(VolumesActivity.this);

			View promptsView = li.inflate(R.layout.my_dialog_volume_info, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VolumesActivity.this);

			alertDialogBuilder.setView(promptsView);

			alertDialogBuilder.setTitle(getString(R.string.IMAGEINFO));
			alertDialogVolumeInfo = alertDialogBuilder.create();


			((TextView)promptsView.findViewById(R.id.volumeName)).setText( V.getName() );
			((TextView)promptsView.findViewById(R.id.volumeSize)).setText( "" + V.getSize() + " GB" );
			((TextView)promptsView.findViewById(R.id.volumeStatus)).setText( V.getStatus() );
			((TextView)promptsView.findViewById(R.id.volumeBootable)).setText( V.isBootable() ? getString(R.string.YES) : "No");
			((TextView)promptsView.findViewById(R.id.volumeReadonly)).setText( V.isReadOnly() ? getString(R.string.YES) : "No" );
			if(V.isAttached())
				((TextView)promptsView.findViewById(R.id.volumeAttachInfo)).setText( V.getAttachedServerName() + " ("+V.getAttachedDevice() + ")" );
			else
				((TextView)promptsView.findViewById(R.id.volumeAttachInfo)).setText( "-" );
			((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new VolumesActivity.OkImageServerListener());
			alertDialogVolumeInfo.setCanceledOnTouchOutside(false);
			alertDialogVolumeInfo.setCancelable(false);
			alertDialogVolumeInfo.show();
    	}
    }

	//__________________________________________________________________________________
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

	//__________________________________________________________________________________
    protected class CreateVolumeCancelClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		alertDialogCreateVolume.dismiss();
    		return;
    	}
    }

	//__________________________________________________________________________________
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

	//__________________________________________________________________________________
    protected class CancelButtonHandler implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		VolumesActivity.this.alertDialogSelectServer.dismiss( );
    	}
    }

	//__________________________________________________________________________________
    protected class AttachVolClickListener implements OnClickListener {
		@Override
    	public void onClick( View v ) {
    		ImageButtonWithView bt = (ImageButtonWithView)v;
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
    		mButton.setOnClickListener(new VolumesActivity.ConfirmButtonHandler());
    	    mButtonCancel.setOnClickListener(new VolumesActivity.CancelButtonHandler());
    	    alertDialogSelectServer.setCanceledOnTouchOutside(false);
    	    alertDialogSelectServer.setCancelable(false);
    	    alertDialogSelectServer.show();
    	}

    		
    }


	//__________________________________________________________________________________
    protected class DetachVolClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		ImageButtonWithView bt = (ImageButtonWithView)v;
    		final Volume V = bt.getVolumeView().getVolume();
    		if(!V.isAttached()) {
    			Utils.alert(VolumesActivity.this.getString(R.string.ALREADYDETACHED), VolumesActivity.this);
    			return;
    		}
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(VolumesActivity.this);
			builder.setMessage( getString(R.string.AREYOUSURETODETACHVOL));
			builder.setCancelable(false);
			    
			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				    VolumesActivity.this.progressDialogWaitStop.show( );
		    		(new VolumesActivity.AsyncTaskDetachVolume()).execute( V.getID(), V.getAttachedServerID() );
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

	//__________________________________________________________________________________
    protected class DeleteVolClickListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		final Volume V = ((ImageButtonWithView)v).getVolumeView().getVolume();
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

	//__________________________________________________________________________________
    public void createVolume( View v ) {
    	
    	LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.my_dialog_create_volume, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

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

	//__________________________________________________________________________________
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
    		//Log.d("VOLUMES", "V="+v.tostring());
    		VolumeView vv = new VolumeView(v,
    									   new VolumesActivity.AttachVolClickListener(), 
    									   new VolumesActivity.DetachVolClickListener(), 
    									   new VolumesActivity.DeleteVolClickListener(), 
    									   new VolumesActivity.InfoVolumeClickListener(),
    									   this);
    									   
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
     			jsonBufVols		= osc.listVolumes( );
     			//jsonBufServers	= osc.listServers( );
     			Command cmd = Command.commandFactory(Command.commandType.LISTSERVERS, U );
     			if(cmd!=null)
	    			cmd.execute();
	    		else {
					//Utils.alert("SEVERE ERROR: Command.commandFactory return null object!", OSImagesActivity.this);
					hasError = true;
					errorMessage = "SEVERE ERROR: Command.commandFactory has returned null object!";
     				return null;
	    		}
	    		jsonBufServers = cmd.getRESTResponse();
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
     			Vector<Volume> volumes = Volume.parse( jsonBufVols, jsonBufServers );
     			servers = Server.parse(jsonBufServers,null);
     			VolumesActivity.this.refreshView( volumes );
     		} catch(ParseException pe) {
     			
     			Utils.alert("VolumesActivity.AsyncTaskListVolumes.onPostExecute - Error parsing json: "+pe.getMessage( ), VolumesActivity.this );
     		} 
     		VolumesActivity.this.progressDialogWaitStop.dismiss( );
     	}
    }

	//__________________________________________________________________________________
    protected class AsyncTaskCreateVolume extends AsyncTask< String, Void, Void >
    {
    	private  String   errorMessage     = null;
    	private  boolean  hasError         = false;
    	
    	@Override
    	protected Void doInBackground( String ... v ) 
    	{
    		//OSClient osc = OSClient.getInstance( U );
    		try {
    			//osc.createVolume( v[0], Integer.parseInt( v[1] ) );
    			Command cmd = Command.commandFactory(Command.commandType.DELETEVOLUME, U );
   				if(cmd!=null) {
   					cmd.setup( v[0], Integer.parseInt( v[1] ) );
	    			cmd.execute();
	    		} else {
					hasError = true;
					errorMessage = "SEVERE ERROR: Command.commandFactory has returned null object!";
     				return null;
	    		}
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
    	}
   }

	//__________________________________________________________________________________
    protected class AsyncTaskDeleteVolume extends AsyncTask< String, Void, Void >
  	{
   		private  String   errorMessage     = null;
   		private  boolean  hasError         = false;
   	
   		@Override
   		protected Void doInBackground( String ... v )
   		{
   			//OSClient osc = OSClient.getInstance( U );
   			try {
   				//osc.deleteVolume( v[0] );
   				Command cmd = Command.commandFactory(Command.commandType.DELETEVOLUME, U );
   				if(cmd!=null) {
   					cmd.setup( v[0] );
	    			cmd.execute();
	    		} else {
					hasError = true;
					errorMessage = "SEVERE ERROR: Command.commandFactory has returned null object!";
     				return null;
	    		}
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
   		}
 	}

	//__________________________________________________________________________________
 	protected class AsyncTaskAttachVolume extends AsyncTask< String, Void, Void >
 	{
  		private  String   errorMessage     = null;
  		private  boolean  hasError         = false;
  	
  		@Override
  		protected Void doInBackground( String ... v )
  		{
  			//OSClient osc = OSClient.getInstance( U );
  			try {
  				//osc.volumeAttach( v[0], v[1] );
  				Command cmd = Command.commandFactory(Command.commandType.ATTACHVOLUME, U );
				if(cmd!=null) {
	    			cmd.setup( v[0], v[1] );
					cmd.execute();
	    		}
	    		else {
					hasError = true;
					errorMessage = "SEVERE ERROR: Command.commandFactory has returned null object!";
     				return null;
	    		}
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
  		}
 	}

	//__________________________________________________________________________________
	protected class AsyncTaskDetachVolume extends AsyncTask< String, Void, Void >
	{
 		private  String   errorMessage     = null;
 		private  boolean  hasError         = false;
 	
 		@Override
 		protected Void doInBackground( String ... v )
 		{
 			//OSClient osc = OSClient.getInstance( U );
 			try {
				//osc.volumeDetach( v[0], v[1] );
				Command cmd = Command.commandFactory(Command.commandType.DETACHVOLUME, U );
				if(cmd!=null)
	    			cmd.execute();
	    		else {
					hasError = true;
					errorMessage = "SEVERE ERROR: Command.commandFactory has returned null object!";
     				return null;
	    		}
 			} catch(Exception e) {
 				Log.d("VOLUMEDETACH", "ECCEZIONE: "+e.getMessage());
 				errorMessage = e.getMessage();;
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
 		}
	}
}
