package org.openstack.views;

import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Typeface;
import android.graphics.Color;

import android.view.Gravity;
import android.view.View;

import android.content.Context;

import org.openstack.R;

import org.openstack.utils.*;

public class ServerView extends LinearLayout {
    
    private Context ctx = null;

    private LinearLayoutNamed text = null;
    private LinearLayoutNamed info = null;

    private TextViewNamed Name = null;
    private TextViewNamed Flavor = null;
    private TextViewNamed Status = null;

    private ImageButtonNamed snapServer = null;
    private ImageButtonNamed deleteServer = null;

    private Server S = null;

    public ServerView( Server s, Context ctx ) {
	super(ctx);
	S = s;
	
	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( Utils.getIntegerPreference("SCREENW", 480, ctx)-8/*LayoutParams.FILL_PARENT*/, LayoutParams.FILL_PARENT);
	setLayoutParams( params1 );
	setBackgroundResource(R.drawable.rounded_corner_thin);

	text = new LinearLayoutNamed( ctx, (ServerView)this );
	text.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = 
	    new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
	String flavName = S.getFlavor( ).getName() + " (" + (int)(S.getFlavor( ).getDISK()) + "GB, " + S.getFlavor( ).getVCPU( )+ " cpu, " + S.getFlavor( ).getRAM( ) + " ram)";
	if(flavName.length()>30)
	    flavName = flavName.substring(0,28) + "..";
	Flavor.setText( flavName );
	Flavor.setOnClickListener( (OnClickListener)ctx );
	Flavor.setTextColor( Color.parseColor("#999999"));
	
	Status = new TextViewNamed( ctx, (ServerView)this );
	Status.setText("Status: "+S.getStatus( ) );
	if(S.getStatus( ).compareToIgnoreCase("active")==0)
	    Status.setTextColor( Color.parseColor("#00AA00") );
	if(S.getStatus( ).compareToIgnoreCase("error")==0)
	    Status.setTextColor( Color.parseColor("#AA0000") );
	if(S.getStatus( ).compareToIgnoreCase("build")==0) {
	    Status.setText("Status: " + S.getStatus( ) +" (" + S.getTask( ) + ")");
	    if(S.getTask( ).compareToIgnoreCase("deleting")==0) 
		Status.setTextColor( Color.parseColor("#000000") );
	}

	text.addView(Name);
	text.addView(Flavor);
	text.addView(Status);
	text.setOnClickListener( (OnClickListener)ctx );
	addView(text);
	setOnClickListener( (OnClickListener)ctx );

	deleteServer = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_SERVER );
	deleteServer.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
	deleteServer.setOnClickListener( (OnClickListener)ctx );

	snapServer = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_SNAP_SERVER );
	snapServer.setImageResource(android.R.drawable.ic_menu_camera);
	snapServer.setOnClickListener( (OnClickListener)ctx );

	info = new LinearLayoutNamed( ctx, (ServerView)this );
	info.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = 
	    new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	info.setLayoutParams( params3 );
	info.setGravity( Gravity.RIGHT );
	info.addView( snapServer );
	info.addView( deleteServer );
	
	addView( info );
    }

    public Server getServer( ) { return S; }
    
}
