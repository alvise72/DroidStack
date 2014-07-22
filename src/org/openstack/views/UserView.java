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

public class UserView extends LinearLayout {

    private Context ctx = null;

    private LinearLayoutNamed row            = null;
    private LinearLayoutNamed buttonsLayout  = null;
    private LinearLayoutNamed userLayout     = null;
    private TextViewNamed     textUserName   = null;
    private TextViewNamed     textTenantName = null;
    private TextViewNamed     textEndpoint   = null;
    // private ImageButtonNamed  modifyUser     = null;
    private ImageButtonNamed  deleteUser     = null;
    
    private User user = null;

    public UserView ( User U, Context ctx ) {
	super(ctx);

	user = U;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	setLayoutParams( params1 );
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );

	row = new LinearLayoutNamed( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);

	userLayout = new LinearLayoutNamed( ctx, this );
	userLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 
	    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	userLayout.setLayoutParams( params2 );
	
	textUserName = new TextViewNamed( ctx, (UserView)this );
	textUserName.setText("User: "+user.getUserName() );
	textUserName.setTextColor( Color.parseColor("#333333") );
	textUserName.setOnClickListener( (OnClickListener)ctx );
	textUserName.setTextColor( Color.parseColor("#BBBBBB"));

	textTenantName = new TextViewNamed( ctx, (UserView)this );
	textTenantName.setText("Tenant: "+user.getTenantName() );
	textTenantName.setTextColor( Color.parseColor("#333333") );
	textTenantName.setOnClickListener( (OnClickListener)ctx );
	textTenantName.setTextColor( Color.parseColor("#BBBBBB"));

	textEndpoint = new TextViewNamed( ctx, (UserView)this );
	textEndpoint.setText("Endpoint: "+U.getEndpoint( ));
	textEndpoint.setTextColor( Color.parseColor("#333333") );
	textEndpoint.setOnClickListener( (OnClickListener)ctx );
	textEndpoint.setTextColor( Color.parseColor("#BBBBBB"));

	

	userLayout.addView(textUserName);
	userLayout.addView(textTenantName);
	userLayout.addView(textEndpoint);
	userLayout.setOnClickListener( (OnClickListener)ctx );

	row.addView(userLayout);
	setOnClickListener( (OnClickListener)ctx );
      
	buttonsLayout = new LinearLayoutNamed( ctx, this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
	params3.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params3 );
	buttonsLayout.setGravity( Gravity.RIGHT|Gravity.CENTER_VERTICAL );
	
// 	modifyUser = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_MODIFY_USER );
// 	modifyUser.setImageResource(android.R.drawable.ic_menu_edit);
// 	modifyUser.setOnClickListener( (OnClickListener)ctx );
	
	deleteUser = new ImageButtonNamed( ctx, this, ImageButtonNamed.BUTTON_DELETE_USER );
	deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
	deleteUser.setOnClickListener( (OnClickListener)ctx );
	
	//	buttonsLayout.addView( modifyUser );
	buttonsLayout.addView( deleteUser );
	buttonsLayout.setOnClickListener( (OnClickListener)ctx );
	
	row.addView( buttonsLayout );
	addView( row );
    }

    public void setSelected( ) {
	textEndpoint.setTypeface( null, Typeface.BOLD );
	textUserName.setTypeface( null, Typeface.BOLD );
	textTenantName.setTypeface( null, Typeface.BOLD );
	textEndpoint.setTextColor( Color.parseColor("#00AA00") );
	textUserName.setTextColor( Color.parseColor("#00AA00") );
	textTenantName.setTextColor( Color.parseColor("#00AA00") );
    }

    public void setUnselected( ) {
	textEndpoint.setTypeface( null, Typeface.NORMAL );
	textUserName.setTypeface( null, Typeface.NORMAL );
	textTenantName.setTypeface( null, Typeface.NORMAL );
	textEndpoint.setTextColor( Color.parseColor("#BBBBBB") );
	textUserName.setTextColor( Color.parseColor("#BBBBBB") );
	textTenantName.setTextColor( Color.parseColor("#BBBBBB") );
    }

    public String getFilename( ) { return user.getUserID()+"."+user.getTenantID(); }
}
