package org.stackdroid.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
//import android.widget.ScrollView;
import android.widget.TextView;
//import android.widget.Toast;
//import android.content.Intent;
//import android.graphics.Typeface;
import android.app.Activity;
import android.util.Log;
//import android.view.View.OnClickListener;
//import android.view.View;

import org.stackdroid.R;
import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.ServerException;
import org.stackdroid.parse.ParseException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Router;
import org.stackdroid.utils.RouterPort;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
/*
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
*/
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

//import org.stackdroid.views.UserView;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.views.RouterPortView;
//import org.stackdroid.utils.NotExistingFileException;
//import org.stackdroid.utils.TextViewWithView;
//import org.stackdroid.utils.ImageButtonWithView;

public class RouterEditActivity extends Activity {
	private User 				 U 						    = null;
	private CustomProgressDialog progressDialogWaitStop     = null;
	private String				 routerID					= null;
	private String				 routerName					= null;

	//__________________________________________________________________________________
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
		setTitle(getString(R.string.EDITROUTER) + " " + routerName);
  }
  
  	//__________________________________________________________________________________
 	@Override
 	public void onResume( ) {
    	super.onResume( );
		// Fare il neutron router-show
		// Fare il neutron router-port-list per avere le interfacce alle reti tenant
		progressDialogWaitStop.show();
		(new AsyncTaskGetRouterInfo()).execute(routerID);
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
		private String jsonBufNetworks = "";
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

	private void putInfo(String jsonBufRouterShow, String jsonBufRouterPorts, String jsonBufNet, String jsonBufSubnet) {
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
				gwname = "Gateway: " + router.getGateway().getName();
				if(router.getGateway().getSubNetworks().size()!=0)
					gwname = gwname + " ("+router.getGateway().getSubNetworks().elementAt(0).getAddress() + ")";
			}
			((TextView)findViewById(R.id.GATEWAYSHOWTEXT)).setText(gwname);

			Vector<RouterPort> ports = RouterPort.parse(jsonBufRouterPorts);
			LinearLayout iL = (LinearLayout)findViewById(R.id.interfacesLayout);
			Iterator<RouterPort> portIterator = ports.iterator();
			while( portIterator.hasNext()) {
				RouterPort rp = portIterator.next();
				RouterPortView rpv = new RouterPortView( rp, null, this );
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
