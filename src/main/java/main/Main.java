//package main;
//
//import java.io.File;
//import java.io.PrintWriter;
//import java.sql.Timestamp;
////import java.io.PrintWriter;
//import java.util.List;
//import java.util.Map;
//
//
//import database.MongoFacade;
////import database.MongoFacade;
//import model.InformationsMatching;
//import model.Profile;
//import model.ProfileRepository;
//import model.Source;
//import model.WebPage;
////import model.Source;
////import model.WebPage;
//
//public class Main {
//
//	static double parameterTextFusion = -1;
//	
//	public static String path = "output/CC-Cache_10_conAND/";
//
//	public static void main(String[] args) throws Exception {
//
//		Timestamp timestamp_inizio = new Timestamp(System.currentTimeMillis());
//		
//		SourceInput.inizializzaLista();
//		System.out.println("lista inizializzata");
//		CronologiaStampe.println("lista inizializzata");
//
//		List<InformationsMatching> informations = 
//				SegmentGraphGenerator.getInformations();
//		System.out.println("informazioni generate");
//		CronologiaStampe.println("informazioni generate");
//
//		ProfileRepository pr = ProfileRepository.getInstance();
//		pr.updateProfiles(informations);
//		System.out.println("profili generati");
//		CronologiaStampe.println("profili generati");
//		
//
//		Timestamp timestamp_fine = new Timestamp(System.currentTimeMillis());
//		CronologiaStampe.println("inizio: "+timestamp_inizio);
//		CronologiaStampe.println("fine: "+timestamp_fine);
//		
//		CronologiaStampe.close();
//
//		//hai i profili, per ogni profilo  prendi il dominio
//		//per ogni dominio prendi le pagine web
//		//per ogni pagina applichi le xpath del profilo
//		//salvi in un json le info
//		//un json per dominio
//		Map<String,Profile> domains2profile = pr.getRepository();
//		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
//			String domain = SourceInput.getSorgenti().get(i);
//			Profile currentProfile = domains2profile.get(domain);
//			if (currentProfile != null) {
//				String id = currentProfile.getIdDomain();
//				//per ogni profilo  prendi il dominio
//				MongoFacade facade = new MongoFacade("web_search_pages");
//				System.out.println("*****PRENDO UNA NUOVA SORGENTE");
//				Source currentSource = facade.getSourceWithId(id);
//				String json = "[";
//				//per ogni dominio prendi le pagine web
//				// cambias
//				
////				for (int j=0;j<currentSource.getPages().size();j++) {
//				for (int j=0;j<100 && j<currentSource.getPages().size();j++) {
//					//				for (int j=0;j<100;j++) {
//					if ((j+1)%10==0) 
//						System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
//					//per ogni pagina applichi le xpath del profilo
//					WebPage currentPage = currentSource.getPages().get(j);
//					List<String> contents = currentProfile.getContentInformation(currentPage, currentSource.getId().toString());
//					//ora faccio un json
//					json = json + "{"+
//							"url: \""+currentPage.getUrl()+"\", \n";
//					for (int c=0; c<contents.size();c++) {
//						String currentContent=contents.get(c);
//						currentContent = currentContent.replaceAll("\"", "");
//						currentContent = currentContent.replaceAll("\n", "");
//						String pathCode=currentProfile.getMatchingInformation().get(c);
//						//					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
//						json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
//					}
//					json = json.substring(0, json.length()-2);
//					json = json + "}, \n";
//
//				}
//				json = json.substring(0, json.length()-2);
//				json = json + "]";
//				//salvi in un json le info
//				//un json per dominio
//				File dir = new File(path);
//				dir.mkdirs();
//				PrintWriter testPrinter = new PrintWriter(path+"profiliDominio_"+
//						domain.substring(domain.length()-4, domain.length())
//				+".json", "UTF-8");
//
//				testPrinter.println(json);
//
//				testPrinter.close();
//				System.out.println("*****HO STAMPATO UN NUOVO JSON!");
//				//poi elimina la sorgente, per risparmiare spazio
//				currentSource = null;
//			}
//		}
//	}
//
//}
