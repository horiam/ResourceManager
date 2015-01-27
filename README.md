ResourceManager
===============

**Overview**
------------

RM is a Java EE project that aims to answer a problem: How to allocate one resource to just one user exclusively and concurrently. The resource could virtually be anything from a folder in data storage to an entire node in Shared Nothing architecture for example. This project focuses on the allocation: Finding one resource for one user, and not on any IaaS operation.  

**Use**
------------

The project can immediately be used through its MegaWar module that, once built (see Build section), will create a deployable war. The deployed web app (see Deploy serve side section) needs to be contacted via REST, SOAP or JMS (see Client usage section).
Build

JDK 1.7, Maven and an Internet connection are needed to build the project. 
```
mvn clean install
```
The main usable WAR will be generated in MegaWar/target.


**Deploy server side**

TomEE is the only JEE server on which the project has been tested. You need TomEE Plus 1.7.1. The persistency requires a MySQL, or H2 in memory database.

**Client usage**

The idea is to register all the existing free Resources at the beginning. Then, when a User wants a Resource:

1. Register the User if it’s a new one.

2. Call the ‘Assign a Resource service’ for the User.  A task will be returned immediately upon this call.

3. Poll this task until its completion. 

4. If the task has succeeded, poll the User to see what Resource it has.  

The same process should be applied on every asynchronous service. See Services description section for more details.

**Dependencies**

In any case, include Model module or JAR in your client. 

-> REST: You need a REST client like Jersey, Apache CXF or even a web browser will do for GETs.

-> SOAP: A SOAP client like Apache CXF is needed as well as BaseAdditions module for SEI.

-> JMS: You need to have a JMS client like ApacheMQ and the JMSClient module or JAR. 

**Reference**
------------

**Services**

The core of the project is a set of services that deals with three objects: User, Resource and Task.

It does several things. Some services are synchronous :

Create or update a User, Get a User, Get all Users, Delete a User, Create or update a Resource, Get a Resource, Get all Resources, Delete a Resource, Get a Task, Get all Tasks

Whereas complex services are asynchronous:

To allocate one Resource for one User:

1.	Create a task and return it

2.	Find a Resource that doesn’t already have a User

3.	Allocate this Resource to this User and assign it

4.	Now this User and this resource are linked together 

To deallocate a User from a Resource:

1.	Create a task and return it

2.	Deallocate the User from the Resource

3.	Unassign the Resource from the User 

4.	Now the User and Resource have been freed from each other

To remove a User:

1.	Create a task and return it

2.	If the User has a Resource deallocate it

3.	Delete the User

To remove a Resource:

1.	Create a task and return it

2.	If the User has a Resource, deallocate it

3.	Delete the Resource

**Concurrency**

One of the biggest challenges of this project was to handle concurrency. Concurrent calls can happen all the time on asynchronous services because of their potential slow pace.

The solution is to book a User or Resource with a Task combined with JPA Optimistic Lock. This forbids other Tasks to be executed on the same User or Resource at the same time.

**Persistence**

The application persists three different entities in a database by using JPA2.

A User that represents a User can reference an allocated Resource and an assigned Task.

A Resource that can reference an allocated User and an assigned Task.

A Task that models the different types of tasks associated with asynchronous services.

These three entities are also manipulated as Java objects on the Server and Client’s side.

**Security**

All service calls have to be authenticated with certain roles. This is intended in case the service is called directly by Users and in this case, they have to have a User role. 

A caller authenticated with a User role can only create, get, delete, allocate and deallocate the User it has authenticated with. It can only see its allocated Resource and its own Tasks. 

A caller authenticated with an Admin role can do everything. 

**Tests**

JUnit maven is Firesafe for everybody except for MegaWAR that is Failsafe.

In our tests, we use a wild range of technos, from OpenEJB embeded container, Araquillian embeded or Cargo, Apache CXF, Jersey, Apache VFS.

See details in module descriptions as well as coverage rate.

**Logging**

To activate logging copy in your _conf/logging.properties_
```
org.horiam.ResourceManager.level = FINEST
org.horiam.ResourceManager.handlers = <THE HANDLER YOU WANT TO USE>
```

**Architecture**

RM is split into several modules. The essential ones are Model, BaseAdditions, Service and Beans. 

Restful, Soapful and JMS modules offer convenient ways to call the services. 

Some modules are only useful for test cases like MockExample and MockServices. 

**Modules description**
------------------------

**Model**

Contains the data model common to client/beans/database. The Users, Resources and Tasks are defined here. This model is a dependency if you need to deal with these objects on the client’s side.

*Tests*

Test the JAXB marshall of the model to a Virtual FS and then unmarshall it back. This is done to make sure that the model can be transferred consistently over REST or SOAP.

Coverage is 74%.

**BaseAdditions**

Is the module that contains the Service Endpoints Interfaces and Fault needed by the SOAP client.
Services:

Contains the interfaces of the three major services that the RM offers: UserService, ResourceService and TaskService.

**Beans**

This is the core of the RM. 

The DAO package holds the persistency services for the Model entities. UserDao, ResourceDao and TaskDao EJBs extend the Dao abstract class that offers generic access to the ResourceManager: Create, get, getLock, update, list, remove and clear on the entities. 

The BusinessLogic package core is the TaskExecutor EJB that asynchronously executes Tasks and is Bean Transaction Managed.  Every task is executed in the same manner, which is as follows: 

1.	Try to book the main subject; a User or Resource regarding the Task type, of the Task through the Booking EJB. 

  -> If the booking fails because the entity is already booked or because of an Optimistic Lock exception, the entity is released and the Task is set to fail.

2.	If the booking succeeds, the TaskExecutor tries to book the second subject of the Task, a User if the main subject was a Resource or a Resource if it is a User. 

  ->	If the booking fails because the entity is already booked or because of an Optimistic Lock exception, the entity is released and the Task is set to fail.
  
3.	Once both subjects are booked, the TaskExecutor calls the Allocator EJB that does the necessary work of allocating or deallocating the Resource to the User. The Allocator EJB calls the AllocatorDriver object that is supposed to be the “real” allocation implementation, regarding the allocation type: doing a fdisk, calling EC2 or something else. The default RM provided implementation does nothing.

4.	If everything has worked out, the Task is set to succeed and the two subjects of the task are released from their booked state.

5.	A Task event is fired to tell the listeners that the Task has succeeded or failed. See JMS module.

The Services package is the implementation of the Services module and uses the BusinessLogic package for Task processing and the Dao package for the rest. The three stateless EJBs are secured by authenticated Roles and Interceptors, defined in the authorisation package.

The authorisation package checks that the authenticated principal is entitled to view or modify a User, Resource or Task. Admin role principals are allowed to do anything and User role principals are only allowed to see their Resources or Tasks. If a principal is not allowed, a custom Authorisation exception is thrown.

*Tests*

All tests are done with a TomEE embedded EJB container and a memory H2 database. 

To test the Roles, Callable classes with RunAs annotation are used. The authentication is tested due to TomEE LocalInitialContextFactory that allows authentication to be tested with two properties files where the test Users and Roles are defined. 

Task event is tested by an observer class.

Coverage is 86%.

**MockServices**

Tests in JMS, Restful and Soapful modules are always done without the Beans module. These modules use the Service module. Therefore, any implementation of these service interfaces would integrate with these modules. For the tests, we use the MockService module.

**JMS and JMSClient**

Module that aims to offer access to the Services from a JMS client.

Three different MDB (Message Driven Bean) listen for incoming requests messages on three different queues. They call the UserService, ResourceService and TaskService and then return the result as an ObjectMessage. The BaseMDB serves as a virtual superclass to avoid duplicating receive and send message codes.

Also in this module, an Observer for Task events will publish on a special Topic the Tasks that have completed (although not necessary succeeded). 

*Tests*

The MDBs are tested in the TomEE embedded container and by using the JMSClient module. The JMSClient module is a prerequisite because the MDBs are required to respond to a temporary queue created specially for and by the client. In addition, a correlation ID is injected in the response. A MockService module is used for the business logic.

For testing the Task Event observer, we use a class that will fire a TaskEvent. The TestTaskObserver JUnit would have previously subscribed to the Topic.

Coverage is 92%.

**Restful**

A simple REST exposition of the services from the Service module. User, Resource or Task objects are returned for most of the calls. 

*Tests*
We use Arquillan for the deployment of the REST application and CXF JAX-RS for the client.

Coverage is 84%.

**Restful**

A simple SOAP exposition of the services from the Service module. User, Resource or Task objects are returned for most of the calls as well as a custom Fault as an exception wrapper.

*Tests*

We use Arquillan for the deployment of the SOAP application and CXF JAX-WS for the client.

Coverage is 76%.

**MegaWAR**
Is the assembly of Restful, Soapful and JMS with Beans module. It's basically the whole webapp in one module.

*Tests*

Here we do the integration tests by running Maven Failsafe with Cargo that will deploy the maven built WAR in the freshly downloaded standalone TomEE. Cargo had to be used because the Beans module requires authentication to be used. Therefore, we configured Cargo to use a TomEE basic authentication with a custom tomcat-users.xml which provided the test with test users. 
For JMS tests, we have to use an ActiveMQ client because the Cargo container is standalone. Therefore the client and TomEE do not share anything. 

For REST tests, we use a Jersey client that will authenticate.

For SOAP tests, we use a CXF JaxWsProxyFactoryBean client that allows authentication and avoids the WSDL. It is not usable here because authentication is needed to read it. Therefore it uses the SEI instead.

**JaaS modules**

These two modules are optional. They are made for TomEE authentication. 

The _JaaSCutomRealm_ is a Tomcat auth realm that removes the right part of the user’s name if it’s an @example.com email. For example, a login with userA@example.com is the same as with userA; therefore you do not need to declare this user two times in your JaaS realm. 

_JaaSCustomModule_ is a custom Tomcat JaaS login module that will look for the user, passwords and roles in the file /tmp/logins.txt.

**Devloppment**
------------------

This app has been written on Eclipse and Vim and build with maven.

**Motivation**
------------------

Allotting identical resources to concurrent users without collision is a common problem in computer science. This happens when you have to assign an S3 bucket or an EC2 instance to a user without handing the same to different concurrent users. 
The asynchronous dimension can also add to the difficulty of the problem. An allocation can be a long process requiring long IaaS calls in order to prepare the resource for the user, like starting diverse services on a VM or an fdisk on a sotrage for example.
This project aims to address these two using the tools Java EE offers.
