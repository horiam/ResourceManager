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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

import org.horiam.ResourceManager.jms.JmsClient;
import org.horiam.ResourceManager.model.Model;

public class JmsAssertion <E extends Model>{

	private JmsClient client;
	
	public JmsAssertion(String queueName) throws NamingException, JMSException {
		
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		Context context = new InitialContext(props);
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		Queue queue = (Queue) context.lookup("dynamicQueues/" + queueName);
		client = new JmsClient(factory, queue);
    	client.init();    
	}
	
	public void assertReceive(E model) throws JMSException {
		TextMessage sendMessage = client.getSession().createTextMessage(model.getId());
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	E recvModel = (E) receivedMessage.getObject();
    	
    	assertEquals("Must be equal", model, recvModel);
	}
	
	public void assertSize(int size) throws JMSException {
		TextMessage sendMessage2 = client.getSession().createTextMessage("");
    	client.sendMessage(sendMessage2);
    
    	ObjectMessage receivedMessage2 = (ObjectMessage) client.receiveMessage(2000);
    	List<E> recvList = (ArrayList<E>) receivedMessage2.getObject();
    	
    	assertEquals("Must be " + size, recvList.size(), size);
	}
	
	public void close() throws JMSException {
		client.close();
	}
	
}
