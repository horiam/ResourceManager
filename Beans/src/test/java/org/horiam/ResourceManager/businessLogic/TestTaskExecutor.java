
package org.horiam.ResourceManager.businessLogic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
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

@javax.annotation.ManagedBean 
public class TestTaskExecutor  {
	
	
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
	protected TaskExecutor executor;
	
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
		taskHelper = (TaskHelper) lookup("java:global/Beans/TaskHelper!" + TaskHelper.class.getName());
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
	public void test() throws NamingException, EntityNotFoundException, 
								InterruptedException, ExecutionException, TimeoutException {		
		System.out.println("\nTest TaskExecutor EJB : allocate and deallocate ...\n");	
		
		//TaskExecutor executor = (TaskExecutor) lookup("java:global/Beans/TaskExecutor!" + TaskExecutor.class.getName());
		
		Task task1 = taskHelper.createTask(TaskType.allocateResourceForUser);
		task1 = taskHelper.setUser(task1.getId(), userId);
		
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
		task2 = taskHelper.setUser(task2.getId(), user2.getId());
		
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
		assertNull("New user must have no resource", user2.getResource());
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
		// everything must be untouched
		assertTrue("Task must be succedeed", (task1.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user1.isBooked());
		assertTrue("User must still have the task", task1.equals(user1.getTask()));		
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have the task", task1.equals(resource1.getTask()));
		assertTrue("User must have this resource", user1.getResource().equals(resource1));
		assertTrue("Resource must have this user", resource1.getUser().equals(user1));
		
		// allocate user2 
		Task task3 = taskHelper.createTask(TaskType.allocateResourceForUser);
		taskHelper.setUser(task3.getId(), user2.getId());

		executor.executeTask(task3.getId()).get();
		
		task3 = taskDao.get(task3.getId());
		user2 = userDao.get(user2.getId());
		resource2 = resourceDao.get(resource2.getId());
		assertTrue("Task must be succedeed", (task3.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user2.isBooked());
		assertTrue("User must still have the task", task3.equals(user2.getTask()));		
		assertFalse("Resource must not be booked", resource2.isBooked());
		assertTrue("Resource must still have the task", task3.equals(resource2.getTask()));
		assertTrue("User must have this resource", user2.getResource().equals(resource2));
		assertTrue("Resource must have this user", resource2.getUser().equals(user2));;
		
		
		// deallocate
		Task task4 = taskHelper.createTask(TaskType.deallocateUser);
		task4 = taskHelper.setUser(task4.getId(), userId);
		executor.executeTask(task4.getId()).get();
		
		task4 = taskDao.get(task4.getId());
		user1 = userDao.get(userId);
		resource1 = resourceDao.get(resourceId);
		assertTrue("Task must be succedeed", (task4.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user1.isBooked());
		assertTrue("User must still have the task", task4.equals(user1.getTask()));	
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have the task", task4.equals(resource1.getTask()));
		assertNull("User must have no resource", user1.getResource());
		assertNull("Resource must have no user", resource1.getUser());
		
		// try to deallocate again
		Task task5 = taskHelper.createTask(TaskType.deallocateUser);
		task5 = taskHelper.setUser(task5.getId(), userId);
		executor.executeTask(task5.getId()).get();
		
		task5 = taskDao.get(task5.getId());
		user1 = userDao.get(userId);
		resource1 = resourceDao.get(resourceId);
		assertTrue("Task must be succedeed", (task5.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must not be booked", user1.isBooked());
		assertTrue("User must still have the task", task5.equals(user1.getTask()));	
		assertFalse("Resource must not be booked", resource1.isBooked());
		assertTrue("Resource must still have the old task", task4.equals(resource1.getTask()));
		assertNull("User must have no resource", user1.getResource());
		assertNull("Resource must have no user", resource1.getUser());
		
		// remove resource1
		Task task6 = taskHelper.createTask(TaskType.removeResource);
		task6 = taskHelper.setResource(task6.getId(), resourceId);
		executor.executeTask(task6.getId()).get();
		
		task6 = taskDao.get(task6.getId());
		assertTrue("Task must be succedeed", (task6.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("Resource must be deleted", userDao.exists(resourceId));
				
		// remove user2
		Task task7 = taskHelper.createTask(TaskType.removeUser);
		task7 = taskHelper.setUser(task7.getId(), user2.getId());
		executor.executeTask(task7.getId()).get();
		
		task7 = taskDao.get(task7.getId());
		assertTrue("Task must be succedeed", (task7.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must be deleted", userDao.exists(user2.getId()));
		resource2 = resourceDao.get(resource2.getId());
		assertFalse("Resource must not be booked", resource2.isBooked());
		assertTrue("Resource must still have the task", task7.equals(resource2.getTask()));
		assertNull("Resource must have no user", resource2.getUser());
		
		// recreate user2
		user2 = new User("user2");
		userDao.create(user2);	
		// remove it again
		Task task8 = taskHelper.createTask(TaskType.removeUser);
		task8 = taskHelper.setUser(task8.getId(), user2.getId());
		executor.executeTask(task8.getId()).get();
		
		task8 = taskDao.get(task8.getId());
		assertTrue("Task must be succedeed", (task8.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("User must be deleted", userDao.exists(user2.getId()));
		
		// allocate resource2 to user1
		Task task9 = taskHelper.createTask(TaskType.allocateResourceForUser);
		task9 = taskHelper.setUser(task9.getId(), userId);
		executor.executeTask(task9.getId()).get();
		task9 = taskDao.get(task9.getId());
		assertTrue("Task must be succedeed", task9.getStatus() == Task.Status.SUCCEEDED);
		// remove resource2
		Task task10 = taskHelper.createTask(TaskType.removeResource);
		task10 = taskHelper.setResource(task10.getId(), resource2.getId());
		user1 = userDao.get(userId);
		executor.executeTask(task10.getId()).get();
		task10 = taskDao.get(task10.getId());
		assertTrue("Task must be succedeed", (task10.getStatus() == Task.Status.SUCCEEDED));
		assertFalse("Resource must be deleted", userDao.exists(resource2.getId()));
		user1 = userDao.get(userId);
		assertTrue("User must still have the task", task10.equals(user1.getTask()));
		assertNull("User must have no resource", user1.getResource());
		assertFalse("User must not be booked", user1.isBooked());
	}

}
