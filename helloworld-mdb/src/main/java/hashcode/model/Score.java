package hashcode.model;

import java.util.ArrayList;
import java.util.List;

public class Score {
	
	public static int computeScore(Slide current, Slide next) {
		// nombre de tags uniquement présent dans le slide current
		int nbUniqueTagsCurrent = 0;
		int nbUniqueTagsNext = 0;
		int nbUniquesCommuns = 0;
		nbUniqueTagsCurrent = current.getTags().size();
				
		List<String> listeUniqueCurrent = new ArrayList<String>(current.getTags());
		List<String> listeUniqueNext = new ArrayList<String>(next.getTags());
		List<String> listeCommune = new ArrayList<String>(current.getTags());
		List<String> tempListe = new ArrayList<String>(next.getTags());
		listeCommune.retainAll(tempListe);
		
		// on retire ce qui est commun
		listeUniqueCurrent.removeAll(next.getTags());
		// on retire ce qui est commun
		listeUniqueNext.removeAll(current.getTags());
		
		
		nbUniqueTagsCurrent = listeUniqueCurrent.size();
		nbUniqueTagsNext = listeUniqueNext.size();
		nbUniquesCommuns = listeCommune.size();
		
		return Math.min(nbUniquesCommuns, Math.min(nbUniqueTagsCurrent, nbUniqueTagsNext));
	}
}
