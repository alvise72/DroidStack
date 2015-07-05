package org.stackdroid.utils;

import android.util.Pair;

import java.io.Serializable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class RouterPort {//implements Serializable {

	private String id;
	private String name;
	private String networkID;
	private String subnetID;
	private String fixedIP;
	private String tenantID;
	private String device_owner;
	private String MAC;
	private String status;
	private Vector<Pair<String, String>> subnets_fixedips;
	
	public RouterPort( String id, String name, String status, String networkID, String subnetID, String fixedIP, String tenantID, String device_owner, String MAC ) {
		this.id 				= id;
		this.name 				= name;
		this.networkID			= networkID;
		this.tenantID			= tenantID;
		this.device_owner		= device_owner;
		this.MAC				= MAC;
		this.status 			= status;
		this.subnetID			= subnetID;
		this.fixedIP			= fixedIP;
	}
	
	public String getFixedIP( ) {
		//if(subnets_fixedips.size()==0) return null;
		//return subnets_fixedips.elementAt(0).second;
		return fixedIP;
	}

	public String getStatus( ) { return status; }

	public String getName( ) { return name; }

	public String getNetworkID( ) { return networkID; }

	public String getSubnetID( ) { return subnetID; }

	public String getTenantID( ) { return tenantID; }

	public String getDeviceOwner( ) { return device_owner; }

	public String getMAC( ) { return MAC; }

	public String getID( ) { return id ; }
	
	public static Vector<RouterPort> parse( String jsonPort ) throws ParseException {
		Vector<RouterPort> vecP = new Vector<RouterPort>();
		try {
			JSONObject jsonObject = new JSONObject( jsonPort );
			JSONArray ports = jsonObject.getJSONArray("ports");
			for(int i = 0; i<ports.length(); ++i) {
				JSONObject port = ports.getJSONObject(i);
				String id = port.getString("id");
				//String deviceid = port.getString("device_id");
				String device_owner = port.getString("device_owner");
				String tenantid = port.getString("tenant_id");
				String name = port.getString("name");
				String netid = port.getString("network_id");
				String mac = port.getString("mac_address");
				String status = port.getString("status");
				Pair<String,String> P = null;
				//Vector<Pair<String,String>> subnets_ips = new Vector();
				String ipaddr ="N/A";
				String subnetid = "N/A";
				if(port.has("fixed_ips")) {
					JSONArray fxips = port.getJSONArray("fixed_ips");

					//for(int j = 0; j<fxips.length(); j++) {

					if(fxips.length()>0) {
						JSONObject fxip = fxips.getJSONObject(0);
						ipaddr = fxip.getString("ip_address");
						subnetid = fxip.getString("subnet_id");

						//P = new Pair<String,String>(subnetid,ipaddr);
						//subnets_ips.add(P);
					}
				}
				vecP.add(new RouterPort(id, name, status, netid, subnetid, ipaddr, tenantid, device_owner, mac));
			}
			return vecP;
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
	}
}