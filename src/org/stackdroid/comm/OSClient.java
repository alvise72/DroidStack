package org.stackdroid.comm;

import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stackdroid.R;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;

import android.util.Log;
import android.util.Pair;

public class OSClient {
	private static Hashtable<String, OSClient> instanceArray = null;//new Hashtable<String, OSClient>( );
	
	User U = null;

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	synchronized public static OSClient getInstance( User U ) {
		if(instanceArray==null) instanceArray = new Hashtable<String, OSClient>( );
		Log.d("OSCLIENT", "User="+U+" - ID="+U.getUserID( ));
		if(instanceArray.containsKey(U.getUserID()))
			return instanceArray.get( U.getUserID() );
		
		OSClient osc = new OSClient(U);
		instanceArray.put(U.getUserID( ), osc );
		return osc;
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private OSClient( User U ) {
		this.U = U;
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private void checkToken( ) throws RuntimeException {
		if(U.getTokenExpireTime() <= Utils.now() + 5) {
			try {
			    String jsonBuffer = RESTClient.requestToken( U.useSSL() ,
			    										   U.getEndpoint(),
			    										   U.getTenantName(),
			    										   U.getUserName(),
			    										   U.getPassword() );
			    String  pwd = U.getPassword();
			    String  edp = U.getEndpoint();
			    boolean ssl = U.useSSL();
			    U = ParseUtils.parseUser( jsonBuffer, U.getContext( ) );
			    U.setPassword( pwd );
			    U.setEndpoint( edp );
			    U.setSSL( ssl );
			    U.toFile( Utils.getStringPreference("FILESDIR","", U.getContext( ) ) );// to save new token + expiration
			} catch(Exception e) {
				throw new RuntimeException( e.getMessage( ) );			}
		    }
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void createInstanceSnapshot( String serverID, String snapshotName ) 
			throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
	{
		checkToken( );
		
		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"createImage\": {\"name\": \"" + snapshotName + "\", \"metadata\": {}}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				 		 		    U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/" + serverID + "/action", 
				 		 		    U.getToken(), 
				 		 		    extradata, 
				 		 		    vp );
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void requestFloatingIPAllocation( String externalNetworkID ) 
			throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
		checkToken( );
		
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"pool\": \"" + externalNetworkID + "\"}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-floating-ips", 
    								U.getToken(), 
    								extradata, 
    								vp );
    }
	

    /**
     *
     *
     * 
     *
     * 
     * 
     */
    public void createSecGroup( String secgrpName, String desc)  
    		throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"security_group\": {\"name\": \"" + secgrpName + "\", \"description\": \"" + desc + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-security-groups", 
    								U.getToken(), 
    								extradata, 
    								vp );
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     */
    public String requestVolumes( ) throws RuntimeException {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
		Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
		vp.add( p );
		return RESTClient.sendGETRequest( U.useSSL(), U.getEndpoint() + ":8776/v1/" + U.getTenantID() + "/volumes/detail", U.getToken( ), vp );
    }

    /**
     * 
     * 
     * 
     * 
     * 
     */
    public void requestFloatingIPAssociate( String fip, String serverid ) 
    		throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"addFloatingIp\": {\"address\": \"" + fip + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/"+serverid+"/action", 
    								U.getToken(), 
    								extradata, 
    								vp );   	
    }
    
    /**
     * 
     * 
     *
     *
     *
    */
    public void requestFloatingIPRelease( String fip ) 
    	throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	
    	RESTClient.sendDELETERequest( U.useSSL(), 
    								  U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-floating-ips/" + fip, 
    								  U.getToken(), 
    								  vp );
    }
    
    /**
    *
    *
    * curl -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -H "Content-Type: application/json" http://cloud-areapd.pd.infn.it:9292/v2/images
    *
    *
    *
    */
   public void requestReleaseFloatingIP( String floatingip, String serverid )
      throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
   {
	   Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	   Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	   vp.add( p );
	   
	   String extradata = "{\"removeFloatingIp\": {\"address\": \"" + floatingip + "\"}}";
	   
	   RESTClient.sendPOSTRequest( U.useSSL(), 
			   					   U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/" + serverid + "/action", 
			   					   U.getToken(), 
			   					   extradata, 
			   					   vp );
   }

   /**
    *
    *
    * 
    *
    *
    *
    */
   public String requestServerLog( String serverid )
	   throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException  
   {
	   Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	   Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	   vp.add( p );
	    
	   return RESTClient.sendPOSTRequest( U.useSSL(), 
			  							  U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/servers/"+serverid+"/action",
			  							  U.getToken(), 
			  							  "{\"os-getConsoleOutput\": {\"length\": null}}", 
			  							  vp );   
   }

    /**
     *
     *
     * 
     *
     *
     */
    public String requestImages( ) throws RuntimeException  
    {
	  return RESTClient.sendGETRequest( U.useSSL(), U.getEndpoint() + ":9292/v2/images", U.getToken(), null );   
    }
    
    /**
     *
     *
     * 
     *
     *
     */
    public String requestQuota( ) throws RuntimeException
    {
    	Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String,String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(),  U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/limits", U.getToken(), v);
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/limits -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public String requestFloatingIPs( ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return RESTClient.sendGETRequest( U.useSSL(), U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/os-floating-ips", U.getToken(), v);
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/servers/detail -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public String requestServers( ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return RESTClient.sendGETRequest( U.useSSL(),
			  							U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/detail?all_tenants=1",
			  							U.getToken(), 
			  							v );
    }


    /**
     *
     *
     *
     *
     *
     */
    public String requestFlavors( ) throws RuntimeException
    {
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(),
    									  U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/flavors/detail",
    									  U.getToken(),
    									  v );
    }

    /**
     *
     *
     * 
     * 
     *
     *
     */
    public void deleteGlanceImage( String imageID ) 
    	throws RuntimeException, NotFoundException
    {
    	try {
    		RESTClient.sendDELETERequest(  U.useSSL(),
    									   U.getEndpoint() + ":9292/v2/images/" + imageID, 
    									   U.getToken( ),
    									   null );
    	} catch(NotAuthorizedException na) {
    		throw new RuntimeException(na.getMessage() + "\n\n" + U.getContext().getString(R.string.PLEASECHECKCREDSFORIMAGE) );
    	}
    }

    /**
     *
     *
     * 
     * 
     *
     *
     */
    public void deleteInstance( String serverID ) throws RuntimeException, NotFoundException
    {
    	try {
    		RESTClient.sendDELETERequest( U.useSSL(), 
    									  U.getEndpoint() + ":8774/v2/" + U.getTenantID()+ "/servers/" + serverID, 
    									  U.getToken(),
    									  null );
    	} catch(NotAuthorizedException na) {
    		throw new RuntimeException(na.getMessage() + "\n\n" + U.getContext().getString(R.string.PLEASECHECKCREDSFORINSTANCE) );
    	}
    }

    /**
     *
     *
     * 
     *
     *
     */
    public String requestNetworks( ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return RESTClient.sendGETRequest( U.useSSL(),  
			  							U.getEndpoint() + ":9696/v2.0/networks",
			  							U.getToken(), 
			  							v );
    }

    /**
     *
     *
     * 
     *
     *
     */
    public String requestSubNetworks( ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return RESTClient.sendGETRequest( U.useSSL(), 
			  							U.getEndpoint() + ":9696/v2.0/subnets",
			  							U.getToken(), 
			  							v );
    }

    /**
     *
     *
     *
     * 
     *
     */
    public String requestKeypairs( ) throws RuntimeException 
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return RESTClient.sendGETRequest( U.useSSL(),  
			  							U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-keypairs",
			  							U.getToken(), 
			  							v );
    }

    /**
     *
     *
     *
     * 
     *
     */
    public String requestSecGroups( ) throws RuntimeException 
    {
	  return RESTClient.sendGETRequest( U.useSSL(), 
			  							U.getEndpoint() + ":9696/v2.0/security-groups.json",
			  							U.getToken(), 
			  							null );
    }

    /**
     *
     *
     *
     * 
     *
     */
    public void deleteSecGroup( String secgrpID ) 
    	throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName( ) );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  RESTClient.sendDELETERequest( U.useSSL(), 
			  						U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-security-groups/" + secgrpID,
			  						U.getToken(),
			  						v );
    }    

    /**
     *
     *
     *
     * 
     *
     */
    public void requestInstanceCreation( String instanceName, 
    									 String imageID,
    									 String key_name,
    									 String flavorID,
    									 int count,
    									 String securityGroupID,
    									 Hashtable<String, String> netID_to_netIP )
    	throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName( ) );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	String data = "{\"server\": {\"name\": \"" + instanceName + 
    		    "\", \"imageRef\": \"" + imageID + 
    		    "\", " + (key_name != null ? "\"key_name\": \"" + key_name : "") + 
    		    "\", \"flavorRef\": \"" + flavorID + 
    		    "\", \"max_count\": " + count + 
    		    ", \"min_count\": " + count + "}}";
    	JSONObject obj = null;
    	//Log.d("RESTClient", "_secgrpIDs=["+_secgrpIDs+"]");
    	String[] secgrpIDs = securityGroupID.split(",");
    	//String[] networkIDs = _networkIDs.split(",");
    	try {
    	    obj = new JSONObject( data );
    	    JSONArray secgs = new JSONArray();
    	    JSONArray nets = new JSONArray();
    	    if(securityGroupID.length()!=0) 
    		for(int i = 0; i<secgrpIDs.length; ++i)
    		    secgs.put( new JSONObject("{\"name\": \"" + secgrpIDs[i] + "\"}") );


    	    {
    		Iterator<String> it = netID_to_netIP.keySet().iterator();
    		while( it.hasNext() ) {
    		    String netID = it.next( );
    		    String netIP = netID_to_netIP.get( netID );
    		    if( netIP != null && netIP.length()!=0) 
    			nets.put( new JSONObject("{\"uuid\": \"" + netID + "\", \"fixed_ip\":\"" + netIP + "\"}") );
    		    else
    			nets.put( new JSONObject("{\"uuid\": \"" + netID + "\"}") );
    		}
    	    }



    	    obj.getJSONObject("server").put("security_groups", secgs);
    	    obj.getJSONObject("server").put("networks", nets);
    	    
    	} catch(JSONException je) {
    		throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
    	}
    	
    	data = obj.toString( );
    	 RESTClient.sendPOSTRequest( U.useSSL(), 
		     						 U.getEndpoint() + ":8774/v2/" + U.getTenantID( ) + "/servers",
				  					 U.getToken(), 
				  					 data, 
				  					 v );
    }
}