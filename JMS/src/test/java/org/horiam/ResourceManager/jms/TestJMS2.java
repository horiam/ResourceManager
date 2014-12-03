package org.horiam.ResourceManager.jms;

import javax.annotation.Resource;
import javax.ejb.embeddable.EJBContainer;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.horiam.ResourceManager.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJMS2 {

	@Resource
    private ConnectionFactory connectionFactory;    
	@Resource(name ="jms/ObjectTestQueue")
    private Queue testQueue;

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
    public void test() throws JMSException  {
    	System.out.println("\nTest JMS Listener in TestJMS2...\n");
    
    	JmsClient client = new JmsClient(connectionFactory, testQueue);
    	client.init();
    	
    	TextMessage sendMessage = client.getSession().createTextMessage("ping");
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	Object receivedObj = receivedMessage.getObject();
    
		if (receivedObj instanceof User) {
			User user = (User) receivedObj;

			System.out.println("\n id=" + user.getId() + "\n");
		}
    }
}
