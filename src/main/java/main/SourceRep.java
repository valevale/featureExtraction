package main;

import java.util.Map;

import database.MongoFacade;
import model.Source;

public class SourceRep {

	static Map<String,Source> id2source;

	public static void addSource(String id) {
		MongoFacade facade = new MongoFacade("web_search_pages");
		Source s = facade.getSourceWithId(id);
		id2source.put(id, s);
	}

	public static Source getSource(String id) {
		Source s = id2source.get(id);
		if (s == null) {
			MongoFacade facade = new MongoFacade("web_search_pages");
			s = facade.getSourceWithId(id);
			id2source.put(id, s);
		}
		return s;
	}

}
