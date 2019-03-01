package fr.timwi.hashcode2019.fileloader.model;

import java.util.ArrayList;
import java.util.List;

public class Score {

	public static int computeScore(Slide current, Slide next) {
		// nombre de tags uniquement présent dans le slide current

		List<String> listeUniqueCurrent = new ArrayList<>(current.getTags());
		List<String> listeUniqueNext = new ArrayList<>(next.getTags());
		List<String> listeCommune = new ArrayList<>(current.getTags());
		List<String> tempListe = new ArrayList<>(next.getTags());
		listeCommune.retainAll(tempListe);

		// on retire ce qui est commun
		listeUniqueCurrent.removeAll(next.getTags());
		// on retire ce qui est commun
		listeUniqueNext.removeAll(current.getTags());

		int nbUniqueTagsCurrent = listeUniqueCurrent.size();
		int nbUniqueTagsNext = listeUniqueNext.size();
		int nbUniquesCommuns = listeCommune.size();

		return Math.min(nbUniquesCommuns, Math.min(nbUniqueTagsCurrent, nbUniqueTagsNext));
	}
}
