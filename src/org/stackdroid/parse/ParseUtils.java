package org.stackdroid.parse;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class ParseUtils {

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
		if(jsonObject.get("NeutronError") instanceof String) {
			return jsonObject.getString("NeutronError");
		}
		if(jsonObject.get("NeutronError") instanceof JSONObject) {
			JSONObject NE = (JSONObject)jsonObject.get("NeutronError");
			if(NE.has("message")) {
				return NE.getString("message");
			} else return "Cannot parse Neutron server's error message";
		}
		return "Cannot parse Neutron server's error message";
  	  } catch (JSONException e) {
  		return "Cannot parse Neutron server's error message";
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
   public static String parseSingleNetwork( String jsonBuf)  throws ParseException  {
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
	public static String getCurrentAPI( String jsonBuffer )  throws ParseException  {
	  try {
   			JSONObject jsonObject = new JSONObject( jsonBuffer );
   			JSONArray versions = jsonObject.getJSONArray("versions");
   			
   			for(int i = 0; i<versions.length(); ++i ) {
   			  JSONObject element = versions.getJSONObject( i );
   			  if(element.getString("status").compareTo("CURRENT")==0) {
   			    JSONObject link = element.getJSONArray("links").getJSONObject(0);
   			    String api = link.getString("href");
   			    if(api.endsWith("/"))
   			      api = api.substring(0, api.length()-1);
   			    return api;
   			  }
   			}
   			
   			//return jsonObject.getJSONObject("network").getString("id");   		
   		} catch(org.json.JSONException je) {
   			throw new ParseException( je.getMessage( ) );
   		}
   	  return null;
	}
}
