package org.openstack.utils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;

public class ImageButtonNamed extends ImageButton implements Named {

//     private int type;
//     private String extras;

    public static int BUTTON_MODIFY_USER = 1;
    public static int BUTTON_DELETE_USER = 2;
    private UserView relatedUserView = null;
    private int type;

    public ImageButtonNamed( Context ctx, UserView uv, int _type ) {
	super( ctx );relatedUserView = uv;
	
 	this.type = _type;
// 	this.extras = _extras;
    }

//     public int getType( ) { return type; }
//     public String getExtras( ) { return extras; }
    // public void setRelatedImage( ImageView iv ) { }
    // public ImageView getRelatedImage( ) { return null; }
    public UserView getUserView( ) { return relatedUserView; }
    public int getType( ) { return type; }
};
