package org.stackdroid.comm.commands;


import android.util.Log;
import android.util.Pair;


import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;


import java.util.Hashtable;


public class ListImagesCommand extends Command {
	
	public ListImagesCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a, int b) {}
	public void setup(String a) {}
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
		restResponse = RESTClient.sendGETRequest(U.useSSL(),
						    	      			 U.getGlanceEndpoint() + 
						    	      			 "/" + 
						    	      			 U.getGlanceEndpointAPIVER() + 
						    	      			 "/images",
						    	      			 U.getToken(),
						    	      			 null);
	}
	
}