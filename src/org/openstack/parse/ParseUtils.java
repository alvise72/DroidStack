package org.openstack.parse;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Vector;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.openstack.utils.AllocationPool;
import org.openstack.utils.FloatingIP;
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
        Vector<OSImage> result = new Vector<OSImage>();
        
        JSONObject jsonObject = new JSONObject( jsonString );
        JSONArray images      = (JSONArray)jsonObject.getJSONArray("images");
      
        for(int i=0; i<images.length( ); ++i ) {
          String name         = images.getJSONObject(i).getString("name");
	  long   size         = (long)images.getJSONObject(i).getLong("size");
	  String format       = images.getJSONObject(i).getString("disk_format");
	  String creationDate = images.getJSONObject(i).getString("created_at");
	  String visibility   = images.getJSONObject(i).getString("visibility");
	  String status       = images.getJSONObject(i).getString("status");
	  String ID           = images.getJSONObject(i).getString("id");
	  int    mindisk      = images.getJSONObject(i).getInt("min_disk");
	  int    minram       = images.getJSONObject(i).getInt("min_ram");

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
	  
	  OSImage osimg = new OSImage( name, ID, size, format, status, pub, cdate, mindisk,minram );
	  
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
      //Log.d("PARSEUTILS", "jsonBuf="+jsonBuf);
      try {
        jsonObject = new JSONObject( jsonBuf );
	//JSONObject error = jsonObject.getJSONObject("error");

	if(jsonObject.has("error"))
	    errorMessage = jsonObject.getJSONObject("error").getString("message");

	if(jsonObject.has("badRequest"))
	    errorMessage = jsonObject.getJSONObject("badRequest").getString("message");

	if(jsonObject.has("overLimit"))
	    errorMessage = jsonObject.getJSONObject("overLimit").getString("message");

	if(jsonObject.has("itemNotFound"))
	    errorMessage = jsonObject.getJSONObject("itemNotFound").getString("message");

      } catch(org.json.JSONException joe) {
        return "Cannot parse json error message from remote server";
      }
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
    public static Vector<FloatingIP> parseFloatingIP( String jsonBuf ) throws ParseException {
	  try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray fips = jsonObject.getJSONArray( "floating_ips" );
	    
	    Vector<FloatingIP> res = new Vector<FloatingIP>();
	    for(int i = 0; i<fips.length(); ++i) {
		JSONObject fip = fips.getJSONObject( i );
		String id = fip.getString("id");
		String ip = fip.getString("ip");
		String fixip = fip.getString("fixed_ip");
		String poolname = fip.getString("pool");
		String server = null;
		if(fip.has("instance_id")== true)
		  server = fip.getString("instance_id");
		
		FloatingIP Fip = new FloatingIP(ip,fixip,id,server,poolname);
		res.add( Fip );
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
    public static Vector<Server> parseServers( String jsonBuf )  throws ParseException {

	//Log.d("PARSEUTILS", "jsonBuf="+jsonBuf);

	Vector<Server> serverVector = new Vector<Server>();
	String status        = "N/A";
	String keyname       ="N/A";
	String[] secgrpNames = null;
	String flavorID      = "N/A";
	String ID            = "N/A";
	String computeNode   = "N/A";
	String name          = "N/A";
	String task          = "N/A";
	long creationTime    = 0;
	int power            = -1;
	
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray servers     = (JSONArray)jsonObject.getJSONArray("servers");

	    for(int i=0; i<servers.length( ); ++i) {
		JSONObject server = (JSONObject)servers.getJSONObject(i);
		status = (String)server.getString("status");
		try{keyname = (String)server.getString("key_name");} catch(JSONException je) {}
		try{
		    JSONArray secgarray  =  ((JSONArray)server.getJSONArray("security_groups"));
		    secgrpNames = new String[secgarray.length()];
		    for(int j=0; j<secgarray.length(); j++) 
			secgrpNames[j] = secgarray.getJSONObject(j).getString("name");

		} catch(JSONException je) { secgrpNames = null; }
		flavorID = (String)((JSONObject)server.getJSONObject("flavor")).getString("id");
		ID = (String)server.getString("id");
		if(server.has("OS-EXT-SRV-ATTR:hypervisor_hostname"))
		    computeNode = server.getString("OS-EXT-SRV-ATTR:hypervisor_hostname");
		else
		    computeNode = "N/A (admin privilege required)";
		name = (String)server.getString("name");
		task = (String)server.getString("OS-EXT-STS:task_state");
		Vector<String> fixedIP = new Vector<String>();//.clear();
		Vector<String> floatingIP = new Vector<String>();
		try {
		    JSONObject addresses = server.getJSONObject("addresses");

		    Iterator<String> keys = addresses.keys( );
		    while( keys.hasNext( ) ) {
			String key = keys.next();
			JSONArray arrayAddr = addresses.getJSONArray( key );
			
			floatingIP.clear();
			for(int j = 0; j < arrayAddr.length(); ++j) {
			    
			    String ip = arrayAddr.getJSONObject(j).getString("addr");
			    String type = arrayAddr.getJSONObject(j).getString("OS-EXT-IPS:type");

			    Log.d("PARSEUTILS", "ip="+ip+" - type="+type);
			    
			    if(type.compareTo("fixed")==0)
				fixedIP.add(ip);
			    if(type.compareTo("floating")==0)
				floatingIP.add(ip);
			}
		    }

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
		Server S = new Server(name,ID,status,task,power,fixedIP,floatingIP,computeNode,keyname,flavorID,creationTime,secgrpNames);
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
	Hashtable<String, Flavor> flavorTable = new Hashtable<String, Flavor>();
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
		//SubNetwork sub = new SubNetwork( name, ID, cidr, gateway, pools, dns, dhcp );
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
    public static Vector<SecGroup> parseSecGroups( String jsonBuf ) throws ParseException  {
	//SecGroup secg[] = null;
    	Vector<SecGroup> secg = new Vector<SecGroup>();
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray secgroups = (JSONArray)jsonObject.getJSONArray("security_groups");
	    
	    for(int i =0; i<secgroups.length(); ++i) {
		  JSONObject secgrp = secgroups.getJSONObject(i);
		  String id   = secgrp.getString("id");
		  String name = secgrp.getString("name");
		  secg.add( new SecGroup( name, id ) );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	  return secg;
    }
}
