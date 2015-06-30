package org.stackdroid.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;

import org.stackdroid.R;
import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.ServerException;
import org.stackdroid.parse.ParseException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Router;
import org.stackdroid.utils.RouterPort;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.views.RouterPortView;

public class RouterEditActivity extends Activity {
	private User 				 U 						    = null;
	private CustomProgressDialog progressDialogWaitStop     = null;
	private String				 routerID					= null;

	/**
	 *
	 *
	 *
	 *
	 */
	protected class DeleteRouterPortListener implements View.OnClickListener {
		@Override
		public void onClick( View v ) {
			ImageButtonWithView bt = (ImageButtonWithView)v;
			final RouterPort rp = bt.getRouterPortView().getRouterPort();

			AlertDialog.Builder builder = new AlertDialog.Builder(RouterEditActivity.this);
			builder.setMessage( getString(R.string.AREYOUSURETODELETEINTERFACE));
			builder.setCancelable(false);

			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					RouterEditActivity.this.progressDialogWaitStop.show( );
					(new RouterEditActivity.AsyncTaskDeleteRouterInterface()).execute( routerID, rp.getSubnetID() );
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
  	@Override
  	public void onCreate(Bundle savedInstanceState) {
   	  super.onCreate(savedInstanceState);
	  setContentView( R.layout.editrouter );
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
		  Utils.alert("RouterEditActivity.onCreate: "+re.getMessage(), this );
		  return;
	  }
	  if(selectedUser.length()!=0)
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")");
	  else
		  ((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE));

		Bundle bundle = getIntent( ).getExtras();
		routerID = bundle.getString("ROUTERID");
		//String routerName = ;
		setTitle(getString(R.string.EDITROUTER) + " " + bundle.getString("ROUTERNAME"));
		progressDialogWaitStop.show();
		(new AsyncTaskGetRouterInfo()).execute(routerID);
    }

	/**
	 *
	 *
	 *
	 *
	 */
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
		super.onDestroy();
		progressDialogWaitStop.dismiss();
	}

  	/**
	 *
	 *
	 *
	 *
	 */
	private class AsyncTaskGetRouterInfo extends AsyncTask<String, Void, Void> {
		private String errorMessage = "";
		private boolean hasError = false;
		private String jsonBufRouterPorts = "";
		private String jsonBufRouterShow = "";
		//private String jsonBufNetworks = "";
		private String jsonBufNet = "";
		private String jsonBufSubnet = "";

		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);

			try {
				jsonBufRouterPorts 	= osc.requestRouterPorts(v[0]);
				jsonBufRouterShow  	= osc.requestRouterShow(v[0]);
				jsonBufNet		 	= osc.requestNetworks();
				jsonBufSubnet	 	= osc.requestSubNetworks( );
			} catch(ServerException se) {
				//Log.e("NEUTRONROUTER", se.getMessage());
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
			super.onPostExecute(v);

			if(hasError) {
				Utils.alert( errorMessage, RouterEditActivity.this );
				RouterEditActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			RouterEditActivity.this.putInfo( jsonBufRouterShow, jsonBufRouterPorts, jsonBufNet, jsonBufSubnet );
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private class AsyncTaskDeleteRouterInterface extends AsyncTask<String,Void,Void> {
		private String errorMessage = "";
		private boolean hasError 	= false;
		private String routerName 	= "";
		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);
			routerName = v[1] ;
			try {
				osc.deleteRouterInterface(v[0], v[1]);
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
			super.onPostExecute(v);

			if(hasError) {
				String err = "";
				if(errorMessage.toLowerCase().contains("could not be found")==true) {
					err = errorMessage;
					err="Router " + routerName + " " + getString(R.string.COULDNOTBEFOUND);
				} else err = errorMessage;
				Utils.alert( err, RouterEditActivity.this );
				RouterEditActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			( new AsyncTaskGetRouterInfo( ) ).execute(RouterEditActivity.this.routerID);
			//NeutronRouterActivity.this.progressDialogWaitStop.dismiss();
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private void putInfo(String jsonBufRouterShow, String jsonBufRouterPorts, String jsonBufNet, String jsonBufSubnet) {
		LinearLayout iL = (LinearLayout)findViewById(R.id.interfacesLayout);
		iL.removeAllViews();
		try {
			Vector<Network> netVec = Network.parse(jsonBufNet, jsonBufSubnet);
			HashMap<String, Network> mapID_to_Net = new HashMap();
			Iterator<Network> netIt = netVec.iterator();
			while (netIt.hasNext()) {
				Network thisNet = netIt.next();
				mapID_to_Net.put(thisNet.getID(), thisNet);
			}
			Router router = Router.parseSingle(jsonBufRouterShow, mapID_to_Net);
			String gwname = "";
			if(router.hasGateway()) {
				gwname = router.getGateway().getName();
				if(router.getGateway().getSubNetworks().size()!=0)
					gwname = gwname + " ("+router.getGateway().getSubNetworks().elementAt(0).getAddress() + ")";
			}
			((TextView)findViewById(R.id.GATEWAYNAMETEXT)).setText(gwname);
			((TextView)findViewById(R.id.GATEWAYNAMETEXT)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

			Vector<RouterPort> ports = RouterPort.parse(jsonBufRouterPorts);

			Iterator<RouterPort> portIterator = ports.iterator();
			while( portIterator.hasNext()) {
				RouterPort rp = portIterator.next();
				RouterPortView rpv = new RouterPortView( rp, new RouterEditActivity.DeleteRouterPortListener( ), this );
				iL.addView(rpv);
			}

		} catch (ParseException pe) {
			RouterEditActivity.this.progressDialogWaitStop.dismiss();
			Utils.alert(pe.getMessage(), this);
			return;
		}
		RouterEditActivity.this.progressDialogWaitStop.dismiss();
	}
}
