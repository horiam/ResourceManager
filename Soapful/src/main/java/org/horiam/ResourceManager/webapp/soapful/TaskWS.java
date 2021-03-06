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
import org.horiam.ResourceManager.services.TaskService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.TaskSEI;

@Stateless 
@WebService(serviceName = "TaskWS",		
targetNamespace = "http://ResourceManagerNS/Tasks",
endpointInterface = "org.horiam.ResourceManager.soap.TaskSEI")
public class TaskWS implements TaskSEI {

	private static final String CLASS_NAME = TaskWS.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	@EJB
	private TaskService taskService;

	@Override
	public List<Task> list() {
		log.entering(CLASS_NAME, "list");
		List<Task> ret = taskService.list();
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}

	@Override
	public boolean exists(String id) {
		log.entering(CLASS_NAME, "exists", new Object[] { id });
		boolean ret = taskService.exists(id);
		log.exiting(CLASS_NAME, "exists", ret);
		return ret;
	}

	@Override
	public Task get(String id) throws ResourceManagerFault {
		log.entering(CLASS_NAME, "get", new Object[] { id });
		try {
			Task ret = taskService.get(id);
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
		taskService.delete(id);
		log.exiting(CLASS_NAME, "delete");
	}	
}
