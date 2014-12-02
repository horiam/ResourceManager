package org.horiam.ResourceManager.jms;


import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.embeddable.EJBContainer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestJMSListener {

    @Resource
    private ConnectionFactory connectionFactory;
    @Resource(name ="jms/TestQueue")
    private Queue usersQueue;
    
    private EJBContainer container;
    
    @Before
    public void before() throws NamingException {
    	container = EJBContainer.createEJBContainer();
    	container.getContext().bind("inject", this);
    }
    
    @After 
    public void after() {
    	container.close();
    }
    
    
    @Test
    public void testUsersWS() throws  JMSException  {    	
    	System.out.println("\nTest JMS Listener...\n");
        	
    	Connection connection = connectionFactory.createConnection();

        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        MessageProducer questions = session.createProducer(usersQueue);

        Destination tempDest = session.createTemporaryQueue();
        MessageConsumer responseConsumer = session.createConsumer(tempDest); 
        
        
        TextMessage sendMessage = session.createTextMessage("ping");
        sendMessage.setJMSReplyTo(tempDest);
        sendMessage.setJMSCorrelationID(UUID.randomUUID().toString());
        questions.send(sendMessage);
        
        TextMessage message = (TextMessage) responseConsumer.receive(1000);
        
        System.out.println("\n message="+message.getText()+"\n"); 
    } 
}
