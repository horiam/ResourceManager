package org.horiam.ResourceManager.mock;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.services.TaskService;

@Stateless
public class TaskMockService implements TaskService {

	public final static String[] taskIds = {"taskX", "taskY"};
	public final static Task[]   tasks   = {new Task(taskIds[0]), new Task(taskIds[1])};
	
	@Override
	public List<Task> list() {
		return Arrays.asList(tasks);
	}

	@Override
	public boolean exists(String id) {
		if (Arrays.binarySearch(taskIds, id) > -1)
			return true;

		return false;
	}

	@Override
	public Task get(String id) throws RecordNotFoundException {
		int idx = Arrays.binarySearch(taskIds, id);
		if (idx > -1)
			return tasks[idx];
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void delete(String id) {}

}
