package nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Created by lorenzoluce on 04/08/16.
 */
public class NlpFacade {

	//prendo il text e l'html da analizzare e restituisce la mappa
	public static HashMap<String, List<String>> getEntities(String text, String html) throws Exception {

		// trasformare encoding text in utf-8
		//text = convertToUTF8(text);

		HashMap<String, List<String>> entities = new HashMap<>();

		String languageCode = LanguageDetectionFacade.getInstance().detectLanguage(text);

		entities = PolyglotFacade.extractNamedEntities(languageCode, text);

		entities.putAll(PatternExtractor.getInstance().extractFeatures(text, html));

		//TODO va in un metodo a parte, non nella facade
		GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");

		List<ResolvedLocation> resolvedLocations = parser.parse(text);

		List<String> occurrencesExtractedToString = new ArrayList<>();

		for (ResolvedLocation resolvedLocation : resolvedLocations)
			occurrencesExtractedToString.add(resolvedLocation.getMatchedName()+" = "+resolvedLocation.getGeoname());

		
		entities.put("CLAVIN-EXTRACTOR", occurrencesExtractedToString);
		if (entities.get("I-LOC") != null) {
			//entities.put("CLAVIN-RESOLUTOR", resolve(entities.get("I-LOC")));
		}

		return entities;
	}

	public static HashMap<String, List<String>> getLocations(String text, String html) throws Exception {

		// trasformare encoding text in utf-8
		//text = convertToUTF8(text);

		HashMap<String, List<String>> entities = new HashMap<>();

		String languageCode = LanguageDetectionFacade.getInstance().detectLanguage(text);

		entities = PolyglotFacade.extractNamedEntities(languageCode, text);

		//TODO va in un metodo a parte, non nella facade
		GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");

		List<ResolvedLocation> resolvedLocations = parser.parse(text);

		List<String> occurrencesExtractedToString = new ArrayList<>();

		for (ResolvedLocation resolvedLocation : resolvedLocations)
			occurrencesExtractedToString.add(resolvedLocation.getMatchedName()+" = "+resolvedLocation.getGeoname());

		HashMap<String, List<String>> locations = new HashMap<>();


		locations.put("CLAVIN-EXTRACTOR", occurrencesExtractedToString);
		if (entities.get("I-LOC") != null) {
			locations.put("POLYGLOT", entities.get("I-LOC"));
			//locations.put("CLAVIN-RESOLUTOR", resolve(entities.get("I-LOC")));
		}
		return locations;
	}

	public static List<String> resolve(List<String> locations) throws Exception {
		List<String> resolvedLocations = new ArrayList<>();
		ClavinFacade cf = ClavinFacade.getInstance();
		for(int i=0;i<locations.size();i++) {
			String location = locations.get(i);
			List<GeoName> geonames = cf.resolveLocation(location);
			geonames.forEach(geoname -> {
				resolvedLocations.add(location + " = " + geoname.toString());
			});
		}
		
		return resolvedLocations;
	}

}
