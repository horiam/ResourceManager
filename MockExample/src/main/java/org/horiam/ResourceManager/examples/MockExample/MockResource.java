package org.horiam.ResourceManager.examples.MockExample;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.horiam.ResourceManager.model.Resource;

//just copy paste all these annotations
@Entity
@Table(name = "RESOURCE")
@NamedQueries({
@NamedQuery(name="Resource.getAll", query="SELECT a FROM MockResource a"),
@NamedQuery(name="Resource.getAllFree", query="SELECT a FROM MockResource a WHERE"
									   +" a.user IS NULL"
									   +" AND a.booked = FALSE")
})
@XmlRootElement(name="Resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class MockResource extends Resource {


	private static final long serialVersionUID = -4458004865420639752L;
	private int mockWaitTime;
	

	public int getMockWaitTime() {
		return mockWaitTime;
	}

	public void setMockWaitTime(int mockWaitTime) {
		this.mockWaitTime = mockWaitTime;
	}	
}
