package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.MongoFacade;
import database.WebPageSelector;
import model.Source;
import model.WebPage;

public class TestPagineUniche {

	public static void main(String[] args) {
		MongoFacade facade = new MongoFacade("web_search_pages");
		Map<String,List<WebPage>> domain2pages = new HashMap<>();
		//seleziona i domini
		//primo modulo: raccolta di pagine con ancore uniche dai domini scelti
		//magari poi puoi vedere se anche con la source sfigata ci sono abbastanza pagine
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
		List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		System.out.println("dimensione1: "+pagesOfCurrentSource);
		domain2pages.put("5750678b3387e31f516fa1c7", pagesOfCurrentSource);
		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1d0");
		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		System.out.println("dimensione2: "+pagesOfCurrentSource);
		domain2pages.put("5750678b3387e31f516fa1d0", pagesOfCurrentSource);
		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1ca");
		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		System.out.println("dimensione2: "+pagesOfCurrentSource);
		domain2pages.put("5750678b3387e31f516fa1ca", pagesOfCurrentSource);
		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		System.out.println("dimensione4: "+pagesOfCurrentSource);
		domain2pages.put("5750678b3387e31f516fa1cd", pagesOfCurrentSource);
		currentSource = facade.getSourceWithId("5750678a3387e31f516fa185");
		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		System.out.println("dimensione5: "+pagesOfCurrentSource);
		domain2pages.put("5750678a3387e31f516fa185", pagesOfCurrentSource);
		//secondo modulo: filtri e selezioni le pagine di persone che compaiono in almeno 2 domini
		//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
//		Map<String,Set<WebPage>> domains2pagesWUNIMTOS = WebPageSelector.getPagesWUNIMTOS(domain2pages);
	}
	
}
