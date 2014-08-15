package org.stackdroid.comm;

public class NotAuthorizedException extends Exception {
	private static final long serialVersionUID = 2087368867376448461L;
    public NotAuthorizedException(String message) {
	super(message);
    }
}
