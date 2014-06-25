package org.openstack;

import java.io.Serializable;

public class Tenant implements Serializable {
    private String userName;
    private String tenantName;
    private String tenantId;
    private String token;
    private long   tokenExpireTime;
    private ObjectOutputStream aOutputStream = null;
    private ByteArrayOutputStream bos = null;

    Tenant( String _username, String _tenantName, String _tenantId, String _token, long _tokenExpireTime ) {
	userName        = _userName;
	tenantName      = _tenantName;
	tenantId        = _tenantId;
	token           = _token;
	tokenExpireTime = _tokenExpireTime;
	bos = new ByteArrayOutputStream();
	aOutputStream = new ObjectOutputStream( bos );
    }

    public String getTenantName( ) { return tenantName; }
    public String getTenantID( ) { return tenantId; }
    public String getToken( ) { return token; }
    public long   getTokenExpireTime( ) { return tokenExpireTime; }
    public String getUserName( ) { return userName; }

    private void writeObject( ObjectOutputStream aOutputStream ) throws IOException {
      aOutputStream.defaultWriteObject();
    }
}
