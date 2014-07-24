package org.openstack.views;


import org.openstack.R;
import org.openstack.utils.FloatingIP;
import org.openstack.utils.ImageButtonNamed;
import org.openstack.utils.Utils;

import android.view.Gravity;

import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class FloatingIPView extends LinearLayout {
	
    private FloatingIP fip                  = null;
    private LinearLayout row                = null;
    private LinearLayout buttonsLayout      = null;
    private LinearLayout nameLayout         = null;
    private TextView     textIP             = null;
    private TextView     textPool           = null;
    private TextView     textServer         = null;
    private ImageButtonNamed  releaseFIP    = null;
    private ImageButtonNamed  dissociateFIP = null;
    
    public FloatingIPView( FloatingIP fip, Context ctx ) {
	    super(ctx);
	    this.fip = fip;
	  
	    setOrientation( LinearLayout.HORIZONTAL );
	    LinearLayout.LayoutParams params1 
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams( params1 );
		//setBackgroundResource(R.drawable.rounded_corner_thin);
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
		//setOnClickListener( (OnClickListener)ctx );
		
		row = new LinearLayout( ctx);
		row.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams _params1
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);
		
		nameLayout = new LinearLayout( ctx );
		nameLayout.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams params2 
		    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		nameLayout.setLayoutParams( params2 );
		
		
		textIP = new TextView( ctx );
		textIP.setText( "IPv4: "+fip.getIP() );
		textIP.setTextColor( Color.parseColor("#333333") );
		textIP.setTypeface( null, Typeface.BOLD );
		
		textPool = new TextView( ctx );
		textPool.setText( "Pool: "+fip.getPoolName() );
		textPool.setTextColor( Color.parseColor("#333333") );
		textPool.setTypeface( null, Typeface.BOLD );
		
		textServer = new TextView( ctx );
		textServer.setText( "Server: "+(fip.getServerName()!=null && fip.getServerName().length()!=0 ? fip.getServerName() : "None") ); 
		textServer.setTextColor( Color.parseColor("#333333") );
		textServer.setTypeface( null, Typeface.BOLD );
		
		nameLayout.addView(textIP);
		nameLayout.addView(textPool);
		nameLayout.addView(textServer);
		
		row.addView(nameLayout);
		
		buttonsLayout = new LinearLayout( ctx );
		buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams params4 
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		params4.gravity=Gravity.RIGHT;
		buttonsLayout.setLayoutParams( params4 );
		buttonsLayout.setGravity( Gravity.RIGHT );
		
		releaseFIP = new ImageButtonNamed( ctx, (FloatingIPView)this, ImageButtonNamed.BUTTON_RELEASE_IP);
		//releaseFIP.setImageResource(android.R.drawable.ic_menu_close_clear_cancel );
		releaseFIP.setImageResource(android.R.drawable.ic_menu_delete );
		
		releaseFIP.setOnClickListener( (OnClickListener)ctx );
		
		dissociateFIP = new ImageButtonNamed( ctx, (FloatingIPView)this, ImageButtonNamed.BUTTON_DISSOCIATE_IP);
		dissociateFIP.setImageResource(android.R.drawable.ic_delete );
		dissociateFIP.setOnClickListener( (OnClickListener)ctx );
		
		
		buttonsLayout.addView( dissociateFIP );
		buttonsLayout.addView( releaseFIP );
		row.addView( buttonsLayout );
		addView( row );
    }
    
    public FloatingIP getFloatingIP( ) { return fip; }
    
}
