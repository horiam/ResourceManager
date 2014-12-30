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

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.authorisation.ActionOnUserAuthorisationInterceptor;
import org.horiam.ResourceManager.businessLogic.TaskExecutor;
import org.horiam.ResourceManager.businessLogic.TaskHelper;
import org.horiam.ResourceManager.businessLogic.TaskType;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;



@DeclareRoles(value = {"Admin", "User"})
@Stateless
public class UserServiceBean implements UserService {
	
	private static final String CLASS_NAME = UserServiceBean.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@Resource 
	private SessionContext context;
	@EJB
	private UserDao users;
	@EJB
	private TaskHelper taskHelper;
	@EJB
	private TaskExecutor async;
	
	
	@Override
	@RolesAllowed(value = {"Admin"})
	public List<User> list() {
		log.entering(CLASS_NAME, "list", new Object[] {});
		List<User> ret = users.list();
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////////////

	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	public boolean exists(String id) {
		log.entering(CLASS_NAME, "exists", new Object[] { id });
		boolean ret = users.exists(id);
		log.exiting(CLASS_NAME, "exists", ret);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////
	
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	public void createOrUpdate(String id, User user) {		
		log.entering(CLASS_NAME, "createOrUpdate", new Object[] { id, user });
		if (id.equals(user.getId())) {
			// clean input User
			user.removeResource();
			user.removeTask();
				
			if (users.exists(id))  
				users.update(user);		
			else
				users.create(user);
		} 
		log.exiting(CLASS_NAME, "createOrUpdate");
	}

	////////////////////////////////////////////////////////////////////////////
	
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})		
	public User get(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "get", new Object[] { id });
		User ret = users.get(id);
		log.exiting(CLASS_NAME, "get", ret);
		return ret;
	}

	////////////////////////////////////////////////////////////////////////////

	@Override
	@RolesAllowed(value = {"Admin"})
	public void delete(String id) {
		log.entering(CLASS_NAME, "delete", new Object[] { id });
		users.remove(id);
		log.exiting(CLASS_NAME, "delete");
	}
		
	////////////////////////////////////////////////////////////////////////////
	
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task allocateUser(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "allocateUser", new Object[] { id });
		Task task = taskHelper.createTaskForUser(id, TaskType.allocateResourceForUser);
		Future<Void> future = async.executeTask(task.getId());
		log.exiting(CLASS_NAME, "allocateUser", task);
		return task;
	}
		
	////////////////////////////////////////////////////////////////////////////

	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task deallocateUser(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "deallocateUser", new Object[] { id });
		Task task = taskHelper.createTaskForUser(id, TaskType.deallocateUser);
		Future<Void> future = async.executeTask(task.getId());
		log.exiting(CLASS_NAME, "deallocateUser", task);
		return task;
	}
		
	////////////////////////////////////////////////////////////////////////////

	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task removeUser(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "removeUser", new Object[] { id });
		Task task = taskHelper.createTaskForUser(id, TaskType.removeUser);
		Future<Void> future = async.executeTask(task.getId());
		log.exiting(CLASS_NAME, "removeUser", task);
		return task;  
	}
}
