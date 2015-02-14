package org.stackdroid.utils;

import java.io.Serializable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Port implements Serializable {

	private static final long serialVersionUID = 8087367767376441461L;
	
	private String id;
	
	private String fixedIP;
	
	public Port( String id, String fixedIP ) {
		this.id = id;
		this.fixedIP = fixedIP;
	}
	
	public String getFixedIP( ) { return fixedIP; }
	public String getID( ) { return id ; }
	
	public static Vector<Port> parse( String jsonPort ) throws ParseException {
		Vector<Port> vecP = new Vector<Port>();
		try {
			JSONObject jsonObject = new JSONObject( jsonPort );
			JSONArray ports = jsonObject.getJSONArray("ports");
			for(int i = 0; i<ports.length(); ++i) {
				JSONObject port = ports.getJSONObject(i);
				String id = port.getString("id");
				String fixedip = port.has("fixed_ips") ? port.getJSONArray("fixed_ips").getJSONObject(0).getString("ip_address") : "";
				vecP.add(new Port(id, fixedip));
			}
			return vecP;
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
	}
}