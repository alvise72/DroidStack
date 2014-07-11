package org.openstack.utils;

public class Network {
    private String status;
    private String name;
    private String ID;
    private SubNetwork[] subnets;
    private boolean shared;
    private boolean up;
    private boolean ext;
    private String tenantID;
    
    public Network( String status, String name, String ID, SubNetwork[] subnets, boolean shared, boolean up, boolean ext, String tenantID ) {
	this.status = status;
	this.name = name;
	this.ID   = ID;
	this.subnets = subnets;
	this.shared= shared;
	this.up = up;
	this.ext = ext;
	this.tenantID = tenantID;
    }

    @Override
    public String toString( ) {
	return "Network{name=" + name
	    + ",status=" + status
	    + ",shared="+shared
	    + ",up="+up
	    + ",external="+ext
	    + ",Subnet="+(subnets[0]!=null ? subnets[0].toString() : "N/A")
	    + "}";
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public SubNetwork[] getSubNetworks( ) { return subnets; }
    public boolean isShared( ) { return shared; }
    public boolean isUp( ) { return up; }
    public boolean isExt( ) { return ext; }
    public String getTenantID() { return tenantID; }
}
