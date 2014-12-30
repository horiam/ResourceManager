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

package org.horiam.ResourceManager.jms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;

import org.horiam.ResourceManager.exceptions.AuthorisationException;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.services.UserService;

@RunAs("Admin")
@MessageDriven
(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",  
                				 propertyValue = "javax.jms.Queue"),                 
        @ActivationConfigProperty(propertyName = "destination",  
                				 propertyValue = "jms/usersQueue")})
public class UsersMdb extends BaseMdb implements MessageListener {

	protected static final String CLASS_NAME = UsersMdb.class.getName();

	@EJB
	private UserService userService;
	
	@Override
	public Serializable createResponseObject(String recvText) throws AuthorisationException,
																	RecordNotFoundException {
		log.entering(CLASS_NAME, "createResponseObject", new Object[] { recvText });

		Serializable ret; 

		if (recvText == null || recvText.isEmpty()) {
			List<User> usersList = userService.list();
			ArrayList<User> serialisable = new ArrayList<User>();
			serialisable.addAll(usersList);
			ret = serialisable;
		} else {
			ret =userService.get(recvText);
		}
		
		log.exiting(CLASS_NAME, "createResponseObject", ret);
		return ret;
	}

}
