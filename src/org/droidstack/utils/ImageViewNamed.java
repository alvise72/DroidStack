package org.openstack.utils;

import android.widget.ImageView;
import android.content.Context;

import org.openstack.views.FloatingIPView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class ImageViewNamed extends ImageView implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private FloatingIPView relatedFloatingIPView = null;
    
    public ImageViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public ImageViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public ImageViewNamed( Context ctx, OSImageView sv ) {
	super( ctx );
	relatedOSImageView = sv;
    }

    public ImageViewNamed( Context ctx, NetworkView nv ) {
	super( ctx );
	relatedNetworkView = nv;
    }


    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
};




