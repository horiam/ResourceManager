package org.horiam.ResourceManager.services;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;

import org.apache.openejb.core.security.jaas.LoginProvider;

import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestSercvicesWithUser {
	
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
	
    private Context getContext(String user, String pass) throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
        p.setProperty("openejb.authentication.realmName", "ServiceProviderLogin");
        p.put(Context.SECURITY_PRINCIPAL, user);
        p.put(Context.SECURITY_CREDENTIALS, pass);
        return new InitialContext(p);      
    }
        
    public static class TestLoginModule implements LoginProvider {

        @Override
        public List<String> authenticate(String user, String password) throws FailedLoginException {
            if (user.startsWith("user") && password.equals(user)) {
                return Arrays.asList("User");
            }

            if ("Admin".equals(user) && "super".equals(password)) {
                return Arrays.asList("Admin");
            }

            throw new FailedLoginException("Bad user or password!");
        }
    }
    
    
    @Test
    public void testAsUser() throws Exception {
        final Context context = getContext(userId, userId);        
        try {
        	userService.get(userId);
        	
        } finally {
        	context.close();
        }
    }
}
