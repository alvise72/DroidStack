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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 3000000000000000004L;

    private String  userName;
    private String  userID;
    private String  tenantName;
    private String  tenantId;
    private String  token;
    private long    tokenExpireTime;
    private String  password;
    private boolean usessl;
    private boolean role_admin;
    
    private boolean hasGlance;
    private boolean hasNova;
    private boolean hasNeutron;
    private boolean hasCinder1;
    private boolean hasCinder2;
    
    private String identityEndpoint;
    //private String identityEndpointIP;
    
    private String novaEndpoint;
    private String glanceEndpoint;
    private String neutronEndpoint;
    private String cinder1Endpoint;
    private String cinder2Endpoint;
    private String identityHostname;
	private File   CAFile;
	private boolean insecure;
    //private URL    identityUrl;
    
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
    			 //String identityEndpointIP,
    			 String glanceEndpoint,
    			 String novaEndpoint,
    			 String neutronEndpoint,
    			 String cinder1Endpoint,
    			 String cinder2Endpoint,
    			 String identityHostname,
				 File CAFile,
				 boolean insecure)
    {
        userName       			 = _userName;
        userID         			 = _userID;
        tenantName     			 = _tenantName;
        tenantId       			 = _tenantId;
        token          			 = _token;
        tokenExpireTime			 = _tokenExpireTime;
        password       			 = "";
        role_admin     			 = _role_admin;
        this.hasGlance  	 	 = hasGlance;
        this.hasNova    		 = hasNova;
        this.hasNeutron 		 = hasNeutron;
        this.hasCinder1 		 = hasCinder1;
        this.hasCinder2 		 = hasCinder2;
        this.identityEndpoint 	 = identityEndpoint;
        //this.identityEndpointIP  = identityEndpointIP;
        this.glanceEndpoint 	 = glanceEndpoint;
        this.novaEndpoint   	 = novaEndpoint;
        this.neutronEndpoint	 = neutronEndpoint;
        this.cinder1Endpoint	 = cinder1Endpoint;
        this.cinder2Endpoint	 = cinder2Endpoint;
        this.identityHostname    = identityHostname;
		this.CAFile				 = CAFile;
		this.insecure            = insecure;
    }
    
    public String getIdentityHostname( ) { 
			return identityHostname;
    }
    
    public void setPassword( String _password ) { password = _password ;} 
    public void setSSL( boolean _usessl ) { usessl = _usessl; }
	public void setCAFile( File cafile ) { this.CAFile = cafile; }
	public void setInsecure( boolean insecure ) { this.insecure=insecure; }
    
    public String getTenantName( ) { return tenantName; }
    public String getTenantID( ) { return tenantId; }
    public String getToken( ) { return token; }
    public long   getTokenExpireTime( ) { return tokenExpireTime; }
    public String getUserName( ) { return userName; }
    public String getUserID( ) { return userID; }
    public String getPassword( ) { return password; }
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
	public File   getCAFile( ) { return CAFile; }
	public boolean getInsecure( ) { return insecure; }
    
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
    	//",identityEndpointIP="+identityEndpointIP+
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
				",insecure="+insecure+
				",CAFile="+CAFile+
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

	/*
     *
     *
     *
     *
     *
     */
    public static User parse( String jsonString ) throws ParseException
    {
      try {
    	  JSONObject jsonObject = null;
    	  jsonObject = new JSONObject( jsonString );
	  
    	  JSONObject access = (JSONObject)jsonObject.get("access");
    	  JSONObject token = (JSONObject)access.get("token");
    	  String stoken = (String)token.get("id");
    	  String expires = (String)token.get("expires");
    	  JSONObject tenant = (JSONObject)token.get("tenant");
    	  String tenantid = (String)tenant.get("id");
    	  String tenantname = (String)tenant.get("name");
    	  String username = (String)((JSONObject)access.get("user")).get("username");
    	  String userID = (String)((JSONObject)access.get("user")).get("id");
    	  JSONArray roleArray = access.getJSONObject("user").getJSONArray("roles");
    	  JSONArray serviceArray = access.getJSONArray("serviceCatalog");

    	  boolean nova=false, glance=false, neutron=false, cinder1=false, cinder2=false;
    	  String novaEP=null, glanceEP=null, neutronEP=null, cinder1EP=null, cinder2EP=null, identityEP = null;
    	  for(int i = 0; i<serviceArray.length();++i) {

    		  JSONObject service = serviceArray.getJSONObject(i);
    		  JSONArray endpoints = service.getJSONArray("endpoints");
    		  String type = service.getString("type");
    		  JSONObject endpoint = endpoints.getJSONObject(0);
    			  if(type.compareTo("compute")==0) {
    				  nova=true;
    				  novaEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("network")==0) {
    				  neutron=true;
    				  neutronEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("volumev2")==0) {
    				  cinder2=true;
    				  //Log.d("ParseUtils", "VOLUME2 - PublicURL="+endpoint.getString("publicURL"));
    				  cinder2EP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("volume")==0) {
    				  cinder1=true;
    				  //Log.d("ParseUtils", "VOLUME - PublicURL="+endpoint.getString("publicURL"));
    				  cinder1EP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("image")==0) {
    				  glance=true;
    				  glanceEP = endpoint.getString("publicURL");
    			  }
    			  if(type.compareTo("identity") == 0) {
    				  identityEP = endpoint.getString("publicURL");
    			  }
    		  
    	  }
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
    	  try {
    		  URL identityUrl = new URL(identityEP);
    		  InetAddress addr = InetAddress.getByName(identityUrl.getHost());
    		  addrS = addr.getCanonicalHostName();//.getHostName();
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
    			  			 //identityIP,
    			  			 glanceEP,
    			  			 novaEP,
    			  			 neutronEP,
    			  			 cinder1EP,
    			  			 cinder2EP,
    			  			 addrS,
				  			 null,
				             true);
    	  return U;
      } catch(org.json.JSONException je) {
    	  throw new ParseException( je.getMessage( ) );
      }
    }

/*	public String getIdentityEndpointIP() {
		// TODO Auto-generated method stub
		return identityEndpointIP;
	}
*/
}
