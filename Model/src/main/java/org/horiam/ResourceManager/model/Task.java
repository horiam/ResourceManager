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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.horiam.ResourceManager.model.XmlAdapter.ResourceXmlAdapter;
import org.horiam.ResourceManager.model.XmlAdapter.UserXmlAdapter;


@Entity
@Table(name = "TASK")
@XmlRootElement(name="Task")
@NamedQueries({
@NamedQuery(name = "Task.getAll", query = "SELECT a FROM Task a"),
@NamedQuery(name = "Task.deleteOlderThan", query = "DELETE FROM Task a WHERE a.date < :since")
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends Model implements Serializable {
	
	private static final long serialVersionUID = 4893437168564164967L;

	public enum Status {
		PROCESSING, FAILED, SUCCEEDED;
	}
	

	@Enumerated(EnumType.STRING)
	private Status status;	
	@OneToOne
	@XmlJavaTypeAdapter(UserXmlAdapter.class)
	private User user; 
	@OneToOne
	@XmlJavaTypeAdapter(ResourceXmlAdapter.class)	
	private Resource resource;	   
	private String message;
	private String type;
	@Column(nullable = false)
	private boolean retryable;

	
	public Task() {
		super();
		setStatus(Status.PROCESSING);
		setRetryable(false);
	}
	
	public Task(String id) {
		this();
		setId(id);
	}
		
	public Task(String id, String type) {
		this(id);
		setType(type);
	}	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Resource getResource() {
		return resource;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void succeeded() {		
		status = Status.SUCCEEDED;
	}

	public void failed() {		
		status = Status.FAILED;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isRetryable() {
		return retryable;
	}

	public void setRetryable(boolean retry) {
		this.retryable = retry;
	}
	
	public boolean equals(Object other) {
		
		if (other == null)
			return false;
		
		if (other == this)
			return true;
		
		if (this.getClass().equals(other.getClass())) {
			Task cast = (Task) other;
			
			String userId = null;
			String castUserId = null;
			if (user != null) 
				userId = user.getId();
			if (cast.getUser() != null) 
				castUserId = cast.getUser().getId();
					
			String resourceId = null;
			String castResourceId = null;
			if (resource != null) 
				resourceId = resource.getId();
			if (cast.getResource() != null) 
				castResourceId = cast.getResource().getId();
			
			return new EqualsBuilder().appendSuper(super.equals(other))
									  .append(message, cast.getMessage())
									  .append(status.toString(), cast.getStatus().toString())
									  .append(type, cast.getType())
									  .append(userId, castUserId)
									  .append(resourceId, castResourceId)
									  .isEquals();
		}
		return false;
	}
	
	public int hashCode() {
		 return new HashCodeBuilder(17, 83).append(serialVersionUID)
									       .toHashCode();
	}
}
