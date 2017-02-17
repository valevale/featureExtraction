package model;

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;


public class PairMatchingRepositoryRepository {
	private static PairMatchingRepositoryRepository instance = null;
	private Map<Tuple2<String,String>,PairMatchingRepository> domains2repository;

	public static PairMatchingRepositoryRepository getInstance() {
		if (instance == null)
			instance = new PairMatchingRepositoryRepository();
		return instance;
	}

	private PairMatchingRepositoryRepository() {
		this.domains2repository = new HashMap<>();
	}
	
	public PairMatchingRepository getRepository(int ds1, int ds2) {
		Tuple2<Integer,Integer> domains = new Tuple2<>(ds1,ds2);
		PairMatchingRepository pr = this.domains2repository.get(domains);
		if (pr == null) {
			pr = this.domains2repository.get(domains.swap());
		}
		return pr;
	}

	
	public void addMatching(Xpath xpath1, String ds1, Xpath xpath2, String ds2, float score) {
		Tuple2<String,String> domains = new Tuple2<>(ds1,ds2);
		//check anche sugli inversi
		//se non esiste ancora un repository per quei domini
		if (!this.domains2repository.containsKey(domains) && 
				!this.domains2repository.containsKey(domains.swap())) {
			PairMatchingRepository newRepository = new PairMatchingRepository();
			this.domains2repository.put(domains, newRepository);
		}
		PairMatchingRepository currentRepository = this.domains2repository.get(domains);
		if (currentRepository == null) {
			currentRepository = this.domains2repository.get(domains.swap());
		}
		currentRepository.addMatching(xpath1, xpath2, score);
	}
	
}
