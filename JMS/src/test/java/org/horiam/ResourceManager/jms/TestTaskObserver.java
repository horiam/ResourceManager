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

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.NamingException;

import org.horiam.ResourceManager.mock.TaskMockService;
import org.horiam.ResourceManager.model.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTaskObserver {

	@EJB
	private TaskEventLauncher taskEventLauncher;

	@Resource
    private ConnectionFactory connectionFactory;    
	@Resource(name = "jms/tasksTopic")
	private Topic topic;
	
	private EJBContainer container;
    
    @Before
    public void before() throws NamingException {
    	container = EJBContainer.createEJBContainer();
    	container.getContext().bind("inject", this);
    }
    
    @After
    public void after() {
    	if (container != null)
    		container.close();
    }
    
    @Test 
    public void test() throws JMSException {
    	System.out.println("\n Test TaskObserver ...\n");
    	
    	// Subscribe topic 
    	Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(topic);
		
		Task task = TaskMockService.initialTasks[0];
		taskEventLauncher.fireEvent(task);
		
		ObjectMessage message = (ObjectMessage) consumer.receive(3000);
		Task recvTask = (Task) message.getObject();
		
		assertTrue("Must be the same", task.equals(recvTask));
		
		connection.close();
    }
}
