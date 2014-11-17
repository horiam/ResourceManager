package org.horiam.ResourceManager.services;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.test.ContainerWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestServices  extends ContainerWrapper {
	
	private String userId = "userA";
	private String resourceId = "resource1";
	private String taskId = "taskX";
	
	private static UserDao userDao;
	private static ResourceDao resourceDao;
	private static TaskDao taskDao;
	

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
	public void testUserServiceAsAdmin() throws NamingException {
		System.out.println("\nTest UserService EJB as Admin...\n");	
		
		UserService userService = (UserService) lookup("java:global/Beans/UserService!" + UserService.class.getName());
				
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
        properties.put(Context.SECURITY_PRINCIPAL, "admin");
        properties.put(Context.SECURITY_CREDENTIALS, "super");

        //InitialContext context = new InitialContext(properties);
        
        try {
        	
        	//List<User> users = (List<User>) userService.list();
        	
        } finally {
            context.close();
        }
	}
	
}
