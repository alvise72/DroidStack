
package org.stackdroid.utils;
    
public class IPAllocationPool {
	private String start;
	private String end;
	public IPAllocationPool( String start, String end ) {
	    this.start = start;
	    this.end = end;
	}
	public String getStartIP( ) { return start; }
	public String getEndIP( ) { return end; }
}
