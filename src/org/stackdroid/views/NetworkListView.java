package org.stackdroid.views;


import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.Utils;

import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class NetworkListView extends LinearLayout {
	
    private Network      	 net                = null;
    private LinearLayout 	 row                = null;
    private LinearLayout 	 buttonsLayout      = null;
    private LinearLayout 	 nameLayout         = null;
    private TextView     	 name               = null;
    private TextView     	 subnet             = null;
    private ImageButtonWithView delete  		= null;
    
    
    public NetworkListView( Network net, 
    						OnClickListener deleteNetListener,
    						Context ctx )
    {
	    super(ctx);
	    this.net = net;
	  
	    setOrientation( LinearLayout.HORIZONTAL );
	    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams( params1 );
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
		
		row = new LinearLayout( ctx);
		row.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams _params1
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);
		
		nameLayout = new LinearLayout( ctx );
		nameLayout.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams params2 
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		nameLayout.setLayoutParams( params2 );
		
		
		name = new TextView( ctx );
		name.setText( net.getName() );
		name.setTextColor( Color.parseColor("#333333") );
		name.setTypeface( null, Typeface.BOLD );
		
		subnet = new TextView( ctx );
		subnet.setText( net.getSubNetworks()[0].getAddress() );
		subnet.setTextColor( Color.parseColor("#333333") );
		//textPool.setTypeface( null, Typeface.BOLD );
		
		
		
		nameLayout.addView(name);
		nameLayout.addView(subnet);
		
		row.addView(nameLayout);
		
		buttonsLayout = new LinearLayout( ctx );
		buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f );
		params4.gravity=Gravity.RIGHT;
		buttonsLayout.setLayoutParams( params4 );
		buttonsLayout.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL );
		
		//delete = new ImageButtonNamed( ctx, (NetworkListView)this, ImageButtonNamed.BUTTON_ASSOCIATE_IP);
		delete.setImageResource(R.drawable.ipassociate);
		delete.setOnClickListener( deleteNetListener );
		
		
		
		buttonsLayout.addView( delete );
		row.addView( buttonsLayout );
		addView( row );
    }
    
    public Network getNetwork( ) { return net; }
    
}
