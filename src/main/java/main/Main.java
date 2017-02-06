package main;

import java.io.PrintWriter;
//import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


import database.MongoFacade;
//import database.MongoFacade;
import model.InformationsMatching;
import model.Profile;
import model.ProfileRepository;
import model.Source;
import model.WebPage;
//import model.Source;
//import model.WebPage;

public class Main {

	static double parameterTextFusion = -1;
	static String path = "testGenericXpath/persone/";

	public static void main(String[] args) throws Exception {

		List<InformationsMatching> informations = 
				SegmentGraphGenerator.getInformations();

		ProfileRepository pr = ProfileRepository.getInstance();
		pr.updateProfiles(informations);

		//hai i profili, per ogni profilo  prendi il dominio
		//per ogni dominio prendi le pagine web
		//per ogni pagina applichi le xpath del profilo
		//salvi in un json le info
		//un json per dominio
		Map<Integer,Profile> domains2profile = pr.getRepository();
		for (int i=4; i<=5; i++) {
			Profile currentProfile = domains2profile.get(i);
			String id = currentProfile.getIdDbDomain();
			//per ogni profilo  prendi il dominio
			MongoFacade facade = new MongoFacade("web_search_pages");
			System.out.println("*****PRENDO UNA NUOVA SORGENTE");
			Source currentSource = facade.getSourceWithId(id);
			String json = "[";
			//per ogni dominio prendi le pagine web
			for (int j=0;j<currentSource.getPages().size();j++) {
				System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
				//per ogni pagina applichi le xpath del profilo
				WebPage currentPage = currentSource.getPages().get(j);
				List<String> contents = currentProfile.getContentInformation(currentPage, path, i);
				//ora faccio un json
				json = json + "{"+
						"url: \""+currentPage.getUrl()+"\", \n";
				for (int c=0; c<contents.size();c++) {
					String currentContent=contents.get(c);
					currentContent = currentContent.replaceAll("\"", "");
					//assumiamo che la lista  dei pathId abbia stessa lunghezza
					String pathCode=currentProfile.getMatchingInformation().get(c);
					//					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
				}
				json = json.substring(0, json.length()-2);
				json = json + "}, \n";

			}
			json = json.substring(0, json.length()-2);
			json = json + "]";
			//salvi in un json le info
			//un json per dominio
			PrintWriter testPrinter = new PrintWriter(path+"profiliDominio"+i+".json", "UTF-8");

			testPrinter.println(json);

			testPrinter.close();
			System.out.println("*****HO STAMPATO UN NUOVO JSON!");
			//poi elimina la sorgente, per risparmiare spazio
			currentSource = null;
		}
	}

}
