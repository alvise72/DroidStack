package org.openstack.utils;

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

public class ServerView extends LinearLayout {
    
    private Context ctx = null;

    private LinearLayoutNamed text = null;
    private LinearLayoutNamed info = null;

    private TextViewNamed Name = null;
    private TextViewNamed Flavor = null;

    private ImageButtonNamed delete = null;
    private ImageView status = null;

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

	Name = new TextViewNamed( ctx, this );
	Name.setText( S.getName() );
	Name.setTextColor( Color.parseColor("#333333") );
	Name.setOnClickListener( (OnClickListener)ctx );
	Name.setTypeface( null, Typeface.BOLD );
	
	Flavor = new TextViewNamed( ctx, this );
	Flavor.setText( S.getFlavorID( ));
	Flavor.setOnClickListener( (OnClickListener)ctx );
	Flavor.setTextColor( Color.parseColor("#BBBBBB"));
	
	text.addView(Name);
	text.addView(Flavor);
	text.setOnClickListener( (OnClickListener)ctx );
	addView(text);
	setOnClickListener( (OnClickListener)ctx );
    }
    
}
