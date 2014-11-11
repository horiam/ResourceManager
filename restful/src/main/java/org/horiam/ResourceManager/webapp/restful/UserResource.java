package org.horiam.ResourceManager.webapp.restful;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("me")
public class UserResource {
	
	@Context 
	private HttpServletRequest req;
	@EJB //TODO
	private UsersResource users;
	@EJB //TODO
	private ResourcesResource resources;	
	@EJB //TODO
	private TasksResource tasks;	

	
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getMyUser() {
		
		String myUsername = getMyUsername();
		return users.getUserXML(myUsername);
	}

	@Path("/resource")
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getMyRequest() {
		
		String myResource = getMyResourceId();
		return resources.getResourceXML(myResource);
	}
	
	@Path("/task")
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getMyTask() {
		
		String myTask = getMyTaskId();
		return tasks.getTaskXML(myTask);
	}	
	
	private String getMyUsername() {
		return req.getUserPrincipal().getName();
	}
	
	private String getMyResourceId() {
		return null; // TODO		
	}	
	
	private String getMyTaskId() {
		return null; // TODO
	}
}
