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
    public static Vector<Server> parseServers( String jsonBuf )  throws ParseException {
// 	try {

// 	} catch(org.json.JSONException je) {
// 	    throw new ParseException( je.getMessage( ) );
// 	}
	return null;
    }
}
