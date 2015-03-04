package org.stackdroid.utils;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Router {
    private String name;
    private String ID;
    private ExtGateway gw;
    private String tenantID;
    
    public Router( String name, String ID, ExtGateway gw, String tenantID ) {
	  this.name = name;
	  this.ID   = ID;
	  this.gw   = gw;
	  this.tenantID = tenantID;
    }

    @Override
    public String toString( ) {
	  return name;
    }

    public String getName( ) { return name; }
    public String getID( ) { return ID; }
    public String getTenantID( ) { return tenantID; }
    public ExtGateway getExternalGateway( ) { return gw; }
    
    public static Vector<Router> parse ( String jsonBuf ) throws ParseException {
    	Vector<Router> VR = new Vector<Router>( );
    
    	try {
       		JSONObject jsonObject = new JSONObject( jsonBuf );
       		JSONArray routers = (JSONArray)jsonObject.getJSONArray("routers");
       		for(int i =0; i<routers.length(); ++i) {
       			JSONObject routerObj = routers.getJSONObject(i);
       			String name = routerObj.has("name") ? routerObj.getString("name") : "N/A";
       			String ID = routerObj.getString("id");
       			String tenantID = routerObj.getString("tenant_id");
       			JSONObject gwObj = routerObj.getJSONObject("external_gateway_info");
       			ExtGateway gw = null;
       			if(gwObj!=null) {
       				gw = ExtGateway.parse( gwObj.toString() );
       			}
       			VR.add(new Router(name, ID, gw, tenantID ));
       		}
       		return VR;
        } catch(org.json.JSONException je) {
	   		throw new ParseException( je.getMessage( ) );
	   	}
    }
}
