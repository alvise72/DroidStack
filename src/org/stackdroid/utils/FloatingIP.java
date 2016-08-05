package org.stackdroid.utils;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

import android.util.Log;

public class FloatingIP {
    private String IP;
    private String fixedIP;
    private String ID;
    private String attachedTo = null;
    private String poolName;
    private String serverName = null;
    //private String serverID;
    
    public FloatingIP( String IP, String fixedIP, String ID, String instanceID, String poolName) {
	  this.IP = IP;
	  this.fixedIP = fixedIP;
  	  this.attachedTo = instanceID;
	  this.ID   = ID;
	  this.poolName = poolName;
	  //this.serverID = instanceID;
	  //Log.d("FLOATINGIP", "attachedTo="+attachedTo);
    }

    @Override
    public String toString( ) {
    	return IP;
/*	return "FloatingIP{IP=" + IP
		+ ",fixed IP=" + fixedIP
	    + ",pool=" + poolName
	    + ",server="+attachedTo
	    + "}";*/
    }

    public String getPoolName( ) { return poolName; }
    public String getID( ) { return ID; }
    public String getIP( ) { return IP; }
    public String getFixedIP( ) { return fixedIP; }
    public String getServerName( ) { return serverName; }
    public String getServerID( ) { return attachedTo; }
    //public String getServerID( ) { return serverID; }
    public void setServerName( String name ) { serverName=name; }
    public boolean isAssociated( ) {
      if(attachedTo!=null && attachedTo.length()!=0 && attachedTo.compareTo("null")!=0)	
        return true;
      else
    	 return false;
    }
    
    /**
    *
    *
    *
    *
    */    
   public static Vector<FloatingIP> parse( String jsonBuf, boolean only_unassigned ) throws ParseException {
   	
	  try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray fips = jsonObject.getJSONArray( "floating_ips" );
	    //JSONArray fips = jsonObject.getJSONArray( "floatingips" ); // string floatingips if neutron endpoint is used
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
	    	if(only_unassigned)
	    		if(Fip.isAssociated())
	    			continue;
	    	res.add( Fip );
	    }
	    return res;
     } catch(org.json.JSONException je) {
	    throw new ParseException( "FloatingIP.parse: "+je.getMessage( ) );
 	  }
   }
}
