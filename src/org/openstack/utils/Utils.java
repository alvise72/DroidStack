package org.openstack.utils;

import java.net.URL;
import java.util.*;
import java.security.MessageDigest;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Context;

import android.app.*;
import android.graphics.*;
import android.util.Log;
import android.view.WindowManager;
import android.net.*;
import android.net.NetworkInfo.*;
import android.os.*;
import android.widget.TextView;
import android.view.*;

import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Set;


public class Utils {

    /**
     *
     *
     *
     *
     */
    public static boolean createDir( String path ) {
      return (new File(path)).mkdirs();
    }

    /**
     *
     *
     *
     *
     */
    public static String getFileSize( File file ) {
	return StatTransfer.getBytesString( file.length() );
    }

    /**
     *
     *
     *
     *
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
	    {
		byte[] bytes=new byte[buffer_size];
		for(;;)
		    {
			int count=is.read(bytes, 0, buffer_size);
			if(count==-1)
			    break;
			os.write(bytes, 0, count);
		    }
	    }
        catch(Exception ex){}
    }

    /**
     *
     *
     *
     *
     */
    public static String getFilename( URL url ) {
	if(url==null)
	    return "";

	String baseFilename = url.getFile();
	
	String sep = new String(File.separator);
	
	if(baseFilename.contains(sep.subSequence(0,1))) {
	    int idx = baseFilename.lastIndexOf( sep );
	    if(idx>=0)
		baseFilename = baseFilename.substring(idx+1, baseFilename.length());
	}
	
	return baseFilename;
    }
    
    /**
     *
     *
     *
     *
     */
    public static String getCompleteDate( ) {
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb, Locale.US);
	formatter.format(Locale.US, "%04d%02d%02d-%02d%02d%02d", Calendar.getInstance().get(Calendar.YEAR) ,
			 Calendar.getInstance().get(Calendar.MONTH)+1, 
			 Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
			 Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
			 Calendar.getInstance().get(Calendar.MINUTE),
			 Calendar.getInstance().get(Calendar.SECOND));
	return formatter.toString( );
    }
    
    /**
     *
     *
     *
     *
     */
    public static String getDate( ) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(Locale.US, "%04d%02d%02d", Calendar.getInstance().get(Calendar.YEAR) ,
                         Calendar.getInstance().get(Calendar.MONTH)+1, 
                         Calendar.getInstance().get(Calendar.DAY_OF_MONTH) );
        
        return formatter.toString( );
    }

    /**
     *
     *
     *
     *
     */
    public static String getStringPreference( String key, String _default, Context ctx ) {
	
        SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        if( settings != null ) {
            String value = settings.getString( key, _default );
            
            if(value != null ) {
                return value;
            } else {
                return _default;
            }
        } else {
            return _default;
        }
    }
    
    /**
     *
     *
     *
     *
     */
    public static boolean getBoolPreference( String key, boolean _default, Context ctx ) {
	
        SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        if( settings != null ) {
            boolean value = settings.getBoolean( key, _default );
            return value;
        } else {
            return _default;
        }
    }
   
    /**
     *
     *
     *
     *
     */
    public static void putBoolPreference( String key, boolean value, Context ctx ) {
	SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean( key, value );
        editor.commit();	
    } 

    /**
     *
     *
     *
     *
     */
    public static int getIntegerPreference( String key, int _default, Context ctx ) {
	
        SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        if( settings != null ) {
            int value = settings.getInt( key, _default );
	    //Log.i("Utils.getIntegerPreference","VALUE="+value);
            return value;
        } else {
            return _default;
        }
    }
 
    /**
     *
     *
     *
     *
     */
    public static long getLongPreference( String key, long _default, Context ctx ) {
	
        SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        if( settings != null ) {
            long value = settings.getLong( key, _default );
	    return value;
        } else {
            return _default;
        }
    }
       
    /**
     *
     *
     *
     *
     */
    public static void putStringPreference( String key, String value, Context ctx ) {
	SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value );
        editor.commit();
    }
    
    /**
     *
     *
     *
     *
     */
    public static void putIntegerPreference( String key, int value, Context ctx ) {
	SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt( key, value );
        editor.commit();
    }
 
    /**
     *
     *
     *
     *
     */
    public static void putLongPreference( String key, long value, Context ctx ) {
	SharedPreferences settings = ctx.getSharedPreferences( "OPENSTACK", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong( key, value );
        editor.commit();
    }

    /**
     *
     *
     *
     *
     */
    public static String join(Vector s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    /**
     *
     *
     *
     *
     */
    public static void alert( String message, Context ctx ) {
	AlertDialog.Builder alertbox = new AlertDialog.Builder( ctx );
        alertbox.setMessage( message );
	alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
 
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
	AlertDialog alert = alertbox.create();
	alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,  
				   WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alert.show();
    }

    /**
     *
     *
     *
     *
     */
    public static void customAlert( String message, String title, Context ctx ) {
	ProgressBarDialog dialog = new ProgressBarDialog( ctx, message, title );
	
	dialog.show();
    }

    /**
     *
     *
     *
     *
     */
    public static int countLines(String filename) {
	try {
	    LineNumberReader reader  = new LineNumberReader(new FileReader(filename));
	    int cnt = 0;
	    String lineRead = "";
	    while ((lineRead = reader.readLine()) != null) {}
	    
	    cnt = reader.getLineNumber(); 
	    reader.close();
	    return cnt;
	} catch(Exception e) {return 3;}
    }

    /**
     *
     *
     *
     *
     */
    public static boolean internetOn( Context ctx ) {

	ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
       	if(cm==null) return true;	
	NetworkInfo ni_mob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	NetworkInfo ni_wifi= cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	if(ni_mob!=null) {
	    if(ni_mob.getState()==NetworkInfo.State.CONNECTED)
		return true;
	}
	if(ni_wifi!=null) {
	    if(ni_wifi.getState()==NetworkInfo.State.CONNECTED)
		return true;
	}
	if(ni_wifi==null && ni_mob==null) return true;
	return false;
    }

    /**
     *
     *
     *
     *
     */
    public static boolean externalSdcardON( ) {
	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
	   Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING ) )
	    return true;
	else
	    return false;
    }
    
    /**
     *
     *
     *
     *
     */
    public static void showProblemAndExit( final Context ctx, String message ) {
	
	AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
	builder.setMessage( message )
            .setCancelable(false)
            .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
			((Activity)ctx).finish();
                    }
                });
        
        AlertDialog alert = builder.create();

        alert.show();
    } 

    /**
     *
     *
     *
     *
     */
    public static int getBetterFontSize( Context ctx) {
      	int h = getIntegerPreference( "SCREENH", 320, ctx );
        int w = getIntegerPreference( "SCREENW", 240, ctx );
	//Log.i("Utils.getBetterFonts", "W="+w);
	if(w>240) return 22;
	else return 16;
    }
    
    /**
     *
     *
     *
     *
     */
    public static byte[] getBytesFromFile(File file) {
	try {
	    InputStream is = new FileInputStream(file);
	    
	    // Get the size of the file
	    long length = file.length();
	    
	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
		// File is too large
	    }
	    
	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];
	    
	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
		   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		offset += numRead;
	    }
	    
	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
		//throw new IOException("Could not completely read file "+file.getName());
		return null;
	    }
	    
	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	} catch(IOException e) {
	    return null;
	}
    }
 
    /**
     *
     *
     *
     *
     */    
    public static String replace(String text, String searchString, String replacement) {
	
        if ( text==null || searchString==null || text.length()==0 || searchString.length()==0 || replacement == null ) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
	int len = searchString.length();
	int startpos = 0;
	int pos = 0;
	int max = 0;
	do {
	    pos = text.indexOf( searchString, startpos );
	    if(pos!=-1) max++;
	    startpos=pos+len;
	} while(pos!=-1);
	
	int replLength = searchString.length();
	int increase = replacement.length() - replLength;
	increase = increase < 0 ? 0 : increase;
	increase *= max < 0 ? 16 : max > 64 ? 64 : max;
	StringBuilder buf = new StringBuilder(text.length() + increase);
	while (end != -1) {
	    buf.append(text.substring(start, end)).append(replacement);
	    start = end + replLength;
	    if (--max == 0) {
		break;
	    }
	    end = text.indexOf(searchString, start);
	}
	buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     *
     *
     *
     * 
     */
    public static String stringFromFile( String file ) throws IOException {
	FileInputStream stream = new FileInputStream(new File(file));
	try {
	    FileChannel fc = stream.getChannel();
	    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
	    return Charset.defaultCharset().decode(bb).toString();
	}
	finally {
	    stream.close();
	}
    }

    /**
     *
     *
     *
     *
     */    
    public static long now( ) {
      return Calendar.getInstance( ).getTimeInMillis()/1000;
    }
    
    /**
     *
     *
     *
     *
     */    
    // public static void userToFile( User U ) throws Exception, IOException {
    // 	String filename = Environment.getExternalStorageDirectory() + "/AndroStack/users/" + U.getUserID( ) + "." + U.getTenantID( );
    // 	File f = new File( filename );
    // 	if(f.exists()) f.delete();

    // 	OutputStream os = new FileOutputStream( filename );
    // 	ObjectOutputStream oos = new ObjectOutputStream( os );
    // 	oos.writeObject( U );
    // 	oos.close( );
    // }
    
    /**
     *
     *
     *
     *
     */        
     // public static User userFromFile( String filename ) throws Exception, IOException {
     // 	 if(false == (new File(filename)).exists())
     // 	     throw new IOException( "File ["+filename+"] doesn't exist" );
	 
     // 	 InputStream is = new FileInputStream( filename );
     // 	 ObjectInputStream ois = new ObjectInputStream( is );
     // 	 User U = (User)ois.readObject( );

     // 	 //	 Log.d("Utils.userFromFile", "USERID="+U.getUserID() );

     // 	 ois.close( );
     // 	 return U;
     // }
 

}
