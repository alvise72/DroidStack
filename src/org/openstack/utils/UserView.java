package org.openstack.utils;


import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import android.content.Context;
//import org.openstack.R;

public class UserView extends LinearLayout {

    private Context ctx = null;
    private LinearLayout textLayout = null;
    private LinearLayout buttonsLayout = null;
    private TextView textUserName = null;
    private TextView textEndpoint = null;
    private ImageButton modifyUser = null;
    private ImageButton deleteUser = null; 
    
    public UserView ( User U, Context ctx ) {
      super(ctx);
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) ;
      this.setOrientation( LinearLayout.HORIZONTAL );
      this.setLayoutParams( params );
      
      textLayout = new LinearLayout( ctx );
      textLayout.setOrientation( LinearLayout.VERTICAL );
      params = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
      textLayout.setLayoutParams( params );
      
     
      buttonsLayout = new LinearLayout( ctx );
      buttonsLayout.setOrientation( LinearLayout.HORIZONTAL );
      params = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) ;
      buttonsLayout.setLayoutParams( params );
      
      modifyUser = new ImageButton( ctx );
      modifyUser.setImageResource(android.R.drawable.ic_menu_edit);
      
      deleteUser = new ImageButton( ctx );
      deleteUser.setImageResource(android.R.drawable.ic_menu_delete);
      
      textUserName = new TextView( ctx );
      textEndpoint = new TextView( ctx );
      textUserName.setText( U.getUserName( ) );
      textEndpoint.setText( U.getEndpoint( ) );
      
      textLayout.addView( textUserName );
      textLayout.addView( textEndpoint );
      
      
    }
}
