package org.horiam.ResourceManager.services;

import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
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


public class TestServices {
	
	private String userId = "userA";
	private String resourceId = "resource1";
	private String taskId = "taskX";
	
	@EJB
	private UserDao userDao;
	@EJB
	private ResourceDao resourceDao;
	@EJB
	private TaskDao taskDao;
	@EJB
	private UserService userService;
	

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
	
	@Test
	public void testUserServiceAsAdmin() throws NamingException {
		System.out.println("\nTest UserService EJB as Admin...\n");	
		
		//UserService userService = (UserService) lookup("java:global/Beans/UserService!" + UserService.class.getName());
				
        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
        properties.put(Context.SECURITY_PRINCIPAL, "admin");
        properties.put(Context.SECURITY_CREDENTIALS, "super");

        //InitialContext context = new InitialContext(properties);
        
        try {
        	
        	//List<User> users = (List<User>) userService.list();
        	
        } finally {
            //context.close();
        }
	}
	
}
