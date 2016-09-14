package other;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import database.MongoFacade;
import lib.utils.BoilerController;
import lib.utils.MapUtils;
import lib.utils.PrettyPrintMap;
import model.BlacklistElement;
import model.Page;
import model.Source;
import nlp.LanguageDetectionFacade;
import nlp.NlpFacade;
import nlp.PolyglotFacade;

public class Test {

	public static void main(String[] args) throws Exception {
		//		MongoFacade mf = new MongoFacade();
		//		final List<Page> pages = mf.getPages(20);
		//		System.out.println(pages.size());
		//pages.forEach(item->System.out.println(item.toPrettyString()));

		//		String fileName = "/home/valentina/Scrivania/prova.txt";
		//
		//    	
		//    	String prova = "";
		//		//read file into stream, try-with-resources
		//    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		//
		//			String line;
		//			while ((line = br.readLine()) != null) {
		//				prova = prova + line;
		//			}
		//
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//    	
		//    	Document doc = Jsoup.parse(prova);
		//        
		//        System.out.println(doc.text());
		////		
		//		
		//		MongoFacade facade = new MongoFacade("profiles_development");
		//		BoilerController bc = new BoilerController();
		//		List<Source> sources = facade.getSources(1,3);
		//		
		//		Source siteReturn = new Source();
		//		
		//		for(int i=0;i<sources.size();i++) {
		////			System.out.println(sources.get(i).getId().toString());
		////			System.out.println("PRIMA");
		////			System.out.println(sources.get(i).toPrettyString());
		//		
		//		
		//			//System.out.println(e.toPrettyString());
		//	
		////	        siteReturn = bc.boilPages(sources.get(i));
		//		
		//		
		//        
		//
		//        
		//        
		//		//Source source2 = facade.getSourceWithId(siteReturn.getId().toString());
		//		//System.out.println(sources.get(i).getId().toString());
		//		//System.out.println(source2.toPrettyString());
		//		
		//		}

		//        for(int i=0;i<sources2.size();i++) {
		//        	System.out.println(sources2.get(i).getId().toString());
		//        	System.out.println("NEL DB");
		//	        System.out.println(sources2.get(i).toPrettyString());
		//	        System.out.println();
		//        }

		//		String s = "Avvocato Claudio Amoroso d'aragona a Roma - San Giovanni Claudio Amoroso d'aragona Claudio Amoroso d'aragona Via umberto biancamano, 33 00185 Telefono: 06.99709045";
		//		String languageCode = LanguageDetectionFacade.getInstance().detectLanguage(s);
		//
		//System.out.println("linguaggio "+languageCode);
		//
		//HashMap<String, List<String>> entitiesBoiler = PolyglotFacade.extractNamedEntities(languageCode, s);
		//System.out.println(new PrettyPrintMap<String, String>(entitiesBoiler));
		//		HashSet<BlacklistElement> blacklistedTxt = new HashSet<>();
		//		System.out.println(blacklistedTxt.isEmpty());

		String langCode="it";
		String text = "Ciao sono Alessandra Rossi. Come va? Io lavoro per la Apple.";
		//String text = "Carlo Amoruso Photography   CARLO AMORUSO PHOTOGRAPHY PORTRAITS PORTRAITS II LIFSTYLE Contacts Contacts: info@carloamoruso.com +393317812247  Rome +330618022893 Paris   Carlo Amoruso Photography, development and design by Gearup © 2013";

		try {
			//          text = text.replaceAll("\"", "'").replaceAll("\\p{C}", "").replaceAll("\t|\r|\n", " ");

			//          String input = "{\"lang\":\"" + langCode + "\",\"text\":\"" + text.replaceAll("\"", "'").replaceAll("\n", " ") + "\"}";


			String input = langCode.length()+langCode+text;

			//System.out.println(input);

			URL url = new URL("http://localhost:5000/todo/api/v1.0/tasks");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/plain");

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed - HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			JsonNode jsonNode = new ObjectMapper().readTree(br);
			JsonNode entities = jsonNode.get("entities");

			//System.out.println(jsonNode);
			HashMap<String, List<String>> map = new HashMap<>();

			entities.forEach(sentence -> {
				System.out.println(sentence);
			});

			entities.forEach(jsonNode1 -> {
				String key = jsonNode1.get(0).textValue();
				JsonNode s3 = jsonNode1.get(1);

				StringBuilder sb = new StringBuilder();
				int i = 0;

				for (int len = s3.size(); i < len; ++i) {
					if (i > 0) {
						sb.append(' ');
					}
					sb.append(s3.get(i).textValue());
				}

				MapUtils.mergeLists(map, key, sb.toString());
			});

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Polyglot TOKENIZE Exception on : " + e);
		}
	}

}


