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


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.horiam.ResourceManager.model.XmlAdapter.TaskXmlAdapter;


@MappedSuperclass
@XmlRootElement(name="ModelWithTask")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ModelWithTask extends Model {
	
	@XmlJavaTypeAdapter(TaskXmlAdapter.class)
	@OneToOne
	private Task task;
	@XmlTransient
	@Column(nullable = false)
	private boolean booked;
	
	
	public ModelWithTask() {
		super();
		setBooked(false);
	}	
	
	public boolean isBooked() {
		return booked;
	}
	
	public void setBooked(boolean booked) {
		this.booked = booked;
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}
	
	public boolean hasTask() {
		
		if (getTask()== null)
			return false;
		
		return true;
	}
		
	public void removeTask() {
		
		this.task = null;
	}	
	
	public boolean equals(Object other) {
		
		if (other == null)
			return false;
		
		if (other == this)
			return true;
		
		if (this.getClass().equals(other.getClass())) {
			ModelWithTask cast = (ModelWithTask) other;
			
			String taskId = null;
			String castTaskId = null;
			if (task != null) 
				taskId = task.getId();
			if (cast.getTask() != null) 
				castTaskId = cast.getTask().getId();

			return new EqualsBuilder().appendSuper(super.equals(other))
									  .append(booked, cast.isBooked()) 
									  .append(taskId, castTaskId)
									  .isEquals();
		}
		return false;
	}
	
	public int hashCode() {
		 return new HashCodeBuilder(17, 78).appendSuper(super.hashCode())
				 						   .append(booked)
									       .toHashCode();
	}
}
