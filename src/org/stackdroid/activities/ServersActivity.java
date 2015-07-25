package org.stackdroid.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.NotFoundException;
import org.stackdroid.comm.ServerException;
import org.stackdroid.comm.ServiceUnAvailableOrInternalError;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.R;
import org.stackdroid.utils.CheckBoxWithView;
import org.stackdroid.utils.FloatingIP;
import org.stackdroid.utils.IPv4AddressKeyListener;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.ButtonWithView;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.KeyPair;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.OSImage;
import org.stackdroid.utils.Port;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.SimpleNumberKeyListener;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.Flavor;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.ServerView;
import org.stackdroid.utils.TextViewWithView;

import android.graphics.Typeface;
import android.os.AsyncTask;

import org.stackdroid.utils.CustomProgressDialog;


public class ServersActivity extends Activity {

	private CustomProgressDialog     progressDialogWaitStop    = null;
    private User 				     U 						   = null;
	private Server 				     server    				   = null;
	private ArrayAdapter<OSImage>    spinnerImagesArrayAdapter = null;
    private ArrayAdapter<Flavor>     spinnerFlavorArrayAdapter = null;
    private ArrayAdapter<KeyPair>    spinnerKeyPairArrayAdapter = null;

    private AlertDialog 		     alertDialogSelectImage    = null;
	private AlertDialog 		     alertDialogServerLaunch   = null;
	private AlertDialog 		     alertDialogSelectFIP      = null;	


	public  ArrayAdapter<FloatingIP> spinnerFIPArrayAdapter    = null;
	private Spinner                  fipSpinner                = null;
	private AlertDialog 			 manageInstanceDialog      = null;
    private AlertDialog 		     alertDialogServerInfo	   = null;
	private Hashtable<String,ServerView> mapID_to_ServerView   = null;

    private Vector<Flavor>     flavors;
    private Vector<KeyPair>    keypairs;
    private Vector<Network>    networks;
    private Vector<SecGroup>   secgroups;
    public  Vector<OSImage>    images;
    public  Vector<FloatingIP> fips					   = null;

    private Vector<NetworkView>						  netViewList				  = null;
    Hashtable<String, String> 						  netids 					  = null;
    HashSet<String> selectedSecgroups 		  = null;

    //__________________________________________________________________________________
    protected class ServerLaunchListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            if(alertDialogServerLaunch!=null)
                alertDialogServerLaunch.dismiss();
        }
    }

    //__________________________________________________________________________________
    protected class ServerCancelListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            if(alertDialogServerLaunch!=null)
                alertDialogServerLaunch.dismiss();
        }
    }

    //__________________________________________________________________________________
    protected class OkImageServerListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            if(alertDialogServerInfo!=null)
                alertDialogServerInfo.dismiss();
        }
    }

    //__________________________________________________________________________________
    protected class NetworkViewListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            CheckBoxWithView cb = (CheckBoxWithView)v;
            NetworkView nv = cb.getNetworkView();
            //Log.d("IMAGELAUNCH", "NetworkID=" + nv.getNetwork().getID());

            if(cb.isChecked() && netids.containsKey(nv.getNetwork().getID())) {
                cb.setChecked(false);
                Utils.alert(getString(R.string.ALREADYCHOOSENNET) + ": "+nv.getNetwork().getName(), ServersActivity.this);
                return;
            }

            if(cb.isChecked()) {
                netids.put(nv.getNetwork().getID(), "1");
            }
            if(!cb.isChecked()) {
                netids.remove(nv.getNetwork().getID());
            }

            if(cb.isChecked() && nv.getSubNetwork().getIPVersion().compareTo("4")==0) {
                nv.getNetworkIP().setEnabled(true);
                //netids.put(nv.getNetwork().getID(), "1");
                return;
            }
            if(!cb.isChecked() && nv.getSubNetwork().getIPVersion().compareTo("4")==0) {
                nv.getNetworkIP().setEnabled(false);
                //netids.remove(nv.getNetwork().getID());
                return;
            }
        }
    }

    //__________________________________________________________________________________
    protected class SecGroupListener implements OnClickListener {
        @Override
        public void onClick( View v ) {
            SecGroupView s = (SecGroupView)v;
            if(s.isChecked())
                selectedSecgroups.add( s.getSecGroup().getID() );
            else
                selectedSecgroups.remove( s.getSecGroup().getID() );
            return;
        }
    }

    /**
     *
     * @author dorigoa
     *
     */
	//protected class ResizeInstance implements OnClickListener {
	//	@Override
	//	public void onClick(View v) {
	//		server  = ((ButtonWithView)v).getServerView().getServer();
			//ServersActivity.this.progressDialogWaitStop.show();
            //(new AsyncTaskPauseInstance( )).execute( server.getID() );
            //ServersActivity.this.manageInstanceDialog.dismiss();
			
	//	}
		
	//}
	
	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class PauseInstance implements OnClickListener {
		@Override
		public void onClick(View v) {
			server  = ((ButtonWithView)v).getServerView().getServer();
			ServersActivity.this.progressDialogWaitStop.show();
            (new AsyncTaskPauseInstance( )).execute( server.getID() );
            ServersActivity.this.manageInstanceDialog.dismiss();
		}
		
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ResumeInstance implements OnClickListener {
		@Override
		public void onClick(View v) {
			server  = ((ButtonWithView)v).getServerView().getServer();
			ServersActivity.this.progressDialogWaitStop.show();
            (new AsyncTaskResumeInstance( )).execute( server.getID() );
            ServersActivity.this.manageInstanceDialog.dismiss();
		}
		
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class HardRebootInstance implements OnClickListener {
		@Override
		public void onClick(View v) {
			server  = ((ButtonWithView)v).getServerView().getServer();
			ServersActivity.this.progressDialogWaitStop.show();
            (new AsyncTaskHardReboot( )).execute( server.getID() );
            ServersActivity.this.manageInstanceDialog.dismiss();
		}
		
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class SoftRebootInstance implements OnClickListener {
		@Override
		public void onClick(View v) {
			server  = ((ButtonWithView)v).getServerView().getServer();
			ServersActivity.this.progressDialogWaitStop.show();
            (new AsyncTaskSoftReboot( )).execute( server.getID() );
            ServersActivity.this.manageInstanceDialog.dismiss();
		}
		
	}

	/**
	 * 
	 * @author dorigoa
	 *
	 */	
	protected class MakeInstanceSnapshot implements OnClickListener {

		@Override
		public void onClick(View v) {
			server  = ((ButtonWithView)v).getServerView().getServer();
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
	                	ServersActivity.this.progressDialogWaitStop.show();
	                    (new AsyncTaskCreateSnapshot( )).execute(server.getID(), snapname);
	                    ServersActivity.this.manageInstanceDialog.dismiss();
	                }
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
	         dia.show();
		    	
		     return;
		}

	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected class ChangeInstanceNameHandler implements OnClickListener {
		@Override
		public void onClick( View v ) {
			server = ( (ButtonWithView)v ).getServerView().getServer();
			
			final AlertDialog.Builder alert = new AlertDialog.Builder(ServersActivity.this);
	        alert.setMessage(getString(R.string.INPUTSNAPNAME));
	        final EditText input = new EditText(ServersActivity.this);
	        alert.setView(input);
	        alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog,int whichButton) {
	            	String newname = input.getText().toString();
	            	newname = newname.trim();
	                if(newname==null || newname.length()==0) {
	                	Utils.alert(getString(R.string.NOEMPTYNAME), ServersActivity.this);
	                } else {
	                	ServersActivity.this.progressDialogWaitStop.show();
	                    (new ServersActivity.AsyncTaskChangeInstanceName()).execute( server.getID(), newname);
	                    ServersActivity.this.manageInstanceDialog.dismiss();
	                }
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
	         dia.show();
		    	
		     return;
		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected class AddIPButtonHandler implements OnClickListener {
		@Override
		public void onClick( View v ) {
			ServersActivity.this.progressDialogWaitStop.show();
			server = ( (ButtonWithView)v ).getServerView().getServer();
			(new ServersActivity.AsyncTaskFIPList()).execute();
            if(ServersActivity.this.manageInstanceDialog!=null)
				ServersActivity.this.manageInstanceDialog.dismiss();
		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected void  pickAFloatingIP( ) {
		if(fips==null) {
			Utils.alert("Severe: FIPS is NULL !!", this);
			return;
		}
		if(fips.size()==0) {
			Utils.alert(getString(R.string.NOFIPTOASSOCIATE), this);
			return;
		}
		spinnerFIPArrayAdapter = new ArrayAdapter<FloatingIP>(ServersActivity.this, android.R.layout.simple_spinner_item,fips.subList(0,fips.size()) );
		spinnerFIPArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
		LayoutInflater li = LayoutInflater.from(this);

	    View promptsView = li.inflate(R.layout.my_dialog_associate_fip, null);

	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

	    alertDialogBuilder.setView(promptsView);

	    alertDialogBuilder.setTitle("Choose a Floating IP");
	    alertDialogSelectFIP = alertDialogBuilder.create();

	    fipSpinner = (Spinner) promptsView.findViewById(R.id.mySpinnerChooseIP);
	    fipSpinner.setAdapter(spinnerFIPArrayAdapter);
	    final Button mButton = (Button) promptsView.findViewById(R.id.myButton);
	    final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCancel);
		
	    mButton.setOnClickListener(new ServersActivity.ConfirmButtonHandlerForFIP());
	    mButtonCancel.setOnClickListener(new ServersActivity.CancelButtonHandlerForFIP());
	    alertDialogSelectFIP.setCanceledOnTouchOutside(false);
	    alertDialogSelectFIP.setCancelable(false);
	    alertDialogSelectFIP.show();
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected class ConfirmButtonHandlerForFIP implements OnClickListener {
		@Override
		public void onClick( View v ) {
			FloatingIP fip = (FloatingIP)fipSpinner.getSelectedItem();
			ServersActivity.this.alertDialogSelectFIP.dismiss();
			ServersActivity.this.progressDialogWaitStop.show();
			String ip = server.getPrivateIP().elementAt(0);
			(new ServersActivity.AsyncTaskFIPAssociate()).execute( fip.getID(), ip );
            ServersActivity.this.manageInstanceDialog.dismiss();
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	protected class CancelButtonHandlerForFIP implements OnClickListener {
		@Override
		public void onClick( View v ) {
			alertDialogSelectFIP.dismiss();
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 */
/*	protected class ConfirmButtonHandler implements OnClickListener {
		@Override
		public void onClick( View v ) {
			OSImage osi = (OSImage)imageSpinner.getSelectedItem();
			Class<?> c = (Class<?>)ImageLaunchActivity.class;
    		Intent I = new Intent( ServersActivity.this, c );
    		I.putExtra( "IMAGEID", osi.getID() );
    	    I.putExtra("IMAGENAME", osi.getName());
    	    ServersActivity.this.alertDialogSelectImage.dismiss();
    		startActivity(I);
		}
	} */

	/**
	 * 
	 * 
	 * 
	 * 
	 */
/*	protected class CancelButtonHandler implements OnClickListener {
		@Override
		public void onClick( View v ) {
			alertDialogSelectImage.dismiss();
		}
	} */

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	public void createInstance( View v ) {
		progressDialogWaitStop.show();
        ( new ServersActivity.AsyncTaskPrepareServerLaunch( ) ).execute();
	}

    private void displayDialogServerCreate( ) {
        LayoutInflater li = LayoutInflater.from(ServersActivity.this);

        View promptsView = li.inflate(R.layout.imagelaunch, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServersActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle(getString(R.string.CREATESERVER));
        alertDialogServerLaunch = alertDialogBuilder.create();

        //((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new ServersActivity.OkImageServerListener());
        spinnerFlavorArrayAdapter = new ArrayAdapter<Flavor>(ServersActivity.this, android.R.layout.simple_spinner_item,flavors.subList(0,flavors.size()) );
        spinnerFlavorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)promptsView.findViewById(R.id.spinnerFlavor)).setAdapter(spinnerFlavorArrayAdapter);

        spinnerImagesArrayAdapter = new ArrayAdapter<OSImage>(ServersActivity.this, android.R.layout.simple_spinner_item,images.subList(0,images.size()) );
        spinnerImagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)promptsView.findViewById(R.id.spinnerImages)).setAdapter(spinnerImagesArrayAdapter);

        spinnerKeyPairArrayAdapter = new ArrayAdapter<KeyPair>(ServersActivity.this, android.R.layout.simple_spinner_item,keypairs.subList(0,keypairs.size()) );
        spinnerKeyPairArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner)promptsView.findViewById(R.id.keypairSP)).setAdapter(spinnerKeyPairArrayAdapter);

        Iterator<Network> nit = networks.iterator();
        while(nit.hasNext()) {
            Network net = nit.next();
            if(U.getTenantID().compareTo( net.getTenantID() )!=0) {
                if(net.isShared()==false) {
                    continue;
                }
            }

            Iterator<SubNetwork> subnetsIT = net.getSubNetworks().iterator();
            while(subnetsIT.hasNext()) {
                SubNetwork sn = subnetsIT.next();
                NetworkView nv = new NetworkView( net, sn, new ServersActivity.NetworkViewListener(), IPv4AddressKeyListener.getInstance(), getString(R.string.SPECIFYOPTIP), ServersActivity.this );
                //networksL.addView( nv );
                //netViewList.add(nv);
                ((LinearLayout)promptsView.findViewById(R.id.networksLayer)).addView( nv );
                netViewList.add( nv );
            }
        }

        Iterator<SecGroup> sit = secgroups.iterator();
        while(sit.hasNext()) {
            SecGroupView sgv = new SecGroupView( sit.next(), new ServersActivity.SecGroupListener(),ServersActivity.this );
            sgv.setOnClickListener( new ServersActivity.SecGroupListener() );
            ((LinearLayout)promptsView.findViewById(R.id.secgroupsLayer)).addView(sgv);
            if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
        }

        ((Button)promptsView.findViewById(R.id.launchButton)).setOnClickListener( new ServersActivity.ServerLaunchListener());
        ((Button)promptsView.findViewById(R.id.cancelButton)).setOnClickListener( new ServersActivity.ServerLaunchListener());

        alertDialogServerLaunch.setCanceledOnTouchOutside(false);
        alertDialogServerLaunch.setCancelable(false);
        ServersActivity.this.progressDialogWaitStop.dismiss();
        alertDialogServerLaunch.show();
    }

	/**
	 * 
	 * 
	 * 
	 * 
	 */
/*	protected void pickAnImageToLaunch( ) {
		if(images.size()==0) {
			Utils.alert(getString(R.string.NOIMAGETOASSOCIATE), this);
			return;
		}
		spinnerImagesArrayAdapter = new ArrayAdapter<OSImage>(ServersActivity.this, android.R.layout.simple_spinner_item,images.subList(0,images.size()) );
		spinnerImagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		LayoutInflater li = LayoutInflater.from(this);

	    View promptsView = li.inflate(R.layout.my_dialog_create_instance, null);

	    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

	    alertDialogBuilder.setView(promptsView);

	    alertDialogBuilder.setTitle("Choose an image");
	    alertDialogSelectImage = alertDialogBuilder.create();

	    imageSpinner = (Spinner) promptsView.findViewById(R.id.mySpinnerChooseImage);
	    imageSpinner.setAdapter(spinnerImagesArrayAdapter);
	    final Button mButton = (Button) promptsView.findViewById(R.id.myButton);
	    final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCancel);
		mButton.setOnClickListener(new ServersActivity.ConfirmButtonHandler());
	    mButtonCancel.setOnClickListener(new ServersActivity.CancelButtonHandler());
	    alertDialogSelectImage.setCanceledOnTouchOutside(false);
	    alertDialogSelectImage.setCancelable(false);
	    alertDialogSelectImage.show();
	}*/
	
	/**
	 * 
	 * @author dorigoa
	 *
	 */
	protected class ServerDeleteClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
			// Delete the server
			final String serverid = ((ImageButtonWithView)v).getServerView( ).getServer().getID();

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
	protected class ServerManageClickListener implements OnClickListener {
		@Override
	    public void onClick( View v ) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(ServersActivity.this);
			alert.setMessage(getString(R.string.MANAGESERVERS));
			ScrollView sv = new ScrollView(ServersActivity.this);
			LinearLayout L = new LinearLayout( ServersActivity.this );
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            							  LinearLayout.LayoutParams.MATCH_PARENT,
                                            							  LinearLayout.LayoutParams.MATCH_PARENT);
			
			L.setLayoutParams( lp );
			L.setOrientation(LinearLayout.VERTICAL);
			final ButtonWithView changeName = new ButtonWithView( ServersActivity.this, ((ImageButtonWithView)v).getServerView() );
			final ButtonWithView makeSnap = new ButtonWithView( ServersActivity.this, ((ImageButtonWithView)v).getServerView()  );
			final ButtonWithView hardReboot = new ButtonWithView( ServersActivity.this,  ((ImageButtonWithView)v).getServerView()  );
			final ButtonWithView softReboot = new ButtonWithView( ServersActivity.this,  ((ImageButtonWithView)v).getServerView()  );
			
			final ButtonWithView pauseServer = new ButtonWithView( ServersActivity.this,  ((ImageButtonWithView)v).getServerView()  );
			final ButtonWithView resumeServer = new ButtonWithView( ServersActivity.this,  ((ImageButtonWithView)v).getServerView()  );
			//final Button resizeServer = new Button( ServersActivity.this );
			changeName.setText(getString(R.string.CHANGENAMESERVER));
			makeSnap.setText(getString(R.string.MAKESNAP));
			hardReboot.setText(getString(R.string.HARDREBOOT));
			softReboot.setText(getString(R.string.SOFTREBOOT));
			pauseServer.setText(getString(R.string.PAUSESERVER));
			resumeServer.setText(getString(R.string.RESUMESERVER));
			//resizeServer.setText(getString(R.string.RESIZESERVER));
			
			changeName.setOnClickListener( new ServersActivity.ChangeInstanceNameHandler( ) ) ;
			makeSnap.setOnClickListener( new ServersActivity.MakeInstanceSnapshot( ) );
			hardReboot.setOnClickListener( new ServersActivity.HardRebootInstance( ) );
			softReboot.setOnClickListener( new ServersActivity.SoftRebootInstance( ) );
			pauseServer.setOnClickListener( new ServersActivity.PauseInstance( ) );
			resumeServer.setOnClickListener( new ServersActivity.ResumeInstance( ) );
			L.addView(changeName);
			L.addView(makeSnap);
			L.addView(hardReboot);
			L.addView(softReboot);
			
			//L.addView(startUp);
			L.addView(pauseServer);
			L.addView(resumeServer);
			//L.addView(resizeServer);
			sv.addView( L );
			alert.setView(sv);
			
	        alert.setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                    
	                }
	         });
	         alert.setCancelable(false);
	         manageInstanceDialog = alert.create();
	         manageInstanceDialog.setCancelable(false);
	         manageInstanceDialog.setCanceledOnTouchOutside(false);
	         manageInstanceDialog.show( );
		    	
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
			if( v instanceof TextViewWithView )
				s = ((TextViewWithView)v).getServerView( ).getServer( );
			if( v instanceof ServerView )
				s = ((ServerView)v).getServer( );
			if( v instanceof LinearLayoutWithView )
				s = ((LinearLayoutWithView)v).getServerView( ).getServer();
			
			String[] secgrps = s.getSecurityGroupNames( );

            LayoutInflater li = LayoutInflater.from(ServersActivity.this);

            View promptsView = li.inflate(R.layout.my_dialog_server_info, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServersActivity.this);

            alertDialogBuilder.setView(promptsView);

            alertDialogBuilder.setTitle(getString(R.string.IMAGEINFO));
            alertDialogServerInfo = alertDialogBuilder.create();

            String fxip = "";
            if(s.getPrivateIP().isEmpty()) {
                fxip = getString(R.string.NONE);
            } else {

                for(int i = 0; i<s.getPrivateIP().size(); i++) {
                    String ip = s.getPrivateIP().elementAt(i);
                    fxip += ip + "\n";
                }
            }
            String fip = "";
            if(s.getPublicIP().isEmpty()) {
                fip = getString(R.string.NONE);
            } else {
                for(int i = 0; i<s.getPublicIP().size(); i++) {
                    fip += s.getPublicIP( ).elementAt(i) + "\n";
                }
            }
            if(fip.endsWith("\n"))
                fip = fip.substring(0, fip.length() - 2);
            if(fxip.endsWith("\n"))
                fxip = fxip.substring(0,fxip.length()-2);

            ((TextView)promptsView.findViewById(R.id.serverName)).setText(s.getName());
            ((TextView)promptsView.findViewById(R.id.serverID)).setText(s.getID());
            ((TextView)promptsView.findViewById(R.id.serverStatus)).setText(s.getStatus() + (s.getTask()!=null && s.getTask().length()!=0 && s.getTask().equalsIgnoreCase("null")==false ? " (" + s.getTask()+")" : ""));
            ((TextView)promptsView.findViewById(R.id.serverFlavor)).setText(s.getFlavor()!=null ? s.getFlavor().getFullInfo() : "N/A");
            ((TextView)promptsView.findViewById(R.id.serverIP)).setText( fxip );
            ((TextView)promptsView.findViewById(R.id.serverFIP)).setText( fip );
            ((TextView)promptsView.findViewById(R.id.serverKeyName)).setText( s.getKeyName( ).length() != 0 ? s.getKeyName( ) : getString(R.string.NONE) );
            ((TextView)promptsView.findViewById(R.id.secGroups)).setText( secgrps != null && secgrps.length!=0 ? Utils.join(s.getSecurityGroupNames(),", ") : getString(R.string.NONE) );
            ((TextView)promptsView.findViewById(R.id.hostedBy)).setText( s.getComputeNode2( ) != null ? s.getComputeNode2( ) : "N/A (" + getString(R.string.INSUFFICIENTPRIVILEGES)+")" );
            ((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new ServersActivity.OkImageServerListener());
            alertDialogServerInfo.setCanceledOnTouchOutside(false);
            alertDialogServerInfo.setCancelable(false);
            alertDialogServerInfo.show();
            /*
			TextView tv1 = new TextView(ServersActivity.this);
			tv1.setText(getString(R.string.INSTANCENAME));
			tv1.setTypeface( null, Typeface.BOLD );
			TextView tv2 = new TextView(ServersActivity.this);
			tv2.setText(s.getName());
			TextView tv3 = new TextView(ServersActivity.this);
			tv3.setText("Status:");
			tv3.setTypeface( null, Typeface.BOLD );
			TextView tv4 = new TextView(ServersActivity.this);
			tv4.setText(s.getStatus() + (s.getTask()!=null && s.getTask().length()!=0 && s.getTask().equalsIgnoreCase("null")==false ? " (" + s.getTask()+")" : "") );
			TextView tv5 = new TextView(ServersActivity.this);
			tv5.setText("Flavor: ");
			tv5.setTypeface( null, Typeface.BOLD );
			TextView tv6 = new TextView(ServersActivity.this);
			tv6.setText( s!=null && s.getFlavor( )!=null ? s.getFlavor().getFullInfo() : "N/A" );
			TextView tv7 = new TextView(ServersActivity.this);
			tv7.setText("Fixed IP(s):");
			tv7.setTypeface( null, Typeface.BOLD );
			TextView[] tv8_privip = null;
			if(s.getPrivateIP().isEmpty()) {
				tv8_privip = new TextView[1];
				tv8_privip[0] = new TextView(ServersActivity.this);
				tv8_privip[0].setText( getString(R.string.NONE) );
			} else {
				tv8_privip = new TextView[s.getPrivateIP().size()];
				for(int i = 0; i<s.getPrivateIP().size(); i++) {
					tv8_privip[i] = new TextView(ServersActivity.this);
					String ip = s.getPrivateIP().elementAt(i);
					tv8_privip[i].setText( ip );
				}
			}

			TextView tv9 = new TextView(ServersActivity.this);
			tv9.setText("Floating IP:");
			tv9.setTypeface( null, Typeface.BOLD );
			TextView[] tv10_pubip = null;
			if(s.getPublicIP().isEmpty()) {
				tv10_pubip =new TextView[1];
				tv10_pubip[0] = new TextView(ServersActivity.this);
				tv10_pubip[0].setText( getString(R.string.NONE) );
			} else {
				tv10_pubip = new TextView[s.getPublicIP().size()];
				for(int i = 0; i<s.getPublicIP().size(); i++) {
					tv10_pubip[i] = new TextView(ServersActivity.this);
					tv10_pubip[i].setText( s.getPublicIP( ).elementAt(i)  );
				}
			}
			TextView tv11 = new TextView( ServersActivity.this );
			tv11.setText("Key name:");
			tv11.setTypeface( null, Typeface.BOLD );
			TextView tv12 = new TextView( ServersActivity.this );
			tv12.setText( s.getKeyName( ).length() != 0 ? s.getKeyName( ) : getString(R.string.NONE) );
			TextView tv13 = new TextView( ServersActivity.this );
			tv13.setText("Security groups:");
			tv13.setTypeface( null, Typeface.BOLD );
			TextView tv14 = new TextView( ServersActivity.this );
			if(secgrps != null && secgrps.length!=0)
				tv14.setText( Utils.join(s.getSecurityGroupNames(),", ") );
			else
				tv14.setText( getString(R.string.NONE) );
			TextView tv15 = new TextView( ServersActivity.this );
			tv15.setText(getString(R.string.HOSTEDBY));
			tv15.setTypeface( null, Typeface.BOLD );
			TextView tv16 = new TextView( ServersActivity.this );
			tv16.setText( s.getComputeNode2( ) != null ? s.getComputeNode2( ) : "N/A (" + getString(R.string.INSUFFICIENTPRIVILEGES)+")");
	    
	    
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
			if(s.getName().length()>=30)
				name = s.getName().substring(0,27) + "...";
			else
				name = s.getName();
			Utils.alertInfo( sv, getString(R.string.INSTANCEINFO)+": \n"+name, ServersActivity.this );
			*/
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
			
			ButtonWithView btv = (ButtonWithView)v;
			server = btv.getServerView().getServer();
			
			final AlertDialog.Builder alert = new AlertDialog.Builder(ServersActivity.this);
	        alert.setMessage(getString(R.string.INPUTNUMLOGLINES));
	        final EditText input = new EditText(ServersActivity.this);
			input.setText("15");
			//input.setSelection(0, 2);
	        input.setKeyListener(SimpleNumberKeyListener.getInstance());
	        
	        alert.setView(input);
	        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
        //menu.add(GROUP, 1, order++, getString(R.string.MENUDELETEALL) ).setIcon(android.R.drawable.ic_menu_delete);
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

	return super.onOptionsItemSelected( item );
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    protected void deleteNovaInstance( String serverid ) {
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
        	Utils.alert("ServersActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
	      ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 

		mapID_to_ServerView = new Hashtable<String, ServerView>();
        //images = new Vector<OSImage>();

        netViewList = new Vector<NetworkView>( );
        netids = new Hashtable<String, String>();
        selectedSecgroups = new HashSet<String>();
        //progressDialogWaitStop.show();
        //(new AsyncTaskOSListServers()).execute();
        //(Toast.makeText(this, getString(R.string.TOUCHUSERTOVIEWINFO), Toast.LENGTH_LONG)).show();
    }

    /**
     *
     * @author dorigoa
     *
     */
	@Override
	public void onResume( ) {
		super.onResume( );
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
		mapID_to_ServerView.clear();
    }

	/**
	 * 
	 * @author dorigoa
	 *
	 */
    protected void refreshView( Vector<Server> servers, Vector<Flavor> flavors ) {
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
		
		Iterator<Flavor> flvIt = flavors.iterator( );
		while(flvIt.hasNext( ) ) {
			Flavor f = flvIt.next( );
		}

		
    		Flavor F = flavHash.get( s.getFlavorID( ) );
    		if( F != null)
    			s.setFlavor( F );
    		ServerView sv = new ServerView(s, new ServersActivity.ServerInfoClickListener(),
    										  new ServersActivity.ConsoleLogClickListener(),
    										  new ServersActivity.ServerDeleteClickListener(),
    										  new ServersActivity.AddIPButtonHandler(),
    										  new ServersActivity.ServerManageClickListener(),
    										  this);
    		((LinearLayout)findViewById( R.id.serverLayout) ).addView(sv);
			//sv.activateStatusUpdatePB();
			mapID_to_ServerView.put(sv.getServer().getID(), sv);
    		((LinearLayout)findViewById( R.id.serverLayout) ).setGravity(Gravity.CENTER_HORIZONTAL);
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.serverLayout)).addView(space);
			//(new ServersActivity.AsyncTaskServerStatusUpdate()).execute(sv.getServer().getID());
    	}
    }













    //  ASYNC TASKS.....
    
    //__________________________________________________________________________________
    protected class AsyncTaskPauseInstance extends AsyncTask<String, String, String> {
    	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
    	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		OSClient osc = OSClient.getInstance( U );
     		try {
     			osc.pauseServer(serverid);
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
    		Utils.alert(ServersActivity.this.getString(R.string.SERVERRPAUSED), ServersActivity.this);
    		(new AsyncTaskOSListServers()).execute( );
    	}
    }
    
    //__________________________________________________________________________________
    protected class AsyncTaskResumeInstance extends AsyncTask<String, String, String> {
    	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
    	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		OSClient osc = OSClient.getInstance( U );
     		try {
     			osc.resumeServer(serverid);
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
    		Utils.alert(ServersActivity.this.getString(R.string.SERVERRESUMED), ServersActivity.this);
    		(new AsyncTaskOSListServers()).execute( );
    	}
    }

    //__________________________________________________________________________________
    protected class AsyncTaskSoftReboot extends AsyncTask<String, String, String> {
    	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
    	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		OSClient osc = OSClient.getInstance( U );
     		try {
     			osc.softReboot(serverid);
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
    		Utils.alert(ServersActivity.this.getString(R.string.SERVERREBOOTED), ServersActivity.this);
    		(new AsyncTaskOSListServers()).execute( );
    	}
    }
    
    //__________________________________________________________________________________
    protected class AsyncTaskHardReboot extends AsyncTask<String, String, String> {
    	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
    	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		OSClient osc = OSClient.getInstance( U );
     		try {
     			osc.hardReboot(serverid);
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
    		Utils.alert(ServersActivity.this.getString(R.string.SERVERREBOOTED), ServersActivity.this);
    		(new AsyncTaskOSListServers()).execute( );
    	}
    	
    }

    //__________________________________________________________________________________
    protected class AsyncTaskChangeInstanceName extends AsyncTask<String, String, String> {
    	private  String   errorMessage     = null;
     	private  boolean  hasError         = false;
     	
    	protected String doInBackground( String... v ) 
     	{
     		String serverid = v[0];
     		String newname  = v[1];
		    OSClient osc = OSClient.getInstance( U );
		    
	     	
     		try {
     			osc.changeServerName(serverid, newname);
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
    		Utils.alert(ServersActivity.this.getString(R.string.SERVERNAMECHANGED), ServersActivity.this);
    		(new AsyncTaskOSListServers()).execute( );
    	}
    }
    
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
     			osc.createInstanceSnapshot(serverid, snapname);
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
		ServersActivity.this.progressDialogWaitStop.dismiss();
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
	    	  jsonBuf 	    = osc.requestServers( );
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
				Vector<Server> servers = Server.parse( jsonBuf );
				ServersActivity.this.refreshView( servers, Flavor.parse( jsonBufferFlavor ) );
			} catch(ParseException pe) {
				Utils.alert("ServersActivity.AsyncTaskOSListServers.onPostExecute: "+pe.getMessage( ), ServersActivity.this );
			}
			ServersActivity.this.progressDialogWaitStop.dismiss();
			//(new AsyncTaskOSListServers()).execute( );
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
     		if(hasError==true) {
     			Utils.alert( errorMessage, ServersActivity.this );
     			ServersActivity.this.progressDialogWaitStop.dismiss( );
     		}
     		else {
     			Utils.alert(getString(R.string.DELETEDINSTSANCES), ServersActivity.this );
     			(new AsyncTaskOSListServers()).execute( );
     		}
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
	    
	    String ID = ServersActivity.this.server.getID();
	    
	    try {
		  jsonBuf = osc.requestServerLog( ID, maxnumlines );
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
    
    /**
     * 
     * 
     * 
     * 
     * 
     */
    protected class AsyncTaskPrepareServerLaunch extends AsyncTask<Void, Void, Void>
    {
     	private  String   errorMessage   = null;
     	private  boolean  hasError       = false;
     	private  String   jsonImageBuf   = null;
        private  String   jsonFlavorBuf  = null;
        private  String   jsonKeyPairBuf = null;
        private  String   jsonNetworkBuf = null;
        private  String   jsonSubNetBuf  = null;
        private  String   jsonSecGrpsBuf = null;

        @Override
     	protected Void doInBackground(Void ... voids ) 
     	{
     		OSClient osc = OSClient.getInstance(U);

     		try {
                jsonImageBuf   = osc.requestImages();
                jsonFlavorBuf  = osc.requestFlavors();
                jsonKeyPairBuf = osc.requestKeypairs();
                jsonNetworkBuf = osc.requestNetworks();
                jsonSubNetBuf  = osc.requestSubNetworks();
                jsonSecGrpsBuf = osc.requestSecGroups();
     		} catch(ServiceUnAvailableOrInternalError se) {
     			errorMessage = ServersActivity.this.getString(R.string.SERVICEUNAVAILABLE);
     			hasError = true;
     		} catch (Exception e) {
     			errorMessage = e.getMessage( );
     			hasError = true;
     		} 
	    
	        return null;
     	}		
	
     	@Override
     	protected void onPostExecute( Void v ) {
     		super.onPostExecute(v);
	    
     		if(hasError) {
     			Utils.alert( errorMessage, ServersActivity.this );
     			ServersActivity.this.progressDialogWaitStop.dismiss( );
     			return;
     		}
	    
     		try {
     			ServersActivity.this.images    = OSImage.parse(jsonImageBuf);
                ServersActivity.this.flavors   = Flavor.parse(jsonFlavorBuf);
                ServersActivity.this.keypairs  = KeyPair.parse(jsonKeyPairBuf);
                ServersActivity.this.networks  = Network.parse(jsonNetworkBuf,jsonSubNetBuf);
                ServersActivity.this.secgroups = SecGroup.parse(jsonSecGrpsBuf);
     			//ServersActivity.this.pickAnImageToLaunch();
     		} catch(ParseException pe) {
     			Utils.alert("OSImagesActivity.AsyncTaskPrepareServerLaunch.onPostExecute: " + pe.getMessage( ),
     					ServersActivity.this);
     		}
            displayDialogServerCreate();


     	}
    }
    
    /**
	 * 
	 * 
	 *
	 */
    protected class AsyncTaskFIPList extends AsyncTask<Void, Void, Void>
    {
      private  String   errorMessage     = null;
  	  private  boolean  hasError         = false;
	  private  String   jsonBuf          = null;
	  
	  @Override
	  protected Void doInBackground( Void ... v ) 
	  {
	    OSClient osc = OSClient.getInstance(U);

	    try {
		  jsonBuf         = osc.requestFloatingIPs();
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
 		  Utils.alert( errorMessage, ServersActivity.this );
 		  ServersActivity.this.progressDialogWaitStop.dismiss( );
 		  return;
 	    }
	    
	    try {
	    	boolean only_unassigned = true;
	    	ServersActivity.this.fips = FloatingIP.parse(jsonBuf, only_unassigned );
	    } catch(ParseException pe) {
		    Utils.alert("ServersActivity.AsyncTaskFIPList.onPostExecute: "+pe.getMessage( ), ServersActivity.this );
		    ServersActivity.this.progressDialogWaitStop.dismiss();
		    return;
	    }
	    ServersActivity.this.progressDialogWaitStop.dismiss( );
	    ServersActivity.this.pickAFloatingIP();
	  }
    }
    
    /**
 	 * 
 	 * 
 	 *
 	 */
    protected class AsyncTaskFIPAssociate extends AsyncTask<String, Void, Void>
    {
       private  String   errorMessage     = null;
   	   private  boolean  hasError         = false;
       private  String   jsonPort		  = null;
       
       @Override
       protected Void doInBackground( String... ip_serverid ) 
       {
 		String floatingipid = ip_serverid[0];
 		String fixedip      = ip_serverid[1];
 		OSClient osc = OSClient.getInstance(U);
 	    
 	    try {
 		  
 		  jsonPort = osc.requestPortList( );
	      Vector<Port> vecP = Port.parse(jsonPort);
	    	
	      Iterator<Port> portIt = vecP.iterator();
	      String portID = "";
	      while(portIt.hasNext()) {
	    	  Port p = portIt.next();
	    	  if(p.getFixedIP().compareTo(fixedip)==0)
	       	  portID = p.getID();
	      }
	    	
	      if(portID.compareTo("")==0) {
	    	  return null;
	      }

	      osc.associateFloatingIP(floatingipid, portID);
	      
 		} catch(ServerException se) {
 		  errorMessage = ParseUtils.parseNeutronError(se.getMessage());
 		  hasError = true;
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
  		  Utils.alert( errorMessage, ServersActivity.this );
  		  ServersActivity.this.progressDialogWaitStop.dismiss( );
  		  return;
  	    }
  	    Utils.alert(getString(R.string.FIPASSOCIATED2), ServersActivity.this);
  	    (new AsyncTaskOSListServers()).execute();
  	    
 	  }
     }

	/**
	 *
	 *
	 *
	 */
	protected class AsyncTaskServerStatusUpdate extends AsyncTask<String, Void, Void>
	{
		private  String   errorMessage     = null;
		private  boolean  hasError         = false;
		private String    status           = "";

		@Override
		protected Void doInBackground( String... serverid )
		{

			OSClient osc = OSClient.getInstance(U);
			while(status.compareToIgnoreCase("ACTIVE")==0 || status.compareToIgnoreCase("ERROR")==0) {
				try {
					String jsonBuf = osc.requestInstanceDetails(serverid[0]);
					Server s = Server.parseSingle(jsonBuf);
					status = s.getStatus();
					mapID_to_ServerView.get(serverid[0]).getServer().setStatus(status);
					Thread.sleep(2000);
				} catch (Exception e) {
					//errorMessage = e.getMessage();
					//hasError = true;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void v ) {
			super.onPostExecute( v );

			/*if(hasError) {
				Utils.alert( errorMessage, ServersActivity.this );
				ServersActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}*/



		}
	}
}
