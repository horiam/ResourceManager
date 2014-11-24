package org.horiam.ResourceManager.services;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;

@Local
public interface ResourceService {

	public abstract boolean exists(String id);

	public abstract Resource get(String id) throws RecordNotFoundException;

	public abstract void createOrUpdate(String id, Resource resource);

	public abstract List<Resource> list();

	public abstract void delete(String id);

	public abstract Task removeResource(String id)
			throws RecordNotFoundException;

}