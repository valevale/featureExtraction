package other;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Iterator;

import database.MongoFacade;
import model.WebPage;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		MongoFacade facade = new MongoFacade("web_search_pages");
		String id ="5750678a3ceacf06c82caabf";
		Iterator<WebPage> webpages = facade.getWebPagesWithQueryId(id);
		PrintWriter textPrinter = new PrintWriter("queries.txt", "UTF-8");
		while(webpages.hasNext()) {
			WebPage webpage = webpages.next();
			if (facade.isValidated(webpage)) {
				textPrinter.println(webpage.getUrl());
			}
		}
		
		textPrinter.close();
	}
}
