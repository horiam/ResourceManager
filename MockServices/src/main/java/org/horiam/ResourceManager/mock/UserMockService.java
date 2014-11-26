package org.horiam.ResourceManager.mock;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;

@Singleton
@Lock(READ)
public class UserMockService implements UserService {
	
	public final static String[] initialUserIds = {"userA", "userB"};
	public final static User[]   initialUsers   = {new User(initialUserIds[0]), 
												   new User(initialUserIds[1])};

	private List<User> users;
	
	@PostConstruct
	protected void postConstruct() {
		
		users = new ArrayList<User>();		
		users.addAll(Arrays.asList(initialUsers));
	}
	
	
	@Override
	public List<User> list() {
		return users;		
	}

	@Override
	public boolean exists(String id) {
		if (getUser(id) != null)
			return true;
		
		return false;
	}

	@Override
	public void createOrUpdate(String id, User user) {
		addUser(user);
	}

	@Override
	public User get(String id) throws RecordNotFoundException {		
		User user = getUser(id);
		if (user != null)
			return user;
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void delete(String id) {
		deleteUser(id);
	}

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
		delete(id);
		Task task = new Task("mock remoceUser");
		task.setType("removeUser");
		return task;
	}
	
	
	private User getUser(String id) {		
		for (User user : users) {
			if (user.getId().equals(id))
				return user;
		}
		return null;
	}
	@Lock(WRITE)
	private void addUser(User user) {
		users.add(user);
	}
	@Lock(WRITE)
	private void deleteUser(String id) {
		User user = getUser(id);
		if (user != null) {
			users.remove(user);
		}
	}

}
