package hashcode.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Structure {
	String name;
	Integer reinject = 0; 

	public Integer getReinject() {
		return reinject;
	}

	public void setReinject(Integer reinject) {
		this.reinject = reinject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
