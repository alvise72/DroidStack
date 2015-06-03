package org.stackdroid.views;


import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Router;
import org.stackdroid.utils.Utils;

import android.widget.CheckBox;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.graphics.Color;

public class RouterView extends LinearLayout {
    
    Router router                             = null;
    private LinearLayoutWithView row          = null;
    TextView routerName                       = null;
    private ImageButtonWithView deleteButton  = null;
    private ImageButtonWithView modifyButton  = null;


    public RouterView( Router router, OnClickListener deleteListener, OnClickListener modifyListener, Context ctx ) {
	    super(ctx);
        //setOnClickListener(listener);
        //setText(router.getName());
	    this.router = router;
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params1);
        int padding = Utils.getDisplayPixel(ctx, 2);
        setPadding(padding, padding, padding, padding);
        row = new LinearLayoutWithView( ctx, this );
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        row.setLayoutParams( _params1 );
        row.setBackgroundResource(R.drawable.rounded_corner_thin);

        routerName = new TextView(ctx);
        routerName.setText(router.getName());
        row.addView(routerName);
        addView(row);
    }
    
    public Router getRouter( ) { return router; }
    
}
