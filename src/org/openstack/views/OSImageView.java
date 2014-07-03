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

    private LinearLayoutNamed buttonsLayout = null;
    private LinearLayoutNamed nameLayout    = null;
    private TextViewNamed     textImageName = null;
    private TextViewNamed     textPublic    = null;
    private TextViewNamed     textFormat    = null;
    private ImageButtonNamed  launchImage   = null;
    private ImageButtonNamed  deleteImage   = null;
    
    private OSImage image = null;

    public OSImageView ( OSImage I, Context ctx ) {
	super(ctx);

	image = I;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	setLayoutParams( params1 );
	setBackgroundResource(R.drawable.rounded_corner_thin);
	

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
	textPublic = new TextViewNamed( ctx, (OSImageView)this );
	textPublic.setText("Public: " + (image.isPublic() ? "yes" : "no"));
	textPublic.setTextColor( Color.parseColor("#333333") );
	textPublic.setOnClickListener( (OnClickListener)ctx );
	textPublic.setTextColor( Color.parseColor("#BBBBBB"));
	
	textFormat = new TextViewNamed( ctx, (OSImageView)this );
	textFormat.setText( "Format: "+image.getFormat( ) + ", Size: " + image.getSizeMB() + " MB" );
	textFormat.setTextColor( Color.parseColor("#BBBBBB") );
	textFormat.setOnClickListener( (OnClickListener)ctx );

	nameLayout.addView(textImageName);
	nameLayout.addView(textPublic);
	nameLayout.setOnClickListener( (OnClickListener)ctx );
	nameLayout.addView( textFormat );
	
	addView(nameLayout);
	setOnClickListener( (OnClickListener)ctx );
      

	buttonsLayout = new LinearLayoutNamed( ctx, (OSImageView)this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params4 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
	params4.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params4 );
	buttonsLayout.setGravity( Gravity.RIGHT );
	
	launchImage = new ImageButtonNamed( ctx, (OSImageView)this, ImageButtonNamed.BUTTON_LAUNCH_IMAGE );
	launchImage.setImageResource(R.drawable.ic_menu_play_clip );
	launchImage.setOnClickListener( (OnClickListener)ctx );
	
	deleteImage = new ImageButtonNamed( ctx, (OSImageView)this, ImageButtonNamed.BUTTON_DELETE_IMAGE );
	deleteImage.setImageResource(android.R.drawable.ic_menu_delete);
	deleteImage.setOnClickListener( (OnClickListener)ctx );
	
	buttonsLayout.addView( launchImage );
	buttonsLayout.addView( deleteImage );
	buttonsLayout.setOnClickListener( (OnClickListener)ctx );
	
	addView( buttonsLayout );
	
    }
}
