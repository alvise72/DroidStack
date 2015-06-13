package org.stackdroid.utils;

public class Option implements Comparable<Option> {
	private String name;
	private String rendername;
	private String data;
	private String path;
	private boolean folder;
	private boolean parent;
	
	public Option(String n, String d,String p, boolean folder, boolean parent)
	{
		name = n;
		//rendername = rn;
		data = d;
		path = p;
		this.folder = folder;
		this.parent = parent;
	}
	public String get_name()
	{
		return name;
	}
	//public String getRenderName( ) { return rendername; }
	public String getData()
	{
		return data;
	}
	public String getPath()
	{
		return path;
	}
	@Override
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.get_name().toLowerCase());
		else 
			throw new IllegalArgumentException();
	}
	public boolean isFolder() {
		return folder;
	}
	public boolean isParent() {
		return parent;
	}
}
