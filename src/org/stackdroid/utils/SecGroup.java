package org.stackdroid.utils;

import java.util.Vector;

//import java.util.Serializable;

public class SecGroup {//implements Serializable {
    private String name;
    private String id;
    private String desc;
    private Vector<Rule> rules;
    
    public SecGroup(String name, String id, String descr, Vector<Rule> rules) {
    	this.name  = name;
    	this.id    = id;
    	this.desc  = descr;
    	this.rules = rules;
    }
    
    public String getDescription( ) { return desc; }
    public String getName( ) { return name; }
    public String getID( ) { return id; }
    public Vector<Rule> getRules( ) { return rules; }
    
    @Override
	public String toString( ) { return name; }

}
