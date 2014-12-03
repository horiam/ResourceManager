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
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	private MessageProducer requestProducer;
	private Queue myQueue;
	private MessageConsumer responseConsumer;

    
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
