package org.openstack.utils;

public class FloatingIP {
    private String IP;
    private String fixedIP;
    private String ID;
    private String attachedTo;
    private String poolName;
    //private String poolID;
    
    public FloatingIP( String IP, String fixedIP, String ID, String instance, String poolName ) {
	  this.IP = IP;
	  this.fixedIP = fixedIP;
  	  this.attachedTo = instance;
	  this.ID   = ID;
	  this.poolName = poolName;
	  //this.poolID = poolID;
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
    //public String getPoolID( ) { return poolID; }
    public String getID( ) { return ID; }
    public String getIP( ) { return IP; }
    public String getFixedIP( ) { return fixedIP; }
    public String getInstance( ) { return attachedTo; }
}
