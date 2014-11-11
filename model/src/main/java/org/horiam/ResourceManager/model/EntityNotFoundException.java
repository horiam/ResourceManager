package org.horiam.ResourceManager.model;

public class EntityNotFoundException extends Exception {

	private static final long serialVersionUID = 4106113723005876617L;
	
	public EntityNotFoundException(String message) {
		super(message);
	}
	
	public EntityNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public EntityNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
