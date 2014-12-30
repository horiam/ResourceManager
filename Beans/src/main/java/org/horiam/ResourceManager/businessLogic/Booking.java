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
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.horiam.ResourceManager.businessLogic.exceptions.RecoverableException;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.model.Resource;


@Stateless
public class Booking {
	
	private static final String CLASS_NAME = Booking.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;
	@EJB
	private TaskHelper taskHelper;
	
	
	public User reserveUser(String taskId, String userId) throws RecoverableException, RecordNotFoundException {
		log.entering(CLASS_NAME, "reserveUser", new Object[] { taskId, userId });

		Task task = tasks.get(taskId);
		User user = users.getLock(userId);
		
		if (task.equals(user.getTask()))
			return user;

		if (user.isBooked())
			throw new RecoverableException("user " + user.getId() + " is booked with task " 
											+ user.getTask().getId());
		user.setBooked(true);
		user.setTask(task);
		User ret = users.update(user);			    

		log.exiting(CLASS_NAME, "reserveUser", ret);
		return ret;
	}	

	public String reserveOneAvailableResource(String taskId) throws RecoverableException, RecordNotFoundException {
		log.entering(CLASS_NAME, "reserveOneAvailableResource", new Object[] { taskId });

		Task task = tasks.get(taskId);				
		List<Resource> freeResources = resources.listFree();
		
		if (freeResources.size() == 0)
			throw new RecoverableException("No free Resource found");
		
		Random rand = new Random();  
		int next = rand.nextInt(freeResources.size()); // in case of concurrency race
		
		Resource resource = freeResources.get(next);

		resource.setBooked(true);
		resource.setTask(task);
		resource = resources.update(resource);

		String ret = resource.getId();
		log.exiting(CLASS_NAME, "reserveOneAvailableResource", ret);
		return ret;
	}

	public Resource reserveResource(String taskId, String resourceId) throws RecoverableException, RecordNotFoundException {
		log.entering(CLASS_NAME, "reserveResource", new Object[] { taskId, resourceId });

		Task task = tasks.get(taskId);
		Resource resource = resources.getLock(resourceId);
		
		if (task.equals(resource.getTask()))
			return resource;

		if (resource.isBooked()) {
			RecoverableException re = new RecoverableException("Resource " + resource.getId() + " is booked");
			log.throwing(CLASS_NAME, "reserveResource", re);
			throw re;
		}
		resource.setBooked(true);
		resource.setTask(task);
		
		Resource ret = resources.update(resource);

		log.exiting(CLASS_NAME, "reserveResource", ret);
		return ret;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public User freeUserWithTask(String taskId) throws RecordNotFoundException { 
		log.entering(CLASS_NAME, "freeUserWithTask", new Object[] { taskId });

		String userId = tasks.get(taskId).getUser().getId();
		
		User ret = freeUser(userId);
		log.exiting(CLASS_NAME, "freeUserWithTask", ret);
		return ret;
	}
		
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Resource freeResourceWithTask(String taskId) throws RecordNotFoundException { 
		log.entering(CLASS_NAME, "freeResourceWithTask", new Object[] { taskId });
		
		String resourceId = tasks.get(taskId).getResource().getId();
		
		Resource ret = freeResource(resourceId);
		log.exiting(CLASS_NAME, "freeResourceWithTask", ret);
		return ret;
	}	
	
	public User freeUser(String userId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "freeUser", new Object[] { userId });

		User user = users.get(userId);
		user.setBooked(false);
		
		User ret = users.update(user);	
		log.exiting(CLASS_NAME, "freeUser", ret);
		return ret;
	}

	public Resource freeResource(String resourceId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "freeResource", new Object[] { resourceId });

		Resource resource = resources.get(resourceId);
		resource.setBooked(false);

		Resource ret = resources.update(resource); 
		log.exiting(CLASS_NAME, "freeResource", ret);
		return ret;
	}
}
