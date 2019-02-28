package hashcode.model;

public class Score {
	
	public static int computeScore(Photo[] current, Photo[] next) {
		// nombre de tags uniquement présent dans le slide current
		int nbUniqueTagsCurrent = 0;
		int nbUniqueTagsnext = 0;
		int nbUniquesCommuns = 0;
		return Math.min(nbUniquesCommuns, Math.min(nbUniqueTagsCurrent, nbUniqueTagsnext));
	}
}
