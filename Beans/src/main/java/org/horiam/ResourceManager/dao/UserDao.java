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

package org.horiam.ResourceManager.dao;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.User;


@Stateless
public class UserDao extends Dao<User> {
	
	protected static final String CLASS_NAME = UserDao.class.getName();

	@EJB
	protected ClassFinder classFinder;
	
	
	@PostConstruct
	public void postConstruct() {
		log.entering(CLASS_NAME, "postConstruct");
		setEntityClass(classFinder.getUserClass());
		log.exiting(CLASS_NAME, "postConstruct");
	}	

	public User setResource(String userId, String resourceId) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "setResource", new Object[] { userId, resourceId });

		User user = get(userId);		
		Resource storage = em.find(classFinder.getResourceClass(), resourceId);
		storage.setUser(user);
		user.setResource(storage);
		
		User ret = em.merge(user);
		log.exiting(CLASS_NAME, "setResource", ret);
		return ret;
	}
}
