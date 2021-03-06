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

package org.horiam.ResourceManager.exceptions;

import javax.ejb.ApplicationException;
import javax.xml.ws.WebFault;

@ApplicationException
public class AuthorisationException extends Exception {
	
	private static final long serialVersionUID = -80465465266714602L;
	

	public AuthorisationException(String message) {
		super(message);
	}
	
	public AuthorisationException(Throwable throwable) {
		super(throwable);
	}

	public AuthorisationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
