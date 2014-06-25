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
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// http://examples.javacodegeeks.com/core-java/json/java-json-parser-example/
// https://code.google.com/p/json-simple/
// https://code.google.com/p/json-simple/wiki/DecodingExamples
public class testAPI {

  public static void main(String[] args) {

   try {

   String sUrl = "https://cloud-areapd.pd.infn.it:5000/v2.0/tokens";
   URL url = new URL(sUrl);
   
   TrustManager[] trustAllCerts = new TrustManager[] {
       new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

       }
    };
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
   
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
   
   
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setRequestProperty("Accept", "application/json");
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");

    String data = "{\"auth\": {\"tenantName\": \"admin\", \"passwordCredentials\": {\"username\": \"admin\", \"password\": \"ADMIN_PASS\"}}}";
    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
    out.write(data);
    out.close();

    BufferedInputStream inStream = new BufferedInputStream(  conn.getInputStream());
    int read;
            
    byte[] b = new byte[ 2048 ];
    int res = 0;
    StringBuffer buf = new StringBuffer( 2048*1000 );
    while( (res = inStream.read( b, 0, 2048 )) != -1 ) {
      if( res>0 ) {
        String tmp = new String( b, 0, res );
        buf.append( tmp );
      } 
    }
    System.out.println(buf);
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(buf.toString());
    JSONObject access = (JSONObject)jsonObject.get("access");
    JSONObject token = (JSONObject)access.get("token");
    String sToken = (String)token.get("id");
    String expires = (String)token.get("expires");
    
    Date expireDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(expires); // data fornita in UTC, va convertita in localtime
   
    SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
   timeFormatter.setTimeZone( TimeZone.getDefault( ) );
    Calendar calendar = Calendar.getInstance();
    
        calendar.setTime(timeFormatter.parse(expires));
    
    
    long expirationTimeStamp = calendar.getTimeInMillis()/1000;
    
    System.out.println("timestamp expiration="+expirationTimeStamp+" - now="+Calendar.getInstance( ).getTimeInMillis()/1000);
    
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
