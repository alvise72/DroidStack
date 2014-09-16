package org.stackdroid.views;


import org.stackdroid.utils.Router;
import android.widget.CheckBox;
import android.content.Context;
//import android.graphics.Color;

public class RouterView extends CheckBox {
    
    Router router = null;

    public RouterView( Router router, OnClickListener listener, Context ctx ) {
	  super(ctx);
	  setOnClickListener(listener);
	  setText( router.getName( ) );
	  this.router = router;
    }
    
    public Router getRouter( ) { return router; }
    
}
