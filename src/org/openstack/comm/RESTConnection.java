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

//import android.util.Log;
import org.apache.http.HttpStatus;

import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

import android.util.Log;

public class RESTConnection {
    
    private static final int METHOD_GET = 1;
    private static final int METHOD_POST = 2;
    private static final int METHOD_DELETE = 3;

    private static final String[] methods = {"GET", "POST", "DELETE"};

    private StringBuffer buf = null;

    public RESTConnection( String url, Vector<Pair<String, String> > properties, int method ) throws RuntimeException {
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
	    conn.setRequestProperty(p.first, p.second);
	}
	conn.setRequestProperty("Content-Type", "application/json");
	conn.setRequestProperty("Accept", "application/json");

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
	
	//return buf.toString( );    	
    }

    public String getJSONResponse( ) { return buf.toString(); }
}
