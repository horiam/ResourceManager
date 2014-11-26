package org.horiam.ResourceManager.jms;


import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven
public class MessageDrivenBean implements MessageListener {

	@Resource
    private ConnectionFactory connectionFactory;
	private final static String queueName = "UsersRequestQueue";
	
	
	@PostConstruct
	protected void postConstruct() {

	}
	
	@Override
	public void onMessage(Message message) {
				
	}

}
