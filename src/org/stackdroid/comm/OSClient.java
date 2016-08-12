package org.stackdroid.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
import android.util.Pair;
import android.widget.EditText;

public class OSClient {
    
    private static Hashtable<String, OSClient> instanceArray = null;
    
    User U = null;
    
    /**
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
    
    /**
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
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    private void checkToken( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
				      IOException, ParseException,CertificateException {
	
	String gapiver = U.getGlanceEndpointAPIVER( );
	String napiver = U.getNeutronEndpointAPIVER( );
	    
	//Log.d("OSClient.checkToken", "GLANCEAPIVER="+gapiver+" - NEUTRONAPIVER="+napiver);
	
	if(U.getVerifyServerCert()) {
	    //Log.d("OSCLIENT", "Verify server's cert="+U.getVerifyServerCert());
	    X509Certificate cert = null;
	    cert = (X509Certificate)(CertificateFactory.getInstance("X.509")).generateCertificate(new FileInputStream( U.getCAFile() ));
	    if(RESTClient.checkServerCert(U.getIdentityEndpoint(), cert.getIssuerX500Principal().getName()) == false)
		throw new CertificateException("Couldn't verify server's certificate. Please verify the correct CA selected.");
	}

	long exp_time = U.getTokenExpireTime();

    	if(exp_time <= Utils.now() + 5) {
	    String payload = null;
	    if(U.useV3())
		payload = "{ \"auth\": { \"identity\": { \"methods\": [\"password\"],\"password\": { \"user\": { \"name\": \"" + U.getUserName() + "\",\"domain\": { \"id\": \"default\" }, \"password\": \"" + U.getPassword() + "\" } } }, \"scope\": { \"project\": { \"name\": \"" +  U.getTenantName() + "\", \"domain\": { \"id\": \"default\" } }}}}";
	    else
		payload = "{\"auth\": {\"tenantName\": \""
		    + U.getTenantName() 
		    + "\", \"passwordCredentials\": {\"username\": \"" 
		    + U.getUserName( ) + "\", \"password\": \"" 
		    + U.getPassword() + "\"}}}";
	    
	    String identityEP = U.getIdentityEndpoint();
	    if(U.useV3() )
	    	identityEP += "/auth";
	    identityEP += "/tokens";
	    
	    Pair<String,String> jsonBuffer_Token = RESTClient.requestToken(U.useSSL(),
				      												   identityEP,
									   								   payload);
	    
	    String  pwd = U.getPassword();
	    String  edp = U.getIdentityEndpoint();
	    boolean ssl = U.useSSL();
	    boolean verifyServerCert = U.getVerifyServerCert();
	    String CAFile = U.getCAFile();


		//Log.d("OSClient.checkToken", "BEFORE PARSE GLANCEAPIVER="+U.getGlanceEndpointAPIVER()+" - NEUTRONAPIVER="+U.getNeutronEndpointAPIVER());
	    U = User.parse( jsonBuffer_Token.first, U.useV3( ), jsonBuffer_Token.second );
	    U.setPassword( pwd);
	    U.setSSL(ssl);
	    //U.toFile(Configuration.getInstance().getValue("FILESDIR", Defaults.DEFAULTFILESDIR));
	    U.setCAFile( CAFile );
	    //Log.d("OSClient.checkToken", "SETTING GLANCEAPIVER="+gapiver+" - NEUTRONAPIVER="+napiver);
	    U.setGlanceEndpointAPIVER( gapiver );
     	U.setNeutronEndpointAPIVER( napiver );
		U.toFile(Configuration.getInstance().getValue("FILESDIR", Defaults.DEFAULTFILESDIR));
		//Log.d("OSClient.checkToken", "AFTER PARSE GLANCEAPIVER="+U.getGlanceEndpointAPIVER()+" - NEUTRONAPIVER="+U.getNeutronEndpointAPIVER());
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
    public String getCurrentAPIVersion( String endpoint ) throws NotAuthorizedException, NotFoundException,
								ServerException, ServiceUnAvailableOrInternalError,
								IOException, ParseException,CertificateException
    {
      return RESTClient.sendGETRequestWithoutToken(U.useSSL(),
				       		   endpoint );
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public void removeFIP( String serverid, String fip ) throws NotAuthorizedException, NotFoundException,
								ServerException, ServiceUnAvailableOrInternalError,
								IOException, ParseException,CertificateException
    {
	checkToken( );
  	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add(p);
    	String extradata = "{\"removeFloatingIp\": {\"address\": \"" + fip + "\"}}";
    	RESTClient.sendPOSTRequest(U.useSSL(),
				   U.getNovaEndpoint() + "/servers/" + serverid + "/action",
				   U.getToken(),
				   extradata,
				   vp);
    } 
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public String requestRouterShow( String routerID ) throws NotAuthorizedException, NotFoundException,
							      ServerException, ServiceUnAvailableOrInternalError,
							      IOException, ParseException,CertificateException
    {
	checkToken( );
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add(p);
	return RESTClient.sendGETRequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER() + "/routers/" + routerID + ".json",
					 U.getToken(),
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
    public String clearRouterGateway( String routerID ) throws NotAuthorizedException, NotFoundException,
							       ServerException, ServiceUnAvailableOrInternalError,
							       IOException, ParseException,CertificateException
    {
	checkToken( );
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add(p);
	String extradata = "{\"router\": {\"external_gateway_info\": {}}}";
	return RESTClient.sendPUTRequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers/" + routerID + ".json",
					 U.getToken(),
					 extradata,
					 vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public String setRouterGateway( String routerID, String netID ) throws NotAuthorizedException, NotFoundException,
									   ServerException, ServiceUnAvailableOrInternalError,
									   IOException, ParseException,CertificateException
    {
	checkToken( );
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add(p);
	String extradata = "{\"router\": {\"external_gateway_info\": {\"network_id\": \"" + netID + "\"}}}";
	return RESTClient.sendPUTRequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers/" + routerID + ".json",
					 U.getToken(),
					 extradata,
					 vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public String requestRouters( ) throws NotAuthorizedException, NotFoundException,
					   ServerException, ServiceUnAvailableOrInternalError,
					   IOException, ParseException,CertificateException
    {
	checkToken();
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add(p);
	
	//Log.d("OSC", "Calling sedGETRequest...");
	return RESTClient.sendGETRequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers.json",
					 U.getToken(),
					 vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public String requestRouterPorts( String routerID ) throws NotAuthorizedException, NotFoundException,
							       ServerException, ServiceUnAvailableOrInternalError,
							       IOException, ParseException,CertificateException
    {
	checkToken();
	//Log.d("OSCLIENT", "routerID="+routerID);
	return RESTClient.sendGETRequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/ports.json?device_id=" + routerID,
					 U.getToken(),
					 new Vector<Pair<String, String>>());
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public void deleteRouterInterface( String routerID, String subnetID ) throws NotAuthorizedException, NotFoundException,
										 ServerException, ServiceUnAvailableOrInternalError,
										 IOException, ParseException,CertificateException
    {
	checkToken();
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	String extradata = "{\"subnet_id\": \"" + subnetID + "\"}";
	//Log.d("OSC","routerID="+routerID);
	RESTClient.sendPUTRequest(U.useSSL(),
				  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers/" + routerID + "/remove_router_interface.json",
				  U.getToken(),
				  extradata,
				  vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public void addRouterInterface( String routerID, String subnetID ) throws NotAuthorizedException, NotFoundException,
									      ServerException, ServiceUnAvailableOrInternalError,
									      IOException, ParseException,CertificateException
    {
	checkToken();
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	String extradata = "{\"subnet_id\": \"" + subnetID + "\"}";
	//Log.d("OSC","routerID="+routerID);
	RESTClient.sendPUTRequest(U.useSSL(),
				  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers/" + routerID + "/add_router_interface.json",
				  U.getToken(),
				  extradata,
				  vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public void deleteRouter( String routerID ) throws NotAuthorizedException, NotFoundException,
						       ServerException, ServiceUnAvailableOrInternalError,
						       IOException, ParseException,CertificateException
    {
	checkToken( );
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	RESTClient.sendDELETERequest(U.useSSL(),
				     U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers/" + routerID + ".json",
				     U.getToken(),
				     vp);
    }
    
    /**
     *
     *
     *
     *
     *
     *
     */
    public void createRouter( String routerName ) throws NotAuthorizedException, NotFoundException,
							 ServerException, ServiceUnAvailableOrInternalError,
							 IOException, ParseException,CertificateException
    {
	checkToken();
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	String extradata = "{\"router\": {\"name\": \"" + routerName + "\", \"admin_state_up\": true}}";
	vp.add( p );
	RESTClient.sendPOSTRequest(U.useSSL(),
				   U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/routers.json",
				   U.getToken(),
				   extradata,
				   vp);
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void volumeAttach( String volumeID, String serverID ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
    	
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	String extradata = "{\"volumeAttachment\": {\"device\": null, \"volumeId\": \"" + volumeID + "\"}}";
	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverID + "/os-volume_attachments", 
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
    public void volumeDetach( String volumeID, String serverID ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	//String extradata = "{\"volumeAttachment\": {\"device\": null, \"volumeId\": \"" + volumeID + "\"}}";
	RESTClient.sendDELETERequest(U.useSSL(),
				     U.getNovaEndpoint() + "/servers/" + serverID + "/os-volume_attachments/" + volumeID,
				     U.getToken(),
				     vp);
    }
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void createVolume( String volname, int size_in_GB ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
	checkToken( );
	String cinderEP = null;
	if(U.getCinder2Endpoint()!=null)
	    cinderEP = U.getCinder2Endpoint();
	else
	    cinderEP = U.getCinder1Endpoint();
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
	String extradata = "{\"volume\": {\"display_name\": \"" + volname + "\", \"imageRef\": null, \"availability_zone\": null, \"volume_type\": null, \"display_description\": null, \"snapshot_id\": null, \"size\": " + size_in_GB + "}}";
	RESTClient.sendPOSTRequest(U.useSSL(),
				   cinderEP + "/volumes",
				   U.getToken(),
				   extradata,
				   vp);
    }
    
    
    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void deleteVolume( String volID ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
    	String cinderEP = null;
    	if(U.getCinder2Endpoint()!=null)
	    cinderEP = U.getCinder2Endpoint();
    	else
	    cinderEP = U.getCinder1Endpoint();
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	//Log.d("OSC", "endpoint="+U.getCinder2Endpoint() + "/volumes/" + volID);
    	RESTClient.sendDELETERequest(U.useSSL(),
				     cinderEP + "/volumes/" + volID,
				     U.getToken(),
				     vp);
    }



    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void deleteNetwork( String netID ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	//String extradata = "{\"network\": {\"shared\": " + shared + ", \"name\": \"" + netname + "\", \"admin_state_up\": true}}";
    	//Log.d("REST/createNetwork", "extradata="+extradata);
    	RESTClient.sendDELETERequest( U.useSSL(), 
				      U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/networks/"+netID, 
				      U.getToken(), 
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
    public String createNetwork( String netname, boolean shared )
	throws NotAuthorizedException, NotFoundException,
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"network\": {\"shared\": " + shared + ", \"name\": \"" + netname + "\", \"admin_state_up\": true}}";
    	//Log.d("REST/createNetwork", "extradata="+extradata);
    	return RESTClient.sendPOSTRequest( U.useSSL(), 
					   U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/networks.json", 
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
    public void createSubnetwork( String netID, String CIDR, String DNS, String startIP, String endIP, String gatewayIP, boolean enableDHCP )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"subnet\": {\"network_id\": \"" + netID + "\", \"ip_version\": 4, \"enable_dhcp\": " + enableDHCP  + ", \"cidr\": \"" + CIDR + "\", \"dns_nameservers\": [\"" + DNS + "\"], \"gateway_ip\":\"" + gatewayIP + "\", \"allocation_pools\": [{\"start\": \"" + startIP + "\", \"end\": \"" + endIP + "\"}]}}";
    	//Log.d("OSC", "extradata="+extradata);
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/subnets.json", 
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
    public void createInstanceSnapshot( String serverID, String snapshotName ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
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

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     */
    public void softReboot( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"reboot\": {\"type\": \"SOFT\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void startInstance( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"os-start\": null}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void stopInstance( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"os-stop\": null}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void suspendServer( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"suspend\": \"null\"}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void resumeServer( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"resume\": null}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void hardReboot( String serverid )
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"reboot\": {\"type\": \"HARD\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNovaEndpoint() + "/servers/" + serverid + "/action", 
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
    public void allocateFloatingIP( String externalNetworkID ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, 
	       ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"floatingip\": {\"floating_network_id\": \"" + externalNetworkID + "\"}}";
    	RESTClient.sendPOSTRequest( U.useSSL(), 
				    U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/floatingips.json",//os-floating-ips", 
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
	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError ,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	//Log.v("OSClient.createSecGroup", "USING NEUTRON SEC GROUP");
    	if(U.getNeutronEndpoint()!=null && U.getNeutronEndpoint().length() > 1) {
	    //Log.v("OSClient.createSecGroup", "USING NEUTRON SEC GROUP");
	    Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	    Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	    vp.add( p );
	    String extradata = "{\"security_group\": {\"name\": \"" + secgrpName + "\", \"description\": \"" + desc + "\"}}";
	    RESTClient.sendPOSTRequest( U.useSSL(), 
    					U.getNeutronEndpoint()  + "/" + U.getNeutronEndpointAPIVER( ) + "/security-groups.json", 
    					U.getToken(), 
    					extradata, 
    					vp );
    	} else {
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
    }
    
    /**
     * @throws ParseException 
     * 
     * 
     * 
     * 
     * 
     */
    public String listVolumes( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					   IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	String cinderEP = null;
    	if(U.getCinder2Endpoint()!=null)
    		cinderEP = U.getCinder2Endpoint();
    	else
    		cinderEP = U.getCinder1Endpoint();
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(), 
					  					  cinderEP + "/volumes/detail", 
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
    public void associateFloatingIP( String fipid, String portid ) 
	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError ,
	       IOException, MalformedURLException, ProtocolException, ParseException, CertificateException
    {
    	checkToken();
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add(p);
    	String extradata = "{\"floatingip\": {\"port_id\": \"" + portid + "\"}}";
    	RESTClient.sendPUTRequest(U.useSSL(),
				  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/floatingips/" + fipid + ".json",
				  U.getToken(),
				  extradata,
				  vp);

    }
    
    /**
     * @throws ParseException 
     * 
     * 
     * 
     * 
     * 
     */
    public String requestPortList( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					    IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken();
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(), 
					  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/ports.json", 
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
    public void requestFloatingIPRelease( String fip ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
							      IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
    	
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String ep = U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/floatingips/" + fip + ".json";
    	//Log.v("OSClient.requestFloatingIPRelease", "ep=["+ep+"]");
    	RESTClient.sendDELETERequest(U.useSSL(),
				     ep,
				     U.getToken(),
				     vp);
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
    public void requestReleaseFloatingIP( String floatingip, String serverid ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
										      IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
	
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add( p );
       
	String extradata = "{\"removeFloatingIp\": {\"address\": \"" + floatingip + "\"}}";
       
	RESTClient.sendPOSTRequest(U.useSSL(),
				   U.getNovaEndpoint() + "/servers/" + serverid + "/action",
				   U.getToken(),
				   extradata,
				   vp);
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
    public String requestServerLog( String serverid, int maxlines ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
									   IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
	//Log.d("requestServerLog", "serverid="+serverid+" - maxlines="+maxlines);
	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	vp.add(p);
	
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
    public String listQuotas( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					 IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
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
    public String listVolQuotas( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					    IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
	String cinderEP = null;
	if(U.getCinder2Endpoint()!=null)
	    cinderEP = U.getCinder2Endpoint();
	else
	    cinderEP = U.getCinder1Endpoint();
	Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String,String>>();
	v.add(p);
	return RESTClient.sendGETRequest( U.useSSL(),  
					  cinderEP + "/os-quota-sets/"+U.getTenantID( )+"?usage=True", 
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
    public String listFloatingIPs( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
 		
	Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	v.add(p);
	return RESTClient.sendGETRequest( U.useSSL(), 
					  //U.getNeutronEndpoint() + "/floatingips.json", 
					  U.getNovaEndpoint()+"/os-floating-ips",
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
    public void changeServerName( String serverid, String newServerName ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
										 IOException, MalformedURLException, ProtocolException, ParseException ,CertificateException
    {
    	checkToken( );
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	v.add(p);
	String extradata = "{\"server\": {\"name\": \"" + newServerName + "\"}}";
	RESTClient.sendPUTRequest(U.useSSL(),
				  U.getNovaEndpoint() + "/servers/" + serverid,
				  U.getToken(),
				  extradata,
				  v);
    }
    
    /**
     * @throws ParseException 
     *
     *
     * 
     *
     *
     */
    public String listServers( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					   IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken();
 		
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
    public String listFlavors( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					   IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
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
     */
/*
     public String listImages( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
				       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
        //Log.d("OSClient.listImages", "Calling checkToken");
	checkToken( );
	//Log.d("OSClient.listImages", "CheckToken returned");
	String EP =  U.getGlanceEndpoint() + "/" + U.getGlanceEndpointAPIVER() + "/images" ;
	//Log.d("OSClient.listImages", "EP="+EP);
	//Log.d("OSClient.listImages", "Calling sendGETRequest");
	String listResult = RESTClient.sendGETRequest(U.useSSL(),
						      EP,
						      U.getToken(),
						      null);
						      
	return listResult;
    }
*/  

    /**
     * @throws ParseException 
     *
     *
     * 
     * 
     *
     *
     */
/*
     public void deleteImage( String imageID ) 
    	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
	//Log.v("OSClient.deleteImage", "deleting Image ["+imageID+"] EP=["+U.getNovaEndpoint() + "/images/" + imageID+"]");
	RESTClient.sendDELETERequest(U.useSSL(),
				     U.getGlanceEndpoint() + "/" + U.getGlanceEndpointAPIVER() + "/images/" + imageID,
				     U.getToken(),
				     null);
    }
*/

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
    	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
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
    public void deleteInstance( String serverID ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
							 IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
 	String ep = U.getNovaEndpoint() + "/servers/" + serverID;
 	Log.v("OSClient.deleteInstance", "EP=["+ep+"]");
    	RESTClient.sendDELETERequest( U.useSSL(), 
				      ep, 
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
    public String listNetworks( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					    IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(),  
					  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/networks",
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
    public String requestExternalNetworks( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
						    IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );

	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	v.add(p);
	return RESTClient.sendGETRequest( U.useSSL(),
					  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/networks.json?router%3Aexternal=True",
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
    public String listSubNetworks( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	return RESTClient.sendGETRequest( U.useSSL(), 
					  U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/subnets",
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
    public String requestKeypairs( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					    IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
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
    public String listSecGroups( ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
					  IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken();
	//if(U.getNeutronEndpoint()!=null && U.getNeutronEndpoint().length()>1) {
	    //Log.v("OSClient.createSecGroup", "USING NEUTRON SEC GROUP");
	    
/*	    return RESTClient.sendGETRequest( U.useSSL(), 
					      U.getNeutronEndpoint() + "/security-groups",
					      U.getToken(), 
					      null );*/
					      
	/*} else {*/     	
	    return RESTClient.sendGETRequest( U.useSSL(), 
					      U.getNovaEndpoint() + "/os-security-groups",
					      U.getToken(), 
					      null );
	/*}*/
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public String listSecGroupRules( String secgrpID ) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
							      IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
/*	if(U.getNeutronEndpoint()!=null && U.getNeutronEndpoint().length()>1) {
	    //Log.v("OSClient.createSecGroup", "USING NEUTRON SEC GROUP");
	    String API = U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/security-group-rules.json";
	    //Log.v("OSClient.createSecGroup", "Calling API (" + API + ")");
	    return RESTClient.sendGETRequest( U.useSSL(), 
					      API,
					      U.getToken(), 
					      null );
	} else {
*/ 	   
	    return RESTClient.sendGETRequest( U.useSSL(), 
					      U.getNovaEndpoint() + "/os-security-groups/" + secgrpID,
					      U.getToken(), 
					      null );
	//}
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
    	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken( );
 		
    	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName( ) );
    	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
    	v.add(p);
    	if(U.getNeutronEndpoint()!=null && U.getNeutronEndpoint().length()>1) {
	    //Log.v("OSClient.createSecGroup", "USING NEUTRON SEC GROUP");
	    RESTClient.sendDELETERequest(U.useSSL(),
					 U.getNeutronEndpoint() + "/" + U.getNeutronEndpointAPIVER( ) + "/security-groups/" + secgrpID + ".json",
					 U.getToken(),
					 v);
    	} else {
	    RESTClient.sendDELETERequest(U.useSSL(),
					 U.getNovaEndpoint() + "/os-security-groups/" + secgrpID,
					 U.getToken(),
					 v);
        }
    }    

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public void createRule(String secgrpID, int fromPort, int toPort, String protocol, String cidr, String direction) 
	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
    	checkToken( );
		boolean useNeutron = U.getNeutronEndpoint()!=null && U.getNeutronEndpoint().length()>1;
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	//{"security_group_rule": {"direction": "egress", "port_range_min": "22", "remote_ip_prefix": "0.0.0.0/24", "ethertype": "IPv4", "port_range_max": "23", "protocol": "TCP", "security_group_id": "c903b316-cf00-460a-9fd7-d1661e0c4583"}
    	
/*    	if(useNeutron) {
    		String extradata = "{\"security_group_rule\": {\"direction\":\"" + direction + "\",\"port_range_min\": " + fromPort + ", \"protocol\": \"" 
    		+ protocol + "\", \"port_range_max\": " + toPort + ", \"remote_ip_prefix\": \"" 
    		+ cidr + "\", \"security_group_id\": \"" + secgrpID + "\"}}";
    		String url = U.getNeutronEndpoint( ) + "/" + U.getNeutronEndpointAPIVER( ) + "/security-group-rules.json";
    		Log.v("OSClient.createRule", "extradata=["+extradata+"]");
    		Log.v("OSClient.createRule", "URL="+url);
    		
    		
    		RESTClient.sendPOSTRequest( U.useSSL(), 
					url,
					U.getToken(), 
					extradata, 
					vp );
		} else {*/
    	  String extradata = "{\"security_group_rule\": {\"from_port\": " + fromPort + ", \"ip_protocol\": \"" + protocol + "\", \"to_port\": " + toPort + ", \"parent_group_id\": \"" + secgrpID + "\", \"cidr\": \"" + cidr + "\", \"group_id\": null}}";
			RESTClient.sendPOSTRequest( U.useSSL(), 
					U.getNovaEndpoint() + "/os-security-group-rules", 
					U.getToken(), 
					extradata, 
					vp );
		/*}*/
    }

    /**
     * @throws ParseException 
     *
     *
     *
     * 
     *
     */
    public void createInstance( String instanceName, 
				String imageID,
				String key_name,
				String flavorID,
				int count,
				String securityGroupID,
				Hashtable<Pair<String,String>, String> netID_to_netIP )
    	throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
	       IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
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
    	
    	String[] secgrpIDs = securityGroupID.split(",");
    	
    	try {
    	    obj = new JSONObject( data );
    	    JSONArray secgs = new JSONArray();
    	    JSONArray nets = new JSONArray();
    	    if(securityGroupID.length()!=0) 
    		for(int i = 0; i<secgrpIDs.length; ++i)
    		    secgs.put( new JSONObject("{\"name\": \"" + secgrpIDs[i] + "\"}") );


    	    
	    Iterator<Pair<String,String>> it = netID_to_netIP.keySet().iterator();
    	    while( it.hasNext() ) {
    	    	Pair<String,String> thisNet = it.next();
    	    	String netID = thisNet.first;
    	    	String netIP = netID_to_netIP.get( thisNet );
    	    	
    	    	if( netIP != null && netIP.length()!=0) 
		    nets.put( new JSONObject("{\"uuid\": \"" + netID + "\", \"fixed_ip\":\"" + netIP + "\"}") );
    	    	else {
    	    		
		    nets.put( new JSONObject("{\"uuid\": \"" + netID + "\"}") );
    	    	}
    	    }
    	    
    	    obj.getJSONObject("server").put("security_groups", secgs);
    	    obj.getJSONObject("server").put("networks", nets);
    	    
    	} catch(JSONException je) {
	    throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
    	}
    	
    	data = obj.toString( );
    	//Log.d("OSC","data="+data);
	RESTClient.sendPOSTRequest(U.useSSL(),
				   U.getNovaEndpoint() + "/servers",
				   U.getToken(),
				   data,
				   v);
    }

    /**
     * @throws ParseException
     *
     *
     *
     *
     *
     */
    public String requestInstanceDetails(String servertID) throws NotAuthorizedException, NotFoundException, ServerException, ServiceUnAvailableOrInternalError,
								  IOException, MalformedURLException, ProtocolException, ParseException,CertificateException
    {
	checkToken();

	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	v.add(p);
	return RESTClient.sendGETRequest( U.useSSL(),
					  U.getNovaEndpoint() + "/servers/" + servertID,
					  U.getToken(),
					  v );
    }
}
