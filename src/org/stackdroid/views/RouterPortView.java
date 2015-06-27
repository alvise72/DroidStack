package org.stackdroid.views;



import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.Router;
import org.stackdroid.utils.RouterPort;
import org.stackdroid.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.stackdroid.R;

public class RouterPortView extends LinearLayout {
    
    RouterPort routerPort                      = null;
    private LinearLayoutWithView row           = null;
    private LinearLayoutWithView buttonsLayout = null;
    private LinearLayoutWithView nameLayout    = null;
    //TextView routerName                        = null;
    private ImageButtonWithView deleteButton   = null;
    //private ImageButtonWithView modifyButton   = null;
    //private ImageButtonWithView infoButton     = null;

    /**
     *
     *
     *
     *
     *
     *
     */
    public RouterView( Router router,
                       OnClickListener deleteListener,
                       OnClickListener modifyListener,
                       OnClickListener infoListener,
                       Context ctx )
    {
	    super(ctx);
        this.router = router;
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params1);
        int padding = Utils.getDisplayPixel(ctx, 2);
        setPadding(padding, padding, padding, padding);
        row = new LinearLayoutWithView( ctx, this );
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        row.setLayoutParams(_params1);
        row.setBackgroundResource(R.drawable.rounded_corner_thin);

        routerName = new TextView(ctx);
        String rname = router.getName();
        if(rname.length()>30) {
            rname = rname.substring(0,29) + "...";
        }
        routerName.setText(router.getName());
        routerName.setTextColor(Color.parseColor("#333333"));
        routerName.setTypeface(null, Typeface.BOLD);

        nameLayout = new LinearLayoutWithView( ctx, (RouterView)this );
        nameLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params2
                = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params2.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        nameLayout.setLayoutParams(params2);

        nameLayout.addView(routerName);

        buttonsLayout = new LinearLayoutWithView( ctx, (RouterView)this );
        buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
        LinearLayout.LayoutParams params4
                = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
        params4.gravity= Gravity.RIGHT|Gravity.CENTER_VERTICAL;
        buttonsLayout.setLayoutParams( params4 );
        buttonsLayout.setGravity(Gravity.RIGHT);

        infoButton = new ImageButtonWithView( ctx, (RouterView)this );
        infoButton.setImageResource(android.R.drawable.ic_dialog_info);
        infoButton.setOnClickListener(infoListener);

        deleteButton = new ImageButtonWithView( ctx, (RouterView)this );
        deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        deleteButton.setOnClickListener(deleteListener);

        modifyButton = new ImageButtonWithView( ctx, (RouterView)this );
        modifyButton.setImageResource(android.R.drawable.ic_menu_edit);
        modifyButton.setOnClickListener(modifyListener);

        buttonsLayout.addView(infoButton);
        buttonsLayout.addView(modifyButton);
        buttonsLayout.addView(deleteButton);


        row.addView(nameLayout);
        row.addView(buttonsLayout);
        addView(row);
    }

    /**
     *
     *
     *
     *
     *
     *
     */
    public Router getRouter( ) { return router; }
    
}
