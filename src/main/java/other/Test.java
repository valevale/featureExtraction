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
		int i=1;
		while(webpages.hasNext()) {
			System.out.print(i+" "+webpages.next().getUrl()+" ");
			textPrinter.print(i+" "+webpages.next().getUrl()+" ");
			if (facade.isValidated(webpages.next())) {
				System.out.print("VALIDATO");
				textPrinter.print("VALIDATO");
			}
			System.out.println();
			textPrinter.println();
			i++;
		}
		
		textPrinter.close();
	}
}
