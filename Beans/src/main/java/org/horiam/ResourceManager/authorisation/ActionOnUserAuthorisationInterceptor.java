package org.horiam.ResourceManager.authorisation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ActionOnUserAuthorisationInterceptor extends UserAuthorisation {
	
	@AroundInvoke
	public Object intercept(InvocationContext invocationCxt) throws AuthorisationException, Exception {
				
		if (isCallerAdmin()
			|| isUserAuthorised((String) invocationCxt.getParameters()[0])) {
			return invocationCxt.proceed();	
		}
			
		throw new AuthorisationException("User is not authorised to access this object");					
	}
	
}
