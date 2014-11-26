package org.horiam.ResourceManager.soap;

import java.util.List;

import javax.jws.WebService;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@WebService(targetNamespace = "http://ResourceManager/wsdl")
public interface UserSEI {

	public abstract List<User> list();

	public abstract boolean exists(String id);

	public abstract void createOrUpdate(String id, User user);

	public abstract User get(String id) throws ResourceManagerFault;

	public abstract void delete(String id);

	public abstract Task allocateUser(String id) throws ResourceManagerFault;

	public abstract Task deallocateUser(String id)
			throws ResourceManagerFault;

	public abstract Task removeUser(String id) throws ResourceManagerFault;

}