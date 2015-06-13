package org.stackdroid.utils;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Port {//implements Serializable {

	//private static final long serialVersionUID = 8087367767376441461L;
	
	private String id;
	//private String fixedIP;
	private String name;
	private Network network;
	private String tenantID;
	private String device_owner;
	private String MAC;
	private Vector<Pair<SubNetwork, String>> subnets_fixedips;
	
	public Port( String id, String name, Network net, String tenantID, String device_owner, String MAC, Vector<Pair<SubNetwork,String>> subs ) {
		this.id = id;
		this.name = name;
		this.network=net;
		this.tenantID=tenantID;
		this.device_owner=device_owner;
		this.MAC=MAC;
		this.subnets_fixedips=subs;
	}
	
	public String getFixedIP( ) {
		if(subnets_fixedips.size()==0) return null;
		return subnets_fixedips.elementAt(0).second;
	}

	public String getName( ) { return name; }

	public Network getNetwork( ) { return network; }

	public String getTenantID( ) { return tenantID; }

	public String getDeviceOwner( ) { return device_owner; }

	public String getMAC( ) { return MAC; }

	public Pair<SubNetwork,String> getSubnetInterface( ) {
		if(subnets_fixedips.size()==0) return null;
		return subnets_fixedips.elementAt(0);
	}

	public String get_ID( ) { return id ; }
	
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