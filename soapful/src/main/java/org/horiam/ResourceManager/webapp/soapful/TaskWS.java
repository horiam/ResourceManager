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

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.services.TaskService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.TaskSEI;

@Stateless // TODO
@WebService(serviceName = "TaskWS",		
targetNamespace = "http://ResourceManager/wsdl",
endpointInterface = "org.horiam.ResourceManager.soap.TaskSEI")
public class TaskWS implements TaskSEI {

	@EJB
	private TaskService taskService;

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#list()
	 */
	@Override
	public List<Task> list() {
		return taskService.list();
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String id) {
		return taskService.exists(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#get(java.lang.String)
	 */
	@Override
	public Task get(String id) throws ResourceManagerFault {
		try {
			return taskService.get(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		taskService.delete(id);
	}	
}
