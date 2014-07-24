package org.openstack.utils;

import android.widget.TextView;
import android.content.Context;
//import android.widget.ImageView;


import org.openstack.views.FloatingIPView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class TextViewNamed extends TextView implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private FloatingIPView relatedFloatingIPView = null;

    public TextViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public TextViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public TextViewNamed( Context ctx, OSImageView sv ) {
	super( ctx );
	relatedOSImageView = sv;
    }

    public TextViewNamed( Context ctx, NetworkView nv ) {
	super( ctx );
	relatedNetworkView = nv;
    }

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
};
