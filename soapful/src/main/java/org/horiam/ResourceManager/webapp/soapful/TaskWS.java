package org.horiam.ResourceManager.webapp.soapful;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.services.TaskService;
import org.horiam.ResourceManager.soap.MessageHolderBean;
import org.horiam.ResourceManager.soap.ResourceManagerFault;
import org.horiam.ResourceManager.soap.TaskSEI;

@Stateless // TODO
@WebService(serviceName = "TaskWS",		
targetNamespace = "http://ResourceManager/wsdl",
endpointInterface = "org.horiam.ResourceManager.soap.TaskSEI")
public class TaskWS implements TaskSEI {

	@EJB
	private TaskService taskService;

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#list()
	 */
	@Override
	public List<Task> list() {
		return taskService.list();
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#exists(java.lang.String)
	 */
	@Override
	public boolean exists(String id) {
		return taskService.exists(id);
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#get(java.lang.String)
	 */
	@Override
	public Task get(String id) throws ResourceManagerFault {
		try {
			return taskService.get(id);
		} catch (RecordNotFoundException e) {
			throw new ResourceManagerFault(e.getMessage(), new MessageHolderBean());
		}
	}

	/* (non-Javadoc)
	 * @see org.horiam.ResourceManager.webapp.soapful.TaskSEI#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		taskService.delete(id);
	}	
}
