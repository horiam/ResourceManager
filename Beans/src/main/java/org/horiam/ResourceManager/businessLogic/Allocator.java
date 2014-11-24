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

package org.horiam.ResourceManager.businessLogic;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.businessLogic.exceptions.ResourceUnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UserUnrecoverableException;
import org.horiam.ResourceManager.dao.ClassFinder;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.model.Resource;


@Stateless
public class Allocator {
	
	private AlloctionDriver driver;
	@EJB
	private ClassFinder classFinder;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources;
	
	
	@PostConstruct
	public void postConstruct() throws Exception {
		driver = classFinder.getAllocateDriverInstance();
	}
	
	public void attachUser(String userId, String resourceId) 
			throws InterruptedException, UnrecoverableException, 
			ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {
		
		User user = users.getLock(userId);
		Resource resource = resources.get(resourceId);
		
		driver.allocate(user, resource);
									
		resource.setUser(user);
		user.setResource(resource);
		users.update(user);
	}
		
	public void detachUser(String userId) 
			throws InterruptedException, UnrecoverableException, 
			ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {
		
		User user = users.getLock(userId);	
		Resource resource = user.getResource();
		
		driver.deallocate(user, resource);
		
		user.removeResource();
		users.update(user);					
	}	
}
