package org.droidstack.comm;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;

import org.apache.http.HttpStatus;
import org.droidstack.parse.ParseUtils;
//import org.droidstack.parse.ParseUtils;
import org.droidstack.utils.Base64;
import org.droidstack.utils.User;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.util.Log;
import android.util.Pair;

public class RESTClient {

    /**
     *
     *
     * curl --cacert $HOME/Dropbox/INFN-CA-2006.pem -i 'https://cloud-areapd.pd.infn.it:5000/v2.0/tokens' -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d '{"auth": {"tenantName": "admin", "passwordCredentials": {"username": "admin", "password": "ADMIN_PASS"}}}'
     *
     *
     *
     */
    public static String requestToken( boolean usessl, 
    								   String endpoint, 
    								   String tenantName, 
    								   String username, 
    								   String password )
      throws RuntimeException, GenericException, NotFoundException, NotAuthorizedException
    {
	String proto = "http://";
	if(usessl)
	    proto = "https://";
	
	String sUrl = proto + endpoint + ":5000/v2.0/tokens";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException( "Malformed URL: "+mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
	if(usessl) {
	    trustAllCerts = new TrustManager[] {
		new X509TrustManager() {
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		    }
		    
		    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
		    
		    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
		    
		}
	    };
	    
	    try {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch(java.security.NoSuchAlgorithmException e) {
	    } catch(java.security.KeyManagementException e) {
	    }
	    
	    try {
		  conn = (HttpsURLConnection)url.openConnection( );
	    } catch(java.io.IOException ioe) {
		throw  new RuntimeException("URL.openConnection https: "+ioe.getMessage( ) );
	    }
	} else {
	
	    try {
		  conn = (HttpURLConnection)url.openConnection();
	    } catch(java.io.IOException ioe) {
		  throw new RuntimeException("URL.openConnection http: "+ioe.getMessage());
	    }
	}
	
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("Accept", "application/json");
	conn.setDoOutput(true);
	conn.setDoInput(true);
	((HttpURLConnection)conn).setChunkedStreamingMode(0);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
		if(usessl)
		  ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}
	
	String data = "{\"auth\": {\"tenantName\": \"" 
			+ tenantName 
			+ "\", \"passwordCredentials\": {\"username\": \"" 
			+ username + "\", \"password\": \"" 
			+ password + "\"}}}";

	OutputStream out = null;
	try {
	    out = new BufferedOutputStream( conn.getOutputStream() );
	    out.write( data.getBytes( ) );
	    out.flush( );
	    out.close( );
	} catch(java.io.IOException ioe) {
		if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("OutputStream.write/close: "+ioe.getMessage( ) );
	}
	
	int status = HttpStatus.SC_OK;
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
		String mex = ioe.getMessage();
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		if(mex==null || mex.length()==0 || mex.compareTo("null")==0) {
			mex = "getResponseCode: Unable to get server's error message. Probably the endpoint is listening on SSL";
			
		} else
			mex = "getResponseCode: " + mex;
	    throw new RuntimeException( mex );
	}

	if( status != HttpStatus.SC_OK ) {
	    InputStream in = new BufferedInputStream( ((HttpURLConnection)conn).getErrorStream( ) );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer)))
			buf += new String(buffer, 0, len);
		    in.close();
		} catch(IOException ioe) {
			if(usessl)
			      ((HttpsURLConnection)conn).disconnect( );
				else
				  ((HttpURLConnection)conn).disconnect( );
		    throw new RuntimeException("InputStream.write/close: "+ioe.getMessage( ) );
		}
	    
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_UNAUTHORIZED ) {
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials and try again..." );
		}
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );

		throw new GenericException( ParseUtils.getErrorMessage( buf ) );
	    }
	}
	
	String res = "";
	try {
	    //String buf = "";
	    InputStream in = new BufferedInputStream( conn.getInputStream( ) );
	    int len;
	    byte[] buffer = new byte[4096];
	    while (-1 != (len = in.read(buffer)))
		res += new String(buffer, 0, len);
	    in.close();
	    
	} catch(java.io.IOException ioe) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}    
	if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	return res;
    }
    
    /**
     * 
     * @param U
     * @param serverid
     * @param snapname
     */
    public static void createInstanceSnapshot( User U, String serverid, String snapname )  throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"createImage\": {\"name\": \"" + snapname + "\", \"metadata\": {}}}";
    	sendPOSTRequest( U.useSSL(), 
				 		 U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/" + serverid + "/action", 
				 		 U.getToken(), 
				 		 extradata, 
				 		 vp );
    }
    
    /**
     * *
     * 
     * 
curl -i 'http://90.147.77.39:8774/v2/4f531aab49c849279b9bb6f3b6df5189/os-floating-ips' -X POST -H "X-Auth-Project-Id: Alvise" -H "User-Agent: python-novaclient" -H "Content-Type: application/json" -H "Accept: application/json" -H "X-Auth-Token: MIIMvAYJKoZIhvcNAQcCoIIMrTCCDKkCAQExCTAHBgUrDgMCGjCCCxIGCSqGSIb3DQEHAaCCCwMEggr-eyJhY2Nlc3MiOiB7InRva2VuIjogeyJpc3N1ZWRfYXQiOiAiMjAxNC0wOC0wN1QxMToyMDo0NC45MjM3MzMiLCAiZXhwaXJlcyI6ICIyMDE0LTA4LTA3VDIwOjIwOjQ0WiIsICJpZCI6ICJwbGFjZWhvbGRlciIsICJ0ZW5hbnQiOiB7ImRlc2NyaXB0aW9uIjogIiIsICJlbmFibGVkIjogdHJ1ZSwgImlkIjogIjRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgIm5hbWUiOiAiQWx2aXNlIn19LCAic2VydmljZUNhdGFsb2ciOiBbeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3NC92Mi80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc0L3YyLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgImlkIjogIjFjZDE5YzllZTg1MjRhMTY5MzRjMmJhN2U0NjRmMTY2IiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzQvdjIvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAiY29tcHV0ZSIsICJuYW1lIjogIm5vdmEifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6OTY5NiIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo5Njk2IiwgImlkIjogIjM0MmI2ZDY3ZDg5NDQwMWNhYjk4ZTY3ZjEyYTJlNDlmIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojk2OTYifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAibmV0d29yayIsICJuYW1lIjogIm5ldXRyb24ifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3Ni92Mi80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc2L3YyLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgImlkIjogIjAwNjUxMTQ3NTU4MDQyYzNhYTkzMWFiNGQ0N2Y4YWZkIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzYvdjIvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAidm9sdW1ldjIiLCAibmFtZSI6ICJjaW5kZXJ2MiJ9LCB7ImVuZHBvaW50cyI6IFt7ImFkbWluVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo5MjkyIiwgInJlZ2lvbiI6ICJyZWdpb25PbmUiLCAiaW50ZXJuYWxVUkwiOiAiaHR0cDovLzE5Mi4xNjguNjAuMTExOjkyOTIiLCAiaWQiOiAiYjNjNjg5NDBmNzI0NDlkNDhjNWY0YzQzMDcxNjk3MjAiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6OTI5MiJ9XSwgImVuZHBvaW50c19saW5rcyI6IFtdLCAidHlwZSI6ICJpbWFnZSIsICJuYW1lIjogImdsYW5jZSJ9LCB7ImVuZHBvaW50cyI6IFt7ImFkbWluVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc2L3YxLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgInJlZ2lvbiI6ICJyZWdpb25PbmUiLCAiaW50ZXJuYWxVUkwiOiAiaHR0cDovLzE5Mi4xNjguNjAuMTExOjg3NzYvdjEvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkiLCAiaWQiOiAiMzc5MDU0Yzg2MDg0NGU4OGEwYTRlZGZkYTVhNDk1YTgiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6ODc3Ni92MS80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSJ9XSwgImVuZHBvaW50c19saW5rcyI6IFtdLCAidHlwZSI6ICJ2b2x1bWUiLCAibmFtZSI6ICJjaW5kZXIifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3My9zZXJ2aWNlcy9BZG1pbiIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4NzczL3NlcnZpY2VzL0Nsb3VkIiwgImlkIjogIjAxMTdlZGU3NWI0NzRhOGNhOGM4M2MwNzk3M2ZkYzEwIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzMvc2VydmljZXMvQ2xvdWQifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAiZWMyIiwgIm5hbWUiOiAibm92YV9lYzIifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6MzUzNTcvdjIuMCIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo1MDAwL3YyLjAiLCAiaWQiOiAiM2U4ZmJkMWMxODE3NDk2MGIzZDZiMWRiMDdlMGE1YTkiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6NTAwMC92Mi4wIn1dLCAiZW5kcG9pbnRzX2xpbmtzIjogW10sICJ0eXBlIjogImlkZW50aXR5IiwgIm5hbWUiOiAia2V5c3RvbmUifV0sICJ1c2VyIjogeyJ1c2VybmFtZSI6ICJhbHZpc2UiLCAicm9sZXNfbGlua3MiOiBbXSwgImlkIjogImFiNWI4YjkzM2FhOTQ1YWJhYzljMDIzYmY4NmExNjlkIiwgInJvbGVzIjogW3sibmFtZSI6ICJfbWVtYmVyXyJ9LCB7Im5hbWUiOiAiTWVtYmVyIn1dLCAibmFtZSI6ICJhbHZpc2UifSwgIm1ldGFkYXRhIjogeyJpc19hZG1pbiI6IDAsICJyb2xlcyI6IFsiOWZlMmZmOWVlNDM4NGIxODk0YTkwODc4ZDNlOTJiYWIiLCAiNGU5ODRmNTNjOWJlNDNjMWIyMTllZDI3MzJlN2Y2NjAiXX19fTGCAYEwggF9AgEBMFwwVzELMAkGA1UEBhMCVVMxDjAMBgNVBAgMBVVuc2V0MQ4wDAYDVQQHDAVVbnNldDEOMAwGA1UECgwFVW5zZXQxGDAWBgNVBAMMD3d3dy5leGFtcGxlLmNvbQIBATAHBgUrDgMCGjANBgkqhkiG9w0BAQEFAASCAQBT0hwT2jouyWWyfRu7XupmuvtgEJ2WmR4hCE4MpVvvlqfps-h6DCn69gtLsTfvmraMVPRRXOASwW+Mz23DCYHwN-zTTX1M0hnyKSU5d5qBViO7kz9ngsbpJQc9Dy9RtG9whMMFlOyOTlOkByW0WIo3ZAtRcazvWimHAvEwKa0Ccu+ehvSZFLy4VQRS3Qg3aYefCwqEfKwzUdHdBjYWrCvyLiT+A8Dg5GcrtctrXa-r3Gez+pIzzJO+HCaoj-J9-8dDrH3BMi7RjWahcyW8CEIOmsIe9dKoCD3EL3Fq9GgTBPoPpCoSbdJh8+7AWuZe32Ojdc1PTm2x6DsFN022GMIW" -d '{"pool": "e71e453e-6c43-4207-bab8-b10101d6ccb2"}'
     *
     *    
    */
    public static void requestFloatingIPAllocation( User U, String external_net_ID ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"pool\": \"" + external_net_ID + "\"}";
    	sendPOSTRequest( U.useSSL(), 
    					 U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-floating-ips", 
    					 U.getToken(), 
    					 extradata, 
    					 vp );
    }
    
    /**
     * *
     * *
     * 
     * *
     * 
     * 
     */
    public static void createSecGroup( User U, String secgrpName, String desc)  throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"security_group\": {\"name\": \"" + secgrpName + "\", \"description\": \"" + desc + "\"}}";
    	sendPOSTRequest( U.useSSL(), 
    					 U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-security-groups", 
    					 U.getToken(), 
    					 extradata, 
    					 vp );
    }
    
    /**
     * 
     * 
	 *
curl -i https://cloud-areapd.pd.infn.it:8774/v2/e4a9d116b61c4609a92e701ffc3b36bc/servers/15006200-7063-4f89-8ab9-385c99ad995c/action 
-X POST -H "X-Auth-Project-Id: Large" -H "User-Agent: python-novaclient" 
-H "Content-Type: application/json" -H "Accept: application/json" 
-H "X-Auth-Token: $TOKEN" -d '{"addFloatingIp": {"address": "90.147.77.212"}}'
	 *
	 *
     */
    public static void requestFloatingIPAssociate(User U, String fip, String serverid )  throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	String extradata = "{\"addFloatingIp\": {\"address\": \"" + fip + "\"}}";
    	sendPOSTRequest( U.useSSL(), 
    					 U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/"+serverid+"/action", 
    					 U.getToken(), 
    					 extradata, 
    					 vp );   	
    }
    
    /**
     * 
     * 
     * 
curl -i 'http://90.147.77.39:8774/v2/4f531aab49c849279b9bb6f3b6df5189/os-floating-ips' -X POST -H "X-Auth-Project-Id: Alvise" -H "User-Agent: python-novaclient" -H "Content-Type: application/json" -H "Accept: application/json" -H "X-Auth-Token: MIIMvAYJKoZIhvcNAQcCoIIMrTCCDKkCAQExCTAHBgUrDgMCGjCCCxIGCSqGSIb3DQEHAaCCCwMEggr-eyJhY2Nlc3MiOiB7InRva2VuIjogeyJpc3N1ZWRfYXQiOiAiMjAxNC0wOC0wN1QxMToyMDo0NC45MjM3MzMiLCAiZXhwaXJlcyI6ICIyMDE0LTA4LTA3VDIwOjIwOjQ0WiIsICJpZCI6ICJwbGFjZWhvbGRlciIsICJ0ZW5hbnQiOiB7ImRlc2NyaXB0aW9uIjogIiIsICJlbmFibGVkIjogdHJ1ZSwgImlkIjogIjRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgIm5hbWUiOiAiQWx2aXNlIn19LCAic2VydmljZUNhdGFsb2ciOiBbeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3NC92Mi80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc0L3YyLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgImlkIjogIjFjZDE5YzllZTg1MjRhMTY5MzRjMmJhN2U0NjRmMTY2IiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzQvdjIvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAiY29tcHV0ZSIsICJuYW1lIjogIm5vdmEifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6OTY5NiIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo5Njk2IiwgImlkIjogIjM0MmI2ZDY3ZDg5NDQwMWNhYjk4ZTY3ZjEyYTJlNDlmIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojk2OTYifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAibmV0d29yayIsICJuYW1lIjogIm5ldXRyb24ifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3Ni92Mi80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc2L3YyLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgImlkIjogIjAwNjUxMTQ3NTU4MDQyYzNhYTkzMWFiNGQ0N2Y4YWZkIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzYvdjIvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAidm9sdW1ldjIiLCAibmFtZSI6ICJjaW5kZXJ2MiJ9LCB7ImVuZHBvaW50cyI6IFt7ImFkbWluVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo5MjkyIiwgInJlZ2lvbiI6ICJyZWdpb25PbmUiLCAiaW50ZXJuYWxVUkwiOiAiaHR0cDovLzE5Mi4xNjguNjAuMTExOjkyOTIiLCAiaWQiOiAiYjNjNjg5NDBmNzI0NDlkNDhjNWY0YzQzMDcxNjk3MjAiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6OTI5MiJ9XSwgImVuZHBvaW50c19saW5rcyI6IFtdLCAidHlwZSI6ICJpbWFnZSIsICJuYW1lIjogImdsYW5jZSJ9LCB7ImVuZHBvaW50cyI6IFt7ImFkbWluVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4Nzc2L3YxLzRmNTMxYWFiNDljODQ5Mjc5YjliYjZmM2I2ZGY1MTg5IiwgInJlZ2lvbiI6ICJyZWdpb25PbmUiLCAiaW50ZXJuYWxVUkwiOiAiaHR0cDovLzE5Mi4xNjguNjAuMTExOjg3NzYvdjEvNGY1MzFhYWI0OWM4NDkyNzliOWJiNmYzYjZkZjUxODkiLCAiaWQiOiAiMzc5MDU0Yzg2MDg0NGU4OGEwYTRlZGZkYTVhNDk1YTgiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6ODc3Ni92MS80ZjUzMWFhYjQ5Yzg0OTI3OWI5YmI2ZjNiNmRmNTE4OSJ9XSwgImVuZHBvaW50c19saW5rcyI6IFtdLCAidHlwZSI6ICJ2b2x1bWUiLCAibmFtZSI6ICJjaW5kZXIifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6ODc3My9zZXJ2aWNlcy9BZG1pbiIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo4NzczL3NlcnZpY2VzL0Nsb3VkIiwgImlkIjogIjAxMTdlZGU3NWI0NzRhOGNhOGM4M2MwNzk3M2ZkYzEwIiwgInB1YmxpY1VSTCI6ICJodHRwOi8vOTAuMTQ3Ljc3LjM5Ojg3NzMvc2VydmljZXMvQ2xvdWQifV0sICJlbmRwb2ludHNfbGlua3MiOiBbXSwgInR5cGUiOiAiZWMyIiwgIm5hbWUiOiAibm92YV9lYzIifSwgeyJlbmRwb2ludHMiOiBbeyJhZG1pblVSTCI6ICJodHRwOi8vMTkyLjE2OC42MC4xMTE6MzUzNTcvdjIuMCIsICJyZWdpb24iOiAicmVnaW9uT25lIiwgImludGVybmFsVVJMIjogImh0dHA6Ly8xOTIuMTY4LjYwLjExMTo1MDAwL3YyLjAiLCAiaWQiOiAiM2U4ZmJkMWMxODE3NDk2MGIzZDZiMWRiMDdlMGE1YTkiLCAicHVibGljVVJMIjogImh0dHA6Ly85MC4xNDcuNzcuMzk6NTAwMC92Mi4wIn1dLCAiZW5kcG9pbnRzX2xpbmtzIjogW10sICJ0eXBlIjogImlkZW50aXR5IiwgIm5hbWUiOiAia2V5c3RvbmUifV0sICJ1c2VyIjogeyJ1c2VybmFtZSI6ICJhbHZpc2UiLCAicm9sZXNfbGlua3MiOiBbXSwgImlkIjogImFiNWI4YjkzM2FhOTQ1YWJhYzljMDIzYmY4NmExNjlkIiwgInJvbGVzIjogW3sibmFtZSI6ICJfbWVtYmVyXyJ9LCB7Im5hbWUiOiAiTWVtYmVyIn1dLCAibmFtZSI6ICJhbHZpc2UifSwgIm1ldGFkYXRhIjogeyJpc19hZG1pbiI6IDAsICJyb2xlcyI6IFsiOWZlMmZmOWVlNDM4NGIxODk0YTkwODc4ZDNlOTJiYWIiLCAiNGU5ODRmNTNjOWJlNDNjMWIyMTllZDI3MzJlN2Y2NjAiXX19fTGCAYEwggF9AgEBMFwwVzELMAkGA1UEBhMCVVMxDjAMBgNVBAgMBVVuc2V0MQ4wDAYDVQQHDAVVbnNldDEOMAwGA1UECgwFVW5zZXQxGDAWBgNVBAMMD3d3dy5leGFtcGxlLmNvbQIBATAHBgUrDgMCGjANBgkqhkiG9w0BAQEFAASCAQBT0hwT2jouyWWyfRu7XupmuvtgEJ2WmR4hCE4MpVvvlqfps-h6DCn69gtLsTfvmraMVPRRXOASwW+Mz23DCYHwN-zTTX1M0hnyKSU5d5qBViO7kz9ngsbpJQc9Dy9RtG9whMMFlOyOTlOkByW0WIo3ZAtRcazvWimHAvEwKa0Ccu+ehvSZFLy4VQRS3Qg3aYefCwqEfKwzUdHdBjYWrCvyLiT+A8Dg5GcrtctrXa-r3Gez+pIzzJO+HCaoj-J9-8dDrH3BMi7RjWahcyW8CEIOmsIe9dKoCD3EL3Fq9GgTBPoPpCoSbdJh8+7AWuZe32Ojdc1PTm2x6DsFN022GMIW" -d '{"pool": "e71e453e-6c43-4207-bab8-b10101d6ccb2"}'
     *
     *
     *
    */
    public static void requestFloatingIPRelease( User U, String fip ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
    {
    	Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
    	Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
    	vp.add( p );
    	
    	sendDELETERequest( U.useSSL(), 
    					   U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-floating-ips/" + fip, 
    					   U.getToken(), vp );
    }
    
    /**
    *
    *
    * curl -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -H "Content-Type: application/json" http://cloud-areapd.pd.infn.it:9292/v2/images
    *
    *
    *
    */
   public static void requestReleaseFloatingIP( User U, String floatingip, String serverid ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException 
   {
	   Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	   Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	   vp.add( p );
	   
	   String extradata = "{\"removeFloatingIp\": {\"address\": \"" + floatingip + "\"}}";
	   
	   sendPOSTRequest( U.useSSL(), U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/" + serverid + "/action", 
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
   public static String requestServerLog( User U, String serverid ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException  
   {
	   Vector<Pair<String,String>> vp = new Vector<Pair<String,String>>();
	   Pair<String,String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	   vp.add( p );
	    
	  return sendPOSTRequest( U.useSSL(), 
			  				  U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/servers/"+serverid+"/action", U.getToken(), 
			  				  "{\"os-getConsoleOutput\": {\"length\": null}}", 
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
    public static String requestImages( User U ) throws RuntimeException  
    {
	  return sendGETRequest( U.useSSL(), U.getEndpoint() + ":9292/v2/images", U.getToken(), null );   
    }
    
    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/limits -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestQuota( User U ) throws RuntimeException
    {
	Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String,String>>();
	v.add(p);
	return sendGETRequest( U.useSSL(),  U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/limits", U.getToken(), v);
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/limits -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestFloatingIPs( User U ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String,String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return sendGETRequest( U.useSSL(), U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/os-floating-ips", U.getToken(), v);
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/servers/detail -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestServers( User U ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return sendGETRequest( U.useSSL(),
						     U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/servers/detail?all_tenants=1",
						     U.getToken(), 
						     v );
    }


    /**
     *
     *
     *curl -i 'http://90.147.77.40:8774/v2/f4d55a77e1d14023ba0be21ac5b140cb/flavors/detail' -X GET -H "X-Auth-Project-Id: Alvise" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestFlavors( User U ) throws RuntimeException
    {
	Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	v.add(p);
	return sendGETRequest( U.useSSL(),
			        	    U.getEndpoint() + ":8774/v2/"+U.getTenantID()+"/flavors/detail",
			        	   U.getToken(),
			        	   v );
    }

    /**
     *
     *
     * curl -i -X GET -H "X-Auth-Token: $TOKEN"  -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     * curl -i -X DELETE -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     *
     *
     *
     */
    public static void deleteGlanceImage( User U, String imageid) throws RuntimeException, NotFoundException
    {
    	try {
    		sendDELETERequest(  U.useSSL(),
    							U.getEndpoint() + ":9292/v2/images/"+imageid, 
    							U.getToken( ),
    							null );
    	} catch(NotAuthorizedException na) {
    		throw new RuntimeException(na.getMessage() + "\n\nPlease check your credentials or that the image you're trying to delete is owned by you...");
    	}
    }

    /**
     *
     *
     * curl -i -X GET -H "X-Auth-Token: $TOKEN"  -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     * curl -i -X DELETE -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     *
     *
     *
     */
    public static void deleteInstance( User U, String serverid) throws RuntimeException, NotFoundException
    {
    	try {
    		sendDELETERequest( U.useSSL(), 
    						   U.getEndpoint() + ":8774/v2/" +U.getTenantID()+ "/servers/"+serverid, 
    						   U.getToken(),
    						   null );
    	} catch(NotAuthorizedException na) {
    		throw new RuntimeException(na.getMessage() + "\n\nPlease check your credentials or that the instance you're trying to delete is owned by you...");
    	}
    }

    /**
     *
     *
     * curl -i 'http://90.147.77.40:9696/v2.0/networks' -X GET -H "X-Auth-Project-Id: Alvise" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestNetworks( User U ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return sendGETRequest( U.useSSL(),  U.getEndpoint() + ":9696/v2.0/networks",
			                 U.getToken(), 
			                 v );
    }

    /**
     *
     *
     * curl -i 'http://90.147.77.40:9696/v2.0/subnets' -X GET -H "X-Auth-Project-Id: Alvise" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestSubNetworks( User U ) throws RuntimeException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return sendGETRequest( U.useSSL(), U.getEndpoint() + ":9696/v2.0/subnets",
			                 U.getToken(), 
			                 v );
    }

    /**
     *
     *
     *
     * curl -i 'http://90.147.77.40:8774/v2/$TENANT_ID/os-keypairs' -X GET -H "X-Auth-Project-Id: admin" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     */
    public static String requestKeypairs( User U ) throws RuntimeException 
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  return sendGETRequest( U.useSSL(),  U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-keypairs",
			                 U.getToken(), 
			                 v );
    }

    /**
     *
     *
     *
     * curl -i 'http://90.147.77.40:8774/v2/$TENANT_ID/os-security-groups' -X GET -H "X-Auth-Project-Id: admin" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     */
    public static String requestSecGroups( User U ) throws RuntimeException 
    {
	  /*Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);*/
	  return sendGETRequest( U.useSSL(), 
			  				 U.getEndpoint() + ":9696/v2.0/security-groups.json",
			  				 U.getToken(), 
			                 null );
    }


    /**
     *
     *
     *
     * curl -i 'http://90.147.77.40:8774/v2/$TENANT_ID/os-security-groups/SecGrpID' -X DELETE -H "X-Auth-Project-Id: admin" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     */
    public static void deleteSecGroup( User U, String secgrpID ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
	  Pair<String, String> p = new Pair<String, String>( "X-Auth-Project-Id", U.getTenantName() );
	  Vector<Pair<String, String>> v = new Vector<Pair<String, String>>();
	  v.add(p);
	  sendDELETERequest( U.useSSL(), 
			  			 U.getEndpoint() + ":8774/v2/" + U.getTenantID() + "/os-security-groups/" + secgrpID,
			  			 U.getToken(),
			  			 v );
    }    
    
    /**
     *
     *
     * curl -i 'http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/servers' -X POST -H "X-Auth-Project-Id: admin" -H "Content-Type: application/json" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -d '{"server": {"name": "BLAHBLAHBLAH", "imageRef": "4988f1ee-5cfc-4505-aed1-6d812442a56d", "key_name": "lxadorigo", "flavorRef": "b639f517-c01f-483f-a8e2-c9ee3370ac36", "max_count": 1, "min_count": 1, "networks": [{"fixed_ip": "10.0.1.29", "uuid": "e93ad35f-aac5-4fa7-bfc9-1e3c45d58fc1"}], "security_groups": [{"name": "848f1b29-c793-415c-8f3f-10836c1f99f7"}, {"name": "cf5b187b-1e1c-4ca2-87a9-54b5dce244bc"}]}}'
     *
     *
     */
    public static void requestInstanceCreation( User U,
						String instanceName, 
						String glanceImageID,
						String key_name, 
						String flavorID,
						int count, 
						String _secgrpIDs,
						String adminPass,
						Hashtable<String, String> netID_fixedIP,
						String filesDir ) 
	throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
    	boolean usessl = U.useSSL();
    	String proto = "http";
    	if(usessl)
	    proto = "https";
    	String sUrl = proto + "://" + U.getEndpoint( ) + ":8774/v2/" + U.getTenantID( ) + "/servers";
    	URL url = null;
    	try {
    		url = new URL(sUrl);
    	} catch(java.net.MalformedURLException mfu) {
    		throw new RuntimeException("new URL: " + mfu.toString( ) );
    	}
    	URLConnection conn = null;
    	TrustManager[] trustAllCerts = null;
    	if(usessl) {
    		trustAllCerts = new TrustManager[] {
    				new X509TrustManager() {
    					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    						return null;
    					}
		    
    					public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
		    
    					public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
		    
    				}
    		};
	    
    		try {
    			SSLContext sc = SSLContext.getInstance("SSL");
    			sc.init(null, trustAllCerts, new java.security.SecureRandom());
    			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    		} catch(java.security.NoSuchAlgorithmException e) {
    		} catch(java.security.KeyManagementException e) {
    		}
	    
    		try {
    			conn = (HttpsURLConnection)url.openConnection( );
    		} catch(java.io.IOException ioe) {
    			throw  new RuntimeException("URL.openConnection https: "+ioe.getMessage( ) );
    		}
    	} else {
	
    		try {
    			conn = (HttpURLConnection)url.openConnection();
    		} catch(java.io.IOException ioe) {
    			throw new RuntimeException("URL.openConnection http: "+ioe.getMessage());
    		}
    	}
    
    	conn.setRequestProperty("X-Auth-Project-Id", U.getTenantName( ) );
    	conn.setRequestProperty("Accept", "application/json");
    	conn.setRequestProperty("Content-Type", "application/json");
    	conn.setRequestProperty("X-Auth-Token", U.getToken() );
    	conn.setDoOutput(true);

    	try {
    		((HttpURLConnection)conn).setRequestMethod("POST");
    	} catch(java.net.ProtocolException pe ) {
    		if(usessl)
    			((HttpsURLConnection)conn).disconnect( );
    		else
    			((HttpURLConnection)conn).disconnect( );
    		throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
    	}

    	String userdata="";
    	if(adminPass!=null) {
    		try {
    			File f = new File(filesDir + "/userdata");
    			if( f.exists( ) ) f.delete();
    			BufferedWriter bw = new BufferedWriter( new FileWriter(f) );
    			bw.write("#!/bin/bash");
    			bw.newLine();
    			bw.write("passwd -d root");
    			bw.newLine();
    			bw.write("echo \"alvise\" >/tmp/pwd");
    			bw.newLine();
    			bw.write("cat /tmp/pwd | passwd --stdin root");
    			bw.newLine();
    			bw.write("\rm -f /tmp/pwd");
    			bw.newLine();
    			bw.close();
    			userdata = ", \"user_data\": \"" + Base64.encodeFromFile( filesDir + "/userdata" ) + "\"";
    		} catch(IOException ioe) {
    			//		Log.d("RESTClient.requestInstanceCreation", "ERROR ENCODING USERDATA: " + ioe.getMessage( ) );
    			userdata = "";
    		}
    	}

	String _data = "{\"server\": {\"name\": \"" + instanceName + 
	    "\", \"imageRef\": \"" + glanceImageID + 
	    "\", " + (key_name != null ? "\"key_name\": \"" + key_name : "") + 
	    "\", \"flavorRef\": \"" + flavorID + 
	    "\", \"max_count\": " + count + 
	    ", \"min_count\": " + count + userdata + "}}";

	JSONObject obj = null;
	//Log.d("RESTClient", "_secgrpIDs=["+_secgrpIDs+"]");
	String[] secgrpIDs = _secgrpIDs.split(",");
	//String[] networkIDs = _networkIDs.split(",");
	try {
	    obj = new JSONObject( _data );
	    JSONArray secgs = new JSONArray();
	    JSONArray nets = new JSONArray();
	    if(_secgrpIDs.length()!=0) 
		for(int i = 0; i<secgrpIDs.length; ++i)
		    secgs.put( new JSONObject("{\"name\": \"" + secgrpIDs[i] + "\"}") );


	    {
		Iterator<String> it = netID_fixedIP.keySet().iterator();
		while( it.hasNext() ) {
		    String netID = it.next( );
		    String netIP = netID_fixedIP.get( netID );
		    if( netIP != null && netIP.length()!=0) 
			nets.put( new JSONObject("{\"uuid\": \"" + netID + "\", \"fixed_ip\":\"" + netIP + "\"}") );
		    else
			nets.put( new JSONObject("{\"uuid\": \"" + netID + "\"}") );
		}
	    }



	    obj.getJSONObject("server").put("security_groups", secgs);
	    obj.getJSONObject("server").put("networks", nets);// );
	    
	} catch(JSONException je) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
	}
	
	String data = obj.toString( );
	//Log.d("RESTClient.requestInstanceCreation","data="+data);
	OutputStreamWriter out = null;
	try {
	    out = new OutputStreamWriter(conn.getOutputStream());
	    out.write(data);
	    out.close();
	} catch(java.io.IOException ioe) {
	    ioe.printStackTrace( );
	    if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("OutputStreamWriter.write/close: "+ioe.getMessage( ) );
	}

	int status = HttpStatus.SC_OK;
	
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	    //Log.d("RESTClient", "Status="+status);
	} catch(IOException ioe) {
	    //Log.d("RESTCLIENT", ioe.toString( ) );
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("getResponseCode: "+ioe.getMessage( ) );
	}
	if( status != HttpStatus.SC_OK && status !=HttpStatus.SC_ACCEPTED ) {
	    // if(status == HttpStatus.SC_BAD_REQUEST) 
	    // 	throw new RuntimeException("Bad HTTP request" );

	    InputStream in = ((HttpURLConnection)conn).getErrorStream( );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer)))
			buf += new String(buffer, 0, len);
		    in.close();
		    //Log.d("RESTCLIENT", "buf="+buf);
		} catch(IOException ioe) {
			if(usessl)
			      ((HttpsURLConnection)conn).disconnect( );
				else
				  ((HttpURLConnection)conn).disconnect( );
		    throw new RuntimeException("InputStream.write/close: "+ioe.getMessage( ) );
		}
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_UNAUTHORIZED ) 
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials and try again..." );
		
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );

		throw new GenericException( ParseUtils.getErrorMessage( buf ) );
	    }
	}
	if(usessl)
	  ((HttpsURLConnection)conn).disconnect( );
	else
		((HttpURLConnection)conn).disconnect( );
    } 

    //________________________________________________________________________________
    public static String sendGETRequest( boolean usessl,
    									 String sURL, 
					 				     String token,
					 				     Vector<Pair<String,String>> properties ) throws RuntimeException 
    {
    	String Url = sURL;
    	if(usessl)
    		Url = "https://"+Url;
    	else
    		Url = "http://"+Url;
    	
	//    	Log.d("RESTClient", "sendGETRequest - URL="+Url);
	URL url = null;
	try {
	    url = new URL(Url);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("Malformed URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
	if(usessl) {
	    trustAllCerts = new TrustManager[] {
		new X509TrustManager() {
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		    }
		    
		    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
		    
		    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
		    
		}
	    };
	    
	    try {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch(java.security.NoSuchAlgorithmException e) {
	    } catch(java.security.KeyManagementException e) {
	    }
	    
	    try {
		//	      Log.d("RESTClient", "1 Connecting...");
		  conn = (HttpsURLConnection)url.openConnection( );
	    } catch(java.io.IOException ioe) {
		throw  new RuntimeException("URL.openConnection https: "+ioe.getMessage( ) );
	    }
	} else {
	
	    try {
		//	    	Log.d("RESTClient", "2 Connecting...");
		  conn = (HttpURLConnection)url.openConnection();
	    } catch(java.io.IOException ioe) {
		  throw new RuntimeException("URL.openConnection http: "+ioe.getMessage());
	    }
	}
	
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
	
	if( properties!=null ) {
	    Iterator<Pair<String,String>> it = properties.iterator();
	    while( it.hasNext( ) ) {
		Pair<String, String> pair = it.next( );
		conn.setRequestProperty( pair.first, pair.second );
	    }
	}
	conn.setReadTimeout(20000 /* milliseconds */);
    conn.setConnectTimeout(15000 /* milliseconds */);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    //		Log.d("RESTClient", "1 Disconnecting...");
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "setRequestMethod(GET): " + pe.getMessage( ) );
	}
	
	conn.setDoInput(true);
	conn.setDoOutput(false);

	BufferedInputStream inStream = null;
	String buf = "";
	try {
	    inStream = new BufferedInputStream( conn.getInputStream() );
	        
	    byte[] b = new byte[ 2048 ];
	    int res = 0;
	    
	    while( (res = inStream.read( b, 0, 2048 )) != -1 )
		if( res>0 )
		    buf += new String( b, 0, res );
	} catch(java.io.IOException ioe) {
	    //		Log.d("RESTClient", "2 Disconnecting...");
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	//	Log.d("RESTClient", "3 Disconnecting...");
	if(usessl)
		  ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	return buf.toString( );    	
    } 
    
  //________________________________________________________________________________
    public static String sendPOSTRequest( boolean usessl,
    								      String sURL, 
					 					  String token,
					 					  String extradata,
					 					  Vector<Pair<String,String>> properties ) 
	  throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
    	String Url = sURL;
    	if(usessl)
    		Url = "https://" + Url;
    	else
    		Url = "http://" + Url;
    Log.d("RESTCLIENT","extradata="+extradata);
	URL url = null;
	try {
	    url = new URL(Url);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("Malformed URL: " + mfu.toString( ) );
	}
	
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
	if(usessl) {
	    trustAllCerts = new TrustManager[] {
		new X509TrustManager() {
		    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		    }
		    
		    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
		    
		    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
		    
		}
	    };
	    
	    try {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	    } catch(java.security.NoSuchAlgorithmException e) {
	    } catch(java.security.KeyManagementException e) {
	    }
	    
	    try {
		  conn = (HttpsURLConnection)url.openConnection( );
	    } catch(java.io.IOException ioe) {
		throw  new RuntimeException("URL.openConnection https: "+ioe.getMessage( ) );
	    }
	} else {
	
	    try {
		  conn = (HttpURLConnection)url.openConnection();
	    } catch(java.io.IOException ioe) {
		  throw new RuntimeException("URL.openConnection http: "+ioe.getMessage());
	    }
	}
	
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
	conn.setRequestProperty("Content-Type", "application/json");
	
	if( properties!=null ) {
	    Iterator<Pair<String,String>> it = properties.iterator();
	    while( it.hasNext( ) ) {
		Pair<String, String> pair = it.next( );
		//Log.d("RESTCLIENT","Adding property ["+pair.first+", "+pair.second+"]");
		conn.setRequestProperty( pair.first, pair.second );
	    }
	}
	conn.setReadTimeout( 20000 );
    conn.setConnectTimeout( 15000 );
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}

	conn.setDoInput(true);
	conn.setDoOutput(true);
	
	((HttpURLConnection)conn).setChunkedStreamingMode(0);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}
	
	//Log.d("RESTCLIENT", "extradata="+extradata);
	
	OutputStream out = null;
	try {
	    out = new BufferedOutputStream( conn.getOutputStream() );
	    out.write( extradata.getBytes( ) );
	    out.flush( );
	    out.close( );
	} catch(java.io.IOException ioe) {
	    if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("OutputStream.write/close: "+ioe.getMessage( ) );
	}
	
	int status = HttpStatus.SC_OK;
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	    //Log.d("RESTCLIENT","status="+status);
	} catch(IOException ioe) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("getResponseCode: "+ioe.getMessage( ) );
	}

	if( status != HttpStatus.SC_OK && status != HttpStatus.SC_ACCEPTED ) {
	    InputStream in = new BufferedInputStream( ((HttpURLConnection)conn).getErrorStream( ) );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while(-1 != (len = in.read(buffer))) {
			  buf += new String(buffer, 0, len);
		    }
		    in.close();
		} catch(IOException ioe) {
			if(usessl)
			      ((HttpsURLConnection)conn).disconnect( );
				else
				  ((HttpURLConnection)conn).disconnect( );
		    throw new RuntimeException("InputStream.write/close: "+ioe.getMessage( ) );
		}
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		if( status==HttpStatus.SC_UNAUTHORIZED ) {
			//Log.d("RESTCLIENT","NOT AUTHORIZED !");
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials and try again..." );
		}
		if( status==HttpStatus.SC_NOT_FOUND ) {
			//Log.d("RESTCLIENT","NOT FOUND !");
			throw new NotFoundException( "SC_NOT_FOUND: "+ParseUtils.getErrorMessage( buf ) );
		}
		if( status==HttpStatus.SC_BAD_REQUEST) {
			throw new NotAuthorizedException(  "Bad Request: "+ParseUtils.getErrorMessage( buf ) + " - " );
		}
		//Log.d("RESTCLIENT","GENERIC !");
		throw new GenericException( "Generic error: "+ParseUtils.getErrorMessage( buf ) );
	    }
	}
	
	BufferedInputStream inStream = null;
	String buf = "";
	try {
	    inStream = new BufferedInputStream( conn.getInputStream() );
	        
	    byte[] b = new byte[ 2048 ];
	    int res = 0;
	    
	    while( (res = inStream.read( b, 0, 2048 )) != -1 )
		if( res>0 )
		    buf += new String( b, 0, res );
	} catch(java.io.IOException ioe) {
	    //		Log.d("RESTClient", "2 Disconnecting...");
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	//	Log.d("RESTClient", "3 Disconnecting...");
	if(usessl)
		  ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	return buf.toString( );    	
	
    } 
    
    
    //________________________________________________________________________________
    public static void sendDELETERequest( boolean usessl,
    									  String sURL, 
					  					  String token,
					  					  Vector<Pair<String,String>> properties ) 
	throws RuntimeException, NotFoundException, NotAuthorizedException
    {
    	String Url = sURL;
    	if(usessl)
    		Url = "https://"+Url;
    	else
    		Url = "http://"+Url;
    	
	//	Log.d("RESTCLIENT", "sURL="+sURL);
	URL url = null;
	try {
	    url = new URL(Url);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("Malformed URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	conn.setRequestProperty("Content-Type", "application/octet-stream");
	conn.setRequestProperty("X-Auth-Token", token);
    
	try {
	    ((HttpURLConnection)conn).setRequestMethod("DELETE");
	} catch(java.net.ProtocolException pe ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "setRequestMethod: " + pe.getMessage( ) );
	}
	
	if( properties!=null ) {
	    Iterator<Pair<String,String>> it = properties.iterator();
	    while( it.hasNext( ) ) {
		Pair<String, String> pair = it.next( );
		conn.setRequestProperty( pair.first, pair.second );
	    }
	}
	
	conn.setDoInput(false);
	conn.setDoOutput(false);

	int status = HttpStatus.SC_OK;
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException( "getResponseCode: " + ioe.getMessage( ) );
	}

	//	Log.d("RESTCLIENT", "status="+status);
	
	if( status == HttpStatus.SC_NO_CONTENT) {
	    return;
	}

	if( status == HttpStatus.SC_NOT_FOUND ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new NotFoundException( "Server responded with NOT_FOUND (HTTP 404)" );
	}

	if( status != HttpStatus.SC_OK ) {
	    InputStream in = ((HttpURLConnection)conn).getErrorStream( );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer))) {
			//bos.write(buffer, 0, len);
			buf += new String(buffer, 0, len);
			//			Log.d("RESTCLIENT", new String(buffer, 0, len));
		    }
		    in.close();
		} catch(IOException ioe) {
			if(usessl)
			      ((HttpsURLConnection)conn).disconnect( );
				else
				  ((HttpURLConnection)conn).disconnect( );
		    throw new RuntimeException( "InputStream.read/close: " + ioe.getMessage( ) );
		}
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		if( status==HttpStatus.SC_UNAUTHORIZED ) 
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf ) );
		
		if( status==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );
		
		throw new RuntimeException( ParseUtils.getErrorMessage( buf ) );
	    }
	}
	if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
    }
}

