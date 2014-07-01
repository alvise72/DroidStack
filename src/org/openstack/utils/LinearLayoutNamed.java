package org.openstack.utils;

import android.widget.LinearLayout;
import android.content.Context;

public class LinearLayoutNamed extends LinearLayout implements Named {

//     private int type;
//     private String extras;

    private UserView relatedUserView = null;

    public LinearLayoutNamed( Context ctx, UserView uv ) {
	super( ctx );
// 	this.type = _type;
// 	this.extras = _extras;
	relatedUserView = uv;
    }

//     public int getType( ) { return type; }
//     public String getExtras( ) { return extras; }
//     public TextViewNamed getRelatedTextViewNamed( ) { return relatedText; }
//     public void setRelatedTextViewNamed( TextViewNamed tv ) { relatedText = tv; }
    // public void setRelatedImage( ImageView iv ) { }
    // public ImageView getRelatedImage( ) { return null; } 
    public UserView getUserView( ) { return relatedUserView; }
};
