package org.openstack.utils;

public interface Named {
    
    public final static int BUTTON_DELETE_USER = 0;
    public final static int BUTTON_MODIFY_USER = 1;
    public final static int TEXTVIEW           = 2;
    public final static int IMAGEVIEW          = 2;

    
    // public int type = -1;
    // public String extras = -1;

    public int getType( );// { return type; } 
    public String getExtras();// { return extras; }
};
