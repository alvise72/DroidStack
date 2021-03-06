package org.stackdroid.comm.commands;


import android.util.Log;
import android.util.Pair;


import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;


import java.util.Vector;
import java.util.Hashtable;


public class ServerInfoCommand extends Command {
	
	public ServerInfoCommand( User U ) {
		this.U = U;
	}
	
	private String serverID;
	
	public void setup(String a, int b) {}
	public void setup(String a, String b) {}
	public void setup(String a) { serverID = a; }
	public void setup(String serverName, 
					  String imageID,
					  String key_name,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) {}
	
	public void execute( ) throws Exception
	{
		checkToken();
 		
		Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
		Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
		v.add(p);
		restResponse = RESTClient.sendGETRequest( U.useSSL(),
					 					  		  U.getNovaEndpoint() + "/servers/" + serverID,
					 					  		  U.getToken(),
					 					  		  v );
	}
}