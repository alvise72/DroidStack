package org.openstack.utils;

import android.widget.ImageView;
import android.content.Context;

public class ImageViewNamed extends ImageView implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;

    ImageViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }
    ImageViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
};




