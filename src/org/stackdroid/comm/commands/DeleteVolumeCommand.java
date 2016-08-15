package org.stackdroid.comm.commands;


import android.util.Log;
import android.util.Pair;

import org.stackdroid.utils.User;
import org.stackdroid.comm.RESTClient;


import java.util.Hashtable;
import java.util.Vector;


public class DeleteVolumeCommand extends Command {
	
	private String volumeID;
	
	public DeleteVolumeCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a, int b) {}
	public void setup(String volID) { this.volumeID = volID; }
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
    	String cinderEP = null;
    	if(U.getCinder2Endpoint()!=null)
    		cinderEP = U.getCinder2Endpoint();
    	else
    		cinderEP = U.getCinder1Endpoint();
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	RESTClient.sendDELETERequest(U.useSSL(),
				    			     cinderEP + "/volumes/" + volumeID,
				    			     U.getToken(),
				    			     vp);
	}
}