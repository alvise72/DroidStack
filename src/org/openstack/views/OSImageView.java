package org.openstack.views;

import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.graphics.Typeface;
import android.graphics.Color;

import android.view.Gravity;
import android.view.View;

import android.content.Context;

import org.openstack.R;

import org.openstack.utils.*;

public class OSImageView extends LinearLayout {

    private Context ctx = null;

    private LinearLayoutNamed row           = null;
    private LinearLayoutNamed buttonsLayout = null;
    private LinearLayoutNamed nameLayout    = null;
    private TextViewNamed     textImageName = null;
    private TextViewNamed     textSize    = null;
    private ImageButtonNamed  launchImage   = null;
    private ImageButtonNamed  deleteImage   = null;
    
    private OSImage image = null;

    public OSImageView ( OSImage I, Context ctx ) {
	super(ctx);

	image = I;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	setLayoutParams( params1 );
	//setBackgroundResource(R.drawable.rounded_corner_thin);
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );
	setOnClickListener( (OnClickListener)ctx );

	row = new LinearLayoutNamed( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);

	nameLayout = new LinearLayoutNamed( ctx, (OSImageView)this );
	nameLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 
	    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	nameLayout.setLayoutParams( params2 );
	
	textImageName = new TextViewNamed( ctx, (OSImageView)this );
	String name = image.getName( );
	if(name.length()>20) {
	    name = name.substring(0,17) + "..";
	}
	textImageName.setText( name );
	textImageName.setTextColor( Color.parseColor("#333333") );
	textImageName.setTypeface( null, Typeface.BOLD );
	textImageName.setOnClickListener( (OnClickListener)ctx );
	
	textSize = new TextViewNamed( ctx, (OSImageView)this );
	textSize.setText( "Size: " + image.getSizeMB() + " MB" );
	textSize.setTextColor( Color.parseColor("#BBBBBB") );
	textSize.setOnClickListener( (OnClickListener)ctx );

	nameLayout.addView(textImageName);
	nameLayout.addView(textSize);
	nameLayout.setOnClickListener( (OnClickListener)ctx );
	
	row.addView(nameLayout);
	setOnClickListener( (OnClickListener)ctx );

	buttonsLayout = new LinearLayoutNamed( ctx, (OSImageView)this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params4 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
	params4.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params4 );
	buttonsLayout.setGravity( Gravity.RIGHT );
	buttonsLayout.setOnClickListener( (OnClickListener)ctx );

	launchImage = new ImageButtonNamed( ctx, (OSImageView)this, ImageButtonNamed.BUTTON_LAUNCH_IMAGE );
	launchImage.setImageResource(R.drawable.ic_menu_play_clip );
	launchImage.setOnClickListener( (OnClickListener)ctx );
	
	deleteImage = new ImageButtonNamed( ctx, (OSImageView)this, ImageButtonNamed.BUTTON_DELETE_IMAGE );
	deleteImage.setImageResource(android.R.drawable.ic_menu_delete);
	deleteImage.setOnClickListener( (OnClickListener)ctx );
	
	buttonsLayout.addView( launchImage );
	buttonsLayout.addView( deleteImage );
	buttonsLayout.setOnClickListener( (OnClickListener)ctx );
	
	row.addView( buttonsLayout );
	addView( row );
    }

    public OSImage getOSImage( ) { return image; }
}
