
package org.openstack.utils;
    
public class AllocationPool {
	private String start;
	private String end;
	public AllocationPool( String start, String end ) {
	    this.start = start;
	    this.end = end;
	}
	public String getStartIP( ) { return start; }
	public String getEndIP( ) { return end; }
    }
