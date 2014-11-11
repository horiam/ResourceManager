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

import java.security.Principal;

import org.apache.catalina.realm.JAASRealm;

public class JaasCustomRealm extends JAASRealm {

		
	protected boolean hasMessageDigest() {
		
		return super.hasMessageDigest();
	}
	
	
	protected String digest(String pass) {
		
		return super.digest(pass);
	}
	
	private static String processUsername(String username) {

		if (username.endsWith("@example.com")) {
		
			int atIdx = username.indexOf('@');
			if (atIdx > 0) {
				return username.substring(0, atIdx);					
			}
		}
		
		return username;		
	}
	
    public Principal authenticate(String username, String credentials) {
    	String processedUsername = processUsername(username);
        return authenticate(username, new CustomJaasCallbackHandler(this, processedUsername, credentials));
    }
}
