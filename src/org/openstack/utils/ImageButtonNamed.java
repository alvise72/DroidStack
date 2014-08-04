package org.openstack.utils;

import android.widget.ImageButton;
import android.content.Context;

import org.openstack.views.FloatingIPView;
import org.openstack.views.ListSecGroupView;
import org.openstack.views.SecGroupView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class ImageButtonNamed extends ImageButton implements Named {

    public static final int BUTTON_MODIFY_USER   = 1;
    public static final int BUTTON_DELETE_USER   = 2;
    public static final int BUTTON_DELETE_SERVER = 3;
    public static final int BUTTON_DELETE_IMAGE  = 4;
    public static final int BUTTON_LAUNCH_IMAGE  = 5;
    public static final int BUTTON_SNAP_SERVER   = 6;
    public static final int BUTTON_RELEASE_IP    = 7;
    public static final int BUTTON_DISSOCIATE_IP = 8;
    public static final int BUTTON_DELETE_SECGRP = 9;
    public static final int BUTTON_CONSOLE_LOG   = 10;
	
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    

    private int type;

    public ImageButtonNamed( Context ctx, UserView uv, int _type ) {
	super( ctx );
	relatedUserView = uv;
 	this.type = _type;
    }

    public ImageButtonNamed( Context ctx, ServerView sv, int _type ) {
	super( ctx );
	relatedServerView = sv;
 	this.type = _type;
    }

    public ImageButtonNamed( Context ctx, OSImageView iv, int _type ) {
	super( ctx );
	relatedOSImageView = iv;
 	this.type = _type;
    }

    public ImageButtonNamed( Context ctx, NetworkView iv, int _type ) {
	super( ctx );
	relatedNetworkView = iv;
 	this.type = _type;
    }

    public ImageButtonNamed( Context ctx, FloatingIPView iv, int _type ) {
	super( ctx );
	relatedFloatingIPView = iv;
 	this.type = _type;
    }

    public ImageButtonNamed( Context ctx, ListSecGroupView sv, int _type ) {
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
