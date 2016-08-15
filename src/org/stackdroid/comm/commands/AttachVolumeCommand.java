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


public class AttachVolumeCommand extends Command {
	
	private String  volumeID;
	private String  serverID;
	
	public AttachVolumeCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a) {}
	public void setup(String a, int b) {}
	public void setup(String volumeID, String serverID) {
		this.volumeID = volumeID;
		this.serverID = serverID;
	}
	public void setup(String serverName, 
					  String imageID,
					  String keyName,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) 
	{}
	
	public void execute( ) throws Exception
	{
    	checkToken( );
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	JSONObject data = new JSONObject();
    	data.put("volumeAttachment", (new JSONObject()).put("device", "null").put("volumeId", volumeID) );
    	//String extradata = "{\"volumeAttachment\": {\"device\": null, \"volumeId\": \"" + volumeID + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    				U.getNovaEndpoint() + "/servers/" + serverID + "/os-volume_attachments", 
				    				U.getToken(), 
				    				data.toString( ), 
				    				vp );
	}
	
}