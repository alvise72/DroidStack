package org.stackdroid.utils;

public class SimpleSecGroupRule {
  private String ID;
  private int fromPort;
  private int toPort;
  private String protocol;
  private String IPRange;

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
  }

  String getID( ) { return ID; }
  String getProtocol( ) { return protocol; }
  int getFromPort( ) { return fromPort; }
  int getToPort( ) { return toPort; }
  String getIPRange( ) { return IPRange; }
}
