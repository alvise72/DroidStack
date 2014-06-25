package org.openstack;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class User implements Serializable {
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
    

    User( String _userName, String _tenantName, String _tenantId, String _token, long _tokenExpireTime ) {
	userName        = _userName;
	tenantName      = _tenantName;
	tenantId        = _tenantId;
	token           = _token;
	tokenExpireTime = _tokenExpireTime;
	//password        = _password;
 	bos = new ByteArrayOutputStream(10240);
 	bin = new ByteArrayInputStream(new byte[10240]);
 	try {
	  aOutputStream = new ObjectOutputStream( bos );
 	  aInputStream = new ObjectInputStream( bin );
	} catch(IOException ioe) {
	  // TODO
	}
    }
    
    public void setPassword( String _password ) { password = _password ;} 

    public String getTenantName( ) { return tenantName; }
    public String getTenantID( ) { return tenantId; }
    public String getToken( ) { return token; }
    public long   getTokenExpireTime( ) { return tokenExpireTime; }
    public String getUserName( ) { return userName; }
    public String getPassword( ) { return password; }

//     public void prepareForStream( ) {
//       
//     }

    private void writeObject( ) throws IOException {
      aOutputStream.defaultWriteObject();
      
    }
    
    private void readObject( ) throws IOException {
      try {aInputStream.defaultReadObject();}
      catch(java.lang.ClassNotFoundException e) { throw new IOException(e.getMessage( )); }
    }
    
    public byte[] serialize( ) {
      try {writeObject( );} catch(IOException ioe) { return null; }
      return bos.toByteArray( );
    }
    
    public String toString( ) {
      return userName+"|"+tenantName+"|"+tenantId+"|"+tokenExpireTime+"|"+password;
    }
}
