package other;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Iterator;

import database.MongoFacade;
import model.QueryEntry;
import model.WebPage;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		MongoFacade facade = new MongoFacade("web_search_pages");
		PrintWriter textPrinter = new PrintWriter("queries.txt", "UTF-8");
		Iterator<QueryEntry> queries = facade.getQueries();
		int i=0;
		while(queries.hasNext() && i<5) {
			QueryEntry query = queries.next();
			String id =query.getId().toString();
			textPrinter.println("---"+query.getQuery()+"---");
			textPrinter.println();
			Iterator<WebPage> webpages = facade.getWebPagesWithQueryId(id);
			while(webpages.hasNext()) {
				WebPage webpage = webpages.next();
				if (facade.isValidated(webpage)) {
					textPrinter.println(webpage.getUrl());
					textPrinter.println();
				}
			}
			textPrinter.println();textPrinter.println();textPrinter.println();textPrinter.println();
			i++;
		}

		textPrinter.close();
	}
}
