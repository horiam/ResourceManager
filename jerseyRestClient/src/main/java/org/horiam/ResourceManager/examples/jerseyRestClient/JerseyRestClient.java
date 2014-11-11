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

package org.horiam.ResourceManager.examples.jerseyRestClient;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;


public class JerseyRestClient {
	
	static boolean https = false;
	
	private static URI getBaseURI() {

		return UriBuilder.fromUri("http://localhost:8080/ResourceManager/rest").build();
	}

	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, 
												  CertificateException, FileNotFoundException, 
												  IOException, KeyManagementException {
		
		ClientConfig config = new DefaultClientConfig();

		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		
		if (https) {
		    KeyStore trustStore;
		    trustStore = KeyStore.getInstance("JKS");
		    trustStore.load(new FileInputStream("myKeyStore"),"myKSPass".toCharArray());
		    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		    tmf.init(trustStore);
			
			SSLContext ctx = SSLContext.getInstance("SSL");
			ctx.init(null, tmf.getTrustManagers(), null);
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(null,ctx));
		}
		
		Client client = Client.create(config);
		client.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter("admin", "super"));
		
		WebResource service = client.resource(getBaseURI());	
		
		////////////////////////////////////////////////////////////////////////////
		
		System.out.println("GET all Resources :");
		ClientResponse response = service.path("resources").accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		System.out.println(response.getStatus());

		////////////////////////////////////////////////////////////////////////////
		
		List<String> resources = new ArrayList<String>();
		resources.add("resource1"); resources.add("resource2"); 
		resources.add("resource3"); resources.add("resource4"); resources.add("resource5");
		for (String resourceId : resources) {		
			Resource resource = new Resource(resourceId);
			
			System.out.println("DELETE Resource "+resourceId+" :");
			
			response = service.path("resources").path(resourceId)
											.accept(MediaType.APPLICATION_XML)
											.delete(ClientResponse.class);		
			System.out.println(response.getStatus());
	
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("PUT resource "+resourceId+":");
			
			response = service.path("resources").path(resourceId)
											.accept(MediaType.APPLICATION_XML)
											.put(ClientResponse.class, resource);
			// Return code should be 201 == created resource
			System.out.println(response.getStatus());		
			
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("GET resource "+resourceId+":");
			
			resource = service.path("resources").path(resourceId).accept(MediaType.APPLICATION_XML).get(Resource.class);		
			//System.out.println(response.getStatus());	
			String id = resource.getId();
			
			System.out.println("GET id="+id);
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		List<String> users = new ArrayList<String>();
		users.add("user2"); users.add("user1"); users.add("user3");
		for (String userName : users) {		
			////////////////////////////////////////////////////////////////////////////						
			User user = new User(userName);		
			System.out.println("DELETE USER "+userName+" :");
			response = service.path("users").path(userName)
											.accept(MediaType.APPLICATION_XML)
											.delete(ClientResponse.class);
			// Return code should be 201 == created resource
			System.out.println(response.getStatus());
			
			////////////////////////////////////////////////////////////////////////////
			
			System.out.println("PUT USER "+userName+" :");
			response = service.path("users").path(userName)
											.accept(MediaType.APPLICATION_XML)
											.put(ClientResponse.class, user);
			// Return code should be 201 == created resource
			System.out.println(response.getStatus());				
			////////////////////////////////////////////////////////////////////////////	
			
			System.out.println("GET user "+userName+" :");
			
			User user2 = service.path("users").path(userName).accept(MediaType.APPLICATION_XML).get(User.class);		
			
			System.out.println("GET id="+user2.getId());	
			
		}
		
		////////////////////////////////////////////////////////////////////////////

		for (String userName : users) {	
		
			System.out.println("POST attach "+userName+" :");
	
			Task task = service.path("users").path(userName).queryParam("action", "attach").accept(MediaType.APPLICATION_XML).post(Task.class);
					
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId());
		
		}
		
		////////////////////////////////////////////////////////////////////////////
						
		for (String userName : users) {	
			System.out.println("POST remove "+userName+" :");
			
			Task task = service.path("users").path(userName).queryParam("action", "remove").accept(MediaType.APPLICATION_XML).post(Task.class);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId());	
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		for (String resourceId : resources) {	
			System.out.println("POST remove "+resourceId+" :");
			
			Task task = service.path("resources").path(resourceId).queryParam("action", "remove").accept(MediaType.APPLICATION_XML).post(Task.class);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId());
		}
		
		////////////////////////////////////////////////////////////////////////////
		
		for (String userName : users) {	
			System.out.println("POST detach "+userName+" :");
			
			Task task = service.path("users").path(userName).queryParam("action", "detach").accept(MediaType.APPLICATION_XML).post(Task.class);
			
			if (task == null)
				System.err.println("Task is null");
			else
				System.out.println("Task id="+task.getId());	
		}		
	}
	
	
}
