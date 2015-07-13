package org.stackdroid.activities;

import java.util.Iterator;
import java.util.Vector;

import org.stackdroid.R;
import org.stackdroid.activities.ServersActivity.AsyncTaskDeleteServer;
import org.stackdroid.activities.ServersActivity.AsyncTaskOSListServers;
import org.stackdroid.comm.OSClient;
import org.stackdroid.parse.ParseException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.CustomProgressDialog;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.SimpleSecGroupRule;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.ServerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SecGroupEditActivity extends Activity implements OnItemSelectedListener {

    private String secgrpID   						    = null;
	private User   U          						    = null;
    private ArrayAdapter<String> spinnerRulesAdapter    = null;
    private Spinner ruleSpinner 						= null;
    private Vector<String> predefinedRules 				= null;
    private AlertDialog alertDialogSelectRule 			= null;
    private CustomProgressDialog progressDialogWaitStop = null;
    private EditText fromPort 							= null;
    private EditText toPort 							= null;
    private EditText CIDR 								= null;
    private Spinner PROTO 								= null;
	private ArrayAdapter<String> spinnerProtoAdapter 	= null;
	private Vector<String> protocols 					= null;

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
        //menu.add(GROUP, 1, order++, getString(R.string.MENUUPDATE) ).setIcon(R.drawable.ic_menu_refresh);
        //menu.add(GROUP, 2, order++, getString(R.string.MENUDELETEALL) ).setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }

	/**
	 *
	 * @author dorigoa
	 *
	 */
	public void update(View v) {
    	progressDialogWaitStop.setCancelable(false);
		progressDialogWaitStop.setCanceledOnTouchOutside(false);
		progressDialogWaitStop.show( );
    	(new AsyncTaskListRules()).execute( secgrpID );
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
     * 
     * 
     * 
     * 
     * 
     */    
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String selected  = (String)parent.getSelectedItem();
		
		if(selected.compareTo("Custom")==0) {
			fromPort.setEnabled(true);
			toPort.setEnabled(true);
			PROTO.setEnabled(true);
		} else {
			fromPort.setEnabled(false);
			toPort.setEnabled(false);	
			PROTO.setEnabled(false);		
		}
		
		if(selected.compareTo("SSH")==0) {
			fromPort.setText("22");
			toPort.setText("22");
			PROTO.setSelection(0);
		}
		
		if(selected.compareTo("HTTP(80)")==0) {
			fromPort.setText("80");
			toPort.setText("80");
			PROTO.setSelection(0);
		}
		
		if(selected.compareTo("HTTP(8080)")==0) {
			fromPort.setText("8080");
			toPort.setText("8080");
			PROTO.setSelection(0);
		}
		
		if(selected.compareTo("HTTPS(443)")==0) {
			fromPort.setText("443");
			toPort.setText("443");
			PROTO.setSelection(0);
		}
		
		if(selected.compareTo("HTTPS(8443)")==0) {
			fromPort.setText("8443");
			toPort.setText("8443");
			PROTO.setSelection(0);
		}
		
		if(selected.compareTo("FTP")==0) {
			fromPort.setText("21");
			toPort.setText("21");
			PROTO.setSelection(0);
		}

		if(selected.compareTo("PING")==0) {
			fromPort.setText("-1");
			toPort.setText("-1");
			PROTO.setSelection(2);
		}
		
		if(selected.compareTo("Custom")==0) {
			fromPort.setText("");
			toPort.setText("");
			PROTO.setSelection(0);
		}
	}
	
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */    
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}	
	
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */    
    public SecGroupEditActivity( ) {
    	predefinedRules = new Vector<String>( );
    	predefinedRules.add("SSH");
    	predefinedRules.add("HTTP(80)");
    	predefinedRules.add("HTTP(8080)");
    	predefinedRules.add("HTTPS(443)");
    	predefinedRules.add("HTTPS(8443)");
    	predefinedRules.add("FTP");
    	predefinedRules.add("PING");
    	predefinedRules.add("Custom");
    	protocols = new Vector<String>( );
    	protocols.add("TCP");
    	protocols.add("UDP");
    	protocols.add("ICMP");
    }
    
    /**
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
        setContentView( R.layout.editsecgroup );
        

        secgrpID   = this.getIntent().getStringExtra("SECGRPID");
		String secgrpName = this.getIntent().getStringExtra("SECGRPNAME");
        setTitle(getString(R.string.EDITSECGROUP) + " " + secgrpName);
		String secgrpDesc = this.getIntent().getStringExtra("SECGRPDESC");
        //((EditText)findViewById(R.id.secgrpName)).setText(secgrpName);
        //((EditText)findViewById(R.id.secgrpDesc)).setText(secgrpDesc);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
    	try {
    		U = User.fromFileID( selectedUser, Configuration.getInstance().getValue("FILESDIR",Defaults.DEFAULTFILESDIR) );
    		if(U==null) {
        		Utils.alert(getString(R.string.RECREATEUSERS), this);
        		return;
        	}
    	} catch(Exception re) {
    		Utils.alert("OSImagesActivity.onCreate: "+re.getMessage(), this );
    		return;
    	} 
	
    	if(selectedUser.length()!=0)
    		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
    	else
    		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
    	
    	progressDialogWaitStop = new CustomProgressDialog( this, ProgressDialog.STYLE_SPINNER );
        progressDialogWaitStop.setMessage( getString(R.string.PLEASEWAITCONNECTING) );
        progressDialogWaitStop.setCancelable(false);
		progressDialogWaitStop.setCanceledOnTouchOutside(false);
    	this.progressDialogWaitStop.show( );
    	(new AsyncTaskListRules()).execute( secgrpID );
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void addRule( View v ) { 
    	
    	spinnerRulesAdapter = new ArrayAdapter<String>(SecGroupEditActivity.this, android.R.layout.simple_spinner_item, predefinedRules.subList(0,predefinedRules.size()) );
    	spinnerRulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	spinnerProtoAdapter = new ArrayAdapter<String>(SecGroupEditActivity.this, android.R.layout.simple_spinner_item, protocols.subList(0,protocols.size()) );
    	spinnerProtoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	
    	LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.my_dialog_layout_addrule, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle(getString(R.string.ADDRULE) );

        alertDialogSelectRule = alertDialogBuilder.create();

        ruleSpinner = (Spinner)promptsView.findViewById(R.id.mySpinner);
        ruleSpinner.setAdapter(spinnerRulesAdapter);
        
        ( (Spinner)promptsView.findViewById(R.id.protoSpinner) ).setAdapter(spinnerProtoAdapter);
        
        ((Button)promptsView.findViewById(R.id.confirmRuleAdd)).setOnClickListener( new SecGroupEditActivity.ConfirmRuleAddClickListener( ) );
        ((Button)promptsView.findViewById(R.id.cancelRuleAdd)).setOnClickListener( new SecGroupEditActivity.CancelRuleAddClickListener( ) );
        
        fromPort = (EditText)promptsView.findViewById(R.id.fromPortET);
        toPort = (EditText)promptsView.findViewById(R.id.toPortET);
        PROTO = (Spinner)promptsView.findViewById(R.id.protoSpinner);
        CIDR = (EditText)promptsView.findViewById(R.id.cidrET);

        ruleSpinner.setOnItemSelectedListener((OnItemSelectedListener)this);
        
        alertDialogSelectRule.setCanceledOnTouchOutside(false);
        alertDialogSelectRule.setCancelable(false);
        alertDialogSelectRule.show();
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    protected class ConfirmRuleAddClickListener implements OnClickListener {
    	@Override
        public void onClick(View v) { 
    		String S = (String)ruleSpinner.getSelectedItem();
            
    		alertDialogSelectRule.dismiss();
            progressDialogWaitStop.show();
            String fromPortS = fromPort.getText().toString();
            String toPortS = toPort.getText().toString();
            String cidrS = CIDR.getText().toString();
            String protoS = PROTO.getSelectedItem().toString().toLowerCase();
            ( new AsyncTaskCreateRule( ) ).execute( secgrpID, fromPortS, toPortS, cidrS, protoS );
    	}
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    protected class DeleteRuleClickListener implements OnClickListener {
    	@Override
        public void onClick(View v) { 
    		ImageButtonWithView bt = (ImageButtonWithView)v;
    		String ruleID = bt.getRuleView().getRule().getID();
    		
            progressDialogWaitStop.show();
            
            ( new AsyncTaskDeleteRule( ) ).execute( ruleID );
    	}
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    protected class CancelRuleAddClickListener implements OnClickListener {
    	@Override
        public void onClick(View v) { 
    		alertDialogSelectRule.dismiss();
    	}
    }
	
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    private void update( Vector<SimpleSecGroupRule> rules ) {
    	((LinearLayout)findViewById(R.id.layoutRuleList)).removeAllViews();
    	Iterator<SimpleSecGroupRule> rit = rules.iterator();
    	while(rit.hasNext()) {
    		SimpleSecGroupRule rl = rit.next( );
    		//Log.d("EDITSEC", "Rule="+rl.to_string());
    		RuleView rv = new RuleView( rl, new DeleteRuleClickListener(), this );
    		((LinearLayout)findViewById(R.id.layoutRuleList)).addView( rv );
    	}
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */   
    protected class AsyncTaskListRules extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	
	    @Override
	    protected Void doInBackground( String... args ) 
	    {
	      String secgrpID = args[0];
	      OSClient osc = OSClient.getInstance( U );

	      try {
	    	  jsonBuf = osc.requestSecGroupListRules(secgrpID);
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
	    		Utils.alert( errorMessage, SecGroupEditActivity.this );
				SecGroupEditActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    	try {
	    		Vector<SimpleSecGroupRule> rules = SimpleSecGroupRule.parse(jsonBuf);
				SecGroupEditActivity.this.update( rules );
	    	} catch(ParseException pe) {
	    		Utils.alert("EditSecGroupActivity.AsyncTaskListRules.onPostExecute: " + pe.getMessage( ), SecGroupEditActivity.this );
	    	}
			SecGroupEditActivity.this.progressDialogWaitStop.dismiss( );
	    }
    }	
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */   
    protected class AsyncTaskDeleteRule extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;
	    private  String   jsonBuf          = null;
	    @Override
	    protected Void doInBackground( String... args ) 
	    {
	      String ruleid = args[0];
	      OSClient osc = OSClient.getInstance( U );

	      try {
	    	  osc.deleteRule( ruleid );
	    	  jsonBuf = osc.requestSecGroupListRules(secgrpID);
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
	    		Utils.alert( errorMessage, SecGroupEditActivity.this );
				SecGroupEditActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    	try {
	    		Vector<SimpleSecGroupRule> rules = SimpleSecGroupRule.parse(jsonBuf);
				SecGroupEditActivity.this.update( rules );
	    	} catch(ParseException pe) {
	    		Utils.alert("SecGroupEditActivity.AsyncTaskListRules.onPostExecute: " + pe.getMessage( ), SecGroupEditActivity.this );
	    	}
	    	Utils.alert(SecGroupEditActivity.this.getString(R.string.RULEDELETED), SecGroupEditActivity.this);
			SecGroupEditActivity.this.progressDialogWaitStop.dismiss( );
	    }
    }
	
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */   
    protected class AsyncTaskCreateRule extends AsyncTask<String, Void, Void>
    {
     	private  String   errorMessage     = null;
	    private  boolean  hasError         = false;

	    @Override
	    protected Void doInBackground( String... args ) 
	    {
	      
	      OSClient osc = OSClient.getInstance( U );
	      String SecgrpID = args[0];
	      int FromPort = Integer.parseInt(args[1]);
	      int ToPort = Integer.parseInt(args[2]);
	      String Cidr = args[3];
	      String Protocol = args[4];
	      try {
	    	  osc.createRule( SecgrpID, FromPort, ToPort, Protocol, Cidr );
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
	    		Utils.alert( errorMessage, SecGroupEditActivity.this );
				SecGroupEditActivity.this.progressDialogWaitStop.dismiss( );
	    		return;
	    	}
	    	Utils.alert(SecGroupEditActivity.this.getString(R.string.RULECREATED), SecGroupEditActivity.this);
	    	(new AsyncTaskListRules()).execute( secgrpID );
	    	//EditSecGroupActivity.this.progressDialogWaitStop.dismiss( );
	    }
    } 
}