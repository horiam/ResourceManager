package org.horiam.ResourceManager.jms;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
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

import org.horiam.ResourceManager.model.User;



@MessageDriven
(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",  
                				 propertyValue = "javax.jms.Queue"),                 
        @ActivationConfigProperty(propertyName = "destination",  
                				 propertyValue = "jms/ObjectTestQueue")})
public class Mdb4ObjectTests implements MessageListener {

	@Resource
    private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageProducer replyProducer;

	@PostConstruct
	protected void postConstruct() throws JMSException {
	System.out.println("\n postConstruct\n");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		replyProducer = session.createProducer(null);
		replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);				
	
	}
	
	@PreDestroy 
	protected void preDestroy() throws JMSException {
		if (session != null) 
			session.close();
        if (connection != null) 
        	connection.close();
	}
	
	@Override
	public void onMessage(Message message) {
		 try {
			ObjectMessage response = session.createObjectMessage();
			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				String messageText = txtMsg.getText();
				System.out.println("\n messageText="+messageText+"\n");
				response.setObject(new User("Testuser") );
			}
			response.setJMSCorrelationID(message.getJMSCorrelationID());
			replyProducer.send(message.getJMSReplyTo(), response);
		} catch (JMSException e) {
			e.printStackTrace();
		}		 
	}
}
