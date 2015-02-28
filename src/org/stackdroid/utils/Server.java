package org.stackdroid.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Server implements Serializable {
	
	private static final long serialVersionUID = 2087368867376448461L;
	
    public final static int POWER_RUNNING = 1;
    public final static int POWER_NOSTATE = 0;
    public final static int POWER_SHUTDOWN = 4;

    public final static String[] POWER_STRING = {"No State", "Running", "", "", "Shutdown"};

    private String			 name;
    private String 			ID;
    private String 			status;
    private String 			task;
    private int 			powerstate;
    private Vector<String> 	privIP;
    private Vector<String> 	pubIP;
    private String 			computeNode;
    private String 			keyname;
    private String 			flavorID;
    private String[] 		secgrpNames;
    private long 			creationTime;
    private Flavor 			flavor;
    
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

    public String 	getName() { return name; }
    public String	getID() { return ID; }
    public String 	getStatus() { return status; }
    public String 	getTask() { return task; }
    public int    	getPowerState() { return powerstate; }
    public String[] getPrivateIP() { 
	String[] 		ips = new String[privIP.size()];
	
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
	return name;
    }
    
    public static Vector<Server> parse( String jsonBuf )  throws ParseException {
        
    Vector<Server> serverVector = new Vector<Server>();
	String status        = "N/A";
	String keyname       ="N/A";
	String[] secgrpNames = null;
	String flavorID      = "N/A";
	String ID            = "N/A";
	String computeNode   = "N/A";
	String name          = "N/A";
	String task          = "N/A";
	long creationTime    = 0;
	int power            = -1;
	
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray servers     = (JSONArray)jsonObject.getJSONArray("servers");

	    for(int i=0; i<servers.length( ); ++i) {
		JSONObject server = (JSONObject)servers.getJSONObject(i);
		status = (String)server.getString("status");
		try{keyname = (String)server.getString("key_name");} catch(JSONException je) {}
		try{
		    JSONArray secgarray  =  ((JSONArray)server.getJSONArray("security_groups"));
		    secgrpNames = new String[secgarray.length()];
		    for(int j=0; j<secgarray.length(); j++) 
			secgrpNames[j] = secgarray.getJSONObject(j).getString("name");

		} catch(JSONException je) { secgrpNames = null; }
		JSONObject flavObj = server.getJSONObject("flavor");
		flavorID = flavObj.getString("id");
		ID = server.getString("id");
		if(server.has("OS-EXT-SRV-ATTR:hypervisor_hostname"))
		    computeNode = server.getString("OS-EXT-SRV-ATTR:hypervisor_hostname");
		else
		    computeNode = "N/A (admin privilege required)";
		name = (String)server.getString("name");
		if(server.has("OS-EXT-STS:task_state"))
			task = (String)server.getString("OS-EXT-STS:task_state");
		else
			task = null;
		Vector<String> fixedIP = new Vector<String>();
		Vector<String> floatingIP = new Vector<String>();
		try {
		    JSONObject addresses = server.getJSONObject("addresses");

		    Iterator<String> keys = addresses.keys( );
		    while( keys.hasNext( ) ) {
			String key = keys.next();
			JSONArray arrayAddr = addresses.getJSONArray( key );
			
			floatingIP.clear();
			for(int j = 0; j < arrayAddr.length(); ++j) {
			    
			    String ip = arrayAddr.getJSONObject(j).getString("addr");
			    String type = arrayAddr.getJSONObject(j).getString("OS-EXT-IPS:type");

			    if(type.compareTo("fixed")==0)
				fixedIP.add(ip);
			    if(type.compareTo("floating")==0)
				floatingIP.add(ip);
			}
		    }

		} catch(JSONException je) {}
		try {
		    String creation = (String)server.getString("created");
		    SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    timeFormatter.setTimeZone( TimeZone.getDefault( ) );
		    Calendar calendar = Calendar.getInstance();
		    try {
			calendar.setTime(timeFormatter.parse(creation));
		    } catch(java.text.ParseException pe) {
			throw new ParseException( "Error parsing the creation date ["+creation+"]" );
		    }
		    creationTime = calendar.getTimeInMillis() / 1000;
		} catch(JSONException je) {throw new ParseException( je.getMessage( ) );}

		try { power = (int)server.getInt("OS-EXT-STS:power_state");} catch(JSONException je) {}
		Server S = new Server(name,ID,status,task,power,fixedIP,floatingIP,computeNode,keyname,flavorID,creationTime,secgrpNames);
		serverVector.add(S);
	    }
 	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return serverVector;
    }   

}
