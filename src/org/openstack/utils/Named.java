package org.openstack.utils;

import android.widget.ImageView;

public interface Named {
    
    public final static int BUTTON_DELETE_USER = 0;
    public final static int BUTTON_MODIFY_USER = 1;
    public final static int TEXTVIEW           = 2;
    public final static int IMAGEVIEW          = 2;
    //private ImageView relatedImage             = null;

    
    // public int type = -1;
    // public String extras = -1;

    public int getType( );// { return type; } 
    public String getExtras();// { return extras; }
    //public TextViewNamed getRelatedTextView();
    // public void setRelatedImage( ImageView iv );
    // public ImageView getRelatedImage( ) ;
};
