package org.stackdroid.utils;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Volume {//implements Serializable {
	
	//private static final long serialVersionUID = 2087368867376448461L;
	
	public enum Status {
		CREATING,
		ATTACHING,
		DELETING,
		ERROR,
		ERROR_DELETING,
		BACKING_UP,
		RESTORING_BACKUP,
		ERROR_RESTORING,
		ERROR_EXTENDING,
		INUSE,
		AVAILABLE,
		NA
    }
	
    
    private String name;
    private String ID;
    private Status status;
    private boolean bootable;
    private boolean readonly;
    private int gigabyte;
    
    private String attachedto_serverid;
    private String attachedto_servername;
    private String attachedto_device;
    private static Hashtable<Status, String> statusString;
    private static Hashtable<String, Status> stringStatus;
    
    public Volume( String _name,
    			   String _ID,
    			   boolean _bootable,
    			   boolean _readonly,
    			   Status status,
    			   int _gigabyte,
    			   String _servid,
    			   String _servname,
    			   String _device) 
    {
    	statusString = new Hashtable<Status,String>();
    	stringStatus = new Hashtable<String,Status>();
        statusString.put( Status.CREATING, "creating");
        statusString.put( Status.ATTACHING,"attaching");
        statusString.put( Status.DELETING,"deleting");
        statusString.put( Status.ERROR,"error");
        statusString.put( Status.ERROR_DELETING,"error_deleting");
        statusString.put( Status.BACKING_UP,"backing-up");
		statusString.put( Status.RESTORING_BACKUP,"restoring-backup");
		statusString.put( Status.ERROR_RESTORING,"error_restoring");
		statusString.put( Status.ERROR_EXTENDING,"error_extending");
		statusString.put( Status.INUSE,"in-use");
		statusString.put( Status.AVAILABLE,"available"); 
		statusString.put( Status.NA,"N/A");
  	  	
		stringStatus.put( "creating", Status.CREATING);
		stringStatus.put( "attaching", Status.ATTACHING);
		stringStatus.put( "deleting", Status.DELETING);
		stringStatus.put( "error", Status.ERROR);
		stringStatus.put( "error_deleting", Status.ERROR_DELETING);
		stringStatus.put( "backing-up", Status.BACKING_UP);
		stringStatus.put( "restoring-backup", Status.RESTORING_BACKUP);
		stringStatus.put( "error_restoring", Status.ERROR_RESTORING);
		stringStatus.put( "error_extending", Status.ERROR_EXTENDING);
		stringStatus.put( "in-use", Status.INUSE);
		stringStatus.put( "available", Status.AVAILABLE);
		stringStatus.put( "N/A", Status.NA);
		
		
		
		this.name        = _name;
		this.ID          = _ID;
		this.status      = status;
		this.bootable    = _bootable;
		this.readonly    = _readonly;
		//status      = _attachmode;
		this.gigabyte    = _gigabyte;
	  
		this.attachedto_serverid   = _servid;
		this.attachedto_servername = _servname;
		this.attachedto_device     = _device;
    }

    public String 	getName() { return name; }
    public String 	getID() { return ID; }
    public Status 	getStatus() { return status; }
    public String   getStatusString( ) { return statusString.get(status);}
    public boolean 	isBootable( ) { return bootable; }
    public boolean 	isReadOnly( ) { return readonly; }
    //public String 	getAttachMod( ) { return attachmode; }
    public int 		getSize( ) { return gigabyte; }
    public String 	getAttachedServerID( ) { return attachedto_serverid; }
    public String 	getAttachedServerName( ) { return attachedto_servername; }
    public String 	getAttachedDevice( ) { return attachedto_device; }
    
    @Override
    public String toString( ) {
    	return name;
    }
    
    public String tostring( ) {
    	return "Volume={" + 
    			"name=" + name +
    			", ID=" + ID +
    			", status=" + statusString.get(status) +
    			", bootable=" + bootable +
    			", readonly=" + readonly +
    			", gigabytes=" + gigabyte +
    			", serverid=" + attachedto_serverid +
    			", servername=" + attachedto_servername +
    			", device=" + attachedto_device +
    			", isAttached=" + isAttached( ) +
    			"}";
    }
    
    //public boolean isAttached( ) { return ( attachedto_serverid!=null && attachedto_serverid.length()!=0 ); }
    public boolean isAttached( ) { return status==Status.INUSE || status==Status.ATTACHING; }
    
	public static Vector<Volume> parse( String volumesJson, String serversJson)  throws ParseException  {
		
		statusString = new Hashtable<Status,String>();
    	stringStatus = new Hashtable<String,Status>();
        statusString.put( Status.CREATING, "creating");
        statusString.put( Status.ATTACHING,"attaching");
        statusString.put( Status.DELETING,"deleting");
        statusString.put( Status.ERROR,"error");
        statusString.put( Status.ERROR_DELETING,"error_deleting");
        statusString.put( Status.BACKING_UP,"backing-up");
		statusString.put( Status.RESTORING_BACKUP,"restoring-backup");
		statusString.put( Status.ERROR_RESTORING,"error_restoring");
		statusString.put( Status.ERROR_EXTENDING,"error_extending");
		statusString.put( Status.INUSE,"in-use");
		statusString.put( Status.AVAILABLE,"available"); 
		statusString.put( Status.NA,"N/A");
  	  	
		stringStatus.put( "creating", Status.CREATING);
		stringStatus.put( "attaching", Status.ATTACHING);
		stringStatus.put( "deleting", Status.DELETING);
		stringStatus.put( "error", Status.ERROR);
		stringStatus.put( "error_deleting", Status.ERROR_DELETING);
		stringStatus.put( "backing-up", Status.BACKING_UP);
		stringStatus.put( "restoring-backup", Status.RESTORING_BACKUP);
		stringStatus.put( "error_restoring", Status.ERROR_RESTORING);
		stringStatus.put( "error_extending", Status.ERROR_EXTENDING);
		stringStatus.put( "in-use", Status.INUSE);
		stringStatus.put( "available", Status.AVAILABLE);
		stringStatus.put( "N/A", Status.NA);
		
		Vector<Server> servs = Server.parse( serversJson, null );
		Hashtable<String, String> server_id_to_name_mapping = new Hashtable<String, String>();
		Iterator<Server> sit = servs.iterator();
		while(sit.hasNext()) {
			Server S = sit.next();
			server_id_to_name_mapping.put( S.getID(), S.getName() );
		}
		Vector<Volume> vols = new Vector<Volume>();
		try {
			JSONArray volArray = (new JSONObject( volumesJson )).getJSONArray("volumes");
			for(int i = 0; i<volArray.length(); i++) {
				JSONObject volume = volArray.getJSONObject(i);
				String name = volume.has("display_name") ? volume.getString("display_name") : "N/A";
				if(name.compareTo("N/A")==0) {
					name = volume.has("name") ? volume.getString("name") : "N/A";
				}
				//Status status = volume.has("status") ? volume.getString("status") : "N/A";
				Status status = Status.NA;
				if(volume.has("status")) {
					if(volume.getString("status").compareTo("null")==0)
						status = Status.NA;
					else
						status = stringStatus.get(volume.getString("status"));
				}
				boolean bootable = volume.has("bootable") ? volume.getBoolean("bootable") : false;
				boolean readonly = false;
				String attachmode = "rw";
				if(volume.has("metadata")) {
					JSONObject metadata = volume.getJSONObject("metadata");
					if(metadata.has("attached_mode"))
						attachmode = metadata.getString("attached_mode");
					if(metadata.has("readonly"))
					    readonly = metadata.getBoolean("readonly");
				}
				String ID = volume.getString("id");
				int size = volume.getInt("size");
				JSONArray attaches = volume.getJSONArray("attachments");
				String attached_serverid = null;
				String attached_servername = null;
				String attached_device = null;
				if(attaches.length()>0) {
					attached_serverid = attaches.getJSONObject(0).getString("server_id");
					attached_servername = server_id_to_name_mapping.get(attached_serverid);
					attached_device   = attaches.getJSONObject(0).getString("device");
				}
				Volume vol = new Volume(name, ID,
										bootable, readonly, status,
										size, attached_serverid, attached_servername, attached_device );
				vols.add(vol);
			}
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
		return vols;
	}

}
