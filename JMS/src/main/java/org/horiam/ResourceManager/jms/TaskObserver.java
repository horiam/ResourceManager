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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.horiam.ResourceManager.model.Task;

@Stateless
public class TaskObserver {
	
	private static final String CLASS_NAME = TaskObserver.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);
	
	@Resource
    private ConnectionFactory connectionFactory;    	
	@Resource(name = "jms/tasksTopic")
	private Topic topic;
	private Connection connection;
	private Session session;
	private MessageProducer producer;

	@PostConstruct
	public void postConstruct() throws JMSException {
		log.entering(CLASS_NAME, "postConstruct");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(topic);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		log.exiting(CLASS_NAME, "postConstruct");
	}
	
	@PreDestroy
	public void preDestroy() throws JMSException {
		log.entering(CLASS_NAME, "preDestroy");
		producer.close();
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
        log.exiting(CLASS_NAME, "preDestroy");
	}
	
	public void handleEvent(@Observes(during = TransactionPhase.AFTER_COMPLETION) Task task) {
		log.entering(CLASS_NAME, "handleEvent", new Object[] { task });
		try {
			ObjectMessage message = session.createObjectMessage(task);
			producer.send(message);
		} catch (JMSException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		log.exiting(CLASS_NAME, "handleEvent");
	}
}