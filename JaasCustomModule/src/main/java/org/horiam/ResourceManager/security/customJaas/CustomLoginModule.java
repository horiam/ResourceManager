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

package org.horiam.ResourceManager.security.customJaas;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;


public class CustomLoginModule implements LoginModule {

	private CallbackHandler handler;
	private Subject subject;
	private String login;
	private List<String> roles;
	private UserPrincipal userPrincipal;
	private RolePrincipal rolePrincipal;
	
	private static Map<String, String> localLogins = null;
	
	
	@Override
	public void initialize(Subject subject, CallbackHandler handler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.handler = handler;
		this.subject = subject;
	}

	@Override
	public boolean login() throws LoginException {

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("login");
		callbacks[1] = new PasswordCallback("password", true);

		try {

			handler.handle(callbacks);
			String name = ((NameCallback) callbacks[0]).getName();
			String password = String.valueOf(((PasswordCallback) callbacks[1])
																.getPassword());

			if (isUserValid(name, password)) {
				System.out.println("User "+name+" is Authenticated");
				return true;
			}
			
			System.out.println("User "+name+" is NOT Authenticated");
			throw new LoginException("Authentication failed");

		} catch (IOException e) {
			throw new LoginException(e.getMessage());
		} catch (UnsupportedCallbackException e) {
			throw new LoginException(e.getMessage());
		}
	}
		
	private boolean isUserValid(String userName, String password) {
		
		if (isLocallyValid(userName, password)) {
			return true;
		} 
		// other condition here
		return false;
	}
	
	private void loadLocalLogins(String filename) {
		
		CustomLoginModule.localLogins = new HashMap<String, String>();
		
		System.out.println("loadLocalLogins " + filename);
		
		BufferedReader br = null;
		try {
			
			br = new BufferedReader(new FileReader(filename));
			String line;
			
			while ((line = br.readLine()) != null) {
				int whitespace = line.indexOf(' ');
				if (whitespace > 0) {
					String userName = line.substring(0, whitespace);
					String rest = line.substring(whitespace + 1);
					System.out.println("loadLocalLogins "+ userName + " " + rest);
					CustomLoginModule.localLogins.put(userName, rest);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace(); // TODO trace
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isLocallyValid(String userName, String password) {
		
		if (localLogins == null) {		
			String filename = "E:\\temp\\logins.txt"; // TODO 
			loadLocalLogins(filename);
		}
				
		if (CustomLoginModule.localLogins.containsKey(userName)) {
			
			String passAndRoles = CustomLoginModule.localLogins.get(userName);
				
			if (passAndRoles != null) {
				int whitespace = passAndRoles.indexOf(' ');
				if (whitespace > 0) {
					if (password.equals(passAndRoles.substring(0, whitespace))) {
						String[] rolesArray = passAndRoles.substring(whitespace + 1).split(" ");
						this.roles = Arrays.asList(rolesArray);
						return true;
					}				
				}					
			}				
		}
		
		return false;
	}

	@Override
	public boolean commit() throws LoginException {

		userPrincipal = new UserPrincipal(login);				
		subject.getPrincipals().add(userPrincipal);

		if (roles != null && roles.size() > 0) {
			for (String roleName : roles) {
				rolePrincipal = new RolePrincipal(roleName);
				subject.getPrincipals().add(rolePrincipal);
			}
		}
		
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		return false;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		subject.getPrincipals().remove(rolePrincipal);
		return true;
	}
}
