package org.stackdroid.utils;

public class Router {
    private String name;
    private String ID;
    
    public Router(  ) {
	
    }

    @Override
    public String toString( ) {
	  return name;
    }

    //public void setFixedIP( String IP ) { fixedIP = IP; }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    
}
