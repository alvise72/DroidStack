package org.stackdroid.utils;

import android.util.Log;

public class FloatingIP {
    private String IP;
    private String fixedIP;
    private String ID;
    private String attachedTo = null;
    private String poolName;
    private String serverName = null;
    //private String serverID;
    
    public FloatingIP( String IP, String fixedIP, String ID, String instanceID, String poolName) {
	  this.IP = IP;
	  this.fixedIP = fixedIP;
  	  this.attachedTo = instanceID;
	  this.ID   = ID;
	  this.poolName = poolName;
	  //this.serverID = instanceID;
	  //Log.d("FLOATINGIP", "attachedTo="+attachedTo);
    }

    @Override
    public String toString( ) {
    	return IP;
/*	return "FloatingIP{IP=" + IP
		+ ",fixed IP=" + fixedIP
	    + ",pool=" + poolName
	    + ",server="+attachedTo
	    + "}";*/
    }

    public String getPoolName( ) { return poolName; }
    public String getID( ) { return ID; }
    public String getIP( ) { return IP; }
    public String getFixedIP( ) { return fixedIP; }
    public String getServerName( ) { return serverName; }
    public String getServerID( ) { return attachedTo; }
    //public String getServerID( ) { return serverID; }
    public void setServerName( String name ) { serverName=name; }
    public boolean isAssociated( ) {
      if(attachedTo!=null && attachedTo.length()!=0 && attachedTo.compareTo("null")!=0)	
        return true;
      else
    	 return false;
    }
}
