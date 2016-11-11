package lib.utils;

import java.util.HashSet;

public class Jaccard {

	public static double similarity(String s1, String s2) {
		HashSet<String> h1 = makeSet(s1);
		HashSet<String> h2 = makeSet(s2);
		HashSet<String> intersection = new HashSet<String>(h1);
		intersection.retainAll(h2);
		double countIntersection = intersection.size();
		HashSet<String> union = new HashSet<String>(h1);
	    union.addAll(h2);
	    double countUnion = union.size();
	    return countIntersection / countUnion;
	}
	
	public static HashSet<String> makeSet(String s) {
		String cleaned = s.replaceAll("[^a-zA-Z0-9 ]+", " ");
		String trimmed = cleaned.trim();
		String[] words = trimmed.split("\\s+");
		HashSet<String> setWords = new HashSet<>();
		for(int i=0;i<words.length;i++) {
			String w = words[i].toLowerCase();
			setWords.add(w);
		}
		return setWords;
	}
	
	public static void main(String[] args) {
		System.out.println(similarity("Amoroso claudio d'aragona","amoROSO d'aragona avv. claudio"));
	}
	
}
