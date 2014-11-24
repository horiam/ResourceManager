package org.horiam.ResourceManager.mock;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.ResourceService;

@Stateless
public class ResourceMockService implements ResourceService {
	
	public final static String[] resourceIds = {"resource1", "resouce2"};
	public final static Resource[] resources = {new Resource(resourceIds[0]), new Resource(resourceIds[1])};

	@Override
	public boolean exists(String id) {
		if (Arrays.binarySearch(resourceIds, id) > -1)
			return true;

		return false;
	}

	@Override
	public Resource get(String id) throws RecordNotFoundException {
		int idx = Arrays.binarySearch(resourceIds, id);
		if (idx > -1)
			return resources[idx];
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void createOrUpdate(String id, Resource resource) {}

	@Override
	public List<Resource> list() {
		return Arrays.asList(resources);
	}

	@Override
	public void delete(String id) {}

	@Override
	public Task removeResource(String id) throws RecordNotFoundException {
		Resource user = get(id);		
		Task task = new Task("mock removeResource");
		task.setType("removeResource");
		return task;
	}

}
