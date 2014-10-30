package org.stackdroid.parse;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Vector;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.stackdroid.utils.AllocationPool;
import org.stackdroid.utils.FloatingIP;
import org.stackdroid.utils.QuotaVol;
import org.stackdroid.utils.SimpleSecGroupRule;
//import org.stackdroid.utils.Rule;
import org.stackdroid.utils.SubNetwork;
import org.stackdroid.utils.SecGroup;
import org.stackdroid.utils.Network;
import org.stackdroid.utils.KeyPair;
import org.stackdroid.utils.OSImage;
import org.stackdroid.utils.Server;
import org.stackdroid.utils.Flavor;
import org.stackdroid.utils.Quota;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Volume;

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
    	  //System.out.println("PARSEUSER - jsonUser="+jsonString);
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
    	  JSONArray roleArray = access.getJSONObject("user").getJSONArray("roles");
    	  JSONArray serviceArray = access.getJSONArray("serviceCatalog");

    	  boolean nova=false, glance=false, neutron=false, cinder1=false, cinder2=false;
    	  String novaEP=null, glanceEP=null, neutronEP=null, cinder1EP=null, cinder2EP=null, identityEP = null;
    	  for(int i = 0; i<serviceArray.length();++i) {

    		  JSONObject service = serviceArray.getJSONObject(i);
    		  JSONArray endpoints = service.getJSONArray("endpoints");
    		  String type = service.getString("type");
    		  JSONObject endpoint = endpoints.getJSONObject(0);
    			  if(type.compareTo("compute")==0) {
    				  nova=true;
    				  novaEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("network")==0) {
    				  neutron=true;
    				  neutronEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("volumev2")==0) {
    				  cinder2=true;
    				  cinder2EP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("volume")==0) {
    				  cinder1=true;
    				  cinder1EP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("image")==0) {
    				  glance=true;
    				  glanceEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("identity") == 0) {
    				  identityEP = endpoint.getString("publicURL");
    			  }
    		  
    	  }
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
    	  String addrS = "";
    	  try {
    		  URL identityUrl = new URL(identityEP);
    		  InetAddress addr = InetAddress.getByName(identityUrl.getHost());
    		  addrS = addr.getCanonicalHostName();//.getHostName();
    	  } catch(Exception e) {
    		  addrS = identityEP;
    	  }
    	  
    	  User U = new User( 
    			  			 username, 
    			  			 userID, 
    			  			 tenantname, 
    			  			 tenantid, 
    			  			 stoken, 
    			  			 expireTimestamp, 
    			  			 role_admin,
    			  			 glance,
    			  			 nova,
    			  			 neutron,
    			  			 cinder1,
    			  			 cinder2,
    			  			 identityEP,
    			  			 glanceEP,
    			  			 novaEP,
    			  			 neutronEP,
    			  			 cinder1EP,
    			  			 cinder2EP,
    			  			 addrS);
    	  return U;
      } catch(org.json.JSONException je) {
    	  throw new ParseException( je.getMessage( ) );
      }
    }

    /**
     * @throws JSONException 
     *
     *
     *
     *
     */ 
    public static String parseNeutronError( String buffer ) {
      JSONObject jsonObject = null;
  	  try {
		jsonObject = new JSONObject( buffer );
		JSONObject NE = (JSONObject)jsonObject.get("NeutronError");
		  if(NE.has("message")) {
			  return NE.getString("message");
		  } else return "Cannot parse Neutron server's error message";
  	  } catch (JSONException e) {
		// TODO Auto-generated catch block
  		return "Cannot parse Neutron server's error message";
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
//    	Log.d("PARSE", "image json="+jsonString);
    	try {
    		Vector<OSImage> result = new Vector<OSImage>();
        
    		JSONObject jsonObject = new JSONObject( jsonString );
    		JSONArray images      = (JSONArray)jsonObject.getJSONArray("images");
      
    		for(int i=0; i<images.length( ); ++i ) {
    			//Log.d("PARSE", images.getJSONObject(i).toString(4));
    			String name         = images.getJSONObject(i).has("name") ? images.getJSONObject(i).getString("name") : "N/A";
    			long   size         = images.getJSONObject(i).has("size") ? (long)images.getJSONObject(i).getLong("size") : 0L;
    			String format       = images.getJSONObject(i).has("disk_format") ? images.getJSONObject(i).getString("disk_format") : "N/A";
    			String creationDate = images.getJSONObject(i).has("created_at") ? images.getJSONObject(i).getString("created_at") : "N/A";
    			String visibility   = images.getJSONObject(i).has("visibility") ? images.getJSONObject(i).getString("visibility") : "N/A";
    			String status       = images.getJSONObject(i).has("status") ? images.getJSONObject(i).getString("status") : "N/A";
    			String ID           = images.getJSONObject(i).has("id") ? images.getJSONObject(i).getString("id") : "N/A";
    			int    mindisk      = images.getJSONObject(i).has("min_disk") ? images.getJSONObject(i).getInt("min_disk") : 0;
    			int    minram       = images.getJSONObject(i).has("min_ram") ? images.getJSONObject(i).getInt("min_ram") : 0;

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
     * @throws ParseException 
     *
     *
     *
     *
     */
    public static String getErrorMessage ( String jsonBuf ) throws ParseException {
    	
    	//Log.d("PARSE.getErrorMessage", "jsonBuf="+jsonBuf);
    	
      JSONObject jsonObject = null;
      String errorMessage = null;
      try {
        jsonObject = new JSONObject( jsonBuf );

        if(jsonObject.has("error"))
        	errorMessage = jsonObject.getJSONObject("error").getString("message");

        if(jsonObject.has("badRequest"))
        	errorMessage = jsonObject.getJSONObject("badRequest").getString("message");

        if(jsonObject.has("overLimit"))
        	errorMessage = jsonObject.getJSONObject("overLimit").getString("message");

        if(jsonObject.has("itemNotFound"))
        	errorMessage = jsonObject.getJSONObject("itemNotFound").getString("message");

      } catch(org.json.JSONException joe) {
        throw new ParseException ("Cannot parse json error message from remote server");
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
		JSONObject flavObj = server.getJSONObject("flavor");
		flavorID = flavObj.getString("id");
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

			    //Log.d("PARSEUTILS", "ip="+ip+" - type="+type);
			    
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
    public static Vector<Flavor> parseFlavors( String jsonBuf )  throws ParseException {
	Vector<Flavor> flavorTable = new Vector<Flavor>();
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
		flavorTable.add( F );
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
    public static Vector<Network> parseNetworks( String jsonBuf, String jsonBufSubnet )  throws ParseException  {
    	Hashtable<String, SubNetwork> subnetsTable = parseSubNetworks( jsonBufSubnet );
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
		
    			SubNetwork[] _subnets = new SubNetwork[subnets.length()];
    			for(int j = 0; j< arraySubnetID.length; j++)
    				if(subnetsTable.containsKey(arraySubnetID[j]) == true) 
    					_subnets[j] = subnetsTable.get(arraySubnetID[j]);
    			nets.add( new Network(status, name, ID, _subnets, shared, up, ext, tenantID ) );
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
   public static String parseSingleNetwork( String jsonBuf)  throws ParseException  {
   		//Log.d("PARSE", "jsonBuf="+jsonBuf);
   		//Log.d("PARSE", "jsonBufSubnet="+jsonBufSubnet);
   	
   		//Hashtable<String, SubNetwork> subnetsTable = parseSubNetworks( jsonBufSubnet );
   		//Vector<Network> nets = new Vector<Network>();
   		try {
   			JSONObject jsonObject = new JSONObject( jsonBuf );
   			return jsonObject.getJSONObject("network").getString("id");   		
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
    private static Hashtable<String, SubNetwork> parseSubNetworks( String jsonBuf )  throws ParseException  {
    	//Log.d("PARSE", "subnet json="+jsonBuf);
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
    public static Vector<KeyPair> parseKeyPairs( String jsonBuf ) throws ParseException  {
	Vector<KeyPair> kpairs = new Vector<KeyPair>();
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray keypairs = (JSONArray)jsonObject.getJSONArray("keypairs");
	    //kpairs = new KeyPair[keypairs.length()];
	    for(int i =0; i<keypairs.length(); ++i) {
		JSONObject keypair = keypairs.getJSONObject(i).getJSONObject("keypair");
		String key  = keypair.getString("public_key");
		String fp   = keypair.getString("fingerprint");
		String name = keypair.getString("name");
		kpairs.add( new KeyPair( name, key, fp ) );
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
	    JSONArray secgroups = jsonObject.getJSONArray("security_groups");
	    
	    for(int i =0; i<secgroups.length(); ++i) {
		  JSONObject secgrp = secgroups.getJSONObject(i);
		  String id   = secgrp.getString("id");
		  String name = secgrp.getString("name");
		  String desc = secgrp.getString("description");
		  if(desc == null) desc ="";
		  //Vector<Rule> rules = parseRules( secgrp.getJSONArray("security_group_rules"));
		  secg.add( new SecGroup( name, id, desc/*, null*/ ) );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	  return secg;
    }

    /**
    *
    *
    *
    *
    */    
   public static Vector<SimpleSecGroupRule> parseSecGroupRules( String jsonBuf ) throws ParseException  {
	Vector<SimpleSecGroupRule> rulesV = new Vector<SimpleSecGroupRule>();
	//Log.d("PARSE", "jsonBuf="+jsonBuf);
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray rules = jsonObject.getJSONObject("security_group").getJSONArray("rules");
	    
	    for(int i =0; i<rules.length(); ++i) {
		  JSONObject rule = rules.getJSONObject(i);
		  String id = rule.getString("id");
		  String iprange = "";
		  if(rule.getJSONObject("ip_range").has("cidr"))
			  iprange = rule.getJSONObject("ip_range").getString("cidr");
		  String proto = rule.getString("ip_protocol");
		  int fromport = rule.getInt("from_port");
		  int toport = rule.getInt("to_port");
		  rulesV.add(new SimpleSecGroupRule(id, fromport, toport, proto, iprange) );
		  //Log.d("PARSE", "Rule="+id);
	    }
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
	  return rulesV;
   }
    
    /**
     *
     *
     *
     *
     */  
	public static String parseServerConsoleLog(String jsonBuf)  throws ParseException  {
		String consoleLog = null;
		try{
		    JSONObject jsonObject = new JSONObject( jsonBuf );
		    consoleLog = jsonObject.getString("output");
		    
		} catch(org.json.JSONException je) {
	 	    throw new ParseException( je.getMessage( ) );
	 	}
		return consoleLog;
	}

    /**
     *
     *
     *
     *
     */
	public static Vector<Volume> parseVolumes( String volumesJson, String serversJson)  throws ParseException  {
		
		//Log.d("PARSE", "volume json="+volumesJson);
		
		Vector<Server> servs = parseServers( serversJson );
		Hashtable<String, String> server_id_to_name_mapping = new Hashtable<String, String>();
		Iterator<Server> sit = servs.iterator();
		while(sit.hasNext()) {
			Server S = sit.next();
			server_id_to_name_mapping.put( S.getID(), S.getName() );
		}
		Vector<Volume> vols = new Vector<Volume>();
		try {
			JSONArray volArray = (new JSONObject( volumesJson )).getJSONArray("volumes");
			for(int i = 0; i<volArray.length(); i++) {
				JSONObject volume = volArray.getJSONObject(i);
				String name = volume.has("display_name") ? volume.getString("display_name") : "N/A";
				if(name.compareTo("N/A")==0) {
					name = volume.has("name") ? volume.getString("name") : "N/A";
				}
				String status = volume.has("status") ? volume.getString("status") : "N/A";
				boolean bootable = volume.has("bootable") ? volume.getBoolean("bootable") : false;
				boolean readonly = false;
				String attachmode = "rw";
				if(volume.has("metadata")) {
					JSONObject metadata = volume.getJSONObject("metadata");
					if(metadata.has("attached_mode"))
						attachmode = metadata.getString("attached_mode");
					if(metadata.has("readonly"))
					    readonly = metadata.getBoolean("readonly");
				}
				String ID = volume.getString("id");
				int size = volume.getInt("size");
				JSONArray attaches = volume.getJSONArray("attachments");
				String attached_serverid = null;
				String attached_servername = null;
				String attached_device = null;
				if(attaches.length()>0) {
					attached_serverid = attaches.getJSONObject(0).getString("server_id");
					attached_servername = server_id_to_name_mapping.get(attached_serverid);
					attached_device   = attaches.getJSONObject(0).getString("device");
				}
				Volume vol = new Volume(name, ID, status,
										bootable, readonly, attachmode,
										size, attached_serverid, attached_servername, attached_device );
				vols.add(vol);
			}
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
		return vols;
	}

	public static QuotaVol parseQuotaVolume(String jsonVols) throws ParseException  {
		
		try {
			JSONObject quota = (new JSONObject( jsonVols )).getJSONObject("quota_set");
			JSONObject giga = quota.getJSONObject("gigabytes");
			JSONObject vols = quota.getJSONObject("volumes");
			JSONObject snaps= quota.getJSONObject("snapshots");
			
			int volUsage = vols.getInt("in_use");
			int gigaUsage = giga.getInt("in_use");
			int snapUsage = snaps.getInt("in_use");
			int maxVols  = vols.getInt("limit");
			int maxGiga = giga.getInt("limit");
			int maxSnaps = snaps.getInt("limit");
			return new QuotaVol( volUsage, gigaUsage, snapUsage, maxVols, maxGiga, maxSnaps );
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
	}
	
}
