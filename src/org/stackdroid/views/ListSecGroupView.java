package org.stackdroid.views;

//import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;

import android.graphics.Typeface;
import android.graphics.Color;

import android.view.Gravity;
//import android.view.View;

import android.content.Context;

import org.stackdroid.R;

import org.stackdroid.utils.*;

public class ListSecGroupView extends LinearLayout {
    
//    private Context ctx = null;
    
    private LinearLayoutNamed row  = null;
    private LinearLayoutNamed text = null;
    private LinearLayoutNamed info = null;
    private TextViewNamed Name     = null;
    //private TextViewNamed Flavor   = null;
    //private TextViewNamed Status   = null;

//    private ImageButtonNamed edit = null;
    private ImageButtonNamed deleteSecGroup = null;
    private ImageButtonNamed editSecGroup = null;
    
    private SecGroup S = null;

    public ListSecGroupView( SecGroup s, Context ctx ) {
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


	text = new LinearLayoutNamed( ctx, (ListSecGroupView)this );
	text.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = 
	    new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	text.setLayoutParams( params2 );
	text.setGravity( Gravity.CENTER_VERTICAL);

	Name = new TextViewNamed( ctx, (ListSecGroupView)this );
	String servName = S.getName();
	if(servName.length()>16)
	    servName = servName.substring(0,14) + "..";
	Name.setText( servName );
	Name.setTextColor( Color.parseColor("#333333") );
	//Name.setOnClickListener( (OnClickListener)ctx );
	Name.setTypeface( null, Typeface.BOLD );
	
	text.addView(Name);
	row.addView(text);
	
	if(S.getName( ).compareTo("default") != 0) {
		editSecGroup = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_EDIT_SECGRP );
		editSecGroup.setImageResource(android.R.drawable.ic_menu_edit);
		editSecGroup.setOnClickListener( (OnClickListener)ctx );
		deleteSecGroup = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_SECGRP );
		deleteSecGroup.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
		deleteSecGroup.setOnClickListener( (OnClickListener)ctx );
	}
	info = new LinearLayoutNamed( ctx, (ListSecGroupView)this );
	info.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = 
	    new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	info.setLayoutParams( params3 );
	info.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
	
	if(S.getName( ).compareTo("default") != 0) {
		info.addView( editSecGroup );
		info.addView( deleteSecGroup );
	}
	
	row.addView( info );
	addView( row );
    }

    public SecGroup getSecGroup( ) { return S; }
    
}
