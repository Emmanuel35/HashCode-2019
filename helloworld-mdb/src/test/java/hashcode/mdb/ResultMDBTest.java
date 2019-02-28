package hashcode.mdb;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import hashcode.model.Structure;

public class ResultMDBTest {

	@Test
	public void testOnMessage() throws FileNotFoundException, IOException {
		Structure structure = new Structure();
		structure.setScore(2);
		List<String> slides = structure.getSlides();
		slides.add("0");
		slides.add("3");
		slides.add("1 2");
		new ResultMDB().writeStructure("target", "test", structure);
	}

}
