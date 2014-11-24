package org.horiam.ResourceManager.mock;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;

@Stateless
public class UserMockService implements UserService {
	
	final public static String[] userIds = {"userA", "userB"};
	final public static User[]     users = {new User(userIds[0]), new User(userIds[1])};

	@Override
	public List<User> list() {
		return Arrays.asList(users);		
	}

	@Override
	public boolean exists(String id) {
		if (Arrays.binarySearch(userIds, id) > -1)
			return true;
		
		return false;
	}

	@Override
	public void createOrUpdate(String id, User user) {}

	@Override
	public User get(String id) throws RecordNotFoundException {		
		int idx = Arrays.binarySearch(userIds, id);
		if (idx > -1)
			return users[idx];
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void delete(String id) {}

	@Override
	public Task allocateUser(String id) throws RecordNotFoundException {		
		User user = get(id);
		Task task = new Task("mock allocateUser");
		task.setType("allocateResourceForUser");
		return task;
	}

	@Override
	public Task deallocateUser(String id) throws RecordNotFoundException {
		User user = get(id);
		Task task = new Task("mock deallocateUser");
		task.setType("deallocateUser");
		return task;		
	}

	@Override
	public Task removeUser(String id) throws RecordNotFoundException {
		User user = get(id);		
		Task task = new Task("mock remoceUser");
		task.setType("removeUser");
		return task;
	}

}
