package org.openstack;

import android.widget.TextView;
import android.content.Context;

public class TextViewNamed extends TextView implements Named {

    private String name = null;

    TextViewNamed( Context ctx, String name ) {
	super( ctx );
	this.name = name;
    }

    public String getName( ) { return name; }

};
