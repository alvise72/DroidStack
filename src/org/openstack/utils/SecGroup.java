package org.openstack.utils;

public class SecGroup {
    private String name;
    private String id;
    
    public SecGroup(String name, String id) {
	this.name = name;
	this.id    = id;
    }

    public String getName( ) { return name; }
    public String getID( ) { return id; }

    @Override
	public String toString( ) { return "SecGroup{name="+name+",ID="+id+"}"; }
}
