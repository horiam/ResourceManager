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

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.horiam.ResourceManager.businessLogic.AlloctionDriver;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.test.ContainerWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestClassFinder {
	
	@EJB
	protected ClassFinder classFinder;
	
	protected EJBContainer container;
	
	@Before
	public void setup() throws NamingException {

		container = EJBContainer.createEJBContainer();
		container.getContext().bind("inject", this);
	}
	
	@After
	public void tearDown() {
		
		container.close();
	}
	
	@Test
	public void testClassFinder() throws Exception {
		System.out.println("\nTest ClassFinder EJB...\n");
		//ClassFinder classFinder = (ClassFinder) lookup("java:global/Beans/ClassFinder!" + ClassFinder.class.getName());
		
		assertTrue("Should return a subclass", User.class.isAssignableFrom(classFinder.getUserClass()));
		assertTrue("Should return a subclass", Resource.class.isAssignableFrom(classFinder.getResourceClass()));
		assertTrue("Should return a subclass", AlloctionDriver.class.isAssignableFrom(classFinder.getAllocatorDriverClass()));
		assertTrue("Should be an instance of/sublclass", (classFinder.getAllocateDriverInstance() instanceof AlloctionDriver));
		// TODO test class lookup
	}
}
