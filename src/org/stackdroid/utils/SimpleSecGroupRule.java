package org.stackdroid.utils;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class SimpleSecGroupRule {
  private String ID;
  private int fromPort;
  private int toPort;
  private String protocol;
  private String IPRange;
  private Hashtable<Integer, String> PROTO = new Hashtable<Integer, String>();
  
  public SimpleSecGroupRule( String ID, int fromPort, int toPort, String protocol, String IPRange ) {
    this.ID = ID;
    this.fromPort = fromPort;
    this.toPort = toPort;
    this.protocol = protocol;
    this.IPRange = IPRange;
    
    PROTO.put(22, "SSH");
    PROTO.put(80, "HTTP");
    PROTO.put(443, "HTTPS");
    PROTO.put(8080, "HTTP");
    PROTO.put(8443, "HTTPS");
    PROTO.put(21, "FTP");
  }

  public String getID( ) { return ID; }
  public String getProtocol( ) { return protocol; }
  public int getFromPort( ) { return fromPort; }
  public int getToPort( ) { return toPort; }
  public String getIPRange( ) { return IPRange; }
  
  public String getProtoName( ) {
	  if(fromPort != toPort) return "";
	  return PROTO.get( new Integer(fromPort) );
  }
  
  @Override
  public String toString( ) {
	  return ID;
  }
  
  public String to_string( ) {
	  return "SimpleSecGroupRule{ID="+ID + ", FromPort="+fromPort+", ToPort="+toPort+", Protocol="+protocol+", IP Range="+IPRange+"}";
  }
  
  public static Vector<SimpleSecGroupRule> parse( String jsonBuf ) throws ParseException  {
	Vector<SimpleSecGroupRule> rulesV = new Vector<SimpleSecGroupRule>();
	//Log.d("PARSE", "jsonBuf="+jsonBuf);
	try{
	    JSONObject jsonObject = new JSONObject( jsonBuf );
	    JSONArray rules = jsonObject.getJSONObject("security_group").getJSONArray("rules");
	    
	    for(int i =0; i<rules.length(); ++i) {
		  JSONObject rule = rules.getJSONObject(i);
		  String id = rule.getString("id");
		  String iprange = "";
		  if(rule.getJSONObject("ip_range").has("cidr"))
			  iprange = rule.getJSONObject("ip_range").getString("cidr");
		  String proto = rule.getString("ip_protocol");
		  int fromport = rule.getInt("from_port");
		  int toport = rule.getInt("to_port");
		  rulesV.add(new SimpleSecGroupRule(id, fromport, toport, proto, iprange) );
		  //Log.d("PARSE", "Rule="+id);
	    }
	} catch(org.json.JSONException je) {
	    throw new ParseException( je.getMessage( ) );
	}
	  return rulesV;
  }
}
