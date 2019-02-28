package hashcode.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Photo {

	public Photo() {
		super();
		// TODO Auto-generated constructor stub
	}

	private Integer id;
	private Boolean horizontal = Boolean.TRUE;
	private List<String> tags;

	public Photo(Integer id, Boolean horizontal, List<String> tags) {
		super();
		this.id = id;
		this.horizontal = horizontal;
		this.tags = tags;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getHorizontal() {
		return horizontal;
	}

	public void setHorizontal(Boolean horizontal) {
		this.horizontal = horizontal;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
