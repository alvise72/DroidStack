package org.stackdroid.activities;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.MenuItem;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.http.conn.util.InetAddressUtils;
import org.stackdroid.comm.NotAuthorizedException;
import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.NotFoundException;
import org.stackdroid.comm.ServiceUnAvailableOrInternalError;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.utils.CheckBoxWithView;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.R;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.Flavor;
import org.stackdroid.utils.IPv4AddressKeyListener;
import org.stackdroid.utils.KeyPair;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.SubnetUtils;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.OSImage;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.ServerView;

import android.graphics.Typeface;

public class OSImagesActivity extends Activity {
    
    private Vector<OSImage> 	 OS;
    private CustomProgressDialog progressDialogWaitStop 	= null;
	private AlertDialog 		 alertDialogImageInfo	    = null;
	private String 				 ID 						= null;
    private String 				 NAME 						= null;
    		User 				 U 							= null;
	View						 promptsViewLaunch			= null;
	private Hashtable<Pair<String,String>, String> selectedNetworks = null;
	private Vector<NetworkView>		 netViewList			= null;
	Hashtable<String, String> 		 netids 				= null;
	HashSet<String> selectedSecgroups 		   				= null;
	public  Vector<OSImage>          images;
	private AlertDialog 		     alertDialogServerInfo	   = null;
	private AlertDialog 		     alertDialogServerLaunch   = null;
	private Vector<Flavor>           flavors;
	private Vector<KeyPair>          keypairs;
	private Vector<Network>          networks;
	private Vector<SecGroup>         secgroups;
    private Hashtable<String,ServerView> mapID_to_ServerView   = null;

    private ArrayAdapter<OSImage>    spinnerImagesArrayAdapter = null;
    private ArrayAdapter<Flavor>     spinnerFlavorArrayAdapter = null;
    private ArrayAdapter<KeyPair>    spinnerKeyPairArrayAdapter = null;

    private OSImage imageToLaunch;
    private String name_InstanceToLaunch;//args[0], // instance name
    private String imageID_InstanceToLaunch; //args[1], // imageID
    private String keyname_InstanceToLaunch; // key_name
    private String flavorID_InstanceToLaunch; // flavorID
    private String count_InstanceToLaunch; // count
    private String secgrpID_InstanceToLaunch;

	//__________________________________________________________________________________
	protected class ServerLaunchListener implements OnClickListener {
		@Override
		public void onClick( View v ) {

			if(promptsViewLaunch!=null) {
                if( ((Spinner)promptsViewLaunch.findViewById(R.id.spinnerImages)).getCount() == 0) {
                    Utils.alert(getString(R.string.NOIMAGEAVAILABLEFIXPROBLEM),OSImagesActivity.this);
                    return;
                }
                if( ((Spinner)promptsViewLaunch.findViewById(R.id.spinnerFlavor)).getCount() == 0) {
                    Utils.alert(getString(R.string.NOFLAVORAVAILABLEFIXPROBLEM),OSImagesActivity.this);
                    return;
                }
				String serverName = ((EditText)promptsViewLaunch.findViewById(R.id.serverName)).getText().toString();
				String imageName  = ((OSImage)((Spinner)promptsViewLaunch.findViewById(R.id.spinnerImages)).getSelectedItem()).getID();
				String flavor	  = ((Flavor)((Spinner)promptsViewLaunch.findViewById(R.id.spinnerFlavor)).getSelectedItem()).getID();
				String number	  = ((EditText)promptsViewLaunch.findViewById(R.id.instanceNum)).getText().toString();
				String keypair;
                if( ((Spinner) promptsViewLaunch.findViewById(R.id.spinnerKeypair)).getCount() > 0)
                    keypair= ((KeyPair)((Spinner) promptsViewLaunch.findViewById(R.id.spinnerKeypair)).getSelectedItem()).getName();
                else
                    keypair = "";
				//Log.d("SERVERLAUNCH", "serverName="+serverName + " - imageName="+imageName+" - flavor="+flavor+" - number="+number+" - keypair="+keypair);
				String secgroups  = Utils.join(selectedSecgroups, ",");

				if(serverName.isEmpty()) {

					Utils.alert(getString(R.string.MUSTSETNAME), OSImagesActivity.this);
					return;
				}

				if(number.isEmpty()) {
					Utils.alert(getString(R.string.MUSTSETNUMSERVERS), OSImagesActivity.this);
					return;
				}



				int count = Integer.parseInt(number);

				Iterator<NetworkView> nvit = netViewList.iterator();
				//Hashtable<Pair<String,String>, String> selectedNetworks = new Hashtable<Pair<String,String>, String>();
				selectedNetworks.clear();

				while(nvit.hasNext()) {
					NetworkView nv = nvit.next();
					if(nv.isChecked()) {
						String netIP = "";
						if(nv.getSubNetwork().getIPVersion().compareTo("4") == 0) {
							netIP = nv.getNetworkIP().getText().toString().trim();
							if(netIP != null && netIP.length()!=0 && count>1) {
								Utils.alert(getString(R.string.NOCUSTOMIPWITHMOREVM), OSImagesActivity.this);
								//if(alertDialogServerLaunch!=null)
								//    alertDialogServerLaunch.dismiss();
								return;
							}
							if(netIP != null && netIP.length()!=0 && InetAddressUtils.isIPv4Address(netIP) == false) {
								Utils.alert(getString(R.string.INCORRECTIPFORMAT)+ ": " + netIP, OSImagesActivity.this);
								//if(alertDialogServerLaunch!=null)
								//    alertDialogServerLaunch.dismiss();
								return;
							}
							if(netIP != null && netIP.length()!=0) { // Let's check only if the user specified the custom IP
								SubnetUtils su = null;
								SubNetwork sn = nv.getSubNetwork();
								su = new SubnetUtils( sn.getAddress() ); // let's take only the first one
								SubnetUtils.SubnetInfo si = su.getInfo();
								if(!si.isInRange(netIP)) {
									Utils.alert("IP "+netIP+" "+getString(R.string.NOTINRANGE) + " "+sn.getAddress(), OSImagesActivity.this);
									//if(alertDialogServerLaunch!=null)
									//    alertDialogServerLaunch.dismiss();
									return;
								}
							}
						}

						Pair<String,String> net_subnet = new Pair<String,String>( nv.getNetwork().getID(), nv.getSubNetwork().getID() );
						if(netIP==null) netIP = "";
						selectedNetworks.put(net_subnet, netIP);
						//Log.d("SERVERLAUNCH", "Added network " + net_subnet.first + " - " + net_subnet.second + " - IP=" + netIP);

					}
				}
				if(selectedNetworks.isEmpty()) {
					Utils.alert(getString(R.string.MUSTSELECTNET), OSImagesActivity.this);
					return;
				}
				//Log.d("SERVERLAUNCH", Utils.join())
                name_InstanceToLaunch = serverName;
                imageID_InstanceToLaunch = imageName;
                keyname_InstanceToLaunch = keypair;
                flavorID_InstanceToLaunch = flavor;
                count_InstanceToLaunch = number;
                secgrpID_InstanceToLaunch = secgroups;
                progressDialogWaitStop.show();
                if(alertDialogServerLaunch!=null)
                    alertDialogServerLaunch.dismiss();
                (new OSImagesActivity.AsyncTaskLaunch()).execute();
			}
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

			if(cb.isChecked() && netids.containsKey(nv.getNetwork().getID())) {
				cb.setChecked(false);
				Utils.alert(getString(R.string.ALREADYCHOOSENNET) + ": "+nv.getNetwork().getName(), OSImagesActivity.this);
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
				return;
			}
			if(!cb.isChecked() && nv.getSubNetwork().getIPVersion().compareTo("4")==0) {
				nv.getNetworkIP().setEnabled(false);
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
				selectedSecgroups.remove(s.getSecGroup().getID());
			return;
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private void displayDialogServerCreate( ) {
		LayoutInflater li = LayoutInflater.from(OSImagesActivity.this);

		promptsViewLaunch = li.inflate(R.layout.my_dialog_server_launch, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OSImagesActivity.this);

		alertDialogBuilder.setView(promptsViewLaunch);

		alertDialogBuilder.setTitle(getString(R.string.CREATESERVER));
		alertDialogServerLaunch = alertDialogBuilder.create();

		spinnerFlavorArrayAdapter = new ArrayAdapter<Flavor>(OSImagesActivity.this, android.R.layout.simple_spinner_item,flavors.subList(0,flavors.size()) );
		spinnerFlavorArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)promptsViewLaunch.findViewById(R.id.spinnerFlavor)).setAdapter(spinnerFlavorArrayAdapter);

		spinnerImagesArrayAdapter = new ArrayAdapter<OSImage>(OSImagesActivity.this, android.R.layout.simple_spinner_item,images.subList(0,images.size()) );
		spinnerImagesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)promptsViewLaunch.findViewById(R.id.spinnerImages)).setAdapter(spinnerImagesArrayAdapter);

		spinnerKeyPairArrayAdapter = new ArrayAdapter<KeyPair>(OSImagesActivity.this, android.R.layout.simple_spinner_item,keypairs.subList(0,keypairs.size()) );
		spinnerKeyPairArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)promptsViewLaunch.findViewById(R.id.spinnerKeypair)).setAdapter(spinnerKeyPairArrayAdapter);

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
				NetworkView nv = new NetworkView( net, sn, new OSImagesActivity.NetworkViewListener(), IPv4AddressKeyListener.getInstance(), getString(R.string.SPECIFYOPTIP), OSImagesActivity.this );
				((LinearLayout)promptsViewLaunch.findViewById(R.id.networksLayer)).addView( nv );
				netViewList.add( nv );
			}
		}

		Iterator<SecGroup> sit = secgroups.iterator();
		while(sit.hasNext()) {
			SecGroupView sgv = new SecGroupView( sit.next(), new OSImagesActivity.SecGroupListener(), OSImagesActivity.this );
			sgv.setOnClickListener( new OSImagesActivity.SecGroupListener() );
			((LinearLayout)promptsViewLaunch.findViewById(R.id.secgroupsLayer)).addView(sgv);
			if(sgv.isChecked()) selectedSecgroups.add( sgv.getSecGroup( ).getID() );
		}

		((Button)promptsViewLaunch.findViewById(R.id.launchButton)).setOnClickListener( new OSImagesActivity.ServerLaunchListener() );
		((Button)promptsViewLaunch.findViewById(R.id.cancelButton)).setOnClickListener( new OSImagesActivity.ServerCancelListener() );

		alertDialogServerLaunch.setCanceledOnTouchOutside(false);
		alertDialogServerLaunch.setCancelable(false);
        OSImagesActivity.this.progressDialogWaitStop.dismiss();
		alertDialogServerLaunch.show();
	}

    /**
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

	public boolean onOptionsItemSelected( MenuItem item ) {
	 
        int id = item.getItemId();     
        
        if( id == Menu.FIRST-1 ) {
            Utils.alert( getString(R.string.NOTIMPLEMENTED) ,this );
            return true;
        }
       
        return super.onOptionsItemSelected( item );
    }

    public void update( View v ) {
    	progressDialogWaitStop.show();
		(new AsyncTaskOSListImages()).execute( );
    }

	//__________________________________________________________________________________
	protected class OkImageInfoListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			if(alertDialogImageInfo!=null)
				alertDialogImageInfo.dismiss();
		}
	}

    //__________________________________________________________________________________
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.osimagelist );
	
    	String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
    	try {
    		U = User.fromFileID( selectedUser, org.stackdroid.utils.Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
    		if(U==null) {
        		Utils.alert(getString(R.string.RECREATEUSERS), this);
        		return;
        	}
    	} catch(Exception re) {
    		Utils.alert("OSImagesActivity: "+re.getMessage(), this );
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
        selectedNetworks = new Hashtable<Pair<String,String>, String>();
        images = new Vector<OSImage>();

    	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage(getString(R.string.PLEASEWAITCONNECTING));
        progressDialogWaitStop.setCancelable(false);
        progressDialogWaitStop.setCanceledOnTouchOutside(false);
        //(Toast.makeText(this, getString(R.string.TOUCHUIMGTOVIEWINFO), Toast.LENGTH_LONG)).show();
        this.update( );
    }
    
    /**
    *
    *
    *
    *
    */
    private void update( ) {
    	progressDialogWaitStop.show();
    	(new AsyncTaskOSListImages()).execute( );
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
      //	Log.d("OSIMAGE.ONDESTROY", "OSIMAGE.ONDESTROY");
      progressDialogWaitStop.dismiss();
    }
   

    /**
     *
     *
     *
     *
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	setContentView(R.layout.osimagelist);
    	this.refreshView( );
    }

    protected class imageDeleteListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
    		ID = ((ImageButtonWithView)v).getOSImageView( ).getOSImage().getID();
    		AlertDialog.Builder builder = new AlertDialog.Builder(OSImagesActivity.this);
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
    		alert.setCancelable(false);
    		alert.setCanceledOnTouchOutside(false);
    		alert.show();
    	}
    }

    protected class imageLaunchListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {

            imageToLaunch = ((ImageButtonWithView)v).getOSImageView( ).getOSImage();

/*    		ID = ((ImageButtonWithView)v).getOSImageView( ).getOSImage().getID();
    		NAME = ((ImageButtonWithView)v).getOSImageView( ).getOSImage().getName();
    		Class<?> c = (Class<?>)ImageLaunchActivity.class;
    		Intent I = new Intent( OSImagesActivity.this, c );
    		I.putExtra( "IMAGEID", ID );
    	    I.putExtra("IMAGENAME", NAME);
    		startActivity( I );*/
            netids.clear();
            mapID_to_ServerView.clear();
            netViewList.clear();
            selectedSecgroups.clear();
            selectedNetworks.clear();

            progressDialogWaitStop.show();
            ( new OSImagesActivity.AsyncTaskPrepareServerLaunch( ) ).execute();
    	}
    }

    protected class imageInfoListener implements OnClickListener {
    	@Override
    	public void onClick( View v ) {
			OSImage osi = null;
			if(v instanceof OSImageView) {
				osi = ((OSImageView)v).getOSImage();
			}
			if(v instanceof TextViewWithView) {
				osi = ((TextViewWithView)v).getOSImageView().getOSImage();
			}
			if(v instanceof LinearLayoutWithView) {
				osi = ((LinearLayoutWithView)v).getOSImageView().getOSImage();
			}

			LayoutInflater li = LayoutInflater.from(OSImagesActivity.this);

			View promptsView = li.inflate(R.layout.my_dialog_image_info, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OSImagesActivity.this);

			alertDialogBuilder.setView(promptsView);

			alertDialogBuilder.setTitle(getString(R.string.IMAGEINFO));
			alertDialogImageInfo = alertDialogBuilder.create();

			((TextView)promptsView.findViewById(R.id.imageName)).setText(osi.getName());
			((TextView)promptsView.findViewById(R.id.imageStatus)).setText(osi.getStatus());
			((TextView)promptsView.findViewById(R.id.imageSize)).setText(osi.getSizeMB()+" MBytes");
			((TextView)promptsView.findViewById(R.id.imagePublic)).setText(osi.isPublic() ? getString(R.string.YES) : "No");
			((TextView)promptsView.findViewById(R.id.imageFormat)).setText(osi.getFormat());
			((TextView)promptsView.findViewById(R.id.imageID)).setText(osi.getID());
			((TextView)promptsView.findViewById(R.id.imageMinDisk)).setText(osi.getMinDISK()+" GBytes");
			((TextView)promptsView.findViewById(R.id.imageMinRAM)).setText(osi.getMinRAM()+" MBytes");
			((Button)promptsView.findViewById(R.id.buttonOk)).setOnClickListener( new OSImagesActivity.OkImageInfoListener());
			alertDialogImageInfo.setCanceledOnTouchOutside(false);
			alertDialogImageInfo.setCancelable(false);
			alertDialogImageInfo.show();
			/*
    	    TextView tv1 = new TextView(OSImagesActivity.this);
    	    tv1.setText(getString(R.string.IMAGENAME));
    	    tv1.setTypeface( null, Typeface.BOLD );
    	    TextView tv2 = new TextView(OSImagesActivity.this);
    	    tv2.setText(osi.getName());
    	    TextView tv3 = new TextView(OSImagesActivity.this);
    	    tv3.setText(getString(R.string.STATUS));
    	    tv3.setTypeface( null, Typeface.BOLD );
    	    TextView tv4 = new TextView(OSImagesActivity.this);
    	    tv4.setText(osi.getStatus());
    	    TextView tv5 = new TextView(OSImagesActivity.this);
    	    tv5.setText(getString(R.string.SIZE));
    	    tv5.setTypeface( null, Typeface.BOLD );
    	    TextView tv6 = new TextView(OSImagesActivity.this);
    	    tv6.setText(""+osi.getSize() + " (" + osi.getSize()/1048576 + " MB)");
    	    TextView tv7 = new TextView(OSImagesActivity.this);
    	    tv7.setText(getString(R.string.PUBLIC));
    	    tv7.setTypeface( null, Typeface.BOLD );
    	    TextView tv8 = new TextView(OSImagesActivity.this);
    	    tv8.setText(""+ (osi.isPublic() ? getString(R.string.YES) : getString(R.string.NO)));
    	    TextView tv9 = new TextView(OSImagesActivity.this);
    	    tv9.setText(getString(R.string.FORMAT));
    	    tv9.setTypeface( null, Typeface.BOLD );
    	    TextView tv10 = new TextView(OSImagesActivity.this);
    	    tv10.setText(osi.getFormat());
    	    TextView tv11 = new TextView( OSImagesActivity.this );
    	    tv11.setText("ID:");
    	    tv11.setTypeface( null, Typeface.BOLD );
    	    TextView tv12 = new TextView( OSImagesActivity.this );
    	    tv12.setText(osi.getID());
    	    TextView tv13 = new TextView( OSImagesActivity.this );
    	    tv13.setText(getString(R.string.MINDISK));
    	    tv13.setTypeface( null, Typeface.BOLD );
    	    TextView tv14 = new TextView( OSImagesActivity.this );
    	    tv14.setText(osi.getMinDISK( ) + " GB");
    	    TextView tv15 = new TextView( OSImagesActivity.this );
    	    tv15.setText(getString(R.string.MINRAM));
    	    tv15.setTypeface( null, Typeface.BOLD );
    	    TextView tv16 = new TextView( OSImagesActivity.this );
    	    tv16.setText(osi.getMinRAM( ) + " MB");
    	    ScrollView sv = new ScrollView(OSImagesActivity.this);
    	    LinearLayout.LayoutParams lp 
    		= new LinearLayout.LayoutParams(
    						LinearLayout.LayoutParams.MATCH_PARENT,
    						LinearLayout.LayoutParams.MATCH_PARENT);
    	    sv.setLayoutParams( lp );
    	    LinearLayout l = new LinearLayout(OSImagesActivity.this);
    	    l.setLayoutParams( lp );
    	    l.setOrientation( LinearLayout.VERTICAL );
    	    int paddingPixel = 8;
    	    float density = Utils.getDisplayDensity( OSImagesActivity.this );
    	    int paddingDp = (int)(paddingPixel * density);
    	    l.setPadding(paddingDp, 0, 0, 0);
    	    l.addView( tv1 );
    	    tv2.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv2 );
    	    l.addView( tv3 );
    	    tv4.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv4 );
    	    l.addView( tv5 );
    	    tv6.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv6 );
    	    l.addView( tv7 );
    	    tv8.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv8 );
    	    l.addView( tv9 );
    	    tv10.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv10 );
    	    l.addView( tv11 );
    	    tv12.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv12 );
    	    l.addView( tv13 );
    	    tv14.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv14 );
    	    l.addView( tv15 );
    	    tv16.setPadding(2*paddingDp, 0, 0, 0);
    	    l.addView( tv16 );
    	    sv.addView(l);
    	    String name;
    	    if(osi.getName().length()>=30)
    			name = osi.getName().substring(0,27) + "...";
    	    else
    			name = osi.getName();
    			*/
    	    //Utils.alertInfo( sv, getString(R.string.IMAGEINFO)+": \n" + name, OSImagesActivity.this );
    	}
    }

    private   void  deleteGlanceImage( String ID ) {
    	progressDialogWaitStop.show();
    	AsyncTaskOSDelete task = new AsyncTaskOSDelete();
    	task.execute( ID );
    }

    //__________________________________________________________________________________
    private void refreshView( ) {
    	if(OS.size()==0) {
    		Utils.alert(getString(R.string.NOIMAGEAAVAIL), this);
    		return;
    	}
    	Iterator<OSImage> sit = OS.iterator();
    	((LinearLayout)findViewById(R.id.osimagesLayout)).removeAllViews();
    	while( sit.hasNext( )) {
    		OSImage os = sit.next();
    		((LinearLayout)findViewById(R.id.osimagesLayout)).addView( new OSImageView(os, 
    																				   new OSImagesActivity.imageInfoListener(),
    																				   new OSImagesActivity.imageLaunchListener(),
    																				   new OSImagesActivity.imageDeleteListener(),
    																				   this) );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.osimagesLayout)).addView( space );
    		((LinearLayout)findViewById( R.id.osimagesLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
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
     *
     */
    protected class AsyncTaskOSDelete extends AsyncTask<String, Void, Void>
    {
      private  String   errorMessage  =  null;
	  private  boolean  hasError      =  false;
	  private  String   jsonBuf       = null;
	
	  protected Void doInBackground(String... u ) 
	  {
	    String imagetodel = u[0];
	    OSClient osc = OSClient.getInstance(U);
	    
	    try {
	    	osc.deleteGlanceImage( imagetodel );
	    	jsonBuf = osc.requestImages( );
	    } catch(NotFoundException nfe) {
	    	errorMessage = getString(R.string.NOTFOUND)+": " + nfe.getMessage();
	    	hasError = true;
	    } catch(NotAuthorizedException ne) {
	    	errorMessage = getString(R.string.NOTAUTHORIZED)+ ": " + ne.getMessage() + "\n\n" + getString(R.string.PLEASECHECKCREDSFORIMAGE);
	    	hasError = true;
	    } catch(ServiceUnAvailableOrInternalError se) {
	    	errorMessage = OSImagesActivity.this.getString(R.string.SERVICEUNAVAILABLE);
	    	hasError = true;
	    } catch (Exception e) {
	    	errorMessage = e.getMessage( );
	    	hasError = true;
		} 
	    return null;
	}

	/**
	 * 
	 * 
	*/	
	@Override
	protected void onPostExecute( Void v ) {
	    super.onPostExecute(v);
	    
 	    if(hasError) {
 	    	Utils.alert( errorMessage, OSImagesActivity.this );
 	    	OSImagesActivity.this.progressDialogWaitStop.dismiss( );
 	    	return;
 	    }
	    
	    try {
	    	OSImagesActivity.this.OS = OSImage.parse(jsonBuf);
	    	OSImagesActivity.this.refreshView( );
	    } catch(ParseException pe) {
	    	Utils.alert("OSImagesActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
	    				OSImagesActivity.this);
	    }

	    OSImagesActivity.this.update( );
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
     *
     *
     *
     *
     *
     *
     */
    protected class AsyncTaskOSListImages extends AsyncTask<Void, Void, Void>
    {
     	private  String   errorMessage  =  null;
     	private  boolean  hasError      =  false;
     	private  String   jsonBuf       = null;

     	@Override
     	protected Void doInBackground(Void ... voids ) 
     	{
     		OSClient osc = OSClient.getInstance(U);

     		try {
     			jsonBuf = osc.requestImages( );
     		} catch(ServiceUnAvailableOrInternalError se) {
     			errorMessage = OSImagesActivity.this.getString(R.string.SERVICEUNAVAILABLE);
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
     			Utils.alert( errorMessage, OSImagesActivity.this );
     			OSImagesActivity.this.progressDialogWaitStop.dismiss( );
     			return;
     		}
	    
     		try {
     			OSImagesActivity.this.OS = OSImage.parse(jsonBuf);
     			OSImagesActivity.this.refreshView( );
     		} catch(ParseException pe) {
     			Utils.alert("OSImagesActivity.AsyncTaskOSListImages.onPostExecute: " + pe.getMessage( ), 
     					    OSImagesActivity.this);
     		}
     		OSImagesActivity.this.progressDialogWaitStop.dismiss();
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
				//jsonImageBuf   = osc.requestImages();
				jsonFlavorBuf  = osc.requestFlavors();
				jsonKeyPairBuf = osc.requestKeypairs();
				jsonNetworkBuf = osc.requestNetworks();
				jsonSubNetBuf  = osc.requestSubNetworks();
				jsonSecGrpsBuf = osc.requestSecGroups();
			} catch(ServiceUnAvailableOrInternalError se) {
				errorMessage = OSImagesActivity.this.getString(R.string.SERVICEUNAVAILABLE);
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
				Utils.alert( errorMessage, OSImagesActivity.this );
				OSImagesActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}

			try {
                images.clear();
                images.add(imageToLaunch);
				//OSImagesActivity.this.images    = OSImage.parse(jsonImageBuf);
				OSImagesActivity.this.flavors   = Flavor.parse(jsonFlavorBuf);
				OSImagesActivity.this.keypairs  = KeyPair.parse(jsonKeyPairBuf);
				OSImagesActivity.this.networks  = Network.parse(jsonNetworkBuf, jsonSubNetBuf);
				OSImagesActivity.this.secgroups = SecGroup.parse(jsonSecGrpsBuf);
				//ServersActivity.this.pickAnImageToLaunch();
			} catch(ParseException pe) {
				Utils.alert("OSImagesActivity.AsyncTaskPrepareServerLaunch.onPostExecute: " + pe.getMessage( ),
						OSImagesActivity.this);
			}
			displayDialogServerCreate();

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
     *
     *
     *
     *
     *
     *
     */
    protected class AsyncTaskLaunch extends AsyncTask<Void, Void, Void>
    {
        private  String  errorMessage  = null;
        private  boolean hasError      = false;

        @Override
        protected Void doInBackground( Void... args )
        {
            OSClient osc = OSClient.getInstance( U );



            try {
                osc.createInstance( name_InstanceToLaunch,//args[0], // instance name
                                    imageID_InstanceToLaunch, //args[1], // imageID
                                    keyname_InstanceToLaunch, // key_name
                                    flavorID_InstanceToLaunch, // flavorID
                                    Integer.parseInt(count_InstanceToLaunch), // count
                                    secgrpID_InstanceToLaunch, // sec group ID
                                    OSImagesActivity.this.selectedNetworks );
            } catch(Exception e) {
                e.printStackTrace( );
                errorMessage = e.getMessage();
                hasError = true;
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute( Void v ) {
            super.onPostExecute( v );
            if(hasError) {
                Utils.alert( errorMessage, OSImagesActivity.this );
            } else {
                //(new OSImagesActivity.AsyncTaskOSListServers()).execute( );
                Class<?> c = (Class<?>)ServersActivity.class;
                Intent I = new Intent( OSImagesActivity.this, c );
                startActivity( I );
            }

            //OSImagesActivity.this.progressDialogWaitStop.dismiss();

        }
    }
}
