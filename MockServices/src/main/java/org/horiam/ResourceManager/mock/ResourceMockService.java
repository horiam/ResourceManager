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

package org.horiam.ResourceManager.mock;

import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.ResourceService;

@Singleton
@Lock(READ)
public class ResourceMockService implements ResourceService {
	
	public final static String[]  initialResourceIds = {"resource1", "resouce2"};
	public final static Resource[] initialResources = {new Resource(initialResourceIds[0]), 
														new Resource(initialResourceIds[1])};
	
	private List<Resource> resources;
	
	@PostConstruct
	protected void postConstruct() {
		resources = new ArrayList<Resource>();
		resources.addAll(Arrays.asList(initialResources));
	}

	@Override
	public boolean exists(String id) {
		if (getResource(id) != null)
			return true;
		
		return false;
	}

	@Override
	public Resource get(String id) throws RecordNotFoundException {
		Resource resource = getResource(id);
		if (resource != null)
			return resource;
		
		throw new RecordNotFoundException(id);
	}

	@Override
	public void createOrUpdate(String id, Resource resource) {
		addResource(resource);
	}

	@Override
	public List<Resource> list() {
		return resources;
	}

	@Override
	public void delete(String id) {
		deleteResource(id);
	}

	@Override
	public Task removeResource(String id) throws RecordNotFoundException {
		Resource resource = get(id);
		deleteResource(id);
		Task task = new Task("mock removeResource");
		task.setType("removeResource");
		return task;
	}
	
	private Resource getResource(String id) {		
		for (Resource resource : resources) {
			if (resource.getId().equals(id))
				return resource;
		}
		return null;
	}
	@Lock(WRITE)
	private void addResource(Resource resource) {
		resources.add(resource);
	}
	@Lock(WRITE)
	private void deleteResource(String id) {
		Resource resource  = getResource(id);
		if (resource != null) {
			resources.remove(resource);
		}
	}

}
