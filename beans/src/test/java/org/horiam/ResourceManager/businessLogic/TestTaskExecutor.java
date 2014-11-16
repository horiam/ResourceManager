
package org.horiam.ResourceManager.businessLogic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.naming.NamingException;

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
import org.junit.Test;

public class TestTaskExecutor extends ContainerWrapper {
	
	
	private String userId = "userA";
	private String resourceId = "resource1";
	private String taskId = "taskX";
	
	private static UserDao userDao;
	private static ResourceDao resourceDao;
	private static TaskDao taskDao;
	private static TaskHelper taskHelper;
	
	@BeforeClass
	public static void setup() throws NamingException {
		
		Properties properties = new Properties();	
				
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");

		setupContainer(properties);
		
		userDao = (UserDao) lookup("java:global/Beans/UserDao");
		resourceDao = (ResourceDao) lookup("java:global/Beans/ResourceDao");
		taskDao = (TaskDao) lookup("java:global/Beans/TaskDao");
		taskHelper = (TaskHelper) lookup("java:global/Beans/TaskHelper");
	}
	
	@AfterClass
	public static void stop() {
		closeContainer();
	}
	
	@Before
	public void before() {
		
		User user = new User(userId);		
		Resource resource = new Resource(resourceId);		
		Task task = new Task(taskId);
		
		userDao.create(user);
		resourceDao.create(resource);	
		taskDao.create(task);				
	}
	
	@After
	public void after() {
		
		userDao.clear();
		resourceDao.clear();
		taskDao.clear();
	}
	
	
	@Test
	public void dTest() throws NamingException, EntityNotFoundException, 
								InterruptedException, ExecutionException, TimeoutException {		
		System.out.println("\nTest TaskExecutor EJB : allocate and deallocate ...\n");	
		
		TaskExecutor executor = (TaskExecutor) lookup("java:global/Beans/TaskExecutor");
		
		Task task1 = taskHelper.createTask(TaskType.allocateResourceForUser);
		taskHelper.setUser(task1.getId(), userId);
		
		Future<Void> future = executor.executeTask(task1.getId());
		future.get();
		
		User user1 = userDao.get(userId);
		Resource resource1 = resourceDao.get(resourceId);
		task1 = taskDao.get(task1.getId());
		assertTrue("Task must be succedeed", (task1.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user1.isBooked());
		assertTrue("User must still have the task", task1.equals(user1.getTask()));		
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have the task", task1.equals(resource1.getTask()));
		assertTrue("User must have this resource", user1.getResource().equals(resource1));
		assertTrue("Resource must have this user", resource1.getUser().equals(user1));
				
		// create another user
		User user2 = new User("user2");
		userDao.create(user2);	
		Task task2 = taskHelper.createTask(TaskType.allocateResourceForUser);
		taskHelper.setUser(task2.getId(), user2.getId());
		
		// allocateResourceForUser must fail
		future = executor.executeTask(task2.getId());
		future.get();
		
		task2 = taskDao.get(task2.getId());
		user2 = userDao.get(user2.getId());
		assertTrue("Task must be failed", (task2.getStatus() == Task.Status.FAILED));
		assertTrue("Task must be retraybale", task2.isRetryable());
		assertFalse("User must not be booked", user2.isBooked());
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have old task", task1.equals(resource1.getTask()));
		assertNull("New user must have no resource", task2.getResource());
		assertTrue("Resource must have old user", resource1.getUser().equals(user1));
		
		
		// create another resource
		Resource resource2 = new Resource("resource2");
		resourceDao.create(resource2);
		
		// reallocate user1 must give the same Vm1
		future = executor.executeTask(task1.getId());
		future.get();
		task1 = taskDao.get(task1.getId());
		user1 = userDao.get(userId);
		resource1 = resourceDao.get(resourceId);
		// must be untouched
		assertTrue("Task must be succedeed", (task1.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user1.isBooked());
		assertTrue("User must still have the task", task1.equals(user1.getTask()));		
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have the task", task1.equals(resource1.getTask()));
		assertTrue("User must have this resource", user1.getResource().equals(resource1));
		assertTrue("Resource must have this user", resource1.getUser().equals(user1));
				
	}

}
