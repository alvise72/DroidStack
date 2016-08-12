package org.stackdroid.comm.commands;


import android.util.Log;
import android.util.Pair;

import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;


import java.util.Hashtable;


public class DeleteImageCommand extends Command {
	
	private String imageID;
	
	public DeleteImageCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String imageID) { this.imageID = imageID; }
	public void setup(String serverName, 
					  String imageID,
					  String key_name,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) {}
	
	public void execute( ) throws Exception
	{
		checkToken( );
		RESTClient.sendDELETERequest(U.useSSL(),
				     				 U.getGlanceEndpoint() + 
				     				 "/" 
				     				 + U.getGlanceEndpointAPIVER() 
				     				 + "/images/" + imageID,
				     				 U.getToken(),
				     				 null);
	}
}