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

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Task;



@DeclareRoles(value = { "Admin", "User" })
@Stateless
public class TaskService {
	
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;
	@EJB
	private UserService userService;
	

	@RolesAllowed(value = { "Admin" })
	public List<Task> list() {
		return tasks.list();
	}
	
	@RolesAllowed(value = { "Admin", "User" })
	public boolean exists(String id) {
		return tasks.exists(id);
	}
	
	@RolesAllowed(value = { "Admin", "User" })
	public Task get(String id) throws EntityNotFoundException {

		Task task = tasks.get(id);		
		if (userService.isUserAuthorised(task.getUser()))
			return task;
		
		return null;
	}
	
	@RolesAllowed(value = { "Admin" })
	public void delete(String id) {
		tasks.remove(id);
	}
	
}
