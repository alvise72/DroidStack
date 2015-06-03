package org.stackdroid.utils;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.ListSecGroupView;
import org.stackdroid.views.NetworkListView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.RouterView;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.UserView;
import org.stackdroid.views.VolumeView;

public interface GetView {
    
    public RuleView getRuleView( );
    public UserView getUserView( );
    public ServerView getServerView( );
    public OSImageView getOSImageView( );
    public NetworkView getNetworkView( );
    public ListSecGroupView getListSecGroupView( );
    public FloatingIPView getFloatingIPView( );
    public VolumeView getVolumeView( );
    public SecGroupView getSecGroupView( );
    public NetworkListView getNetworkListView( );
    public RouterView getRouterView( );
    
};
