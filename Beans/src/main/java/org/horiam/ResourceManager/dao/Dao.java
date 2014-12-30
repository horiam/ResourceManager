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
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;

public abstract class Dao<E> {

	protected static final String CLASS_NAME = Dao.class.getName();
	protected static final Logger log = Logger.getLogger(CLASS_NAME);

	protected Class<? extends E> entityClass;

	@PersistenceContext
	protected EntityManager em;

	protected void setEntityClass(Class<? extends E> entityClass) {
		log.entering(CLASS_NAME, "setEntityClass", new Object[] { entityClass });
		this.entityClass = entityClass;
		log.exiting(CLASS_NAME, "setEntityClass");
	}

	public void create(E entity) {
		log.entering(CLASS_NAME, "create", new Object[] { entity });
		em.persist(entity);
		log.exiting(CLASS_NAME, "create");
	}

	public boolean exists(String id) {
		log.entering(CLASS_NAME, "exists", new Object[] { id });

		E entity = em.find(entityClass, id);
		boolean ret = (entity != null);

		log.exiting(CLASS_NAME, "exists", ret);
		return ret;
	}

	public E get(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "get", new Object[] { id });

		E entity = em.find(entityClass, id);
		if (entity == null) {
			RecordNotFoundException rnfe = new RecordNotFoundException(
					entityClass.getSimpleName() + " " + id + " was not found");
			
			log.throwing(CLASS_NAME, "get", rnfe);
			throw rnfe;
		}

		log.exiting(CLASS_NAME, "get", entity);
		return entity;
	}

	public E getLock(String id) throws RecordNotFoundException {
		log.entering(CLASS_NAME, "getLock", new Object[] { id });

		E entity = em.find(entityClass, id, LockModeType.OPTIMISTIC);
		if (entity == null) {
			RecordNotFoundException rnfe = new RecordNotFoundException(
					entityClass.getSimpleName() + " " + id + " was not found");
			log.throwing(CLASS_NAME, "getLock", rnfe);
			throw rnfe;
		}
		
		log.exiting(CLASS_NAME, "getLock", entity);
		return entity;
	}

	public E update(E entity) {
		log.entering(CLASS_NAME, "update", new Object[] { entity });
		E ret = em.merge(entity);
		log.exiting(CLASS_NAME, "update", ret);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<E> list() {
		log.entering(CLASS_NAME, "list");

		Query query = em.createQuery(
				"SELECT a FROM " + entityClass.getSimpleName() + " a",
				entityClass);
		List<E> ret = query.getResultList();
		
		log.exiting(CLASS_NAME, "list", ret);
		return ret;
	}

	public void remove(String id) {
		log.entering(CLASS_NAME, "remove", new Object[] { id });
		E entity = em.find(entityClass, id);
		if (entity != null)
			em.remove(entity);
		log.exiting(CLASS_NAME, "remove");
	}

	public void clear() {
		log.entering(CLASS_NAME, "clear");
		for (E entity : list())
			em.remove(entity);
		log.exiting(CLASS_NAME, "clear");
	}
}
