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

package org.horiam.ResourceManager.soap;

import java.util.List;

import javax.jws.WebService;

import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@WebService(targetNamespace = "http://ResourceManagerNS/Users")
public interface UserSEI {

	public abstract List<User> list();

	public abstract boolean exists(String id) throws ResourceManagerFault;

	public abstract void createOrUpdate(String id, User user) throws ResourceManagerFault;

	public abstract User get(String id) throws ResourceManagerFault;

	public abstract void delete(String id);

	public abstract Task allocateUser(String id) throws ResourceManagerFault;

	public abstract Task deallocateUser(String id)
			throws ResourceManagerFault;

	public abstract Task removeUser(String id) throws ResourceManagerFault;

}