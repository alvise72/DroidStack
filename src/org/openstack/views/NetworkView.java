package org.openstack.views;


import org.openstack.utils.Network;
import android.widget.CheckBox;
import android.content.Context;
import android.graphics.Color;

public class NetworkView extends CheckBox {
    
    private Context ctx = null;
    Network net = null;

    public NetworkView( Network net, Context ctx ) {
	super(ctx);
	setText( net.getName( ) /*+ " (" + net.getFixedIP() + ")"*/ );
	this.net = net;
	//setTextSize(20,1);
	//setTextColor(Color.parseColor("#FFFFFF"));
    }
    
    public Network getNetwork( ) { return net; }
}
