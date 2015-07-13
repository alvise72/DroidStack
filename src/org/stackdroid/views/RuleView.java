package org.stackdroid.views;

import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.ImageButtonWithView;
import org.stackdroid.utils.LinearLayoutWithView;
import org.stackdroid.utils.SimpleSecGroupRule;
import org.stackdroid.utils.TextViewWithView;
import org.stackdroid.utils.Utils;

public class RuleView extends LinearLayout {
    
    private LinearLayoutWithView row       = null;
    private LinearLayoutWithView text      = null;
    private LinearLayoutWithView info      = null;
    private TextViewWithView ruleInfo      = null;
    private TextViewWithView ruleInfo2     = null;
    private TextViewWithView ruleInfo3     = null;
    
    
    private ImageButtonWithView deleteRule = null;
    
    private SimpleSecGroupRule Rl = null;

    public RuleView( SimpleSecGroupRule r, OnClickListener deleteRuleListener, Context ctx ) {
    	super(ctx);
    	Rl = r;
	
    	setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	setLayoutParams(params1);
    	int padding = Utils.getDisplayPixel( ctx, 2 );
    	setPadding( padding, padding, padding, padding );
	
    	row = new LinearLayoutWithView( ctx, this );
    	row.setOrientation(LinearLayout.HORIZONTAL);
    	LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    	row.setLayoutParams(_params1);
    	row.setBackgroundResource(R.drawable.rounded_corner_thin);

    	text = new LinearLayoutWithView( ctx, this );
    	text.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
    	text.setLayoutParams(params2);

    	ruleInfo = new TextViewWithView( ctx, this );
    	ruleInfo.setText("IPv4 Proto: " + Rl.getProtocol().toUpperCase());
    	ruleInfo.setTextColor(Color.parseColor("#333333"));
    	ruleInfo.setTypeface(null, Typeface.BOLD);
    	

    	ruleInfo2 = new TextViewWithView( ctx, this );
    	ruleInfo2.setText("IP Range: " + Rl.getIPRange());
    	ruleInfo2.setTextColor( Color.parseColor("#333333") );
    	

    	ruleInfo3 = new TextViewWithView( ctx, this );
		if(Rl.getFromPort() != Rl.getToPort())
	    	ruleInfo3.setText("Port range: " + Rl.getFromPort()+"/"+Rl.getToPort() + (Rl.getProtoName()!=null && Rl.getProtoName().length()!=0 ? " ("+Rl.getProtoName()+")" : "") );
		else
			ruleInfo3.setText("Port: " + Rl.getFromPort() + (Rl.getProtoName()!=null && Rl.getProtoName().length()!=0 ? " ("+Rl.getProtoName()+")" : "") );
		ruleInfo3.setTextColor( Color.parseColor("#333333") );
    	
    	text.addView(ruleInfo);
    	text.addView(ruleInfo2);
    	text.addView(ruleInfo3);
    	
    	row.addView(text);

    	deleteRule = new ImageButtonWithView( ctx, this );
    	deleteRule.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
    	deleteRule.setOnClickListener( deleteRuleListener );

    	info = new LinearLayoutWithView( ctx, this );
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
