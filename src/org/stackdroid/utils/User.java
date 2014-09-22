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

public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 2087368867376448462L;

    private String  endpoint;
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
    
    private String novaEndpoint;
    private String glanceEndpoint;
    private String neutronEndpoint;
    private String cinder1Endpoint;
    private String cinder2Endpoint;
    
    
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
    			 String glanceEndpoint,
    			 String novaEndpoint,
    			 String neutronEndpoint,
    			 String cinder1Endpoint,
    			 String cinder2Endpoint) 
    {
        userName        = _userName;
        userID          = _userID;
        tenantName      = _tenantName;
        tenantId        = _tenantId;
        token           = _token;
        tokenExpireTime = _tokenExpireTime;
        password        = "";
        role_admin      = _role_admin;
        this.hasGlance  = hasGlance;
        this.hasNova    = hasNova;
        this.hasNeutron = hasNeutron;
        this.hasCinder1 = hasCinder1;
        this.hasCinder2 = hasCinder2;
        this.glanceEndpoint = glanceEndpoint;
        this.novaEndpoint   = novaEndpoint;
        this.neutronEndpoint= neutronEndpoint;
        this.cinder1Endpoint = cinder1Endpoint;
        this.cinder2Endpoint = cinder2Endpoint;
    }
    
    public void setPassword( String _password ) { password = _password ;} 
    public void setEndpoint( String ep ) { endpoint = ep; }
    public void setSSL( boolean _usessl ) { usessl = _usessl; }
    
    public String getEndpoint( ) { return endpoint; }
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
     
    public String getNovaEndpoint( ) { return novaEndpoint; }
    public String getGlanceEndpoint( ) { return glanceEndpoint; }
    public String getNeutronEndpoint( ) { return neutronEndpoint; }
    public String getCinder1Endpoint( ) { return cinder1Endpoint; }
    public String getCinder2Endpoint( ) { return cinder2Endpoint; }
    
    /*
     * 
     * 
     * 
     * 
     * 
     */
    @Override
    public String toString( ) {
    	return "User{endpoint="+endpoint+
	    ",userName="+userName+
	    ",userID="+userID+
	    ",tenantName="+tenantName+
	    ",tenantId="+tenantId+
	    ",tokenExpireTime="+tokenExpireTime+
	    ",password="+password+
	    ",usessl="+usessl+
	    ",role_admin="+role_admin+
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
    public static User fromFileID( String ID, String filesDir ) throws IOException, ClassNotFoundException {
    	String filename = filesDir + "/users/" + ID;
    	if(false == (new File(filename)).exists())
    		throw new RuntimeException( "File ["+filename+"] doesn't exist" );
    	try {
    		InputStream is = new FileInputStream( filename );
    		ObjectInputStream ois = new ObjectInputStream( is );
    		User U = (User)ois.readObject( );
    		ois.close( );
    		//U.setContext( ctx );
    		return U;
    	} catch(IOException ioe) {
    		(new File(filename)).delete();
    		throw new IOException( "User.fromFileID.InputStream.read/close: " + ioe.getMessage( ) );
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
    	String filename = filesDir + "/users/" + getUserID( ) + "." + getTenantID( );
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
}
