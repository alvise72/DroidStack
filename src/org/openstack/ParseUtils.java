package org.openstack;

import java.util.Hashtable;
import java.util.Calendar;
import java.util.TimeZone;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class ParseUtils {

    /**
     *
     *
     *
     *
     */    
    public static User getToken( String jsonString ) throws ParseException
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
	  String username = (String)((JSONObject)jsonObject.get("user")).get("username");
	  
	  SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  timeFormatter.setTimeZone( TimeZone.getDefault( ) );
	  Calendar calendar = Calendar.getInstance();
	  try {
	      calendar.setTime(timeFormatter.parse(expires));
	  } catch(java.text.ParseException pe) {
	      throw new ParseException( "Error parsing the expiration date ["+expires+"]" );
	  }
	  long expireTimestamp = calendar.getTimeInMillis() / 1000;
	  User U = new User( username, tenantname, tenantid, stoken, expireTimestamp );
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
    public static Hashtable<String, OpenStackImage> getImages( String jsonString ) throws ParseException
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
      String errorMEssage = null;
      try {
        jsonObject = new JSONObject( jsonBuf );
	errorMEssage = (String)jsonObject.getJSONObject("error").get("message");
      } catch(org.json.JSONException joe) {
        return "Cannot parse json error message from remote server";
      }
      
      return errorMEssage;
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
}
