package org.stackdroid.views;


import org.stackdroid.utils.Network;
import org.stackdroid.utils.SubNetwork;

import android.content.Context;
import android.widget.CheckBox;

public class NetworkView extends CheckBox {
    
    Network    net 	  = null;
    SubNetwork subnet = null;
    
    public NetworkView( Network net, SubNetwork subnet, OnClickListener listener, Context ctx ) {
	  super(ctx);
	  setOnClickListener(listener);
	  if(net.getSubNetworks().size()>0)
		  setText( net.getName( )+" ("+subnet.getAddress()+")" );
	  this.net = net;
	  this.subnet = subnet;
    }
    
    public Network getNetwork( ) { return net; }
    public SubNetwork getSubNetwork( ) { return subnet; }
}
