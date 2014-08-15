package org.openstack.views;


import org.openstack.utils.SecGroup;
import android.widget.CheckBox;
import android.content.Context;

public class SecGroupView extends CheckBox {
    
    //private Context ctx = null;
    SecGroup sg = null;

    public SecGroupView( SecGroup sg, Context ctx ) {
	super(ctx);
	setText( sg.getName( ) );
	if(sg.getName().compareTo("default")==0)
	    setChecked(true);
	else 
	    setChecked(false);
	    
	this.sg = sg;
    }
    
    public SecGroup getSecGroup( ) { return sg; }
}
