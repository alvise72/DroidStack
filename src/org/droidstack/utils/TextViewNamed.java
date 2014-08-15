package org.droidstack.utils;

import android.widget.TextView;
import android.content.Context;
//import android.widget.ImageView;




import org.droidstack.views.FloatingIPView;
import org.droidstack.views.ListSecGroupView;
import org.droidstack.views.SecGroupView;
import org.droidstack.views.UserView;
import org.droidstack.views.ServerView;
import org.droidstack.views.OSImageView;
import org.droidstack.views.NetworkView;

public class TextViewNamed extends TextView implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private ListSecGroupView relatedListSecGroupView = null;
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
    public TextViewNamed( Context ctx, ListSecGroupView nv ) {
    	super( ctx );
    	relatedListSecGroupView = nv;
        }
    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public ListSecGroupView getSecGroupView( ) { return relatedListSecGroupView;}
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
};
