package org.stackdroid.comm.commands;


import android.util.Log;


import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;

public class DeleteImageCommand extends Command {
	
	private String imageID;
	
	public DeleteImageCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String imageID) { this.imageID = imageID; }
	
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