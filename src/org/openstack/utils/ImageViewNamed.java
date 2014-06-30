package org.openstack.utils;

import android.widget.ImageView;
import android.content.Context;

public class ImageViewNamed extends ImageView implements Named {

    private String extras;
    private int type;

    ImageViewNamed( Context ctx, int _type, String _extras ) {
	super( ctx );
	this.type = _type;
	this.extras = _extras;
    }

    public String getExtras( ) { return extras; }
    public int getType( ) { return type; }
    // public void setRelatedImage( ImageView iv ) { }
    // public ImageView getRelatedImage( ) { return null; }
};
