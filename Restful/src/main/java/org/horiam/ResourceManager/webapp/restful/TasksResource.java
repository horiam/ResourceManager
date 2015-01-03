/*
 * Copyright (C) 2014  Horia Musat
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.horiam.ResourceManager.webapp.restful;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.horiam.ResourceManager.services.TaskService;
import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;


@Path("tasks")
public class TasksResource {
	
	private static final String CLASS_NAME = TasksResource.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	@EJB
	private TaskService taskService;
	
	
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Task> getTasks() {
		log.entering(CLASS_NAME, "getTasks");
		List<Task> ret = taskService.list();
		log.exiting(CLASS_NAME, "getTasks", ret);
		return ret;
	}
			
	@Path("{id}")
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getTask(@PathParam("id") String id) {
		log.entering(CLASS_NAME, "getTask", new Object[] { id });
		Response ret = null; 
		try {	
			ret = Response.ok(taskService.get(id)).build();			
		} catch (RecordNotFoundException ex) {
			log.log(Level.FINEST, ex.getMessage(), ex);
			ret = Response.status(404).build();
		} catch (AuthorisationException e) {			
			log.log(Level.FINEST, e.getMessage(), e);
			ret = Response.status(401).build();
		} 
		log.exiting(CLASS_NAME, "getTask", ret);
		return ret;
	}	
	
	@Path("{id}")
	@DELETE
	public Response deleteTask(@PathParam("id") String id) {
		log.entering(CLASS_NAME, "deleteTask", new Object[] { id });
		taskService.delete(id);	
		Response ret = Response.ok().build();
		log.exiting(CLASS_NAME, "deleteTask", ret);
		return ret;
	}		
}
