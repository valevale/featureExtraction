package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.WebPageSelector;
import model.DomRepToClean;
import model.Source;
import model.WebPage;

public class PagesWithUniqueNamesRepository {
	
	static Map<String,List<WebPage>> domain2pages;
	
	public static Map<String,List<WebPage>> getPages() {
		return domain2pages;
	}
	
	public static void setDomain2Pages(Map<String,List<WebPage>> d2p) {
		domain2pages = d2p;
	}
	
//	public static Map<String,List<WebPage>> selectDomainsAndGetPagesWithUniqueName() {
////		MongoFacade facade = new MongoFacade("web_search_pages");
//		Map<String,List<WebPage>> domain2pages = new HashMap<>();
//		DomRepToClean drtc = DomRepToClean.getInstance();
//		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
//			Source currentSource = SourceRep.getSource(SourceInput.getSorgenti().get(i));
////			Source currentSource = facade.getSourceWithId(SourceInput.getSorgenti().get(i));
//			drtc.addDomain(currentSource);
////			SourceRep.addSource(currentSource);
//			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
//			domain2pages.put(currentSource.getId().toString(), pagesOfCurrentSource);
//		}
//		return domain2pages;
//	}
}
