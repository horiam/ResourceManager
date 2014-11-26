package org.horiam.ResourceManager.webapp.soapful;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.services.ResourceService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.ResourceSEI;

@Stateless // TODO
@WebService(serviceName = "ResourceWS",		
targetNamespace = "http://ResourceManager/wsdl",
endpointInterface = "org.horiam.ResourceManager.soap.ResourceSEI")
public class ResourceWS implements ResourceSEI {
	
	@EJB
	private ResourceService resourceService;
	
	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String id) {
		return resourceService.exists(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#get(java.lang.String)
	 */
	@Override
	public Resource get(String id) throws ResourceManagerFault {
		try {
			return resourceService.get(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#createOrUpdate(java.lang.String, org.horiam.ResourceManager.model.Resource)
	 */
	@Override
	public void createOrUpdate(String id, Resource resource) {
		resourceService.createOrUpdate(id, resource);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#list()
	 */
	@Override
	public List<Resource> list() {
		return resourceService.list();
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		resourceService.delete(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.ResourceSEI#removeResource(java.lang.String)
	 */
	@Override
	public Task removeResource(String id) throws ResourceManagerFault {
		try {
			return resourceService.removeResource(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}
	
}
