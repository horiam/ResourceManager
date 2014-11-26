package org.horiam.ResourceManager.soap;

public class MessageHolderBean {

    private String message;
    
    public MessageHolderBean() {
    	
    }
    public MessageHolderBean(String message) {
        this.message = message;
    }
 
    public String getMessage() {
        return message;
    }
}
