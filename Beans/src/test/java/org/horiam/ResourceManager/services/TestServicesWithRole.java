package org.horiam.ResourceManager.services;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.Task.Status;
import org.horiam.ResourceManager.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;


public class TestServicesWithRole {
	
	private String userId     = "userA";
	private String resourceId = "resource1";
	
	@EJB
	private UserDao userDao;
	@EJB
	private ResourceDao resourceDao;
	@EJB
	private TaskDao taskDao;
	// Beans to test
	@EJB
	private UserService userService;
	@EJB
	private ResourceService resourceService;
	@EJB
	private TaskService taskService;
	// Callers
	@EJB
	private AdminCaller adminCaller;
	@EJB
	private UserCaller userCaller;
	

	protected EJBContainer container;
	
	@Before
	public void setup() throws NamingException {
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");
		container = EJBContainer.createEJBContainer(properties);
		container.getContext().bind("inject", this);
		
		userDao.create(new User(userId));
		resourceDao.create(new Resource(resourceId));
	}
	
	@After
	public void tearDown() {		
		userDao.clear();
		resourceDao.clear();
		taskDao.clear();		
		container.close();
	}
	
	///////////////////////////////////////////////////////////////
	
	@Stateless
	@RunAs("Admin")
	// otherwise the bean uses the same transaction for every bean call
	@TransactionManagement(value=TransactionManagementType.BEAN) 
	public static class AdminCaller {
		public Void call(Callable<Void> callable) throws Exception {
			return callable.call();
		}
	}
	
	@Stateless
	@RunAs("User")
	// otherwise the bean uses the same transaction for every bean call
	@TransactionManagement(value=TransactionManagementType.BEAN)
	public static class UserCaller {
		public Void call(Callable<Void> callable) throws Exception {
			return callable.call();
		}
	}
	
	///////////////////////////////////////////////////////////////
	
	@Test
	public void testUserServiceAsAdmin() throws Exception {
		System.out.println("\nTest UserService EJB as Admin...\n");	
		
		Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				List<? extends User> users = userService.list();
				assertTrue("Must contain one User", users.size() == 1);
				assertTrue("Must the same", users.get(0).equals(userDao.get(userId)));
				
				String user2Id = "user2";
				userService.createOrUpdate(user2Id, new User(user2Id));
				assertTrue("Must contain two Users", userService.list().size() == 2);				
				assertTrue("Must exist", userService.exists(user2Id));
				assertNotNull("Must be not null", userService.get(user2Id));
				
				userService.delete(user2Id);
				assertTrue("Must contain one User", userService.list().size() == 1);
				assertFalse("Must exist", userService.exists(user2Id));
								
				return null;
			}			
		};
		
		adminCaller.call(callable);
	}
	
	@Test
	public void testUserServiceAsUser() throws Exception {
		System.out.println("\nTest UserService EJB as User...\n");	
		
		Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				boolean hasException = false;
				try {
					 userService.list();
				} catch (Exception ex) {
					hasException = true;
				}
				assertTrue("Has Exception", hasException);
				return null;
			}			
		};
		
		userCaller.call(callable);
	}
	
	@Test
	public void testResourceServiceAsAdmin() throws Exception {
		System.out.println("\nTest ResourceService EJB as Admin...\n");	
		
		Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				List<? extends Resource> resources = resourceService.list();
				assertTrue("Must contain one Resource", resources.size() == 1);
				assertTrue("Must the same", resources.get(0).equals(resourceDao.get(resourceId)));
				
				String resource2Id = "resource2";
				resourceService.createOrUpdate(resource2Id, new Resource(resource2Id));
				assertTrue("Must contain two Resource", resourceService.list().size() == 2);				
				assertTrue("Must exist", resourceService.exists(resource2Id));
				assertNotNull("Must be not null", resourceService.get(resource2Id));
				
				resourceService.delete(resource2Id);
				assertTrue("Must contain one Resource", resourceService.list().size() == 1);
				assertFalse("Must exist", resourceService.exists(resource2Id));
								
				return null;
			}			
		};
		
		adminCaller.call(callable);
	}
	
	@Test
	public void testTaskServiceAsAdmin() throws Exception {
		System.out.println("\nTest TaskService EJB as Admin...\n");

		Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {

				Task allocateTask = userService.allocateUser(userId);
				String allocateTaskId = allocateTask.getId();				
				boolean isSucceeded = false;
				for (int i = 0; i < 10; i++) {
					Thread.sleep(1000);
					if (taskService.get(allocateTaskId).getStatus() == Status.SUCCEEDED) {
						isSucceeded = true;
						break;
					}
				}
				assertTrue("Task must be succeeded", isSucceeded);
				
				
				Task deallocateTask = userService.deallocateUser(userId);
				String deallocateTaskId = deallocateTask.getId();
				isSucceeded = false;
				for (int i = 0; i < 10; i++) {
					Thread.sleep(1000);
					if (taskService.get(deallocateTaskId).getStatus() == Status.SUCCEEDED) {
						isSucceeded = true;
						break;
					}
				}
				assertTrue("Task must be succeeded", isSucceeded);
				
				
				Task deallocateResource = resourceService.removeResource(resourceId);
				String deallocateResourceId = deallocateResource.getId();
				isSucceeded = false;
				for (int i = 0; i < 10; i++) {
					Thread.sleep(1000);
					if (taskService.get(deallocateResourceId).getStatus() == Status.SUCCEEDED) {
						isSucceeded = true;
						break;
					}
				}
				assertTrue("Task must be succeeded", isSucceeded);
				assertFalse("Must not exist", resourceService.exists(resourceId));
				
				List<Task> tasks = taskService.list();
				assertTrue("Must contain 3 Taks", tasks.size() == 3);
				
				assertTrue("Must exist", taskService.exists(allocateTaskId));
				taskService.delete(allocateTaskId);
				assertFalse("Must not exist", taskService.exists(allocateTaskId));
				assertTrue("Must contain 3 Taks", taskService.list().size() == 2);
			
				Task removeUserTask = userService.removeUser(userId);
				String removeUserTaskId = removeUserTask.getId();
				isSucceeded = false;
				for (int i = 0; i < 10; i++) {
					Thread.sleep(1000);
					if (taskService.get(removeUserTaskId).getStatus() == Status.SUCCEEDED) {
						isSucceeded = true;
						break;
					}
				}
				assertTrue("Task must be succeeded", isSucceeded);
				assertFalse("Must not exist", userService.exists(userId));

				return null;
			}
		};

		adminCaller.call(callable);
	}
	  	
}
