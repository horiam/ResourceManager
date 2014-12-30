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

import javax.annotation.Resource;
import javax.ejb.SessionContext;

public abstract class UserAuthorisation {

	private static final String CLASS_NAME = UserAuthorisation.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@Resource 
	private SessionContext sessionCtx; 
	
	protected boolean isCallerAdmin() {
		log.entering(CLASS_NAME, "isCallerAdmin");
		
		boolean ret = sessionCtx.isCallerInRole("Admin");
		
		log.exiting(CLASS_NAME, "isCallerAdmin", ret);
		return ret;
	}
	
	protected boolean isUserAuthorised(String id) {
		log.entering(CLASS_NAME, "isUserAuthorised", new Object[] { id });

		String callerUsername = sessionCtx.getCallerPrincipal().getName();		
		
		boolean ret = callerUsername.equals(id);
		log.exiting(CLASS_NAME, "isUserAuthorised", ret);
		return ret;
	}
}
