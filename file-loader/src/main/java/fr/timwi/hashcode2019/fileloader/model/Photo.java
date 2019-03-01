package fr.timwi.hashcode2019.fileloader.model;

import java.util.List;

public class Photo {

	private Integer id;
	private Boolean horizontal = Boolean.TRUE;
	private List<String> tags;

	public Photo() {
		super();
	}

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

	@Override
	public String toString() {
		return "Photo [id=" + id + "]";
	}
}
