package org.stackdroid.comm;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Iterator;

import org.apache.http.HttpStatus;
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
    public static String requestToken( boolean usessl, 
    								   String endpoint, 
    								   String tenantName, 
    								   String username, 
    								   String password )
      throws IOException, GenericException, 
      		 NotFoundException, NotAuthorizedException, 
      		 ServiceUnAvailableOrInternalError, MalformedURLException,
      		 ProtocolException
    {
	String proto = "http://";
	if(usessl)
	    proto = "https://";
	
	String sUrl = proto + endpoint + ":5000/v2.0/tokens";
	URL url = null;
	url = new URL(sUrl);
	
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
		throw  new IOException("RESTClient.requestToken.URL.openConnection https: "+ioe.getMessage( ) );
	    }
	} else {
	
	    try {
		  conn = (HttpURLConnection)url.openConnection();
	    } catch(java.io.IOException ioe) {
		  throw new IOException("RESTClient.requestToken.URL.openConnection http: "+ioe.getMessage());
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
	    throw new ProtocolException( "RESTClient.requestToken.setRequestMethod(POST): " + pe.getMessage( ) );
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
		if(mex==null || mex.length()==0 || mex.compareTo("null")==0) {
			mex = "RESTClient.requestToken.getResponseCode: Unable to get server's error message. Probably the endpoint is listening on SSL";
			
		} else
			mex = "RESTClient.requestToken.getResponseCode: " + mex;
	    throw new IOException( mex );
	}
	
	/**
	 * Handling of 5xx HTTP error codes
	 * Note: HttpStatus does not provide 506 error code (W3 stops at 505)
	 * status == HttpStatus.SC_SERVICE_UNAVAILABLE || // 503
	   status == HttpStatus.SC_BAD_GATEWAY || // 502
	   status == HttpStatus.SC_GATEWAY_TIMEOUT || // 504
	   status == HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED || // 505
	   status == HttpStatus.SC_INSUFFICIENT_STORAGE || // 507
	   status == HttpStatus.SC_INTERNAL_SERVER_ERROR || // 500
	   status == HttpStatus.SC_NOT_IMPLEMENTED ) // 501
	 */
	if( status >= 500 )
		throw(new ServiceUnAvailableOrInternalError());
	
	/**
	 * Any 4xx HTTP error from the Server
	 */
	if( status >= 400 ) {
		
		if(status == HttpStatus.SC_UNAUTHORIZED)
			throw new NotAuthorizedException( "RESTClient.requestToken: Not Authorized" );
		if(status == HttpStatus.SC_NOT_FOUND)
			throw new NotFoundException( "RESTClient.requestToken: Not Found" );
		
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
		    throw new IOException("RESTClient.requestToken.InputStream.write/close: "+ioe.getMessage( ) );
		}
	    
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		
		throw new GenericException( "RESTClient.requestToken: " + ParseUtils.getErrorMessage( buf ) );
	    }
	}
	
	/**
	 * Any other code:
	 * 1xx can be ignore for now
	 * 2xx are all for OK
	 * 3xx redirection (we assume that the cloud doesn't have this feature)
	 */
	
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
	    throw new IOException("RESTClient.requestToken.BufferedInputStream.read: " + ioe.getMessage( ) );
	}    
	if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
	return res;
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
		throws IOException, ServiceUnAvailableOrInternalError, MalformedURLException, ProtocolException
    {
    	String Url = sURL;
    	if(usessl)
	    Url = "https://"+Url;
    	else
	    Url = "http://"+Url;
    	
    	URL url = null;
    	url = new URL(Url);
    	
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
    			throw  new IOException("RESTClient.sendGETRequest.URL.openConnection https: "+ioe.getMessage( ) );
    		}
    	} else {
	
    		try {
    			//	    	Log.d("RESTClient", "2 Connecting...");
    			conn = (HttpURLConnection)url.openConnection();
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
	    conn.setConnectTimeout(15000 /* milliseconds */);
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
	  		 GenericException, ServiceUnAvailableOrInternalError, 
	  		 MalformedURLException, IOException, ProtocolException
    {
    	String Url = sURL;
    	if(usessl)
    		Url = "https://" + Url;
    	else
    		Url = "http://" + Url;
    
    	URL url = null;
    	url = new URL(Url);
	
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
    			throw  new IOException("RESTClient.sendPOSTRequest.URL.openConnection https: "+ioe.getMessage( ) );
    		}
    	} else {
	
    		try {
    			conn = (HttpURLConnection)url.openConnection();
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
    	conn.setReadTimeout( 20000 );
    	conn.setConnectTimeout( 15000 );
    	((HttpURLConnection)conn).setChunkedStreamingMode(0);
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
    		//Log.d("RESTCLIENT","status="+status);
    	} catch(IOException ioe) {
    		if(usessl)
    			((HttpsURLConnection)conn).disconnect( );
    		else
    			((HttpURLConnection)conn).disconnect( );
    		throw new IOException("RESTClient.sendPOSTRequest.getResponseCode: "+ioe.getMessage( ) );
    	}
	
    	/**
    	 * Handling of 5xx HTTP error codes
    	 * Note: HttpStatus does not provide 506 error code (W3 stops at 505)
    	 * status == HttpStatus.SC_SERVICE_UNAVAILABLE || // 503
	       status == HttpStatus.SC_BAD_GATEWAY || // 502
	       status == HttpStatus.SC_GATEWAY_TIMEOUT || // 504
	       status == HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED || // 505
	       status == HttpStatus.SC_INSUFFICIENT_STORAGE || // 507
	       status == HttpStatus.SC_INTERNAL_SERVER_ERROR || // 500
	       status == HttpStatus.SC_NOT_IMPLEMENTED ) // 501
	     */
    	if( status >= 500 )
    		throw(new ServiceUnAvailableOrInternalError());
	
	
	
	if( status >= 400 ) {
		

		if(status == HttpStatus.SC_UNAUTHORIZED)
			throw new NotAuthorizedException( "RESTClient.sendPOSTRequest: Not Authorized" );
		if(status == HttpStatus.SC_NOT_FOUND)
			throw new NotFoundException( "RESTClient.sendPOSTRequest: Not Found" );
		
		
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
		    throw new RuntimeException("RESTClient.sendPOSTRequest.InputStream.write/close: "+ioe.getMessage( ) );
		}
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		
		
		throw new GenericException( "RESTClient.sendPOSTRequest: " + ParseUtils.getErrorMessage( buf ) );
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
	    throw new IOException("RESTClient.sendPOSTRequest.read: " + ioe.getMessage( ) );
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
	throws NotFoundException, NotAuthorizedException, ServiceUnAvailableOrInternalError, IOException, GenericException, MalformedURLException
    {
    	String Url = sURL;
    	if(usessl)
    		Url = "https://"+Url;
    	else
    		Url = "http://"+Url;
    	
	//	Log.d("RESTCLIENT", "sURL="+sURL);
	URL url = null;
	url = new URL(Url);
	
	URLConnection conn = null;
	
	try {
	    conn = (HttpURLConnection)url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new IOException("RESTClient.sendDELETERequest.URL.openConnection http: "+ioe.getMessage( ) );
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
	    throw new RuntimeException( "RESTClient.sendDELETERequest.setRequestMethod(DELETE): " + pe.getMessage( ) );
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
	    throw new IOException( "RESTClient.sendDELETERequest.getResponseCode: " + ioe.getMessage( ) );
	}

	/**
	 * Handling of 5xx HTTP error codes
	 * Note: HttpStatus does not provide 506 error code (W3 stops at 505)
	 * status == HttpStatus.SC_SERVICE_UNAVAILABLE || // 503
       status == HttpStatus.SC_BAD_GATEWAY || // 502
       status == HttpStatus.SC_GATEWAY_TIMEOUT || // 504
       status == HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED || // 505
       status == HttpStatus.SC_INSUFFICIENT_STORAGE || // 507
       status == HttpStatus.SC_INTERNAL_SERVER_ERROR || // 500
       status == HttpStatus.SC_NOT_IMPLEMENTED ) // 501
     */
	if( status >= 500 )
		throw(new ServiceUnAvailableOrInternalError());

	
	if( status == HttpStatus.SC_NO_CONTENT) {
	    return;
	}

/*	if( status == HttpStatus.SC_NOT_FOUND ) {
		if(usessl)
		      ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
	    throw new NotFoundException( "Server responded with NOT_FOUND (HTTP 404)" );
	}*/

	if( status >= 400 ) {
		
		if(status == HttpStatus.SC_UNAUTHORIZED)
			throw new NotAuthorizedException( "RESTClient.sendDELETERequest: Not Authorized" );
		if(status == HttpStatus.SC_NOT_FOUND)
			throw new NotFoundException( "RESTClient.sendDELETERequest: Not Found" );
		
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
		    throw new IOException( "RESTClient.sendDELETERequest.InputStream.read/close: " + ioe.getMessage( ) );
		}
		if(usessl)
			  ((HttpsURLConnection)conn).disconnect( );
			else
			  ((HttpURLConnection)conn).disconnect( );
		
		throw new GenericException( "RESTClient.sendDELETERequest: " + ParseUtils.getErrorMessage( buf ) );
	    }
	}
	if(usessl)
	      ((HttpsURLConnection)conn).disconnect( );
		else
		  ((HttpURLConnection)conn).disconnect( );
    }
}

