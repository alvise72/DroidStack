package org.stackdroid.utils;



public class SubNetwork { 
    private String name;
    private String ID;
    private String cidr;
    private String gw;
    private AllocationPool[] allocPools;
    private String[] dns;
    private boolean dhcp;
    
    public SubNetwork( String name, String ID, String cidr, String gatewayIP, AllocationPool[] allocPools, String[] dns, boolean dhcp ) {
    	this.name = name;
    	this.ID   = ID;
    	this.cidr = cidr;
    	this.gw= gatewayIP;
    	this.allocPools = allocPools;
    	this.dns = dns;
    	this.dhcp = dhcp;
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public String getAddress( ) { return cidr; }
    public String getGatewayIP( ) { return gw; }
    public AllocationPool[] getAllocationPools( ) { return allocPools; }
    public String[] getDNS( ) { return dns; }

    @Override
    public String toString() {
	return "Subnet{name=" + name
	    + ",ID="+ID
	    + ",cidr="+cidr
	    + ",gateway="+gw
	    + ",start=" + allocPools[0].getStartIP( )
	    + ",end=" + allocPools[0].getEndIP( ) 
	    + ",dns=" + dns[0]
	    + ",dhcp=" + dhcp
	    +"}";
    }
}
