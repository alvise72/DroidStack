package org.stackdroid.views;



import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.RouterPort;
import org.stackdroid.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.stackdroid.R;

public class RouterPortView extends LinearLayout {
    
    private  RouterPort           routerPort         = null;
    private LinearLayoutWithView row                 = null;
    private LinearLayoutWithView buttonsLayout       = null;
    private LinearLayoutWithView nameLayout          = null;
    private ImageButtonWithView  deleteButton        = null;
    private TextView             routerPortIPAddress = null;
    private TextView             routerPortStatus    = null;
    private TextView             routerPortMAC       = null;

    /**
     *
     *
     *
     *
     *
     *
     */
    public RouterPortView( RouterPort routerPort,
                           OnClickListener deleteListener,
                           Context ctx )
    {
	    super(ctx);
        this.routerPort = routerPort;
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params1);
        int padding = Utils.getDisplayPixel(ctx, 2);
        setPadding(padding, padding, padding, padding);
        row = new LinearLayoutWithView( ctx, this );
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        row.setLayoutParams(_params1);
        row.setBackgroundResource(R.drawable.rounded_corner_thin);

        routerPortIPAddress = new TextView(ctx);
        String IP = routerPort.getFixedIP();//.getName();
        routerPortIPAddress.setText(IP);
        routerPortIPAddress.setTextColor(Color.parseColor("#333333"));
        routerPortIPAddress.setTypeface(null, Typeface.BOLD);

        routerPortStatus = new TextView(ctx);
        String status = " (" + (routerPort.getStatus() != null ? routerPort.getStatus() : "Status N/A" ) + " )";
        routerPortStatus.setText(status);

        routerPortMAC = new TextView(ctx);
        String mac = " - MAC = " + (routerPort.getMAC() != null ? routerPort.getMAC().toUpperCase() : "N/A" ) ;
        routerPortMAC.setText(mac);


        nameLayout = new LinearLayoutWithView( ctx, (RouterPortView)this );
        nameLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params2
                = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params2.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        nameLayout.setLayoutParams(params2);

        nameLayout.addView(routerPortIPAddress);
        nameLayout.addView(routerPortStatus);
        nameLayout.addView(routerPortMAC);

        buttonsLayout = new LinearLayoutWithView( ctx, (RouterPortView)this );
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL );
        LinearLayout.LayoutParams params4
                = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        params4.gravity= Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        buttonsLayout.setLayoutParams( params4 );
        buttonsLayout.setGravity(Gravity.RIGHT);

        deleteButton = new ImageButtonWithView( ctx, (RouterPortView)this );
        deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        deleteButton.setOnClickListener(deleteListener);

        buttonsLayout.addView(deleteButton);


        row.addView(nameLayout);
        row.addView(buttonsLayout);
        addView(row);
    }

    /**
     *
     *
     *
     *
     *
     *
     */
    public RouterPort getRouterPort( ) { return routerPort; }
    
}
