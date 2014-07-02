package org.openstack.utils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;

public class ImageButtonNamed extends ImageButton implements Named {

    public static int BUTTON_MODIFY_USER = 1;
    public static int BUTTON_DELETE_USER = 2;
    public static int BUTTON_DELETE_SERVER = 3;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
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

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public int getType( ) { return type; }
};
