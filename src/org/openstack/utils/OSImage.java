package org.openstack.utils;

import java.io.Serializable;

public class OSImage implements Serializable {

    private String   name    = null;
    private long     size    = 0;
    private String   format  = null;
    private String   status  = null;
    private boolean  is_public = true;
    private long     createdAt = 0;
    private String ID        = null;

    public OSImage( String name,
		  String ID,
		  long size,
		  String format,
		  String status,
		  boolean _pub,
		  long createdAt ) 
    {
  	this.name = name;
	this.ID = ID;
	this.size = size;
  	this.status = status;
  	this.is_public = _pub;
  	this.createdAt = createdAt;
	this.format = format;
    } 
    
    public String getName( ) { return name; }
    public long getSize( ) { return size; }
    public int getSizeMB( ) { return (int)(size/1048576); }
    public String getFormat( ) { return format; }
    public String getStatus( ) { return status; }
    public boolean isPublic( ) { return is_public; }
    public long getCreationDate( ) { return createdAt; }
    public String getID( ) { return ID; }
    
    @Override
    public String toString( ) {
	return "Image{name=" + name
	    + ",ID="+ID
	    + ",size="+size
	    + ",format="+format
	    + ",status="+status
	    + ",is_public="+is_public
	    + ",createdAt="+createdAt+"}";
    }
}
