package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PairMatchingRepository {
	private Map<PairMatching,Float> matchings2vote;

	public PairMatchingRepository() {
		this.matchings2vote = new HashMap<PairMatching,Float>();
	}
	
	public void addMatching(Xpath xpath1, Xpath xpath2, float score) {
		PairMatching newMatching = new PairMatching(xpath1,xpath2);
		Float vote;
		if (this.matchings2vote.containsKey(newMatching)) {
			//il matching già esiste; aggiungiamo un voto
			vote = this.matchings2vote.get(newMatching)+score;
//			System.out.println("VECCHIO MATCHING. ");
//			System.out.println("---"+xpath1.getXpath());
//			System.out.println("---"+xpath2.getXpath());
//			System.out.println("VOTO: "+vote);
		}
		else {
			//il matching non esiste; mettiamolo nella mappa con voto 1
//			System.out.println("NUOVO MATCHING");
//			System.out.println("+++"+xpath1.getXpath());
//			System.out.println("+++"+xpath2.getXpath());
			vote = score;
		}
//		if ((xpath1.getXpath().equals("//html[1]/body[1]/div[1]/div[3]/div[1]/a[4]")
//				&& xpath2.getXpath().equals("//html[1]/body[1]/div[3]/div[2]/div[1]/div[1]/ul[1]/li[2]/span[1]/a[1]"))) {
//			System.out.println("AH-HA!");
//		}
		this.matchings2vote.put(newMatching, vote);
	}
	
	public Map<PairMatching,Float> getMatchings2vote() {
		return this.matchings2vote;
	}
	
	public Set<PairMatching> getMatchings() {
		return this.matchings2vote.keySet();
	}
	
	public List<PairMatching> getPairsWith(Xpath xpath, boolean isElementAtRight) {
		List<PairMatching> pairsWithXpath = new ArrayList<>();
		Iterator<PairMatching> matchingsIt = this.matchings2vote.keySet().iterator();
		while (matchingsIt.hasNext()) {
			PairMatching currentPair = matchingsIt.next();
			Xpath currentElement;
			if (isElementAtRight) {
				currentElement = currentPair.getXpath2();
			}
			else {
				currentElement = currentPair.getXpath1();
			}
			if (currentElement.equals(xpath)) {
				pairsWithXpath.add(currentPair);
			}
		} //end while
		return pairsWithXpath;
	}
}
