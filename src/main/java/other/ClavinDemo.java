package other;

import java.io.File;
import java.util.List;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.extractor.ApacheExtractor;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.resolver.ResolvedLocation;
import com.bericotech.clavin.util.TextUtils;

public class ClavinDemo {

	/**
	 * Run this after installing & configuring CLAVIN to get a sense of
	 * how to use it.
	 * 
	 * @param args              not used
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Instantiate the CLAVIN GeoParser
		GeoParser parser = GeoParserFactory.getDefault("./IndexDirectory");

		// Unstructured text file about Somalia to be geoparsed
		File inputFile = new File("somalia.txt");

		// Grab the contents of the text file as a String
		String inputString = TextUtils.fileToString(inputFile);
		//String sb = "Acquaviva, Altamura, Andria, Bari, Barletta, Bisceglie, Bitonto, Cassano, Corato, Fasano, Gioia, Giovinazzo, Gravina, Margherita, Melfi, Minervino, Molfetta, Monopoli, Ostuni, Palo, Ruvo, Santeramo, Spinazzola, Terlizzi, Trani";

		String s = "Europe";

		ApacheExtractor e = new ApacheExtractor();

		List<LocationOccurrence> occurrencesExtracted = e.extractLocationNames(s);

		/*nota: restituisce la stringa estratta e la posizione nel testo!!*/
		for (LocationOccurrence occurrenceExtracted : occurrencesExtracted)
			System.out.println(occurrenceExtracted.getText());

		// Parse location names in the text into geographic entities
		List<ResolvedLocation> resolvedLocations = parser.parse(s);

		// Display the ResolvedLocations found for the location names
		for (ResolvedLocation resolvedLocation : resolvedLocations)
			System.out.println(resolvedLocation.getGeoname());
		
		System.out.println("done");
		

	}
}
