package org.stackdroid.views;

import android.text.TextUtils;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Volume;

public class VolumeView extends LinearLayout {
    
    private LinearLayoutWithView row  			= null;
    private LinearLayoutWithView text 			= null;
    private LinearLayoutWithView commands   	= null;
    private TextViewWithView Name     		    = null;
    private TextViewWithView Status_and_Size   = null;
    
    private ImageButtonWithView attach = null;
    private ImageButtonWithView detach = null;
    private ImageButtonWithView delete = null;

    private Volume V = null;

    public VolumeView( Volume v, 
    				   OnClickListener attachVol, 
    				   OnClickListener detachVol, 
    				   OnClickListener deleteVol, 
    				   OnClickListener infoVol,
    				   Context ctx ) 
    {
    	super(ctx);
    	V = v;
	
    	setOrientation( LinearLayout.HORIZONTAL );
    	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	setLayoutParams( params1 );
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
		row = new LinearLayoutWithView( ctx, this );
		row.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);
		setOnClickListener(infoVol);

		text = new LinearLayoutWithView( ctx, (VolumeView)this );
		text.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
		text.setLayoutParams( params2 );
		text.setOnClickListener(infoVol);
		
		Name = new TextViewWithView( ctx, (VolumeView)this );
		String volName = V.getName();
		//if( volName.length() > 16 )
		//	volName = volName.substring(0,14) + "..";
		Name.setText( volName );
		Name.setTextColor( Color.parseColor("#333333") );
		//Name.setOnClickListener( (OnClickListener)ctx );
		Name.setTypeface(null, Typeface.BOLD);
		Name.setOnClickListener(infoVol);
		Name.setEllipsize(TextUtils.TruncateAt.END);
		Name.setSingleLine();
		
		Status_and_Size = new TextViewWithView( ctx, (VolumeView)this );
		Status_and_Size.setText( V.getSize() + "GB (" + V.getStatus()+")" );
		Status_and_Size.setOnClickListener( infoVol );
	
		text.addView(Name);
		text.addView(Status_and_Size);
		//text.setOnClickListener( (OnClickListener)ctx );
		row.addView(text);
		//setOnClickListener( (OnClickListener)ctx );
		row.setOnClickListener(infoVol);
		
		attach = new ImageButtonWithView( ctx, this );
		attach.setImageResource(R.drawable.ipassociate);
		attach.setOnClickListener( attachVol );

		detach = new ImageButtonWithView( ctx, this );
		detach.setImageResource(android.R.drawable.ic_delete);
		detach.setOnClickListener( detachVol );
		
		delete = new ImageButtonWithView( ctx, this );
		delete.setImageResource(android.R.drawable.ic_menu_delete);
		delete.setOnClickListener( deleteVol );

		commands = new LinearLayoutWithView( ctx, (VolumeView)this );
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
