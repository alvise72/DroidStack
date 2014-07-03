package org.openstack.utils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;

import org.openstack.views.UserView;
import org.openstack.views.ServerView;
import org.openstack.views.OSImageView;

public class ImageButtonNamed extends ImageButton implements Named {

    public static int BUTTON_MODIFY_USER = 1;
    public static int BUTTON_DELETE_USER = 2;
    public static int BUTTON_DELETE_SERVER = 3;
    public static int BUTTON_DELETE_IMAGE = 4;
    public static int BUTTON_LAUNCH_IMAGE = 4;

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
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

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public int getType( ) { return type; }
};
