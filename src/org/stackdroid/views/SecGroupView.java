package org.stackdroid.views;


import org.stackdroid.utils.SecGroup;
import android.widget.CheckBox;
import android.content.Context;

public class SecGroupView extends CheckBox {
    
    //private Context ctx = null;
    SecGroup sg = null;

    public SecGroupView( SecGroup sg, OnClickListener listener, Context ctx ) {
    	super(ctx);
    	setOnClickListener(listener);
    	setText( sg.getName( ) );
    	if(sg.getName().compareTo("default")==0)
    		setChecked(true);
    	else 
    		setChecked(false);
    	this.sg = sg;
    }
    
    public SecGroup getSecGroup( ) { return sg; }
}
