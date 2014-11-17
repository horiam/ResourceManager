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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.horiam.ResourceManager.businessLogic.exceptions.RecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.ResourceUnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UserUnrecoverableException;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.test.ContainerWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestBusinessLogic  {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private String userId     = "userA";
	private String resourceId = "resource1";
	private String taskId     = "taskX";
	
	@EJB
	protected UserDao userDao;
	@EJB
	protected ResourceDao resourceDao;
	@EJB
	protected TaskDao taskDao;
	@EJB
	protected TaskHelper taskHelper;	
	@EJB
	protected Allocator allocator;
	@EJB
	protected Booking booking;
	
	/*
	@BeforeClass
	public static void setup() throws NamingException {
		
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");
		setupContainer(properties);
		
		userDao = (UserDao) lookup("java:global/Beans/UserDao!" + UserDao.class.getName());
		resourceDao = (ResourceDao) lookup("java:global/Beans/ResourceDao!" + ResourceDao.class.getName());
		taskDao = (TaskDao) lookup("java:global/Beans/TaskDao!" + TaskDao.class.getName());		
	}
	
	@AfterClass
	public static void stop() {
		closeContainer();
	}
	*/
	
	protected Context context;
	
	@Before
	public void setup() throws NamingException {
		
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");
		context = EJBContainer.createEJBContainer(properties).getContext();
		context.bind("inject", this);
		
		User user = new User(userId);		
		Resource resource = new Resource(resourceId);		
		Task task = new Task(taskId);
		
		userDao.create(user);
		resourceDao.create(resource);	
		taskDao.create(task);				
	}
	
	@After
	public void tearDown() throws NamingException {
		
		userDao.clear();
		resourceDao.clear();
		taskDao.clear();
		
		context.close();
	}	
	
	@Test
	public void aTest() throws NamingException, InterruptedException, UnrecoverableException, 
			ResourceUnrecoverableException, UserUnrecoverableException, EntityNotFoundException {		
		System.out.println("\nTest Allocator EJB...\n");
		
		//Allocator allocator = (Allocator) lookup("java:global/Beans/Allocator!" + Allocator.class.getName());
		
		allocator.attachUser(userId, resourceId);
		
		User user = userDao.get(userId);
		Resource resource = resourceDao.get(resourceId);
		
		assertTrue("User must have this resource", user.getResource().equals(resource));
		assertTrue("Resource must have this user", resource.getUser().equals(user));
		
		allocator.detachUser(userId);
		
		user = userDao.get(userId);
		resource = resourceDao.get(resourceId);
		
		assertNull("User must not have a resource", user.getResource());
		assertNull("Resource must not have a user", resource.getUser());		
	}
	
	@Test
	public void bTest() throws NamingException, EntityNotFoundException {		
		System.out.println("\nTest TaskHelper EJB...\n");
		
		//taskHelper = (TaskHelper) lookup("java:global/Beans/TaskHelper!" + TaskHelper.class.getName());
		
		Task task = taskHelper.createTask(TaskType.allocateResourceForUser);		
		assertTrue("Tasks must be in the DAO", task.equals(taskDao.get(task.getId())));
		assertTrue("Task must be processing", (task.getStatus() == Task.Status.PROCESSING));
		assertTrue("Must return true", taskHelper.getStatus(task.getId()) == Task.Status.PROCESSING);
		
		task = taskHelper.setUser(task.getId(), userId);
		User user1 = task.getUser();
		User user2 = userDao.get(userId);
		assertTrue("Users must be equal", user2.equals(user1));
		
		task = taskHelper.setResource(task.getId(), resourceId);
		Resource resource1 = task.getResource();
		Resource resource2 = resourceDao.get(resourceId);
		assertTrue("Resources must be equal", resource2.equals(resource1));
		
		String message = "foo";
		task = taskHelper.succeeded(task.getId(), message);
		assertTrue("Task must be succedeed", (task.getStatus() == Task.Status.SUCCEEDED));
		assertTrue("Message must be the same", message.equals(task.getMessage()));		
		
		//clean
		taskDao.remove(task.getId());
		
		// this time we use the task created in the before()
		task = taskHelper.failed(taskId, message, true);
		assertTrue("Task must be failed", (task.getStatus() == Task.Status.FAILED));
		assertTrue("Message must be the same", message.equals(task.getMessage()));		
		assertTrue("Task must be retryable", task.isRetryable());
	}
	
	@Test
	public void cTest() throws NamingException, EntityNotFoundException, RecoverableException {		
		System.out.println("\nTest Booking EJB...\n");		
		
		//Booking booking = (Booking) lookup("java:global/Beans/Booking!" + Booking.class.getName());
		
		User user = userDao.get(userId);
		assertNull("User must not have any task", user.getTask());
		Task task = taskHelper.setUser(taskId, userId);
		assertFalse("User must not be booked", user.isBooked());
		
		user = booking.reserveUser(taskId, userId);
		task = taskDao.get(taskId);
		user = userDao.get(userId);
		assertTrue("User must be booked", user.isBooked());
		assertTrue("User must have this task", task.equals(user.getTask()));
		// again
		booking.reserveUser(taskId, userId);
		
		Task task2 = taskHelper.createTask(TaskType.allocateResourceForUser);
		task2 = taskHelper.setUser(task2.getId(), userId);
		boolean hasException = false;
		try {
			booking.reserveUser(task2.getId(), userId);
		} catch (RecoverableException re) {
			hasException = true;
		}
		assertTrue("Must throw exception", hasException);
		
		Resource resource = resourceDao.get(resourceId);
		assertNull("Resource must not have any task", resource.getTask());
		task = taskHelper.setResource(taskId, resourceId);
		assertFalse("Resource must not be booked", resource.isBooked());
		
		resource = booking.reserveResource(taskId, resourceId);
		task = taskDao.get(taskId);
		resource = resourceDao.get(resourceId);
		assertTrue("Resource must be booked", resource.isBooked());
		assertTrue("Resource must have this task", task.equals(resource.getTask()));
		
		resource = booking.reserveResource(taskId, resourceId);
		
		task2 = taskHelper.setResource(task2.getId(), resourceId);
		hasException = false;
		try {
			booking.reserveResource(task2.getId(), resourceId);
		} catch (RecoverableException re) {
			hasException = true;
		}	
		assertTrue("Must throw exception", hasException);
		
		hasException = false;
		try {
			booking.reserveOneAvailableResource(task2.getId());
		} catch (RecoverableException ex) {
			hasException = true;
		}
		assertTrue("Must throw exception", hasException);
		
		user = booking.freeUserWithTask(taskId);
		assertFalse("User must not be booked", user.isBooked());
		assertNotNull("User must still have a task", user.getTask());
		
		resource = booking.freeResourceWithTask(taskId);
		assertFalse("Resource must not be booked", resource.isBooked());
		assertNotNull("Resource must still have a task", resource.getTask());
		
		String id = booking.reserveOneAvailableResource(taskId);
		resource = resourceDao.get(id);
		assertTrue("Resource must be booked", resource.isBooked());
		assertTrue("Resource must have this task", task.equals(resource.getTask()));		
	}	


}
