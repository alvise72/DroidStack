package org.stackdroid.views;

//import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;

import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
//import android.view.View;

import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class ServerView extends LinearLayout {
    
//    private Context ctx = null;
    
    private LinearLayoutNamed row  = null;
    private LinearLayoutNamed text = null;
    private LinearLayoutNamed info = null;
    private TextViewNamed Name     = null;
    private TextViewNamed Flavor   = null;
    private TextViewNamed Status   = null;

    private ImageButtonNamed snapServer = null;
    private ImageButtonNamed deleteServer = null;
    private ButtonNamed consoleLog = null;

    private Server S = null;

    public ServerView( Server s, Context ctx ) {
	super(ctx);
	S = s;
	
	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	setLayoutParams( params1 );
	//setBackgroundResource(R.drawable.rounded_corner_thin);
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );
	
	row = new LinearLayoutNamed( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);


	text = new LinearLayoutNamed( ctx, (ServerView)this );
	text.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = 
	    new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
	text.setLayoutParams( params2 );

	Name = new TextViewNamed( ctx, (ServerView)this );
	String servName = S.getName();
	if(servName.length()>16)
	    servName = servName.substring(0,14) + "..";
	Name.setText( servName );
	Name.setTextColor( Color.parseColor("#333333") );
	Name.setOnClickListener( (OnClickListener)ctx );
	Name.setTypeface( null, Typeface.BOLD );
	
	Flavor = new TextViewNamed( ctx, (ServerView)this );
	String flavName = S.getFlavor( ).getName();// + " (" + (int)(S.getFlavor( ).getDISK()) + "GB, " + S.getFlavor( ).getVCPU( )+ " cpu, " + S.getFlavor( ).getRAM( ) + " ram)";
	if(flavName.length()>30)
	    flavName = flavName.substring(0,28) + "..";
	Flavor.setText( flavName );
	Flavor.setOnClickListener( (OnClickListener)ctx );
	Flavor.setTextColor( Color.parseColor("#999999"));
	//Log.d("SERVERVIEW", "STATUS="+S.getStatus( ));
	Status = new TextViewNamed( ctx, (ServerView)this );
	Status.setText("Status: "+S.getStatus( ) );
	Status.setOnClickListener( (OnClickListener)ctx );

	if(S.getStatus( ).compareToIgnoreCase("active")==0)
	    Status.setTextColor( Color.parseColor("#00AA00") );
	if(S.getStatus( ).compareToIgnoreCase("error")==0)
	    Status.setTextColor( Color.parseColor("#AA0000") );
	if(S.getStatus( ).compareToIgnoreCase("build")==0) {
	    Status.setText("Status: " + S.getStatus( ) +" (" + S.getTask( ) + ")");
	    if(S.getTask( ).compareToIgnoreCase("deleting")==0) 
		Status.setTextColor( Color.parseColor("#000000") );
	}

	LinearLayout.LayoutParams params5 = 
		    new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	consoleLog = new ButtonNamed(ctx, this, -1);
	consoleLog.setText("Console Log");
	consoleLog.setTextSize(10.0f);
	int density = Utils.getIntegerPreference("DISPLAYDENSITY", 200, ctx);
	consoleLog.setPadding(10 * density, 2 * density, 10 * density, 2 * density);
	consoleLog.setOnClickListener( (OnClickListener)ctx );
	consoleLog.setLayoutParams(params5);
	
	text.addView(Name);
	text.addView(Flavor);
	text.addView(Status);
	text.addView(consoleLog);
	text.setOnClickListener( (OnClickListener)ctx );
	row.addView(text);
	setOnClickListener( (OnClickListener)ctx );

	deleteServer = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_SERVER );
	deleteServer.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
	deleteServer.setOnClickListener( (OnClickListener)ctx );

	snapServer = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_SNAP_SERVER );
	snapServer.setImageResource(android.R.drawable.ic_menu_camera);
	snapServer.setOnClickListener( (OnClickListener)ctx );

	info = new LinearLayoutNamed( ctx, (ServerView)this );
	info.setOrientation( LinearLayout.HORIZONTAL );
	//info.setGravity(Gravity.CENTER_VERTICAL);
	LinearLayout.LayoutParams params3 = 
	    new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
	info.setLayoutParams( params3 );
	info.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
	info.addView( snapServer );
	info.addView( deleteServer );
	
	row.addView( info );
	addView( row );
	
/*    int textLayoutWidth = text.getLayoutParams()..width;
    int nameWidth       = Name.getLayoutParams().width;
    int flavorWidth     = Flavor.getLayoutParams().width;
    int statusWidth     = Status.getLayoutParams().width;
    Log.d("SERVERVIEW", "textLayoutWidth="+textLayoutWidth+" - nameWidth="+nameWidth);
    if( nameWidth >= textLayoutWidth ) {
    	String origText = Name.getText().toString();
    	origText = origText.substring(0, origText.length()-7);
    	origText = origText + "...";
    	Name.setText(origText);
    }
    if( flavorWidth >= textLayoutWidth ) {
    	String origText = Flavor.getText().toString();
    	origText = origText.substring(0, origText.length()-7);
    	origText = origText + "...";
    	Flavor.setText(origText);
    }
    if( statusWidth >= textLayoutWidth ) {
    	String origText = Status.getText().toString();
    	origText = origText.substring(0, origText.length()-7);
    	origText = origText + "...";
    	Status.setText(origText);
    } */
    		
    }

    public Server getServer( ) { return S; }
    
}
