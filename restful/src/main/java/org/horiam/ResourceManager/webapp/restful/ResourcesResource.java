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

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.horiam.ResourceManager.services.ResourceService;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;


@Path("resources")
public class ResourcesResource {

	@EJB
	private ResourceService resourceService;

	
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Resource> getResources() {
		
		return resourceService.list();
	}
	
	@Path("{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Resource putResource(@Context UriInfo uriInfo, 
					   			 @PathParam("id") String id, JAXBElement<? extends Resource> xml) {
		
		Resource resource = xml.getValue();
		resourceService.createOrUpdate(id, resource);		
		return  resource;
	}
	
	@Path("{id}")
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getResource(@PathParam("id") String id) {
			
		try {		
			return Response.ok(resourceService.get(id)).build();
		} catch (RecordNotFoundException ex) {
			return Response.status(404).build();
		}
	}	
		
	@Path("{id}")
	@DELETE
	public Response deleteResource(@PathParam("id") String id) {
		
		resourceService.delete(id);		
		return Response.ok().build();
	}
	
	@Path("{id}")
	@POST
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postResourceAction(@PathParam("id") String id, @QueryParam("action") String action) {
		
		try {
			if (action != null) {
				switch (action) {			
					case "remove" : return Response.ok(resourceService.removeResource(id)).build();	
					default : 			
				}
			}
			return Response.status(400).build();
		} catch (RecordNotFoundException ex) {
			return Response.status(404).build();
		}		
	}	
}
