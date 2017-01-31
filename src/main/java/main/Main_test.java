package main;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import database.MongoFacade;
import model.InformationsMatching;
import model.Profile;
import model.ProfileRepository;
import model.Source;
import model.WebPage;

public class Main_test {

	static double parameterTextFusion = -1;
	static String path = "testGenericXpath/persone/";

	public static void main(String[] args) throws Exception {

//		List<InformationsMatching> informations = 
//				SegmentGraphGenerator.getInformations();
//
//		ProfileRepository pr = ProfileRepository.getInstance();
//		pr.updateProfiles(informations);

		//cosa fare dopo?
		//hai i profili, per ogni profilo  prendi il dominio
		//per ogni dominio prendi le pagine web
		//per ogni pagina applichi le xpath del profilo
		//salvi in un json le info
		//un json per dominio
//		Map<Integer,Profile> domains2profile = pr.getRepository();
//		for (int i=1; i<=5; i++) {
//			Profile currentProfile = domains2profile.get(i);
//			String id = currentProfile.getIdDbDomain();
			//per ogni profilo  prendi il dominio
			MongoFacade facade = new MongoFacade("web_search_pages");
			System.out.println("*****PRENDO UNA NUOVA SORGENTE");
			Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
//			String json = "[";
			//per ogni dominio prendi le pagine web
			for (int j=0;j<currentSource.getPages().size();j++) {
				System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
				//per ogni pagina applichi le xpath del profilo
				WebPage page = currentSource.getPages().get(j);
				System.out.println(page.getHtml());
				String cleanedHTML = Jsoup.clean(page.getHtml(), Whitelist.relaxed()
						.addAttributes(":all", "class", "id"));
				System.out.println("*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*");
				System.out.println(cleanedHTML);
				Document document_jsoup = Jsoup.parse(cleanedHTML);
				System.out.println("*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*");
				System.out.println(document_jsoup);
				
////				List<String> contents = currentProfile.getContentInformation(currentPage);
//				//ora faccio un json
//				json = json + "{"+
//						"url: \""+currentPage.getUrl()+"\", \n";
//				for (int c=0; c<contents.size();c++) {
//					String currentContent=contents.get(c);
//					currentContent = currentContent.replaceAll("\"", "");
//					//assumiamo che la lista  dei pathId abbia stessa lunghezza
//					String pathCode=currentProfile.getMatchingInformation().get(c);
//					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
//				}
//				json = json.substring(0, json.length()-2);
//				json = json + "}, \n";
//
//			}
//			json = json.substring(0, json.length()-2);
//			json = json + "]";
			//salvi in un json le info
			//un json per dominio
//			PrintWriter testPrinter = new PrintWriter(path+"profiliDominio"+i+".json", "UTF-8");
//
//			testPrinter.println(json);
//
//			testPrinter.close();
//			System.out.println("*****HO STAMPATO UN NUOVO JSON!");
//			//poi elimina la sorgente, per risparmiare spazio
//			currentSource = null;
		}
	}
}


//		System.out.println("*****************************INIZIO A ESTRARRE DAL DB*******************************");
//		MongoFacade facade = new MongoFacade("web_search_pages");
//		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
//		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
//		 currentSource = facade.getSourceWithId("5750678b3387e31f516fa1d0");
//		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
//		 currentSource = facade.getSourceWithId("575067b33387e31f516face0");
//		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
//		 currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
//		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
//		 currentSource = facade.getSourceWithId("5750678a3387e31f516fa185");
//		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
