package org.horiam.ResourceManager.soap;

import javax.ejb.ApplicationException;
import javax.xml.ws.WebFault;

@ApplicationException
@WebFault(name="SimpleException")
public class ResourceManagerFault extends Exception {
	
	private static final long serialVersionUID = 4846502381900933305L;
	private MessageHolderBean faultBean;
	 
    public ResourceManagerFault(String message, MessageHolderBean faultInfo){
        super(message);
        faultBean = faultInfo;
    }
 
    public ResourceManagerFault(String message, MessageHolderBean faultInfo, Throwable cause) {
        super(message, cause);
        faultBean = faultInfo;
    }
 
    public MessageHolderBean getFaultInfo(){
        return faultBean;
    }
}
