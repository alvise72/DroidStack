package org.stackdroid.views;

//import android.widget.LinearLayout.LayoutParams;
import android.text.TextUtils;
import android.widget.LinearLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;

import android.graphics.Typeface;
import android.graphics.Color;

import android.view.Gravity;
//import android.view.View;

import android.content.Context;

import org.stackdroid.R;

import org.stackdroid.utils.*;

public class OSImageView extends LinearLayout {

    //private Context ctx = null;

    private LinearLayoutWithView row           = null;
    private LinearLayoutWithView buttonsLayout = null;
    private LinearLayoutWithView nameLayout    = null;
    private TextViewWithView     textImageName = null;
    private TextViewWithView     textSize      = null;
    private TextViewWithView     testStatus    = null;
    private ImageButtonWithView  launchImage   = null;
    private ImageButtonWithView  deleteImage   = null;
    
    private OSImage image = null;

    public OSImageView ( OSImage I, 
    					 OnClickListener infoListener,
    					 OnClickListener launchListener,
    					 OnClickListener deleteListener,
    					 Context ctx ) {
	super(ctx);

	image = I;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	setLayoutParams( params1 );
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );
	setOnClickListener( infoListener );

	row = new LinearLayoutWithView( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);

	nameLayout = new LinearLayoutWithView( ctx, (OSImageView)this );
	nameLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.6f);
	nameLayout.setLayoutParams( params2 );
	//nameLayout.setWeightSum(1);
	
	textImageName = new TextViewWithView( ctx, (OSImageView)this );
	String name = image.getName( );
	/* if(name.length()>20) {
	    name = name.substring(0,17) + "..";
	}*/
	textImageName.setText( name );
	textImageName.setTextColor( Color.parseColor("#333333") );
	textImageName.setTypeface(null, Typeface.BOLD);
	textImageName.setOnClickListener(infoListener);
	textImageName.setEllipsize(TextUtils.TruncateAt.END);
	textImageName.setSingleLine();
	
	textSize = new TextViewWithView( ctx, (OSImageView)this );
	textSize.setText( "Size: " + image.getSizeMB() + " MB" );
	textSize.setTextColor( Color.parseColor("#BBBBBB") );
	textSize.setOnClickListener( infoListener );

	testStatus = new TextViewWithView( ctx, (OSImageView)this );
	testStatus.setText( "Status: " + image.getStatus() );
	testStatus.setTextColor( Color.parseColor("#BBBBBB") );
	testStatus.setOnClickListener( infoListener );

	nameLayout.addView(textImageName);
	nameLayout.addView(textSize);
	nameLayout.addView(testStatus);
	nameLayout.setOnClickListener( infoListener );
	
	row.addView(nameLayout);
	setOnClickListener( infoListener );

	buttonsLayout = new LinearLayoutWithView( ctx, (OSImageView)this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params4 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f );
	params4.gravity=Gravity.RIGHT|Gravity.CENTER_VERTICAL;
	buttonsLayout.setLayoutParams( params4 );
	buttonsLayout.setGravity( Gravity.RIGHT );
	buttonsLayout.setOnClickListener( infoListener );
	buttonsLayout.setWeightSum(1);

	launchImage = new ImageButtonWithView( ctx, (OSImageView)this );
	launchImage.setImageResource(R.drawable.ic_menu_play_clip );
	launchImage.setOnClickListener( launchListener );
	
	deleteImage = new ImageButtonWithView( ctx, (OSImageView)this );
	deleteImage.setImageResource(android.R.drawable.ic_menu_delete);
	deleteImage.setOnClickListener( deleteListener );
	
	buttonsLayout.addView( launchImage );
	buttonsLayout.addView( deleteImage );
	buttonsLayout.setOnClickListener( infoListener );
	
	row.addView( buttonsLayout );
	addView( row );
    }

    public OSImage getOSImage( ) { return image; }
}
