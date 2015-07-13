package org.stackdroid.views;


import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.Utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class NetworkListView extends LinearLayout {
	
    private Network      	 		 net                = null;
    //private SubNetwork				 subnet				= null;
    private LinearLayoutWithView 	 row                = null;
    private LinearLayoutWithView 	 buttonsLayout      = null;
    private LinearLayoutWithView 	 nameLayout         = null;
    private TextViewWithView     	 name               = null;
    //private TextViewWithView     	 subnetView         = null;
    private ImageButtonWithView 	 delete  			= null;
    
    
    public NetworkListView( Network net, 
    						//SubNetwork subnet,
    						OnClickListener infoNetListener,
    						OnClickListener deleteNetListener,
    						Context ctx )
    {
	    super(ctx);
	    this.net = net;
	    //this.subnet = subnet;
	    this.setOnClickListener(infoNetListener);
	    setOrientation(LinearLayout.HORIZONTAL);
	    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(params1);
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
		
		row = new LinearLayoutWithView( ctx, (NetworkListView)this );
		row.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams _params1
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);
		row.setOnClickListener(infoNetListener);
		nameLayout = new LinearLayoutWithView( ctx, (NetworkListView)this  );
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.3f);
		nameLayout.setLayoutParams( params2 );
		nameLayout.setOnClickListener(infoNetListener);
		name = new TextViewWithView( ctx, (NetworkListView)this  );
		name.setText(net.getName());
		name.setTextColor( Color.parseColor("#333333") );
		name.setTypeface(null, Typeface.BOLD);
		name.setEllipsize(TextUtils.TruncateAt.END);
		name.setSingleLine();
		
		/*subnetView = new TextViewWithView( ctx, (NetworkListView)this  );
		subnetView.setText( net.getSubNetworks()!=null && net.getSubNetworks().size()!=0 ? net.getSubNetworks().elementAt(0).getAddress() : "" );
		subnetView.setTextColor( Color.parseColor("#333333") );
		subnetView.setOnClickListener(infoNetListener);*/
		nameLayout.addView(name);
		//nameLayout.addView(subnetView);
		nameLayout.setOnClickListener(infoNetListener);
		row.addView(nameLayout);
		
		buttonsLayout = new LinearLayoutWithView( ctx, (NetworkListView)this );
		buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f );
		params4.gravity=Gravity.RIGHT;
		buttonsLayout.setLayoutParams( params4 );
		buttonsLayout.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL );
		
		delete = new ImageButtonWithView( ctx, (NetworkListView)this );
		delete.setImageResource(android.R.drawable.ic_menu_delete);
		delete.setOnClickListener( deleteNetListener );
		
		buttonsLayout.addView( delete );
		row.addView( buttonsLayout );
		addView( row );
    }
    
    public Network getNetwork( ) { return net; }
    
}
