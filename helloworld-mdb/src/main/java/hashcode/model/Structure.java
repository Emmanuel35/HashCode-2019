package hashcode.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Structure {

	private Integer score;
	private List<String> slides = new ArrayList<>();
	private List<Photo> photos;
	private List<Photo> slideCourant;
	
	public List<Photo> getSlideCourant() {
		return slideCourant;
	}

	public void setSlideCourant(List<Photo> slideCourant) {
		this.slideCourant = slideCourant;
	}

	public List<String> getSlides() {
		return slides;
	}

	public void setSlides(List<String> slides) {
		this.slides = slides;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
