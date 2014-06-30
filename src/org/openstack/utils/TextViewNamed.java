package org.openstack.utils;

import android.widget.TextView;
import android.content.Context;

public class TextViewNamed extends TextView implements Named {

    private String extras;// = null;
    private int type;

    TextViewNamed( Context ctx, int _type, String _extras ) {
	super( ctx );
	this.type = _type;
	this.extras = _extras;
    }

    public String getExtras( ) { return extras; }
    public int getType( ) { return type; }

};
