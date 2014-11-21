import java.util.List;

import javax.ejb.Stateless;

import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;

@Stateless
public class UsersMockService implements UserService {

	@Override
	public List<? extends User> list() {		
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createOrUpdate(String id, User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User get(String id) throws EntityNotFoundException {	
		return new User(id);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Task allocateUser(String id) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task deallocateUser(String id) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task removeUser(String id) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
