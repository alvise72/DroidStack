package org.stackdroid.utils;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class User implements Serializable, Comparable<User> {
    
    private static final long serialVersionUID = 3000000000000000006L;
    
    protected String  userName;
    protected String  userID;
    protected String  tenantName;
    protected String  tenantId;
    protected String  token;
    protected long    tokenExpireTime;
    protected String  password;
    protected boolean usessl;
    protected boolean role_admin;
    protected boolean hasGlance;
    protected boolean hasNova;
    protected boolean hasNeutron;
    protected boolean hasCinder1;
    protected boolean hasCinder2;
    protected boolean verifyServerCert;
    protected String  identityEndpoint;
    protected String  novaEndpoint;
    protected String  glanceEndpoint;
    protected String  neutronEndpoint;
    protected String  cinder1Endpoint;
    protected String  cinder2Endpoint;
    protected String  identityHostname;
    protected String  identityIP;
    protected String  CAFile;
    protected boolean useV3;
    
    public User( String _userName, 
		 String _userID, 
		 String _tenantName, 
		 String _tenantId, 
		 String _token, 
		 long _tokenExpireTime, 
		 boolean _role_admin,
		 boolean hasGlance,
		 boolean hasNova,
		 boolean hasNeutron,
		 boolean hasCinder1,
		 boolean hasCinder2,
		 String identityEndpoint,
		 String glanceEndpoint,
		 String novaEndpoint,
		 String neutronEndpoint,
		 String cinder1Endpoint,
		 String cinder2Endpoint,
		 String identityHostname,
		 String identityIP,
		 boolean verifyServerCert,
		 String ca_file,
		 boolean useV3)
    {
        userName       	       = _userName;
        userID         	       = _userID;
        tenantName     	       = _tenantName;
        tenantId       	       = _tenantId;
        token          	       = _token;
        tokenExpireTime	       = _tokenExpireTime;
        password       	       = "";
        role_admin     	       = _role_admin;
        this.hasGlance         = hasGlance;
        this.hasNova           = hasNova;
        this.hasNeutron        = hasNeutron;
        this.hasCinder1        = hasCinder1;
        this.hasCinder2        = hasCinder2;
        this.identityEndpoint  = identityEndpoint;
        this.glanceEndpoint    = glanceEndpoint;
        this.novaEndpoint      = novaEndpoint;
        this.neutronEndpoint   = neutronEndpoint;
        this.cinder1Endpoint   = cinder1Endpoint;
        this.cinder2Endpoint   = cinder2Endpoint;
        this.identityHostname  = identityHostname;
        this.identityIP        = identityIP;
	this.verifyServerCert  = verifyServerCert;
	this.CAFile	       = ca_file;
	this.useV3             = useV3;
    }
    
    public String getIdentityHostname( ) { return identityHostname; }
    public String getIdentityIP( ) { return identityIP; }
    public void setPassword( String _password ) { password = _password ;} 
    public void setSSL( boolean _usessl ) { usessl = _usessl; }
    public void setVerifyServerCert( boolean verifyServerCert ) { this.verifyServerCert=verifyServerCert; }
    public void setCAFile( String ca_file) { this.CAFile=ca_file;}
    
    public void setGlanceEndpoint( String ep ) { glanceEndpoint = ep; }
    public void setNeutronEndpoint( String ep ) { neutronEndpoint = ep; }
    
    
    public String getTenantName( ) { return tenantName; }
    public String getTenantID( ) { return tenantId; }
    public String getToken( ) { return token; }
    public long   getTokenExpireTime( ) { return tokenExpireTime; }
    public String getUserName( ) { return userName; }
    public String getUserID( ) { return userID; }
    public String getPassword( ) { return password; }
    public String getCAFile( ) { return CAFile; }
    public boolean useV3( )  { return useV3; }
    public boolean useSSL( ) { return usessl; }
    public boolean isRoleAdmin( ) { return role_admin; }
    
    public boolean hasNova( ) { return hasNova; }
    public boolean hasGlance( ) { return hasGlance; }
    public boolean hasNeutron( ) { return hasNeutron; }
    public boolean hasCinder1( ) { return hasCinder1; }
    public boolean hasCinder2( ) { return hasCinder2; }
    
    public String getIdentityEndpoint( ) { return identityEndpoint; }
    public String getNovaEndpoint( ) { return novaEndpoint; }
    public String getGlanceEndpoint( ) { return glanceEndpoint; }
    public String getNeutronEndpoint( ) { return neutronEndpoint; }
    public String getCinder1Endpoint( ) { return cinder1Endpoint; }
    public String getCinder2Endpoint( ) { return cinder2Endpoint; }
    public boolean getVerifyServerCert( ) { return verifyServerCert; }
    
    public String getFilename( ) {
    	String filename = getUserID( );
    	filename += "."+getTenantID( );
    	filename += "."+identityEndpoint.hashCode();
    	return filename;
    }
    
    /*
     * 
     * 
     * 
     * 
     * 
     */
    @Override
    public String toString( ) {
    	return "User{identityEndpoint="+identityEndpoint+
	    ",novaEndpoint="+novaEndpoint+
	    ",glanceEndpoint="+glanceEndpoint+
	    ",neutronEndpoint="+neutronEndpoint+
	    ",cinder1Endpoint="+cinder1Endpoint+
	    ",cinder2Endpoint="+cinder2Endpoint+
	    ",identityHostname="+identityHostname+
	    ",userName="+userName+
	    ",userID="+userID+
	    ",tenantName="+tenantName+
	    ",tenantId="+tenantId+
	    ",tokenExpireTime="+tokenExpireTime+
	    ",password="+password+
	    ",usessl="+usessl+
	    ",role_admin="+role_admin+
	    ",Verify Server Cert="+verifyServerCert+
	    ",CA="+CAFile+
	    ",use V3 API=" + (useV3 ? "yes" : "no(v2)")+
	    "}";
    }
    
    /*
     * 
     * 
     * 
     * 
     * 
     */
    public int compareTo( User u ) {
    	if(u.getUserID()!=this.getUserID())
	    return 1;
    	return 0;
    }
    
    /*
     * 
     * 
     * 
     * 
     * 
     */
    public static User fromFileID( String ID, String filesDir ) throws IOException, ClassNotFoundException, NotExistingFileException {
    	String filename = filesDir + "/users/" + ID;
    	if(false == (new File(filename)).exists())
	    throw new NotExistingFileException( "File [" + filename + "] doesn't exist" );
    	try {
	    InputStream is = new FileInputStream( filename );
	    ObjectInputStream ois = new ObjectInputStream( is );
	    User U = (User)ois.readObject( );
	    ois.close( );
	    return U;
    	} catch(IOException ioe) {
	    (new File(filename)).delete( );
	    
	    if(ioe.getMessage( ).contains("Incompatible class (SUID")) {
		return null;
	    }
	    
	    throw new IOException( "User.fromFileID.InputStream.readObject: " + ioe.getMessage( ) );
    	} catch(ClassNotFoundException cnfe) {
	    throw new ClassNotFoundException( "User.fromFileID.ObjectInputStream.readObject: " + cnfe.getMessage( ) );
    	}
    }
    
    /*
     * 
     * 
     * 
     * 
     * 
     */
    public void toFile( String filesDir ) throws IOException {
    	String filename = filesDir + "/users/" + getFilename( );// getUserID( ) + "." + getTenantID( ) + "." + endpoint.hashCode();
    	File f = new File( filename );
    	if(f.exists()) f.delete();
    	try {
	    OutputStream os = new FileOutputStream( filename );
	    ObjectOutputStream oos = new ObjectOutputStream( os );
	    oos.writeObject( this );
	    oos.close( );
    	} catch(IOException ioe) {
	    throw new IOException("User.toFile.OutputStream.write/close: "+ioe.getMessage() );
	}
    }
    
    /**
     *
     *
     *
     *
     *
     */
    public static User parse( String jsonString, boolean useV3, String _stoken ) throws ParseException
    {    
	try {
	    JSONObject jsonObject = new JSONObject( jsonString );

	    JSONObject token = useV3 ? (JSONObject)jsonObject.getJSONObject("token") : (JSONObject)jsonObject.getJSONObject("access").getJSONObject("token");
	    
	    String stoken = useV3 ? _stoken : (String)token.get("id") ;
	    String expires = useV3 ? (String)token.get("expires_at") : (String)token.get("expires");
	    
	    JSONObject tenant = useV3 ? (JSONObject)token.getJSONObject("project") : (JSONObject)token.getJSONObject("tenant");
	    
	    String tenantid = (String)tenant.getString("id") ;
	    
	    String tenantname = (String)tenant.getString("name") ;
	    
	    String username = useV3 ? (String)token.getJSONObject("user").getString("name") : (String)((JSONObject)jsonObject.getJSONObject("access").getJSONObject("user")).getString("username") ;
	    String userID = useV3 ? (String)token.getJSONObject("user").getString("id") : (String)((JSONObject)jsonObject.getJSONObject("access").getJSONObject("user")).getString("id") ;
		      
	    JSONArray roleArray = useV3 ? token.getJSONArray("roles") : jsonObject.getJSONObject("access").getJSONObject("user").getJSONArray("roles");
	    JSONArray serviceArray = useV3 ? token.getJSONArray("catalog") : ((JSONObject)jsonObject.getJSONObject("access")).getJSONArray("serviceCatalog");

	    //Log.v("User.parse","jsonString="+jsonString);
	    
	    boolean nova=false, glance=false, neutron=false, cinder1=false, cinder2=false;
	    String novaEP=null, glanceEP=null, neutronEP=null, cinder1EP=null, cinder2EP=null, identityEP = null;
	    for(int i = 0; i<serviceArray.length();++i) {
		
		JSONObject service = serviceArray.getJSONObject(i);
		JSONArray endpoints = service.getJSONArray("endpoints");
		String type = service.getString("type");
		JSONObject endpoint = null;
		if(!useV3)
		    endpoint = endpoints.getJSONObject(0);
		if(type.compareTo("compute")==0) {
		    nova=true;
		    //novaEP = endpoint.getString("publicURL");
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		novaEP = thisEndpoint.getString("url");
		    }
		    }
		    else novaEP = endpoint.getString("publicURL");
		}
		if(type.compareTo("network")==0) {
		    neutron=true;
		    //neutronEP = endpoint.getString("publicURL");
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		neutronEP = thisEndpoint.getString("url");
		    }
		    }
		    else neutronEP = endpoint.getString("publicURL");
		}
		if(type.compareTo("volumev2")==0) {
		    cinder2=true;
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		cinder2EP = thisEndpoint.getString("url");
		    }
		    }
		    else cinder2EP = endpoint.getString("publicURL");
		    //cinder2EP = endpoint.getString("publicURL");
		}
		if(type.compareTo("volume")==0) {
		    cinder1=true;
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		cinder1EP = thisEndpoint.getString("url");
		    }
		    }
		    else cinder1EP = endpoint.getString("publicURL");
		    //cinder1EP = endpoint.getString("publicURL");
		    
		}
		if(type.compareTo("image")==0) {
		    glance=true;
		    //glanceEP = endpoint.getString("publicURL");
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		glanceEP = thisEndpoint.getString("url");
		    }
		    }
		    else glanceEP = endpoint.getString("publicURL");
		}
		if(type.compareTo("identity") == 0) {
		    //identityEP = endpoint.getString("publicURL");
		    if(useV3) {
		    for(int j = 0; j<endpoints.length(); j++) {
		    	JSONObject thisEndpoint = endpoints.getJSONObject(j);
		    	if(thisEndpoint.getString("interface").compareTo("public")==0)
		    		identityEP = thisEndpoint.getString("url");
		    }
		    }
		    else identityEP = endpoint.getString("publicURL");
		}
	    }
	    
	    /**
	     If user requested to first connect to NON-V3 API, but the service returns a V3 API for keystone
	     must force useV3 for current user anyway
	     */
	    boolean forceUseV3 = useV3;
	    Log.v("User.parse", "identityEP="+identityEP);
	    if(identityEP.contains("/v3"))
	      forceUseV3 = true;
	    else
	      forceUseV3 = false;
	    boolean role_admin = false;
	    for(int i = 0; i<roleArray.length(); ++i)
		if(roleArray.getJSONObject(i).getString("name").compareTo("admin")==0)
		    role_admin = true;
	    
	    SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    timeFormatter.setTimeZone( TimeZone.getDefault( ) );
	    Calendar calendar = Calendar.getInstance();
	    try {
		calendar.setTime(timeFormatter.parse(expires));
	    } catch(java.text.ParseException pe) {
		throw new ParseException( "Error parsing the expiration date ["+expires+"]" );
	    }
	    long expireTimestamp = calendar.getTimeInMillis() / 1000;
	    String addrS = "";
	    String addrIP = "";
	    try {
		URL identityUrl = new URL(identityEP);
		InetAddress addr = InetAddress.getByName(identityUrl.getHost());
		addrS = addr.getCanonicalHostName();//.getHostName();
		addrIP = addr.getHostAddress();
	    } catch(Exception e) {
		addrS = identityEP;
	    }
	    
	    User U = new User( 
			      username, 
			      userID, 
			      tenantname, 
			      tenantid, 
			      stoken, 
			      expireTimestamp, 
			      role_admin,
			      glance,
			      nova,
			      neutron,
			      cinder1,
			      cinder2,
			      identityEP,
			      glanceEP,
			      novaEP,
			      neutronEP,
			      cinder1EP,
			      cinder2EP,
			      addrS,
			      addrIP,
			      false,
			      null,
			      forceUseV3);
	    //Log.v("User.parse", "USER=["+U);
	    return U;
	} catch(org.json.JSONException je) {
	    throw new ParseException( "User.parse - JSONException: "+je.getMessage( ) );
	}
    }

    /**
     *
     *
     *
     *
     *
     */
    // public static User parseV3( JSONObject jsonObject ) throws ParseException
    // {
    // 	JSONObject access = (JSONObject)jsonObject.get("tokens");
    // }	
}
