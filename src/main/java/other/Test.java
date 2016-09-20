package other;

import java.util.List;

import database.MongoFacade;
import model.WebPage;

public class Test {

	public static void main(String[] args) {
		MongoFacade facade = new MongoFacade("web_search_pages");
		String id ="5750678a3ceacf06c82caabf";
		List<WebPage> webpages = facade.getWebPagesWithQueryId(id);
		
		for (int i=0;i<webpages.size();i++) {
			System.out.println(i+ " " +webpages.get(i).getUrl());
		}
	}
}
