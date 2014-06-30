package org.openstack.utils;

import java.io.Serializable;

import android.os.Environment;

public class User implements Serializable {

    private static final long serialVersionUID = 2087368867376448460L;

    private String endpoint;
    private String userName;
    private String tenantName;
    private String tenantId;
    private String token;
    private long   tokenExpireTime;
    private String password;
    private boolean usessl;
    
    public User( String _userName, String _tenantName, String _tenantId, String _token, long _tokenExpireTime /*, boolean _usessl*/ ) {
        userName        = _userName;
	tenantName      = _tenantName;
	tenantId        = _tenantId;
	token           = _token;
	tokenExpireTime = _tokenExpireTime;
	password        = "";
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
    public String getPassword( ) { return password; }
    public boolean useSSL( ) { return usessl; }

    @Override
    public String toString( ) {
	return "User{endpoint="+endpoint+
	    ",userName="+userName+
	    ",tenantName="+tenantName+
	    ",tenantId="+tenantId+
	    ",tokenExpireTime="+tokenExpireTime+
	    ",password="+password+
	    ",usessl"+usessl+
	    "}";
    }
}
