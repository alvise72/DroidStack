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
					  String keyName,
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
    	
    	JSONObject data = new JSONObject( );
    	JSONObject serverDesc = new JSONObject( );
    	serverDesc.put("name", serverName).put("imageRef", imageID);
    	if(keyName!=null)
    		serverDesc.put("key_name", keyName);
    	serverDesc.put("flavorRef", flavorID).put("max_count", count).put("min_count", count);
    	
    	String[] secgrpIDs = securityGroupID.split(",");
    	
    	Log.d("CreateServerCommand","data="+data);
    	
    	try {
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
    	    
    	    serverDesc.put("security_groups", secgs).put("networks", nets);
    	    
    	} catch(JSONException je) {
    		throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
    	}
    	
    	data.put("server", serverDesc);
    	
    	Log.d("CreateServerCommand","data="+data.toString());
    	
    	RESTClient.sendPOSTRequest(U.useSSL(),
				   				   U.getNovaEndpoint() + "/servers",
				   				   U.getToken(),
				   				   data.toString(),
				   				   v);
	}
	
}