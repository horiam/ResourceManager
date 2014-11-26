package org.horiam.ResourceManager.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.horiam.ResourceManager.mock.ResourceMockService;
import org.horiam.ResourceManager.mock.TaskMockService;
import org.horiam.ResourceManager.mock.UserMockService;
import org.horiam.ResourceManager.model.Model;
import org.horiam.ResourceManager.model.ModelWithTask;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.webapp.soapful.ResourceWS;
import org.horiam.ResourceManager.webapp.soapful.TaskWS;
import org.horiam.ResourceManager.webapp.soapful.UserWS;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebServiceTest {
	
    @ArquillianResource
    private URL deployUrl;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).addClasses(UserWS.class, UserMockService.class, 
        		User.class, ModelWithTask.class, Model.class, ResourceWS.class, ResourceMockService.class, 
        		Resource.class, TaskWS.class, TaskMockService.class, Task.class);
    }
    	   
    @Test
    public void testUsersWS() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest UsersWS on URL=" + deployUrl.toString() + "...\n");	
    	  	
    	URL wsdlURL = new URL(deployUrl.toString() + "webservices/UserWS?wsdl");    
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
    	   	
    	User user = port.get(UserMockService.initialUserIds[0]);
    	assertTrue("Must be eaual", UserMockService.initialUsers[0].equals(user)); 
    	
    	assertTrue("Must exist", port.exists(UserMockService.initialUserIds[0])); 
    	   	
    	List<User> userList = port.list();
    	assertEquals("Must this length", userList.size(), UserMockService.initialUsers.length); 
    	for (User userIt : userList)
    		assertTrue("Must be a User", userIt instanceof User); 
    	
    	String newUserId = "newUser";
    	port.createOrUpdate(newUserId, new User(newUserId));
    	assertTrue("Must exist", port.exists(newUserId));
    	
    	port.delete("newUser");
    	assertFalse("Must not exist", port.exists("newUser"));
    	
    	Task taskAllocate = port.allocateUser(UserMockService.initialUserIds[0]);
    	assertEquals("Must be type", taskAllocate.getType(), "allocateResourceForUser");
    	
    	Task taskDeallocate = port.deallocateUser(UserMockService.initialUserIds[0]);
    	assertEquals("Must be type", taskDeallocate.getType(), "deallocateUser");
    	
    	Task taskRemove = port.removeUser(UserMockService.initialUserIds[0]);
    	assertEquals("Must be type", taskRemove.getType(), "removeUser");  
    	
    }
    
    @Test
    public void testResourceWS() throws URISyntaxException, MalformedURLException, 
    										ResourceManagerFault {    	
    	System.out.println("\nTest ResourceWS on URL=" + deployUrl.toString() + "...\n");
    	
    	URL wsdlURL = new URL(deployUrl.toString() + "webservices/ResourceWS?wsdl");    
    	QName serviceQName =  new QName("http://ResourceManager/wsdl", "ResourceWS");
    	
    	Service service = Service.create(wsdlURL, serviceQName);
    	ResourceSEI port = service.getPort(ResourceSEI.class);
    	
    	assertFalse("Must not exist", port.exists("UnknownResource"));
	   	/*
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownResource");
		} catch (ResourceManagerFault e) {
			System.out.println("Exception type="+e.getClass());
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	*/
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
    	System.out.println("\nTest TaskWS on URL=" + deployUrl.toString() + "...\n");
    	
    	URL wsdlURL = new URL(deployUrl.toString() + "webservices/TaskWS?wsdl");    
    	QName serviceQName =  new QName("http://ResourceManager/wsdl", "TaskWS");
    	
    	Service service = Service.create(wsdlURL, serviceQName);
    	TaskSEI port = service.getPort(TaskSEI.class);
    	
    	assertFalse("Must not exist", port.exists("UnknownTask"));
    	/*
    	boolean hasException = false;
    	try { // TODO
			port.get("UnknownTask");
		} catch (ResourceManagerFault e) {
			System.out.println("Exception type="+e.getClass());
			hasException = true;
		}
    	assertTrue("Must have exception", hasException);
    	*/
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
}
