package org.openstack.utils;

import java.io.Serializable;

import java.util.Vector;

public class Server implements Serializable {
    public final static int POWER_RUNNING = 1;
    public final static int POWER_NOSTATE = 0;
    public final static int POWER_SHUTDOWN = 4;

    public final static String[] POWER_STRING = {"No State", "Running", "", "", "Shutdown"};

    private String name;
    private String ID;
    private String status;
    private String task;
    private int powerstate;
    private Vector<String> privIP;
    private Vector<String> pubIP;
    private String computeNode;
    private String keyname;
    private String flavorID;
    private String[] secgrpNames;
    private long creationTime;
    private Flavor flavor;
    
    public Server( String _name,
		   String _ID,
		   String _status,
		   String _task,
		   int _power,
		   Vector<String> _privIP,
		   Vector<String> _pubIP,
		   String _computeNode,
		   String _keyname,
		   String _flavorID,
		   long _creationTime,
		   String[] secgroups ) 
    {
	name           = _name;
	ID             = _ID;
	status         = _status;
	task           = _task;
	powerstate     = _power;
	privIP         = _privIP;
	pubIP          = _pubIP;
	computeNode    = _computeNode;
	keyname        = _keyname;
	flavorID       = _flavorID;
	creationTime   = _creationTime;
	secgrpNames    = secgroups;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public String getStatus() { return status; }
    public String getTask() { return task; }
    public int    getPowerState() { return powerstate; }
    public String[] getPrivateIP() { 
	String[] ips = new String[privIP.size()];
	privIP.toArray(ips);
	return ips;
    }
    public String[] getPublicIP() { 
	String[] ips = new String[pubIP.size()];
	pubIP.toArray(ips);
	return ips; 
    }
    public String getComputeNode() { return computeNode; }
    public String getKeyName() { return keyname; }
    public String getFlavorID() { return flavorID;}//flavorID; }
    public String[] getSecurityGroupNames() { return secgrpNames; }

    public long getCreationTime() { return creationTime; }

    public void setFlavor( Flavor f ) { flavor = f; }
    public Flavor getFlavor( ) { return flavor; }

    @Override
    public String toString( ) {
	return "Server{Name=" + (getName() != null ? getName() : "N/A") + 
	    ",ID="+(getID() != null ? getID() : "N/A") +
	    ",status="+( getStatus() != null ? getStatus() : "N/A") + 
	    ",task="+( getTask()!= null ? getStatus() : "N/A") +
	    ",power=" + getPowerState() +
	    ",private IP=" + ( getPrivateIP()!= null ? getPrivateIP() : "N/A") +
	    ",public IP=" + ( getPublicIP()!= null ? getPublicIP() : "N/A") +
	    ",compute node=" + ( getComputeNode()!= null ? getComputeNode() : "N/A") +
	    ",Key name=" + ( getKeyName()!= null ? getKeyName() : "N/A") +
	    ",flavor=" + ( getFlavor()!= null ? getFlavor().getName() : "N/A") +
	    ",creation time=" + getCreationTime();
    }
}
