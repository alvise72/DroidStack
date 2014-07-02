package org.openstack.utils;

import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;

import org.openstack.views.UserView;
import org.openstack.views.ServerView;

public class TextViewNamed extends TextView implements Named {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;

    public TextViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public TextViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
};
