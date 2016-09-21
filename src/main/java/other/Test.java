package other;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.mongodb.morphia.query.MorphiaIterator;

import database.MongoFacade;
import model.WebPage;

public class Test {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		MongoFacade facade = new MongoFacade("web_search_pages");
		PrintWriter textPrinter = new PrintWriter("queries.txt", "UTF-8");
		//575068ee3ceacf06c82d6569 bagnasco luca
		//57506bbd3ceacf06c82e2acc gaia mariottini
		//57506bbe3ceacf06c82e2af3 giacomo laporta
		//57506c7a3ceacf06c82e54db luca parenti
		String id ="5750678a3ceacf06c82caabf";
		textPrinter.println(id);
		Iterator<WebPage> webpages = facade.getWebPagesWithQueryId(id);
		WebPage webpage2 = webpages.next();
		while(webpages.hasNext()) {
			WebPage webpage = webpages.next();
			if (facade.isValidated(webpage)) {
				textPrinter.println(webpage.getUrl());
				textPrinter.println();
			}
			textPrinter.println();textPrinter.println();textPrinter.println();textPrinter.println();
			((MorphiaIterator) webpages).close();
		}
		((MorphiaIterator) webpages).close();
		textPrinter.close();
	}
}
