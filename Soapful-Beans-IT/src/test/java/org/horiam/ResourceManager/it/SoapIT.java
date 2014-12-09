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
import javax.xml.ws.Service;

import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.ResourceSEI;
import org.horiam.ResourceManager.soap.TaskSEI;
import org.horiam.ResourceManager.soap.UserSEI;
import org.junit.Test;

public class SoapIT {

	private final static String deploy = "http://localhost:8081/Soapful/"; 
	
	@Test
    public void testUsersWS() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest UsersWS on URL=" + deploy + "...\n");
    	
    	Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("admin", "super".toCharArray());
			}
		});
    	  	
    	URL wsdlURL = new URL(deploy + "webservices/UserWS?wsdl");    
    	QName serviceQName =  new QName("http://ResourceManager/wsdl", "UserWS");
    	
    	Service service = Service.create(wsdlURL, serviceQName);
    	UserSEI port = service.getPort(UserSEI.class);
    	
    	assertFalse("Must not exist", port.exists("UnknownUser"));
    	/*  	
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownUser");
		} catch (ResourceManagerFault e) {
			System.out.println("Exception type="+e.getClass());
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	*/
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
    	   	
    	port.delete(user2id);
    	assertFalse("Must not exist", port.exists(user2id));
   
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
    }
    
	/*
	
    @Test
    public void testResourceWS() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest ResourceWS on URL=" + deploy + "...\n");
    	
    	URL wsdlURL = new URL(deploy + "webservices/ResourceWS?wsdl");    
    	QName serviceQName =  new QName("http://ResourceManager/wsdl", "ResourceWS");
    	
    	Service service = Service.create(wsdlURL, serviceQName);
    	ResourceSEI port = service.getPort(ResourceSEI.class);
    	
    	assertFalse("Must not exist", port.exists("UnknownResource"));
    	
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownResource");
		} catch (ResourceManagerFault e) {
			System.out.println("Exception type="+e.getClass());
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	
    	Resource resource = port.get(ResourceMockService.initialResourceIds[0]);
    	assertTrue("Must be eaual", ResourceMockService.initialResources[0].equals(resource));
    	
    	assertTrue("Must exist", port.exists(ResourceMockService.initialResourceIds[0])); 
    	
    	List<Resource> resourceList = port.list();
    	assertEquals("Must this length", resourceList.size(), ResourceMockService.initialResources.length); 
    	for (Resource resourceIt : resourceList)
    		assertTrue("Must be a Resource", resourceIt instanceof Resource); 
    	
    	String newResourceId = "NewResource";
    	port.createOrUpdate(newResourceId, new Resource(newResourceId));
    	assertTrue("Must exist", port.exists(newResourceId));
    	
    	port.delete("NewResource");
    	assertFalse("Must not exist", port.exists("NewResource"));
    	
    	Task taskRemove = port.removeResource(ResourceMockService.initialResourceIds[0]);
    	assertEquals("Must be type", taskRemove.getType(), "removeResource"); 
    }
    
    @Test
    public void testTaskWS() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest TaskWS on URL=" + deploy + "...\n");
    	
    	URL wsdlURL = new URL(deploy + "webservices/TaskWS?wsdl");    
    	QName serviceQName =  new QName("http://ResourceManager/wsdl", "TaskWS");
    	
    	Service service = Service.create(wsdlURL, serviceQName);
    	TaskSEI port = service.getPort(TaskSEI.class);
    	
    	assertFalse("Must not exist", port.exists("UnknownTask"));
    	
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownTask");
		} catch (ResourceManagerFault e) {
			System.out.println("Exception type="+e.getClass());
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	
    	Task task = port.get(TaskMockService.initialTaskIds[0]);
    	assertTrue("Must be eaual", TaskMockService.initialTasks[0].equals(task));
    	
    	assertTrue("Must exist", port.exists(TaskMockService.initialTaskIds[0])); 
    	
    	List<Task> resourceList = port.list();
    	assertEquals("Must this length", resourceList.size(), TaskMockService.initialTasks.length); 
    	for (Task taskIt : resourceList)
    		assertTrue("Must be a Task", taskIt instanceof Task);
    	
    	port.delete(TaskMockService.initialTaskIds[1]);
    	assertFalse("Must not exist", port.exists(TaskMockService.initialTaskIds[1]));
    }

    */
}
