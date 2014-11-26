package org.horiam.ResourceManager.soap;

import java.util.List;

import javax.jws.WebService;

import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;

@WebService(targetNamespace = "http://ResourceManager/wsdl")
public interface ResourceSEI {

	abstract boolean exists(String id);

	abstract Resource get(String id) throws ResourceManagerFault;

	abstract void createOrUpdate(String id, Resource resource);

	abstract List<Resource> list();

	abstract void delete(String id);

	abstract Task removeResource(String id) throws ResourceManagerFault;

}