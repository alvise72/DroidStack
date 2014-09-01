package org.stackdroid.views;

import android.widget.LinearLayout;


import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
//import android.view.View;

import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class VolumeView extends LinearLayout {
    
    private LinearLayoutNamed row  			= null;
    private LinearLayoutNamed text 			= null;
    private LinearLayoutNamed info 			= null;
    private TextViewNamed Name     		    = null;
    private TextViewNamed Status_and_Size   = null;
    private TextViewNamed Attach   			= null;

    private ImageButtonNamed detach = null;
    private ImageButtonNamed delete = null;
    //private ButtonNamed consoleLog = null;

    private Volume V = null;

    public VolumeView( Volume v, Context ctx ) {
    	super(ctx);
    	V = v;
	
    	setOrientation( LinearLayout.HORIZONTAL );
    	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	setLayoutParams( params1 );
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
		row = new LinearLayoutNamed( ctx, this );
		row.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);


		text = new LinearLayoutNamed( ctx, (VolumeView)this );
		text.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
		text.setLayoutParams( params2 );

		Name = new TextViewNamed( ctx, (VolumeView)this );
		String volName = V.getName();
		if(volName.length()>16)
			volName = volName.substring(0,14) + "..";
		Name.setText( volName );
		Name.setTextColor( Color.parseColor("#333333") );
		Name.setOnClickListener( (OnClickListener)ctx );
		Name.setTypeface( null, Typeface.BOLD );
	
		Status_and_Size = new TextViewNamed( ctx, (VolumeView)this );
		Status_and_Size.setText( V.getSize() + "GB (" + Volume.status_str[V.getStatus()]+")" );
//		Name.setTextColor( Color.parseColor("#333333") );
		Status_and_Size.setOnClickListener( (OnClickListener)ctx );
//		Name.setTypeface( null, Typeface.BOLD );

		LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	
		text.addView(Name);
		text.addView(Status_and_Size);
		//text.addView(Status);
		//text.addView(consoleLog);
		text.setOnClickListener( (OnClickListener)ctx );
		row.addView(text);
		setOnClickListener( (OnClickListener)ctx );

		detach = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_ATTACHDETACH_VOlUME );
		detach.setImageResource(R.drawable.ipassociate);
		detach.setOnClickListener( (OnClickListener)ctx );

		delete = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_SNAP_SERVER );
		delete.setImageResource(android.R.drawable.ic_menu_delete);
		delete.setOnClickListener( (OnClickListener)ctx );

		info = new LinearLayoutNamed( ctx, (VolumeView)this );
		info.setOrientation( LinearLayout.HORIZONTAL );
		//info.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
		info.setLayoutParams( params3 );
		info.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		info.addView( detach );
		info.addView( delete );
	
		row.addView( info );
		addView( row );
    		
    }

    public Volume getVolume( ) { return V; }
    
}
