package org.openstack.utils;

public class Flavor  implements Comparable<Flavor> {
    private String name = null;
    private String ID = null;
    private int ram = 0;
    private int vcpus = 0;
    private int swap = 0;
    private int ephemeral = 0;
    private int disk = 0;
    
    public Flavor( String _name,
		   String _ID,
		   int _ram,
		   int _vcpus,
		   int _swap,
		   int _ephemeral,
		   int _disk )
    {
	name = _name;
	ID = _ID;
	ram = _ram;
	vcpus = _vcpus;
	swap = _swap;
	ephemeral = _ephemeral;
	disk = _disk;
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public int getRAM( ) { return ram; }
    public int getVCPU( ) { return vcpus; }
    public int getSWAP( ) { return swap; }
    public int getEphemeral( ) { return ephemeral; }
    public int getDISK( ) { return disk; }

    public int compareTo( Flavor f ) {
	if(f.getDISK ( ) > this.getDISK( ) )
	    return -1;
	if(f.getDISK ( ) == this.getDISK( ) )
	    return 0;
	if(f.getDISK ( ) < this.getDISK( ) )
	    return 1;
	return 0;
    }
}
