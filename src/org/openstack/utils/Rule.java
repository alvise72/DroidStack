package org.openstack.utils;

public class Rule {
	private int direction;
	private String name;
	private String ID;
	private int protocol;
	private String tenantID;
	private int port_min;
	private int port_max;
	private String secgrpID;
	
	public final static int EGRESS = 0;
	public final static int INGRESS = 1;
	public final static int TCP = 2;
	public final static int UDP = 3;
	public final static int ICMP = 4;
	
	public Rule( int dir, 
				 String name,
				 String ID,
				 int proto,
				 String tenantID,
				 int port_min,
				 int port_max,
				 String secgrpID ) 
				 {
					this.direction = dir;
					this.name      = name;
					this.ID        = ID;
					this.protocol  = proto;
					this.tenantID  = tenantID;
					this.port_min  = port_min;
					this.port_max  = port_max;
					this.secgrpID  = secgrpID;
				 }
	
	public String getName( ) { return name; }
	public String getID( ) { return ID; }
	public String getTenantID( ) { return tenantID; }
	public String getSecGroupID( ) { return secgrpID; }
	public int    getDirection( ) { return direction; }
	public int    getProtocolo( ) { return protocol; }
	public int    getPortMin( ) { return port_min; }
	public int    getPortMax( ) { return port_max; }
	
	public void   setID( String id) { ID = id; }
	public void   setTenantID( String tenantid ) { tenantID = tenantid; }
	public void   setSecGroupID( String secgrpid ) { secgrpID = secgrpid; }
	
	public static Rule SSH_I = new Rule( Rule.INGRESS, "SSH_I", "", Rule.TCP, "", 22, 22, "");
	public static Rule SSH_E = new Rule( Rule.EGRESS, "SSH_E", "", Rule.TCP, "", 22, 22, "");
	public static Rule PING_I = new Rule( Rule.INGRESS, "PING_I", "", Rule.ICMP, "", 0, 0, "");
	public static Rule PING_E = new Rule(Rule.EGRESS, "PING_E", "", Rule.ICMP, "", 0, 0, "");
	public static Rule HTTP_E = new Rule(Rule.EGRESS, "HTTP_E", "", Rule.TCP, "", 80, 80, "");
	public static Rule HTTP_I = new Rule(Rule.INGRESS, "HTTP_I", "", Rule.TCP, "", 80, 80, "");
	public static Rule HTTPS_E = new Rule(Rule.EGRESS, "HTTPS_E", "", Rule.TCP, "", 443, 443, "");
	public static Rule HTTPS_I = new Rule(Rule.INGRESS, "HTTPS_I", "", Rule.TCP, "", 443, 443, "");
	
	
}
