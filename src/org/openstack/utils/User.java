package org.openstack.utils;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

//import android.content.Context;

//import android.util.Log;

public class User implements Serializable {

    private static final long serialVersionUID = 2087368867376448459L;



    private String endpoint;
    private String userName;
    private String tenantName;
    private String tenantId;
    private String token;
    private long   tokenExpireTime;
    private String password;
    
    private ObjectOutputStream aOutputStream = null;
    private ObjectInputStream aInputStream = null;
    private ByteArrayOutputStream bos = null;
    private ByteArrayInputStream bin = null;
    

    public User( String _userName, String _tenantName, String _tenantId, String _token, long _tokenExpireTime ) {
        //endpoint	= _endpoint;
	userName        = _userName;
	tenantName      = _tenantName;
	tenantId        = _tenantId;
	token           = _token;
	tokenExpireTime = _tokenExpireTime;
	password        = "";
    }
    
    public void setPassword( String _password ) { password = _password ;} 
    public void setEndpoint( String ep ) { endpoint = ep; }
    
    public String getEndpoint( ) { return endpoint; }
    public String getTenantName( ) { return tenantName; }
    public String getTenantID( ) { return tenantId; }
    public String getToken( ) { return token; }
    public long   getTokenExpireTime( ) { return tokenExpireTime; }
    public String getUserName( ) { return userName; }
    public String getPassword( ) { return password; }

    private void writeObject( ) throws IOException {
	aOutputStream.defaultWriteObject();
    }
    
    private void readObject( ) throws IOException {
      try {aInputStream.defaultReadObject();}
      catch(java.lang.ClassNotFoundException e) { throw new IOException(e.getMessage( )); }
    }
    
    public byte[] serialize(  ) {

	try {
	    //FileOutputStream fos = new FileOutputStream(fileName);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(bos);
	    oos.writeObject(this);
	    bos.close();
	    return bos.toByteArray();
	} catch(FileNotFoundException fnfe) {}
	catch(IOException ioe) {}
	return null;
    }

    public static User deserialize( byte[] source ) {
	try {
	    //FileInputStream fis = new FileInputStream(fileName);
	    ByteArrayInputStream bis = new ByteArrayInputStream( source );
	    ObjectInputStream ois = new ObjectInputStream(bis);
	    User obj = (User)ois.readObject();
	    ois.close();
	    return obj;
	} catch(FileNotFoundException fnfe) {}
	catch(IOException ioe) {}
	catch(ClassNotFoundException cnfe) {}
	return null;
    }

    @Override
    public String toString( ) {
	return "User{endpoint="+endpoint+",userName="+userName+",tenantName="+tenantName+",tenantId="+tenantId+",tokenExpireTime="+tokenExpireTime+",password="+password+"}";
    }
}
