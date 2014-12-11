package org.horiam.ResourceManager.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.ResourceSEI;
import org.horiam.ResourceManager.soap.TaskSEI;
import org.horiam.ResourceManager.soap.UserSEI;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SoapIT {

	private final static String deploy = "http://localhost:8081/Soapful/webservices/"; 


	private Object createClient(Class<?> clazz, String wsName, String user, String pass) {
		JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
    	clientFactory.setAddress(deploy + wsName); 
    	clientFactory.setServiceClass(clazz); 
    	clientFactory.setUsername(user); 
    	clientFactory.setPassword(pass);
    	clientFactory.getInInterceptors().add(new LoggingInInterceptor());
    	clientFactory.getInInterceptors().add(new LoggingOutInterceptor());
    	return clientFactory.create();  
	}
	
	@Ignore
	@Test
    public void testUsers() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest UsersWS on URL=" + deploy + "...\n");
    	
    	UserSEI port = (UserSEI) createClient(UserSEI.class, "UserWS", "admin", "super");
    
    	
    	assertFalse("Must not exist", port.exists("UnknownUser"));
	
    	boolean hasException = false;
    	try {
			port.get("UnknownUser");
		} catch (ResourceManagerFault e) {
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
	  	
     	String user1id = "user1";
     	User user1 = new User(user1id);
    	port.createOrUpdate(user1id, user1);
    	assertTrue("Must exist", port.exists(user1id));

     	String user2id = "user2";
     	User user2 = new User(user2id);
    	port.createOrUpdate(user2id, user2);
    	assertTrue("Must exist", port.exists(user2id));
    	   	
    	User user = port.get(user1id);
    	assertTrue("Must be eaual", user1.equals(user)); 
    	
    	List<User> userList = port.list();
    	assertEquals("Must this length", userList.size(), 2); 
    	for (User userIt : userList)
    		assertTrue("Must be a User", userIt instanceof User); 
    	
    	port.delete(user1id);
    	assertFalse("Must not exist", port.exists(user1id));
      	
    	port.delete(user2id);
    	assertFalse("Must not exist", port.exists(user2id));
   
    	
    }
    
	@Ignore
    @Test
    public void testResource() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest ResourceWS on URL=" + deploy + "...\n");
    	
       	ResourceSEI port = (ResourceSEI) createClient(ResourceSEI.class, "ResourceWS", "admin", "super");
    	

    	assertFalse("Must not exist", port.exists("UnknownResource"));
   	
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownResource");
		} catch (ResourceManagerFault e) {
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	
   		String resource1id = "resource1";
    	Resource resource1 = new Resource(resource1id);
    	port.createOrUpdate(resource1id, resource1);
    	assertTrue("Must exist", port.exists(resource1id));
    	
    	String resource2id = "resource2";
    	Resource resource2 = new Resource(resource2id);
    	port.createOrUpdate(resource2id, resource2);
    	assertTrue("Must exist", port.exists(resource2id));
    	
    	
    	Resource resource = port.get(resource1id);
    	assertTrue("Must be eaual", resource1.equals(resource));
    	
    	List<Resource> resourceList = port.list();
    	assertEquals("Must this length", resourceList.size(), 2); 
    	for (Resource resourceIt : resourceList)
    		assertTrue("Must be a Resource", resourceIt instanceof Resource); 
    	
    	port.delete(resource1id);
    	assertFalse("Must not exist", port.exists(resource1id));
    	
    	port.delete(resource2id);
    	assertFalse("Must not exist", port.exists(resource2id));
    	
    }
    
    @Test(timeout = 30000)
    public void testTask() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest TaskWS on URL=" + deploy + "...\n");
    	
    	TaskSEI port = (TaskSEI) createClient(TaskSEI.class, "TaskWS", "admin", "super"); 
    	
    	UserSEI userWS = (UserSEI) createClient(UserSEI.class, "UserWS", "admin", "super");
    	
       	ResourceSEI resourceWS = (ResourceSEI) createClient(ResourceSEI.class, "ResourceWS", "admin", "super");

    	assertFalse("Must not exist", port.exists("UnknownTask"));
    /*	
    	boolean hasException = false;
    	try { 
			port.get("UnknownTask");
		} catch (ResourceManagerFault e) {
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    */
    	
    	String user1id = "user1";
     	User user1 = new User(user1id);
    	userWS.createOrUpdate(user1id, user1);
    	assertTrue("Must exist", userWS.exists(user1id));

     	String user2id = "user2";
     	User user2 = new User(user2id);
    	userWS.createOrUpdate(user2id, user2);
    	assertTrue("Must exist", userWS.exists(user2id));
    
    	
    	// Populate with Users and Resources
    	String resource1id = "resource1";
    	Resource resource1 = new Resource(resource1id);
    	resourceWS.createOrUpdate(resource1id, resource1);
    	assertTrue("Must exist", resourceWS.exists(resource1id));
    	
    	/*
    	 *  allocateUser User1
    	 *  check Task
    	 *  wait Task
    	 *  check that Resource1 allocated to User1
    	 *  
    	 *  allocate User2
    	 *  wait Task 
    	 *  check Task : must be failed
    	 *  check User2 and Resource1 must be untouched
    	 *  
    	 *  remove User1
    	 *  wait Task
    	 *  check Task 
    	 *  check User1 does not exist
    	 *  check that Resource1 exists and free
    	 *  
    	 *  allocate User2
    	 *  wait Task
    	 *  check Resource1 associated with User1
    	 *  
    	 *  list Tasks
    	 *  exists Tasks
    	 *  remove all Tasks 
    	 *  
    	 */
    	
    	Task allocateUser1Task = userWS.allocateUser(user1id);
    	assertEquals("Must be type", allocateUser1Task.getType(), "allocateResourceForUser");
    	assertEquals("Must be the same id", allocateUser1Task.getUser().getId(), user1id);

    	while (allocateUser1Task.getStatus() == Task.Status.PROCESSING)
    		allocateUser1Task = port.get(allocateUser1Task.getId());

    	assertEquals("Must be SUCCEEDED", allocateUser1Task.getStatus(), Task.Status.SUCCEEDED);
    	assertEquals("Must have this Resource", allocateUser1Task.getResource().getId(), resource1id);
    	
    	user1 = userWS.get(user1id);
    	assertEquals("Must have this Resource", user1.getResource().getId(), resource1id);
    	resource1 = resourceWS.get(resource1id);
    	assertEquals("Must have this User", resource1.getUser().getId(), user1id);
    	
    	/*
    	
    	Task taskAllocate = port.allocateUser(user1id);
    	assertEquals("Must be type", taskAllocate.getType(), "allocateResourceForUser");
    	// TODO WAIT and check if task is complete
    	Task taskDeallocate = port.deallocateUser(user1id);
    	assertEquals("Must be type", taskDeallocate.getType(), "deallocateUser");
    	// TODO WAIT and check if task is complete
    	Task taskRemove = port.removeUser(user1id);
    	assertEquals("Must be type", taskRemove.getType(), "removeUser");  
    	// TODO WAIT and check if task is complete
    	assertFalse("Must not exist", port.exists(user1id));
    	
    	*/
    /*	
    	String resource2id = "resource2";
    	Resource resource2 = new Resource(resource2id);
    	port.createOrUpdate(resource2id, resource2);
    	assertTrue("Must exist", port.exists(resource2id));
    	*/
    /*	
    	Task task = port.get(TaskMockService.initialTaskIds[0]);
    	assertTrue("Must be eaual", TaskMockService.initialTasks[0].equals(task));
    	
    	assertTrue("Must exist", port.exists(TaskMockService.initialTaskIds[0])); 
    	
    	List<Task> resourceList = port.list();
    	assertEquals("Must this length", resourceList.size(), TaskMockService.initialTasks.length); 
    	for (Task taskIt : resourceList)
    		assertTrue("Must be a Task", taskIt instanceof Task);
    	
    	port.delete(TaskMockService.initialTaskIds[1]);
    	assertFalse("Must not exist", port.exists(TaskMockService.initialTaskIds[1]));
    	
    	Task taskRemove = port.removeResource(ResourceMockService.initialResourceIds[0]);
    	assertEquals("Must be type", taskRemove.getType(), "removeResource"); 
    */
    }
	
}
