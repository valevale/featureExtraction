package test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import database.MongoFacade;
import database.WebPageSelector;
import model.Source;
import model.WebPage;
import model.WebPageDocument;

public class TestPagineUniche {

	public static void main(String[] args) throws Exception {
		List<String> idSorgenti = new ArrayList<>();
		idSorgenti.add("5750678b3387e31f516fa1c7");
		idSorgenti.add("5750678b3387e31f516fa1d0");
		idSorgenti.add("5750678b3387e31f516fa1ca");
		idSorgenti.add("5750678b3387e31f516fa1cd");
		idSorgenti.add("5750678a3387e31f516fa185");
		MongoFacade facade = new MongoFacade("web_search_pages");
		Map<Source,List<WebPage>> domain2pages = new HashMap<>();
		for (int i=0;i<idSorgenti.size();i++) {
			Source currentSource = facade.getSourceWithId(idSorgenti.get(i));
			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
			domain2pages.put(currentSource, pagesOfCurrentSource);
		}

		System.out.println("FINE PRIMO MODULO");
		
		//secondo modulo: filtri e selezioni le pagine di persone che compaiono in almeno 2 domini
		Map<String,Set<WebPageDocument>> ancore2pagesWUNIMTOS = WebPageSelector.getPagesWUNIMTOS(domain2pages);
		
		PrintWriter testPrinterMap = new PrintWriter("testPagineUniche.txt", "UTF-8");
		Iterator<String> it = ancore2pagesWUNIMTOS.keySet().iterator();
		while (it.hasNext()) {
			String ancora = it.next();
			Set<WebPageDocument> set = ancore2pagesWUNIMTOS.get(ancora);
			testPrinterMap.print(ancora+" -> "+set.size()+" (");
			Iterator<WebPageDocument> wpdit = set.iterator();
			while (wpdit.hasNext()) {
				WebPageDocument w = wpdit.next();
				System.out.println(w.getIdPage()+"||||");
			}
			testPrinterMap.println(");");
			testPrinterMap.println();
		}
		testPrinterMap.close();
	}

}
