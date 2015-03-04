package org.stackdroid.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class ExtGateway {
	private String ID;
	private String Name;
	private boolean enableSnat = false;
	
	public ExtGateway( String name, String ID, boolean enableSnat ) {
		this.Name = name;
		this.ID = ID;
		this.enableSnat = enableSnat;
	}
	
	public String getName ( ) { return Name; }
	public String getID( ) { return ID; }
	
	public static ExtGateway parse( String jsonBuf ) throws ParseException {
		try {
       		JSONObject jsonObject = new JSONObject( jsonBuf );
       		String ID = jsonObject.getString("id");
       		boolean enableSnat = jsonObject.getBoolean("enable_snat");
       		return new ExtGateway( "NotImplementedYet", ID, enableSnat);
		} catch(org.json.JSONException je) {
	   		throw new ParseException( je.getMessage( ) );
	   	}
	}
}
