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

import javax.ejb.ApplicationException;
import javax.xml.ws.WebFault;

@ApplicationException
@WebFault(name="SimpleException")
public class ResourceManagerFault extends Exception {
	
	private static final long serialVersionUID = 4846502381900933305L;
	private MessageHolderBean faultBean;
	 
    public ResourceManagerFault(String message, MessageHolderBean faultInfo){
        super(message);
        faultBean = faultInfo;
    }
 
    public ResourceManagerFault(String message, MessageHolderBean faultInfo, Throwable cause) {
        super(message, cause);
        faultBean = faultInfo;
    }
 
    public MessageHolderBean getFaultInfo(){
        return faultBean;
    }
}
