package org.horiam.ResourceManager.jms;


import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
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
import javax.jms.Topic;


public abstract class SimpleJMSClient {
	
	@Resource
	private ConnectionFactory connectionFactory;
	private Connection connection;
	protected Session session;
	protected MessageProducer requestProducer;
	private Queue myQueue;
	private MessageConsumer responseConsumer;


	@PostConstruct
	protected void postConstruct() throws JMSException {
		System.out.println("\n postConstruct SimpleJMSClient \n");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestProducer = session.createProducer(null);
		requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);					
		myQueue = session.createTemporaryQueue();
		responseConsumer = session.createConsumer(myQueue);
	}
	
	@PreDestroy 
	protected void preDestroy() throws JMSException {
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
	}
	
	protected void sendMessage(Destination destination, Message message) throws JMSException {
		message.setJMSReplyTo(myQueue);
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        requestProducer.send(destination, message);
	}
	
	protected Message receiveMessage() throws JMSException {
		return responseConsumer.receive(1000); //TODO 
	}
}
