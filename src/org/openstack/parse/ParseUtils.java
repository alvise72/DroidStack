package org.openstack.parse;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.openstack.utils.AllocationPool;
import org.openstack.utils.SubNetwork;
import org.openstack.utils.SecGroup;
import org.openstack.utils.Network;
import org.openstack.utils.KeyPair;
import org.openstack.utils.OSImage;
import org.openstack.utils.Server;
import org.openstack.utils.Flavor;
import org.openstack.utils.Quota;
import org.openstack.utils.User;

import android.util.Log;
import android.util.Pair;

public class ParseUtils {

    /**
     *
     *
     *
     *
     */    
    public static User parseUser( String jsonString ) throws ParseException
    {
      try {
	  JSONObject jsonObject = null;
       
	  jsonObject = new JSONObject( jsonString );
	  
	  JSONObject access = (JSONObject)jsonObject.get("access");
	  JSONObject token = (JSONObject)access.get("token");
	  String stoken = (String)token.get("id");
	  String expires = (String)token.get("expires");
	  JSONObject tenant = (JSONObject)token.get("tenant");
	  String tenantid = (String)tenant.get("id");
	  String tenantname = (String)tenant.get("name");
	  String username = (String)((JSONObject)access.get("user")).get("username");
	  String userID = (String)((JSONObject)access.get("user")).get("id");
	  //boolean is_admin = access.getJSONObject("metadata").getInt("is_admin") == 0 ? true : false;
	  JSONArray roleArray = access.getJSONObject("user").getJSONArray("roles");
	  boolean role_admin = false;
	  for(int i = 0; i<roleArray.length(); ++i)
	      if(roleArray.getJSONObject(i).getString("name").compareTo("admin")==0)
		  role_admin = true;
	  
	  SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  timeFormatter.setTimeZone( TimeZone.getDefault( ) );
	  Calendar calendar = Calendar.getInstance();
	  try {
	      calendar.setTime(timeFormatter.parse(expires));
	  } catch(java.text.ParseException pe) {
	      throw new ParseException( "Error parsing the expiration date ["+expires+"]" );
	  }
	  long expireTimestamp = calendar.getTimeInMillis() / 1000;
	  User U = new User( username, userID, tenantname, tenantid, stoken, expireTimestamp, role_admin );
	  return U;
      } catch(org.json.JSONException je) {
	  throw new ParseException( je.getMessage( ) );
      }
    }

    /**
     *
     *
     *
     *
     */ 
    public static Vector<OSImage> parseImages( String jsonString ) throws ParseException
    {
      try {
        Vector<OSImage> result = new Vector();
        
        JSONObject jsonObject = new JSONObject( jsonString );
        JSONArray images      = (JSONArray)jsonObject.getJSONArray("images");
      
        for(int i=0; i<images.length( ); ++i ) {
          String name         = (String)images.getJSONObject(i).get("name");
	  long   size         = (long)  images.getJSONObject(i).getLong("size");
	  String format       = (String)images.getJSONObject(i).get("disk_format");
	  String creationDate = (String)images.getJSONObject(i).get("created_at");
	  String visibility   = (String)images.getJSONObject(i).get("visibility");
	  String status       = (String)images.getJSONObject(i).get("status");
	  String ID           = (String)images.getJSONObject(i).get("id");

	  SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          timeFormatter.setTimeZone( TimeZone.getDefault( ) );
          Calendar calendar = Calendar.getInstance();
	  long cdate = 0;
          try {
            calendar.setTime(timeFormatter.parse(creationDate));
	    cdate = calendar.getTimeInMillis() / 1000;
          } catch(java.text.ParseException pe) {
          }
          	  
	  boolean pub = (visibility.compareTo("public")==0 ? true : false);
	  
	  OSImage osimg = new OSImage( name, ID, size, format, status, pub, cdate );
	  
	  if(format.compareToIgnoreCase("ari")!=0 && format.compareToIgnoreCase("aki") != 0)
	    result.add( osimg );
	  
        }
        return result;
	
      } catch(org.json.JSONException je) {
	  throw new ParseException( je.getMessage( ) );
      }
    }

    /**
     *
     *
     *
     *
     */
    public static String getErrorMessage ( String jsonBuf ) {
      JSONObject jsonObject = null;
      String errorMessage = null;
      try {
        jsonObject = new JSONObject( jsonBuf );
	errorMessage = (String)jsonObject.getJSONObject("error").get("message");
      } catch(org.json.JSONException joe) {
        return "Cannot parse json error message from remote server";
      }
      //      Log.d("ParseUtils.getErrorCodeMessage", "Returning: "+errorMessage);
      return errorMessage;
    }
       
    /**
     *
     *
     *
     *
     */    
    public static int getErrorCode ( String jsonBuf ) {
      JSONObject jsonObject = null;
      int errorCode = -1;
      try {
        jsonObject = new JSONObject( jsonBuf );
	errorCode = jsonObject.getJSONObject("error").getInt("code");
      } catch(org.json.JSONException joe) {
        return -1;
      }
      //      Log.d("ParseUtils.getErrorCode", "Returning: "+errorCode);
      return errorCode;
    }
           
    /**
     *
     *
     *
     *
     */    
    public static Quota parseQuota( String jsonBuf )  throws ParseException {
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONObject limits     = (JSONObject)jsonObject.getJSONObject("limits");
	    JSONObject absolute   = (JSONObject)limits.getJSONObject("absolute");
	    int maxInstances      = absolute.getInt("maxTotalInstances");
	    int maxVirtCPU        = absolute.getInt("maxTotalCores");
	    int maxRAM            = absolute.getInt("maxTotalRAMSize");
	    int maxFIP            = absolute.getInt("maxTotalFloatingIps");
	    int maxSecGroups      = absolute.getInt("maxSecurityGroups");
	    int currentInstance   = absolute.getInt("totalInstancesUsed");
	    int currentVirtCPU    = absolute.getInt("totalCoresUsed");
	    int currentRAM        = absolute.getInt("totalRAMUsed");
	    int currentFIP        = absolute.getInt("totalFloatingIpsUsed");
	    int currentSECG       = absolute.getInt("totalSecurityGroupsUsed");
	    return new Quota(currentInstance, 
			     currentVirtCPU,
			     currentRAM,
			     currentFIP,
			     currentSECG,
			     maxInstances,
			     maxVirtCPU,
			     maxRAM,
			     maxFIP,
			     maxSecGroups );
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
    }
	       
    /**
     *
     *
     *
     *
     */    
    // public static Quota parseQuota( String jsonBuf )  throws ParseException {
    // 	try {
    // 	    JSONObject jsonObject = new JSONObject( jsonBuf );
    // 	    JSONObject limits     = (JSONObject)jsonObject.getJSONObject("limits");
    // 	    JSONObject absolute   = (JSONObject)limits.getJSONObject("absolute");
    // 	    int maxInstances      = absolute.getInt("maxTotalInstances");
    // 	    int maxVirtCPU        = absolute.getInt("maxTotalCores");
    // 	    int maxRAM            = absolute.getInt("maxTotalRAMSize");
    // 	    int maxFIP            = absolute.getInt("maxTotalFloatingIps");
    // 	    int maxSecGroups      = absolute.getInt("maxSecurityGroups");
    // 	    int currentInstance   = absolute.getInt("totalInstancesUsed");
    // 	    int currentVirtCPU    = absolute.getInt("totalCoresUsed");
    // 	    int currentRAM        = absolute.getInt("totalRAMUsed");
    // 	    int currentFIP        = absolute.getInt("totalFloatingIpsUsed");
    // 	    int currentSECG       = absolute.getInt("totalSecurityGroupsUsed");
    // 	    return new Quota(currentInstance, 
    // 			     currentVirtCPU,
    // 			     currentRAM,
    // 			     currentFIP,
    // 			     currentSECG,
    // 			     maxInstances,
    // 			     maxVirtCPU,
    // 			     maxRAM,
    // 			     maxFIP,
    // 			     maxSecGroups );
    // 	} catch(org.json.JSONException je) {
    // 	    throw new ParseException( je.getMessage( ) );
    // 	}
    // }
    
    /**
     *
     *
     *
     *
     */    
    public static Vector<Pair<String, String>> parseFloatingIPs( String jsonBuf ) throws ParseException {
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray fips = jsonObject.getJSONArray( "floating_ips" );
	    Vector<Pair<String, String>> res = new Vector();
	    for(int i = 0; i<fips.length(); ++i) {
		JSONObject fip = fips.getJSONObject( i );
		String id = fip.getString("id");
		String ip = fip.getString("ip");
		Pair<String, String> ip_info = new Pair(id, fip);
		res.add( ip_info );
	    }
	    return res;
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
    }
    
    /**
     *
     *
     *
     *
     */    
    public static Vector<Server> parseServers( String jsonBuf, String username )  throws ParseException {
	Vector<Server> serverVector = new Vector();
	String status = "N/A";
	String keyname ="N/A";
	String[] secgrps = null;
	String flavorID = "N/A";
	String ID = "N/A";
	String computeNode= "N/A";
	String name = "N/A";
	String task = "N/A";
	String privIP = "N/A";
	String pubIP = "N/A";
	long creationTime = 0;
	int power = -1;
	
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray servers     = (JSONArray)jsonObject.getJSONArray("servers");

	    for(int i=0; i<servers.length( ); ++i) {
		JSONObject server = (JSONObject)servers.getJSONObject(i);
		status = (String)server.getString("status");
		try{keyname = (String)server.getString("key_name");} catch(JSONException je) {}
		try{
		    JSONArray secgarray  =  ((JSONArray)server.getJSONArray("security_groups"));
		    secgrps = new String[secgarray.length()];
		    for(int j=0; j<secgarray.length(); j++)
			secgrps[j] = (String)((JSONObject)secgarray.getJSONObject(j)).getString("name");
		} catch(JSONException je) {secgrps=null;}
		flavorID = (String)((JSONObject)server.getJSONObject("flavor")).getString("id");
		ID = (String)server.getString("id");

		computeNode = (String)server.getString("OS-EXT-SRV-ATTR:hypervisor_hostname");
		name = (String)server.getString("name");
		task = (String)server.getString("OS-EXT-STS:task_state");
		try {
		    JSONObject addresses = (JSONObject)server.getJSONObject("addresses");
		    JSONArray user_addresses = (JSONArray)addresses.getJSONArray( username );
		    privIP = (String)((JSONObject)user_addresses.getJSONObject(0)).getString("addr");
		    pubIP = null;
		    if(user_addresses.length()>1)
			pubIP = (String)((JSONObject)user_addresses.getJSONObject(1)).getString("addr");
		} catch(JSONException je) {}
		try {
		    String creation = (String)server.getString("created");
		    SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    timeFormatter.setTimeZone( TimeZone.getDefault( ) );
		    Calendar calendar = Calendar.getInstance();
		    try {
			calendar.setTime(timeFormatter.parse(creation));
		    } catch(java.text.ParseException pe) {
			throw new ParseException( "Error parsing the creation date ["+creation+"]" );
		    }
		    creationTime = calendar.getTimeInMillis() / 1000;
		} catch(JSONException je) {throw new ParseException( je.getMessage( ) );}

		try { power = (int)server.getInt("OS-EXT-STS:power_state");} catch(JSONException je) {}

		Server S = new Server(name,ID,status,task,power,privIP,pubIP,computeNode,keyname,flavorID,secgrps,creationTime);
		serverVector.add(S);
	    }
 	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return serverVector;
    }   

    /**
     *
     *
     *
     *
     */    
    public static Hashtable<String, Flavor> parseFlavors( String jsonBuf )  throws ParseException {
	Hashtable<String, Flavor> flavorTable = new Hashtable();
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray flavors = (JSONArray)jsonObject.getJSONArray("flavors");
	    for(int i=0; i<flavors.length(); ++i ) {
		JSONObject flavor = flavors.getJSONObject(i);
		String name = (String)flavor.getString("name");
		int ram = flavor.getInt("ram");
		int cpus = flavor.getInt("vcpus");
		String s_swap = (String)flavor.getString("swap");
		int swap = 0;
		if(s_swap!=null & s_swap.length()!=0)
		    swap = Integer.parseInt( (String)flavor.getString("swap") );
		int ephemeral = flavor.getInt("OS-FLV-EXT-DATA:ephemeral");
		int disk = flavor.getInt("disk");
		String ID = (String)flavor.getString("id");
		Flavor F = new Flavor(name, ID, ram, cpus, swap, ephemeral, disk);
		flavorTable.put(F.getID(), F);
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return flavorTable;
    }

    /**
     *
     *
     *
     *
     */    
    public static Network[] parseNetworks( String jsonBuf, String jsonBufSubnet )  throws ParseException  {

	Hashtable<String, SubNetwork> subnetsTable = parseSubNetworks( jsonBufSubnet );
	//	Vector<Network> nets = null;
	Network nets[] = null;
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray networks = (JSONArray)jsonObject.getJSONArray("networks");
	    //	    nets = new Vector();
	    nets = new Network[networks.length()];
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

		
		SubNetwork[] _subnets = new SubNetwork[subnets.length()];
		for(int j = 0; j< arraySubnetID.length; j++)
		    if(subnetsTable.containsKey(arraySubnetID[j]) == true) 
			_subnets[j] = subnetsTable.get(arraySubnetID[j]);
		
		nets[i] = new Network(status, name, ID, _subnets, shared, up, ext, tenantID );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return nets;
    }

    /**
     *
     *
     *
     *
     */    
    public static Hashtable<String, SubNetwork> parseSubNetworks( String jsonBuf )  throws ParseException  {
	Hashtable<String, SubNetwork> result = new Hashtable();
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
		SubNetwork sub = new SubNetwork( name, ID, cidr, gateway, pools, dns, dhcp );
		//		Log.d("parseSubNetworks", "SubNetwork="+sub.toString() );
		result.put( ID, new SubNetwork( name, ID, cidr, gateway, pools, dns, dhcp ));
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return result;
    }

    /**
     *
     *
     *
     *
     */    
    public static KeyPair[] parseKeyPairs( String jsonBuf ) throws ParseException  {
	KeyPair kpairs[] = null;
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray keypairs = (JSONArray)jsonObject.getJSONArray("keypairs");
	    kpairs = new KeyPair[keypairs.length()];
	    for(int i =0; i<keypairs.length(); ++i) {
		JSONObject keypair = keypairs.getJSONObject(i).getJSONObject("keypair");
		String key  = keypair.getString("public_key");
		String fp   = keypair.getString("fingerprint");
		String name = keypair.getString("name");
		kpairs[i] = new KeyPair( name, key, fp );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return kpairs;
    }

    /**
     *
     *
     *
     *
     */    
    public static SecGroup[] parseSecGroups( String jsonBuf ) throws ParseException  {
	SecGroup secg[] = null;
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray secgroups = (JSONArray)jsonObject.getJSONArray("security_groups");
	    secg = new SecGroup[secgroups.length()];
	    for(int i =0; i<secgroups.length(); ++i) {
		JSONObject secgrp = secgroups.getJSONObject(i);
		String id   = secgrp.getString("id");
		String name = secgrp.getString("name");
		secg[i] = new SecGroup( name, id );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return secg;
    }
}
