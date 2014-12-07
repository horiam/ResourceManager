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

package org.horiam.ResourceManager.mock;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
//import org.horiam.ResourceManager.jms.TaskEventLauncher;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;

@Singleton
@Lock(READ)
public class UserMockService implements UserService {
	
	public final static String[] initialUserIds = {"userA", "userB"};
	public final static User[]   initialUsers   = {new User(initialUserIds[0]), 
												   new User(initialUserIds[1])};

	private List<User> users;
	
	//@EJB
	//private TaskEventLauncher events;
	
	@PostConstruct
	protected void postConstruct() {
		
		users = new ArrayList<User>();		
		users.addAll(Arrays.asList(initialUsers));
	}
	
	
	@Override
	public List<User> list() {
		return users;		
	}

	@Override
	public boolean exists(String id) {
		if (getUser(id) != null)
			return true;
		
		return false;
	}

	@Override
	public void createOrUpdate(String id, User user) {
		addUser(user);
	}

	@Override
	public User get(String id) throws RecordNotFoundException {		
		User user = getUser(id);
		if (user != null)
			return user;
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void delete(String id) {
		deleteUser(id);
	}

	@Override
	public Task allocateUser(String id) throws RecordNotFoundException {		
		User user = get(id);
		Task task = new Task("mock allocateUser");
		task.setType("allocateResourceForUser");
		//events.fireEvent(task);
		return task;
	}

	@Override
	public Task deallocateUser(String id) throws RecordNotFoundException {
		User user = get(id);
		Task task = new Task("mock deallocateUser");
		task.setType("deallocateUser");
		return task;		
	}

	@Override
	public Task removeUser(String id) throws RecordNotFoundException {
		User user = get(id);
		delete(id);
		Task task = new Task("mock remoceUser");
		task.setType("removeUser");
		return task;
	}
	
	
	private User getUser(String id) {		
		for (User user : users) {
			if (user.getId().equals(id))
				return user;
		}
		return null;
	}
	@Lock(WRITE)
	private void addUser(User user) {
		users.add(user);
	}
	@Lock(WRITE)
	private void deleteUser(String id) {
		User user = getUser(id);
		if (user != null) {
			users.remove(user);
		}
	}

}
