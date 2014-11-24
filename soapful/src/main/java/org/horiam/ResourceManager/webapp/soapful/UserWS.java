package org.horiam.ResourceManager.webapp.soapful;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;
import org.horiam.ResourceManager.soap.UserSEI;

@WebService(serviceName = "UserWS",		
targetNamespace = "http://ResourceManager/wsdl",
endpointInterface = "org.horiam.ResourceManager.soap.UserSEI")
@Stateless
public class UserWS implements UserSEI  {
	
	@EJB
	private UserService userService;
	

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#list()
	 */
	@Override
	public List<User> list() {
		return userService.list();
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String id) {
		return userService.exists(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#createOrUpdate(java.lang.String, org.horiam.ResourceManager.model.User)
	 */
	@Override
	public void createOrUpdate(String id, User user) {
		userService.createOrUpdate(id, user);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#get(java.lang.String)
	 */
	@Override
	public User get(String id) throws AuthorisationException,
			RecordNotFoundException {
		return userService.get(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		userService.delete(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#allocateUser(java.lang.String)
	 */
	@Override
	public Task allocateUser(String id) throws RecordNotFoundException {
		return userService.allocateUser(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#deallocateUser(java.lang.String)
	 */
	@Override
	public Task deallocateUser(String id) throws RecordNotFoundException {
		return userService.deallocateUser(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.UserSEI#removeUser(java.lang.String)
	 */
	@Override
	public Task removeUser(String id) throws RecordNotFoundException {
		return userService.removeUser(id);
	}	
}
