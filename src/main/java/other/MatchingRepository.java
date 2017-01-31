package other;

import java.util.ArrayList;
import java.util.List;

import model.DomainSource;
import model.Xpath;

public class MatchingRepository {
	private static MatchingRepository instance = null;
	private List<Matching> matchings;

	public static MatchingRepository getInstance() {
		if (instance == null)
			instance = new MatchingRepository();
		return instance;
	}

	private MatchingRepository() {
		this.matchings = new ArrayList<>();
	}

	public void addMatching(DomainSource domain1, Xpath xpath1, DomainSource domain2, Xpath xpath2) {
		for(int i=0;i<this.matchings.size();i++) {
			Matching currentMatching = this.matchings.get(i);
			//controllo che la coppia non esista già
			if (currentMatching.has(domain1, xpath1) && currentMatching.has(domain2, xpath2)) {
				//				System.out.println("già ci stava");
				return;
			}
			//inizio dal primo dominio
			else {
				if (currentMatching.has(domain1, xpath1)) {

					//				System.out.println("xpath1 posseduto!");
					//aggiungo il dominio2 in questo collegamento
					if (currentMatching.add(domain2, xpath2)) {
						System.out.println("-------------------SONO QUI");
						return;
					}
					//l'aggiunta non è andata a buon fine perché già esisteva domain2 nel matching
					//copi il matching e ci sovrascrivi xpath2 nel domain2
					Matching newMatching = new Matching(currentMatching);
					newMatching.addOverriding(domain2, xpath2);
					this.matchings.add(newMatching);
					return;
				}
				if (currentMatching.has(domain2, xpath2)) {
					//				System.out.println("xpath2 posseduto!");
					if (currentMatching.add(domain1, xpath1)) {
						System.out.println("------------------SONO QUI");
						return;
					}
					//l'aggiunta non è andata a buon fine perché già esisteva domain1 nel matching
					//copi il matching e ci sovrascrivi xpath1 nel domain1
					Matching newMatching = new Matching(currentMatching);
					newMatching.addOverriding(domain1, xpath1);
					this.matchings.add(newMatching);
					return;
				}
			}
			//caso in cui non si è trovato nessun matching
			//		System.out.println("+++++++++++++++++++++SONO QUI");
//			Matching newMatching = new Matching(domain1, xpath1, domain2, xpath2);
//			this.matchings.add(newMatching);
		}
		Matching newMatching = new Matching(domain1, xpath1, domain2, xpath2);
		this.matchings.add(newMatching);
	}

	public List<Matching> getMatchings() {
		return this.matchings;
	}
}
