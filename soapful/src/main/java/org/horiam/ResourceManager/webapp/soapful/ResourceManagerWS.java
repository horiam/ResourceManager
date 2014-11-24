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
import javax.jws.WebService;

import org.horiam.ResourceManager.services.ResourceService;
import org.horiam.ResourceManager.services.TaskService;
import org.horiam.ResourceManager.services.UserService;
import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.soap.ResourceManagerSEI;


@WebService(serviceName = "ResourceManagerWS",		
			targetNamespace = "http://ResourceManager/wsdl",
			endpointInterface = "org.horiam.ResourceManager.soapSEI.ResourceManagerSEI")
public class ResourceManagerWS implements ResourceManagerSEI {
	
	@EJB
	private UserService userService;
	@EJB
	private ResourceService resourceService;
	@EJB
	private TaskService taskService;	
	

	@Override
	public List<? extends User> listUsers() {		
		return userService.list();
	}

	@Override
	public boolean userExists(String id) {		
		return false;
	}

	@Override
	public void createOrUpdateUser(String id, User user) {
		userService.createOrUpdate(id, user);
	}

	@Override
	public User getUser(String id) throws RecordNotFoundException, AuthorisationException {
		return userService.get(id);
	}

	@Override
	public void deleteUser(String id) {
		userService.delete(id);
	}

	@Override
	public Task attachUser(String id) throws RecordNotFoundException {
		return userService.allocateUser(id);
	}

	@Override
	public Task detachUser(String id) throws RecordNotFoundException {	
		return userService.deallocateUser(id);
	}

	@Override
	public Task removeUser(String id) throws RecordNotFoundException {
		return userService.removeUser(id);
	}

	@Override
	public boolean resourceExists(String id) {		
		return resourceService.exists(id);
	}

	@Override
	public Resource getResource(String id) throws RecordNotFoundException {
		return resourceService.get(id);
	}

	@Override
	public void createOrUpdateResource(String id, Resource resource) {
		resourceService.createOrUpdate(id, resource);		
	}

	@Override
	public List<Resource> listResources() {		
		return resourceService.list();
	}

	@Override
	public void deleteResource(String id) {
		resourceService.delete(id);
	}

	@Override
	public Task removeResource(String id) throws RecordNotFoundException {		
		return resourceService.removeResource(id);
	}

	@Override
	public List<Task> listTasks() {
		return taskService.list();
	}

	@Override
	public boolean taskExists(String id) {
		return taskService.exists(id);
	}

	@Override
	public Task getTask(String id) throws RecordNotFoundException {
		return taskService.get(id);
	}

	@Override
	public void deleteTask(String id) {
		taskService.delete(id);
	}
}
