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

package org.horiam.ResourceManager.services;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.authorisation.UserHolderAuthorisationInterceptor;
import org.horiam.ResourceManager.businessLogic.TaskExecutor;
import org.horiam.ResourceManager.businessLogic.TaskHelper;
import org.horiam.ResourceManager.businessLogic.TaskType;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.Resource;


@DeclareRoles(value = { "Admin", "User" })
@Stateless
public class ResourceServiceBean implements ResourceService {

	@EJB
	private ResourceDao resources;
	@EJB
	private TaskHelper taskHelper;
	@EJB
	private TaskExecutor async;
	@EJB
	private UserService userService;

	
	@Override
	@RolesAllowed(value = { "Admin", "User" })
	public boolean exists(String id) {
		return resources.exists(id);
	}
	
	@Override
	@Interceptors(UserHolderAuthorisationInterceptor.class)
	@RolesAllowed(value = { "Admin", "User" })
	public Resource get(String id) throws RecordNotFoundException {

		Resource resource = resources.get(id);		
		return resource;		
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public void createOrUpdate(String id, Resource resource) {

		resource.removeTask();
		resource.removeUser();

		if (resources.exists(id))
			resources.update(resource);
		else
			resources.create(resource);
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public List<Resource> list() {
		return resources.list();
	}
	
	@Override
	@RolesAllowed(value = { "Admin" })
	public void delete(String id) {
		resources.remove(id);
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public Task removeResource(String id) throws RecordNotFoundException {

		Task task = taskHelper.createTaskForResource(id, TaskType.removeResource);
		Future<Void> future = async.executeTask(task.getId());
		return task;
	}

}
