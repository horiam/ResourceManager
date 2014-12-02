package org.horiam.ResourceManager.jms;


import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
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


public class TestJMSClientTest {

    @Resource
    private ConnectionFactory connectionFactory;
    @EJB
    private TestJMSClient client;
   
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
    public void testUsers() throws JMSException, NamingException  {    	
    	System.out.println("\nTest JMS Listener in TestJMSClientTest...\n");
    	
    	client.send("ping");
    	String message = client.receive();
        
        System.out.println("\n message="+message+"\n"); 
    } 
}
