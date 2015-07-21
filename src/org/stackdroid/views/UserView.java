package org.stackdroid.views;

import android.text.TextUtils;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.Gravity;
import android.content.Context;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class UserView extends LinearLayout {

    private LinearLayoutWithView row                  = null;
    private LinearLayoutWithView buttonsLayout        = null;
    private LinearLayoutWithView userLayout           = null;
    private TextViewWithView     textUserName         = null;
    private TextViewWithView     textTenantName       = null;
    private TextViewWithView     textEndpoint         = null;
    private TextViewWithView	 textSSL              = null;
	private TextViewWithView     textVerifyServerCert = null;

    private ImageButtonWithView  deleteUser           = null;
    private ImageButtonWithView  infoUser             = null;
    
    private User user = null;

    public User getUser( ) { return user; } 
    
    public UserView ( User U, OnClickListener deleteUserListener, OnClickListener selectUserListener, OnClickListener infoUserListener, Context ctx ) {
	super(ctx);

	user = U;

	setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	setLayoutParams( params1 );
	int padding = Utils.getDisplayPixel( ctx, 2 );
	setPadding( padding, padding, padding, padding );

	row = new LinearLayoutWithView( ctx, this );
	row.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams _params1 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	row.setLayoutParams( _params1 );
	row.setBackgroundResource(R.drawable.rounded_corner_thin);

	userLayout = new LinearLayoutWithView( ctx, this );
	userLayout.setOrientation( LinearLayout.VERTICAL );
	LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.6f);
	userLayout.setLayoutParams( params2 );
	
	textUserName = new TextViewWithView( ctx, (UserView)this );
	String uname = "User: "+user.getUserName() ;
	/*if(uname.length()>30) {
		uname = uname.substring(0, 26) + "...";
	}*/
	textUserName.setText(uname);
	textUserName.setTextColor(Color.parseColor("#333333"));
	textUserName.setOnClickListener(selectUserListener);
	textUserName.setTextColor( Color.parseColor("#BBBBBB"));
	textUserName.setEllipsize(TextUtils.TruncateAt.END);
	textUserName.setSingleLine();

	textTenantName = new TextViewWithView( ctx, (UserView)this );
	String tname = "Tenant: "+user.getTenantName();
		//TextUtils.ellipsize( )
	/*if(tname.length()>30) {
		tname = tname.substring(0, 26) + "...";
	}*/
	textTenantName.setText(tname);
	textTenantName.setTextColor( Color.parseColor("#333333") );
	textTenantName.setOnClickListener(selectUserListener);
	textTenantName.setTextColor( Color.parseColor("#BBBBBB"));
		textTenantName.setEllipsize(TextUtils.TruncateAt.END);
		textTenantName.setSingleLine();

	textEndpoint = new TextViewWithView( ctx, (UserView)this );
	String ename = "Endpoint: "+U.getIdentityHostname( );
	/*if(ename.length()>30) {

		ename = ename.substring(0, 26)+"...";
	}*/
	textEndpoint.setText(ename);
	textEndpoint.setTextColor(Color.parseColor("#333333"));
	textEndpoint.setOnClickListener(selectUserListener);
	textEndpoint.setTextColor(Color.parseColor("#BBBBBB"));
		textEndpoint.setEllipsize(TextUtils.TruncateAt.END);
		textEndpoint.setSingleLine();

	textSSL = new TextViewWithView( ctx, (UserView)this );
	//textSSL.setText("SSL: ");
	textSSL.setOnClickListener(selectUserListener);
	if(U.useSSL()==true) {
		textSSL.setTextColor(Color.parseColor("#FF0000"));
		textSSL.setText("SSL: yes");
		textSSL.setTypeface( null, Typeface.BOLD );
	} else {
		textSSL.setTextColor( Color.parseColor("#BBBBBB"));
		textSSL.setText("SSL: no");
		textSSL.setTypeface( null, Typeface.NORMAL );
	}

		textVerifyServerCert = new TextViewWithView( ctx, (UserView)this );
		textVerifyServerCert.setOnClickListener(selectUserListener);
		if(U.getVerifyServerCert()) { //  secure
			textVerifyServerCert.setTextColor(Color.parseColor("#FF0000"));
			textVerifyServerCert.setText("Verify server's certificate: yes");
			textVerifyServerCert.setTypeface(null, Typeface.BOLD);
		} else { // Insecure
			textVerifyServerCert.setTextColor( Color.parseColor("#BBBBBB"));
			textVerifyServerCert.setText("Verify server's certificate: no");
			textVerifyServerCert.setTypeface( null, Typeface.NORMAL );
		}
		userLayout.addView(textUserName);
		userLayout.addView(textTenantName);
		userLayout.addView(textEndpoint);
		userLayout.addView(textSSL);
		userLayout.addView(textVerifyServerCert);
		//userLayout.addView(textCAFile);

	row.addView(userLayout);
      
	buttonsLayout = new LinearLayoutWithView( ctx, this );
	buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f );
	params3.gravity=Gravity.RIGHT;
	buttonsLayout.setLayoutParams( params3 );
	buttonsLayout.setGravity( Gravity.RIGHT|Gravity.CENTER_VERTICAL );
	
	deleteUser = new ImageButtonWithView( ctx, this );
	deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
	deleteUser.setOnClickListener( deleteUserListener );
	infoUser = new ImageButtonWithView( ctx, this );
	infoUser.setImageResource(android.R.drawable.ic_dialog_info);
	infoUser.setOnClickListener( infoUserListener );
	buttonsLayout.addView( infoUser );
	buttonsLayout.addView( deleteUser );

	
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
}
