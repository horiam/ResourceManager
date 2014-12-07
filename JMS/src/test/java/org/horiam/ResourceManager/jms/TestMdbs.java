/*
 * Copyright (C) 2014  Horia Musat
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.horiam.ResourceManager.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.horiam.ResourceManager.mock.ResourceMockService;
import org.horiam.ResourceManager.mock.TaskMockService;
import org.horiam.ResourceManager.mock.UserMockService;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMdbs {

	@javax.annotation.Resource
    private ConnectionFactory connectionFactory;    
	@javax.annotation.Resource(name ="jms/usersQueue")
    private Queue usersQueue;
	@javax.annotation.Resource(name ="jms/resourcesQueue")
    private Queue resourcesQueue;
	@javax.annotation.Resource(name ="jms/tasksQueue")
    private Queue tasksQueue;

    private EJBContainer container;
    
    @Before
    public void before() throws NamingException {
    	container = EJBContainer.createEJBContainer();
    	container.getContext().bind("inject", this);
    }
    
    @After
    public void after() {
    	container.close();
    }
    
    @Test
    public void testUsersMdb() throws JMSException  {
    	System.out.println("\n Test UsersMdb...\n");
    
    	JmsClient client = new JmsClient(connectionFactory, usersQueue);
    	client.init();
    	
    	TextMessage sendMessage = client.getSession().createTextMessage(UserMockService.initialUserIds[0]);
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	User recvUser = (User) receivedMessage.getObject();
    	
    	assertTrue("Must be equal", UserMockService.initialUsers[0].equals(recvUser));
    	
    	TextMessage sendMessage2 = client.getSession().createTextMessage("");
    	client.sendMessage(sendMessage2);
    
    	ObjectMessage receivedMessage2 = (ObjectMessage) client.receiveMessage(2000);
    	List<User> recvList = (ArrayList<User>) receivedMessage2.getObject();
    	
    	assertEquals("Must be 2", recvList.size(), UserMockService.initialUsers.length);
    	for (User user : recvList)
    		assertTrue("Must be instance of", user instanceof User);
    	
    	client.close();
    }
    
    @Test
    public void testResourcesMdb() throws JMSException  {
    	System.out.println("\n Test ResourcesMdb...\n");
    
    	JmsClient client = new JmsClient(connectionFactory, resourcesQueue);
    	client.init();
    	
    	TextMessage sendMessage = client.getSession().createTextMessage(ResourceMockService.initialResourceIds[0]);
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	Resource recvResource = (Resource) receivedMessage.getObject();
    	
    	assertTrue("Must be equal", ResourceMockService.initialResources[0].equals(recvResource));
    	
    	TextMessage sendMessage2 = client.getSession().createTextMessage("");
    	client.sendMessage(sendMessage2);
    
    	ObjectMessage receivedMessage2 = (ObjectMessage) client.receiveMessage(2000);
    	List<Resource> recvList = (ArrayList<Resource>) receivedMessage2.getObject();
    	
    	assertEquals("Must be 2", recvList.size(), ResourceMockService.initialResources.length);
    	for (Resource user : recvList)
    		assertTrue("Must be instance of", user instanceof Resource);
    	
    	client.close();
    }

    @Test
    public void testTasksMdb() throws JMSException  {
    	System.out.println("\n Test TasksMdb...\n");
    
    	JmsClient client = new JmsClient(connectionFactory, tasksQueue);
    	client.init();
    	
    	TextMessage sendMessage = client.getSession().createTextMessage(TaskMockService.initialTaskIds[0]);
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	Task recvTask = (Task) receivedMessage.getObject();
    	
    	assertTrue("Must be equal", TaskMockService.initialTasks[0].equals(recvTask));
    	
    	TextMessage sendMessage2 = client.getSession().createTextMessage("");
    	client.sendMessage(sendMessage2);
    
    	ObjectMessage receivedMessage2 = (ObjectMessage) client.receiveMessage(2000);
    	List<Task> recvList = (ArrayList<Task>) receivedMessage2.getObject();
    	
    	assertEquals("Must be 2", recvList.size(), TaskMockService.initialTasks.length);
    	for (Task user : recvList)
    		assertTrue("Must be instance of", user instanceof Task);
    	
    	client.close();
    }

}
