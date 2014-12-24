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

	private static final String PROPFILENAME = "ResourceManager.properties";

	private Class<? extends User> userClass;
	private Class<? extends Resource> resourceClass;
	private Class<? extends AllocationDriver> allocatorDriverClass;
	private Properties properties = null;
	
	
	@PostConstruct
	protected void postConstruct() {
			
		setUserClass(findClass(User.class));
		setResourceClass(findClass(Resource.class));
		setAllocatorDriverClass(findClass(AllocationDriver.class));
	}

	private <C> Class<? extends C> findClass(Class<C> clazz) {

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
				System.out.println("ClassFinder: found " + canonicalName);
				if (canonicalName != null && canonicalName.isEmpty() == false) {
					Class<?> found = Class.forName(canonicalName);
					System.out.println("ClassFinder: will use class "
							+ found.getCanonicalName());
					return (Class<? extends C>) found;
				}
			}	
			
		} catch (ClassNotFoundException e) {
			//TODO trace
		} catch (IOException e) {
			//TODO trace
		}
		
		System.out.println("ClassFinder: will use default class "+ clazz.getCanonicalName());
		return clazz;
	}
	public Class<? extends User> getUserClass() {
		return userClass;
	}

	public void setUserClass(Class<? extends User> userClass) {
		this.userClass = userClass;
	}

	public Class<? extends Resource> getResourceClass() {
		return resourceClass;
	}

	public void setResourceClass(Class<? extends Resource> resourceClass) {
		this.resourceClass = resourceClass;
	}

	public Class<? extends AllocationDriver> getAllocatorDriverClass() {
		return allocatorDriverClass;
	}

	public void setAllocatorDriverClass(Class<? extends AllocationDriver> allocatorDriverClass) {
		this.allocatorDriverClass = allocatorDriverClass;
	}	
	
	public AllocationDriver getAllocateDriverInstance() throws Exception {
		try {
			return getAllocatorDriverClass().getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new Exception(e);
		}
	}
}
