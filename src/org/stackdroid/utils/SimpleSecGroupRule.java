package org.stackdroid.utils;

import java.util.Hashtable;

public class SimpleSecGroupRule {
  private String ID;
  private int fromPort;
  private int toPort;
  private String protocol;
  private String IPRange;
  private Hashtable<Integer, String> PROTO = new Hashtable<Integer, String>();

/*  private final static int TCP = 0;
  private final static int UDP = 1;
  private final static int ICMP = 2;
*/
  
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
}
