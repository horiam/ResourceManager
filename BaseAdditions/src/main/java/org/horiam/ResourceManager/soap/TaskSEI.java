package org.horiam.ResourceManager.soap;

import java.util.List;

import javax.jws.WebService;

import org.horiam.ResourceManager.model.Task;

@WebService(targetNamespace = "http://ResourceManager/wsdl")
public interface TaskSEI {

	abstract List<Task> list();

	abstract boolean exists(String id);

	abstract Task get(String id) throws ResourceManagerFault;

	abstract void delete(String id);

}