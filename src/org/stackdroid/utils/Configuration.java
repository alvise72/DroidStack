package org.stackdroid.utils;

import java.util.Hashtable;

public class Configuration {
	
  private Hashtable<String, String> values = null;
	
  private static Configuration instance = null;

  private Configuration( ) {
	  values = new Hashtable<String, String>();
  }

  synchronized public static Configuration getInstance( ) {
    if(instance == null)
      instance = new Configuration();
    return instance;
  }

  public String getValue( String key, String Default ) {
	  if(values.containsKey(key))
	  	return values.get(key);
	  return Default;
  }
  
  public void setValue( String key, String val) {
	  values.put(key, val);
  }
}
