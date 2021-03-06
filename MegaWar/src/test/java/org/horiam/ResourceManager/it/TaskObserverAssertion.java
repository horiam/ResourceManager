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

package org.horiam.ResourceManager.it;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertTrue;

import org.horiam.ResourceManager.model.Task;

public class TaskObserverAssertion {
	
	private ConnectionFactory factory;
	private Topic topic;
	private Connection connection;
	private Task recvTask = null;
	private MessageConsumer consumer;
	
	public TaskObserverAssertion() throws NamingException, JMSException {
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		Context context = new InitialContext(props);
		factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		topic = (Topic) context.lookup("dynamicTopics/jms/tasksTopic");
	}
	
	public void init() throws JMSException {
		// Subscribe topic 
    	connection = factory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(topic);
	}	
	
	public void assertTask(Task task) throws JMSException {
		
		ObjectMessage message = (ObjectMessage) consumer.receive(3000);
		recvTask = (Task) message.getObject();
		assertTrue("Must be the same", task.equals(recvTask));
		connection.close();	
	}
}
