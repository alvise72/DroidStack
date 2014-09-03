package org.stackdroid.activities;

import java.util.Vector;

import org.stackdroid.R;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditSecGroupActivity extends Activity  implements OnClickListener {

    private String secgrpID   = null;
	private String secgrpName = null;
	private String secgrpDesc = null;
    private User   U          = null;
    private ArrayAdapter<String> spinnerRulesAdapter  = null;
    private Spinner ruleSpinner = null;
    private Vector<String> predefinedRules = null;
    private AlertDialog alertDialogSelectRule = null;
    
    /*
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
        secgrpName = this.getIntent().getStringExtra("SECGRPNAME");
        secgrpDesc = this.getIntent().getStringExtra("SECGRPDESC");
        ((EditText)findViewById(R.id.secgrpName)).setText(secgrpName);
        ((EditText)findViewById(R.id.secgrpDesc)).setText(secgrpDesc);
        
        String selectedUser = Utils.getStringPreference("SELECTEDUSER", "", this);
    	try {
    		U = User.fromFileID( selectedUser, Utils.getStringPreference("FILESDIR","",this), this );
    	} catch(RuntimeException re) {
    		Utils.alert("OSImagesActivity: "+re.getMessage(), this );
    		return;
    	}
	
    	if(selectedUser.length()!=0)
    		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+U.getUserName() + " (" + U.getTenantName() + ")"); 
    	else
    		((TextView)findViewById(R.id.selected_user)).setText(getString(R.string.SELECTEDUSER)+": "+getString(R.string.NONE)); 
	   
    }

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void addRule( View v ) { 
    	
    	spinnerRulesAdapter = new ArrayAdapter<String>(EditSecGroupActivity.this, android.R.layout.simple_spinner_item, predefinedRules.subList(0,predefinedRules.size()) );
    	spinnerRulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	
    	LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.my_dialog_layout_addrule, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // set dialog message

        alertDialogBuilder.setTitle(getString(R.string.ADDRULE) );

        alertDialogSelectRule = alertDialogBuilder.create();

        ruleSpinner = (Spinner) promptsView.findViewById(R.id.mySpinner);
        ruleSpinner.setAdapter(spinnerRulesAdapter);
        final Button mButton = (Button) promptsView.findViewById(R.id.myButton);
    	//final Button mButtonCancel = (Button) promptsView.findViewById(R.id.myButtonCancel);
        mButton.setOnClickListener(this);
        //mButton.setOnItemSelectedListener( this );
        // show it
        alertDialogSelectRule.show();
        alertDialogSelectRule.setCanceledOnTouchOutside(false);
    }

    /*
     * 
     * 
     * 
     * 
     * 
     * 
     */
    @Override
    public void onClick(View v) { }
}