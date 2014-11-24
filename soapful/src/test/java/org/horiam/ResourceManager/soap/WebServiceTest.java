package org.horiam.ResourceManager.soap;

import java.net.URISyntaxException;
import java.net.URL;

import org.horiam.ResourceManager.exceptions.RecordNotFoundException;
import org.horiam.ResourceManager.mock.UserMockService;
import org.horiam.ResourceManager.model.Model;
import org.horiam.ResourceManager.model.ModelWithTask;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.webapp.soapful.UserWS;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WebServiceTest {
	
    @ArquillianResource
    private URL deployUrl;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).addClasses(UserWS.class, UserMockService.class, 
        		User.class, ModelWithTask.class, Model.class, RecordNotFoundException.class);
    }
   
    @Test
    public void testUsersResource() throws URISyntaxException {    	
    	System.out.println("\nTest UsersResource on URL=" + deployUrl.toString() + "...\n");	
    }
}
