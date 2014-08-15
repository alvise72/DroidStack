package org.droidstack.utils;

import android.widget.LinearLayout;
import android.content.Context;

import org.droidstack.views.FloatingIPView;
import org.droidstack.views.ListSecGroupView;
import org.droidstack.views.UserView;
import org.droidstack.views.ServerView;
import org.droidstack.views.OSImageView;
import org.droidstack.views.NetworkView;

public class LinearLayoutNamed extends LinearLayout implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;

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
     
     public LinearLayoutNamed( Context ctx, FloatingIPView nv ) {
   	    super( ctx );
     	relatedFloatingIPView = nv;
     }   
        

     public LinearLayoutNamed( Context ctx, ListSecGroupView nv ) {
   	    super( ctx );
     	relatedListSecGroupView = nv;
     }
     
    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    
};