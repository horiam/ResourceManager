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
import java.util.logging.Logger;

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

	private static final String CLASS_NAME = ResourceServiceBean.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

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
		log.entering(CLASS_NAME, "exists", new Object[] { id });
		boolean ret = resources.exists(id);
		log.exiting(CLASS_NAME, "exists", ret);
		return ret;
	}
	
	@Override
	@Interceptors(UserHolderAuthorisationInterceptor.class)
	@RolesAllowed(value = { "Admin", "User" })
	public Resource get(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "get", new Object[] { id });
		Resource ret = resources.get(id);		
		log.exiting(CLASS_NAME, "get", ret);
		return ret;
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public void createOrUpdate(String id, Resource resource) {
		log.entering(CLASS_NAME, "createOrUpdate", new Object[] { id, resource });

		resource.removeTask();
		resource.removeUser();

		if (resources.exists(id))
			resources.update(resource);
		else
			resources.create(resource);
		
		log.exiting(CLASS_NAME, "createOrUpdate");
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public List<Resource> list() {
		log.entering(CLASS_NAME, "list", new Object[] {});
		List<Resource> ret = resources.list();
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}
	
	@Override
	@RolesAllowed(value = { "Admin" })
	public void delete(String id) {
		log.entering(CLASS_NAME, "delete", new Object[] { id });
		resources.remove(id);
		log.exiting(CLASS_NAME, "delete");
	}

	@Override
	@RolesAllowed(value = { "Admin" })
	public Task removeResource(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "removeResource", new Object[] { id });
		Task task = taskHelper.createTaskForResource(id, TaskType.removeResource);
		Future<Void> future = async.executeTask(task.getId());
		log.exiting(CLASS_NAME, "removeResource", task);
		return task;
	}

}
