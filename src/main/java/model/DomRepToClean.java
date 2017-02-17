package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DomRepToClean {

	private static DomRepToClean instance = null;
	private Map<String,List<Document>> domain2pagesUsedForClean;
	
	public static DomRepToClean getInstance() {
		if (instance == null)
			instance = new DomRepToClean();
		return instance;
	}

	private DomRepToClean() {
		this.domain2pagesUsedForClean = new HashMap<>();
	}
	
	public List<Document> getPagesToClean(String domain) {
		return this.domain2pagesUsedForClean.get(domain);
	}
	
	public void addDomain(Source domain) {
		if (!this.domain2pagesUsedForClean.containsKey(domain)) {
			List<Document> pagesToClean = new ArrayList<>();
			for (int i=0;i<5 && i<domain.getPages().size();i++) {
				pagesToClean.add(Jsoup.parse(domain.getPages().get(i).getHtml()));
			}
			this.domain2pagesUsedForClean.put(domain.getId().toString(), pagesToClean);
		}
	}
	
}
