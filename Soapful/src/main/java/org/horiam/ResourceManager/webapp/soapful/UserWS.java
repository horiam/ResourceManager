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
import java.util.logging.Level;
import java.util.logging.Logger;

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

@Stateless 
@WebService(serviceName = "UserWS",		
targetNamespace = "http://ResourceManagerNS/Users",
endpointInterface = "org.horiam.ResourceManager.soap.UserSEI")
public class UserWS implements UserSEI  {
	
	private static final String CLASS_NAME = UserWS.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@EJB
	private UserService userService;
	

	@Override
	public List<User> list() {
		log.entering(CLASS_NAME, "list");
		List<User> ret = userService.list();
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}

	@Override
	public boolean exists(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "exists", new Object[] { id });
		try {
			boolean ret = userService.exists(id);
			log.exiting(CLASS_NAME, "exists", ret);
			return ret;
		} catch (AuthorisationException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(),
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}

	@Override
	public void createOrUpdate(String id, User user) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "createOrUpdate", new Object[] { id, user });
		try {
			userService.createOrUpdate(id, user);
			log.exiting(CLASS_NAME, "createOrUpdate");
		} catch (AuthorisationException | RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(),
					new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}

	@Override
	public User get(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "get", new Object[] { id });
		try {
			User ret = userService.get(id);
			log.exiting(CLASS_NAME, "get", ret);
			return ret;
		} catch (AuthorisationException | RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(), 
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}

	@Override
	public void delete(String id) {
		log.entering(CLASS_NAME, "delete", new Object[] { id });
		userService.delete(id);
		log.exiting(CLASS_NAME, "delete");
	}

	@Override
	public Task allocateUser(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "allocateUser", new Object[] { id });
		try {
			Task ret = userService.allocateUser(id);
			log.exiting(CLASS_NAME, "allocateUser", ret);
			return ret;
		} catch (AuthorisationException | RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(), 
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}

	@Override
	public Task deallocateUser(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "deallocateUser", new Object[] { id });
		try {
			Task ret = userService.deallocateUser(id);
			log.exiting(CLASS_NAME, "deallocateUser", ret);
			return ret;
		} catch (AuthorisationException | RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(), 
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}

	@Override
	public Task removeUser(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "removeUser", new Object[] { id });
		try {
			Task ret = userService.removeUser(id);
			log.exiting(CLASS_NAME, "removeUser", ret);
			return ret;
		} catch (AuthorisationException | RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(), 
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}	
}
