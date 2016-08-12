package org.stackdroid.comm.commands;


import android.util.Log;
import android.util.Pair;


import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;


import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;


import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class CreateServerCommand extends Command {
	
	private String 								   serverName;
	private String 								   imageID;
	private String 								   flavorID;
	private String								   keyName;
	private int	   								   count;
	private String 								   securityGroupID;
	private Hashtable<Pair<String,String>, String> netID_to_netIP;
	
	public CreateServerCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a) {}
	
	public void setup(String serverName, 
					  String imageID,
					  String key_name,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) 
	{
		this.serverName			= serverName;
		this.imageID			= imageID;
		this.keyName			= keyName;
		this.flavorID			= flavorID;
		this.count				= count;
		this.securityGroupID	= securityGroupID;
		this.netID_to_netIP		= netID_to_netIP;
	}
	
	public void execute( ) throws Exception
	{
		checkToken();
 		
		Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName( ) );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	String data = "{\"server\": {\"name\": \"" + serverName + 
	    	"\", \"imageRef\": \"" + imageID + 
	    	"\", " + (keyName != null ? "\"key_name\": \"" + keyName : "") + 
	    	"\", \"flavorRef\": \"" + flavorID + 
	    	"\", \"max_count\": " + count + 
	    	", \"min_count\": " + count + "}}";
	    	
    	JSONObject obj = null;
    	
    	String[] secgrpIDs = securityGroupID.split(",");
    	
    	try {
    	    obj = new JSONObject( data );
    	    JSONArray secgs = new JSONArray();
    	    JSONArray nets = new JSONArray();
    	    if(securityGroupID.length()!=0) 
    		for(int i = 0; i<secgrpIDs.length; ++i)
    		    secgs.put( new JSONObject("{\"name\": \"" + secgrpIDs[i] + "\"}") );


    	    
	    Iterator<Pair<String,String>> it = netID_to_netIP.keySet().iterator();
    	    while( it.hasNext() ) {
    	    	Pair<String,String> thisNet = it.next();
    	    	String netID = thisNet.first;
    	    	String netIP = netID_to_netIP.get( thisNet );
    	    	
    	    	if( netIP != null && netIP.length()!=0) 
		    nets.put( new JSONObject("{\"uuid\": \"" + netID + "\", \"fixed_ip\":\"" + netIP + "\"}") );
    	    	else {
    	    		
		    nets.put( new JSONObject("{\"uuid\": \"" + netID + "\"}") );
    	    	}
    	    }
    	    
    	    obj.getJSONObject("server").put("security_groups", secgs);
    	    obj.getJSONObject("server").put("networks", nets);
    	    
    	} catch(JSONException je) {
	    throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
    	}
    	
    	data = obj.toString( );
    	//Log.d("OSC","data="+data);
    	RESTClient.sendPOSTRequest(U.useSSL(),
				   U.getNovaEndpoint() + "/servers",
				   U.getToken(),
				   data,
				   v);
	}
	
}