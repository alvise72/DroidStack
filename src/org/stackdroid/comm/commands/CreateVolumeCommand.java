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


public class CreateVolumeCommand extends Command {
	
	private String  volname;
	private int     size_in_GB;
	
	public CreateVolumeCommand( User U ) {
		this.U = U;
	}
	
	public void setup(String a) {}
	public void setup(String a, String b) {}
	public void setup(String serverName, 
					  String imageID,
					  String keyName,
					  String flavorID,
					  int count,
					  String securityGroupID,
					  Hashtable<Pair<String,String>, String> netID_to_netIP) 
	{}
	
	public void setup( String volname, int size_in_GB) {
		this.volname = volname;
		this.size_in_GB = size_in_GB;
	}
	
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
		JSONObject data = new JSONObject( );
		JSONObject vol_data = new JSONObject( );
		vol_data.put("display_name", volname).put("imageRef", "null").put("availability_zone", "null").put("volume_type", "null").put("display_description", "null").put("snapshot_id", "null").put("size", size_in_GB);
		data.put("volume", vol_data);
		//String extradata = "{\"volume\": {\"display_name\": \"" + volname + "\", \"imageRef\": null, \"availability_zone\": null, \"volume_type\": null, \"display_description\": null, \"snapshot_id\": null, \"size\": " + size_in_GB + "}}";
		RESTClient.sendPOSTRequest(U.useSSL(),
				   				   cinderEP + "/volumes",
				   				   U.getToken(),
				   				   data.toString( ),
				   				   vp);
	}
	
}