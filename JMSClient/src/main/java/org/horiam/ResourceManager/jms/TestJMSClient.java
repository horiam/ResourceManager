package org.horiam.ResourceManager.jms;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Message;
import javax.jms.Queue;

@Stateless
public class TestJMSClient extends SimpleJMSClient<String> {
	
    @Resource(name ="jms/TestQueue")
    private Queue usersQueue;

	@Override
	public void sendMessage(Message message) {
		
	}

	@Override
	public String receiveMessage() {
		
		return null;
	}

}
