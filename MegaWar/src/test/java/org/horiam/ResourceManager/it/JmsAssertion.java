package org.horiam.ResourceManager.it;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;

import org.horiam.ResourceManager.jms.JmsClient;
import org.horiam.ResourceManager.model.Model;

public class JmsAssertion <E extends Model>{

	private JmsClient client;
	
	public JmsAssertion(String queueName) throws NamingException, JMSException {
//        System.setProperty("aConnectionFactory", "connectionfactory:org.apache.activemq.ActiveMQConnectionFactory:tcp://localhost:61616");
//        System.setProperty("aQueue", "queue:org.apache.activemq.command.ActiveMQQueue:LISTENER");
		
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		Context context = new InitialContext(props);
		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");
		Queue queue = (Queue) context.lookup("dynamicQueues/" + queueName);
		client = new JmsClient(factory, queue);
    	client.init();    
	}
	
	public void assertReceive(E model) throws JMSException {
		TextMessage sendMessage = client.getSession().createTextMessage(model.getId());
    	client.sendMessage(sendMessage);
    
    	ObjectMessage receivedMessage = (ObjectMessage) client.receiveMessage(2000);
    	E recvModel = (E) receivedMessage.getObject();
    	
    	assertEquals("Must be equal", model, recvModel);
	}
	
	public void assertSize(int size) throws JMSException {
		TextMessage sendMessage2 = client.getSession().createTextMessage("");
    	client.sendMessage(sendMessage2);
    
    	ObjectMessage receivedMessage2 = (ObjectMessage) client.receiveMessage(2000);
    	List<E> recvList = (ArrayList<E>) receivedMessage2.getObject();
    	
    	assertEquals("Must be " + size, recvList.size(), size);
	}
	
	public void close() throws JMSException {
		client.close();
	}
	
}
