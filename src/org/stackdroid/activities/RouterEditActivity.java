package org.stackdroid.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import org.stackdroid.utils.OSImage;
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
	private String				 routerName					= null;
	private Vector<Network>		 externalNets			    = null;
	private Spinner 			 netsSpinner			    = null;
	private ArrayAdapter<Network> spinnerNetsArrayAdapter   = null;
	private AlertDialog 		 alertDialogSelectNetwork   = null;

	/**
	 *
	 *
	 *
	 *
	 */
	protected class ConfirmButtonHandler implements View.OnClickListener {
		@Override
		public void onClick( View v ) {
			Network net = (Network)netsSpinner.getSelectedItem();
			alertDialogSelectNetwork.dismiss();
			progressDialogWaitStop.show();
			(new RouterEditActivity.AsyncTaskSetRouterGateway()).execute( net.getID(), net.getName());
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	protected class CancelButtonHandler implements View.OnClickListener {
		@Override
		public void onClick( View v ) {
			alertDialogSelectNetwork.dismiss();
		}
	}

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
					(new RouterEditActivity.AsyncTaskDeleteRouterInterface()).execute( routerID, rp.getSubnetID(), rp.getFixedIP() );
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
		routerName = bundle.getString("ROUTERNAME");
		setTitle(getString(R.string.EDITROUTER) + " " + routerName );
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
	private class AsyncTaskGetExternalNets extends AsyncTask<Void, Void, Void> {
		private String errorMessage = "";
		private boolean hasError = false;
		private String jsonBufNet = "";

		@Override
		protected Void doInBackground( Void... v )
		{
			OSClient osc = OSClient.getInstance(U);

			try {
				jsonBufNet		 	= osc.requestExternalNetworks();
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
				Utils.alert( errorMessage, RouterEditActivity.this );
				RouterEditActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			try {
				externalNets = Network.parse(jsonBufNet, null);
			} catch(ParseException pe) {
				Utils.alert("Error parsing external networks", RouterEditActivity.this) ;
				progressDialogWaitStop.dismiss();
			}
			pickANetworkAsGateway( );
			//RouterEditActivity.this.putInfo( jsonBufRouterShow, jsonBufRouterPorts, jsonBufNet, jsonBufSubnet );
		}
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
		private String jsonBufNet = "";
		private String jsonBufSubnet = "";

		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);

			try {
				jsonBufRouterPorts 	= osc.requestRouterPorts( routerID );
				jsonBufRouterShow  	= osc.requestRouterShow( routerID );
				jsonBufNet		 	= osc.requestNetworks( );
				jsonBufSubnet	 	= osc.requestSubNetworks();
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
				Utils.alert( errorMessage, RouterEditActivity.this );
				RouterEditActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			RouterEditActivity.this.putInfo(jsonBufRouterShow, jsonBufRouterPorts, jsonBufNet, jsonBufSubnet);
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
		//private String routerName 	= "";
		private String subnetID = "";
		private String subnetIP = "";
		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);
			//routerName = v[1] ;
			subnetID = v[1];
			subnetIP = v[2];
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
				} else {
					err = errorMessage;
					//Log.d("ROUTEREDIT", "err="+err);
					err = err.replace( routerID, routerName );
					err = err.replace( subnetID, subnetIP );
				}
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
	private class AsyncTaskClearRouterGateway extends AsyncTask<Void,Void,Void> {
		private String errorMessage = "";
		private boolean hasError 	= false;
		private String routerName 	= "";
		@Override
		protected Void doInBackground( Void... v )
		{
			OSClient osc = OSClient.getInstance(U);
			//routerName = v[0] ;

			try {
				osc.clearRouterGateway(routerID);
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
			( new AsyncTaskGetRouterInfo( ) ).execute(RouterEditActivity.this.routerID );
		}
	}


	/**
	 *
	 *
	 *
	 *
	 */
	private class AsyncTaskSetRouterGateway extends AsyncTask<String,Void,Void> {
		private String errorMessage = "";
		private boolean hasError 	= false;
		private String routerName 	= "";
		private String networkID = "";
		private String networkName = "";
		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);
			networkID = v[0];
			networkName = v[1];
			try {
				osc.setRouterGateway( routerID, networkID );
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
				} else {
					err = errorMessage;
					err=err.replace( networkID, networkName);
					err = err.replace( "No more IP addresses available on network ", getString(R.string.NOMOREIPAVAIL));
				}
				Utils.alert( err, RouterEditActivity.this );
				RouterEditActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			( new AsyncTaskGetRouterInfo( ) ).execute(RouterEditActivity.this.routerID );
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
			if(router.hasGateway()) {
				((Button)findViewById(R.id.SETGATEWAY)).setEnabled(false);
				((Button)findViewById(R.id.CLEARGATEWAY)).setEnabled(true);
			} else {
				((Button)findViewById(R.id.SETGATEWAY)).setEnabled(true);
				((Button)findViewById(R.id.CLEARGATEWAY)).setEnabled(false);
			}
		} catch (ParseException pe) {
			RouterEditActivity.this.progressDialogWaitStop.dismiss();
			Utils.alert(pe.getMessage(), this);
			return;
		}

		RouterEditActivity.this.progressDialogWaitStop.dismiss();

	}

	/**
	 *
	 *
	 *
	 *
	 */
	public void clearGateway(View v) {
		progressDialogWaitStop.show();
		(new RouterEditActivity.AsyncTaskClearRouterGateway( )).execute();
	}

	/**
	 *
	 *
	 *
	 *
	 */
	public void setGateway(View v) {
		(new RouterEditActivity.AsyncTaskGetExternalNets()).execute();
	}

	/**
	 *
	 *
	 *
	 *
	 */
	protected void pickANetworkAsGateway( ) {
		progressDialogWaitStop.dismiss();
		spinnerNetsArrayAdapter = new ArrayAdapter<Network>(RouterEditActivity.this, android.R.layout.simple_spinner_item,externalNets.subList(0,externalNets.size()) );
		spinnerNetsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		LayoutInflater li = LayoutInflater.from(this);

		View promptsView = li.inflate(R.layout.my_dialog_create_instance, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder.setTitle("Choose a Network");
		alertDialogSelectNetwork = alertDialogBuilder.create();

		netsSpinner = (Spinner) promptsView.findViewById(R.id.mySpinnerChooseImage);
		netsSpinner.setAdapter(spinnerNetsArrayAdapter);
		final Button mButton = (Button) promptsView.findViewById(R.id.myButton);
		final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCancel);
		mButton.setOnClickListener(new RouterEditActivity.ConfirmButtonHandler());
		mButtonCancel.setOnClickListener(new RouterEditActivity.CancelButtonHandler());
		alertDialogSelectNetwork.setCanceledOnTouchOutside(false);
		alertDialogSelectNetwork.setCancelable(false);
		alertDialogSelectNetwork.show();

	}
}
