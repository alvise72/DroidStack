package org.droidstack.utils;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;

public class StatTransfer {

   public static Context ctx = null;

  /**
   *
   *
   *
   *
   *
   *
   *
   */
  public static String getMessage( long bytes, long before, long after ) {
  
    double delay = (double)(( (double)after-(double)before)/1000);
    String unit = "";
    double gB = 0.0;
    if(bytes > 1024*1024 ) {
      gB = (double)(bytes/1048576);
      unit = "MB";
    }
    else {
      gB = (double)(bytes/1024);
      unit = "kB";
    }
    
    double rate = 0.0;
    
    if(delay>0)
      rate = (double)(gB/delay);
    else
      rate = 0;
      
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb/*, Locale.US*/);
    if(delay>0)
      formatter.format(Locale.US, "%.2f %s @%.2f %s/sec", gB, unit, rate, unit);
    else
      formatter.format(Locale.US, "%.2f %s", gB, unit );
    
    String res = formatter.toString( );
    formatter.close( );
    return res;
  }

  
  /**
   *
   *
   *
   *
   *
   *
   *
   */  
  public static String getFloatString( double f ) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb, Locale.US);
    formatter.format(Locale.US, "%.2f", f );
    String res = formatter.toString( );
    formatter.close( );
    return res;
  }
 
    /**
     *
     *
     *
     *
     *
     *
     *
     */  
    public static String formatSpeed( double value ) {
	String unit = "";
	double gB = 0.0;
	if(value > 1024*1024 ) {
	    gB = (double)(value/1048576);
	    unit = "MB";
	}
	else {
	    gB = (double)(value/1024);
	    unit = "kB";
	}
	
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb);
	formatter.format(Locale.US, "%.2f %s/s", gB, unit);
	String res = formatter.toString ();
	formatter.close();
	return res;
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     */  
    public static String formatSize( long value ) {
	String unit = "";
	double gB = 0.0;
	if(value > 1024*1024 ) {
	    gB = (double)(((double)value)/1048576.0);
	    unit = "MB";
	}
	else {
	    gB = (double)(((double)value)/1024.0);
	    unit = "kB";
	}
	
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb);
	formatter.format(Locale.US, "%.2f %s", gB, unit);
	String res = formatter.toString( );
	formatter.close();
	return res;
    }

    
    /**
     *
     *
     *
     *
     *
     *
     *
     */  
    public static long parseSize( String s ) {
	String[] pieces = s.split(" ");

	if(pieces.length!=2)
	    return 0;
	
	int multiplier = 1024;
	if(pieces[1].equals("MB"))
	    multiplier = 1048576;
	
	long val = (long)(multiplier * Double.parseDouble(pieces[0]));
	return val;
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     */  
    public static String getBytesString( long bytes ) {
	String unit = "";
	double gB = 0.0;
	if(bytes > 1024*1024 ) {
	    gB = (double)(((double)bytes)/1048576);
	    unit = "MB";
	}
	else {
	    gB = (double)(((double)bytes)/1024);
	    unit = "kB";
	}
	StringBuilder sb = new StringBuilder();
	Formatter formatter = new Formatter(sb);
	formatter.format(Locale.US, "%.2f %s",gB, unit);
	String res = formatter.toString( );
	formatter.close();
	return res;
    }
}
