package org.openstack.utils;

import android.widget.EditText;
import android.content.Context;
//import android.widget.ImageView;


import org.openstack.views.FloatingIPView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class EditTextNamed extends EditText implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private FloatingIPView relatedFloatingIPView = null;
    
    public EditTextNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public EditTextNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public EditTextNamed( Context ctx, OSImageView sv ) {
	super( ctx );
	relatedOSImageView = sv;
    }

    public EditTextNamed( Context ctx, NetworkView nv ) {
	super( ctx );
	relatedNetworkView = nv;
    }


    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    
};
