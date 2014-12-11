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

package org.horiam.ResourceManager.webapp.soapful;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.UserSEI;

@Stateless // TODO
@WebService(serviceName = "UserWS",		
targetNamespace = "http://ResourceManagerNS/Users",
endpointInterface = "org.horiam.ResourceManager.soap.UserSEI")
public class UserWS implements UserSEI  {
	
	@EJB
	private UserService userService;
	

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#list()
	 */
	@Override
	public List<User> list() {
		return userService.list();
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String id) {
		return userService.exists(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#createOrUpdate(java.lang.String, org.horiam.ResourceManager.model.User)
	 */
	@Override
	public void createOrUpdate(String id, User user) {
		userService.createOrUpdate(id, user);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#get(java.lang.String)
	 */
	@Override
	public User get(String id) throws ResourceManagerFault {
		try {
			return userService.get(id);
		} catch (AuthorisationException | RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		userService.delete(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#allocateUser(java.lang.String)
	 */
	@Override
	public Task allocateUser(String id) throws ResourceManagerFault {
		try {
			return userService.allocateUser(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#deallocateUser(java.lang.String)
	 */
	@Override
	public Task deallocateUser(String id) throws ResourceManagerFault {
		try {
			return userService.deallocateUser(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#removeUser(java.lang.String)
	 */
	@Override
	public Task removeUser(String id) throws ResourceManagerFault {
		try {
			return userService.removeUser(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}	
}
