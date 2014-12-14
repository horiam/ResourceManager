package org.horiam.ResourceManager.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.ResourceSEI;
import org.horiam.ResourceManager.soap.TaskSEI;
import org.horiam.ResourceManager.soap.UserSEI;
import org.junit.Test;


public class SoapIT {

	private final static String deploy = "http://localhost:8081/ResourceManager/webservices/"; 


	private Object createClient(Class<?> clazz, String wsName, String user, String pass) {
		JaxWsProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
    	clientFactory.setAddress(deploy + wsName); 
    	clientFactory.setServiceClass(clazz); 
    	clientFactory.setUsername(user); 
    	clientFactory.setPassword(pass);
//    	clientFactory.getInInterceptors().add(new LoggingInInterceptor());
//    	clientFactory.getInInterceptors().add(new LoggingOutInterceptor());
    	return clientFactory.create();  
	}
	
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
    
    @Test
    public void testResources() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest ResourceWS on URL=" + deploy + "...\n");
    	
       	ResourceSEI port = (ResourceSEI) createClient(ResourceSEI.class, "ResourceWS", "admin", "super");
    	
    	assertFalse("Must not exist", port.exists("UnknownResource"));
   	
    	boolean hasException = false;
    	try { 
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
    public void testTasks() throws URISyntaxException, MalformedURLException, 
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
    	
    	
    	//  allocateUser User1
    	Task allocateUser1Task = userWS.allocateUser(user1id);
   	 	//  check Task
    	assertEquals("Must be type", allocateUser1Task.getType(), "allocateResourceForUser");
    	assertEquals("Must be the same id", allocateUser1Task.getUser().getId(), user1id);
    	//  wait Task
    	while (allocateUser1Task.getStatus() == Task.Status.PROCESSING)
    		allocateUser1Task = port.get(allocateUser1Task.getId());
    	// check that Resource1 allocated to User1
    	assertEquals("Must be SUCCEEDED", allocateUser1Task.getStatus(), Task.Status.SUCCEEDED);
    	assertEquals("Must have this Resource", allocateUser1Task.getResource().getId(), resource1id);
    	assertEquals("Must have this User", allocateUser1Task.getUser().getId(), user1id);

    	user1 = userWS.get(user1id);
    	assertEquals("Must have this Resource", user1.getResource().getId(), resource1id);
    	resource1 = resourceWS.get(resource1id);
    	assertEquals("Must have this User", resource1.getUser().getId(), user1id);
    	
    	// allocate User2
    	Task allocateUser2Task = userWS.allocateUser(user2id);
    	// wait Task 
    	while (allocateUser2Task.getStatus() == Task.Status.PROCESSING)
    		allocateUser2Task = port.get(allocateUser2Task.getId());
    	// check Task : must be failed
    	assertEquals("Must be FAILED", allocateUser2Task.getStatus(), Task.Status.FAILED);
    	assertNull("Must not have a Resource", allocateUser2Task.getResource());
    	assertEquals("Must have this User", allocateUser2Task.getUser().getId(), user2id);
    	// check User2 and Resource1 must be untouched
    	user2 = userWS.get(user2id);
    	assertNull("Must not have a Resource", user2.getResource());
    	resource1 = resourceWS.get(resource1id);
    	assertFalse("Must not have this User", resource1.getUser().getId().equals(user2id));
 
    	// remove User1
    	Task removeUser1Task = userWS.removeUser(user1id);
    	// wait Task    	
    	while (removeUser1Task.getStatus() == Task.Status.PROCESSING)
    		removeUser1Task = port.get(removeUser1Task.getId());
    	// check Task 
    	assertEquals("Must be SUCCEEDED", removeUser1Task.getStatus(), Task.Status.SUCCEEDED);
    	// check User1 does not exist
    	assertFalse("Must not exist", userWS.exists(user1id));
    	// check that Resource1 exists and free
    	resource1 = resourceWS.get(resource1id);
    	assertNull("Must not have a User", resource1.getUser());
    	
    	// allocate User2
    	Task allocateUser2Task2 = userWS.allocateUser(user2id);
    	// wait Task 
    	while (allocateUser2Task2.getStatus() == Task.Status.PROCESSING)
    		allocateUser2Task2 = port.get(allocateUser2Task2.getId());
    	// check succeeded
    	assertEquals("Must be SUCCEEDED", allocateUser2Task2.getStatus(), Task.Status.SUCCEEDED);
    	// check Resource1 associated with User2
    	user2 = userWS.get(user2id);
    	assertEquals("Must have this Resource", user2.getResource().getId(), resource1id);
    	resource1 = resourceWS.get(resource1id);
    	assertEquals("Must have this User", resource1.getUser().getId(), user2id);
    
    	// remove resource 
    	Task removeResource1Task = resourceWS.removeResource(resource1id);
    	// wait
    	while (removeResource1Task.getStatus() == Task.Status.PROCESSING)
    		removeResource1Task = port.get(removeResource1Task.getId());
    	// check succeeded
    	assertEquals("Must be SUCCEEDED", removeResource1Task.getStatus(), Task.Status.SUCCEEDED);
    	// check Resource1 not associated with User2
    	user2 = userWS.get(user2id);
    	assertNull("Must not have a Resource", user2.getResource());
    	// check Resource1 does not exist
    	assertFalse("Must not exist", resourceWS.exists(resource1id));
    	
    	// list Tasks
    	List<Task> taskList = port.list();
    	assertEquals("Must this length", taskList.size(), 5); 
    	for (Task taskIt : taskList) {
    		assertTrue("Must be a Task", taskIt instanceof Task);
    		assertTrue("Must exist", port.exists(taskIt.getId()));
    		port.delete(taskIt.getId());
    		assertFalse("Must not exist", port.exists(taskIt.getId()));
    	}	
    	
    	//clean DB
    	userWS.delete(user2id);
    }
	
}
