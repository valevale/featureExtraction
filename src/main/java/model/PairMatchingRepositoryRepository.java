package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import scala.Tuple2;


public class PairMatchingRepositoryRepository {
	private static PairMatchingRepositoryRepository instance = null;
	private Map<Tuple2<String,String>,PairMatchingRepository> domains2repository;
	private PairMatchingRepository temp_repository;

	public static PairMatchingRepositoryRepository getInstance() {
		if (instance == null)
			instance = new PairMatchingRepositoryRepository();
		return instance;
	}

	private PairMatchingRepositoryRepository() {
		this.domains2repository = new HashMap<>();
		this.temp_repository = new PairMatchingRepository();
	}

	public PairMatchingRepository getRepository(int ds1, int ds2) {
		Tuple2<Integer,Integer> domains = new Tuple2<>(ds1,ds2);
		PairMatchingRepository pr = this.domains2repository.get(domains);
		if (pr == null) {
			pr = this.domains2repository.get(domains.swap());
		}
		return pr;
	}

	public PairMatchingRepository getTempRepository() {
		return this.temp_repository;
	}

	public void addMatching(Xpath xpath1, String ds1, Xpath xpath2, String ds2, float score) {
		Tuple2<String,String> domains = new Tuple2<>(ds1,ds2);
		//check anche sugli inversi
		//se non esiste ancora un repository per quei domini
		boolean swapped = false;
		if (!this.domains2repository.containsKey(domains) && 
				!this.domains2repository.containsKey(domains.swap())) {
			PairMatchingRepository newRepository = new PairMatchingRepository();
			this.domains2repository.put(domains, newRepository);
		}
		PairMatchingRepository currentRepository = this.domains2repository.get(domains);
		if (currentRepository == null) {
			currentRepository = this.domains2repository.get(domains.swap());
			swapped = true;
		}
		if (!swapped)
			currentRepository.addMatching(xpath1, xpath2, score);
		else
			currentRepository.addMatching(xpath2, xpath1, score);
	}

	//aggiunge la coppia a un repository temporaneo
	public void addMatching_tempRep(Xpath xpath1, Xpath xpath2, float score) {
		if (this.temp_repository==null)
			this.temp_repository = new PairMatchingRepository();
		this.temp_repository.addMatching(xpath1, xpath2, score);
	}

	//sposta tutti i matching del repository temporaneo al repository con i domini indicati
	public void moveTempRepMatchings(String ds1, String ds2) {
		Tuple2<String,String> domains = new Tuple2<>(ds1,ds2);
		//check anche sugli inversi
		//se non esiste ancora un repository per quei domini
		boolean swapped = false;
		if (!this.domains2repository.containsKey(domains) && 
				!this.domains2repository.containsKey(domains.swap())) {
			PairMatchingRepository newRepository = new PairMatchingRepository();
			this.domains2repository.put(domains, newRepository);
		}
		PairMatchingRepository currentRepository = this.domains2repository.get(domains);
		if (currentRepository == null) {
			currentRepository = this.domains2repository.get(domains.swap());
			swapped = true;
		}
		Map<PairMatching,Float> matchings2vote = this.temp_repository.getMatchings2vote();
		Iterator<PairMatching> it = matchings2vote.keySet().iterator();
		while (it.hasNext()) {
			PairMatching currentMatching = it.next();
			float score = matchings2vote.get(currentMatching);
			Xpath xpath1 = currentMatching.getXpath1();
			Xpath xpath2 = currentMatching.getXpath2();
			if (!swapped) {
				currentRepository.addMatching(xpath1, xpath2, score);
			}
			else {
				currentRepository.addMatching(xpath2, xpath1, score);
			}
		}	
	}

	//cancella il repository temporaneo
	public void destroy_tempRep() {
		this.temp_repository = null;
	}

}
