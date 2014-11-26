package org.horiam.ResourceManager.mock;

import static javax.ejb.LockType.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.TaskService;

@Stateless
public class TaskMockService implements TaskService {

	public final static String[] initialTaskIds = {"taskX", "taskY"};
	public final static Task[]   initialTasks   = {new Task(initialTaskIds[0]), 
													new Task(initialTaskIds[1])};
		
	private List<Task> tasks;
	
	@PostConstruct
	protected void postConstruct() {
		tasks = new ArrayList<Task>();
		tasks.addAll(Arrays.asList(initialTasks));
	}

	
	@Override
	public List<Task> list() {
		return tasks;
	}

	@Override
	public boolean exists(String id) {
		if (getTask(id) != null)
			return true;
		
		return false;
	}

	@Override
	public Task get(String id) throws RecordNotFoundException {
		Task task = getTask(id);
		if (task != null)
			return task;
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void delete(String id) {
		deleteTask(id);
	}
		
	private Task getTask(String id) {		
		for (Task task : tasks) {
			if (task.getId().equals(id))
				return task;
		}
		return null;
	}
	@Lock(WRITE)
	private void addTask(Task task) {
		tasks.add(task);
	}
	@Lock(WRITE)
	private void deleteTask(String id) {
		Task task = getTask(id);
		if (task != null) {
			tasks.remove(task);
		}
	}

}
