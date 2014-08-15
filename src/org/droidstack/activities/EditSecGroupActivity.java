package org.openstack.activities;

import org.openstack.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditSecGroupActivity extends Activity {

    private String secgrpID   = null;
	private String secgrpName = null;
	private String secgrpDesc = null;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.editsecgroup );

        secgrpID   = this.getIntent().getStringExtra("SECGRPID");
        secgrpName = this.getIntent().getStringExtra("SECGRPNAME");
        secgrpDesc = this.getIntent().getStringExtra("SECGRPDESC");
        ((EditText)findViewById(R.id.secgrpName)).setText(secgrpName);
        ((EditText)findViewById(R.id.secgrpDesc)).setText(secgrpDesc);
        
    }

    public void addRuleE( View v ){}
    public void addRuleI( View v ){}

}