package main;

import java.util.HashMap;
import java.util.Map;

import database.MongoFacade;
import model.Source;

public class SourceRep {

	static Map<String,Source> id2source = new HashMap<>();

//	public static void addSource(Source s) {
//		if (!id2source.containsKey(s.getId().toString()))
//			id2source.put(s.getId().toString(), s);
//	}

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
