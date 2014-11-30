package org.horiam.ResourceManager.jms;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;


public abstract class SimpleJMSClient<T> {
	
	@Resource
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageProducer requestProducer;
	private Queue myQueue;


	@PostConstruct
	protected void postConstruct() throws JMSException {
		System.out.println("\n postConstruct SimpleJMSClient \n");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestProducer = session.createProducer(null);
		requestProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);					
	}
	
	@PreDestroy 
	protected void preDestroy() throws JMSException {
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
	}
	
	public abstract void sendMessage(Message message);
	
	public abstract T receiveMessage();
}
