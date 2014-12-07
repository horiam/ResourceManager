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

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

public class JmsClient {

	private ConnectionFactory connectionFactory;
    private Queue requestQueue;	
    private Connection connection;
	private Session session;
	private MessageProducer requestProducer;
	private Queue myQueue;
	private MessageConsumer responseConsumer;

 	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}   
	
    public JmsClient(ConnectionFactory connectionFactory, Queue requestQueue) {
    	setConnectionFactory(connectionFactory);
    	setRequestQueue(requestQueue);
    }

    public void init() throws JMSException {
    	connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestProducer = session.createProducer(requestQueue);
		requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);					
		myQueue = session.createTemporaryQueue();
		responseConsumer = session.createConsumer(myQueue);
    }
    
    protected void close() throws JMSException {
    	if (requestProducer != null)
    		requestProducer.close();
    	if (responseConsumer != null)
    		responseConsumer.close();
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
	}
	
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Queue getRequestQueue() {
		return requestQueue;
	}

	public void setRequestQueue(Queue requestQueue) {
		this.requestQueue = requestQueue;
	}
	
	protected void sendMessage(Message message) throws JMSException {
		message.setJMSReplyTo(myQueue);
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        requestProducer.send(message);
	}
	
	protected Message receiveMessage(int timeout) throws JMSException {
		return responseConsumer.receive(timeout);  
	}
}
