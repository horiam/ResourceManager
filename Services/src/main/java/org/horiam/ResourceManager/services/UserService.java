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

package org.horiam.ResourceManager.services;

import java.util.List;

import javax.ejb.Local;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task;
import org.horiam.ResourceManager.model.User;

@Local
public interface UserService {

	abstract List<User> list();
	
	////////////////////////////////////////////////////////////////////////////
	abstract boolean exists(String id);

	////////////////////////////////////////////////////////////////////////////

	abstract void createOrUpdate(String id, User user);

	////////////////////////////////////////////////////////////////////////////

	abstract User get(String id) throws AuthorisationException, RecordNotFoundException;


	abstract void delete(String id);

	////////////////////////////////////////////////////////////////////////////

	abstract Task allocateUser(String id) throws RecordNotFoundException;

	////////////////////////////////////////////////////////////////////////////


	abstract Task deallocateUser(String id) throws RecordNotFoundException;

	////////////////////////////////////////////////////////////////////////////

	abstract Task removeUser(String id) throws RecordNotFoundException;

}