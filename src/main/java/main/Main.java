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



		//		Map<Integer,WebPageDocument> domain2document = new HashMap<>();

		//		String d1Folder = path+"p1/";
		//		for(int j=1;j<=5;j++) {
		//			String currentFolder = d1Folder;
		//			String dPath = currentFolder+"orig"+j+".html";
		//			File d = new File(dPath);
		//			if (d.exists()) {
		//				WebPageDocument w = new WebPageDocument(
		//						new File(path+"p1/"+"orig"+j+".html"), 
		//						j, path+"p1/", 
		//						parameterTextFusion, j);
		//				domain2document.put(j, w);
		//			}
		//		}
		//		
		//cosa fare dopo?
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

		//			String d1Folder = path+"p1/";
		//			String dPath = d1Folder+"orig"+i+".html";
		//			File d = new File(dPath);
		//			String htmlDocumentString = IOUtils.toString(new FileReader(d));
		//			String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
		//					.addAttributes(":all", "class", "id"));
		//			Document document_jsoup = Jsoup.parse(cleanedHTML);
		//			clean(document_jsoup,path+"p1/",i);
		//			//per ogni pagina applichi le xpath del profilo
		//			List<String> contents = currentProfile.getContentInformation2(document_jsoup);
		//			//ora faccio un json

		//		for (int c=0; c<contents.size();c++) {
		//			String currentContent=contents.get(c);
		//			currentContent = currentContent.replaceAll("\"", "");
		//			//assumiamo che la lista  dei pathId abbia stessa lunghezza
		//			String pathCode=currentProfile.getMatchingInformation().get(c);
		//			//					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
		//			System.out.println( "xp"+(c+1)+": "+currentContent+" ");
		//			System.out.println( "xp"+pathCode+": "+currentContent+" ");
		//		}

		//TODO mi sa che hai cancellato qualche parentesi

		//salvi in un json le info
		//un json per dominio
		//						PrintWriter testPrinter = new PrintWriter(path+"profiliDominioPROVA"+i+".json", "UTF-8");
		//			
		//						testPrinter.println(json);
		//			
		//						testPrinter.close();
		//			System.out.println("*****HO STAMPATO UN NUOVO JSON!");
		//			//poi elimina la sorgente, per risparmiare spazio
		//						currentSource = null;
		//		}
	}

	//	private static void clean(Document doc, String cartella, int par) throws Exception {
	//		List<Document> usedPagesForCleaning = new ArrayList<>();
	//
	//		//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
	//		for (int i=1; i<=5;i++) {
	//			try {
	//				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
	//						new FileReader(new File(cartella+"pag"+par+"_"+i+".html")))));
	//			}
	//			catch (Exception e) {
	//				System.out.println("Errore pagina "+i + ": " + e);
	//			}
	//		}
	//		XpathExtractor xpextractor = XpathExtractor.getInstance();
	//		xpextractor.clean(doc, usedPagesForCleaning);
	//	}
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
