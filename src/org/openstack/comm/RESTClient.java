package org.openstack.comm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.KeyManager;

import java.security.cert.X509Certificate;
import java.security.KeyStore;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.net.*;
import java.lang.Thread;

import java.util.Date;
import java.util.Vector;
import java.util.Iterator;

import org.apache.http.HttpStatus;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

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
    public static String requestToken( String endpoint,
				       String tenant,
				       String username,
				       String password,
				       boolean usessl ) throws RuntimeException, GenericException, NotFoundException, NotAuthorizedException
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
		conn = (HttpsURLConnection)url.openConnection();
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
	((HttpsURLConnection)conn).setChunkedStreamingMode(0);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}
	
	String data = "{\"auth\": {\"tenantName\": \"" + tenant + "\", \"passwordCredentials\": {\"username\": \"" + username + "\", \"password\": \"" + password + "\"}}}";
	//OutputStreamWriter out = null;
	OutputStream out = null;
	try {
	    //	    out = new OutputStreamWriter(conn.getOutputStream());
	    out = new BufferedOutputStream( conn.getOutputStream() );
	    out.write( data.getBytes( ) );
	    out.flush( );
	    out.close( );
	} catch(java.io.IOException ioe) {
	    ((HttpsURLConnection)conn).disconnect( );
	    //ioe.printStackTrace( );
	    throw new RuntimeException("OutputStream.write/close: "+ioe.getMessage( ) );
	}
	
	int status = HttpStatus.SC_OK;
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
	    ((HttpsURLConnection)conn).disconnect( );
	    throw new RuntimeException("getResponseCode: "+ioe.getMessage( ) );
	}

	if( status != HttpStatus.SC_OK ) {
	    InputStream in = new BufferedInputStream( ((HttpsURLConnection)conn).getErrorStream( ) );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer)))
			buf += new String(buffer, 0, len);
		    in.close();
		} catch(IOException ioe) {
		    ((HttpsURLConnection)conn).disconnect( );
		    throw new RuntimeException("InputStream.write/close: "+ioe.getMessage( ) );
		}
	    
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
	    String buf = "";
	    InputStream in = new BufferedInputStream( conn.getInputStream( ) );
	    int len;
	    byte[] buffer = new byte[4096];
	    while (-1 != (len = in.read(buffer)))
		res += new String(buffer, 0, len);
	    in.close();
	    
	} catch(java.io.IOException ioe) {
	    ((HttpURLConnection)conn).disconnect( );
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}    
	((HttpsURLConnection)conn).disconnect( );
	return res;
    }
    
    /**
     *
     *
     * curl -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -H "Content-Type: application/json" http://cloud-areapd.pd.infn.it:9292/v2/images
     *
     *
     *
     */
    public static String requestImages( String endpoint,
					String token ) throws RuntimeException  
    {
	return sendGETRequest( "http://" + endpoint + ":9292/v2/images", token, null );   
    }
    
    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/limits -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestQuota( String endpoint,
				       String token,
				       String tenantid,
				       String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/"+tenantid+"/limits", token, v);
	//Log.d("RESTClient.requestQuota", "RES="+res);
	//return res;
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/limits -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestFloatingIPs( String endpoint,
					     String token,
					     String tenantid,
					     String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/"+tenantid+"/os-floating-ips", token, v);
    }

    /**
     *
     *
     * curl -i http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/servers/details -X GET -H "X-Auth-Project-Id: admin" -H "User-Agent: python-novaclient" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN"
     *
     *
     *
     */
    public static String requestServers( String endpoint,
					 String token,
					 String tenantid,
					 String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/"+tenantid+"/servers/detail",
			    token, 
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
    public static String requestFlavors( String endpoint,
					 String token,
					 String tenantid,
					 String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/"+tenantid+"/flavors/detail",
			       token, 
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
    public static void deleteGlanceImage( String endpoint, String token, String imageid) 
	throws RuntimeException, NotFoundException
    {
	try {
	    sendDELETERequest( "http://" + endpoint + ":9292/v2/images/"+imageid, 
			       token,
			       null );
	} catch(NotAuthorizedException na) {
	    throw new RuntimeException(na.getMessage() + "\n\nPlease check your credentials or that the image you're trying to delete is owned by you...");
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
    public static String requestNetworks( String endpoint,
					  String token,
					  String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":9696/v2.0/networks",
			       token, 
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
    public static String requestSubNetworks( String endpoint,
					     String token,
					     String tenantname ) throws RuntimeException
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":9696/v2.0/subnets",
			       token, 
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
    public static String requestKeypairs( String endpoint, String tenantid, String tenantname, String token ) throws RuntimeException 
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/" + tenantid + "/os-keypairs",
			       token, 
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
    public static String requestSecGroups( String endpoint, String tenantid, String tenantname, String token ) throws RuntimeException 
    {
	Pair<String, String> p = new Pair( "X-Auth-Project-Id", tenantname );
	Vector<Pair<String, String>> v = new Vector();
	v.add(p);
	return sendGETRequest( "http://" + endpoint + ":8774/v2/" + tenantid + "/os-security-groups",
			       token, 
			       v );
    }

    /**
     *
     *
     * curl -i 'http://90.147.77.40:8774/v2/467d2e5792b74af282169a26c97ac610/servers' -X POST -H "X-Auth-Project-Id: admin" -H "Content-Type: application/json" -H "Accept: application/json" -H "X-Auth-Token: $TOKEN" -d '{"server": {"name": "BLAHBLAHBLAH", "imageRef": "4988f1ee-5cfc-4505-aed1-6d812442a56d", "key_name": "lxadorigo", "flavorRef": "b639f517-c01f-483f-a8e2-c9ee3370ac36", "max_count": 1, "min_count": 1, "networks": [{"uuid": "e93ad35f-aac5-4fa7-bfc9-1e3c45d58fc1"}], "security_groups": [{"name": "848f1b29-c793-415c-8f3f-10836c1f99f7"}, {"name": "cf5b187b-1e1c-4ca2-87a9-54b5dce244bc"}]}}'
     *
     */
    public static String requestInstanceCreation( String endpoint, 
						  String tenantid,
						  String tenantname, 
						  String token,
						  String instanceName, 
						  String glanceImageID,
						  String key_name, 
						  String flavorID,
						  int count, 
						  String netID,
						  String _secgrpIDs,
						  String FixedIP) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException
    {
	String sUrl = "http://" + endpoint + ":8774/v2/" + tenantid + "/servers";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    //	    Log.d("RESTClient", "EXCEPTION 1");
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
    
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	conn.setRequestProperty("X-Auth-Project-Id", tenantname );
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("X-Auth-Token", token );
	conn.setDoOutput(true);

	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}

	String fixedip="";
	if(FixedIP != null) {
	    fixedip = ",\"fixed_ip\": \"" + FixedIP + "\"";
	}

	String _data = "{\"server\": {\"name\": \"" + instanceName + 
	    "\", \"imageRef\": \"" + glanceImageID + 
	    "\", \"key_name\": \"" + key_name + 
	    "\", \"flavorRef\": \"" + flavorID + 
	    "\", \"max_count\": " + count + 
	    ", \"min_count\": " + count + 
	    ", \"networks\": [{\"uuid\": \"" + netID + "\"" + fixedip + "}]}}";

	JSONObject obj = null;
	String []secgrpIDs = _secgrpIDs.split(",");
	try {
	    obj = new JSONObject( _data );
	    JSONArray secgs = new JSONArray();
	    for(int i = 0; i<secgrpIDs.length; ++i)
		secgs.put( new JSONObject("{\"name\": \"" + secgrpIDs[i] + "\"}") );
	    obj.getJSONObject("server").put("security_groups", secgs);
	    
	} catch(JSONException je) {
	    throw new RuntimeException("JSON parsing: "+je.getMessage( ) );
	}
	
	String data = obj.toString( );

	OutputStreamWriter out = null;
	try {
	    out = new OutputStreamWriter(conn.getOutputStream());
	    out.write(data);
	    out.close();
	} catch(java.io.IOException ioe) {
	    ioe.printStackTrace( );
	    throw new RuntimeException("OutputStreamWriter.write/close: "+ioe.getMessage( ) );
	}

	int status = HttpStatus.SC_OK;
	
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
	    throw new RuntimeException("getResponseCode: "+ioe.getMessage( ) );
	}
	if( status != HttpStatus.SC_OK && status !=HttpStatus.SC_ACCEPTED ) {
	    if(status == HttpStatus.SC_BAD_REQUEST) 
		throw new RuntimeException("Bad HTTP request" );

	    InputStream in = ((HttpURLConnection)conn).getErrorStream( );
	    if(in!=null) {
		int len;
		String buf = "";
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer)))
			buf += new String(buffer, 0, len);
		    in.close();
		} catch(IOException ioe) {
		    throw new RuntimeException("InputStream.write/close: "+ioe.getMessage( ) );
		}
	    
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_UNAUTHORIZED ) {
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials and try again..." );
		}
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );
		throw new GenericException( ParseUtils.getErrorMessage( buf ) );
	    }
	}

	try {
	    String buf = "";
	    InputStream in = conn.getInputStream( );
	    int len;
	    String res = "";
	    byte[] buffer = new byte[4096];
	    while (-1 != (len = in.read(buffer)))
		res += new String(buffer, 0, len);
	    
	    in.close();
	    ((HttpURLConnection)conn).disconnect( );
	    return res; 
	} catch(IOException ioe) {
	    throw new RuntimeException("InputStream.read/close: " + ioe.getMessage( ) );   
	}
    } 

    //________________________________________________________________________________
    public static String sendGETRequest( String sURL, 
					 String token,
					 Vector<Pair<String,String>> properties ) throws RuntimeException 
    {
	URL url = null;
	try {
	    url = new URL(sURL);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("Malformed URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
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
	
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(GET): " + pe.getMessage( ) );
	}

	BufferedInputStream inStream = null;
	String buf = "";
	try {
	    inStream = new BufferedInputStream( conn.getInputStream() );
	    int read;
            
	    byte[] b = new byte[ 2048 ];
	    int res = 0;
	    
	    while( (res = inStream.read( b, 0, 2048 )) != -1 )
		if( res>0 )
		    buf += new String( b, 0, res );
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	
	return buf.toString( );    	
    } 

    //________________________________________________________________________________
    public static void sendDELETERequest( String sURL, 
					  String token,
					  Vector<Pair<String,String>> properties ) 
	throws RuntimeException, NotFoundException, NotAuthorizedException
    {
	URL url = null;
	try {
	    url = new URL(sURL);
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
	    throw new RuntimeException( "setRequestMethod: " + pe.getMessage( ) );
	}
	

	int status = HttpStatus.SC_OK;
	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
	    throw new RuntimeException( "getResponseCode: " + ioe.getMessage( ) );
	}

	if( status == HttpStatus.SC_NO_CONTENT) {
	    return;
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
			//Log.d("requestToken", new String(buffer, 0, len));
		    }
		    in.close();
		} catch(IOException ioe) {
		    throw new RuntimeException( "InputStream.read/close: " + ioe.getMessage( ) );
		}
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_UNAUTHORIZED ) 
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf ) );//+"\n\nPlease check your credentials or that the image you're trying to delete is owned by you..." );
		
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );
		
		throw new RuntimeException( ParseUtils.getErrorMessage( buf ) );
	    }
	}	
    }
}

