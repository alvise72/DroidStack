package org.stackdroid.views;

import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonNamed;
import org.stackdroid.utils.LinearLayoutNamed;
import org.stackdroid.utils.SimpleSecGroupRule;
import org.stackdroid.utils.TextViewNamed;
import org.stackdroid.utils.Utils;

public class RuleView extends LinearLayout {
    
    private LinearLayoutNamed row       = null;
    private LinearLayoutNamed text      = null;
    private LinearLayoutNamed info      = null;
    private TextViewNamed ruleInfo      = null;
    private TextViewNamed ruleInfo2     = null;
    private TextViewNamed ruleInfo3     = null;
    
    
    private ImageButtonNamed deleteRule = null;
    
    private SimpleSecGroupRule Rl = null;

    public RuleView( SimpleSecGroupRule r, Context ctx ) {
    	super(ctx);
    	Rl = r;
	
    	setOrientation( LinearLayout.HORIZONTAL );
    	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	setLayoutParams( params1 );
    	int padding = Utils.getDisplayPixel( ctx, 2 );
    	setPadding( padding, padding, padding, padding );
	
    	row = new LinearLayoutNamed( ctx, this );
    	row.setOrientation( LinearLayout.HORIZONTAL );
    	LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	row.setLayoutParams( _params1 );
    	row.setBackgroundResource( R.drawable.rounded_corner_thin );

    	text = new LinearLayoutNamed( ctx, this );
    	text.setOrientation( LinearLayout.VERTICAL );
    	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
    	text.setLayoutParams( params2 );

    	ruleInfo = new TextViewNamed( ctx, this );
    	ruleInfo.setText("IPv4 Proto: " + Rl.getProtocol( ).toUpperCase() );
    	ruleInfo.setTextColor( Color.parseColor("#333333") );
    	//ruleInfo.setOnClickListener( (OnClickListener)ctx );
    	ruleInfo.setTypeface( null, Typeface.BOLD );
    	

    	ruleInfo2 = new TextViewNamed( ctx, this );
    	ruleInfo2.setText("IP Range: " + Rl.getIPRange( ) );
    	ruleInfo2.setTextColor( Color.parseColor("#333333") );
    	//ruleInfo2.setOnClickListener( (OnClickListener)ctx );
    	//ruleInfo2.setTypeface( null, Typeface.BOLD );
    	

    	ruleInfo3 = new TextViewNamed( ctx, this );
    	//Log.d("RULEVIEW", "Rl="+Rl.to_string());
    	ruleInfo3.setText("Port range: " + Rl.getFromPort()+"/"+Rl.getToPort() + (Rl.getProtoName()!=null && Rl.getProtoName().length()!=0 ? " ("+Rl.getProtoName()+")" : "") );
    	ruleInfo3.setTextColor( Color.parseColor("#333333") );
    	//ruleInfo3.setOnClickListener( (OnClickListener)ctx );
    	//ruleInfo3.setTypeface( null, Typeface.BOLD );
    	
    	text.addView(ruleInfo);
    	text.addView(ruleInfo2);
    	text.addView(ruleInfo3);
    	
    	//text.setOnClickListener( (OnClickListener)ctx );
    	row.addView(text);
    	//setOnClickListener( (OnClickListener)ctx );

    	deleteRule = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_RULE );
    	deleteRule.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    	deleteRule.setOnClickListener( (OnClickListener)ctx );

    	info = new LinearLayoutNamed( ctx, this );
    	info.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams params3 = 
	    new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2f);
		info.setLayoutParams( params3 );
		info.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		
		info.addView( deleteRule );
	
		row.addView( info );
		addView( row );
    		
    }

    public SimpleSecGroupRule getRule( ) { return Rl; }
    
}
