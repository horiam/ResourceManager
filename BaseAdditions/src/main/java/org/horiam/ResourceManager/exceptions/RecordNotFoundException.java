package org.horiam.ResourceManager.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException
public class RecordNotFoundException extends Exception { // TODO rename it RecordNotFoundException

	private static final long serialVersionUID = 4106113723005876617L;
	
	public RecordNotFoundException(String message) {
		super(message);
	}
	
	public RecordNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public RecordNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
