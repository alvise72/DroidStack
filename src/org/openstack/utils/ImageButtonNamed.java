package org.openstack.utils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Context;

public class ImageButtonNamed extends ImageButton implements Named {

    private int type;
    private String extras;

    public ImageButtonNamed( Context ctx, int _type, String _extras ) {
	super( ctx );
	this.type = _type;
	this.extras = _extras;
    }

    public int getType( ) { return type; }
    public String getExtras( ) { return extras; }
    // public void setRelatedImage( ImageView iv ) { }
    // public ImageView getRelatedImage( ) { return null; }
};
