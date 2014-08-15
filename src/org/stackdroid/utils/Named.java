package org.stackdroid.utils;

//import android.widget.ImageView;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.UserView;

public interface Named {
    public UserView getUserView( );
    public ServerView getServerView( );
    public OSImageView getOSImageView( );
    public NetworkView getNetworkView( );
    public FloatingIPView getFloatingIPView( );
};
