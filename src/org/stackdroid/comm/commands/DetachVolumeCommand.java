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


public class DetachVolumeCommand extends Command {
	
	private String  volumeID;
	private String  serverID;
	
	public DetachVolumeCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a) {}
	public void setup(String a, int b) {}
	public void setup(String serverName, 
					  String imageID,
					  String keyName,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) 
	{}
	
	public void setup( String volID, String serverID ) {
		this.volumeID = volID;
		this.serverID = serverID;
	}
	
	public void execute( ) throws Exception
	{
		checkToken( );
		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
		Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
		vp.add( p );
		RESTClient.sendDELETERequest(U.useSSL(),
				     				 U.getNovaEndpoint() + "/servers/" + serverID + "/os-volume_attachments/" + volumeID,
				     				 U.getToken(),
				     				 vp);
	}
	
}