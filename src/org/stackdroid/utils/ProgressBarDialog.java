package org.stackdroid.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.*;
import android.view.*;
/*import android.content.*;
import android.util.Log;
import android.net.Uri;
import android.os.*;
import android.app.*;
//import android.os.Environment;
import android.view.View.OnClickListener;
*/
import org.stackdroid.R;

public class ProgressBarDialog extends Dialog {

    private String message  = null;
    private String TITLE    = null;
//    private Context ctx     = null;

    /**
     *
     *
     *
     *
     *
     */
    public ProgressBarDialog(Context context, String message, String title) {

	super(context);
	this.message = message;
	this.TITLE   = title;
	//this.ctx = context;

    }
 
    /**
     *
     *
     *
     *
     *
     */
    @Override
	public void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.customdialog);
	
	((TextView)findViewById(R.id.customDialogMessage)).setText( message );
	setTitle( TITLE );
	//setIcon( R.drawable.icon );
	Button buttonOK = (Button) findViewById(R.id.buttonOkCustomDialog);
	
	buttonOK.setOnClickListener(new OKListener());
	
	

 	getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
 			     WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	
    }
    
    /**
     *
     *
     *
     *
     *
     */
    private class OKListener implements android.view.View.OnClickListener {

	@Override
	public void onClick(View v) {
	    //readyListener.ready( false );
	    ProgressBarDialog.this.dismiss();
	}
	
    }
 
}
 
 

