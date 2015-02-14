package org.stackdroid.utils;

import java.io.Serializable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Flavor  implements Comparable<Flavor>, Serializable {
	
	private static final long serialVersionUID = 2087368867376448461L;

	
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

    @Override
	public String toString( ) {
	  return name;
    }
    
    public String getFullInfo( ) {
    	return name + " (" + disk + "GB, " + vcpus + " CPU, " + ram + "MB RAM)"; 
    }
    
    /**
    *
    *
    *
    *
    */    
   public static Vector<Flavor> parse( String jsonBuf )  throws ParseException {
	Vector<Flavor> flavorTable = new Vector<Flavor>();
	try {
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray flavors = (JSONArray)jsonObject.getJSONArray("flavors");
	    for(int i=0; i<flavors.length(); ++i ) {
		JSONObject flavor = flavors.getJSONObject(i);
		String name = (String)flavor.getString("name");
		int ram = flavor.getInt("ram");
		int cpus = flavor.getInt("vcpus");
		String s_swap = (String)flavor.getString("swap");
		int swap = 0;
		if(s_swap!=null & s_swap.length()!=0)
		    swap = Integer.parseInt( (String)flavor.getString("swap") );
		int ephemeral = flavor.getInt("OS-FLV-EXT-DATA:ephemeral");
		int disk = flavor.getInt("disk");
		String ID = (String)flavor.getString("id");
		Flavor F = new Flavor(name, ID, ram, cpus, swap, ephemeral, disk);
		flavorTable.add( F );
	    }
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
	return flavorTable;
   }
}
