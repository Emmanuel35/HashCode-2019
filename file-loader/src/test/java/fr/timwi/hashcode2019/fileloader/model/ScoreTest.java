package fr.timwi.hashcode2019.fileloader.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class ScoreTest {

	@Test
	public void testScore() {
		Slide current = new Slide();
		current.setPremierePhoto(new Photo(0, true, Arrays.asList("cat", "beach", "sun")));
		Slide next = new Slide();
		next.setPremierePhoto(new Photo(3, true, Arrays.asList("cat", "garden")));
		int score = Score.computeScore(current, next);
		assertEquals(1, score);
	}

}
