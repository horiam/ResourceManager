package org.horiam.ResourceManager.authorisation;


import javax.ejb.EJBAccessException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.horiam.ResourceManager.model.UserHolder;

public class UserHolderAuthorisationInterceptor extends UserAuthorisation {

	@AroundInvoke
	public Object intercept(InvocationContext invocationCxt) throws Exception {
		
		System.out.println("InterceptUserHolder before");
		
		UserHolder userHolder = (UserHolder) invocationCxt.proceed();
		
		if (isCallerAdmin()
			|| (userHolder.hasUser() 
					&& isUserAuthorised(userHolder.getUser().getId()))) {
			System.out.println("InterceptUserHolder after");
			return userHolder;
		}

		throw new EJBAccessException("User is not authorised to access this object");		
	}
		
}
