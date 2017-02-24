package main;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
//import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import database.MongoFacade;
import database.WebPageSelector;
import lib.utils.DocumentUtils;
import model.CleanedPagesRepository;
import model.DomRepToClean;
//import database.MongoFacade;
import model.InformationsMatching;
import model.Profile;
import model.ProfileRepository;
import model.Source;
import model.WebPage;
//import model.Source;
//import model.WebPage;
import scala.Tuple2;

public class MainNEW {

//	static double parameterTextFusion = -1;
	
	public static String path = "outputNEW/CC_2Domini/";

	public static void main(String[] args) throws Exception {

		Timestamp timestamp_inizio = new Timestamp(System.currentTimeMillis());
		
		SourceInput.inizializzaLista();
		System.out.println("lista inizializzata");
//		CronologiaStampe.println("lista inizializzata");
		
		prepareCleanedPagesAndPagesWithUniqueName(SourceInput.getSorgenti());
		
//		CleanedPagesRepository.clean(SourceInput.getSorgenti());
		System.out.println("pagine per la verifica di significatività delle xpath pulite");
//		CronologiaStampe.println("pagine per la verifica di significatività delle xpath pulite");

		List<InformationsMatching> informations = 
				SegmentGraphGenerator.getInformations();
		System.out.println("informazioni generate");
//		CronologiaStampe.println("informazioni generate");

		ProfileRepository pr = ProfileRepository.getInstance();
		pr.updateProfiles(informations);
		System.out.println("profili generati");
//		CronologiaStampe.println("profili generati");
		

		Timestamp timestamp_fine = new Timestamp(System.currentTimeMillis());
		CronologiaStampe.println("inizio: "+timestamp_inizio);
		CronologiaStampe.println("fine: "+timestamp_fine);
		
		CronologiaStampe.close();

		//hai i profili, per ogni profilo  prendi il dominio
		//per ogni dominio prendi le pagine web
		//per ogni pagina applichi le xpath del profilo
		//salvi in un json le info
		//un json per dominio
		Map<String,Profile> domains2profile = pr.getRepository();
		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
			String domain = SourceInput.getSorgenti().get(i);
			Profile currentProfile = domains2profile.get(domain);
			if (currentProfile != null) {
				String id = currentProfile.getIdDomain();
				//per ogni profilo  prendi il dominio
				MongoFacade facade = new MongoFacade("web_search_pages");
				System.out.println("*****PRENDO UNA NUOVA SORGENTE");
				Source currentSource = facade.getSourceWithId(id);
				String json = "[";
				//per ogni dominio prendi le pagine web
				// cambias
				
//				for (int j=0;j<currentSource.getPages().size();j++) {
				for (int j=0;j<100 && j<currentSource.getPages().size();j++) {
					//				for (int j=0;j<100;j++) {
					if ((j+1)%10==0) 
						System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
					//per ogni pagina applichi le xpath del profilo
					WebPage currentPage = currentSource.getPages().get(j);
					List<Tuple2<String, String>> contents = currentProfile.getXpathAndContentInformation(currentPage, currentSource.getId().toString());
					//ora faccio un json
					json = json + "{"+
							"url: \""+currentPage.getUrl()+"\", \n";
					for (int c=0; c<contents.size();c++) {
						String currentContent=contents.get(c)._2();
						currentContent = currentContent.replaceAll("\"", "");
						currentContent = currentContent.replaceAll("\n", "");
						String pathCode=currentProfile.getMatchingInformation().get(c);
						//					json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
						json = json + "xp"+pathCode+": \""+currentContent+"\", \n";
						json = json + "xpath: \""+contents.get(c)._1()+"\", \n";
					}
					json = json.substring(0, json.length()-2);
					json = json + "}, \n";

				}
				json = json.substring(0, json.length()-2);
				json = json + "]";
				//salvi in un json le info
				//un json per dominio
				File dir = new File(path);
				dir.mkdirs();
				PrintWriter testPrinter = new PrintWriter(path+"profiliDominioConXpath_"+
						domain.substring(domain.length()-4, domain.length())
				+".json", "UTF-8");

				testPrinter.println(json);

				testPrinter.close();
				System.out.println("*****HO STAMPATO UN NUOVO JSON!");
				//poi elimina la sorgente, per risparmiare spazio
				currentSource = null;
			}
		}
	}
	
	public static void prepareCleanedPagesAndPagesWithUniqueName(List<String> idSorgenti) throws Exception {
		DomRepToClean drtc = DomRepToClean.getInstance();
		Map<String,List<WebPage>> domain2pages = new HashMap<>();
		for (int i=0;i<idSorgenti.size();i++) {
			//clean
			List<Document> cleanedPages = new ArrayList<>();
			MongoFacade facade = new MongoFacade("web_search_pages");
			Source s = facade.getSourceWithId(idSorgenti.get(i));
			drtc.addDomain(s);
			for (int j=0;j<100;j++) {
				WebPage currentPage = s.getPages().get(j);
				Document doc = DocumentUtils.prepareDocument(currentPage.getHtml(), idSorgenti.get(i));
				cleanedPages.add(doc);
			}
			CleanedPagesRepository.putIntoRepository(idSorgenti.get(i), cleanedPages);
			//fine clean
			//pages with unique names
			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(s);
			domain2pages.put(s.getId().toString(), pagesOfCurrentSource);
		}
		PagesWithUniqueNamesRepository.setDomain2Pages(domain2pages);
	}

}
