package org.stackdroid.views;

import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class UserView extends LinearLayout {

    private LinearLayoutWithView row            = null;
    private LinearLayoutWithView buttonsLayout  = null;
    private LinearLayoutWithView userLayout     = null;
    private TextViewWithView     textUserName   = null;
    private TextViewWithView     textTenantName = null;
    private TextViewWithView     textEndpoint   = null;
    private TextViewWithView	  textSSL        = null;

    private ImageButtonWithView  deleteUser     = null;
    
    private User user = null;

    public User getUser( ) { return user; } 
    
    public UserView ( User U, OnClickListener deleteUserListener, OnClickListener selectUserListener, Context ctx ) {
	super(ctx);

	user = U;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	setLayoutParams( params1 );
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );

	row = new LinearLayoutWithView( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1
	    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);

	userLayout = new LinearLayoutWithView( ctx, this );
	userLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 
	    = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	userLayout.setLayoutParams( params2 );
	
	textUserName = new TextViewWithView( ctx, (UserView)this );
	textUserName.setText("User: "+user.getUserName() );
	textUserName.setTextColor( Color.parseColor("#333333") );
	textUserName.setOnClickListener( selectUserListener );
	textUserName.setTextColor( Color.parseColor("#BBBBBB"));

	textTenantName = new TextViewWithView( ctx, (UserView)this );
	textTenantName.setText("Tenant: "+user.getTenantName() );
	textTenantName.setTextColor( Color.parseColor("#333333") );
	textTenantName.setOnClickListener( selectUserListener );
	textTenantName.setTextColor( Color.parseColor("#BBBBBB"));

	textEndpoint = new TextViewWithView( ctx, (UserView)this );
	textEndpoint.setText("Endpoint: "+U.getEndpoint( ));
	textEndpoint.setTextColor( Color.parseColor("#333333") );
	textEndpoint.setOnClickListener( selectUserListener );
	textEndpoint.setTextColor( Color.parseColor("#BBBBBB"));

	textSSL = new TextViewWithView( ctx, (UserView)this );
	textSSL.setText("SSL: ");
	if(U.useSSL()==true) {
		textSSL.setTextColor(Color.parseColor("#FF0000"));
		textSSL.setText("ssl: yes");
		textSSL.setTypeface( null, Typeface.BOLD );
	} else {
		textSSL.setTextColor( Color.parseColor("#BBBBBB"));
		textSSL.setText("ssl: no");
		textSSL.setTypeface( null, Typeface.NORMAL );
	}

	userLayout.addView(textUserName);
	userLayout.addView(textTenantName);
	userLayout.addView(textEndpoint);
	userLayout.addView(textSSL);
	userLayout.setOnClickListener( selectUserListener );

	row.addView(userLayout);
	setOnClickListener( selectUserListener );
      
	buttonsLayout = new LinearLayoutWithView( ctx, this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
	params3.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params3 );
	buttonsLayout.setGravity( Gravity.RIGHT|Gravity.CENTER_VERTICAL );
	
	deleteUser = new ImageButtonWithView( ctx, this );
	deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
	deleteUser.setOnClickListener( deleteUserListener );
	
	buttonsLayout.addView( deleteUser );
//	buttonsLayout.setOnClickListener( delete );
	
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
