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

package org.horiam.ResourceManager.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.test.ContainerWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDao  {
	
	@EJB
	protected UserDao userDao;
	@EJB
	protected ResourceDao resourceDao;
	@EJB
	protected TaskDao taskDao; 
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	protected Context context;
	
	/*
	@BeforeClass
	public static void setup() {
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");		
		setupContainer(properties);
	}
	
	@AfterClass
	public static void stop() {
		closeContainer();
	}
	*/
	
	@Before
	public void setup() throws NamingException {
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");
		context = EJBContainer.createEJBContainer(properties).getContext();
		context.bind("inject", this);
	}
	
	@After
	public void tearDown() throws NamingException {
		
		userDao.clear();
		resourceDao.clear();
		taskDao.clear();
		
		context.close();
	}
	
	@Test
	public void aTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest UserDao EJB...\n");
		
		//UserDao userDao = (UserDao) lookup("java:global/Beans/UserDao!" + UserDao.class.getName());
		User userA = new User("userA");
		
		userDao.create(userA);
		assertTrue("Entity should exist", userDao.exists(userA.getId()));
		assertTrue("Objects should be equal", userA.equals(userDao.get(userA.getId())));
		
		userA.setBooked(true);
		User updatedUserA = userDao.update(userA);
		assertTrue("Objects should be equal", userA.equals(updatedUserA));
		assertTrue("Objects should be equal", updatedUserA.equals(userDao.get(userA.getId())));
		
		List<User> users = userDao.list();
		assertTrue("List size should be 1", (users.size() == 1));
		
		userDao.remove(userA.getId());
		users = userDao.list();
		assertTrue("List size should be 0", (users.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		userDao.get(userA.getId());	
	}
	
	@Test
	public void bTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest ResourceDao EJB...\n");
		
		//ResourceDao resourceDao = (ResourceDao) lookup("java:global/Beans/ResourceDao!" + ResourceDao.class.getName());
		Resource resource1 = new Resource("resource1");
		
		resourceDao.create(resource1);
		assertTrue("Entity should exist", resourceDao.exists(resource1.getId()));
		assertTrue("Objects should be equal", resource1.equals(resourceDao.get(resource1.getId())));
		
		resource1.setBooked(true);
		Resource updatedUserA = resourceDao.update(resource1);
		assertTrue("Objects should be equal", resource1.equals(updatedUserA));
		assertTrue("Objects should be equal", updatedUserA.equals(resourceDao.get(resource1.getId())));
		
		Resource resource2 = new Resource("resource2");
		resourceDao.create(resource2);
		
		List<Resource> resources = resourceDao.list();
		assertTrue("List size should be 2", (resources.size() == 2));
		
		List<Resource> freeResources = resourceDao.listFree();
		assertTrue("List size should be 1", (freeResources.size() == 1));
		
		resourceDao.remove(resource1.getId());
		resourceDao.remove(resource2.getId());
		
		resources = resourceDao.list();
		assertTrue("List size should be 0", (resources.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		resourceDao.get(resource1.getId());
	}	
	
	@Test
	public void cTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest UserDao EJB...\n");
		
		//TaskDao taskDao = (TaskDao) lookup("java:global/Beans/TaskDao!" + TaskDao.class.getName());
		Task taskX = new Task("taskX");
		
		taskDao.create(taskX);
		assertTrue("Entity should exist", taskDao.exists(taskX.getId()));
		assertTrue("Objects should be equal", taskX.equals(taskDao.get(taskX.getId())));
		
		taskX.setMessage("foo");
		Task updatedTaskX = taskDao.update(taskX);
		assertTrue("Objects should be equal", taskX.equals(updatedTaskX));
		assertTrue("Objects should be equal", updatedTaskX.equals(taskDao.get(taskX.getId())));
		
		List<Task> tasks = taskDao.list();
		assertTrue("List size should be 1", (tasks.size() == 1));
		
		taskDao.remove(taskX.getId());
		tasks = taskDao.list();
		assertTrue("List size should be 0", (tasks.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		taskDao.get(taskX.getId());	
	}
}
