package main;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NodeList;

import database.MongoFacade;
import database.WebPageSelector;
import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.Source;
import model.WebPage;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;
import xpath.utils.XpathApplier;

/* questo main serve per un task esplorativo in cui cerchiamo di capire se vogliamo dei Matching
 * numerosi o dei cluster di Matching (non avremo un singolo matching sdoppiato)*/
public class PairMatchingMaker {
//	static String path = "testGenericXpath/persone/";
	// refactoring, va parametrizzato. per ora individua a che servono
//	static int n1 = 1;
//	static int n2 = 2;
//	static int n3 = 3;
//	static int n4 = 4;
//	static int parN1 = 1;
//	static int parN2 = 2;
	// fai una classe di configurazione
	//	static double parameterTextFusion = -1;
	static List<String> idSorgenti = new ArrayList<>();

	public static Map<Tuple2<Integer,Integer>,Set<PairMatching>> getMainMatchings() throws Exception {

		//fase di raccolta dei matching
		// migliora qui!!
		// ho bisogno di un repository per le pagine pulite... o insomma un repository per questo oggetto
		//per una questione di efficienza, memorizzo qui i web page documenti
		//		Map<Tuple2<Integer,Integer>,WebPageDocument> personDomain2document = new HashMap<>();
		//		for(int p=1;p<=7;p++){
		//			for(int j=1;j<=5;j++) {
		//				String currentFolder = path+"p"+p+"/";
		//				String dPath = currentFolder+"orig"+j+".html";
		//				File d = new File(dPath);
		//				if (d.exists()) {
		//					WebPageDocument w = new WebPageDocument(
		//							new File(path+"p"+p+"/"+"orig"+j+".html"), 
		//							j, path+"p"+p+"/", 
		//							parameterTextFusion, j);
		//					personDomain2document.put(new Tuple2<>(p,j), w);
		//				}
		//			}
		//		}

		//al posto di questo, devi recuperare un insieme di pagine web dal dataset
		//-scegli un insieme di domini (per ora i soliti 5)
		//-trovi le persone con un'ancora unica e che sono presenti in almeno 2 domini
		//-usi questo insieme di pagine, crei un repository di pagine web


		//seleziona i domini
		inizializzaLista();
		//primo modulo: raccolta di pagine con ancore uniche dai domini scelti
		//magari poi puoi vedere se anche con la source sfigata ci sono abbastanza pagine
		Map<Source,List<WebPage>> domain2pages = selectDomainsAndGetPagesWithUniqueName();

		//secondo modulo: filtri e selezioni le pagine di persone che compaiono in almeno 2 domini
		//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
//		Map<String,Set<WebPageDocument>> ancore2pagesWUNIMTOS = WebPageSelector.getPagesWUNIMTOS(domain2pages);
		//TODO prima testa la raccolta di queste pagine

		//TODO il prossimo passo funziona solo se ci sono almeno 2 persone per dominio.
		//durante il test assicurati che sia così

		//devo iterare in qualche modo...
		//candidati: due persone con gli stessi domini
//		for (int i=0;i<idSorgenti.size();i++) {
//			for (int j=i+1;j<idSorgenti.size();j++) {
//				String domain1 = idSorgenti.get(i);
//				String domain2 = idSorgenti.get(j);
//				//scorro le ancore
//				List<String> setAncore = new ArrayList<>(ancore2pagesWUNIMTOS.keySet());
//				for (int p1=0;p1<setAncore.size();p1++) {
//					for(int p2=p1+1;p2<setAncore.size();p2++) {
//						String first_person = setAncore.get(p1);
//						String second_person = setAncore.get(p2);
//						Set<WebPageDocument> documentsP1 = ancore2pagesWUNIMTOS.get(first_person);
//						Set<WebPageDocument> documentsP2 = ancore2pagesWUNIMTOS.get(second_person);
//						//voglio i documenti con le sorgenti domain1 e domain2
//						WebPageDocument wpd_p1_d1 = getWPD(documentsP1, domain1);
//						WebPageDocument wpd_p1_d2 = getWPD(documentsP1, domain2);
//						WebPageDocument wpd_p2_d1 = getWPD(documentsP2, domain1);
//						WebPageDocument wpd_p2_d2 = getWPD(documentsP2, domain2);
//						if (wpd_p1_d1!=null && wpd_p1_d2!=null
//								&& wpd_p2_d1!=null && wpd_p2_d2!=null) {
//							DomainsWrapper_pairMatching.getSegmentsFrom_server(
//									wpd_p1_d1, wpd_p1_d2, first_person,
//									wpd_p2_d1, wpd_p2_d2, second_person);
//						}
//					}
//				}
//			}
//		}

		//più efficienza con la creazione delle webpage
//		for(int k=1;k<=4;k++) {
//			for (int k2=k+1;k2<=5;k2++) {
//				int domain1 = k;
//				int domain2 = k2;
//
//				for (int i=1;i<=7;i++) {
//					for (int j=(i+1);j<=7;j++) {
//
//						System.out.println("***"+i+" "+j);
//
//						String d1Folder = path+"p"+i+"/";
//						String d2Folder = path+"p"+i+"/";
//						String d3Folder = path+"p"+j+"/";
//						String d4Folder = path+"p"+j+"/";
//
//						String d1Path = d1Folder+"orig"+domain1+".html";
//						String d2Path = d2Folder+"orig"+domain2+".html";
//
//						String d3Path = d3Folder+"orig"+domain1+".html";
//						String d4Path = d4Folder+"orig"+domain2+".html";
//
//						System.out.println("d1: "+d1Path);
//						System.out.println("d2: "+d2Path);
//						System.out.println("d3: "+d3Path);
//						System.out.println("d4: "+d4Path);
//
//
//						File d1 = new File(d1Path);
//						File d2 = new File(d2Path);
//
//						File d3 = new File(d3Path);
//						File d4 = new File(d4Path);
//
//						if (d1.exists() && d2.exists() && d3.exists() && d4.exists()) {
//							System.out.println("Trovati documenti");
//
//							WebPageDocument firstDocument = new WebPageDocument(d1, domain1, d1Folder, 
//									parameterTextFusion, domain1);
//							WebPageDocument secondDocument = new WebPageDocument(d2, domain2, d2Folder, 
//									parameterTextFusion, domain2);
//							WebPageDocument thirdDocument = new WebPageDocument(d3, domain1, d3Folder, 
//									parameterTextFusion, domain1);
//							WebPageDocument fourthDocument = new WebPageDocument(d4, domain2, d4Folder, 
//									parameterTextFusion, domain2);
//
//							DomainsWrapper_pairMatching.getSegmentsFrom(firstDocument, secondDocument, 
//									thirdDocument, fourthDocument, d1Folder, d3Folder, domain1, domain2, domain1, domain2);
//						}
//					}
//				}
//			}
//		}

		//calcolo, per ogni xpath di ogni coppia, quanti domini riesce a raggiungere
		for (int i=1;i<=4;i++) {
			for (int j=(i+1);j<=5;j++) {
				//così scorro i repository senza problemi
				PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
				PairMatchingRepository currentRepository = pmr.getRepository(i, j);
				//ora mi scorro tutti le coppie del repository corrente
				Iterator<PairMatching> matchingsIt = currentRepository.getMatchings().iterator();
				while (matchingsIt.hasNext()) {
					PairMatching currentMatching = matchingsIt.next();
					Xpath firstElement = currentMatching.getXpath1();
					int domain_firstElement = i;
					List<Integer> dominiEsplorati_firstElement = new ArrayList<>();
					dominiEsplorati_firstElement.add(i);
					dominiEsplorati_firstElement.add(j);
					dominiEsplorati_firstElement = 
							calculateReachableDomains(firstElement, domain_firstElement, dominiEsplorati_firstElement);

					currentMatching.setDominiRaggiungibili(firstElement, dominiEsplorati_firstElement);

					Xpath secondElement = currentMatching.getXpath2();
					int domain_secondElement = j;
					List<Integer> dominiEsplorati_secondElement = new ArrayList<>();
					dominiEsplorati_secondElement.add(i);
					dominiEsplorati_secondElement.add(j);
					dominiEsplorati_secondElement = 
							calculateReachableDomains(secondElement, domain_secondElement, dominiEsplorati_secondElement);

					currentMatching.setDominiRaggiungibili(secondElement, dominiEsplorati_secondElement);
				}
			}
		}

		//stampiamo i collegamenti
//		XpathApplier xapplier = XpathApplier.getInstance();
		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();

		//stampo il contenuto dei repository
		// cancellare questo pezzo di codice quando non serve più la stampa
		//scorro i domini
//		for(int k=1;k<=4;k++) {
//			for (int k2=k+1;k2<=5;k2++) {
//				int domain1 = k;
//				int domain2 = k2;
//				PrintWriter testPrinter = new PrintWriter(path+"pairMatchings"+domain1+"_"+domain2+".csv", "UTF-8");
//
//				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
//
//				//scorro le persone
//				for (int p=1;p<=7;p++) {
//
//					WebPageDocument w1 = personDomain2document.get(new Tuple2<>(p,domain1));
//					WebPageDocument w2 = personDomain2document.get(new Tuple2<>(p,domain2));
//
//					if (w1 != null && w2 != null) {
//						Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();
//						Iterator<PairMatching> matchingIt = matchings2votes.keySet().iterator();
//						while (matchingIt.hasNext()) {
//							PairMatching currentPair = matchingIt.next();
//							//**persona
//							testPrinter.print("Persona "+p+";");
//							//**xpath1
//							testPrinter.print(currentPair.getXpath1().getXpath()+";");
//							//**contenuto del segmento identificato applicando xpath1 al documento 1
//							NodeList nl1 = xapplier.getNodes(currentPair.getXpath1().getXpath(), 
//									w1.getDocument_jsoup());
//							if (nl1.getLength() != 0) {
//								testPrinter.print(nl1.item(0).getTextContent().replaceAll(";", "")
//										.replaceAll("\n", "")+";");
//							}
//							else	{ //l'xpath non ha restituito nessun segmento
//								testPrinter.print("--;");
//							}
//							//**domini raggiungibili da xpath1
//							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath1()+" ("+
//									currentPair.getDominiRaggiungibiliDaXpath1().size()+")"+";");
//							//**voto
//							//							testPrinter.print(matchings2votes.get(currentPair)+",");
//							testPrinter.print(new DecimalFormat("#.##").format(matchings2votes.get(currentPair))
//									+";");
//							//**domini raggiungibili da xpath2
//							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath2()+" ("+
//									currentPair.getDominiRaggiungibiliDaXpath2().size()+")"+";");
//							//**contenuto del segmento* identificato applicando xpath2 al documento 2
//							NodeList nl2 = xapplier.getNodes(currentPair.getXpath2().getXpath(),
//									w2.getDocument_jsoup());
//							if (nl2.getLength() != 0) {
//								testPrinter.print(nl2.item(0).getTextContent().replaceAll(";", "")
//										.replaceAll("\n", "")+";");
//							}
//							else	{ //l'xpath non ha restituito nessun segmento
//								testPrinter.print("--;");
//							}
//							//**xpath2
//							testPrinter.println(currentPair.getXpath2().getXpath());
//
//						} //fine while matchings
//					} //fine if d1 e d2 exist
//					testPrinter.println();
//				} //fine for persone
//				testPrinter.close();
//			}
//
//		} //fine scorrimento domini


		//ora elimino dalle repository le coppie che
		//-non hanno il voto massimo
		//-hanno il voto massimo ma una bassa raggiungibilità degli altri domini (si guarda l'elemento non corrente della coppia)
		//scorro le repository
		Map<Tuple2<Integer,Integer>,Set<PairMatching>> finalMap = new HashMap<>();
		for(int k=1;k<=4;k++) {
			for (int k2=k+1;k2<=5;k2++) {
				int domain1 = k;
				int domain2 = k2;

				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
				Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();
				Iterator<PairMatching> matchingIt = matchings2votes.keySet().iterator();
				//liste che servono per efficienza, per non controllare due volte un elemento
				List<Xpath> checkedElements1 = new ArrayList<>();
				List<Xpath> checkedElements2 = new ArrayList<>();
				//lista finale che contiene tutti i massimi elementi
				Set<PairMatching> finalListOfRepository = new HashSet<>();
				//scorro gli elementi dei repository: le coppie
				while (matchingIt.hasNext()) {
					PairMatching currentPair = matchingIt.next();
					PairMatching bestPair1 = null;
					PairMatching bestPair2 = null;
					if (!checkedElements1.contains(currentPair.getXpath1())) {
						//per ogni coppia prendo il primo elemento, poi ripeti col secondo
						bestPair1 = createListWithMaxPairs(currentPair.getXpath1(),
								currentRepository, false, domain1);
						checkedElements1.add(currentPair.getXpath1());
					}

					if (!checkedElements2.contains(currentPair.getXpath2())) {
						//stesso procedimento, ma con il secondo elemento della coppia
						bestPair2 = createListWithMaxPairs(currentPair.getXpath2(),
								currentRepository, true, domain2);
						checkedElements2.add(currentPair.getXpath2());
					}

					if (bestPair1 != null)
						finalListOfRepository.add(bestPair1);
					if (bestPair2 != null)
						finalListOfRepository.add(bestPair2);
				}
				Tuple2<Integer,Integer> domains = new Tuple2<>(domain1,domain2);
				finalMap.put(domains, finalListOfRepository);
			}
		}

		return finalMap;
	} //fine main

	public static List<Integer> calculateReachableDomains(Xpath currentElement,
			int currentElementDomain, List<Integer> reachedDomains) {
		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		List<Integer> maxReachedDomains = new ArrayList<>(reachedDomains);
		//sappiamo che i domini sono 5, quindi conosciamo l'upper bound di questo for
		//TODO in generale, dovresti inventarti qualcos'altro
		//TODO
		//TODO
		//TODO QUI CAMBIAAAAAA
		for (int dom=1; dom<=5; dom++) {
			if (!reachedDomains.contains(dom)) {
				PairMatchingRepository currentRepository;
				boolean isDomAtRight;
				if (currentElementDomain < dom) {
					currentRepository = pmr.getRepository(currentElementDomain, dom);
					isDomAtRight = true;
				}
				else {
					currentRepository = pmr.getRepository(dom, currentElementDomain);
					isDomAtRight = false;
				}
				List<PairMatching> pairsWithCurrentElement = currentRepository
						.getPairsWith(currentElement, !isDomAtRight);
				for (int i=0;i<pairsWithCurrentElement.size();i++) {
					List<Integer> currentReachedDomains = new ArrayList<>(reachedDomains);
					currentReachedDomains.add(dom);
					if (isDomAtRight) {
						currentReachedDomains = 
								calculateReachableDomains(pairsWithCurrentElement.get(i).getXpath2(),
										dom, currentReachedDomains);
					}
					else {
						currentReachedDomains = 
								calculateReachableDomains(pairsWithCurrentElement.get(i).getXpath1(),
										dom, currentReachedDomains);
					}
					if (currentReachedDomains.size() > maxReachedDomains.size()) {
						maxReachedDomains = currentReachedDomains;
					}
				} //end for
			} //end if(!dominiEsplorati.contains(dom))
		} //end for
		return maxReachedDomains;
	}


	public static PairMatching createListWithMaxPairs(Xpath currentPath, 
			PairMatchingRepository currentRepository, boolean isElementRight, int domain) {

		Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();
		//prendo tutte le coppie con quell'elemento
		List<PairMatching> pairListWithCurrentPair = currentRepository
				.getPairsWith(currentPath, isElementRight);
		float maxVoto = 0;
		int maxReachability = 0;
		//scorro le coppie e individuo il voto massimo
		for (int i=0;i<pairListWithCurrentPair.size();i++) {
			PairMatching p = pairListWithCurrentPair.get(i);
			float voto = matchings2votes.get(p);
			if (voto > maxVoto) {
				maxVoto = voto;
				if (!isElementRight) {
					if (p.getDominiRaggiungibiliDaXpath2().size() > maxReachability)
						maxReachability = p.getDominiRaggiungibiliDaXpath2().size();
				}
				else {
					if (p.getDominiRaggiungibiliDaXpath1().size() > maxReachability)
						maxReachability = p.getDominiRaggiungibiliDaXpath2().size();
				}
			}
		}
		int maxVotoApprossimato = (int) maxVoto;
		//prendo dalla lista gli elementi con voto (arrotondato all'unità)
		//e la raggiungibilità massima
		for (int i=0;i<pairListWithCurrentPair.size();i++) {
			PairMatching p = pairListWithCurrentPair.get(i);
			float voto = matchings2votes.get(p);
			int votoApprossimato = (int) voto;
			if (votoApprossimato == maxVotoApprossimato) {
				if (!isElementRight) {
					if(p.getDominiRaggiungibiliDaXpath2().size() == maxReachability) {
						return p;
					}
				}
				else {
					if(p.getDominiRaggiungibiliDaXpath1().size() == maxReachability) {
						return p;
					}
				}
			}
		}
		return null;
	}

	public static Map<Source,List<WebPage>> selectDomainsAndGetPagesWithUniqueName() {
		MongoFacade facade = new MongoFacade("web_search_pages");
		Map<Source,List<WebPage>> domain2pages = new HashMap<>();
		for (int i=0;i<idSorgenti.size();i++) {
			Source currentSource = facade.getSourceWithId(idSorgenti.get(i));
			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
			domain2pages.put(currentSource, pagesOfCurrentSource);
		}
		//		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
		//		List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		//		domain2pages.put(currentSource, pagesOfCurrentSource);
		//		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1d0");
		//		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		//		domain2pages.put(currentSource, pagesOfCurrentSource);
		//		currentSource = facade.getSourceWithId("575067b33387e31f516face0");
		//		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		//		domain2pages.put(currentSource, pagesOfCurrentSource);
		//		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		//		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		//		domain2pages.put(currentSource, pagesOfCurrentSource);
		//		currentSource = facade.getSourceWithId("5750678a3387e31f516fa185");
		//		pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
		//		domain2pages.put(currentSource, pagesOfCurrentSource);
		return domain2pages;
	}

	public static void inizializzaLista() {
		idSorgenti.add("5750678b3387e31f516fa1c7");
		idSorgenti.add("5750678b3387e31f516fa1d0");
		idSorgenti.add("5750678b3387e31f516fa1ca");
		idSorgenti.add("5750678b3387e31f516fa1cd");
		idSorgenti.add("5750678a3387e31f516fa185");
	}

	//restiituisce il documento con il dominio richiesto, o null se non presente
	public static WebPageDocument getWPD(Set<WebPageDocument> documents, String idDomain) {
		Iterator<WebPageDocument> docIt = documents.iterator();
		while (docIt.hasNext()) {
			WebPageDocument currentDoc = docIt.next();
			if (currentDoc.getIdDomain().equals(idDomain)) {
				return currentDoc;
			}
		}
		return null;
	}
}

