package org.openstack.utils;

import java.io.Serializable;

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
    private String privIP;
    private String pubIP;
    private String computeNode;
    //    private String MAC;
    private String keyname;
    private String flavorID;
    private String[] secgrpID;
    private long creationTime;
    private Flavor flavor;
    
    public Server( String _name,
		   String _ID,
		   String _status,
		   String _task,
		   int _power,
		   String _privIP,
		   String _pubIP,
		   String _computeNode,
		   //		   String _MAC,
		   String _keyname,
		   String _flavorID,
		   String[] _secgrpID,
		   long _creationTime) {
	name        = _name;
	ID          = _ID;
	status      = _status;
	task        = _task;
	powerstate  = _power;
	privIP      = _privIP;
	pubIP       = _pubIP;
	computeNode = _computeNode;
	//	MAC         = _MAC;
	keyname     = _keyname;
	flavorID    = _flavorID;
	secgrpID    = _secgrpID;
	creationTime= _creationTime;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public String getStatus() { return status; }
    public String getTask() { return task; }
    public int    getPowerState() { return powerstate; }
    public String getPrivateIP() { return privIP; }
    public String getPublicIP() { return pubIP; }
    public String getComputeNode() { return computeNode; }
    //    public String getMACAddress() { return MAC; }
    public String getKeyName() { return keyname; }
    public String getFlavorID() { return flavorID; }
    public String[] getSecurityGroupdID() { return secgrpID; }
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
