package org.stackdroid.comm.commands;


import android.util.Log;


import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;

public class ListImagesCommand extends Command {
	
	public ListImagesCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a) {}
	
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