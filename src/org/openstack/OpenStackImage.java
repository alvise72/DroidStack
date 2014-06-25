package org.openstack;

import java.lang.String;

public class OpenStackImage {

  private String name    = null;
  private long   size    = 0;
  private String format  = null;
  private String status  = null;
  private boolean Public = true;
  private long createdAt = 0;
  
  public OpenStackImage( String name,
  			      long size,
  			      String format,
  			      String status,
  			      boolean _pub,
  			      long createdAt ) 
  {
  	this.name = name;
  	this.size = size;
  	this.status = status;
  	this.Public = _pub;
  	this.createdAt = createdAt;		
  } 
  
  public String getName( ) { return name; }
  public long getSize( ) { return size; }
  public String getFormat( ) { return format; }
  public String getStatus( ) { return status; }
  public boolean isPublic( ) { return Public; }
  public long getCreationDate( ) { return createdAt; }
  
    // public static void main(String[] args) {
    // 	String a= "a";
    // 	OpenStackImage img = new OpenStackImage(a);
    // }
}
