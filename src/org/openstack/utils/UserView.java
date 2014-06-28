package org.openstack.utils;


import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import android.view.Gravity;

import android.content.Context;

public class UserView extends LinearLayout {

    private Context ctx = null;
    //public LinearLayout usermain = null;
    private LinearLayout buttonsLayout = null;
    private LinearLayout userLayout = null;
    private TextView textUserName = null;
    private TextView textEndpoint = null;
    private ImageButton modifyUser = null;
    private ImageButton deleteUser = null; 
    
    private String username = null;

    public UserView ( User U, Context ctx ) {
       super(ctx);

      username = U.getUserName( );
      //usermain = (LinearLayout)findViewById(R.id.usermain);
      
      //usermain = new LinearLayout( ctx );
      setOrientation( LinearLayout.HORIZONTAL );
      LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
      setLayoutParams( params1 );

      userLayout = new LinearLayout( ctx );
      userLayout.setOrientation( LinearLayout.VERTICAL );
      LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
      userLayout.setLayoutParams( params2 );

      textUserName = new TextView( ctx );
      textUserName.setText(username);
      textEndpoint = new TextView( ctx );
      textEndpoint.setText(U.getEndpoint( ));

      userLayout.addView(textUserName);
      userLayout.addView(textEndpoint);
      addView(userLayout);
      
      buttonsLayout = new LinearLayout( ctx );
      buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
      LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
      //params3.weight = 1.0f;
      params3.gravity=Gravity.RIGHT;
      buttonsLayout.setLayoutParams( params3 );
      buttonsLayout.setGravity( Gravity.RIGHT );
      
      modifyUser = new ImageButton( ctx );
      modifyUser.setImageResource(android.R.drawable.ic_menu_edit);
      
      deleteUser = new ImageButton( ctx );
      deleteUser.setImageResource(android.R.drawable.ic_menu_delete);

      // LinearLayout.LayoutParams params4 = modifyUser.getLayoutParams();
      // params4.weight = 1;
      // modifyUser.setLayoutParams(params4);

      // LinearLayout.LayoutParams params5 = deleteUser.getLayoutParams();
      // params5.weight = 1;
      // deleteUser.setLayoutParams(params5);

      buttonsLayout.addView( modifyUser );
      buttonsLayout.addView( deleteUser );

      addView( buttonsLayout );

      // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
      // this.setOrientation( LinearLayout.HORIZONTAL );
      // this.setLayoutParams( params );
      
      // // userLayout = new LinearLayout( ctx );
      // // userLayout.setOrientation( LinearLayout.HORIZONTAL );
      // // params = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT) ;
      // //userLayout.setLayoutParams( params );

      // textLayout = new LinearLayout( ctx );
      // textLayout.setOrientation( LinearLayout.VERTICAL );
      // params = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
      // textLayout.setLayoutParams( params );
      
     
      // buttonsLayout = new LinearLayout( ctx );
      // buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
      // params = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
      // buttonsLayout.setLayoutParams( params );
      
      // modifyUser = new ImageButton( ctx );
      // modifyUser.setImageResource(android.R.drawable.ic_menu_edit);
      
      // deleteUser = new ImageButton( ctx );
      // deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
      
      // textUserName = new TextView( ctx );
      // textEndpoint = new TextView( ctx );
      // textUserName.setText( U.getUserName( ) );
      // textEndpoint.setText( U.getEndpoint( ) );
      
      // textLayout.addView( textUserName );
      // textLayout.addView( textEndpoint );
      // buttonsLayout.addView( modifyUser );
      // buttonsLayout.addView( deleteUser );

      // addView( textLayout );
      // addView( buttonsLayout );
    }


}
