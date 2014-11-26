package org.horiam.ResourceManager.webapp.restful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.net.URL;

import javax.enterprise.inject.Model;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.mock.ResourceMockService;
import org.horiam.ResourceManager.mock.TaskMockService;
import org.horiam.ResourceManager.mock.UserMockService;
import org.horiam.ResourceManager.model.ModelWithTask;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

import org.horiam.ResourceManager.webapp.restful.UsersResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Collection;


@RunWith(Arquillian.class)
public class TestResources {
	
	    @ArquillianResource
	    private URL deployUrl;
	
	    @Deployment
	    public static WebArchive createDeployment() {
	        return ShrinkWrap.create(WebArchive.class).addClasses(UsersResource.class, UserMockService.class, 
	        		User.class, ModelWithTask.class, Model.class, RecordNotFoundException.class, ResourcesResource.class, 
	        		ResourceMockService.class, Resource.class, TasksResource.class, TaskMockService.class);
	    }
	   
	    @Test
	    public void testUsersResource() throws URISyntaxException {    	
	    	System.out.println("\nTest UsersResource on URL=" + deployUrl.toString() + "...\n");	   	
    	
    	WebClient webClient = WebClient.create(deployUrl.toString() + "users"); 
    	
    	Response resp404 = webClient.path("UnknownUser").get();
    	assertEquals("Must be", resp404.getStatus(), 404); 
    	
    	webClient.replacePath(UserMockService.initialUserIds[0]);    	
    	User user = webClient.get(User.class);
    	assertTrue("Must be eaual", UserMockService.initialUsers[0].equals(user)); 
    	
    	webClient.replacePath("");   	   	
    	Collection<? extends User> userList = webClient.getCollection(User.class);    	
    	assertEquals("Must this length", userList.size(), UserMockService.initialUsers.length); 
    	for (User userIt : userList)
    		assertTrue("Must be a User", userIt instanceof User); 
    	   	    	
    	Response resp201 = webClient.path("newUser").put(new User("newUser"));
    	assertEquals("Must be", resp201.getStatus(), 201); 
    	    	
    	webClient.replacePath(UserMockService.initialUserIds[1]);   	
    	Response resp200 = webClient.delete();
    	assertEquals("Must be", resp200.getStatus(), 200);
    	
    	webClient.replacePath(UserMockService.initialUserIds[0]);
    	Task taskAllocate = webClient.query("action", "allocate").post(null, Task.class);
    	assertEquals("Must be type", taskAllocate.getType(), "allocateResourceForUser");
    	
    	webClient.replaceQueryParam("action", "deallocate");
    	Task taskDeallocate = webClient.post(null, Task.class);
    	assertEquals("Must be type", taskDeallocate.getType(), "deallocateUser");
    	
    	webClient.replaceQueryParam("action", "UnknownAction");
    	Response resp400 = webClient.post(null);
    	assertEquals("Must be", resp400.getStatus(), 400);    	
    	
    	webClient.replaceQueryParam("action", "remove");
    	Task taskRemove = webClient.post(null, Task.class);
    	assertEquals("Must be type", taskRemove.getType(), "removeUser");  
    	
    	webClient.replacePath("NoUser");
    	webClient.replaceQueryParam("action", "allocate");
    	Response resp404b = webClient.post(null);
    	assertEquals("Must be", resp404b.getStatus(), 404);  
    	
    	webClient.resetQuery();
    	Response resp400b = webClient.post(null);
    	assertEquals("Must be", resp400b.getStatus(), 400);  
    }
       
    @Test
    public void testResourcesResource() throws URISyntaxException {
    	System.out.println("\nTest UsersResource on URL=" + deployUrl.toString() + "...\n");	
    	
    	WebClient webClient = WebClient.create(deployUrl.toString() + "resources"); 
    	
    	
    	Response resp404 = webClient.path("UnknownResource").get();
    	assertEquals("Must be", resp404.getStatus(), 404); 
    	
    	webClient.replacePath(ResourceMockService.initialResourceIds[0]);  
    	
    	Resource resource = webClient.get(Resource.class);
    	assertTrue("Must be eaual", ResourceMockService.initialResources[0].equals(resource)); 
    	
    	webClient.replacePath(""); 
    	Collection<? extends Resource> resourceList = webClient.getCollection(Resource.class);    	
    	assertEquals("Must this length", resourceList.size(), ResourceMockService.initialResources.length); 
    	for (Resource resourceIt : resourceList)
    		assertTrue("Must be a Resource", resourceIt instanceof Resource);
    	
    	Response resp201 = webClient.path("newResource").put(new Resource("newResource"));
    	assertEquals("Must be", resp201.getStatus(), 201); 
    	
    	webClient.replacePath(ResourceMockService.initialResourceIds[1]); 
    	Response resp200 = webClient.delete();
    	assertEquals("Must be", resp200.getStatus(), 200);
    	
    	webClient.replacePath(ResourceMockService.initialResourceIds[0]); 
    	webClient.replaceQueryParam("action", "remove");
    	Task taskRemove = webClient.post(null, Task.class);
    	assertEquals("Must be type", taskRemove.getType(), "removeResource"); 
    	
    	webClient.replaceQueryParam("action", "UnknownAction");
    	Response resp400 =webClient.post(null);
    	assertEquals("Must be", resp400.getStatus(), 400); 
    	
    	webClient.replacePath("NoResource");
    	webClient.replaceQueryParam("action", "remove");
    	Response resp404b = webClient.post(null);
    	assertEquals("Must be", resp404b.getStatus(), 404); 
    	
    	webClient.resetQuery();
    	Response resp400b = webClient.post(null);
    	assertEquals("Must be", resp400b.getStatus(), 400);
    }
   
    @Test
    public void testTasksResource() throws URISyntaxException {
    	System.out.println("\nTest TasksResource on URL=" + deployUrl.toString() + "...\n");	
    	
    	WebClient webClient = WebClient.create(deployUrl.toString() + "tasks");
    	
    	
    	Response resp404 = webClient.path("UnknownTask").get();
    	assertEquals("Must be", resp404.getStatus(), 404); 
    	
    	webClient.replacePath(TaskMockService.initialTaskIds[0]);  
    	
    	Task task = webClient.get(Task.class);
    	assertTrue("Must be eaual", TaskMockService.initialTasks[0].equals(task)); 
    	
    	webClient.replacePath(""); 
    	Collection<? extends Task> resourceList = webClient.getCollection(Task.class);    	
    	assertEquals("Must this length", resourceList.size(), TaskMockService.initialTasks.length); 
    	for (Task taskIt : resourceList)
    		assertTrue("Must be a Task", taskIt instanceof Task);
    	
  	
    	Response resp200 = webClient.path(TaskMockService.initialTaskIds[1]).delete();
    	assertEquals("Must be", resp200.getStatus(), 200);
    }
}