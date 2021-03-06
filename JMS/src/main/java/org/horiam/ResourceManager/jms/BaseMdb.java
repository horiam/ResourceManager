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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;

public abstract class BaseMdb implements MessageListener {
	
	protected static final String CLASS_NAME = BaseMdb.class.getName();
	protected static final Logger log = Logger.getLogger(CLASS_NAME);

	@Resource
    private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageProducer replyProducer;

	@PostConstruct
	protected void postConstruct() throws JMSException {
		log.entering(CLASS_NAME, "postConstruct");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		replyProducer = session.createProducer(null);
		replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);				
		log.exiting(CLASS_NAME, "postConstruct");
	}
	
	@PreDestroy 
	protected void preDestroy() throws JMSException {
		log.entering(CLASS_NAME, "preDestroy");
		if (replyProducer != null)
			replyProducer.close();
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
        log.exiting(CLASS_NAME, "preDestroy");
	}
	
	@Override
	public void onMessage(Message message) {
		log.entering(CLASS_NAME, "onMessage", new Object[] { message });

		try {
			ObjectMessage response = session.createObjectMessage();
			Serializable responseObj;
			boolean succeeded = false;
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String recvText = txtMsg.getText();
				try {
					responseObj = createResponseObject(recvText);
					succeeded = true;
				} catch (Throwable t) {
					log.log(Level.WARNING, t.getMessage(), t);
					responseObj = t;
				}
			} else {
				responseObj = "No text message received";
			}
			response.setBooleanProperty("succeeded", succeeded);
			response.setObject(responseObj);
			response.setJMSCorrelationID(message.getJMSCorrelationID());
			replyProducer.send(message.getJMSReplyTo(), response);
		} catch (JMSException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		log.exiting(CLASS_NAME, "onMessage");
	}
	
	abstract public Serializable createResponseObject(String recvText) throws AuthorisationException,
																			RecordNotFoundException ;

}
