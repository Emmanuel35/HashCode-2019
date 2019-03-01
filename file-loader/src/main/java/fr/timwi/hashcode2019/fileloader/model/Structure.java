package fr.timwi.hashcode2019.fileloader.model;

import java.util.ArrayList;
import java.util.List;

public class Structure {

	private Integer score = 0;
	private List<String> slides = new ArrayList<>();
	private List<Photo> horizontalPhotos = new ArrayList<>();
	private List<Photo> verticalPhotos = new ArrayList<>();
	private List<Photo> allPhotos  = new ArrayList<>();
	private Slide slideCourant;
	
	public Slide getSlideCourant() {
		return slideCourant;
	}

	public void setSlideCourant(Slide slideCourant) {
		this.slideCourant = slideCourant;
	}

	public List<String> getSlides() {
		return slides;
	}

	public void setSlides(List<String> slides) {
		this.slides = slides;
	}
	
	public List<Photo> getHorizontalPhotos() {
		return horizontalPhotos;
	}

	public void setHorizontalPhotos(List<Photo> horizontalPhotos) {
		this.horizontalPhotos = horizontalPhotos;
	}

	public List<Photo> getVerticalPhotos() {
		return verticalPhotos;
	}

	public void setVerticalPhotos(List<Photo> verticalPhotos) {
		this.verticalPhotos = verticalPhotos;
	}

	public List<Photo> getAllPhotos() {
		return allPhotos;
	}

	public void setAllPhotos(List<Photo> photos) {
		this.allPhotos = photos;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
