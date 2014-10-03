package org.stackdroid.comm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.stackdroid.R;
import org.stackdroid.parse.ParseException;
import org.stackdroid.parse.ParseUtils;
import org.stackdroid.utils.Configuration;
import org.stackdroid.utils.Defaults;
import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;









import android.util.Log;
//import android.util.Log;
import android.util.Pair;
import android.widget.EditText;

public class OSClient {
    
    private static Hashtable<String, OSClient> instanceArray = null;
    
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
    	
    	if(instanceArray.containsKey(U.getFilename()))
    		return instanceArray.get( U.getFilename() );
	
    	OSClient osc = new OSClient(U);
    	instanceArray.put(U.getFilename(), osc );
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
    private void checkToken( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException {
    	//Log.d("OSC", "expiration="+U.getTokenExpireTime() + " - now="+Utils.now() + 5);
    	if(U.getTokenExpireTime() <= Utils.now() + 5) {
    			String jsonBuffer = RESTClient.requestToken( U.useSSL() ,
	    												 	 U.getIdentityEndpoint() + "/tokens",
	    												 	 U.getTenantName(),
	    												 	 U.getUserName(),
	    												 	 U.getPassword() );
    			String  pwd = U.getPassword();
    			String  edp = U.getIdentityEndpoint();
    			boolean ssl = U.useSSL();
    			U = ParseUtils.parseUser( jsonBuffer );
    			U.setPassword( pwd );
    			//U.setEndpoint( edp );
    			U.setSSL( ssl );
    			U.toFile( Configuration.getInstance().getValue("FILESDIR", Defaults.DEFAULTFILESDIR));
    		
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
    public void volumeAttach( String volumeID, String serverID ) 
    		throws NotAuthorizedException, NotFoundException, 
	   GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
	{
    	checkToken( );
		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
		Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
		vp.add( p );
		String extradata = "{\"volumeAttachment\": {\"device\": null, \"volumeId\": \"" + volumeID + "\"}}";
		RESTClient.sendPOSTRequest( U.useSSL(), 
									U.getCinder2Endpoint() + "/servers/" + serverID + "/os-volume_attachments", 
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
    public void volumeDetach( String volumeID, String serverID ) 
    		throws NotAuthorizedException, NotFoundException, 
	   GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
	{
    	checkToken( );
		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
		Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
		vp.add( p );
		//String extradata = "{\"volumeAttachment\": {\"device\": null, \"volumeId\": \"" + volumeID + "\"}}";
		RESTClient.sendDELETERequest( U.useSSL(), 
									  U.getCinder2Endpoint() + "/servers/" + serverID + "/os-volume_attachments/" + volumeID, 
									  U.getToken(), 
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
    public void createVolume( String volname, int size_in_GB ) 
    		throws NotAuthorizedException, NotFoundException, 
	   GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
	   {
    		checkToken( );
    		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    		Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    		vp.add( p );
    		String extradata = "{\"volume\": {\"display_name\": \"" + volname + "\", \"imageRef\": null, \"availability_zone\": null, \"volume_type\": null, \"display_description\": null, \"snapshot_id\": null, \"size\": " + size_in_GB + "}}";
    		RESTClient.sendPOSTRequest( U.useSSL(), 
    									U.getCinder2Endpoint() + "/volumes", 
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
    public void deleteVolume( String volID ) 
    		throws NotAuthorizedException, NotFoundException, 
	   GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
	   {
    	checkToken( );
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	//Log.d("OSC", "endpoint="+U.getCinder2Endpoint() + "/volumes/" + volID);
    	RESTClient.sendDELETERequest( U.useSSL(), 
    								  U.getCinder2Endpoint() + "/volumes/" + volID, 
    								  U.getToken(), 
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
    public String createNetwork( String netname, boolean shared ) 
	throws NotAuthorizedException, NotFoundException, 
		   GenericException, ServiceUnAvailableOrInternalError,
		   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"network\": {\"shared\": " + shared + ", \"name\": \"myNetTest\", \"admin_state_up\": true}}";
    	return RESTClient.sendPOSTRequest( U.useSSL(), 
									U.getNeutronEndpoint() + "/v2.0/networks.json", 
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
    public String createSubnetworkNetwork( String netID, String CIDR, String DNS, String startIP, String endIP ) 
	throws NotAuthorizedException, NotFoundException, 
		   GenericException, ServiceUnAvailableOrInternalError,
		   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"subnet\": {\"network_id\": \"" + netID + "\", \"ip_version\": 4, \"cidr\": \"" + CIDR + "\", \"dns_nameservers\": [\"" + DNS + "\"], \"allocation_pools\": [{\"start\": \"" + startIP + "\", \"end\": \"" + endIP + "\"}]}}";
    	return RESTClient.sendPOSTRequest( U.useSSL(), 
									U.getNeutronEndpoint() + "/v2.0/subnets.json", 
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
    public void createInstanceSnapshot( String serverID, String snapshotName ) 
	throws NotAuthorizedException, NotFoundException, 
		   GenericException, ServiceUnAvailableOrInternalError,
		   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"createImage\": {\"name\": \"" + snapshotName + "\", \"metadata\": {}}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getNovaEndpoint() + "/servers/" + serverID + "/action", 
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
	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"pool\": \"" + externalNetworkID + "\"}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getNovaEndpoint() + "/os-floating-ips", 
    								U.getToken(), 
    								extradata, 
    								vp );
    }
	

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     * 
     * 
     */
    public void createSecGroup( String secgrpName, String desc)  
    		throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError ,
 		   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"security_group\": {\"name\": \"" + secgrpName + "\", \"description\": \"" + desc + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getNovaEndpoint()  + "/os-security-groups", 
    								U.getToken(), 
    								extradata, 
    								vp );
    }
    
    /**
     * @throws ParseException 
     * 
     * 
     * 
     * 
     * 
     */
    public String requestVolumes( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	return RESTClient.sendGETRequest( U.useSSL(), 
    									  U.getCinder2Endpoint() + "/volumes/detail", 
    									  U.getToken( ), 
    									  vp );
    }

    /**
     * @throws ParseException 
     * 
     * 
     * 
     * 
     * 
     */
    public void requestFloatingIPAssociate( String fip, String serverid ) 
	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError ,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"addFloatingIp\": {\"address\": \"" + fip + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    			    U.getNovaEndpoint() + "/servers/"+serverid+"/action", 
				    			    U.getToken(), 
				    			    extradata, 
				    			    vp );   	
    }
    
    /**
     * @throws ParseException 
     * 
     * 
     *
     *
     *
    */
    public void requestFloatingIPRelease( String fip ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	RESTClient.sendDELETERequest( U.useSSL(), 
				      				  U.getNovaEndpoint() + "/os-floating-ips/" + fip, 
				      				  U.getToken(), 
				      				  vp );
    }
    
    /**
    *
    *
    * curl -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -H "Content-Type: application/json" http://cloud-areapd.pd.infn.it:9292/v2/images
     * @throws ParseException 
    *
    *
    *
    */
   public void requestReleaseFloatingIP( String floatingip, String serverid ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
   {
	   checkToken( );
	
       Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
       Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
       vp.add( p );
       
       String extradata = "{\"removeFloatingIp\": {\"address\": \"" + floatingip + "\"}}";
       
       RESTClient.sendPOSTRequest( U.useSSL(), 
    		   					   U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
    		   					   U.getToken(), 
    		   					   extradata, 
    		   					   vp );
   }

   /**
 * @throws ParseException 
    *
    *
    * 
    *
    *
    *
    */
   public String requestServerLog( String serverid, int maxlines ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
	   checkToken( );
	
	   Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	   Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	   vp.add( p );
	
	   return RESTClient.sendPOSTRequest( U.useSSL(), 
			   							  U.getNovaEndpoint() + "/servers/"+serverid+"/action",
			   							  U.getToken(), 
			   							  "{\"os-getConsoleOutput\": {\"length\": \"" + maxlines + "\"}}", 
			   							  vp );   
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestImages( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
       return RESTClient.sendGETRequest( U.useSSL(), 
    		   							 U.getGlanceEndpoint() + "/v2/images", 
    		   							 U.getToken(), 
    		   							 null );   
    }
    
    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestQuota( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
 	   Vector<Pair<String, String>> v = new Vector<Pair<String,String>>();
 	   v.add(p);
 	   return RESTClient.sendGETRequest( U.useSSL(), 
 			   							 U.getNovaEndpoint() + "/limits", 
 			   							 U.getToken(), 
 			   							 v );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestVolQuota( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
 	   Vector<Pair<String, String>> v = new Vector<Pair<String,String>>();
 	   v.add(p);
 	   return RESTClient.sendGETRequest( U.useSSL(),  
 			   							 U.getCinder2Endpoint() + "/os-quota-sets/"+U.getTenantID( )+"?usage=True", 
 			   							 U.getToken(), 
 			   							 v );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestFloatingIPs( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
 	   Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
 	   v.add(p);
 	   return RESTClient.sendGETRequest( U.useSSL(), 
    									 U.getNovaEndpoint() + "/os-floating-ips", 
    									 U.getToken(), 
    									 v );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestServers( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
 	   Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
 	   v.add(p);
 	   return RESTClient.sendGETRequest( U.useSSL(),
    									 U.getNovaEndpoint() + "/servers/detail", //?all_tenants=1",
    									 U.getToken(), 
    									 v );
    }


    /**
     * @throws ParseException 
     *
     *
     *
     *
     *
     */
    public String requestFlavors( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
 	   Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
 	   v.add(p);
 	   return RESTClient.sendGETRequest( U.useSSL(),
    									 U.getNovaEndpoint() + "/flavors/detail",
    									 U.getToken(),
    									 v );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     * 
     *
     *
     */
    public void deleteGlanceImage( String imageID ) 
    	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
  	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
 	   RESTClient.sendDELETERequest( U.useSSL(),
					   				  U.getGlanceEndpoint() + "/v2/images/" + imageID, 
					   				  U.getToken( ),
					   				  null );
    }


    /**
     * @throws ParseException 
     *
     *
     * 
     * 
     *
     *
     */
    public void deleteRule( String ruleID ) 
    	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
  	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	RESTClient.sendDELETERequest( U.useSSL(),
					   				  U.getNovaEndpoint() + "/os-security-group-rules/" + ruleID, 
					   				  U.getToken( ),
					   				  null );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     * 
     *
     *
     */
    public void deleteInstance( String serverID ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	RESTClient.sendDELETERequest( U.useSSL(), 
    								  U.getNovaEndpoint() + "/servers/" + serverID, 
    								  U.getToken(),
    								  null );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestNetworks( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(),  
    									  U.getNeutronEndpoint() + "/v2.0/networks",
    									  U.getToken(), 
    									  v );
    }

    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String requestSubNetworks( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(), 
    									  U.getNeutronEndpoint() + "/v2.0/subnets",
    									  U.getToken(), 
    									  v );
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public String requestKeypairs( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(),  
					  					  U.getNovaEndpoint() + "/os-keypairs",
					  					  U.getToken(), 
					  					  v );
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public String requestSecGroups( ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	String buf = RESTClient.sendGETRequest( U.useSSL(), 
											    U.getNovaEndpoint() + "/os-security-groups",
											    U.getToken(), 
											    null );
    	return buf;
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public String requestSecGroupListRules( String secgrpID ) throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	String buf = RESTClient.sendGETRequest( U.useSSL(), 
    											U.getNovaEndpoint() + "/os-security-groups/" + secgrpID,
    											U.getToken(), 
    											null );
		//Log.d("OSCLIENT", "buf="+buf);
		return buf;
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public void deleteSecGroup( String secgrpID ) 
    	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
  	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName( ) );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	RESTClient.sendDELETERequest( U.useSSL(), 
    								  U.getNovaEndpoint() + "/os-security-groups/" + secgrpID,
    								  U.getToken(),
    								  v );
    }    

    /**
     * @throws ParseException 
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
    	throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
 	   IOException, MalformedURLException, ProtocolException, ParseException
    {
 	   checkToken( );
 		
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
		     						 U.getNovaEndpoint() + "/servers",
				  					 U.getToken(), 
				  					 data, 
				  					 v );
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
	public void createRule(String secgrpID, int fromPort, int toPort, String protocol, String cidr) 
			throws NotAuthorizedException, NotFoundException, GenericException, ServiceUnAvailableOrInternalError,
			   IOException, MalformedURLException, ProtocolException, ParseException
	{
		checkToken( );
		
		Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"security_group_rule\": {\"from_port\": " + fromPort + ", \"ip_protocol\": \"" + protocol + "\", \"to_port\": " + toPort + ", \"parent_group_id\": \"" + secgrpID + "\", \"cidr\": \"" + cidr + "\", \"group_id\": null}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
    								U.getNovaEndpoint() + "/os-security-group-rules", 
    								U.getToken(), 
    								extradata, 
    								vp );
	}
}
