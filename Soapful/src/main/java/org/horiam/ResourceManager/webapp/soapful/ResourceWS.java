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
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.services.ResourceService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.ResourceSEI;

@Stateless 
@WebService(serviceName = "ResourceWS",		
targetNamespace = "http://ResourceManagerNS/Resources",
endpointInterface = "org.horiam.ResourceManager.soap.ResourceSEI")
public class ResourceWS implements ResourceSEI {
	
	private static final String CLASS_NAME = ResourceWS.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@EJB
	private ResourceService resourceService;
	
	@Override
	public boolean exists(String id) {
		log.entering(CLASS_NAME, "exists", new Object[] { id });
		boolean ret = resourceService.exists(id);
		log.exiting(CLASS_NAME, "exists", ret);
		return ret;
	}

	@Override
	public Resource get(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "get", new Object[] { id });
		try {
			Resource ret = resourceService.get(id);
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
	public void createOrUpdate(String id, Resource resource) {
		log.entering(CLASS_NAME, "createOrUpdate", new Object[] { id, resource });
		resourceService.createOrUpdate(id, resource);
		log.exiting(CLASS_NAME, "createOrUpdate");
	}

	@Override
	public List<Resource> list() {
		log.entering(CLASS_NAME, "list");
		List<Resource> ret = resourceService.list();
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}

	@Override
	public void delete(String id) {
		log.entering(CLASS_NAME, "delete", new Object[] { id });
		resourceService.delete(id);
		log.exiting(CLASS_NAME, "delete");
	}

	@Override
	public Task removeResource(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "removeResource", new Object[] { id });
		try {
			Task ret = resourceService.removeResource(id);
			log.exiting(CLASS_NAME, "removeResource", ret);
			return ret;
		} catch (RecordNotFoundException e) {
			log.log(Level.FINEST, e.getMessage(), e);
			ResourceManagerFault rmf = new ResourceManagerFault(e.getMessage(), 
														new MessageHolderBean());
			log.throwing(CLASS_NAME, "get", rmf);
			throw rmf;
		}
	}
}
