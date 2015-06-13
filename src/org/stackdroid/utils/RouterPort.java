package org.stackdroid.utils;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Port {//implements Serializable {

	private String id;
	private String name;
	private String networkID;
	private String tenantID;
	private String device_owner;
	private String MAC;
	private String deviceID;
	private Vector<Pair<String, String>> subnets_fixedips;
	
	public Port( String id, String name, String networkID, String tenantID, String device_owner, String MAC, String deviceID, Vector<Pair<String,String>> subs ) {
		this.id = id;
		this.name = name;
		this.networkID=networkID;
		this.tenantID=tenantID;
		this.device_owner=device_owner;
		this.MAC=MAC;
		this.subnets_fixedips=subs;
		this.deviceID=deviceID;
	}
	
	public String getFixedIP( ) {
		if(subnets_fixedips.size()==0) return null;
		return subnets_fixedips.elementAt(0).second;
	}

	public String getName( ) { return name; }

	public String getNetworkID( ) { return networkID; }

	public String getTenantID( ) { return tenantID; }

	public String getDeviceOwner( ) { return device_owner; }

	public String getMAC( ) { return MAC; }

	public Pair<String,String> getSubnetInterface( ) {
		if(subnets_fixedips.size()==0) return null;
		return subnets_fixedips.elementAt(0);
	}

	public String getID( ) { return id ; }
	
	public static Vector<Port> parse( String jsonPort ) throws ParseException {
		Vector<Port> vecP = new Vector<Port>();
		try {
			JSONObject jsonObject = new JSONObject( jsonPort );
			JSONArray ports = jsonObject.getJSONArray("ports");
			for(int i = 0; i<ports.length(); ++i) {
				JSONObject port = ports.getJSONObject(i);
				String id = port.getString("id");
				String deviceid = port.getString("device:id");
				String device_owner = port.getString("device_owner");
				String tenantid = port.getString("tenant_id");
				String name = port.getString("name");
				String netid = port.getString("network_id");
				String mac = port.getString("mac_address");
				Pair<String,String> P = null;
				Vector<Pair<String,String>> subnets_ips = new Vector();
				if(port.has("fixed_ips")) {
					JSONArray fxips = port.getJSONArray("fixed_ips");
					for(int j = 0; j<fxips.length(); j++) {
						JSONObject fxip = fxips.getJSONObject(j);
						String ipaddr = fxip.getString("ip_address");
						String subnetid = fxip.getString("subnet_id");
						P = new Pair<String,String>(subnetid,ipaddr);
						subnets_ips.add(P);
					}
				}
				//String fixedip = port.has("fixed_ips") ? port.getJSONArray("fixed_ips").getJSONObject(0).getString("ip_address") : "";
				vecP.add(new Port(id, name, netid, tenantid,device_owner,mac,deviceid,subnets_ips));
			}
			return vecP;
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
	}
}