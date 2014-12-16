package org.horiam.ResourceManager.examples.MockExample;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.horiam.ResourceManager.model.User;


@Entity
@Table(name = "USER")
@NamedQuery(name = "User.getAll", query = "SELECT a FROM MockUser a")
@XmlRootElement(name="User")
@XmlAccessorType(XmlAccessType.FIELD)
public class MockUser extends User {

	private static final long serialVersionUID = 7690634891351160854L;
	private String email;
	private String address;
	
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}		
}
