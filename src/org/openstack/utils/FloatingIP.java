package org.openstack.utils;

public class FloatingIP {
    private String IP;
    private String fixedIP;
    private String ID;
    private String attachedTo;
    private String poolName;
    
    public FloatingIP( String IP, String fixedIP, String ID, String instanceID, String poolName ) {
	  this.IP = IP;
	  this.fixedIP = fixedIP;
  	  this.attachedTo = instanceID;
	  this.ID   = ID;
	  this.poolName = poolName;
    }

    @Override
    public String toString( ) {
	return "FloatingIP{IP=" + IP
		+ ",fixed IP=" + fixedIP
	    + ",pool=" + poolName
	    + ",server="+attachedTo
	    + "}";
    }

    public String getPoolName( ) { return poolName; }
    public String getID( ) { return ID; }
    public String getIP( ) { return IP; }
    public String getFixedIP( ) { return fixedIP; }
    public String getServerName( ) { return attachedTo; }
    public void setServerName( String name ) { attachedTo=name; }
    public boolean isAssociated( ) {
      if(attachedTo==null || attachedTo.length()==0 || attachedTo.compareTo("null")==0)	
        return true;
      else
    	 return false;
    }
}
