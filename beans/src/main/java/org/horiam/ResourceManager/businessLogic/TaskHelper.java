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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.horiam.ResourceManager.businessLogic.TaskType;
import org.horiam.ResourceManager.businessLogic.exceptions.UnrecoverableException;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@Stateless
public class TaskHelper {
	
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;
	
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void setUser(String id, String userId) throws EntityNotFoundException {
		Task task = tasks.getLock(id);		
		try {
			User user = users.get(userId);
			task.setUser(user);
			tasks.update(task);
		} catch (EntityNotFoundException e) {
			failed(task, e.getMessage(), false);
			throw e;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void setResource(String id, String resourceId) throws EntityNotFoundException {
		Task task = tasks.get(id);
		try {
			Resource resource = resources.get(resourceId);
			task.setResource(resource);
			tasks.update(task);
		} catch (EntityNotFoundException e) {
			failed(task, e.getMessage(), false);
			throw e;
		}			
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Task createTask(TaskType type) {
		String taskID = UUID.randomUUID().toString();
		Task task = new Task(taskID, type.toString());
		tasks.create(task);
		return task;
	}

	public void succeeded(String id, String message) throws EntityNotFoundException {
		Task task = tasks.getLock(id);
		task.setMessage(message);
		task.succeeded();
		task = tasks.update(task);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void failed(String id, String message, boolean retryable) throws EntityNotFoundException {
		Task task = tasks.getLock(id);
		failed(task, message, retryable);
	}
	
	private Task failed(Task task, String message, boolean retryable) {
		task.setMessage(message);
		task.setRetryable(retryable);
		task.failed();
		return tasks.update(task);
	}
	
	public boolean isProcessing(String id) throws EntityNotFoundException {
		Task task = tasks.get(id);
		return (task.getStatus() == Task.Status.PROCESSING); 
	}
	
	public TaskType getType(String id) throws EntityNotFoundException {
		Task task = tasks.get(id);
		return TaskType.valueOf(task.getType());
	}
	
	public String getUserId(String taskId) throws EntityNotFoundException {
		Task task = tasks.get(taskId);		
		User user = task.getUser();
		return user.getId();			
	}
	
	public String getResourceId(String taskId) throws EntityNotFoundException {
		Task task = tasks.get(taskId);
		Resource resource = task.getResource();
		return resource.getId();
	}
}