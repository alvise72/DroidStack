package org.stackdroid.utils;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class SecGroup {
    private String name;
    private String id;
    private String desc;
    //private Vector<Rule> rules;
    
    public SecGroup(String name, String id, String descr/*, Vector<Rule> rules*/){
    	this.name  = name;
    	this.id    = id;
    	this.desc  = descr;
    	//this.rules = rules;
    }
    
    public String getDescription( ) { return desc; }
    public String getName( ) { return name; }
    public String getID( ) { return id; }
    //public Vector<Rule> getRules( ) { return rules; }
    
    @Override
	public String toString( ) { return name; }

    public static Vector<SecGroup> parse( String jsonBuf ) throws ParseException  {
	//SecGroup secg[] = null;
    	Vector<SecGroup> secg = new Vector<SecGroup>();
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray secgroups = jsonObject.getJSONArray("security_groups");
	    
	    for(int i =0; i<secgroups.length(); ++i) {
		  JSONObject secgrp = secgroups.getJSONObject(i);
		  String id   = secgrp.getString("id");
		  String name = secgrp.getString("name");
		  String desc = secgrp.getString("description");
		  if(desc == null) desc ="";
		  //Vector<Rule> rules = parseRules( secgrp.getJSONArray("security_group_rules"));
		  secg.add( new SecGroup( name, id, desc/*, null*/ ) );
	    }
	} catch(org.json.JSONException je) {
 	    throw new ParseException( je.getMessage( ) );
 	}
	  return secg;
    }

}
