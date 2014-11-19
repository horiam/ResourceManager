package org.horiam.ResourceManager.authorisation;

import javax.annotation.Resource;
import javax.ejb.SessionContext;

public abstract class UserAuthorisation {

	@Resource 
	private SessionContext sessionCtx; 
	
	protected boolean isCallerAdmin() {
		return sessionCtx.isCallerInRole("Admin");
	}
	
	protected boolean isUserAuthorised(String id) {
		
		String callerUsername = sessionCtx.getCallerPrincipal().getName();		
		return callerUsername.equals(id);
	}
}
