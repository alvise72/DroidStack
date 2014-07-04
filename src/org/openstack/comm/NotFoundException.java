package org.openstack.comm;

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
	super(message);
    }
}
