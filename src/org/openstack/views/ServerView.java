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

    private ImageButtonNamed deleteServer = null;
    private ImageViewNamed status = null;

    private Server S = null;

    public ServerView( Server s, Context ctx ) {
	super(ctx);
	S = s;
	
	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	setLayoutParams( params1 );

	text = new LinearLayoutNamed( ctx, this );
	text.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = 
	    new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
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
	String flavName = S.getFlavor( ).getName();
	if(flavName.length()>16)
	    flavName = flavName.substring(0,14) + "..";
	Flavor.setText( flavName );
	Flavor.setOnClickListener( (OnClickListener)ctx );
	Flavor.setTextColor( Color.parseColor("#BBBBBB"));
	
	text.addView(Name);
	text.addView(Flavor);
	text.setOnClickListener( (OnClickListener)ctx );
	addView(text);
	setOnClickListener( (OnClickListener)ctx );

	deleteServer = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_SERVER );
	deleteServer.setImageResource(android.R.drawable.ic_menu_delete);
	deleteServer.setOnClickListener( (OnClickListener)ctx );

	status = new ImageViewNamed( ctx, this );
	status.setImageResource( R.drawable.statusok );
	
	info = new LinearLayoutNamed( ctx, this );
	info.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = 
	    new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	info.setLayoutParams( params3 );
	info.setGravity( Gravity.RIGHT );
	info.addView( deleteServer );
	info.addView( status );
	
	addView( info );
    }
    
}
