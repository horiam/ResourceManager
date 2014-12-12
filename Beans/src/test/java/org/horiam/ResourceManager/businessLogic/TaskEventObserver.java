package org.horiam.ResourceManager.businessLogic;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;

import org.horiam.ResourceManager.model.Task;

@Singleton
public class TaskEventObserver {
	
	private Task observedTask = null;
	
	public void eventObserver(@Observes Task task) {
		this.setObservedTask(task);
	}

	public Task getObservedTask() {
		return observedTask;
	}

	public void setObservedTask(Task observedTask) {
		this.observedTask = observedTask;
	}
}
