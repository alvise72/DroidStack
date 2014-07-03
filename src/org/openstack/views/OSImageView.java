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
    private LinearLayoutNamed formatLayout  = null;
    private TextViewNamed     textImageName = null;
    private TextViewNamed     textPublic    = null;
    private TextViewNamed     textFormatLabel = null;
    private TextViewNamed     textFormat    = null;
    private ImageButtonNamed  launchImage   = null;
    private ImageButtonNamed  deleteImage   = null;
    
    private Image image = null;

    public OSImageView ( Image I, Context ctx ) {
	super(ctx);

	image = I;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	setLayoutParams( params1 );
	setBackgroundResource(R.drawable.rounded_corner_thin);
	

	nameLayout = new LinearLayoutNamed( ctx, (OSImageView)this );
	nameLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 
	    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	nameLayout.setLayoutParams( params2 );
	
	textImageName = new TextViewNamed( ctx, (OSImageView)this );
	textImageName.setText( image.getName( ) );
	textImageName.setTextColor( Color.parseColor("#333333") );
	textImageName.setOnClickListener( (OnClickListener)ctx );
	textPublic = new TextViewNamed( ctx, (OSImageView)this );
	textPublic.setText("Public: " + (image.isPublic() ? "yes" : "no"));
	textPublic.setTextColor( Color.parseColor("#333333") );
	textPublic.setOnClickListener( (OnClickListener)ctx );
	textPublic.setTextColor( Color.parseColor("#BBBBBB"));
	textImageName.setTextColor( Color.parseColor("#BBBBBB"));
	
	nameLayout.addView(textImageName);
	nameLayout.addView(textPublic);
	nameLayout.setOnClickListener( (OnClickListener)ctx );

	addView(nameLayout);
	setOnClickListener( (OnClickListener)ctx );
      

	formatLayout = new LinearLayoutNamed( ctx, this );
	formatLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params3 
	    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	formatLayout.setLayoutParams( params3 );

	textFormatLabel = new TextViewNamed( ctx, (OSImageView)this );
	textFormatLabel.setText("Format");
	textFormatLabel.setTextColor( Color.parseColor("#333333") );
	textFormatLabel.setOnClickListener( (OnClickListener)ctx );
	textFormat = new TextViewNamed( ctx, (OSImageView)this );
	textFormat.setText( image.getFormat( ) );
	textFormat.setTextColor( Color.parseColor("#333333") );
	textFormat.setOnClickListener( (OnClickListener)ctx );

	formatLayout.addView( textFormatLabel );
	formatLayout.addView( textFormat );
	
	addView( formatLayout );

	buttonsLayout = new LinearLayoutNamed( ctx, (OSImageView)this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params4 
	    = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
	params3.gravity=Gravity.RIGHT;
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
