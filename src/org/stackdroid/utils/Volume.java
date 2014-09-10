package org.stackdroid.utils;

import java.io.Serializable;

public class Volume implements Serializable {
	
	private static final long serialVersionUID = 2087368867376448461L;
	
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
    
    public String tostring( ) {
    	return "Volume={" + 
    			"name=" + name +
    			", ID=" + ID +
    			", status=" + status +
    			", bootable=" + bootable +
    			", readonly=" + readonly +
    			", attachmode=" + attachmode +
    			", gigabytes=" + gigabyte +
    			", serverid=" + attachedto_serverid +
    			", servername=" + attachedto_servername +
    			", device=" + attachedto_device +
    			", isAttached=" + isAttached( ) +
    			"}";
    }
    
    public boolean isAttached( ) { return ( attachedto_serverid!=null && attachedto_serverid.length()!=0 ); }
    /*
    public boolean isAttached( ) { 
    	if(status.compareTo("in-use") == 0) return true;
    	return false;
    
    }
    */
}
