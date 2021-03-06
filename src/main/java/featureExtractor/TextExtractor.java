package featureExtractor;


import org.jsoup.Jsoup;

import database.MongoFacade;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import lib.utils.BoilerController;
import model.PageEntry;

public class TextExtractor {
	MongoFacade facade = new MongoFacade("crawler_db");

	public TextExtractor() {
	}

	public String getTextWithJsoup(String html) {
		return Jsoup.parse(html).text();
	}

	public String getTextWithBoilerArticle(String html) throws BoilerpipeProcessingException  {
		return ArticleExtractor.INSTANCE.getText(html);
	}

	public String getTextWithBoilerDefault(String html) throws BoilerpipeProcessingException {
		return ArticleExtractor.INSTANCE.getText(html);
	}
	
	public String getTextWithCleanHTMLTree(PageEntry page, BoilerController boiler) throws Exception {
		return boiler.boilPages(page);
	}
}
