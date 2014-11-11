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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.horiam.ResourceManager.model.Resource;

@Stateless
public class ResourceDao extends Dao<Resource> {

	@EJB
	protected ClassFinder classFinder;
	
	
	@PostConstruct
	public void postConstruct() {
		setEntityClass(classFinder.getResourceClass());
	}


	@SuppressWarnings("unchecked")
	public List<Resource> getAllFree() {	
		Query query = em.createQuery("SELECT a FROM " + entityClass.getSimpleName() + " a WHERE"
									   + " a.user IS NULL AND a.booked = FALSE", entityClass);
		return query.getResultList();
	}
}
