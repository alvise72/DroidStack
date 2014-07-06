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

import android.util.Pair;

import org.apache.http.HttpStatus;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import android.util.Log;

public class RESTConnection {
    
    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_DELETE = 3;

    public static final String[] methods = {"GET", "POST", "DELETE"};

    private StringBuffer buf = new StringBuffer(1048576);

    /**
     *
     *
     *
     *
     *
     */
    public RESTConnection( String url, Vector<Pair<String, String> > properties, int method ) throws RuntimeException, NotAuthorizedException, NotFoundException, GenericException {
	Log.d("RESTConnection", "URL="+url);

	URL _url = null;
	try {
	    _url = new URL(url);
	} catch(java.net.MalformedURLException mfu) {
	    throw new RuntimeException("new URL: " + mfu.toString( ) );
	}
	URLConnection conn = null;
	
	try {
	    conn = (HttpURLConnection)_url.openConnection();
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("URL.openConnection http: "+ioe.getMessage( ) );
	}
	Iterator<Pair<String,String>> it = properties.iterator();
	while( it.hasNext( ) ) {
	    Pair<String,String> p = it.next( );
	    Log.d("RESTConnection", "Adding property: " + p.first + " -> " + p.second);
	    conn.setRequestProperty(p.first, p.second);
	}
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("Accept", "application/json");

	try {
	    Log.d("RESTConnection", "Setting method: "+methods[method]);
	    ((HttpURLConnection)conn).setRequestMethod(methods[method]);
	} catch(java.net.ProtocolException pe ) {
	    throw new RuntimeException( "setRequestMethod(" + methods[method]+"): " + pe.getMessage( ) );
	}
	Log.d("RESTConnection", "Getting Status");
	int status = HttpStatus.SC_OK;
	try {
	     status = ((HttpURLConnection)conn).getResponseCode();
	} catch(IOException ioe) {
	    throw new RuntimeException("getResponseCode: "+ioe.getMessage( ) );
	}

	if( status == HttpStatus.SC_NO_CONTENT) {
	    return;
	}

	if( status != HttpStatus.SC_OK ) {
	    Log.d("RESTConnection", "STATUS NOT OK="+status);
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

		Log.d("RESTConnection", "ERRORBUF="+buf);
	    
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_UNAUTHORIZED ) {
		    throw new NotAuthorizedException(  ParseUtils.getErrorMessage( buf )+"\n\nPlease check your credentials and try again..." );
		}
		if( ParseUtils.getErrorCode(buf)==HttpStatus.SC_NOT_FOUND ) 
		    throw new NotFoundException(  ParseUtils.getErrorMessage( buf ) );

		throw new GenericException( ParseUtils.getErrorMessage( buf ) );
	    }
	}

	BufferedInputStream inStream = null;
	buf = new StringBuffer( 1048576 );
	
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
	    inStream.close();
	    ((HttpURLConnection)conn).disconnect( );
	} catch(java.io.IOException ioe) {
	    throw new RuntimeException("BufferedInputStream.read: " + ioe.getMessage( ) );
	}

	Log.d("RESTConnection", "buf="+buf.toString());
    }

    /**
     *
     *
     *
     *
     *
     */
    public String getJSONResponse( ) { return buf.toString(); }
}
