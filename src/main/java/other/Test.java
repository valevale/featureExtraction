package other;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import database.MongoFacade;
import model.WebPage;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		MongoFacade facade = new MongoFacade("web_search_pages");
		String id ="5750678a3ceacf06c82caabf";
		List<WebPage> webpages = facade.getWebPagesWithQueryId(id);
		PrintWriter textPrinter = new PrintWriter("queries.txt", "UTF-8");
		
		for (int i=0;i<webpages.size();i++) {
			System.out.print(i+" "+webpages.get(i).getUrl()+" ");
			textPrinter.print(i+" "+webpages.get(i).getUrl()+" ");
			if (facade.isValidated(webpages.get(i))) {
				System.out.print("VALIDATO");
				textPrinter.print("VALIDATO");
			}
			System.out.println();
			textPrinter.println();
		}
		
		textPrinter.close();
	}
}
