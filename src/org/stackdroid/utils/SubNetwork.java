package org.stackdroid.utils;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;



public class SubNetwork { 
    private String name;
    private String ID;
    private String cidr;
    private String gw;
    private AllocationPool[] allocPools;
    private String[] dns;
    private boolean dhcp;
    private String ipv;
    
    public SubNetwork( String name, String ID, String cidr, String gatewayIP, AllocationPool[] allocPools, String[] dns, boolean dhcp, String ipv ) {
    	this.name       = name;
    	this.ID         = ID;
    	this.cidr       = cidr;
    	this.gw         = gatewayIP;
    	this.allocPools = allocPools;
    	this.dns        = dns;
    	this.dhcp       = dhcp;
    	this.ipv        = ipv;
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public String getAddress( ) { return cidr; }
    public String getGatewayIP( ) { return gw; }
    public AllocationPool[] getAllocationPools( ) { return allocPools; }
    public String[] getDNS( ) { return dns; }
    public String getIPVersion( ) { return ipv; }

    @Override
    public String toString() {
	return "Subnet{name=" + name
	    + ",ID="+ID
	    + ",cidr="+cidr
	    + ",gateway="+gw
	    + ",start=" + allocPools[0].getStartIP( )
	    + ",end=" + allocPools[0].getEndIP( ) 
	    + ",dns=" + dns[0]
	    + ",dhcp=" + dhcp
	    +"}";
    }
    
   public static Hashtable<String, SubNetwork> parse( String jsonBuf )  throws ParseException  {
    	
    	if(jsonBuf==null) return new Hashtable<String, SubNetwork>();
    	
    	Hashtable<String, SubNetwork> result = new Hashtable<String, SubNetwork>();
    	try{
    		JSONObject jsonObject = new JSONObject( jsonBuf );
    		JSONArray subnets = (JSONArray)jsonObject.getJSONArray("subnets");
    		for(int i =0; i<subnets.length(); ++i) {
    			JSONObject subnet = subnets.getJSONObject(i);
    			boolean dhcp = subnet.getBoolean("enable_dhcp");
    			String ID = (String)subnet.getString("id");
    			String name = (String)subnet.getString("name");
    			String gateway = (String)subnet.getString("gateway_ip");
    			String cidr = (String)subnet.getString("cidr");
    			String ipv = subnet.getString("ip_version");
    			JSONArray dnsarray = (JSONArray)subnet.getJSONArray("dns_nameservers");
    			String[] dns = new String[dnsarray.length()];
    			for(int j = 0; j<dnsarray.length(); j++)
    				dns[j] = (String)dnsarray.getString(j);
    			JSONArray allocpools = (JSONArray)subnet.getJSONArray("allocation_pools");
    			AllocationPool[] pools = new AllocationPool[allocpools.length()];
    			for(int j=0; j<allocpools.length(); j++) {
    				AllocationPool pool = new AllocationPool( (String)allocpools.getJSONObject(j).getString("start"), 
					(String)allocpools.getJSONObject(j).getString("end") );
    				pools[j] = pool;
    			}
    			result.put( ID, new SubNetwork( name, ID, cidr, gateway, pools, dns, dhcp, ipv ));
    		}
    	} catch(org.json.JSONException je) {
    		throw new ParseException( je.getMessage( ) );
    	}
    	return result;
    }
}
