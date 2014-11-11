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

package org.horiam.ResourceManager.businessLogic;

import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.horiam.ResourceManager.businessLogic.exceptions.RecoverableException;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.model.Resource;


@Stateless
public class Booking {
	
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;
	
	
	public void reserveUser(String taskId, String userId) throws RecoverableException, EntityNotFoundException {
					
		Task task = tasks.get(taskId);
		User user = users.getLock(userId);

		if (user.isBooked())
			throw new RecoverableException("user " + user.getId() + " is booked with task " 
											+ user.getTask().getId());
		user.setTask(task);
		users.update(user);			    
	}	

	public String reserveOneAvailableResource(String taskId) throws RecoverableException, EntityNotFoundException {

		Task task = tasks.get(taskId);				
		List<Resource> freeResources = resources.getAllFree();
		
		if (freeResources.size() == 0)
			throw new RecoverableException("No free Resource found");
		
		Random rand = new Random();  
		int next = rand.nextInt(freeResources.size()); // in case of concurrency race
		
		Resource resource = freeResources.get(next);

		resource.setTask(task);
		resource = resources.update(resource);

		return resource.getId();
	}

	public void reserveResource(String taskId, String resourceId) throws RecoverableException, EntityNotFoundException {

		Task task = tasks.get(taskId);
		Resource resource = resources.getLock(resourceId);

		if (resource.isBooked())
			throw new RecoverableException("Resource " + resource.getId() + " is booked");

		resource.setTask(task);
		resources.update(resource);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void freeUserWithTask(String taskId) throws EntityNotFoundException { 
		
		String userId = tasks.get(taskId).getUser().getId();
		freeUser(userId);
	}
		
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void freeResourceWithTask(String taskId) throws EntityNotFoundException { 
		
		String resourceId = tasks.get(taskId).getResource().getId();
		freeResource(resourceId);
	}	
	
	public void freeUser(String userId) throws EntityNotFoundException {

		User user = users.get(userId);
		user.removeTask();
		users.update(user);	
	}

	public void freeResource(String resourceId) throws EntityNotFoundException {

		Resource resource = resources.get(resourceId);
		resource.removeTask();
		resources.update(resource);
	}
}
