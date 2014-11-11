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

package org.horiam.ResourceManager.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.horiam.ResourceManager.model.XmlAdapter.ResourceXmlAdapter;


@Entity
@Table(name = "USER")
@NamedQuery(name = "User.getAll", query = "SELECT a FROM User a")
@XmlRootElement(name="User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends ModelWithTask implements Serializable {


	private static final long serialVersionUID = -2049975291403443474L;
	
	@XmlJavaTypeAdapter(ResourceXmlAdapter.class)
	@OneToOne(cascade=CascadeType.ALL)
	private Resource resource;
	@XmlTransient
	@Version
	private long version;
	
	
	public User(){
		super();
	}
	
	public User(String id) {
		this();
		setId(id);
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void removeResource() {		
		resource = null;
	}	
	
	public boolean equals(Object other) {
		
		if (other == this)
			return true;
		
		if (this.getClass().equals(other.getClass())) {
			User cast = (User) other;			
			String resourceId = null;
			String castResourceId = null;
			if (resource != null) 
				resourceId = resource.getId();
			if (cast.getResource() != null) 
				castResourceId = cast.getResource().getId();

			return new EqualsBuilder().appendSuper(super.equals(other))
									  .append(resourceId, castResourceId)
									  .isEquals();
		}
		return false;
	}
	
	public int hashCode() {
		 return new HashCodeBuilder(17, 79).append(serialVersionUID)
									       .toHashCode();
	}
}
