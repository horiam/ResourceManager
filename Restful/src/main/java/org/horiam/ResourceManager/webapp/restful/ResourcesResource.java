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
import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;


@Path("resources")
public class ResourcesResource {
	
	private static final String CLASS_NAME = ResourcesResource.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	@EJB
	private ResourceService resourceService;

	
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<Resource> getResources() {
		log.entering(CLASS_NAME, "getResources");
		List<Resource> ret = resourceService.list();
		log.exiting(CLASS_NAME, "getResources", ret);
		return ret;
	}
	
	@Path("{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putResource(@Context UriInfo uriInfo, 
					   			 @PathParam("id") String id, JAXBElement<? extends Resource> xml) {
		log.entering(CLASS_NAME, "putResource", new Object[] { uriInfo, id, xml });
		Resource resource = xml.getValue();
		resourceService.createOrUpdate(id, resource);		
		Response ret = Response.created(uriInfo.getAbsolutePath()).build();
		log.exiting(CLASS_NAME, "putResource", ret);
		return ret;
	}
	
	@Path("{id}")
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getResource(@PathParam("id") String id) {
		log.entering(CLASS_NAME, "getResource", new Object[] { id });
		Response ret; 
		try {		
			ret = Response.ok(resourceService.get(id)).build();
		} catch (AuthorisationException e) {			
			log.log(Level.FINEST, e.getMessage(), e);
			ret = Response.status(401).build();
		} catch (RecordNotFoundException ex) {
			log.log(Level.FINEST, ex.getMessage(), ex);
			ret = Response.status(404).build();
		}
		log.exiting(CLASS_NAME, "getResource", ret);
		return ret;
	}	
		
	@Path("{id}")
	@DELETE
	public Response deleteResource(@PathParam("id") String id) {
		log.entering(CLASS_NAME, "deleteResource", new Object[] { id });
		resourceService.delete(id);		
		Response ret = Response.ok().build();
		log.exiting(CLASS_NAME, "deleteResource", ret);
		return ret;
	}
	
	@Path("{id}")
	@POST
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response postResourceAction(@PathParam("id") String id, @QueryParam("action") String action) {
		log.entering(CLASS_NAME, "postResourceAction", new Object[] { id, action });
		Response ret = null; 
		try {
			if (action != null) {
				switch (action) {			
					case "remove" : ret = Response.ok(resourceService.removeResource(id)).build();	
					break;
					default : ret = Response.status(400).build();
					break;
				}
			} else {
				ret = Response.status(400).build();
			}
		} catch (RecordNotFoundException ex) {
			log.log(Level.FINEST, ex.getMessage(), ex);
			ret = Response.status(404).build();
		}		
		log.exiting(CLASS_NAME, "postResourceAction", ret);
		return ret;
	}	
}
