package org.horiam.ResourceManager.services;

import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.authorisation.ActionOnUserAuthorisationInterceptor;
import org.horiam.ResourceManager.authorisation.AuthorisationException;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@DeclareRoles(value = {"Admin", "User"})
@Local
public interface UserService {

	@RolesAllowed(value = {"Admin"})
	abstract List<? extends User> list();

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	abstract boolean exists(String id);

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	abstract void createOrUpdate(String id, User user);

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})	
	abstract User get(String id) throws AuthorisationException, EntityNotFoundException;

	@RolesAllowed(value = {"Admin"})
	abstract void delete(String id);

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	abstract Task allocateUser(String id) throws EntityNotFoundException;

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	abstract Task deallocateUser(String id) throws EntityNotFoundException;

	////////////////////////////////////////////////////////////////////////////
	@Interceptors(ActionOnUserAuthorisationInterceptor.class)
	@RolesAllowed(value = {"Admin", "User"})
	abstract Task removeUser(String id) throws EntityNotFoundException;

}