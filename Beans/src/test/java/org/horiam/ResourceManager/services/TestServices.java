package org.horiam.ResourceManager.services;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class TestServices {
	
	private String userId     = "userA";
	private String resourceId = "resource1";
	private String taskId     = "taskX";
	
	@EJB
	private UserDao userDao;
	@EJB
	private ResourceDao resourceDao;
	@EJB
	private TaskDao taskDao;
	// Beans to test
	@EJB
	private UserService userService;
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
		taskDao.create(new Task(taskId));
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
	public static class AdminCaller {
		public Void call(Callable<Void> callable) throws Exception {
			return callable.call();
		}
	}
	
	@Stateless
	@RunAs("User")
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
	
}
