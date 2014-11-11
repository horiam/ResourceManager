package org.horiam.ResourceManager.model;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.horiam.ResourceManager.model.Task.Status;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestObjectToXmlToObject {
	
	private static List<User> users;
	private static List<Resource> resources;
	private static List<Task> tasks;

	private static FileSystemManager fsManager;
	private static HashMap<String, FileObject> xmls;
	
    @BeforeClass
    public static void start() throws FileSystemException {
    	
    	fsManager = VFS.getManager();
    	
    	xmls = new HashMap<String, FileObject>();
    	   	
    	populate();      	
    }
    
    private static void populate() {
    	
    	users = new ArrayList<User>();
    	
    	User userA = new User("UserA");
    	userA.setDate(new Date());
    	userA.setBooked(false);
    	users.add(userA);    	
    	User userB = new User("UserB");
    	userA.setDate(new Date(1969, 6, 9));
    	userA.setBooked(true);  
    	users.add(userB);
    	User userC = new User("UserC");
    	userA.setDate(new Date(2014, 1, 1));
    	userA.setBooked(false);  
    	users.add(userC);
    	
    	resources = new ArrayList<Resource>();
    	
    	Resource resource1 = new Resource("Resource1");
    	resource1.setDate(new Date());
    	resource1.setBooked(false); 
    	resources.add(resource1);
    	Resource resource2 = new Resource("Resource2");
    	resource1.setDate(new Date(1971, 7, 10));
    	resource1.setBooked(true); 
    	resources.add(resource2);
    	Resource resource3 = new Resource("Resource3");
    	resource1.setDate(new Date());
    	resource1.setBooked(false);
    	resources.add(resource3);
    	
    	tasks = new ArrayList<Task>();
    	  	
    	Task taskX = new Task("TaskX", "typeX");
    	taskX.setDate(new Date());
    	taskX.setRetryable(false);
    	taskX.setStatus(Status.SUCCEEDED);
    	taskX.setUser(userA);
    	taskX.setResource(resource1);
    	userA.setTask(taskX);
    	userA.setResource(resource1);
    	resource1.setUser(userA);
    	resource1.setTask(taskX);
    	tasks.add(taskX);
    	
    	Task taskY = new Task("TaskY", "typeY");
    	taskY.setDate(new Date(1972, 7, 10));
    	taskY.setRetryable(false);
    	taskY.setStatus(Status.PROCESSING);
    	taskY.setUser(userB);
    	taskY.setResource(resource2);
    	userB.setTask(taskY);
    	resource2.setTask(taskY);
    	tasks.add(taskY);
    	
    	Task taskZ = new Task("TaskZ", "typeZ");
    	taskZ.setDate(new Date());
    	taskZ.setRetryable(true);
    	taskZ.setStatus(Status.FAILED);
    	taskZ.setUser(userC);
    	taskZ.setResource(resource3);   	
    	tasks.add(taskZ);

    }
    
    @AfterClass
    public static void stop() throws FileSystemException {
    	
    	for (FileObject file : xmls.values())
    		file.close();
    }

    @Test
	public void aTest() throws JAXBException, IOException {
    	System.out.println("\nTest marshall User...");
    	JAXBContext context = JAXBContext.newInstance(User.class);
    	Marshaller marshaller = context.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	
    	for (User user : users) {
    		FileObject file = fsManager.resolveFile("ram://"+user.getId()+".xml");    		
	    	OutputStream os = file.getContent().getOutputStream();  
	    	marshaller.marshal(user, System.out);
	    	marshaller.marshal(user, os);
	    	xmls.put(user.getId(), file);
	    	os.close();
    	}   	
	}
    
    @Test
	public void bTest() throws JAXBException, IOException {
    	System.out.println("\nTest marshall Resource...");
    	JAXBContext context = JAXBContext.newInstance(Resource.class);
    	Marshaller marshaller = context.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	
    	for (Resource resource : resources) {
    		FileObject file = fsManager.resolveFile("ram://"+resource.getId()+".xml");    		
	    	OutputStream os = file.getContent().getOutputStream();    	
	    	marshaller.marshal(resource, System.out);
	    	marshaller.marshal(resource, os);
	    	xmls.put(resource.getId(), file);
	    	os.close();
    	}   	
	}
	
    @Test
	public void cTest() throws JAXBException, IOException {
    	System.out.println("\nTest marshall Task...");
    	JAXBContext context = JAXBContext.newInstance(Task.class);
    	Marshaller marshaller = context.createMarshaller();
    	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    	
    	for (Task task : tasks) {
    		FileObject file = fsManager.resolveFile("ram://"+task.getId()+".xml");    		
	    	OutputStream os = file.getContent().getOutputStream(); 
	    	marshaller.marshal(task, System.out);
	    	marshaller.marshal(task, os);
	    	xmls.put(task.getId(), file);
	    	os.close();
    	}   	
	}
    
    @Test
    public void dTest() throws JAXBException, IOException {
    	System.out.println("\nTest unmarshall User...");
    	JAXBContext context = JAXBContext.newInstance(User.class);
    	Unmarshaller unmarshaller = context.createUnmarshaller();
    	
    	for (User user : users) {
    		FileObject file = xmls.get(user.getId());
    		InputStream is = file.getContent().getInputStream();
    		User compare = (User) unmarshaller.unmarshal(is);
    		assertNotSame("Objects must have different references", user, compare);
    		assertTrue("Objects must be equal", user.equals(compare));
    		is.close();
    	}
    }
    
    @Test
    public void eTest() throws JAXBException, IOException {
    	System.out.println("\nTest unmarshall Resource...");
    	JAXBContext context = JAXBContext.newInstance(Resource.class);
    	Unmarshaller unmarshaller = context.createUnmarshaller();
    	
    	for (Resource resource : resources) {
    		FileObject file = xmls.get(resource.getId());
    		InputStream is = file.getContent().getInputStream();
    		Resource compare = (Resource) unmarshaller.unmarshal(is);
    		assertNotSame("Objects must have different references", resource, compare);
    		assertTrue("Objects must be equal", resource.equals(compare));
    		is.close();
    	}
    }
    
    @Test
    public void fTest() throws JAXBException, IOException {
    	System.out.println("\nTest unmarshall Task...");
    	JAXBContext context = JAXBContext.newInstance(Task.class);
    	Unmarshaller unmarshaller = context.createUnmarshaller();
    	
    	for (Task task : tasks) {
    		FileObject file = xmls.get(task.getId());
    		InputStream is = file.getContent().getInputStream();
    		Task compare = (Task) unmarshaller.unmarshal(is);
    		assertNotSame("Objects must have different references", task, compare);
    		assertTrue("Objects must be equal", task.equals(compare));
    		is.close();
    	}
    }
}
