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

	private String address;
	private int age;
	
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}		
}
