package org.horiam.ResourceManager.authorisation;

import javax.ejb.ApplicationException;

@ApplicationException
public class AuthorisationException extends Exception {
	
	private static final long serialVersionUID = -80465465266714602L;

	public AuthorisationException(String message) {
		super(message);
	}
	
	public AuthorisationException(Throwable throwable) {
		super(throwable);
	}

	public AuthorisationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
