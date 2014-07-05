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

//import android.util.Log;
import org.apache.http.HttpStatus;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import org.openstack.comm.RuntimeException;

import android.util.Log;

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
	    throw new RuntimeException( "new URL: "+mfu.toString( ) );
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
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(POST): " + pe.getMessage( ) );
	}
	
	String data = "{\"auth\": {\"tenantName\": \"" + tenant + "\", \"passwordCredentials\": {\"username\": \"" + username + "\", \"password\": \"" + password + "\"}}}";
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
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}    
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
	String proto = "http://";
	
	String sUrl = proto + endpoint + ":9292/v2/images";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
	
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
	
	conn.setRequestProperty("User-Agent", "python-glanceclient");
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
	
	BufferedInputStream inStream = null;
	StringBuffer buf = new StringBuffer( 2048*1000 );
	
	try {
	    inStream = new BufferedInputStream( conn.getInputStream() );
	    int read;
            
	    byte[] b = new byte[ 2048 ];
	    int res = 0;
	    
	    while( (res = inStream.read( b, 0, 2048 )) != -1 ) {
		if( res>0 ) {
		    String tmp = new String( b, 0, res );
		    buf.append( tmp );
		} 
	    }
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	
	return buf.toString( );    
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
				       String tenant ) throws RuntimeException
    {
	String proto = "http://";
	
	String sUrl = proto + endpoint + ":8774/v2/"+tenantid+"/limits";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
    
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	conn.setRequestProperty("X-Auth-Project-Id", tenant);
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
    
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(GET): " + pe.getMessage( ) );
	}
	
	try {
	    String buf = "";
	    InputStream in = conn.getInputStream( );
	    int len;
	    String res = "";
	    byte[] buffer = new byte[4096];
	    while (-1 != (len = in.read(buffer))) {
		//bos.write(buffer, 0, len);
		res += new String(buffer, 0, len);
		//Log.d("requestToken", new String(buffer, 0, len));
	    }
	    in.close();
	    ((HttpURLConnection)conn).disconnect( );
	    //System.out.println(buf)
	    //Log.d("requestToken", buf);
	    return res;    
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("InputStream.read/close: " + ioe.getMessage( ) );
	}
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
	String proto = "http://";
	
	String sUrl = proto + endpoint + ":8774/v2/"+tenantid+"/servers/detail";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
    
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    //Log.d("RESTApiOpenStack.requestImages", "STEP 2");
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	conn.setRequestProperty("X-Auth-Project-Id", tenantname);
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
    
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException("setRequestMethod(GET): " + pe.getMessage( ) );
	}
	
	try {
	    String buf = "";
	    InputStream in = conn.getInputStream( );
	    int len;
	    String res = "";
	    byte[] buffer = new byte[4096];
	    while (-1 != (len = in.read(buffer))) {
		//bos.write(buffer, 0, len);
		res += new String(buffer, 0, len);
		//Log.d("requestToken", new String(buffer, 0, len));
	    }
	    in.close();
	    ((HttpURLConnection)conn).disconnect( );
	    //System.out.println(buf)
	    //Log.d("requestToken", buf);
	    return res;    
	} catch(IOException ioe) {
	    throw new RuntimeException("InputStream.read/close: " + ioe.getMessage( ) );
	}
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
	String proto = "http://";
	
	String sUrl = proto + endpoint + ":8774/v2/"+tenantid+"/flavors/detail";
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
    
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    //Log.d("RESTApiOpenStack.requestImages", "STEP 2");
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	conn.setRequestProperty("X-Auth-Project-Id", tenantname);
	conn.setRequestProperty("Accept", "application/json");
	conn.setRequestProperty("X-Auth-Token", token);
    
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(GET): " + pe.getMessage( ) );
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

    /**
     *
     *
     * curl -i -X GET -H "X-Auth-Token: $TOKEN"  -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     * curl -i -X DELETE -H "X-Auth-Token: $TOKEN" -H 'Content-Type: application/octet-stream' http://90.147.77.40:9292/v2/images/$IMAGEID
     *
     *
     *
     */
    public static void deleteGlanceImage( String endpoint, String token, String imageid) throws RuntimeException, NotFoundException, NotAuthorizedException {
	String proto = "http://";
	
	String sUrl = proto + endpoint + ":9292/v2/images/"+imageid;
	URL url = null;
	try {
	    url = new URL(sUrl);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
    
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    //Log.d("RESTApiOpenStack.requestImages", "STEP 2");
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
    
	//conn.setRequestProperty("X-Auth-Project-Id", tenantname);
	//conn.setRequestProperty("Accept", "application/json");
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

	//Log.d("RESTClient.deleteGlanceImage", "status="+status);
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
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials or that the image you're trying to delete is owned by you..." );
		
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );
		
		throw new RuntimeException( ParseUtils.getErrorMessage( buf ) );
	    }
	}
    }
}

