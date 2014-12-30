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

package org.horiam.ResourceManager.businessLogic;


import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.horiam.ResourceManager.businessLogic.exceptions.RecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.ResourceUnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UnrecoverableException;
import org.horiam.ResourceManager.businessLogic.exceptions.UserUnrecoverableException;
import org.horiam.ResourceManager.dao.ResourceDao;
import org.horiam.ResourceManager.dao.TaskDao;
import org.horiam.ResourceManager.dao.UserDao;
import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.model.Task.Status;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.model.Resource;


@Stateless
@TransactionManagement(value=TransactionManagementType.BEAN)
public class TaskExecutor {
			
	private static final String CLASS_NAME = TaskExecutor.class.getName();
	private static final Logger log = Logger.getLogger(CLASS_NAME);

	@javax.annotation.Resource
	private UserTransaction transaction;
	@EJB
	private Allocator attachService;
	@EJB
	private Booking bookingService;
	@EJB
	private TaskHelper taskHelper;
	@EJB
	private TaskDao tasks;
	@EJB
	private UserDao users;
	@EJB
	private ResourceDao resources; 	
	
	////////////////////////////////////////////////////////////////////////////
	
	@Asynchronous
	public Future<Void> executeTask(String taskId) {
		log.entering(CLASS_NAME, "executeTask", new Object[] { taskId });

		try {						
			try {
				
				switch (taskHelper.getStatus(taskId)) {
					
					case SUCCEEDED : return new AsyncResult<Void>(null);
					
					case FAILED : return new AsyncResult<Void>(null);
					
					case PROCESSING : // we accept				
				}
					
				switch (taskHelper.getType(taskId)) {
				
					case allocateResourceForUser  :  attachUser(taskId);
					break;
					
					case deallocateUser   :  detachUser(taskId);
					break;
					
					case removeUser    :  removeUser(taskId);
					break;
					
					case removeResource :  removeResource(taskId);
					break;
					
					default  :  throw new UnrecoverableException("Unknown type of Task for " + taskId);				
				}
						
				log.exiting(CLASS_NAME, "executeTask");
				return new AsyncResult<Void>(null);
												
			} catch (InterruptedException ie) {
				log.log(Level.SEVERE, ie.getMessage(), ie);
				Thread.interrupted();
				failed(taskId, false, ie.getLocalizedMessage());
				
			} catch (ResourceUnrecoverableException ue) {
				log.log(Level.SEVERE, ue.getMessage(), ue);
				bookingService.freeUserWithTask(taskId); 
				failed(taskId, true, ue.getLocalizedMessage());
						
			} catch (UserUnrecoverableException ue) {	
				log.log(Level.SEVERE, ue.getMessage(), ue);
				bookingService.freeResourceWithTask(taskId);
				failed(taskId, false, ue.getLocalizedMessage());
				
			} catch (HeuristicRollbackException | RollbackException | RecoverableException oe) {
				log.log(Level.WARNING, oe.getMessage(), oe);
				failed(taskId, true, oe.getLocalizedMessage());
	
			} catch (HeuristicMixedException | NotSupportedException | SystemException 
					| IllegalStateException | SecurityException | UnrecoverableException 
					| RecordNotFoundException re) {
				
				log.log(Level.SEVERE, re.getMessage(), re);
				re.printStackTrace();
				failed(taskId, false, re.getLocalizedMessage());			
				
			} finally {
				if (taskHelper.getStatus(taskId) == Status.PROCESSING) { // unhandled exceptions
					failed(taskId, false, "something went wrong with the execution");
				}
				
				taskHelper.fireEvent(taskId);
			}
		
		} catch  (RecordNotFoundException e) { // for the catches
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		log.exiting(CLASS_NAME, "executeTask");
		return new AsyncResult<Void>(null);
	}
	
	////////////////////////////////////////////////////////////////////////////
		
	private void attachUser(String taskId) 
			throws NotSupportedException, SystemException, RecoverableException, 
			IllegalStateException, SecurityException, HeuristicMixedException, 
			HeuristicRollbackException, RollbackException, InterruptedException, 
			UnrecoverableException, ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {
								
			log.entering(CLASS_NAME, "attachUser", new Object[] { taskId });

			/* 1st transaction */				
			transaction.begin();
			
			String userId = taskHelper.getUserId(taskId);
						
			bookingService.reserveUser(taskId, userId);
								
			User user = users.get(userId);
			
			String message = "done";
									
			if (user.getResource() == null) {									
				
				String resourceId = bookingService.reserveOneAvailableResource(taskId);						
				taskHelper.setResource(taskId, resourceId);
				
				transaction.commit(); 
					
				/* 2nd transaction */				
				transaction.begin(); 
				
				attachService.attachUser(userId, resourceId); 
				
				bookingService.freeResource(resourceId);
								
			} else {
				message = "User "+ userId + " has already a Resource " + user.getResource();				
			}
			
			bookingService.freeUser(userId);
			taskHelper.succeeded(taskId, message);
			
			/* end 2nd transaction */
			transaction.commit();													

			log.exiting(CLASS_NAME, "attachUser");
	}	
	
	////////////////////////////////////////////////////////////////////////////
	
	private void failed(String taskId, boolean retryable, String message) {
		log.entering(CLASS_NAME, "failed", new Object[] { taskId, retryable, message });

		try {
			taskHelper.failed(taskId, message, retryable); // CMT 
			transaction.rollback();
		} catch (IllegalStateException | SecurityException | SystemException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		} catch (RecordNotFoundException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
		
		log.exiting(CLASS_NAME, "failed");
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private void detachUser(String taskId) 
			throws NotSupportedException, SystemException, RecoverableException, 
			IllegalStateException, SecurityException, HeuristicMixedException, 
			HeuristicRollbackException, RollbackException, InterruptedException, 
			UnrecoverableException, ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {

			log.entering(CLASS_NAME, "detachUser", new Object[] { taskId });

			/* 1st transaction */			
			transaction.begin();
			
			String userId = taskHelper.getUserId(taskId);
			
			bookingService.reserveUser(taskId, userId);
						
			User user = users.get(userId);
			
			Resource resource = user.getResource();
			
			String message = "done";
			
			if ( resource != null ) {

				taskHelper.setResource(taskId, resource.getId());
				bookingService.reserveResource(taskId, resource.getId());				
				
				transaction.commit();
				
				/* 2nd transaction */				
				transaction.begin();
				
				attachService.detachUser(userId);
				
				bookingService.freeResource(resource.getId());
								
			} else {
				message = "User "+ userId + " has no Resource";
			}
			
			bookingService.freeUser(userId);
			taskHelper.succeeded(taskId, message);
			
			transaction.commit();
			
			log.exiting(CLASS_NAME, "detachUser");
	}	
	
	////////////////////////////////////////////////////////////////////////////
		
	private void removeUser(String taskId) 
			throws NotSupportedException, SystemException, RecoverableException, 
			IllegalStateException, SecurityException, HeuristicMixedException, 
			HeuristicRollbackException, RollbackException, InterruptedException, 
			UnrecoverableException, ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {
		
			log.entering(CLASS_NAME, "removeUser", new Object[] { taskId });

			/* 1st transaction */				
			transaction.begin();
			
			String userId = taskHelper.getUserId(taskId);
			
			bookingService.reserveUser(taskId, userId);
									
			User user = users.get(userId);
			
			Resource resource = user.getResource();
			
			String message = "done";
			
			if ( resource != null ) {
				
				taskHelper.setResource(taskId, resource.getId());
				bookingService.reserveResource(taskId, resource.getId());				
				
				transaction.commit();
				
				/* 2nd transaction */				
				transaction.begin();
				
				attachService.detachUser(userId);	
				
				bookingService.freeResource(resource.getId());
				
			} else {
				message = "User "+ userId + " has no Resource";
			}
			
			users.remove(userId);
			taskHelper.succeeded(taskId, message);
					
			transaction.commit();
			
			log.exiting(CLASS_NAME, "removeUser");
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	private void removeResource(String taskId) 
			throws NotSupportedException, SystemException, RecoverableException, 
			IllegalStateException, SecurityException, HeuristicMixedException, 
			HeuristicRollbackException, RollbackException, InterruptedException, 
			UnrecoverableException, ResourceUnrecoverableException, UserUnrecoverableException, 
			RecordNotFoundException {		
		
			log.entering(CLASS_NAME, "removeResource", new Object[] { taskId });

			/* 1st transaction */				
			transaction.begin();
			
			String resourceId = taskHelper.getResourceId(taskId);
					
			bookingService.reserveResource(taskId, resourceId);
			
			Resource Resource = resources.get(resourceId);
			User user = Resource.getUser();
			
			String message = "done";
			
			if ( user != null ) {
				
				taskHelper.setUser(taskId, user.getId());
				bookingService.reserveUser(taskId, user.getId());				
				
				transaction.commit();
				
				/* 2nd transaction */				
				transaction.begin();
				
				attachService.detachUser(user.getId());	
				
				bookingService.freeUser(user.getId());
				
			} else {
				message = "Resource "+ Resource.getId() + " has no User";
			}
			
			resources.remove(resourceId);
			taskHelper.succeeded(taskId, message);
					
			transaction.commit();
			
			log.exiting(CLASS_NAME, "removeResource");
	}
}
