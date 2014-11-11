package org.stackdroid.utils;

import java.io.Serializable;

public class Port implements Serializable {

	private static final long serialVersionUID = 8087367767376441461L;
	
	private String id;
	
	private String fixedIP;
	
	public Port( String id, String fixedIP ) {
		this.id = id;
		this.fixedIP = fixedIP;
	}
	
	public String getFixedIP( ) { return fixedIP; }
	public String getID( ) { return id ; }
}