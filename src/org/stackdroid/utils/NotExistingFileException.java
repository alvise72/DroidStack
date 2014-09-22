package org.stackdroid.utils;

public class NotExistingFileException extends Exception {
	
	private static final long serialVersionUID = 2087368867376448465L;
	
    public NotExistingFileException( String message ) {
	super(message);
    }
}
