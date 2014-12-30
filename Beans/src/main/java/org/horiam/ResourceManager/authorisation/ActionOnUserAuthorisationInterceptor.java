/*
 * Copyright (C) 2014  Horia Musat
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.horiam.ResourceManager.authorisation;

import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.horiam.ResourceManager.exceptions.AuthorisationException;

public class ActionOnUserAuthorisationInterceptor extends UserAuthorisation {
	
	private static final String CLASS_NAME = ActionOnUserAuthorisationInterceptor.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	@AroundInvoke
	public Object intercept(InvocationContext invocationCxt) throws AuthorisationException, Exception {
		log.entering(CLASS_NAME, "intercept", new Object[] { invocationCxt });

		if (isCallerAdmin()
			|| isUserAuthorised((String) invocationCxt.getParameters()[0])) {

			log.exiting(CLASS_NAME, "intercept");
			return invocationCxt.proceed();	
		}

		AuthorisationException ae = new AuthorisationException("User is not authorised to access this object");					

		log.throwing(CLASS_NAME, "intercept", ae);
		throw ae;
	}
	
}
