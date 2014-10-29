package org.stackdroid.views;

import android.widget.LinearLayout;

import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class ServerView extends LinearLayout {
    
    private LinearLayoutWithView row  = null;
    private LinearLayoutWithView text = null;
    private LinearLayoutWithView btns = null;
    private TextViewWithView Name     = null;
    private TextViewWithView Flavor   = null;
    private TextViewWithView Status   = null;

    private ImageButtonWithView snapServer = null;
    private ImageButtonWithView deleteServer = null;
    private ButtonWithView addIPToServer = null;
    
    private ButtonWithView consoleLog = null;

    private Server S = null;

    public ServerView( Server s, 
    				   OnClickListener infoListener, 
    				   OnClickListener consoleLogListener,
    				   OnClickListener deleteServerListener,
    				   OnClickListener addIP,
    				   OnClickListener snapServerListener,
    				   Context ctx ) {
	super(ctx);
	S = s;
	
	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	setLayoutParams( params1 );
	//setBackgroundResource(R.drawable.rounded_corner_thin);
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );
	
	row = new LinearLayoutWithView( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);


	text = new LinearLayoutWithView( ctx, (ServerView)this );
	text.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
	text.setLayoutParams( params2 );

	Name = new TextViewWithView( ctx, (ServerView)this );
	String servName = S.getName();
	if(servName.length()>16)
	    servName = servName.substring(0,14) + "..";
	Name.setText( servName );
	Name.setTextColor( Color.parseColor("#333333") );
	Name.setOnClickListener( infoListener );
	Name.setTypeface( null, Typeface.BOLD );
	
	Flavor = new TextViewWithView( ctx, (ServerView)this );
	String flavName = S.getFlavor( ).getName();// + " (" + (int)(S.getFlavor( ).getDISK()) + "GB, " + S.getFlavor( ).getVCPU( )+ " cpu, " + S.getFlavor( ).getRAM( ) + " ram)";
	if(flavName.length()>30)
	    flavName = flavName.substring(0,28) + "..";
	Flavor.setText( flavName );
	Flavor.setOnClickListener( infoListener );
	Flavor.setTextColor( Color.parseColor("#999999"));
	//Log.d("SERVERVIEW", "STATUS="+S.getStatus( ));
	Status = new TextViewWithView( ctx, (ServerView)this );
	Status.setText("Status: "+S.getStatus( ) );
	Status.setOnClickListener( infoListener );

	if(S.getStatus( ).compareToIgnoreCase("active")==0)
	    Status.setTextColor( Color.parseColor("#00AA00") );
	if(S.getStatus( ).compareToIgnoreCase("error")==0)
	    Status.setTextColor( Color.parseColor("#AA0000") );
	if(S.getStatus( ).compareToIgnoreCase("build")==0) {
	    Status.setText("Status: " + S.getStatus( ) +" (" + S.getTask( ) + ")");
	    if(S.getTask( ).compareToIgnoreCase("deleting")==0) 
		Status.setTextColor( Color.parseColor("#000000") );
	}

	LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	consoleLog = new ButtonWithView(ctx, this);
	consoleLog.setText("Console Log");
	consoleLog.setTextSize(10.0f);
	int density = 200;
	String dispDensity = Configuration.getInstance().getValue("DISPLAYDENSITY", "200");
	if(dispDensity!=null)
		density = Integer.parseInt(dispDensity);
	consoleLog.setPadding(10 * density, 2 * density, 10 * density, 2 * density);
	consoleLog.setOnClickListener( consoleLogListener );
	consoleLog.setLayoutParams(params5);
	
	text.addView(Name);
	text.addView(Flavor);
	text.addView(Status);
	text.addView(consoleLog);
	text.setOnClickListener( infoListener );
	row.addView(text);
	setOnClickListener( infoListener );

	deleteServer = new ImageButtonWithView( ctx, this );
	deleteServer.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
	deleteServer.setOnClickListener( deleteServerListener );

	snapServer = new ImageButtonWithView( ctx, this );
	snapServer.setImageResource(android.R.drawable.ic_menu_camera);
	snapServer.setOnClickListener( snapServerListener );

	addIPToServer = new ButtonWithView( ctx, this );
	addIPToServer.setText("IP");
	addIPToServer.setOnClickListener(addIP);
	
	btns = new LinearLayoutWithView( ctx, (ServerView)this );
	btns.setOrientation( LinearLayout.HORIZONTAL );
	
	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
	btns.setLayoutParams( params3 );
	btns.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
	//info.addView( consoleLog );
	btns.addView( addIPToServer );
	btns.addView( snapServer );
	btns.addView( deleteServer );
	
	row.addView( btns );
	addView( row );
    		
    }

    public Server getServer( ) { return S; }
    
}
