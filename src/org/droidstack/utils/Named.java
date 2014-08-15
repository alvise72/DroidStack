package org.droidstack.utils;

//import android.widget.ImageView;

import org.droidstack.views.FloatingIPView;
import org.droidstack.views.NetworkView;
import org.droidstack.views.OSImageView;
import org.droidstack.views.ServerView;
import org.droidstack.views.UserView;

public interface Named {
    public UserView getUserView( );
    public ServerView getServerView( );
    public OSImageView getOSImageView( );
    public NetworkView getNetworkView( );
    public FloatingIPView getFloatingIPView( );
};
