package org.openstack.utils;

import android.widget.ImageView;
import android.content.Context;

public class ImageViewNamed extends ImageView implements Named {

    private String name = null;

    ImageViewNamed( Context ctx, String name ) {
	super( ctx );
	this.name = name;
    }

    public String getName( ) { return name; }

};
