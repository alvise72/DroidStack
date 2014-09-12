package org.stackdroid.views;

import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonNamed;
import org.stackdroid.utils.LinearLayoutNamed;
import org.stackdroid.utils.TextViewNamed;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;

public class VolumeView extends LinearLayout {
    
    private LinearLayoutNamed row  			= null;
    private LinearLayoutNamed text 			= null;
    private LinearLayoutNamed commands   	= null;
    private TextViewNamed Name     		    = null;
    private TextViewNamed Status_and_Size   = null;
    
    private ImageButtonNamed attach = null;
    private ImageButtonNamed detach = null;
    private ImageButtonNamed delete = null;

    private Volume V = null;

    public VolumeView( Volume v, 
    				   OnClickListener attachVol, 
    				   OnClickListener detachVol, 
    				   OnClickListener deleteVol, 
    				   Context ctx ) 
    {
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
		if( volName.length() > 16 )
			volName = volName.substring(0,14) + "..";
		Name.setText( volName );
		Name.setTextColor( Color.parseColor("#333333") );
		//Name.setOnClickListener( (OnClickListener)ctx );
		Name.setTypeface( null, Typeface.BOLD );
	
		Status_and_Size = new TextViewNamed( ctx, (VolumeView)this );
		Status_and_Size.setText( V.getSize() + "GB (" + V.getStatus()+")" );
		//Status_and_Size.setOnClickListener( (OnClickListener)ctx );
	
		text.addView(Name);
		text.addView(Status_and_Size);
		//text.setOnClickListener( (OnClickListener)ctx );
		row.addView(text);
		//setOnClickListener( (OnClickListener)ctx );

		attach = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_ATTACHDETACH_VOlUME );
		attach.setImageResource(R.drawable.ipassociate);
		attach.setOnClickListener( attachVol );

		detach = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_ATTACHDETACH_VOlUME );
		detach.setImageResource(android.R.drawable.ic_delete);
		detach.setOnClickListener( detachVol );
		
		delete = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_SNAP_SERVER );
		delete.setImageResource(android.R.drawable.ic_menu_delete);
		delete.setOnClickListener( deleteVol );

		commands = new LinearLayoutNamed( ctx, (VolumeView)this );
		commands.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
		commands.setLayoutParams( params3 );
		commands.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		commands.addView( attach );
		commands.addView( detach );
		commands.addView( delete );
	
		row.addView( commands );
		addView( row );
    		
    }

    public Volume getVolume( ) { return V; }
}
