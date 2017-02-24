package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import database.MongoFacade;
import lib.utils.PrettyPrintMap;
import model.Profile;
import model.ProfileRepository;
import model.WebPage;
import scala.Tuple2;


public class TestAccuracy {

	/*in questo test valutiamo l'accuratezza dell'allineamento verticale*/
	public static void testVerticale() throws Exception {
		//i profili devono essere già stati generati
		ProfileRepository pr = ProfileRepository.getInstance();
		Map<String,Profile> domains2profile = pr.getRepository();
		//prendo la mappa del parse verticale
		Map<String,Map<Tuple2<String,String>,List<String>>> ancora2webPage2valori =
				parseFile_verticale("QUI SERVE IL pathConFile!!!!!!");
		//analizzo ogni ancora
		Iterator<String> ancoraIt = ancora2webPage2valori.keySet().iterator();
		List<Float> fmeasures = new ArrayList<>();
		while (ancoraIt.hasNext()) {
			String ancora = ancoraIt.next();
			Map<Tuple2<String,String>,List<String>> dom_wp2valori = ancora2webPage2valori.get(ancora);
			Iterator<Tuple2<String,String>> d_wpIt = dom_wp2valori.keySet().iterator();
			while (d_wpIt.hasNext()) {
				Tuple2<String,String> dom_wp = d_wpIt.next();
				String domain = dom_wp._1();
				String webPageId = dom_wp._2();
				List<String> valoriGT = dom_wp2valori.get(dom_wp);
				//prendo il profilo del rispettivo dominio
				Profile profilo = domains2profile.get(domain);
				//prendo la pagina web che ci interessa
				MongoFacade facade = new MongoFacade("web_search_pages");
				WebPage webpage = facade.getWebPageWithId(webPageId);
				List<String> contents = profilo.getContentInformation(webpage,
						domain);
				//TODO ricordati di privare le stringhe che estrai dalle pagine web di virgole,\m e ALTRO
				//pulisco i contents in valoriR
				List<String> valoriR = new ArrayList<>();
				for (int i=0;i<contents.size();i++) {
					String currentContent = contents.get(i);
					currentContent = currentContent.replaceAll(",", "");
					currentContent = currentContent.replaceAll("\n", "");
					valoriR.add(currentContent);
				}
				//ora che ho i due valori li confronto
				//veri positivi: quelli che si trovano in GT e R
				Set<String> truePositives = new HashSet<>(valoriGT);
				truePositives.retainAll(valoriR);
				//falsi positivi: quelli che si trovano in valoriR ma non in valoriGT
				//falsi negativi: quelli che si trovano in GT ma non in R
				float precision = (float) truePositives.size() / valoriR.size();
				float recall =  (float) truePositives.size() / valoriGT.size();
				float fMeasure = (float) 2*((precision*recall)/(precision+recall));
				fmeasures.add(fMeasure);
			}
			//TODO pensa se vuoi un fmeasure per ogni persona
		}
		//calcolo la fMeasure media
		float sum_measures = 0;
		for (int i=0;i<fmeasures.size();i++) {
			sum_measures = sum_measures + fmeasures.get(i);
		}
		float mean_fMeasure = sum_measures / fmeasures.size();
	}
	
	/*in questo test valutiamo l'accuratezza dell'allineamento verticale*/
	public static void testOrizzontale() throws Exception {
		//i profili devono essere già stati generati
		ProfileRepository pr = ProfileRepository.getInstance();
		Map<String,Profile> domains2profile = pr.getRepository();
		//prendo la mappa del parse verticale
		Map<String,Map<Tuple2<String,String>,List<Tuple2<String,String>>>> ancora2webPage2valori =
				parseFile_orizzontale("QUI SERVE IL pathConFile!!!!!!");
		//analizzo ogni ancora
		Iterator<String> ancoraIt = ancora2webPage2valori.keySet().iterator();
		List<Float> fmeasures = new ArrayList<>();
		while (ancoraIt.hasNext()) {
			String ancoraCorrente = ancoraIt.next();
			Map<Tuple2<String,String>,List<Tuple2<String,String>>> dom_wp2valori = 
					ancora2webPage2valori.get(ancoraCorrente);
			Iterator<Tuple2<String,String>> d_wpIt = dom_wp2valori.keySet().iterator();
			while (d_wpIt.hasNext()) {
				Tuple2<String,String> dom_wp = d_wpIt.next();
				String domain = dom_wp._1();
				String webPageId = dom_wp._2();
				
				List<Tuple2<String,String>> valoriGT = dom_wp2valori.get(dom_wp);
				//TODO da qui continua
				
				
				
				//prendo il profilo del rispettivo dominio
				Profile profilo = domains2profile.get(domain);
				//prendo la pagina web che ci interessa
				MongoFacade facade = new MongoFacade("web_search_pages");
				WebPage webpage = facade.getWebPageWithId(webPageId);
				List<String> contents = profilo.getContentInformation(webpage,
						domain);
				//TODO qui fai una mappa: per ogni contenuto -> altri contenuti delle altre pagine allineate
				//TODO ricordati di privare le stringhe che estrai dalle pagine web di virgole,\m e ALTRO
				//pulisco i contents in valoriR
				List<String> valoriR = new ArrayList<>();
				for (int i=0;i<contents.size();i++) {
					String currentContent = contents.get(i);
					currentContent = currentContent.replaceAll(",", "");
					currentContent = currentContent.replaceAll("\n", "");
					valoriR.add(currentContent);
				}
				//ora che ho i due valori li confronto
				//veri positivi: quelli che si trovano in GT e R
				Set<Tuple2<String,String>> truePositives = new HashSet<>(valoriGT);
				truePositives.retainAll(valoriR);
				//falsi positivi: quelli che si trovano in valoriR ma non in valoriGT
				//falsi negativi: quelli che si trovano in GT ma non in R
				float precision = (float) truePositives.size() / valoriR.size();
				float recall =  (float) truePositives.size() / valoriGT.size();
				float fMeasure = (float) 2*((precision*recall)/(precision+recall));
				fmeasures.add(fMeasure);
			}
			//TODO pensa se vuoi un fmeasure per ogni persona
		}
		//calcolo la fMeasure media
		float sum_measures = 0;
		for (int i=0;i<fmeasures.size();i++) {
			sum_measures = sum_measures + fmeasures.get(i);
		}
		float mean_fMeasure = sum_measures / fmeasures.size();
	}

	//NB: pathConFile è il path fino al file COMPRESO, quindi anche .txt
	//struttura dell'output:
	//Map<Ancora,Map<WebPage,List<Valori>>>
	public static Map<String,Map<Tuple2<String,String>,List<String>>> parseFile_verticale(String pathConFile) throws FileNotFoundException, IOException {
		File groundTruthFile = new File(pathConFile);
		String file = IOUtils.toString(new FileReader(groundTruthFile));
		//TODO testa
		String[] persone = file.split("\n\n");
		Map<String,Map<Tuple2<String,String>,List<String>>> ancora2webPage2valori = new HashMap<>();
		for (int i=0; i<persone.length; i++) {
			//ogni persona contiene un elenco di dati, separati da un \n
			//il primo elemento è ancora e l'id della webpage che bisogna prendere
			String personaCorrente = persone[i];
			String[] dati_persona = personaCorrente.split("\n");
			String ancora_idWebPage = dati_persona[0];
			String ancora = ancora_idWebPage.split(",")[0];
			String idDominio = ancora_idWebPage.split(",")[1];
			String idWebPage = ancora_idWebPage.split(",")[2];
			List<String> valori = new ArrayList<>();
			for (int j=1; j<dati_persona.length; j++) {
				String valore = dati_persona[j].split(",")[0];
				valori.add(valore);
			}
			//cerco se già è stata creata l'ancora
			Map<Tuple2<String,String>,List<String>> dominio_webPage2valori = ancora2webPage2valori.get(ancora);
			if (dominio_webPage2valori == null) {
				dominio_webPage2valori = new HashMap<>();
			}
			dominio_webPage2valori.put(new Tuple2<>(idDominio,idWebPage), valori);
			ancora2webPage2valori.put(ancora, dominio_webPage2valori);
		}
		return ancora2webPage2valori;
	}
	
	//NB: pathConFile è il path fino al file COMPRESO, quindi anche .txt
		//struttura dell'output:
		//Map<Ancora,Map<Tupla<Dominio,WebPage>,List<Tupla<Id,Valori>>>>
		public static Map<String,Map<Tuple2<String,String>,List<Tuple2<String,String>>>> parseFile_orizzontale
		(String pathConFile) throws FileNotFoundException, IOException {
			File groundTruthFile = new File(pathConFile);
			String file = IOUtils.toString(new FileReader(groundTruthFile));
			String[] persone = file.split("\n\n");
			Map<String,Map<Tuple2<String,String>,List<Tuple2<String,String>>>> ancora2webPage2valori = new HashMap<>();
			for (int i=0; i<persone.length; i++) {
				//ogni persona contiene un elenco di dati, separati da un \n
				//il primo elemento è ancora e l'id della webpage che bisogna prendere
				String personaCorrente = persone[i];
				String[] dati_persona = personaCorrente.split("\n");
				String ancora_idWebPage = dati_persona[0];
				String ancora = ancora_idWebPage.split(",")[0];
				String idDominio = ancora_idWebPage.split(",")[1];
				String idWebPage = ancora_idWebPage.split(",")[2];
				List<Tuple2<String,String>> id_valori = new ArrayList<>();
				for (int j=1; j<dati_persona.length; j++) {
					String valore = dati_persona[j].split(",")[0];
					String id = dati_persona[j].split(",")[1];
					id_valori.add(new Tuple2<>(id,valore));
				}
				//cerco se già è stata creata l'ancora
				Map<Tuple2<String,String>,List<Tuple2<String,String>>> dominio_webPage2valori = ancora2webPage2valori.get(ancora);
				if (dominio_webPage2valori == null) {
					dominio_webPage2valori = new HashMap<>();
				}
				dominio_webPage2valori.put(new Tuple2<>(idDominio,idWebPage), id_valori);
				ancora2webPage2valori.put(ancora, dominio_webPage2valori);
			}
			return ancora2webPage2valori;
		}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Map<String,Map<Tuple2<String,String>,List<Tuple2<String,String>>>> mappa = 
				parseFile_orizzontale("inputdiprova.txt");
		Iterator<String> it = mappa.keySet().iterator();
		while (it.hasNext()) {
			String ancora = it.next();
			System.out.println("persona: "+ancora);
			Map<Tuple2<String,String>,List<Tuple2<String,String>>> dom_wp2valori = mappa.get(ancora);
			Iterator<Tuple2<String,String>> d_wpIt = dom_wp2valori.keySet().iterator();
			while (d_wpIt.hasNext()) {
				Tuple2<String,String> dom_wp = d_wpIt.next();
				System.out.println("--dominio: "+dom_wp._1());
				System.out.println("--webpage: "+dom_wp._2());
				System.out.println("Lista: "+dom_wp2valori.get(dom_wp));
			}
		}
	}

}
