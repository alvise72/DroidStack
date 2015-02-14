package org.stackdroid.utils;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

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
    
    /**
    *
    *
    *
    *
    */    
   public static Vector<KeyPair> parse( String jsonBuf ) throws ParseException  {
	Vector<KeyPair> kpairs = new Vector<KeyPair>();
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray keypairs = (JSONArray)jsonObject.getJSONArray("keypairs");
	    //kpairs = new KeyPair[keypairs.length()];
	    for(int i =0; i<keypairs.length(); ++i) {
		JSONObject keypair = keypairs.getJSONObject(i).getJSONObject("keypair");
		String key  = keypair.getString("public_key");
		String fp   = keypair.getString("fingerprint");
		String name = keypair.getString("name");
		kpairs.add( new KeyPair( name, key, fp ) );
	    }
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
	return kpairs;
   }

}
