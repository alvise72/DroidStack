package org.stackdroid.comm;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.security.cert.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.apache.http.HttpStatus;
import org.stackdroid.parse.ParseException;
import org.stackdroid.parse.ParseUtils;

import android.util.Log;
import android.util.Pair;

public class RESTClient {

    /**
     *
     *
     *
     *
     *
     *
     */
    public static boolean checkServerCert ( String sURL, String CAIssuer ) throws IOException, ServiceUnAvailableOrInternalError, MalformedURLException, ProtocolException {
	
	if(sURL.endsWith("/tokens")) {
	    sURL = sURL.substring(0, sURL.length()-7);
	}
	URL url = new URL(sURL);
	URLConnection conn = null;
	TrustManager[] trustAllCerts = null;
	
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
	    conn.setConnectTimeout(10000);
	} catch(java.io.IOException ioe) {
	    throw new IOException("RESTClient.checkServerCert.URL.openConnection https: " + ioe.getMessage());
	}
	
	conn.setReadTimeout(20000 /* milliseconds */);
	//conn.setConnectTimeout(10000 /* milliseconds */);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    
	    
	    ((HttpsURLConnection)conn).disconnect( );
	    
	    throw new ProtocolException( "RESTClient.checkServerCert.setRequestMethod(GET): " + pe.getMessage( ) );
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
	    
	    
	    boolean validcertServer = false;
	    
	    Certificate[] certs = (Certificate[]) ((HttpsURLConnection)conn).getServerCertificates();
	    
	    for( Certificate cert : certs ) {
		try {
		    ((X509Certificate)cert).checkValidity();
		} catch(CertificateExpiredException e) {
		    //Log.d("RESTCLIENT", "CERT EXPIRED");
		    throw  new IOException("RESTClient.checkServerCert.URL.openConnection https: "+e.getMessage( ) );
		} catch(CertificateNotYetValidException e) {
		    //Log.d("RESTCLIENT", "CERT NOT YET VALID");
		    throw  new IOException("RESTClient.checkServerCert.URL.openConnection https: "+e.getMessage( ) );
		}
		if(((X509Certificate)cert).getIssuerX500Principal().getName().equals(CAIssuer) == true) {
		    //Log.d("RESTCLIENT","Certificates' issuers match: ["+CAIssuer+"] vs. ["+((X509Certificate)cert).getIssuerX500Principal().getName()+"]");
		    return true;
		}
	    }
	    return false;
	    
	} catch(java.io.IOException ioe) {
	    ((HttpsURLConnection)conn).disconnect( );
	    throw new IOException("RESTClient.checkServerCert.BufferedInputStream.read: " + ioe.getMessage( ) );
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
    public static Pair<String,String> requestToken( boolean usessl, 
				       		    String endpoint, 
				       		    String requestPayload)
	throws IOException, ServerException, 
	       NotFoundException, NotAuthorizedException, 
	       ServiceUnAvailableOrInternalError {
	
    	URL url = new URL(endpoint);
	
    	URLConnection conn = null;
    	TrustManager[] trustAllCerts = null;

	//Log.v("RESTClient.requestToken", "url="+url);
	//Log.v("RESTClient.requestToken", "payload="+requestPayload);
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
		conn.setConnectTimeout(10000);
		
	    } catch(java.io.IOException ioe) {
		throw  new IOException("RESTClient.requestToken.URL.openConnection https: "+ioe.getMessage( ) );
	    }
	} else {
	    try {
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw new IOException("RESTClient.requestToken.URL.openConnection http: "+ioe.getMessage());
	    }
	}
	
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("Accept", "application/json");
	conn.setDoOutput(true);
	conn.setDoInput(true);
	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
	    
	} catch(java.net.ProtocolException pe ) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new ProtocolException( "RESTClient.requestToken.setRequestMethod(POST): " + pe.getMessage( ) );
	}
	/*	
	String data = "{\"auth\": {\"tenantName\": \""
	    + tenantName 
	    + "\", \"passwordCredentials\": {\"username\": \"" 
	    + username + "\", \"password\": \"" 
	    + password + "\"}}}";
	*/
	OutputStream out = null;
	try {
	    conn.setReadTimeout(10000);
	    out = new BufferedOutputStream( conn.getOutputStream() );
	    out.write( requestPayload.getBytes( ) );
	    out.flush( );
	    out.close( );
	} catch(java.io.IOException ioe) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new IOException("RESTClient.requestToken.OutputStream.write/close: "+ioe.getMessage( ) );
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
	    if(mex==null || mex.length()==0 || mex.compareTo("null")==0)
		mex = "RESTClient.requestToken.getResponseCode: Unable to get server's error message. Probably the endpoint is listening on SSL";
	    else
		mex = "RESTClient.requestToken.getResponseCode: " + mex;
	    throw new IOException( mex );
	}

	//	Log.v("RESTClient.requestToken", "STATUS CODE="+status);
	
	if( status >= 500 )
	    throw(new ServiceUnAvailableOrInternalError());
	
	/**
	 * Any 4xx HTTP error from the Server
	 */
	if( status >= 400 ) {
	    InputStream in = new BufferedInputStream( ((HttpURLConnection)conn).getErrorStream( ) );
	    String buf = "";
	    if(in!=null) {
	    	int len;
	    	
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
		    throw new IOException("RESTClient.requestToken.InputStream.write/close: "+ioe.getMessage( ) );
	    	}
		
	    	if(usessl)
		    ((HttpsURLConnection)conn).disconnect( );
		else
		    ((HttpURLConnection)conn).disconnect( );
	    }
	    
	    throw new ServerException( buf );
	}
	
	/**
	 * Any other code:
	 * 1xx can be ignored for now
	 * 2xx are all for OK
	 * 3xx redirection (we assume that the cloud doesn't have this feature)
	 */
	//Log.v("RESTClient.requestToken", "status="+status);
	String res = "";
	try {
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
	    throw new IOException("RESTClient.requestToken.BufferedInputStream.read: " + ioe.getMessage( ) );
	}   
	String v3Token = null;
	Map<String, List<String>> headers = conn.getHeaderFields( );
	for( String elem : headers.keySet( ) ) {
	  if(elem!=null && elem.compareTo("X-Subject-Token")==0)
	  	v3Token = headers.get(elem).get(0);
	}
	//Log.v("RESTClient.requestToken", "v3Token="+v3Token);
	
	if(usessl)
	    ((HttpsURLConnection)conn).disconnect( );
	else
	    ((HttpURLConnection)conn).disconnect( );
	Pair<String,String> resP = new Pair<String,String>(res, v3Token);
	
	return resP;
    }
    
    /**
     *
     *
     * 
     *
     *
     *
     */
    public static String sendGETRequestWithoutToken( boolean usessl,
					 	     String sURL ) 
    throws IOException, ServiceUnAvailableOrInternalError {
        URL url = new URL(sURL);
      
      
      
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
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw  new IOException("RESTClient.sendGETRequest.URL.openConnection https: "+ioe.getMessage( ) );
	    }
    	} else {
	    
	    try {
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw new IOException("RESTClient.sendGETRequest.URL.openConnection http: "+ioe.getMessage());
	    }
    	}
	conn.setReadTimeout(20000 /* milliseconds */);
	
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new ProtocolException( "RESTClient.sendGETRequestWithoutToken.setRequestMethod(GET): " + pe.getMessage( ) );
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
	    
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    
	    throw new IOException("RESTClient.sendGETRequest.BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	
	
	
	if(usessl)
	    ((HttpsURLConnection)conn).disconnect( );
	else
	    ((HttpURLConnection)conn).disconnect( );
	return buf.toString( ); 
    }
    
    /**
     *
     *
     * 
     *
     *
     *
     */
    public static String sendGETRequest( boolean usessl,
					 String sURL, 
					 String token,
					 Vector<Pair<String,String>> properties ) 
	throws IOException, ServiceUnAvailableOrInternalError {
	
    	Log.v("RESTClient.sedGETRequest", "sURL=["+sURL+"]");
    	URL url = new URL(sURL);
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
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw  new IOException("RESTClient.sendGETRequest.URL.openConnection https: "+ioe.getMessage( ) );
	    }
    	} else {
	    
	    try {
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw new IOException("RESTClient.sendGETRequest.URL.openConnection http: "+ioe.getMessage());
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
	
	try {
	    ((HttpURLConnection)conn).setRequestMethod("GET");
	} catch(java.net.ProtocolException pe ) {
	    
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new ProtocolException( "RESTClient.sendGETRequest.setRequestMethod(GET): " + pe.getMessage( ) );
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
	    
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    
	    throw new IOException("RESTClient.sendGETRequest.BufferedInputStream.read: " + ioe.getMessage( ) );
	}
	
	if(usessl)
	    ((HttpsURLConnection)conn).disconnect( );
	else
	    ((HttpURLConnection)conn).disconnect( );
	return buf.toString( );    	
    } 
    
    
    /**
     *
     *
     * 
     *
     *
     *
     */
    public static String sendPOSTRequest( boolean usessl,
					  String sURL, 
					  String token,
					  String extradata,
					  Vector<Pair<String,String>> properties ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError, 
	       MalformedURLException, IOException, ProtocolException
    {
	
    	//if(sURL.startsWith("https://")) usessl=true;
    	//if(sURL.startsWith("http://")) usessl=false;
    	
    	URL url = new URL(sURL);
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
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw  new IOException("RESTClient.sendPOSTRequest.URL.openConnection https: "+ioe.getMessage( ) );
	    }
    	} else {
	    
	    try {
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw new IOException("RESTClient.sendPOSTRequest.URL.openConnection http: "+ioe.getMessage());
	    }
    	}
	
    	conn.setRequestProperty("Accept", "application/json");
    	conn.setRequestProperty("X-Auth-Token", token);
    	conn.setRequestProperty("Content-Type", "application/json");
	
    	if( properties!=null ) {
	    Iterator<Pair<String,String>> it = properties.iterator();
		   while( it.hasNext( ) ) {
		       Pair<String, String> pair = it.next( );
		       conn.setRequestProperty( pair.first, pair.second );
		   }
    	}
    	conn.setReadTimeout( 10000 );
    	conn.setConnectTimeout( 10000 );
    	try {
	    ((HttpURLConnection)conn).setRequestMethod("POST");
    	} catch(java.net.ProtocolException pe ) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
    		throw new ProtocolException( "RESTClient.sendPOSTRequest.setRequestMethod(POST): " + pe.getMessage( ) );
    	}
	
    	conn.setDoInput(true);
    	conn.setDoOutput(true);
	
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
	    throw new IOException("RESTClient.sendPOSTRequest.OutputStream.write/close: "+ioe.getMessage( ) );
    	}
	
    	int status = HttpStatus.SC_OK;
    	
    	
    	
    	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
    	} catch(IOException ioe) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new IOException("RESTClient.sendPOSTRequest.getResponseCode: "+ioe.getMessage( ) );
    	}
    	
    	if( status >= 400 ) {
	    String buf = "";
	    InputStream in = new BufferedInputStream( ((HttpURLConnection)conn).getErrorStream( ) );
	    if(in!=null) {
		int len;
	    	
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
		    throw new IOException("RESTClient.sendPOSTRequest.InputStream.write/close: "+ioe.getMessage( ) );
		}
		if(usessl)
		    ((HttpsURLConnection)conn).disconnect( );
		else
		    ((HttpURLConnection)conn).disconnect( );
	    }
	    throw new ServerException( buf );
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
	    throw new IOException("RESTClient.sendPOSTRequest.read: " + ioe.getMessage( ) );
    	}
    	if(usessl)
	    ((HttpsURLConnection)conn).disconnect( );
	else
	    ((HttpURLConnection)conn).disconnect( );
    	return buf.toString( );    	
	
    } 
    
    /**
     *
     *
     * 
     *
     *
     *
     */
    public static String sendPUTRequest( boolean usessl,
					 String sURL, 
					 String token,
					 String extradata,
					 Vector<Pair<String,String>> properties ) 
	throws NotAuthorizedException, NotFoundException, 
	       ServerException, ServiceUnAvailableOrInternalError, 
	       MalformedURLException, IOException, ProtocolException
    {
   	//Log.d("REST", "sURL="+sURL);
   	//Log.d("REST", "extradata="+extradata);
   	//if(sURL.startsWith("https://")) usessl=true;
   	//if(sURL.startsWith("http://")) usessl=false;
   	
   	URL url = new URL(sURL);
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
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw  new IOException("RESTClient.sendPUTRequest.URL.openConnection https: "+ioe.getMessage( ) );
	    }
   	} else {
	    
	    try {
		conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	    } catch(java.io.IOException ioe) {
		throw new IOException("RESTClient.sendPUTRequest.URL.openConnection http: "+ioe.getMessage());
	    }
   	}
	
   	conn.setRequestProperty("Accept", "application/json");
   	conn.setRequestProperty("X-Auth-Token", token);
   	conn.setRequestProperty("Content-Type", "application/json");
	
   	if( properties!=null ) {
	    Iterator<Pair<String,String>> it = properties.iterator();
	    while( it.hasNext( ) ) {
		Pair<String, String> pair = it.next( );
		conn.setRequestProperty( pair.first, pair.second );
	    }
   	}
   	conn.setReadTimeout( 10000 );
   	conn.setConnectTimeout( 10000 );
   	try {
	    ((HttpURLConnection)conn).setRequestMethod("PUT");
   	} catch(java.net.ProtocolException pe ) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new ProtocolException( "RESTClient.sendPUTRequest.setRequestMethod(POST): " + pe.getMessage( ) );
   	}
	
   	conn.setDoInput(true);
   	conn.setDoOutput(true);
	
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
	    throw new IOException("RESTClient.sendPUTRequest.OutputStream.write/close: "+ioe.getMessage( ) );
   	}
	
   	int status = HttpStatus.SC_OK;
   	try {
	    status = ((HttpURLConnection)conn).getResponseCode();
   	} catch(IOException ioe) {
	    if(usessl)
		((HttpsURLConnection)conn).disconnect( );
	    else
		((HttpURLConnection)conn).disconnect( );
	    throw new IOException("RESTClient.sendPUTRequest.getResponseCode: "+ioe.getMessage( ) );
   	}
	
   	if( status >= 400 ) {
	    String buf = "";
	    InputStream in = new BufferedInputStream( ((HttpURLConnection)conn).getErrorStream( ) );
	    if(in!=null) {
		int len;
	    	
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
		    throw new IOException("RESTClient.sendPUTRequest.InputStream.write/close: "+ioe.getMessage( ) );
		}
		if(usessl)
		    ((HttpsURLConnection)conn).disconnect( );
		else
		    ((HttpURLConnection)conn).disconnect( );
	    }
	    
	    throw new ServerException(buf);
	    
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
   		throw new IOException("RESTClient.sendPUTRequest.read: " + ioe.getMessage( ) );
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
	throws NotFoundException, NotAuthorizedException, ServiceUnAvailableOrInternalError, IOException, ServerException, MalformedURLException, ProtocolException
    {

    	//if(sURL.startsWith("https://")) usessl=true;
    	//if(sURL.startsWith("http://")) usessl=false;
    	
	URL url = new URL(sURL);
	
	URLConnection conn = null;
	
	try {
	    conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(10000);
	} catch(java.io.IOException ioe) {
	    throw new IOException("RESTClient.sendDELETERequest.URL.openConnection http: "+ioe.getMessage( ) );
	}
    
//	conn.setRequestProperty("Content-Type", "application/octet-stream");
	conn.setRequestProperty("X-Auth-Token", token);
   	conn.setRequestProperty("Content-Type", "application/json");
    
	try {
	    ((HttpURLConnection)conn).setRequestMethod("DELETE");
	} catch(java.net.ProtocolException pe ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new ProtocolException( "RESTClient.sendDELETERequest.setRequestMethod(DELETE): " + pe.getMessage( ) );
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
	    Log.v("RESTClient.sendDELETERequest", "status="+status);
	} catch(IOException ioe) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new IOException( "RESTClient.sendDELETERequest.getResponseCode: " + ioe.getMessage( ) );
	}

	if( status >= 500 )
		throw(new ServiceUnAvailableOrInternalError());

	
	if( status == HttpStatus.SC_NO_CONTENT) {
	    return;
	}

	if( status >= 400 ) {
		
		String buf = "";
	    InputStream in = ((HttpURLConnection)conn).getErrorStream( );
	    if(in!=null) {
		int len;
		
		byte[] buffer = new byte[4096];
		try {
		    while (-1 != (len = in.read(buffer))) {
			buf += new String(buffer, 0, len);
		    }
		    in.close();
		} catch(IOException ioe) {
			if(usessl)
			      ((HttpsURLConnection)conn).disconnect( );
				else
				  ((HttpURLConnection)conn).disconnect( );
		    throw new IOException( "RESTClient.sendDELETERequest.InputStream.read/close: " + ioe.getMessage( ) );
		}
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    }
	    //Log.v("RESTClient.sendDELETERequest", "BUF="+buf);
	    throw new ServerException( buf );
	}
	if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
    }
}

