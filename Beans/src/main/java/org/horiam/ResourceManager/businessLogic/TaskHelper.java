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

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.horiam.ResourceManager.businessLogic.TaskType;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.Task.Status;
import org.horiam.ResourceManager.model.User;

@Stateless
public class TaskHelper {
	
	private static final String CLASS_NAME = TaskHelper.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;	
	@Inject 
	Event<Task> event;
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task setUser(String id, String userId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "setUser", new Object[] { id, userId });

		Task task = tasks.get(id);		
		Task ret = setUser(userId, task);

		log.exiting(CLASS_NAME, "setUser", ret);
		return ret;
	}
	
	private Task setUser(String userId, Task task) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "setUser", new Object[] { userId, task });

		try {
			User user = users.get(userId);
			task.setUser(user);
			Task ret = tasks.update(task);

			log.exiting(CLASS_NAME, "setUser", ret);
			return ret;
		} catch (RecordNotFoundException e) {
			failed(task, e.getMessage(), false);
			log.throwing(CLASS_NAME, "setUser", e);
			throw e;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task setResource(String id, String resourceId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "setResource", new Object[] { id, resourceId });

		Task task = tasks.get(id);
		Task ret = setResource(resourceId, task);

		log.exiting(CLASS_NAME, "setResource", ret);
		return ret;
	}
	
	private Task setResource(String resourceId, Task task) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "setResource", new Object[] { resourceId, task });

		try {
			Resource resource = resources.get(resourceId);
			task.setResource(resource);
			Task ret = tasks.update(task);

			log.exiting(CLASS_NAME, "setResource", ret);
			return ret;
		} catch (RecordNotFoundException e) {
			failed(task, e.getMessage(), false);
			log.throwing(CLASS_NAME, "setResource", e);
			throw e;
		}			
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTaskForUser(String userId, TaskType type) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "createTaskForUser", new Object[] { userId, type });

		Task task = createTask(type);
		Task ret = setUser(userId, task);

		log.exiting(CLASS_NAME, "createTaskForUser", ret);
		return ret;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTaskForResource(String resourceId, TaskType type) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "createTaskForResource", new Object[] { resourceId, type });

		Task task = createTask(type);
		Task ret = setResource(resourceId, task);

		log.exiting(CLASS_NAME, "createTaskForResource", ret);
		return ret;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTask(TaskType type) {
		log.entering(CLASS_NAME, "createTask", new Object[] { type });

		String taskID = UUID.randomUUID().toString();
		Task task = new Task(taskID, type.toString());
		tasks.create(task);
		
		log.exiting(CLASS_NAME, "createTask", task);
		return task;
	}

	public Task succeeded(String id, String message) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "succeeded", new Object[] { id, message });

		Task task = tasks.getLock(id);
		task.setMessage(message);
		task.succeeded();
		Task ret = tasks.update(task);

		log.exiting(CLASS_NAME, "succeeded", ret);
		return ret;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task failed(String id, String message, boolean retryable) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "failed", new Object[] { id, message, retryable });

		Task task = tasks.getLock(id);
		Task ret = failed(task, message, retryable);

		log.exiting(CLASS_NAME, "failed", ret);
		return ret;
	}
	
	private Task failed(Task task, String message, boolean retryable) {
		log.entering(CLASS_NAME, "failed", new Object[] { task, message, retryable });

		task.setMessage(message);
		task.setRetryable(retryable);
		task.failed();
		Task ret = tasks.update(task);

		log.exiting(CLASS_NAME, "failed", ret);
		return ret;
	}
	
	public Status getStatus(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "getStatus", new Object[] { id });

		Task task = tasks.get(id);
		Status ret = task.getStatus();

		log.exiting(CLASS_NAME, "getStatus", ret);
		return ret;
	}
	
	public TaskType getType(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "getType", new Object[] { id });

		Task task = tasks.get(id);
		TaskType ret = TaskType.valueOf(task.getType());

		log.exiting(CLASS_NAME, "getType", ret);
		return ret;
	}
	
	public String getUserId(String taskId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "getUserId", new Object[] { taskId });

		Task task = tasks.get(taskId);		
		User user = task.getUser();
		String ret = user.getId();			

		log.exiting(CLASS_NAME, "getUserId", ret);
		return ret;
	}
	
	public String getResourceId(String taskId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "getResourceId", new Object[] { taskId });

		Task task = tasks.get(taskId);
		Resource resource = task.getResource();
		String ret = resource.getId();

		log.exiting(CLASS_NAME, "getResourceId", ret);
		return ret;
	}
	
	@Asynchronous
	public void fireEvent(String taskId) {
		log.entering(CLASS_NAME, "fireEvent", new Object[] { taskId });

		try {
			Task task = tasks.get(taskId);
			event.fire(task);
		} catch (RecordNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}		
		
		log.exiting(CLASS_NAME, "fireEvent");
	}
	
}
