package org.stackdroid.utils;

import java.io.Serializable;

public class Volume implements Serializable {
	
	private static final long serialVersionUID = 2087368867376448461L;
	
    //public final static int AVAILABLE = 0;
    //public final static int IN_USE = 1;
    
    //public final static int RW = 0;
    //public final static int RO = 1;

   // public final static String[] status_str = {"Available", "In use"};
    //public final static String[] attachmode_str = {"Read/Write", "Read Only"};
    
    private String name;
    private String ID;
    private String status;
    private boolean bootable;
    private boolean readonly;
    private String attachmode;
    private int gigabyte;
    
    private String attachedto_serverid;
    private String attachedto_servername;
    private String attachedto_device;
    
    public Volume( String _name,
    			   String _ID,
    			   String _status,
    			   boolean _bootable,
    			   boolean _readonly,
    			   String _attachmode,
    			   int _gigabyte,
    			   String _servid,
    			   String _servname,
    			   String _device) 
    {
	  name        = _name;
	  ID          = _ID;
	  status      = _status;
	  bootable    = _bootable;
	  readonly    = _readonly;
	  attachmode  = _attachmode;
	  gigabyte    = _gigabyte;
	  
	  attachedto_serverid   = _servid;
	  attachedto_servername = _servname;
	  attachedto_device     = _device;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public String getStatus() { return status; }
    public boolean isBootable( ) { return bootable; }
    public boolean isReadOnly( ) { return readonly; }
    public String getAttachMod( ) { return attachmode; }
    public int getSize( ) { return gigabyte; }
    public String getAttachedServerID( ) { return attachedto_serverid; }
    public String getAttachedServerName( ) { return attachedto_servername; }
    public String getAttachedDevice( ) { return attachedto_device; }
    
    @Override
    public String toString( ) {
    	return name;
    }
}
