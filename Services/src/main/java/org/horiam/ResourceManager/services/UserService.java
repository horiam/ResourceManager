package org.horiam.ResourceManager.services;

import java.util.List;

import javax.ejb.Local;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@Local
public interface UserService {

	abstract List<User> list();
	
	////////////////////////////////////////////////////////////////////////////
	abstract boolean exists(String id);

	////////////////////////////////////////////////////////////////////////////

	abstract void createOrUpdate(String id, User user);

	////////////////////////////////////////////////////////////////////////////

	abstract User get(String id) throws AuthorisationException, RecordNotFoundException;


	abstract void delete(String id);

	////////////////////////////////////////////////////////////////////////////

	abstract Task allocateUser(String id) throws RecordNotFoundException;

	////////////////////////////////////////////////////////////////////////////


	abstract Task deallocateUser(String id) throws RecordNotFoundException;

	////////////////////////////////////////////////////////////////////////////

	abstract Task removeUser(String id) throws RecordNotFoundException;

}