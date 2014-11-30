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

import org.junit.Test;


public class TestJMSListener {

    
    @Resource
    private ConnectionFactory connectionFactory;
    @Resource(name ="jms/TestQueue")
    private Queue usersQueue;
    
    @Test
    public void testUsersWS() throws JMSException, NamingException  {    	
    	System.out.println("\nTest JMS Listener...\n");

    	EJBContainer.createEJBContainer().getContext().bind("inject", this);
    	
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
