package org.openstack.utils;

import android.widget.Button;
import android.content.Context;

import org.openstack.views.FloatingIPView;
import org.openstack.views.ListSecGroupView;
import org.openstack.views.SecGroupView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class ButtonNamed extends Button implements Named {

    public static final int BUTTON_CONSOLE_LOG   = 1;
    
	
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    

    private int type;

    public ButtonNamed( Context ctx, UserView uv, int _type ) {
	super( ctx );
	relatedUserView = uv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, ServerView sv, int _type ) {
	super( ctx );
	relatedServerView = sv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, OSImageView iv, int _type ) {
	super( ctx );
	relatedOSImageView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, NetworkView iv, int _type ) {
	super( ctx );
	relatedNetworkView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, FloatingIPView iv, int _type ) {
	super( ctx );
	relatedFloatingIPView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, ListSecGroupView sv, int _type ) {
    	super( ctx );
    	relatedListSecGroupView = sv;
     	this.type = _type;
        }
    
    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public ListSecGroupView getSecGroupView( ) { return relatedListSecGroupView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    public int getType( ) { return type; }
};
