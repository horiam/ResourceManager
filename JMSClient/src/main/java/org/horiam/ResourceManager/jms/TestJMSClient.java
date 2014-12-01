package org.horiam.ResourceManager.jms;



import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import javax.jms.Queue;
import javax.jms.TextMessage;

@Stateless
public class TestJMSClient extends SimpleJMSClient {
	
    @Resource(name ="jms/TestQueue")
    private Queue usersQueue;

    public void send(String toSend) throws JMSException {
    	TextMessage text = session.createTextMessage(toSend);
    	sendMessage(usersQueue, text);
    }
    
    public String receive() throws JMSException {
    	TextMessage message = (TextMessage) receiveMessage();
    	return message.getText();
    }
}
