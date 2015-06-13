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
	
    private String name;
    private String ID;
    private String status;
    private boolean bootable;
    private boolean readonly;
    private String attachmode;
    private int gigabyte;
    
    private String attachedto_serverid;
    private String attachedto_servername;
    private String attachedto_device;
    
    public Volume( String _name,
    			   String _ID,
    			   String _status,
    			   boolean _bootable,
    			   boolean _readonly,
    			   String _attachmode,
    			   int _gigabyte,
    			   String _servid,
    			   String _servname,
    			   String _device) 
    {
	  name        = _name;
	  ID          = _ID;
	  status      = _status;
	  bootable    = _bootable;
	  readonly    = _readonly;
	  attachmode  = _attachmode;
	  gigabyte    = _gigabyte;
	  
	  attachedto_serverid   = _servid;
	  attachedto_servername = _servname;
	  attachedto_device     = _device;
    }

    public String getName() { return name; }
    public String getID() { return ID; }
    public String getStatus() { return status; }
    public boolean isBootable( ) { return bootable; }
    public boolean isReadOnly( ) { return readonly; }
    public String getAttachMod( ) { return attachmode; }
    public int getSize( ) { return gigabyte; }
    public String getAttachedServerID( ) { return attachedto_serverid; }
    public String getAttachedServerName( ) { return attachedto_servername; }
    public String getAttachedDevice( ) { return attachedto_device; }
    
    @Override
    public String toString( ) {
    	return name;
    }
    
    public String tostring( ) {
    	return "Volume={" + 
    			"name=" + name +
    			", ID=" + ID +
    			", status=" + status +
    			", bootable=" + bootable +
    			", readonly=" + readonly +
    			", attachmode=" + attachmode +
    			", gigabytes=" + gigabyte +
    			", serverid=" + attachedto_serverid +
    			", servername=" + attachedto_servername +
    			", device=" + attachedto_device +
    			", isAttached=" + isAttached( ) +
    			"}";
    }
    
    public boolean isAttached( ) { return ( attachedto_serverid!=null && attachedto_serverid.length()!=0 ); }
    
	public static Vector<Volume> parse( String volumesJson, String serversJson)  throws ParseException  {
		
		Vector<Server> servs = Server.parse( serversJson );
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
				String status = volume.has("status") ? volume.getString("status") : "N/A";
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
				Volume vol = new Volume(name, ID, status,
										bootable, readonly, attachmode,
										size, attached_serverid, attached_servername, attached_device );
				vols.add(vol);
			}
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
		return vols;
	}

}
