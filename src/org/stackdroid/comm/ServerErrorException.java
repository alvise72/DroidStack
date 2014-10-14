package org.stackdroid.comm;

public class ServerErrorException extends Exception {
	private static final long serialVersionUID = 1087368867376448461L;
    public ServerErrorException(String message) {
	super(message);
    }
}
