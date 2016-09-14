package featureExtractor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.mongodb.morphia.query.MorphiaIterator;

import database.MongoFacade;
import lib.utils.PrettyPrintMap;
import model.Page;
import model.Source;
import nlp.NlpFacade;

public class EntityExtractor {

	final static int N_LIMIT=30;
	final static MongoFacade FACADE = new MongoFacade("profiles_development");
	final static String FILENAME = "resultExtraction4.txt";
    final static Logger log = Logger.getLogger(featureExtractor.EntityExtractor.class);

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
		PrintWriter locationsPrinter = new PrintWriter("locations.txt", "UTF-8");

		Iterator<Page> iterator = FACADE.pageIterator();
		try {
			int i=0;
			String html;
			while (iterator.hasNext() && i<=N_LIMIT) {
				Page page = iterator.next();
				Source site = FACADE.getSite(page);
				if (site != null) {
					if (FACADE.isPageMonitored(page)) {
						html = page.getHtml();
						System.out.println("getting entity " +(i+1));
						log.debug("------------------------------GETTING ENTITY " +(i+1));
						TextExtractor te = new TextExtractor();


						textPrinter.println("------------------------"+(i+1)+"------------------------");
						entitiesPrinter.println("------------------------"+(i+1)+"------------------------");
						locationsPrinter.println("------------------------"+(i+1)+"------------------------");
						String url = page.getUrl();
						String id = page.getId().toString();
						textPrinter.println(url);
						entitiesPrinter.println(url);
						locationsPrinter.println(url);
						textPrinter.println(id);
						entitiesPrinter.println(id);
						locationsPrinter.println(id);
						int pagesToCleanSize = FACADE.getSourcePages(site.getId().toString(), 5).size();
						textPrinter.println("PAGINE UTILIZZATE PER LA PULIZIA: "+pagesToCleanSize+"\n");
						entitiesPrinter.println("PAGINE UTILIZZATE PER LA PULIZIA: "+pagesToCleanSize+"\n");
						locationsPrinter.println("PAGINE UTILIZZATE PER LA PULIZIA: "+pagesToCleanSize+"\n");

						/*JSOUP*/
						textPrinter.println("JSOUP\n");
						entitiesPrinter.println("JSOUP\n");
						locationsPrinter.println("JSOUP\n");
						String textJsoup = te.getTextWithJsoup(html);
						textPrinter.println(textJsoup+"\n\n\n");
						HashMap<String, List<String>> entitiesJsoup = NlpFacade.getEntities(textJsoup, html);
						HashMap<String, List<String>> locationsJsoup = NlpFacade.getLocations(textJsoup, html);
						entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesJsoup)+"\n\n\n");
						locationsPrinter.println(new PrettyPrintMap<String, String>(locationsJsoup)+"\n\n\n");

						/*CLEAN*/
						textPrinter.println("CLEAN\n");
						entitiesPrinter.println("CLEAN\n");
						locationsPrinter.println("CLEAN\n");
						String textClean = te.getTextWithCleanHTMLTree(page);
						textPrinter.println(textClean+"\n\n\n");
						HashMap<String, List<String>> entitiesClean = NlpFacade.getEntities(textClean, html);
						HashMap<String, List<String>> locationsClean = NlpFacade.getLocations(textClean, html);
						entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesClean)+"\n\n\n");
						locationsPrinter.println(new PrettyPrintMap<String, String>(locationsClean)+"\n\n\n");

						/*BOILER*/
						textPrinter.println("BOILER\n");
						entitiesPrinter.println("BOILER\n");
						locationsPrinter.println("BOILER\n");
						String textBoiler = te.getTextWithBoilerArticle(html);
						textPrinter.println(textBoiler);
						HashMap<String, List<String>> entitiesBoiler = NlpFacade.getEntities(textBoiler, html);
						HashMap<String, List<String>> locationsBoiler = NlpFacade.getLocations(textBoiler, html);
						entitiesPrinter.println(new PrettyPrintMap<String, String>(entitiesBoiler));
						locationsPrinter.println(new PrettyPrintMap<String, String>(locationsBoiler)+"\n\n\n");

						i++;
					}
				}
			}
		} finally {
			textPrinter.close();
			entitiesPrinter.close();
			locationsPrinter.close();
			((MorphiaIterator) iterator).close();
		}
	}
}