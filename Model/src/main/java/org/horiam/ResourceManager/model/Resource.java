package org.horiam.ResourceManager.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.horiam.ResourceManager.model.XmlAdapter.UserXmlAdapter;

@Entity
@Table(name = "RESOURCE")
@NamedQueries({
@NamedQuery(name="Resource.getAll", query="SELECT a FROM Resource a"),
@NamedQuery(name="Resource.getAllFree", query="SELECT a FROM Resource a WHERE"
									   +" a.user IS NULL"
									   +" AND a.booked = FALSE")
})
@XmlRootElement(name="Resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource extends ModelWithTask implements Serializable {
	
	private static final long serialVersionUID = 2306626348101072900L;
	
	@XmlJavaTypeAdapter(UserXmlAdapter.class)
	@OneToOne(mappedBy="resource")
	private User user;	
	@XmlTransient
	@Version
	private long version;
	
	public Resource() {
		super();
	}
		
	public Resource(String id) {
		this();
		setId(id);
	}	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean hasUsers() {
		if (user != null)
			return true;
		
		return false;
	}

	public void removeUser() {		
		user = null;
	}
	
	public boolean equals(Object other) {
		
		if (other == null)
			return false;
		
		if (other == this)
			return true;
		
		if (this.getClass().equals(other.getClass())) {
			Resource cast = (Resource) other;
			
			String userId = null;
			String castUserId = null;
			if (user != null) 
				userId = user.getId();
			if (cast.getUser() != null) 
				castUserId = cast.getUser().getId();

			return new EqualsBuilder().appendSuper(super.equals(other))
									  .append(userId, castUserId)
									  .isEquals();
		}
		return false;
	}
	
	public int hashCode() {
		 return new HashCodeBuilder(17, 79).append(serialVersionUID)
									       .toHashCode();
	}
}
