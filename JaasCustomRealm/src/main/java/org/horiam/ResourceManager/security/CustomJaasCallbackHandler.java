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

package org.horiam.ResourceManager.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;


public class CustomJaasCallbackHandler implements CallbackHandler {
	
	
	private String username  = null;
	private String password  = null;
	
	
	public CustomJaasCallbackHandler(String username, String password) {
		
		super();
		
		this.username = username; 
		this.password = password;
	}
	
	public CustomJaasCallbackHandler(JaasCustomRealm realm, String username, String password) {
		
		super();
		
		this.username = username; 
				
		if (realm.hasMessageDigest())
			this.password = realm.digest(password);
		else
			this.password = password;		
	}
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		
		for (int i = 0; i < callbacks.length; i++) {
			
			if (callbacks[i] instanceof NameCallback) {

				((NameCallback) callbacks[i]).setName(username);
				
			} else if (callbacks[i] instanceof PasswordCallback) {
				
				char[] passArray;
				
				if (password != null)
					passArray = password.toCharArray();
				else
					passArray = new char[0];
				
                ((PasswordCallback) callbacks[i]).setPassword(passArray);
			} else {
				 throw new UnsupportedCallbackException(callbacks[i]);
			}
		}		
	}
}
