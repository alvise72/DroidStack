package org.openstack.utils;

public class Server {
    public final static int POWER_RUNNING   = 1;
    public final static int POWER_SHUTDOWN  = 2;

    public final String[] POWER_STRING = {"Running", "Shutdown"};

    private String name;
    private String ID;
    private int status;
    private int task;
    private int powerstate;
    private String privIP;
    private String pubIP;
    private String computeNode;
    private String MAC;
    private String keyname;
    private String flavorID;
    private String secgrpID;
    
    public Server( String _name,
		   String _ID,
		   int _status,
		   int _task,
		   int _power,
		   String _privIP,
		   String _pubIP,
		   String _computeNode,
		   String _MAC,
		   String _keyname,
		   String _flavorID,
		   String _secgrpID ) {
	name        = _name;
	ID          = _ID;
	status      = _status;
	task        = _task;
	powerstate  = _power;
	privIP      = _privIP;
	pubIP       = _pubIP;
	computeNode = _computeNode;
	MAC         = _MAC;
	keyname     = _keyname;
	flavorID    = _flavorID;
	secgrpID    = _secgrpID;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public int    getStatus() { return status; }
    public int    getTask() { return task; }
    public int    getPowerState() { return powerstate; }
    public String getPrivateIP() { return privIP; }
    public String getPublicIP() { return pubIP; }
    public String getComputeNode() { return computeNode; }
    public String getMACAddress() { return MAC; }
    public String getKeyName() { return keyname; }
    public String getFlavorID() { return flavorID; }
    public String getSecurityGroupdID() { return secgrpID; }
}
