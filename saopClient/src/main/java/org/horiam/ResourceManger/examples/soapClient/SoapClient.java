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

package org.horiam.ResourceManger.examples.soapClient;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.soapSEI.ResourceManagerSEI;


public class SoapClient {
	
	private static QName getServiceName() {
		return new QName("http://ResourceManager/wsdl", "ResourceManagerWS");
	}
	
	private static URL getWsdlURL() throws MalformedURLException {
		return new URL("http://localhost:8080/ResourceManager/ResourceManagerWS?wsdl");
	}

	public static void main(String args[]) throws Exception {
		
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("admin", "super".toCharArray());
			}
		});
	
		Service service = Service.create(getWsdlURL(), getServiceName());		
		ResourceManagerSEI port = service.getPort(ResourceManagerSEI.class);
				
		////////////////////////////////////////////////////////////////////////////
		
		System.out.println("List all Resources :");
		List<Resource> listResources = (List<Resource>) port.listResources(); // TODO
		for (Resource resource : listResources)
			System.out.println(resource.getId());

		////////////////////////////////////////////////////////////////////////////
		
		List<String> resources = new ArrayList<String>();
		resources.add("resource1"); resources.add("resource2"); 
		//resources.add("resource3"); resources.add("resource4"); resources.add("resource5");
		for (String resourceId : resources) {		
			Resource resource = new Resource(resourceId);
			
			System.out.println("Delete Resource "+resourceId+" :");		
			port.deleteResource(resourceId);
	
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("Create resource "+resourceId+":");
			port.createOrUpdateResource(resourceId, resource);
			
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("Get resource "+resourceId+":");			
			Resource resource2 = port.getResource(resourceId);
			
			String id = resource2.getId();			
			System.out.println("Get id="+id);
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		List<String> users = new ArrayList<String>();
		users.add("user2"); users.add("user1"); users.add("user3");
		for (String userName : users) {								
			User user = new User(userName);	
			
			System.out.println("Delete user "+userName+" :");
			port.deleteUser(userName);
			
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("Create user "+userName+" :");
			port.createOrUpdateUser(userName, user);
			
			////////////////////////////////////////////////////////////////////////////	
			
			System.out.println("Get user "+userName+" :");			
			User user2 = port.getUser(userName);	
			
			System.out.println("GET id="+user2.getId());				
		}
		
		////////////////////////////////////////////////////////////////////////////

		for (String userName : users) {	
		
			System.out.println("Attach user "+userName+" :");
	
			Task task = port.attachUser(userName);	
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId() +" status="+task.getStatus());		
		}
		
		////////////////////////////////////////////////////////////////////////////
						
		for (String userName : users) {	
			System.out.println("Remove user "+userName+" :");
			
			Task task = port.removeUser(userName);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId() +" status="+task.getStatus());	
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		for (String resourceId : resources) {	
			System.out.println("Remove resource "+resourceId+" :");
			
			Task task = port.removeResource(resourceId);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId() +" status="+task.getStatus());
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		for (String userName : users) {	
			System.out.println("Detach user "+userName+" :");
			
			Task task = port.detachUser(userName);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId() +" status="+task.getStatus());	
		}	
	}
}
