package org.stackdroid.utils;

public class KeyPair {
    private String name;
    private String fingerprint;
    private String key;

    public KeyPair( String name, String key, String fingerprint ) {
	this.name = name;
	this.key = key;
	this.fingerprint = fingerprint;
    }

    public String getName( ) { return name; }
    public String getKey( ) { return key; }
    public String getFingerPrint( ) { return fingerprint; }
    
    @Override
	public String toString() {
	  return name;
    }
}
