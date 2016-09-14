package featureExtractor;


import org.jsoup.Jsoup;

import database.MongoFacade;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import lib.utils.BoilerController;
import model.Page;
import model.Source;

public class TextExtractor {
	MongoFacade facade = new MongoFacade("profiles_development");
	BoilerController boiler = new BoilerController();

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
	
	public String getTextWithCleanHTMLTree(Page page) throws Exception {
		Source site = facade.getSite(page);	
		if (site != null)
			return boiler.boilPages(page);
		return "no sites";
	}
}
