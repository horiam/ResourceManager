package org.horiam.ResourceManager.jms;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Topic;

import org.horiam.ResourceManager.model.Task;

@Stateless
public class TaskObserver {

	@Resource(lookup = "jms/TaskTopic")
	private Topic topic;
	@Inject
	private JMSContext jmsContext;
	
	public void handleEvent(@Observes Task task) {
		String text = "Task "+task.getId()+" is "+task.getStatus();				
		jmsContext.createProducer().send(topic, text);
	}
}
