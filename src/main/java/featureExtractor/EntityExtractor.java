package featureExtractor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.mongodb.morphia.query.MorphiaIterator;

import database.MongoFacade;
import lib.utils.BoilerController;
import lib.utils.PrettyPrintMap;
import model.PageEntry;
import nlp.NlpFacade;

public class EntityExtractor {

	final static int N_LIMIT=20;
	final static MongoFacade FACADE = new MongoFacade("crawler_db");
	//final static Logger log = Logger.getLogger(featureExtractor.EntityExtractor.class);
	final static BoilerController bc = new BoilerController();

	//TODO il vero metodo estrae le entità e le restituisce

	//TODO OVVIAMENTE QUANDO SCEGLI IL METODO PER PULIRE, SE QUESTO RESTITUISCE STRINGA VUOTA, USA JSOUP
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		//		int offset=10;
		//		List<Page> pages = FACADE.getPages(N_LIMIT,offset);
		//		
		//		for(int i =0;i<pages.size();i++) {
		//			Page p = pages.get(i);
		//			System.out.println("1 "+p.toPrettyString());
		//			Source site = FACADE.getSite(p);
		//			BoilerController bc = new BoilerController();
		//			System.out.println(bc.boilPages(site));
		//		}

		PrintWriter textPrinter = new PrintWriter("texts.txt", "UTF-8");
		PrintWriter entitiesPrinter = new PrintWriter("entities.txt", "UTF-8");
		List<String> exploredSources = new ArrayList<>();

		Iterator<PageEntry> iterator = FACADE.pageEntryIterator();
		try {
			int i=0;
			String html;
			while (iterator.hasNext() && i<N_LIMIT) {
				PageEntry page = iterator.next();
				html = page.getPage().getBody();
				System.out.println("getting entity " +(i+1));
				//log.debug("****************************GETTING ENTITY " +(i+1));
				TextExtractor te = new TextExtractor();
				if (!exploredSources.contains(page.getCrawlingId())) {
					
					exploredSources.add(page.getCrawlingId());

					textPrinter.println("------------------------"+(i+1)+"------------------------");
					entitiesPrinter.println("------------------------"+(i+1)+"------------------------");
					String url = page.getPage().getUrl();
					String id = page.getId().toString();
					textPrinter.println(id+"\n");
					entitiesPrinter.println(id+"\n");
					textPrinter.println(url+"\n");
					entitiesPrinter.println(url+"\n");
					String textClean = te.getTextWithCleanHTMLTree(page, bc);
					List<PageEntry> pagesToClean = bc.getUsedPagesForCleaning();
					for (int j=0;j<pagesToClean.size();j++) {
						textPrinter.println(pagesToClean.get(j).getPage().getUrl());
						entitiesPrinter.println(pagesToClean.get(j).getPage().getUrl());
					}
					pagesToClean.clear();
					bc.setUsedPagesForCleaning(pagesToClean);

					/*JSOUP*/
					textPrinter.println("JSOUP\n");
					entitiesPrinter.println("JSOUP\n");
					String textJsoup = te.getTextWithJsoup(html);
					textPrinter.println(textJsoup+"\n\n\n");
					HashMap<String, List<String>> entitiesJsoup = NlpFacade.getEntities(textJsoup, html);
					entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesJsoup)+"\n\n\n");

					/*CLEAN*/
					textPrinter.println("CLEAN\n");
					entitiesPrinter.println("CLEAN\n");

					textPrinter.println(textClean+"\n\n\n");
					HashMap<String, List<String>> entitiesClean = NlpFacade.getEntities(textClean, html);
					entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesClean)+"\n\n\n");

					/*BOILER*/
					textPrinter.println("BOILER\n");
					entitiesPrinter.println("BOILER\n");
					String textBoiler = te.getTextWithBoilerArticle(html);
					textPrinter.println(textBoiler);
					HashMap<String, List<String>> entitiesBoiler = NlpFacade.getEntities(textBoiler, html);
					entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesBoiler));

					i++;
				}
			}
		} finally {
			textPrinter.close();
			entitiesPrinter.close();
			((MorphiaIterator) iterator).close();
		}
	}
}