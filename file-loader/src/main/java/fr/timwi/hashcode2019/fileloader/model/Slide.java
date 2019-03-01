package fr.timwi.hashcode2019.fileloader.model;

import java.util.HashSet;
import java.util.Set;

public class Slide {

	private Photo premierePhoto;

	private Photo secondePhoto;

	private Set<String> tags = new HashSet<>();

	public Photo getPremierePhoto() {
		return premierePhoto;
	}

	public void setPremierePhoto(Photo premierePhoto) {
		this.premierePhoto = premierePhoto;
		tags.addAll(premierePhoto.getTags());
	}

	public Photo getSecondePhoto() {
		return secondePhoto;
	}

	public void setSecondePhoto(Photo secondePhoto) {
		this.secondePhoto = secondePhoto;
		tags.addAll(secondePhoto.getTags());
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		if (secondePhoto != null) {
			return premierePhoto.getId() + " " + secondePhoto.getId();
		}
		return String.valueOf(premierePhoto.getId());
	}
}
