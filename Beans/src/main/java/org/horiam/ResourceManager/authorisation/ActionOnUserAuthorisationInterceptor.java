package org.horiam.ResourceManager.authorisation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class ActionOnUserAuthorisationInterceptor extends UserAuthorisation {
	
	@AroundInvoke
	public Object intercept(InvocationContext invocationCxt) throws Exception {
		
		System.out.println("InterceptActionOnUser before");
		
		if (isCallerAdmin()
			|| isUserAuthorised((String) invocationCxt.getParameters()[0])) {
			System.out.println("InterceptActionOnUser after");
			return invocationCxt.proceed();	
		}
			
		throw new SecurityException("User is not authorised to access this object");					
	}
	
}
