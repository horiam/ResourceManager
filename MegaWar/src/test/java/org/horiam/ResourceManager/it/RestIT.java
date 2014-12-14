package org.horiam.ResourceManager.it;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.User;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;


public class RestIT {

	private final static String deploy = "http://localhost:8081/ResourceManager/rest/"; 
	
	private WebResource createClient(String user, String pass) {
		
		ClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(config);
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(
																	user, pass));
		return  client.resource(deploy);
	}
	
	
	@Test
	public void testUsers() throws NamingException, JMSException {
		System.out.println("\nTest UserRest on URL=" + deploy + "...\n");
    	
		WebResource client = createClient("admin", "super");
		client = client.path("users");  
		
		
    	ClientResponse resp404 = client.path("UnknownUser").get(ClientResponse.class);
    	assertEquals("Must be", resp404.getStatus(), 404); 

     	String user1id = "user1";
     	User user1 = new User(user1id);
    	ClientResponse resp201 = client.path(user1id).put(ClientResponse.class, user1);
    	assertEquals("Must be", resp201.getStatus(), 201); 

    	ClientResponse respUser1 = client.path(user1id).get(ClientResponse.class);
    	assertEquals("Must be", respUser1.getStatus(), 200); 
    	assertEquals("Must be", respUser1.getEntity(User.class), user1); 

      	String user2id = "user2";
     	User user2 = new User(user2id);
     	resp201 = client.path(user2id).put(ClientResponse.class, user2);
    	assertEquals("Must be", resp201.getStatus(), 201); 

    	ClientResponse respUser2 = client.path(user2id).get(ClientResponse.class);
    	assertEquals("Must be", respUser2.getStatus(), 200); 
    	assertEquals("Must be", respUser2.getEntity(User.class), user2);    
    	
    	// JMS
    	JmsAssertion<User> jms = new JmsAssertion<User>("jms/usersQueue");
    	jms.assertReceive(user2);
    	
    	ClientResponse respList = client.get(ClientResponse.class); 
    	assertEquals("Must be", respList.getStatus(), 200); 
    	List<User> userList = respList.getEntity(new GenericType<List<User>>(){});
    	assertEquals("Must this length", userList.size(), 2); 
    	for (User userIt : userList)
    		assertTrue("Must be a User", userIt instanceof User); 
  
    	// JMS
    	jms.assertSize(2);
    	
    	ClientResponse respDelUser1 = client.path(user1id).delete(ClientResponse.class);
    	assertEquals("Must be", respDelUser1.getStatus(), 200); 
    	respUser1 = client.path(user1id).get(ClientResponse.class);
    	assertEquals("Must be", respUser1.getStatus(), 404); 
    	
    	ClientResponse respDelUser2 = client.path(user2id).delete(ClientResponse.class);
    	assertEquals("Must be", respDelUser2.getStatus(), 200); 
    	respUser2 = client.path(user2id).get(ClientResponse.class);
    	assertEquals("Must be", respUser2.getStatus(), 404); 
	}	

	@Test
	public void testResources() throws JMSException, NamingException {
		System.out.println("\nTest ResourceRest on URL=" + deploy + "...\n");
    	
		WebResource client = createClient("admin", "super");
		client = client.path("resources");  
		
		
    	ClientResponse resp404 = client.path("UnknownResource").get(ClientResponse.class);
    	assertEquals("Must be", resp404.getStatus(), 404); 

     	String resource1id = "resource1";
     	Resource resource1 = new Resource(resource1id);
    	ClientResponse resp201 = client.path(resource1id).put(ClientResponse.class, resource1);
    	assertEquals("Must be", resp201.getStatus(), 201); 

    	ClientResponse respResource1 = client.path(resource1id).get(ClientResponse.class);
    	assertEquals("Must be", respResource1.getStatus(), 200); 
    	assertEquals("Must be", respResource1.getEntity(Resource.class), resource1); 

      	String resource2id = "resource2";
     	Resource resource2 = new Resource(resource2id);
     	resp201 = client.path(resource2id).put(ClientResponse.class, resource2);
    	assertEquals("Must be", resp201.getStatus(), 201); 
    	
    	// JMS
    	JmsAssertion<Resource> jms = new JmsAssertion<Resource>("jms/resourcesQueue");
    	jms.assertReceive(resource2);
  
    	ClientResponse respResource2 = client.path(resource2id).get(ClientResponse.class);
    	assertEquals("Must be", respResource2.getStatus(), 200); 
    	assertEquals("Must be", respResource2.getEntity(Resource.class), resource2);    	
    
    	ClientResponse respList = client.get(ClientResponse.class); 
    	assertEquals("Must be", respList.getStatus(), 200); 
    	List<Resource> resourceList = respList.getEntity(new GenericType<List<Resource>>(){});
    	assertEquals("Must this length", resourceList.size(), 2); 
    	for (Resource resourceIt : resourceList)
    		assertTrue("Must be a Resource", resourceIt instanceof Resource); 
   
    	// JMS
    	jms.assertSize(2);

    	ClientResponse respDelResource1 = client.path(resource1id).delete(ClientResponse.class);
    	assertEquals("Must be", respDelResource1.getStatus(), 200); 
    	respResource1 = client.path(resource1id).get(ClientResponse.class);
    	assertEquals("Must be", respResource1.getStatus(), 404); 
    	
    	ClientResponse respDelResource2 = client.path(resource2id).delete(ClientResponse.class);
    	assertEquals("Must be", respDelResource2.getStatus(), 200); 
    	respResource2 = client.path(resource2id).get(ClientResponse.class);
    	assertEquals("Must be", respResource2.getStatus(), 404); 
	}   


    @Test(timeout = 30000)
    public void testTasks() throws JMSException, NamingException {    	
    	System.out.println("\nTest TaskRest on URL=" + deploy + "...\n");
    
   		WebResource client = createClient("admin", "super");
		WebResource clientTasks = client.path("tasks");  
		WebResource	clientUsers = client.path("users");  
		WebResource	clientResources = client.path("resources");  
    	
    	ClientResponse resp404 = clientTasks.path("UnknownTask").get(ClientResponse.class);
    	assertEquals("Must be", resp404.getStatus(), 404); 
    
      	String user1id = "user1";
     	User user1 = new User(user1id);
    	ClientResponse resp201 = clientUsers.path(user1id).put(ClientResponse.class, user1);
    	assertEquals("Must be", resp201.getStatus(), 201); 

    	ClientResponse respUser1 = clientUsers.path(user1id).get(ClientResponse.class);
    	assertEquals("Must be", respUser1.getStatus(), 200); 
    	assertEquals("Must be", respUser1.getEntity(User.class), user1); 

      	String user2id = "user2";
     	User user2 = new User(user2id);
     	resp201 = clientUsers.path(user2id).put(ClientResponse.class, user2);
    	assertEquals("Must be", resp201.getStatus(), 201); 

     	String resource1id = "resource1";
     	Resource resource1 = new Resource(resource1id);
    	resp201 = clientResources.path(resource1id).put(ClientResponse.class, resource1);
    	assertEquals("Must be", resp201.getStatus(), 201); 

    	// JMS Task Topic
    	TaskObserverAssertion to = new TaskObserverAssertion();
    	to.init();
    	
    	//  allocateUser User1
    	ClientResponse respAllocUser1 = clientUsers.path(user1id).queryParam("action", "allocate")
    																.post(ClientResponse.class);
    	assertEquals("Must be", respAllocUser1.getStatus(), 200); 
   	 	//  check Task
    	Task allocUser1 = respAllocUser1.getEntity(Task.class);
    	assertEquals("Must be type", allocUser1.getType(), "allocateResourceForUser");
    	assertEquals("Must be the same id", allocUser1.getUser().getId(), user1id);
    	//  wait Task
    	while (allocUser1.getStatus() == Task.Status.PROCESSING)
    		allocUser1 = clientTasks.path(allocUser1.getId()).get(Task.class); 
    	// check that Resource1 allocated to User1
    	assertEquals("Must be SUCCEEDED", allocUser1.getStatus(), Task.Status.SUCCEEDED);
    	assertEquals("Must have this Resource", allocUser1.getResource().getId(), resource1id);
    	assertEquals("Must have this User", allocUser1.getUser().getId(), user1id);
    	
    	// JMS Task Topic
    	to.assertTask(allocUser1);

    	user1 = clientUsers.path(user1id).get(User.class);
    	assertEquals("Must have this Resource", user1.getResource().getId(), resource1id);
    	resource1 = clientResources.path(resource1id).get(Resource.class);
    	assertEquals("Must have this User", resource1.getUser().getId(), user1id);
    	
    	// allocate User2
    	ClientResponse respAllocUser2 = clientUsers.path(user2id).queryParam("action", "allocate")
    																.post(ClientResponse.class);
    	assertEquals("Must be", respAllocUser2.getStatus(), 200); 
   	 	//  check Task
    	Task allocUser2 = respAllocUser2.getEntity(Task.class);
    	assertEquals("Must be type", allocUser2.getType(), "allocateResourceForUser");
    	assertEquals("Must be the same id", allocUser2.getUser().getId(), user2id);
    	//  wait Task
    	while (allocUser2.getStatus() == Task.Status.PROCESSING)
    		allocUser2 = clientTasks.path(allocUser2.getId()).get(Task.class); 
    	// check Task : must be failed
    	assertEquals("Must be FAILED", allocUser2.getStatus(), Task.Status.FAILED);
    	assertNull("Must not have a Resource", allocUser2.getResource());
    	assertEquals("Must have this User", allocUser2.getUser().getId(), user2id);
    	// check User2 and Resource1 must be untouched
    	user2 = clientUsers.path(user2id).get(User.class);
    	assertNull("Must not have a Resource", user2.getResource());
    	resource1 = clientResources.path(resource1id).get(Resource.class);
    	assertFalse("Must not have this User", resource1.getUser().getId().equals(user2id));

    	// remove User1
    	ClientResponse respRmUser1 = clientUsers.path(user1id).queryParam("action", "remove")
    																.post(ClientResponse.class);
    	assertEquals("Must be", respRmUser1.getStatus(), 200); 
    	Task removeUser1Task = respRmUser1.getEntity(Task.class);
    	// wait Task    	
    	while (removeUser1Task.getStatus() == Task.Status.PROCESSING)
    		removeUser1Task = clientTasks.path(removeUser1Task.getId()).get(Task.class);
    	// check Task 
    	assertEquals("Must be SUCCEEDED", removeUser1Task.getStatus(), Task.Status.SUCCEEDED);
    	// check User1 does not exist
    	assertEquals("Must not exist", 404, clientUsers.path(user1id).get(ClientResponse.class)
    																			.getStatus());
    	// check that Resource1 exists and free
    	resource1 = clientResources.path(resource1id).get(Resource.class);
    	assertNull("Must not have a User", resource1.getUser());
    
    	
    	// allocateUser User2
    	ClientResponse respAllocUser2b = clientUsers.path(user2id).queryParam("action", "allocate")
    																.post(ClientResponse.class);
    	assertEquals("Must be", respAllocUser2b.getStatus(), 200); 
   	 	//  check Task
    	Task allocUser2b = respAllocUser2b.getEntity(Task.class);
    	assertEquals("Must be type", allocUser2b.getType(), "allocateResourceForUser");
    	assertEquals("Must be the same id", allocUser2b.getUser().getId(), user2id);
    	//  wait Task
    	while (allocUser2b.getStatus() == Task.Status.PROCESSING)
    		allocUser2b = clientTasks.path(allocUser2b.getId()).get(Task.class); 
    	// check that Resource1 allocated to User2
    	assertEquals("Must be SUCCEEDED", allocUser2b.getStatus(), Task.Status.SUCCEEDED);
    	assertEquals("Must have this Resource", allocUser2b.getResource().getId(), resource1id);
    	assertEquals("Must have this User", allocUser2b.getUser().getId(), user2id);

    	user2 = clientUsers.path(user2id).get(User.class);
    	assertEquals("Must have this Resource", user2.getResource().getId(), resource1id);
    	resource1 = clientResources.path(resource1id).get(Resource.class);
    	assertEquals("Must have this User", resource1.getUser().getId(), user2id);
    	
    	// remove resource 
    	ClientResponse restRmRes1 = clientResources.path(resource1id).queryParam("action", "remove")
    																.post(ClientResponse.class);
    	assertEquals("Must be", restRmRes1.getStatus(), 200); 
    	Task removeResource1Task = restRmRes1.getEntity(Task.class);
    	// wait
    	while (removeResource1Task.getStatus() == Task.Status.PROCESSING)
    		removeResource1Task = clientTasks.path(removeResource1Task.getId()).get(Task.class); 
    	// check succeeded
    	assertEquals("Must be SUCCEEDED", removeResource1Task.getStatus(), Task.Status.SUCCEEDED);
    	// check Resource1 not associated with User2
    	user2 = clientUsers.path(user2id).get(User.class);
    	assertNull("Must not have a Resource", user2.getResource());
    	// check Resource1 does not exist
    	assertEquals("Must not exist", 404, clientResources.path(resource1id).get(ClientResponse.class)
    																			.getStatus());
    	// JMS
    	JmsAssertion<Task> jmsTasks = new JmsAssertion<Task>("jms/tasksQueue");
    	jmsTasks.assertReceive(removeResource1Task);

     	// list Tasks
    	List<Task> taskList = clientTasks.get(new GenericType<List<Task>>(){});
    	assertEquals("Must this length", taskList.size(), 5); 
    	
    	// JMS
    	jmsTasks.assertSize(5);
    	
    	for (Task taskIt : taskList) {
    		assertTrue("Must be a Task", taskIt instanceof Task);
			assertEquals("Must exist",
					clientTasks.path(taskIt.getId()).get(ClientResponse.class)
							.getStatus(), 200);
			clientTasks.path(taskIt.getId()).delete();
			assertEquals("Must not exist", clientTasks.path(taskIt.getId())
					.get(ClientResponse.class).getStatus(), 404);
    	}	
    	
    	//clean DB
    	clientUsers.path(user2id).delete();
    }

}
