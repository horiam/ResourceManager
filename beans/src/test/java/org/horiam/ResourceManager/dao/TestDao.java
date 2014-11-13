package org.horiam.ResourceManager.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.horiam.ResourceManager.businessLogic.AlloctionDriver;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.test.ContainerWrapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDao extends ContainerWrapper {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		Properties properties = new Properties();
		properties.put("openjpa.Log","DefaultLevel=INFO, Runtime=INFO, Tool=INFO");		
		setupContainer(properties);
	}
	
	@AfterClass
	public static void stop() {
		closeContainer();
	}
	
	@Test
	public void aTest() throws Exception {
		System.out.println("\nTest ClassFinder EJB...");
		ClassFinder classFinder = (ClassFinder) lookup("java:global/beans/ClassFinder");
		
		assertTrue("Should return a subclass", User.class.isAssignableFrom(classFinder.getUserClass()));
		assertTrue("Should return a subclass", Resource.class.isAssignableFrom(classFinder.getResourceClass()));
		assertTrue("Should return a subclass", AlloctionDriver.class.isAssignableFrom(classFinder.getAllocatorDriverClass()));
		assertTrue("Should be an instance of/sublclass", (classFinder.getAllocateDriverInstance() instanceof AlloctionDriver));
		// TODO test class lookup
	}
	
	@Test
	public void bTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest UserDao EJB...");
		UserDao userDao = (UserDao) lookup("java:global/beans/UserDao");
		User userA = new User("userA");
		userDao.create(userA);
		assertTrue("Entity should exist", userDao.exists(userA.getId()));
		assertTrue("Objects should be equal", userA.equals(userDao.get(userA.getId())));
		userA.setBooked(true);
		User updatedUserA = userDao.update(userA);
		assertTrue("Objects should be equal", userA.equals(updatedUserA));
		
		List<User> users = userDao.list();
		assertTrue("List size should be 1", (users.size() == 1));
		userDao.remove(userA.getId());
		users = userDao.list();
		assertTrue("List size should be 0", (users.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		userDao.get(userA.getId());	
	}
	
	@Test
	public void cTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest ResourceDao EJB...");
		ResourceDao resourceDao = (ResourceDao) lookup("java:global/beans/ResourceDao");
		Resource resource1 = new Resource("resource1");
		resourceDao.create(resource1);
		assertTrue("Entity should exist", resourceDao.exists(resource1.getId()));
		assertTrue("Objects should be equal", resource1.equals(resourceDao.get(resource1.getId())));
		resource1.setBooked(true);
		Resource updatedUserA = resourceDao.update(resource1);
		assertTrue("Objects should be equal", resource1.equals(updatedUserA));
		
		Resource resource2 = new Resource("resource2");
		resourceDao.create(resource2);
		
		List<Resource> resources = resourceDao.list();
		assertTrue("List size should be 2", (resources.size() == 2));
		
		List<Resource> freeResources = resourceDao.listFree();
		assertTrue("List size should be 1", (freeResources.size() == 1));
		
		resourceDao.remove(resource1.getId());
		resourceDao.remove(resource2.getId());
		resources = resourceDao.list();
		assertTrue("List size should be 0", (resources.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		resourceDao.get(resource1.getId());
	}	
	
	@Test
	public void dTest() throws NamingException, EntityNotFoundException {
		System.out.println("\nTest UserDao EJB...");
		TaskDao taskDao = (TaskDao) lookup("java:global/beans/TaskDao");
		Task taskX = new Task("taskX");
		taskDao.create(taskX);
		assertTrue("Entity should exist", taskDao.exists(taskX.getId()));
		assertTrue("Objects should be equal", taskX.equals(taskDao.get(taskX.getId())));
		taskX.setMessage("foo");
		Task updatedTaskX = taskDao.update(taskX);
		assertTrue("Objects should be equal", taskX.equals(updatedTaskX));
		
		List<Task> tasks = taskDao.list();
		assertTrue("List size should be 1", (tasks.size() == 1));
		taskDao.remove(taskX.getId());
		tasks = taskDao.list();
		assertTrue("List size should be 0", (tasks.size() == 0));
		
		exception.expect(EntityNotFoundException.class);
		taskDao.get(taskX.getId());	
	}
}
