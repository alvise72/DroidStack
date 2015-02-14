package org.stackdroid.utils;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Network {
    //private String status;
    private String 				name;
    private String 				ID;
    private Vector<SubNetwork> 	subnets;
    private boolean 			shared;
    private boolean 			up;
    private boolean 			ext;
    private String 				tenantID;
    
    public Network( String status, String name, String ID, Vector<SubNetwork> subnets, boolean shared, boolean up, boolean ext, String tenantID ) {
    	//this.status = status;
    	this.name = name;
    	this.ID   = ID;
    	this.subnets = subnets;
    	this.shared= shared;
    	this.up = up;
    	this.ext = ext;
    	this.tenantID = tenantID;
    	//fixedIP = "";
    }

    @Override
    public String toString( ) {
	  return name;
    }

    //public void setFixedIP( String IP ) { fixedIP = IP; }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public Vector<SubNetwork> getSubNetworks( ) { return subnets; }
    public boolean isShared( ) { return shared; }
    public boolean isUp( ) { return up; }
    public boolean isExt( ) { return ext; }
    public String getTenantID() { return tenantID; }
    //public String getFixedIP( ) { return fixedIP; }
    
    
    /**
    *
    *
    *
    *
    */    
   public static Vector<Network> parse( String jsonBuf, String jsonBufSubnet )  throws ParseException  {
   	Hashtable<String, SubNetwork> subnetsTable = SubNetwork.parse( jsonBufSubnet );
   	Vector<Network> nets = new Vector<Network>();
   	try {
   		JSONObject jsonObject = new JSONObject( jsonBuf );
   		JSONArray networks = (JSONArray)jsonObject.getJSONArray("networks");
   		for(int i =0; i<networks.length(); ++i) {
   			JSONObject network = networks.getJSONObject(i);
   			String status = (String)network.getString("status");
   			String name = (String)network.getString("name");
   			boolean up = network.getBoolean("admin_state_up");
   			boolean ext = network.getBoolean("router:external");
   			boolean shared = network.getBoolean("shared");
   			String ID = network.getString("id");
   			JSONArray subnets  = network.getJSONArray("subnets");
   			String[] arraySubnetID = new String[ subnets.length() ];
   			String tenantID = network.getString("tenant_id");
   			for(int j = 0; j<subnets.length(); ++j)
   				arraySubnetID[j] = (String)subnets.getString(j);
		
   			Vector<SubNetwork> _subnets = new Vector<SubNetwork>();
   			for(int j = 0; j< arraySubnetID.length; j++)
   				if(subnetsTable.containsKey(arraySubnetID[j]) == true) 
   					_subnets.add( subnetsTable.get(arraySubnetID[j]) );
   			nets.add( new Network(status, name, ID, _subnets, shared, up, ext, tenantID ) );
   		}
   	} catch(org.json.JSONException je) {
   		throw new ParseException( je.getMessage( ) );
   	}
   	return nets;
   }

}
