package org.openstack.utils;


import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;

import android.content.Context;

public class UserView extends LinearLayout {

    private Context ctx = null;

    private LinearLayout buttonsLayout = null;
    private LinearLayout userLayout = null;
    private TextViewNamed textUserName = null;
    private TextViewNamed textEndpoint = null;
    private ImageButtonNamed modifyUser = null;
    private ImageButtonNamed deleteUser = null; 
    
    private String username = null;

    public UserView ( User U, Context ctx ) {
	super(ctx);

	username = U.getUserName( );
	
	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	setLayoutParams( params1 );
	
	userLayout = new LinearLayout( ctx );
	userLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
	userLayout.setLayoutParams( params2 );
	
	textUserName = new TextViewNamed( ctx, Named.TEXTVIEW, username );
	textUserName.setText(username);
	textUserName.setTextColor( Color.parseColor("#333333") );
	textUserName.setOnClickListener( (OnClickListener)ctx );
	textEndpoint = new TextViewNamed( ctx, Named.TEXTVIEW, username );
	textEndpoint.setText(U.getEndpoint( ));
	textEndpoint.setTextColor( Color.parseColor("#333333") );
	textEndpoint.setOnClickListener( (OnClickListener)ctx );

	userLayout.addView(textUserName);
	userLayout.addView(textEndpoint);
	addView(userLayout);
      
	buttonsLayout = new LinearLayout( ctx );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
	params3.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params3 );
	buttonsLayout.setGravity( Gravity.RIGHT );
	
	modifyUser = new ImageButtonNamed( ctx, Named.BUTTON_MODIFY_USER, username );
	modifyUser.setImageResource(android.R.drawable.ic_menu_edit);
	modifyUser.setOnClickListener( (OnClickListener)ctx );
	
	deleteUser = new ImageButtonNamed( ctx, Named.BUTTON_DELETE_USER, username );
	deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
	deleteUser.setOnClickListener( (OnClickListener)ctx );
	
	
	buttonsLayout.addView( modifyUser );
	buttonsLayout.addView( deleteUser );
	
	addView( buttonsLayout );
	
    }
}
