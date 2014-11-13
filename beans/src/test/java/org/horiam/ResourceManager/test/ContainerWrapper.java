package org.horiam.ResourceManager.test;

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract class ContainerWrapper {
	
	protected static EJBContainer container;
	protected static Context context;
	
	protected static void setupContainer(Properties properties) {
	
		container = EJBContainer.createEJBContainer(properties);
		context   = container.getContext();		
	}
	
	protected <E> E lookup(String name) throws NamingException {
		return (E) context.lookup(name);
	}
	
	protected static void closeContainer() {
		container.close();
	}
}
