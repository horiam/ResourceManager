package org.horiam.ResourceManager.services;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.interceptor.Interceptors;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;

public interface TaskService {

	public abstract List<Task> list();

	public abstract boolean exists(String id);

	public abstract Task get(String id) throws RecordNotFoundException;

	public abstract void delete(String id);

}