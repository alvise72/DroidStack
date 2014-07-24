package org.openstack.utils;

import android.widget.ImageButton;
//import android.widget.ImageView;
import android.content.Context;

import org.openstack.views.FloatingIPView;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;
import org.openstack.views.NetworkView;

public class ImageButtonNamed extends ImageButton implements Named {

    public static int BUTTON_MODIFY_USER = 1;
    public static int BUTTON_DELETE_USER = 2;
    public static int BUTTON_DELETE_SERVER = 3;
    public static int BUTTON_DELETE_IMAGE = 4;
    public static int BUTTON_LAUNCH_IMAGE = 5;
    public static int BUTTON_SNAP_SERVER = 6;
    public static int BUTTON_RELEASE_IP = 7;

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
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

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    public int getType( ) { return type; }
};
