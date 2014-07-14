package org.openstack.utils;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import android.os.Environment;

public class User implements Serializable, Comparable<User> {

    private static final long serialVersionUID = 2087368867376448461L;

    private String endpoint;
    private String userName;
    private String userID;
    private String tenantName;
    private String tenantId;
    private String token;
    private long   tokenExpireTime;
    private String password;
    private boolean usessl;
    private boolean role_admin;
    
    public User( String _userName, String _userID, String _tenantName, String _tenantId, String _token, long _tokenExpireTime, boolean roleadmin  ) {
        userName        = _userName;
	userID          = _userID;
	tenantName      = _tenantName;
	tenantId        = _tenantId;
	token           = _token;
	tokenExpireTime = _tokenExpireTime;
	password        = "";
	role_admin      = roleadmin;
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

    @Override
    public String toString( ) {
	return "User{endpoint="+endpoint+
	    ",userName="+userName+
	    ",userID="+userID+
	    ",tenantName="+tenantName+
	    ",tenantId="+tenantId+
	    ",tokenExpireTime="+tokenExpireTime+
	    ",password="+password+
	    ",usessl"+usessl+
	    ",role_admin"+role_admin+
	    "}";
    }

    public int compareTo( User u ) {
	if(u.getUserID()!=this.getUserID())
	    return 1;
	return 0;
    }

    public static User fromFileID( String ID ) throws RuntimeException {
	String filename = Environment.getExternalStorageDirectory() + "/DroidStack/users/" + ID;
	if(false == (new File(filename)).exists())
	    throw new RuntimeException( "File ["+filename+"] doesn't exist" );
	try {
	    InputStream is = new FileInputStream( filename );
	    ObjectInputStream ois = new ObjectInputStream( is );
	    User U = (User)ois.readObject( );
	    ois.close( );
	    return U;
	} catch(IOException ioe) {
	    throw new RuntimeException( "InputStream.read/close: " + ioe.getMessage( ) );
	} catch(ClassNotFoundException cnfe) {
	    throw new RuntimeException( "ObjectInputStream.readObject: " + cnfe.getMessage( ) );
	}
    }

    public void toFile( ) throws RuntimeException {
    	String filename = Environment.getExternalStorageDirectory() + "/DroidStack/users/" + getUserID( ) + "." + getTenantID( );
    	File f = new File( filename );
    	if(f.exists()) f.delete();
	try {
	    OutputStream os = new FileOutputStream( filename );
	    ObjectOutputStream oos = new ObjectOutputStream( os );
	    oos.writeObject( this );
	    oos.close( );
	} catch(IOException ioe) {
	    throw new RuntimeException("OutputStream.write/close: "+ioe.getMessage() );
	}
    }
}
