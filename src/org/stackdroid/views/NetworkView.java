package org.stackdroid.views;


import org.stackdroid.utils.Network;

import android.content.Context;
import android.widget.CheckBox;

public class NetworkView extends CheckBox {
    
    Network net = null;

    public NetworkView( Network net, OnClickListener listener, Context ctx ) {
	  super(ctx);
	  setOnClickListener(listener);
	  setText( net.getName( )+" ("+net.getSubNetworks()[0].getAddress()+")" );
	  this.net = net;
    }
    
    public Network getNetwork( ) { return net; }
    
}
