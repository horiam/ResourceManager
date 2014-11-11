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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.horiam.ResourceManager.model.EntityNotFoundException;


public abstract class Dao<E> {

	protected Class<? extends E> entityClass;
		
	@PersistenceContext
	protected EntityManager em;
	
	
	protected void setEntityClass(Class<? extends E> entityClass) {
		this.entityClass = entityClass;
	}
	
	public void create(E entity) {
		em.persist(entity);		
	}

	public boolean exists(String id) {
		E entity = em.find(entityClass, id);
		if (entity != null)
			return true;
		
		return false;
	}

	public E get(String id) throws EntityNotFoundException {
		E entity = em.find(entityClass, id);
		if (entity == null)
			throw new EntityNotFoundException(entityClass.getSimpleName() + " " + id + " was not found");
		
		return entity;
	}

	public E getLock(String id) throws EntityNotFoundException {		
		E entity = em.find(entityClass, id, LockModeType.OPTIMISTIC);
		if (entity == null)
			throw new EntityNotFoundException(entityClass.getSimpleName() + " " + id + " was not found");
		
		return entity;
	}

	public E update(E entity) {		
		return em.merge(entity);
	}

	@SuppressWarnings("unchecked")
	public List<E> list() {		
		Query query = em.createQuery("SELECT a FROM " + entityClass.getSimpleName() + " a", entityClass);
		return query.getResultList();
	}

	public void remove(String id) {	
		E entity =  em.find(entityClass, id);
		if (entity != null)
			em.remove(entity);
	}	
}
