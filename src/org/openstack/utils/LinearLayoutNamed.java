package org.openstack.utils;

import android.widget.LinearLayout;
import android.content.Context;
import org.openstack.views.UserView;
import org.openstack.views.ServerView;

public class LinearLayoutNamed extends LinearLayout implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;

    public LinearLayoutNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public LinearLayoutNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
};
