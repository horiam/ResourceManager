package org.horiam.ResourceManager.authorisation;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.model.UserHolder;

public class UserHolderAuthorisationInterceptor extends UserAuthorisation {

	@AroundInvoke
	public Object intercept(InvocationContext invocationCxt) throws AuthorisationException, Exception {
		
		
		UserHolder userHolder = (UserHolder) invocationCxt.proceed();
		
		if (isCallerAdmin() 
				|| (userHolder.hasUser() && isUserAuthorised(userHolder.getUser().getId()))) {

				return userHolder;
		}

		throw new AuthorisationException("User is not authorised to access this object");		
	}
		
}
