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
		Task task = tasks.get(id);		
		return setUser(userId, task);
	}
	
	private Task setUser(String userId, Task task) throws RecordNotFoundException {
		try {
			User user = users.get(userId);
			task.setUser(user);
			return tasks.update(task);
		} catch (RecordNotFoundException e) {
			failed(task, e.getMessage(), false);
			throw e;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task setResource(String id, String resourceId) throws RecordNotFoundException {
		Task task = tasks.get(id);
		return setResource(resourceId, task);
	}
	
	private Task setResource(String resourceId, Task task) throws RecordNotFoundException {
		try {
			Resource resource = resources.get(resourceId);
			task.setResource(resource);
			return tasks.update(task);
		} catch (RecordNotFoundException e) {
			failed(task, e.getMessage(), false);
			throw e;
		}			
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTaskForUser(String userId, TaskType type) throws RecordNotFoundException {
		Task task = createTask(type);
		return setUser(userId, task);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTaskForResource(String resourceId, TaskType type) throws RecordNotFoundException {
		Task task = createTask(type);
		return setResource(resourceId, task);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTask(TaskType type) {
		String taskID = UUID.randomUUID().toString();
		Task task = new Task(taskID, type.toString());
		tasks.create(task);
		return task;
	}

	public Task succeeded(String id, String message) throws RecordNotFoundException {
		Task task = tasks.getLock(id);
		task.setMessage(message);
		task.succeeded();
		return  tasks.update(task);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task failed(String id, String message, boolean retryable) throws RecordNotFoundException {
		Task task = tasks.getLock(id);
		return failed(task, message, retryable);
	}
	
	private Task failed(Task task, String message, boolean retryable) {
		task.setMessage(message);
		task.setRetryable(retryable);
		task.failed();
		return tasks.update(task);
	}
	
	public Status getStatus(String id) throws RecordNotFoundException {
		Task task = tasks.get(id);
		return task.getStatus();
	}
	
	public TaskType getType(String id) throws RecordNotFoundException {
		Task task = tasks.get(id);
		return TaskType.valueOf(task.getType());
	}
	
	public String getUserId(String taskId) throws RecordNotFoundException {
		Task task = tasks.get(taskId);		
		User user = task.getUser();
		return user.getId();			
	}
	
	public String getResourceId(String taskId) throws RecordNotFoundException {
		Task task = tasks.get(taskId);
		Resource resource = task.getResource();
		return resource.getId();
	}
	
	@Asynchronous
	public void fireEvent(String taskId) {
		try {
			Task task = tasks.get(taskId);
			event.fire(task);
		} catch (RecordNotFoundException e) {
			System.out.println(e.getMessage());
		}		
	}
	
}
