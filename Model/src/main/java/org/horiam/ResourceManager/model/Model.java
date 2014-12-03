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
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@MappedSuperclass
public abstract class Model implements Serializable { // TODO rename it RecordModel


	private static final long serialVersionUID = 7196486256060530794L;
	
	@Id
	private String id;
    @Column(name="DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
	private Date date;
    
	
	public Model() {
		this.date = new Date();
	}
	
	public Model(String id) {
		this();
		setId(id);
	}
		
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean equals(Object other) {
		
		if (other == null)
			return false;
		
		if (other == this)
			return true;
		
		if (this.getClass().equals(other.getClass())) {
			Model cast = (Model) other;
			return new EqualsBuilder().append(id, cast.getId()) 
									  .append(date, cast.getDate())
									  .isEquals();
		}
		return false;
	}
	
	public int hashCode() {
		 return new HashCodeBuilder(17, 77).append(id)
									       .append(date)
									       .toHashCode();
	}
}
