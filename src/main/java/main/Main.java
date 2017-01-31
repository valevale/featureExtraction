package main;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import database.MongoFacade;
import model.InformationsMatching;
import model.Profile;
import model.ProfileRepository;
import model.Source;
import model.WebPage;

public class Main {

	static double parameterTextFusion = -1;
	static String path = "testGenericXpath/persone/";

	public static void main(String[] args) throws Exception {

		List<InformationsMatching> informations = 
				SegmentGraphGenerator.getInformations();

		ProfileRepository pr = ProfileRepository.getInstance();
		pr.updateProfiles(informations);

		//cosa fare dopo?
		//hai i profili, per ogni profilo  prendi il dominio
		//per ogni dominio prendi le pagine web
		//per ogni pagina applichi le xpath del profilo
		//salvi in un json le info
		//un json per dominio
		Map<Integer,Profile> domains2profile = pr.getRepository();
		for (int i=1; i<=5; i++) {
			Profile currentProfile = domains2profile.get(i);
			String id = currentProfile.getIdDbDomain();
			//per ogni profilo  prendi il dominio
			MongoFacade facade = new MongoFacade("web_search_pages");
			System.out.println("*****PRENDO UNA NUOVA SORGENTE");
			Source currentSource = facade.getSourceWithId(id);
			String json = "[";
			//per ogni dominio prendi le pagine web
			for (int j=0;j<currentSource.getPages().size();j++) {
				//per ogni pagina applichi le xpath del profilo
				WebPage currentPage = currentSource.getPages().get(j);
				List<String> contents = currentProfile.getContentInformation(currentPage);
				//ora faccio un json
				json = json + "{"+
						"url: \""+currentPage.getUrl()+"\", ";
				for (int c=0; c<contents.size();c++) {
					String currentContent=contents.get(c);
					currentContent = currentContent.replaceAll("\"", "");
					//assumiamo che la lista  dei pathId abbia stessa lunghezza
					String pathCode=currentProfile.getMatchingInformation().get(c);
					json = json + "xp"+pathCode+": \""+currentContent+"\", ";
				}
				json = json.substring(0, json.length()-2);
				json = json + "}, ";

			}
			json = json.substring(0, json.length()-2);
			json = json + "]";
			//salvi in un json le info
			//un json per dominio
			PrintWriter testPrinter = new PrintWriter(path+"profiliDominio"+i+".json", "UTF-8");

			testPrinter.println(json);

			testPrinter.close();
			//poi elimina la sorgente, per risparmiare spazio
			currentSource = null;
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
