package org.openstack.utils;

public class UserException extends Exception {
	
	private static final long serialVersionUID = 2087368867376448461L;
	
    public UserException( String message ) {
	super(message);
    }
}
