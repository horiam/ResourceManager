import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.openejb.OpenEjbContainer;
import org.apache.cxf.jaxrs.client.WebClient;

public class TestUsersResource {

	private static EJBContainer container;

    @BeforeClass
    public static void start() throws NamingException {
		Properties properties = new Properties();
		properties.put("myDatabase", "new://Resource?type=DataSource");
		properties.put("myDatabase.JdbcDriver", "org.h2.Driver");
		properties.put("myDatabase.JdbcUrl", "jdbc:h2:mem:StorageManagerStore");
		properties.setProperty(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true");
		container = EJBContainer.createEJBContainer(properties);
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }
    
    @Test
    public void testUserService() {
    	String message = WebClient.create("http://localhost:4204").path("/ResourceManager/rest/users")
                					.accept(MediaType.APPLICATION_XML_TYPE)
                					.get(String.class);
    	System.out.println("message="+message);
    }
}
