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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import static javax.ejb.LockType.READ;

import javax.ejb.Lock;

import org.horiam.ResourceManager.businessLogic.AllocationDriver;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.User;


@Singleton
@Lock(READ)
public class ClassFinder {
	
	private static final String CLASS_NAME = ClassFinder.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	private static final String PROPFILENAME = "ResourceManager.properties";

	private Class<? extends User> userClass;
	private Class<? extends Resource> resourceClass;
	private Class<? extends AllocationDriver> allocatorDriverClass;
	private Properties properties = null;
	
	
	@PostConstruct
	protected void postConstruct() {
		log.entering(CLASS_NAME, "postConstruct", new Object[] {});	
		setUserClass(findClass(User.class));
		setResourceClass(findClass(Resource.class));
		setAllocatorDriverClass(findClass(AllocationDriver.class));
		log.exiting(CLASS_NAME, "postConstruct");
	}

	private <C> Class<? extends C> findClass(Class<C> clazz) {
		log.entering(CLASS_NAME, "findClass", new Object[] { clazz });
		try {
			if (properties == null) {
				URL url = ClassLoader.getSystemResource(PROPFILENAME);
				if (url != null) {
					InputStream is = url.openStream();
					properties = new Properties();
					properties.load(is);
					is.close();
				}
			}	

			if (properties != null) {
				String canonicalName = properties.getProperty(clazz
						.getCanonicalName());
				if (canonicalName != null && canonicalName.isEmpty() == false) {
					Class<?> found = Class.forName(canonicalName);
					log.config("Will use class " + found.getCanonicalName());
					log.exiting(CLASS_NAME, "findClass");
					return (Class<? extends C>) found;
				}
			}	
			
		} catch (ClassNotFoundException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		} catch (IOException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
		
		log.config("Will use default class "+ clazz.getCanonicalName());
		log.exiting(CLASS_NAME, "findClass");
		return clazz;
	}
	public Class<? extends User> getUserClass() {
		return userClass;
	}

	private void setUserClass(Class<? extends User> userClass) {
		this.userClass = userClass;
	}

	public Class<? extends Resource> getResourceClass() {
		return resourceClass;
	}

	private void setResourceClass(Class<? extends Resource> resourceClass) {
		this.resourceClass = resourceClass;
	}

	public Class<? extends AllocationDriver> getAllocatorDriverClass() {
		return allocatorDriverClass;
	}

	private void setAllocatorDriverClass(Class<? extends AllocationDriver> allocatorDriverClass) {
		this.allocatorDriverClass = allocatorDriverClass;
	}	
	
	public AllocationDriver getAllocateDriverInstance() throws Exception {
		log.entering(CLASS_NAME, "getAllocateDriverInstance");
		try {
			return getAllocatorDriverClass().getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.throwing(CLASS_NAME, "getAllocateDriverInstance", e);
			throw new Exception(e);
		}
	}
}
