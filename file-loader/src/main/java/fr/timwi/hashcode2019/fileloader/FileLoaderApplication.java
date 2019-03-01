package fr.timwi.hashcode2019.fileloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import fr.timwi.hashcode2019.fileloader.model.Photo;
import fr.timwi.hashcode2019.fileloader.model.Score;
import fr.timwi.hashcode2019.fileloader.model.Slide;
import fr.timwi.hashcode2019.fileloader.model.Structure;

public class FileLoaderApplication {

	private Random random = new Random();

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		// String filename = args[0];
		new FileLoaderApplication().parseAndGo("a_example.txt");
		new FileLoaderApplication().parseAndGo("b_lovely_landscapes.txt");
		new FileLoaderApplication().parseAndGo("c_memorable_moments.txt");
		new FileLoaderApplication().parseAndGo("d_pet_pictures.txt");
		new FileLoaderApplication().parseAndGo("e_shiny_selfies.txt");

	}

	protected void parseAndGo(String filename) throws InterruptedException, IOException {
		System.out.println(filename);
		Scanner scanner = new Scanner(new File("src/test/resources/" + filename));
		int lineCount = scanner.nextInt();
		List<Photo> horizontalPhotos = new ArrayList<>();
		List<Photo> verticalPhotos = new ArrayList<>();
		List<Photo> allPhotos = new ArrayList<>();
		for (int i = 0; i < lineCount; i++) {
			Boolean isHorizontal = "H".equals(scanner.next()) ? Boolean.TRUE : Boolean.FALSE;
			int tagCount = scanner.nextInt();
			List<String> tags = new ArrayList<>();
			for (int j = 0; j < tagCount; j++) {
				tags.add(scanner.next());
			}
			tags.sort(String::compareTo);
			Photo photo = new Photo(i, isHorizontal, tags);
			if (isHorizontal) {
				horizontalPhotos.add(photo);
			} else {
				verticalPhotos.add(photo);
			}
			allPhotos.add(photo);
		}
		System.out.println("Horizontal photos size: " + horizontalPhotos.size());
		System.out.println("Vertical photos size: " + verticalPhotos.size());
		System.out.println("All photos size: " + allPhotos.size());

		final Structure bestStructure = new Structure();
		
		ExecutorService threadPool = Executors.newFixedThreadPool(8);

		for (int i = 0; i < 10000; i++) {
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
//					System.out.println("tryNewStructure start");
					long start = System.currentTimeMillis();
					Structure currentStructure = tryNewStructure(new ArrayList<>(horizontalPhotos),
							new ArrayList<>(verticalPhotos), new ArrayList<>(allPhotos));
					long end = System.currentTimeMillis();
					System.out.println("tryNewStructure (elapsed: " + (end - start) + " ms)");
					if (bestStructure.getScore() < currentStructure.getScore()) {
						bestStructure.setScore(currentStructure.getScore());
						bestStructure.setSlides(currentStructure.getSlides());
					}
				}
			});
		}
		threadPool.shutdown();
		if (!threadPool.isTerminated()) {
			threadPool.awaitTermination(5, TimeUnit.MINUTES);
		}

		System.out.println("Score: " + bestStructure.getScore());
		System.out.println("Slides: " + bestStructure.getSlides());
		writeStructure("target", filename, bestStructure);
	}

	protected void writeStructure(String path, String correlationId, Structure struct) throws IOException, FileNotFoundException {
		try (BufferedOutputStream out = IOUtils.buffer(new FileOutputStream(new File(
				path + "/result_" + struct.getScore() + "-" + correlationId)))) {
			IOUtils.write(struct.getSlides().size() + "\r\n", out, Charset.defaultCharset());
			IOUtils.writeLines(struct.getSlides(), "\r\n", out, Charset.defaultCharset());
		}
	}

	protected Structure tryNewStructure(List<Photo> horizontalPhotos, List<Photo> verticalPhotos,
			List<Photo> allPhotos) {
		Structure currentStructure = new Structure();
		while (!allPhotos.isEmpty()) {
			Slide nextSlide = pickNextSlide(horizontalPhotos, verticalPhotos, allPhotos);
			if (currentStructure.getSlideCourant() == null) {
				currentStructure.setSlideCourant(nextSlide);
				currentStructure.getSlides().add(nextSlide.toString());
				continue;
			}
			int transitionScore = Score.computeScore(currentStructure.getSlideCourant(), nextSlide);
//			System.out.println("transitionScore: " + transitionScore);
			currentStructure.setScore(currentStructure.getScore() + transitionScore);
			currentStructure.getSlides().add(nextSlide.toString());
		}
		return currentStructure;
	}

	protected Slide pickNextSlide(List<Photo> horizontalPhotos, List<Photo> verticalPhotos, List<Photo> allPhotos) {
		Slide nextSlide = new Slide();
		Photo photo = pickOnePhoto(allPhotos);
		horizontalPhotos.remove(photo);
		verticalPhotos.remove(photo);

		nextSlide.setPremierePhoto(photo);
		if (!photo.getHorizontal()) {
			Photo secondPhoto = pickOnePhoto(verticalPhotos);
			horizontalPhotos.remove(secondPhoto);
			allPhotos.remove(secondPhoto);
			nextSlide.setSecondePhoto(secondPhoto);
		}
		return nextSlide;
	}

	private Photo pickOnePhoto(List<Photo> photos) {
		int nextInt = random.nextInt(photos.size());
		return photos.remove(nextInt);
	}

	public void reducePhoto(List<Photo> allPhotos) {
		Map<String, List<Photo>> photoByTags = new HashMap<>();
		for (Photo photo : allPhotos) {
			for (String tag : photo.getTags()) {
				if (photoByTags.containsKey(tag)) {
					photoByTags.get(tag).add(photo);
				} else {
					List<Photo> newPhotoList = new ArrayList<>();
					newPhotoList.add(photo);
					photoByTags.put(tag, newPhotoList);
				}
			}
		}
		Set<Photo> reducedPhotos = photoByTags.entrySet().stream().filter(entry -> entry.getValue().size() > 1)
				.map(entry -> new HashSet<Photo>(entry.getValue()))
				.reduce(new HashSet<Photo>(), (accumulator, list) -> {
					accumulator.addAll(list);
					return accumulator;
				});

		System.out.println("ReducedPhotos size: " + reducedPhotos.size());
	}
}
