package org.openstack.utils;

//import android.widget.ImageView;

import org.openstack.views.FloatingIPView;
import org.openstack.views.NetworkView;
import org.openstack.views.OSImageView;
import org.openstack.views.ServerView;
import org.openstack.views.UserView;

public interface Named {
    public UserView getUserView( );
    public ServerView getServerView( );
    public OSImageView getOSImageView( );
    public NetworkView getNetworkView( );
    public FloatingIPView getFloatingIPView( );
};
