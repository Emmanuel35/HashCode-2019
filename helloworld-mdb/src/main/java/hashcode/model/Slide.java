package hashcode.model;

import java.util.ArrayList;
import java.util.List;

public class Slide {
	
	@Override
	public String toString() {
		if (premierePhoto == null)
			return "";
		
		if (secondePhoto == null)
			return ""+premierePhoto.getId();
		
		if (secondePhoto != null)
			return ""+premierePhoto.getId() + " " + secondePhoto.getId();
		
		return "";
	}
	private Photo premierePhoto;
	
	private Photo secondePhoto;
	
	private List<String> tags = new ArrayList<>();
	
	public Photo getPremierePhoto() {
		return premierePhoto;
	}
	public void setPremierePhoto(Photo premierePhoto) {
		this.premierePhoto = premierePhoto;
	}
	public Photo getSecondePhoto() {
		return secondePhoto;
	}
	public void setSecondePhoto(Photo secondePhoto) {
		this.secondePhoto = secondePhoto;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}
