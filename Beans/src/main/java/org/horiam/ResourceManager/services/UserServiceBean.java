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

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.authorisation.ActionOnUserAuthorisationInterceptor;
import org.horiam.ResourceManager.businessLogic.TaskExecutor;
import org.horiam.ResourceManager.businessLogic.TaskHelper;
import org.horiam.ResourceManager.businessLogic.TaskType;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;


@DeclareRoles(value = {"Admin", "User"})
@Stateless
public class UserServiceBean implements UserService {
	
	@Resource 
	private SessionContext context;
	@EJB
	private UserDao users;
	@EJB
	private TaskHelper taskHelper;
	@EJB
	private TaskExecutor async;
	
	
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#list()
	 */
	@Override
	@RolesAllowed(value = {"Admin"})
	public List<? extends User> list() {
		return users.list();
	}
	
	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#exists(java.lang.String)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	public boolean exists(String id) {
		return users.exists(id);
	}

	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#createOrUpdate(java.lang.String, org.horiam.ResourceManager.model.User)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	public void createOrUpdate(String id, User user) {		
		//if (isUserAuthorised(id) && isUserAuthorised(user.getId())) {
		if (id.equals(user.getId())) {
			// clean input User
			user.removeResource();
			user.removeTask();
				
			if (users.exists(id))  
				users.update(user);		
			else
				users.create(user);
		} 
	}

	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#get(java.lang.String)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})		
	public User get(String id) throws EntityNotFoundException {
		
		//if (isUserAuthorised(id))
			return users.get(id);
		
		//return null;
	}

	////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#delete(java.lang.String)
	 */
	@Override
	@RolesAllowed(value = {"Admin"})
	public void delete(String id) {
		
		users.remove(id);
	}
		
	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#allocateUser(java.lang.String)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task allocateUser(String id) throws EntityNotFoundException {
		
		//if (isUserAuthorised(id)) {
		
			Task task = taskHelper.createTask(TaskType.allocateResourceForUser);
			taskHelper.setUser(task.getId(), id);
									
			Future<Void> future = async.executeTask(task.getId());				
			return task; 
		//} 		
		//return null;
	}
		
	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#deallocateUser(java.lang.String)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task deallocateUser(String id) throws EntityNotFoundException {
		
		//if (isUserAuthorised(id)) {
			
			Task task = taskHelper.createTask(TaskType.deallocateUser);
			taskHelper.setUser(task.getId(), id);
			
			Future<Void> future = async.executeTask(task.getId());				
			return task; 
		//} 		
		//return null;
	}
		
	////////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.services.UserService#removeUser(java.lang.String)
	 */
	@Override
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	public Task removeUser(String id) throws EntityNotFoundException {
		
		//if (isUserAuthorised(id)) {
		
			Task task = taskHelper.createTask(TaskType.removeUser);
			taskHelper.setUser(task.getId(), id);
					
			Future<Void> future = async.executeTask(task.getId());
			return task;  
		//} 		
		//return null;
	}
	
	////////////////////////////////////////////////////////////////////////////
/*	
	public boolean isUserAuthorised(String id) {
		
		if (context.isCallerInRole("Admin"))
			return true;
		
		String callerUsername = context.getCallerPrincipal().getName();
		
		if (callerUsername.equals(id))
			return true;
		
		return false;
	}
	
	public boolean isUserAuthorised(User user) {
		
		if (context.isCallerInRole("Admin"))
			return true;
		
		String callerUsername = context.getCallerPrincipal().getName();
				
		if (user != null && callerUsername.equals(user.getId()))
			return true;
		
		return false;
	}
*/
			
}