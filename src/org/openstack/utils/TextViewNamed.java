package org.openstack.utils;

import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;

public class TextViewNamed extends TextView implements Named {

//     private String extras;
//     private int type;
    //private ImageView relatedImage = null;
    //    private TextViewNamed relatedText = null;

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;

    TextViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    TextViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
};
