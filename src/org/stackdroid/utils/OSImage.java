package org.stackdroid.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class OSImage {
	
    private String   name      = null;
    private long     size      = 0;
    private String   format    = null;
    private String   status    = null;
    private boolean  is_public = true;
    private long     createdAt = 0;
    private String   ID        = null;
    private int      minDisk   = 0;
    private int      minRAM    = 0;

    public OSImage( String name,
		    String ID,
		    long size,
		    String format,
		    String status,
		    boolean _pub,
		    long createdAt,
		    int mindisk,
		    int minram) 
    {
  	this.name = name;
	this.ID = ID;
	this.size = size;
  	this.status = status;
  	this.is_public = _pub;
  	this.createdAt = createdAt;
	this.format = format;
	this.minDisk = mindisk;
	this.minRAM = minram;
    } 
    
    public String getName( ) { return name; }
    public long getSize( ) { return size; }
    public int getSizeMB( ) { return (int)(size/1048576); }
    public String getFormat( ) { return format; }
    public String getStatus( ) { return status; }
    public boolean isPublic( ) { return is_public; }
    public long getCreationDate( ) { return createdAt; }
    public String getID( ) { return ID; }
    public int getMinDISK( ) { return minDisk; }
    public int getMinRAM( ) { return minRAM; }
    
    @Override
    public String toString( ) {
    	return name;
    }
    
    public boolean isSaving( ) {
      return status.compareToIgnoreCase("saving")==0;
    }
    
    public boolean isActive( ) {
      return status.compareToIgnoreCase("active")==0;
    }
    
    /**
    *
    *
    *
    *
    */ 
   public static Vector<OSImage> parse( String jsonString ) throws ParseException
   {
   	try {
   		Vector<OSImage> result = new Vector<OSImage>();
       
   		JSONObject jsonObject = new JSONObject( jsonString );
   		
   		Log.d("OSImage.parse", "jsonString="+jsonString);
   		JSONArray images      = (JSONArray)jsonObject.getJSONArray("images");
     
   		for(int i=0; i<images.length( ); ++i ) {
   			String name         = images.getJSONObject(i).has("name") ? images.getJSONObject(i).getString("name") : "N/A";
   			long size = 0L;
   			if(images.getJSONObject(i).has("size") )
   			  size = (long)images.getJSONObject(i).getLong("size") ;
   			if(images.getJSONObject(i).has("OS-EXT-IMG-SIZE:size") )
   			  size = (long)images.getJSONObject(i).getLong("OS-EXT-IMG-SIZE:size") ;
   			//long   size         = images.getJSONObject(i).has("size") ? (long)images.getJSONObject(i).getLong("size") : 0L;
   			String format       = images.getJSONObject(i).has("disk_format") ? images.getJSONObject(i).getString("disk_format") : "N/A";
   			String creationDate = images.getJSONObject(i).has("created_at") ? images.getJSONObject(i).getString("created_at") : "N/A";
   			String visibility   = images.getJSONObject(i).has("visibility") ? images.getJSONObject(i).getString("visibility") : "N/A";
   			String status       = images.getJSONObject(i).has("status") ? images.getJSONObject(i).getString("status") : "N/A";
   			String ID           = images.getJSONObject(i).has("id") ? images.getJSONObject(i).getString("id") : "N/A";
   			int    mindisk      = 0;
   			if( images.getJSONObject(i).has("min_disk") )
   				mindisk = images.getJSONObject(i).getInt("min_disk");
   			if( images.getJSONObject(i).has("minDisk") )
   				mindisk = images.getJSONObject(i).getInt("minDisk");
   			
   			
   			int    minram       = 0;
   			if(images.getJSONObject(i).has("min_ram"))
   			  minram = images.getJSONObject(i).getInt("min_ram");
   			if(images.getJSONObject(i).has("minRam"))
   			  minram = images.getJSONObject(i).getInt("minRam");
   			
   			

   			SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
   			timeFormatter.setTimeZone( TimeZone.getDefault( ) );
   			Calendar calendar = Calendar.getInstance();
   			long cdate = 0;
   			try {
   				calendar.setTime(timeFormatter.parse(creationDate));
   				cdate = calendar.getTimeInMillis() / 1000;
   			} catch(java.text.ParseException pe) {
   				
   			}
         	  
   			boolean pub = (visibility.compareTo("public")==0 ? true : false);
	  
   			OSImage osimg = new OSImage( name, ID, size, format, status, pub, cdate, mindisk,minram );
	  
   			if(format.compareToIgnoreCase("ari")!=0 && format.compareToIgnoreCase("aki") != 0)
   				result.add( osimg );
   		}
   		return result;
	
   	} catch(org.json.JSONException je) {
   		throw new ParseException( je.getMessage( ) );
   	}
   }
}
