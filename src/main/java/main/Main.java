package main;

import java.util.List;
import java.util.Map;

import database.MongoFacade;
import model.InformationsMatching;
import model.Profile;
import model.ProfileRepository;
import model.Source;

public class Main {

	static double parameterTextFusion = -1;
	static String path = "/home/valentina/workspace_nuovo/DataFusion/testGenericXpath/persone/";

	public static void main(String[] args) throws Exception {
		
//		List<InformationsMatching> informations = 
//				SegmentGraphGenerator.getInformations();
//		
//
//		//TODO fallo altrove
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
////			Profile currentProfile = domains2profile.get(i);
////			String id = currentProfile.getIdDbDomain();
//			//per ogni profilo  prendi il dominio
//			MongoFacade facade = new MongoFacade("web_search_pages");
//			System.out.println("*****PRENDO UNA NUOVA SORGENTE");
//			Source currentSource = facade.getSourceWithId(id);
//			System.out.println(currentSource.getId().toString());
//		}
		System.out.println("*****************************INIZIO A ESTRARRE DAL DB*******************************");
		MongoFacade facade = new MongoFacade("web_search_pages");
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
		 currentSource = facade.getSourceWithId("5750678b3387e31f516fa1d0");
		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
		 currentSource = facade.getSourceWithId("575067b33387e31f516face0");
		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
		 currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
		 currentSource = facade.getSourceWithId("5750678a3387e31f516fa185");
		System.out.println("***ECCO L'ID "+currentSource.getId().toString());
	}
}
