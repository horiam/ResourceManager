import java.net.URISyntaxException;
import java.net.URL;

import javax.enterprise.inject.Model;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.horiam.ResourceManager.model.EntityNotFoundException;
import org.horiam.ResourceManager.model.ModelWithTask;
import org.horiam.ResourceManager.model.User;
import org.horiam.ResourceManager.webapp.restful.UsersResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import javax.ws.rs.client.*;;



@RunWith(Arquillian.class)
public class TestUsersResource {
	
    @ArquillianResource
    private URL deployUrl;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class).addClasses(UsersResource.class, UsersMockService.class, 
        		User.class, ModelWithTask.class, Model.class, EntityNotFoundException.class);
    }
   
    @Test
    public void testUserService() throws URISyntaxException {
    	System.out.println("Webapp URL="+deployUrl.toString());
    	
    	Client client = ClientBuilder.newClient();
    	HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "super");
    	client.register(feature);
    	WebTarget service = client.target(deployUrl.toURI());
    	Response resp = service.path("/users/").path("toto").request().get();
    	String message = resp.toString();
    	System.out.println("message="+message);  
    }
}
