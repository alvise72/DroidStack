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

    TextViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
// 	this.type = _type;
// 	this.extras = _extras;
    }

//     public String getExtras( ) { return extras; }
//     public int getType( ) { return type; }
//     public TextViewNamed getRelatedTextViewNamed( ) { return relatedText; }
//     public void setRelatedTextViewNamed( TextViewNamed tv ) { relatedText = tv; }
    // public void setRelatedImage( ImageView iv ) { relatedImage = iv; }
    // public ImageView getRelatedImage( ) { return relatedImage; }

    public UserView getUserView( ) { return relatedUserView; }

};
