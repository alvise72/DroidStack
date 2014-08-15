package org.droidstack.utils;

import android.widget.EditText;
import android.content.Context;
//import android.widget.ImageView;


import org.droidstack.views.FloatingIPView;
import org.droidstack.views.UserView;
import org.droidstack.views.ServerView;
import org.droidstack.views.OSImageView;
import org.droidstack.views.NetworkView;

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
