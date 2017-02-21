//package main;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import database.MongoFacade;
//import database.WebPageSelector;
//import model.DomRepToClean;
//import model.Source;
//import model.WebPage;
//import model.WebPageDocument;
//import scala.Tuple2;
//
//public class Test {
//
//	static List<String> idSorgenti = new ArrayList<>();
//
//	public static void main(String[] args) throws Exception {
//		//al posto di questo, devi recuperare un insieme di pagine web dal dataset
//		//-scegli un insieme di domini (per ora i soliti 5)
//		//-trovi le persone con un'ancora unica e che sono presenti in almeno 2 domini
//		//-usi questo insieme di pagine, crei un repository di pagine web
//
//
//		//seleziona i domini
//		inizializzaLista();
//		//primo modulo: raccolta di pagine con ancore uniche dai domini scelti
//		//magari poi puoi vedere se anche con la source sfigata ci sono abbastanza pagine
//		Map<String,List<WebPage>> domain2pages = selectDomainsAndGetPagesWithUniqueName();
//
//		//secondo modulo: filtri e selezioni le pagine di persone che compaiono in almeno 2 domini
//		//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
//		Map<String,Set<Tuple2<String,WebPage>>> ancore2pagesWUNIMTOS = 
//				WebPageSelector.getPagesWUNIMTOS_new(domain2pages);
//
//		//contiene la lista di persone da blacklistare
//		//ancora_dominio1_dominio2
//		Map<String,List<Tuple2<String,String>>> blacklist_persone = new HashMap<>();
//
//		System.out.println("______ora apprendiamo le xpath");
//		
//		//il prossimo passo funziona solo se ci sono almeno 2 persone per dominio.
//		for (int i=0;i<idSorgenti.size();i++) {
//			for (int j=i+1;j<idSorgenti.size();j++) {
//				String domain1 = idSorgenti.get(i);
//				String domain2 = idSorgenti.get(j);
//				System.out.println("d1: "+domain1.substring(domain1.length()-4, domain1.length()));
//				System.out.println("d2: "+domain2.substring(domain2.length()-4, domain2.length()));
//				//scorro le ancore
//				List<String> listAncore = new ArrayList<>(ancore2pagesWUNIMTOS.keySet());
//				for (int p1=0;p1<listAncore.size();p1++) {
//					String first_person = listAncore.get(p1);
//					//controllo che la prima persona presa non sia in blacklist
//					if (!mapContains(blacklist_persone,first_person,domain1,domain2)) {
//						System.out.println("prima persona non in bl");
//						boolean firstPersonBanned = false;
//						for(int p2=p1+1;p2<listAncore.size() && !firstPersonBanned;p2++) {
//							String second_person = listAncore.get(p2);
//							System.out.println("p1: "+first_person);
//							System.out.println("p2: "+second_person);
//							//controllo che la seconda persona presa non sia in blacklist
//							if (!mapContains(blacklist_persone,second_person,domain1,domain2)) {
//								System.out.println("seconda persona non in bl");
//								Set<Tuple2<String,WebPage>> documentsP1 = ancore2pagesWUNIMTOS.get(first_person);
//								Set<Tuple2<String,WebPage>> documentsP2 = ancore2pagesWUNIMTOS.get(second_person);
//								//voglio i documenti con le sorgenti domain1 e domain2
//								WebPage wp_p1_d1 = getWP(documentsP1, domain1);
//								WebPage wp_p1_d2 = getWP(documentsP1, domain2);
//								WebPage wp_p2_d1 = getWP(documentsP2, domain1);
//								WebPage wp_p2_d2 = getWP(documentsP2, domain2);
//								if (wp_p1_d1!=null && wp_p1_d2!=null
//										&& wp_p2_d1!=null && wp_p2_d2!=null) {
//									//creo i documenti
//									WebPageDocument wpd_p1_d1 = new WebPageDocument(wp_p1_d1, domain1);
//									WebPageDocument wpd_p1_d2 = new WebPageDocument(wp_p1_d2, domain2);
//									WebPageDocument wpd_p2_d1 = new WebPageDocument(wp_p2_d1, domain1);
//									WebPageDocument wpd_p2_d2 = new WebPageDocument(wp_p2_d2, domain2);
//									//nota: il booleano finale si tratta di id_found, che dice se
//									//all'interno del processo è stata identificata una coppia di
//									//xpath identificative, permettendoci di capire se
//									//stiamo usando 2 coppie di pagine che parlano ciascuna della stessa persona, o no
//									int esito = DomainsWrapper_pairMatching.getSegmentsFrom_server(
//											wpd_p1_d1, wpd_p1_d2, first_person,
//											wpd_p2_d1, wpd_p2_d2, second_person, false);
//									
//									System.out.println("esito: "+esito);
//
//									if (esito ==1) {
//										//devo blacklistare la prima persona
//										Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
//										List<Tuple2<String,String>> listBannedDomainPairs =
//												blacklist_persone.get(first_person);
//										if (listBannedDomainPairs == null) {
//											listBannedDomainPairs = new ArrayList<>();
//										}
//										listBannedDomainPairs.add(bannedDomainPairs);
//										blacklist_persone.put(first_person, listBannedDomainPairs);
//										firstPersonBanned = true;
//									}
//									if (esito ==2) {
//										//devo blacklistare la seconda persona
//										Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
//										List<Tuple2<String,String>> listBannedDomainPairs =
//												blacklist_persone.get(second_person);
//										if (listBannedDomainPairs == null) {
//											listBannedDomainPairs = new ArrayList<>();
//										}
//										listBannedDomainPairs.add(bannedDomainPairs);
//										blacklist_persone.put(second_person, listBannedDomainPairs);
//									}
//									//TODO dopo aver creato i matching, puoi liberare dalla memoria questi documenti
//									//se serve
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//
//	public static Map<String,List<WebPage>> selectDomainsAndGetPagesWithUniqueName() {
//		MongoFacade facade = new MongoFacade("web_search_pages");
//		Map<String,List<WebPage>> domain2pages = new HashMap<>();
//		DomRepToClean drtc = DomRepToClean.getInstance();
//		for (int i=0;i<idSorgenti.size();i++) {
//			Source currentSource = facade.getSourceWithId(idSorgenti.get(i));
//			drtc.addDomain(currentSource);
//			SourceRep.addSource(currentSource);
//			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
//			domain2pages.put(currentSource.getId().toString(), pagesOfCurrentSource);
//		}
//		return domain2pages;
//	}
//
//	public static void inizializzaLista() {
//		idSorgenti.add("5750678b3387e31f516fa1c7");
//		idSorgenti.add("5750678b3387e31f516fa1d0");
//		idSorgenti.add("5750678b3387e31f516fa1ca");
//		idSorgenti.add("5750678b3387e31f516fa1cd");
//		idSorgenti.add("5750678a3387e31f516fa185");
//	}
//
//	//restituisce il documento con il dominio richiesto, o null se non presente
//	public static WebPage getWP(Set<Tuple2<String,WebPage>> documents, String idDomain) {
//		Iterator<Tuple2<String,WebPage>> pagesIt = documents.iterator();
//		while (pagesIt.hasNext()) {
//			Tuple2<String,WebPage> currentTuple = pagesIt.next();
//			if (currentTuple._1().equals(idDomain)) {
//				return currentTuple._2();
//			}
//		}
//		return null;
//	}
//
//	public static boolean mapContains(Map<String,List<Tuple2<String,String>>> blacklist_persone,
//			String second_person, String domain1, String domain2) {
//		List<Tuple2<String,String>> domains = blacklist_persone.get(second_person);
//		if (domains != null) {
//			for (int i=0;i<domains.size();i++) {
//				Tuple2<String,String> currentDomainPair = domains.get(i);
//				if (currentDomainPair._1().equals(domain1) && currentDomainPair._2().equals(domain2))
//					return true;
//				if (currentDomainPair._1().equals(domain2) && currentDomainPair._2().equals(domain1))
//					return true;
//			}
//		}
//		return false;
//	}
//
//}
