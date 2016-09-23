package other;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mongodb.morphia.query.MorphiaIterator;

import database.MongoFacade;
import model.PageEntry;

public class ParentExtractor {

	final static int N_LIMIT=50;
	final static MongoFacade FACADE = new MongoFacade("crawler_db");
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {

		PrintWriter textPrinter = new PrintWriter("parents.txt", "UTF-8");
		List<String> exploredSources = new ArrayList<>();

		Iterator<PageEntry> iterator = FACADE.pageEntryIterator();
		try {
			int i=0;
			while (iterator.hasNext() && i<N_LIMIT) {
				PageEntry page = iterator.next();

				if (!exploredSources.contains(page.getCrawlingId())) {

					exploredSources.add(page.getCrawlingId());
					System.out.println("getting entity " +(i+1));

					textPrinter.println("------------------------"+(i+1)+"------------------------");
					String url = page.getPage().getUrl();
					String id = page.getId().toString();
					textPrinter.println(id);
					textPrinter.println(url);
					textPrinter.println("PARENT");
					textPrinter.println(page.getSnapshot().getParentId()+" \n");

					i++;
				}
			}
		} finally {
			textPrinter.close();
			((MorphiaIterator) iterator).close();
		}
	}
}