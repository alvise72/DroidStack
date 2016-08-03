package org.stackdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.View;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.comm.OSClient;
import org.stackdroid.comm.ServerException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.parse.ParseException;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.GetView;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Router;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.RouterView;
import org.stackdroid.utils.CustomProgressDialog;

import org.stackdroid.R;

public class NeutronRouterActivity extends Activity {

    private CustomProgressDialog progressDialogWaitStop     = null;
    private User 				 U 						    = null;
	private AlertDialog 		 alertDialogCreateRouter	= null;
	private EditText 			 routername				    = null;
	//private boolean				 mustRefresh				= false;

	/**
	 *
	 *
	 *
	 *
	 */
	protected class DeleteRouterListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			ImageButtonWithView bt = (ImageButtonWithView)v;
			final Router r = bt.getRouterView().getRouter();

			AlertDialog.Builder builder = new AlertDialog.Builder(NeutronRouterActivity.this);
			builder.setMessage( getString(R.string.AREYOUSURETODELETEROUTER));
			builder.setCancelable(false);

			DialogInterface.OnClickListener yesHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					NeutronRouterActivity.this.progressDialogWaitStop.show( );
					(new NeutronRouterActivity.AsyncTaskDeleteRouter()).execute( r.getID(), r.getName() );
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
	protected class ModifyRouterListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			Router V = (((GetView)v).getRouterView()).getRouter();
			Class<?> c = (Class<?>)RouterEditActivity.class;
			Intent I = new Intent( NeutronRouterActivity.this, c );
			I.putExtra( "ROUTERID", V.getID());
			I.putExtra( "ROUTERNAME", V.getName());
			startActivity( I );
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	protected class InfoRouterListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			//NeutronRouterActivity.this.progressDialogWaitStop.show( );
			Router V = (((GetView)v).getRouterView()).getRouter();
			TextView tv1 = new TextView(NeutronRouterActivity.this);
			tv1.setText(getString(R.string.ROUTERNAME));
			tv1.setTypeface( null, Typeface.BOLD );
			TextView tv2 = new TextView(NeutronRouterActivity.this);
			tv2.setText(V.getName());

			TextView tv3 = new TextView(NeutronRouterActivity.this);
			tv3.setText("Gateway");
			tv3.setTypeface( null, Typeface.BOLD );
			TextView tv4 = new TextView(NeutronRouterActivity.this);
			Network gw = V.getGateway();
			String routerInfo = "N/A (" + getString(R.string.INSUFFICIENTPRIVILEGES)+")";
			if(gw!=null) {
				routerInfo=gw.getName();
				if(gw.getSubNetworks() != null && gw.getSubNetworks().size()!=0 && gw.getSubNetworks().elementAt(0) != null)
					routerInfo += " ("+gw.getSubNetworks().elementAt(0).getAddress() + ")";
			}
			tv4.setText(routerInfo);


			Vector<TextView> interfaces = new Vector();



			ScrollView sv = new ScrollView(NeutronRouterActivity.this);
			LinearLayout.LayoutParams lp
					= new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			sv.setLayoutParams( lp );
			LinearLayout l = new LinearLayout(NeutronRouterActivity.this);
			l.setLayoutParams( lp );
			l.setOrientation( LinearLayout.VERTICAL );
			int paddingPixel = 8;
			float density = Utils.getDisplayDensity( NeutronRouterActivity.this );
			int paddingDp = (int)(paddingPixel * density);
			l.setPadding(paddingDp, 0, 0, 0);
			l.addView( tv1 );
			tv2.setPadding(2 * paddingDp, 0, 0, 0);
			l.addView( tv2 );
			tv4.setPadding(2 * paddingDp, 0, 0, 0);
			l.addView( tv3 );
			l.addView( tv4 );

			sv.addView(l);
			String name;
			if(V.getName().length()>=16)
				name = V.getName().substring(0,14) + "..";
			else
				name = V.getName();
			Utils.alertInfo(sv, "Router information", NeutronRouterActivity.this);
		}
	}

	/**
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
        return true;
    }

	/**
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

	/**
	 *
	 *
	 *
	 *
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView( R.layout.routerlist );

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
        	Utils.alert("NeutronRouterActivity.onCreate: "+re.getMessage(), this );
        	return;
        }
        if(selectedUser.length()!=0)
        	((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
		else
			((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE));

		//progressDialogWaitStop.show();
		//(new AsyncTaskOSListRouters()).execute();
    }

	/**
 	 *
	 *
 	 *
 	 *
 	 */
	@Override
	public void onResume( ) {
		super.onResume();
		progressDialogWaitStop.show();
		(new AsyncTaskOSListRouters()).execute();
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
    public void update(View v) {
    	progressDialogWaitStop.show();
        (new AsyncTaskOSListRouters()).execute();
    }

	/**
	 *
	 *
	 *
	 *
	 */
    private void refreshView( String jsonBufRouter, String jsonNet, String jsonSubnet ) throws ParseException {

		Vector<Network> netVec = Network.parse(jsonNet,jsonSubnet);
		HashMap<String, Network> mapID_to_Net = new HashMap();
		Iterator<Network> netIt = netVec.iterator();
		while(netIt.hasNext()) {
			Network thisNet = netIt.next();
			mapID_to_Net.put(thisNet.getID(), thisNet);
		}

		((LinearLayout) findViewById(R.id.routerLayout)).removeAllViews();
		Vector<Router> vr = Router.parseMultiple(jsonBufRouter, mapID_to_Net);
		Iterator<Router> rit = vr.iterator();
    	while(rit.hasNext()) {
    		Router r = rit.next();
    		((LinearLayout)findViewById( R.id.routerLayout) ).setGravity( Gravity.CENTER_HORIZONTAL );
    		View space = new View( this );
    		space.setMinimumHeight(10);
    		((LinearLayout)findViewById(R.id.routerLayout)).addView( space );
			((LinearLayout)findViewById(R.id.routerLayout)).addView( new RouterView( r,
																					 new NeutronRouterActivity.DeleteRouterListener(),
																					 new NeutronRouterActivity.ModifyRouterListener(),
																					 new NeutronRouterActivity.InfoRouterListener(),
																					 NeutronRouterActivity.this
																					)
																	);
		}
    }

	/**
	 *
	 *
	 *
	 *
	 */
	public void createRouter( View v ) {
		LayoutInflater li = LayoutInflater.from(this);

		View promptsView = li.inflate(R.layout.my_dialog_create_router, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(promptsView);

		alertDialogBuilder.setTitle(getString(R.string.CREATEROUTER) );

		alertDialogCreateRouter = alertDialogBuilder.create();

		final Button mButton = (Button)promptsView.findViewById(R.id.myButtonCreateRouter);
		final Button mButtonCancel = (Button)promptsView.findViewById(R.id.myButtonCreateRouterCancel);
		mButton.setOnClickListener(new CreateRouterClickListener());
		mButtonCancel.setOnClickListener(new CreateRouterCancelClickListener());
		routername = (EditText)promptsView.findViewById(R.id.routernameET);
		alertDialogCreateRouter.setCanceledOnTouchOutside(false);
		alertDialogCreateRouter.setCancelable(false);
		alertDialogCreateRouter.show();
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private class CreateRouterClickListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			String routerName = routername.getText().toString().trim();
			if(routerName.length()==0) {
				Utils.alert(getString(R.string.NOEMPTYNAME), NeutronRouterActivity.this);
				return;
			}

			if(alertDialogCreateRouter!=null) alertDialogCreateRouter.dismiss();
			NeutronRouterActivity.this.progressDialogWaitStop.show( );
			(new NeutronRouterActivity.AsyncTaskCreateRouter()).execute( routerName );
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private class CreateRouterCancelClickListener implements OnClickListener {
		@Override
		public void onClick( View v ) {
			if(alertDialogCreateRouter!=null) alertDialogCreateRouter.dismiss();
			return;
		}
	}





    //  ASYNC TASKS.....





	/**
	 *
	 *
	 *
	 *
	 */
    protected class AsyncTaskOSListRouters extends AsyncTask<Void, Void, Void> {
    	private String jsonBufNet, jsonBufSubnet, jsonBufRouter;
    	private String errorMessage;
    	private boolean hasError = false;
    	
    	@Override
    	protected Void doInBackground( Void... v )
    	{
    		OSClient osc = OSClient.getInstance(U);
    		//Log.d("NEUTRONROUTER", "doInBackground");
    	    try {
    	    	jsonBufRouter    = osc.requestRouters( );
				jsonBufNet		 = osc.listNetworks();
				jsonBufSubnet	 = osc.listSubNetworks( );
    	    } catch(ServerException se) {
    	    	errorMessage = ParseUtils.parseNeutronError( se.getMessage() );
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
     	    	Utils.alert(errorMessage, NeutronRouterActivity.this );
     	    	NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
     	    	return;
     	    }



    	    try {
				NeutronRouterActivity.this.refreshView( jsonBufRouter, jsonBufNet, jsonBufSubnet );
			} catch(ParseException pe) {
				Utils.alert("NeutronRouterActivity.AsyncTaskOSListRouters.onPostExecute: " + pe.getMessage( ),
							NeutronRouterActivity.this);
			}
			NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
    	}
    }

	/**
	 *
	 *
	 *
	 *
	 */
	private class AsyncTaskDeleteRouter extends AsyncTask<String,Void,Void> {
		private String errorMessage = "";
		private boolean hasError 	= false;
		private String routerName 	= "";
		private String routerID = "";
		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);
			routerName = v[1] ;
			routerID = v[0];
			try {
				osc.deleteRouter( routerID );
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
				String err = "";
				if(errorMessage.toLowerCase().contains("could not be found")==true) {
					err = errorMessage;
					err="Router " + routerName + " " + getString(R.string.COULDNOTBEFOUND);
				} else {
					err = errorMessage;
					err = err.replace( routerID, routerName);
					err = err.replace("still has ports", NeutronRouterActivity.this.getString(R.string.STILLHASPORTS));
				}
				Utils.alert( err, NeutronRouterActivity.this );
				(new AsyncTaskOSListRouters()).execute();
//				NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			(new AsyncTaskOSListRouters()).execute();
			//NeutronRouterActivity.this.progressDialogWaitStop.dismiss();
		}
	}

	/**
	 *
	 *
	 *
	 *
	 */
	private class AsyncTaskCreateRouter extends AsyncTask<String,Void,Void> {
		private String errorMessage = "";
		private boolean hasError = false;

		@Override
		protected Void doInBackground( String... v )
		{
			OSClient osc = OSClient.getInstance(U);

			try {
				osc.createRouter(v[0]);
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
				Utils.alert( errorMessage, NeutronRouterActivity.this );
				NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
				return;
			}
			(new AsyncTaskOSListRouters()).execute();
			//NeutronRouterActivity.this.progressDialogWaitStop.dismiss( );
			/*try {
				//NeutronRouterActivity.this.refreshView( jsonBufRouter );
			} catch(ParseException pe) {
				Utils.alert("NeutronRouterActivity.AsyncTaskDeleteRouter.onPostExecute: " + pe.getMessage( ),
							NeutronRouterActivity.this);
			}*/
		}
	}


}
