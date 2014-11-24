package org.horiam.ResourceManager.services;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;

import org.apache.openejb.core.security.jaas.LoginProvider;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestServicesAuth {
	
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
	@EJB
	private ResourceService resourceService;
	@EJB
	private TaskService taskService;
	
	
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
	
    private Context getContext(String user, String pass) throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
        p.put(Context.SECURITY_PRINCIPAL, user);
        p.put(Context.SECURITY_CREDENTIALS, pass);
        return new InitialContext(p);      
    }

    @Test
    public void testAsAdmin() throws Exception {
    	System.out.println("\nTest call as Admin...\n");	
        final Context context = getContext("admin", "super");        
        try {       	
        	userService.get(userId);
        	resourceService.get(resourceId);
        	taskService.get(taskId);
       	
        } finally {
        	context.close();
        }
    }
    
    @Test
    public void testAsUserA() throws Exception {
    	System.out.println("\nTest call as userA...\n");
    	setUserForResource();
        final Context context = getContext(userId, userId);        
        try {       	
        	userService.get(userId);
        	resourceService.get(resourceId);	
        	
        } finally {
        	context.close();
        }
    }
    
    private void setUserForResource() throws RecordNotFoundException {
    	Resource resource = resourceDao.get(resourceId);
    	User user = userDao.get(userId);
    	resource.setUser(user);
    	user.setResource(resource);
    	resourceDao.update(resource);
    	userDao.update(user);
    }
    
    @Test
    public void testAsUserB() throws Exception {
    	System.out.println("\nTest call as userB...\n");
    	setUserForResource();
        final Context context = getContext("userB", "userB");    
        try {
	        boolean hasException = false;
	        try {	        	
	        	userService.get(userId);
	        	
	        } catch (Exception e) {
	        	hasException = true;
	        }       
	        assertTrue("UserB must have exception", hasException);
	        
	        hasException = false;
	        try {	        	
	        	resourceService.get(resourceId);
	        	
	        } catch (Exception e) {
	        	hasException = true;
	        }       
	        assertTrue("UserB must have exception", hasException);
	        
	        hasException = false;
	        try {	        	
	        	taskService.get(taskId);
	        	
	        } catch (Exception e) {
	        	hasException = true;
	        }       
	        assertTrue("UserB must have exception", hasException);
	        
        } finally {
        	context.close();
        }
    }
    
    
}
