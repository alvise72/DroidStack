package org.openstack.utils;

import android.widget.LinearLayout;
import android.content.Context;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class LinearLayoutNamed extends LinearLayout implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;

    public LinearLayoutNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public LinearLayoutNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

     public LinearLayoutNamed( Context ctx, OSImageView sv ) {
	super( ctx );
	relatedOSImageView = sv;
    }   

     public LinearLayoutNamed( Context ctx, NetworkView nv ) {
	super( ctx );
	relatedNetworkView = nv;
    }   

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }

};
