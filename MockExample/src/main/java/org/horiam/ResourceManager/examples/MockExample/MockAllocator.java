package org.horiam.ResourceManager.examples.MockExample;

import org.horiam.ResourceManager.businessLogic.AlloctionDriver;
import org.horiam.ResourceManager.model.Resource;
import org.horiam.ResourceManager.model.User;


public class MockAllocator extends AlloctionDriver {

	@Override
	public void allocate(User user, Resource resource) throws InterruptedException {
		
		MockResource mockResource = (MockResource) resource;
		int sleep = mockResource.getMockWaitTime();
		Thread.sleep(sleep);
	}
	
	@Override
	public void deallocate(User user, Resource resource) throws InterruptedException {
		
		MockResource mockResource = (MockResource) resource;
		int sleep = mockResource.getMockWaitTime();
		Thread.sleep(sleep);
	}
}
