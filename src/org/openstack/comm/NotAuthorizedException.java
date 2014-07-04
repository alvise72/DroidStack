package org.openstack.comm;

public class NotAuthorizedException extends Exception {
    public NotAuthorizedException(String message) {
	super(message);
    }
}
