package org.openstack.utils;

public class Network {
    private String status;
    private String name;
    private String ID;
    private SubNetwork[] subnets;
    private boolean shared;
    private boolean up;
    private boolean ext;
    
    public Network( String status, String name, String ID, SubNetwork[] subnets, boolean shared, boolean up, boolean ext ) {
	this.status = status;
	this.name = name;
	this.ID   = ID;
	this.subnets = subnets;
	this.shared= shared;
	this.up = up;
	this.ext = ext;
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public SubNetwork[] getSubNetworks( ) { return subnets; }
    public boolean isShared( ) { return shared; }
    public boolean isUp( ) { return up; }
    public boolean isExt( ) { return ext; }
}
