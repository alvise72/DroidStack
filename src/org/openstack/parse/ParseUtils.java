package org.openstack.parse;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.openstack.utils.OpenStackImage;
import org.openstack.utils.Server;
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

	  SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  timeFormatter.setTimeZone( TimeZone.getDefault( ) );
	  Calendar calendar = Calendar.getInstance();
	  try {
	      calendar.setTime(timeFormatter.parse(expires));
	  } catch(java.text.ParseException pe) {
	      throw new ParseException( "Error parsing the expiration date ["+expires+"]" );
	  }
	  long expireTimestamp = calendar.getTimeInMillis() / 1000;
	  User U = new User( username, userID, tenantname, tenantid, stoken, expireTimestamp );
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
    public static Hashtable<String, org.openstack.utils.OpenStackImage> parseImages( String jsonString ) throws ParseException
    {
      try {
        Hashtable<String, OpenStackImage> result = new Hashtable<String, OpenStackImage>();
        
        JSONObject jsonObject = new JSONObject( jsonString );
        JSONArray images      = (JSONArray)jsonObject.getJSONArray("images");
      
        for(int i=0; i<images.length( ); ++i ) {
	
          String name         = (String)images.getJSONObject(i).get("name");
	  long   size         = (long)images.getJSONObject(i).getLong("size");
	  String format       = (String)images.getJSONObject(i).get("disk_format");
	  String creationDate = (String)images.getJSONObject(i).get("created_at");
	  String visibility   = (String)images.getJSONObject(i).get("visibility");
	  String status       = (String)images.getJSONObject(i).get("status");
	  
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
	  
	  OpenStackImage img = new OpenStackImage( name, size, format, status, pub, cdate );
	  
	  if(format.compareToIgnoreCase("ari")!=0 && format.compareToIgnoreCase("aki") != 0)
	    result.put( name, img );
	  
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
      Log.d("ParseUtils.getErrorCodeMessage", "Returning: "+errorMessage);
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
      Log.d("ParseUtils.getErrorCode", "Returning: "+errorCode);
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
    public static Vector<Server> parseServers( String jsonBuf, String username )  throws ParseException {
	Vector<Server> serverVector = new Vector();
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray servers     = (JSONArray)jsonObject.getJSONArray("servers");

	    for(int i=0; i<servers.length( ); ++i) {
		JSONObject server = (JSONObject)servers.getJSONObject(i);
		String status     = (String)server.getString("status");
		String keyname    = (String)server.getString("key_name");
		JSONArray secgarray  =  ((JSONArray)server.getJSONArray("security_groups"));
		String secgrps[] = new String[secgarray.length()];
		for(int j=0; j<secgarray.length(); j++)
		    secgrps[j] = (String)((JSONObject)secgarray.getJSONObject(j)).getString("name");

		String flavorID = (String)((JSONObject)server.getJSONObject("flavor")).getString("id");
		String ID = (String)server.getString("id");
		//		String status = (String)server.get("OS-EXT-STS:vm_state");
		String computeNode = (String)server.getString("OS-EXT-SRV-ATTR:hypervisor_hostname");
		String name = (String)server.getString("name");
		String task = (String)server.getString("OS-EXT-STS:task_state");
		JSONObject addresses = (JSONObject)server.getJSONObject("addresses");
		JSONArray user_addresses = (JSONArray)addresses.getJSONArray( username );

		String privIP = (String)((JSONObject)user_addresses.getJSONObject(0)).getString("addr");
		String pubIP = null;
		if(user_addresses.length()>1)
		    pubIP = (String)((JSONObject)user_addresses.getJSONObject(1)).getString("addr");
		String creation = (String)server.getString("created");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		timeFormatter.setTimeZone( TimeZone.getDefault( ) );
		Calendar calendar = Calendar.getInstance();
		try {
		    calendar.setTime(timeFormatter.parse(creation));
		} catch(java.text.ParseException pe) {
		    throw new ParseException( "Error parsing the creation date ["+creation+"]" );
		}
		long creationTime = calendar.getTimeInMillis() / 1000;
		int power = (int)server.getInt("OS-EXT-STS:power_state");
		Server S = new Server(name,ID,status,task,power,privIP,pubIP,computeNode,keyname,flavorID,secgrps,creationTime);
		serverVector.add(S);
	    }
 	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	return serverVector;
    }
}
