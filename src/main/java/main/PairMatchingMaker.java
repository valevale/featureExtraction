package main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import database.MongoFacade;
import database.WebPageSelector;
import model.DomRepToClean;
import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.Source;
import model.WebPage;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;

/* questo main serve per un task esplorativo in cui cerchiamo di capire se vogliamo dei Matching
 * numerosi o dei cluster di Matching (non avremo un singolo matching sdoppiato)*/
public class PairMatchingMaker {

	//TODO prossimo: 5 persona; 10 dominio; 10 successi
	//e 5 domini (o 6 se vuoi metterci quello piccolino)
	//prima era 10 e 5 per pb
	//ora devi fare full mesh aenza controllo
	static int successiPersona = 5;
	static int successiDominio = 30;
	//TODO
//	static int sufficientiSuccessi = 10;
	static int sufficientiSuccessi_pb = 20;
	//	static List<String> idSorgenti = new ArrayList<>();
	static PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();

	public static Map<Tuple2<String,String>,Set<PairMatching>> getMainMatchings() throws Exception {
		//		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		//	public static void getMainMatchings() throws Exception {
		//al posto di questo, devi recuperare un insieme di pagine web dal dataset
		//-scegli un insieme di domini (per ora i soliti 5)
		//-trovi le persone con un'ancora unica e che sono presenti in almeno 2 domini
		//-usi questo insieme di pagine, crei un repository di pagine web


		//seleziona i domini
		//		inizializzaLista();
		//primo modulo: raccolta di pagine con ancore uniche dai domini scelti
		//magari poi puoi vedere se anche con la source sfigata ci sono abbastanza pagine
		Map<String,List<WebPage>> domain2pages = selectDomainsAndGetPagesWithUniqueName();

		//secondo modulo: filtri e selezioni le pagine di persone che compaiono in almeno 2 domini
		//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
		Map<String,Set<Tuple2<String,WebPage>>> ancore2pagesWUNIMTOS = 
				WebPageSelector.getPagesWUNIMTOS_new(domain2pages);

		//contiene la lista di persone da blacklistare
		//ancora_dominio1_dominio2
		Map<String,List<Tuple2<String,String>>> blacklist_persone = new HashMap<>();
		System.out.println("dataset raccolto, ora apprendiamo le xpath");

		//il prossimo passo funziona solo se ci sono almeno 2 persone per dominio.
		boolean fine_apprendimento = false;
		Map<String,Integer> dominio2successi = new HashMap<>();
		inizializzaMappaSuccessi(dominio2successi);
		System.out.println("mappa dei successi inizializzata");
		for (int i=0;i<SourceInput.getSorgenti().size() && !fine_apprendimento;i++) {
			for (int j=i+1;j<SourceInput.getSorgenti().size() && !fine_apprendimento;j++) {
				String domain1 = SourceInput.getSorgenti().get(i);
				String domain2 = SourceInput.getSorgenti().get(j);
				System.out.println("NUOVO d1:" + domain1);
				System.out.println("NUOVO d2:" + domain2);
				//controllo: se quella coppia ha già 5 successi, passa a un'altra
				if (!sufficientiSuccessi(dominio2successi,domain1,domain2)) {
					System.out.println("non ho sufficienti successi per d1 e d2");
					//scorro le ancore
					List<String> listAncore = new ArrayList<>(ancore2pagesWUNIMTOS.keySet());
					for (int p1=0;p1<listAncore.size() && 
							!sufficientiSuccessi(dominio2successi,domain1,domain2)
							&& !fine_apprendimento;p1++) {
						String first_person = listAncore.get(p1);
						int successiPrimaPersona = 0;
						int cont = 1;
						//controllo che la prima persona presa non sia in blacklist
						if (!mapContains(blacklist_persone,first_person,domain1,domain2)) {
							boolean firstPersonBanned = false;

							for(int p2=p1+1;p2<listAncore.size() && !firstPersonBanned
									&& !sufficientiSuccessi(dominio2successi,domain1,domain2) 
									&& !fine_apprendimento;p2++) {
								String second_person = listAncore.get(p2);
								int successiSecondaPersona = 0;
								//controllo che la seconda persona presa non sia in blacklist
								if (!mapContains(blacklist_persone,second_person,domain1,domain2)) {
									Set<Tuple2<String,WebPage>> documentsP1 = ancore2pagesWUNIMTOS.get(first_person);
									Set<Tuple2<String,WebPage>> documentsP2 = ancore2pagesWUNIMTOS.get(second_person);
									//voglio i documenti con le sorgenti domain1 e domain2
									WebPage wp_p1_d1 = getWP(documentsP1, domain1);
									WebPage wp_p1_d2 = getWP(documentsP1, domain2);
									WebPage wp_p2_d1 = getWP(documentsP2, domain1);
									WebPage wp_p2_d2 = getWP(documentsP2, domain2);
									if (wp_p1_d1!=null && wp_p1_d2!=null
											&& wp_p2_d1!=null && wp_p2_d2!=null) {
										System.out.println("d1:" + domain1);
										System.out.println("d2:" + domain2);
										System.out.println("p1: "+first_person);
										System.out.println("p2: "+second_person);
										//creo i documenti
										WebPageDocument wpd_p1_d1 = new WebPageDocument(wp_p1_d1, domain1);
										WebPageDocument wpd_p1_d2 = new WebPageDocument(wp_p1_d2, domain2);
										WebPageDocument wpd_p2_d1 = new WebPageDocument(wp_p2_d1, domain1);
										WebPageDocument wpd_p2_d2 = new WebPageDocument(wp_p2_d2, domain2);
										//nota: il booleano finale si tratta di id_found, che dice se
										//all'interno del processo è stata identificata una coppia di
										//xpath identificative, permettendoci di capire se
										//stiamo usando 2 coppie di pagine che parlano ciascuna della stessa persona, o no
										int esito = DomainsWrapper_pairMatching.getSegmentsFrom_server(
												wpd_p1_d1, wpd_p1_d2, first_person,
												wpd_p2_d1, wpd_p2_d2, second_person, false);
										cont++;
										System.out.println("ESITO: "+esito);
										if (esito ==1 || cont>=10) {
											//devo blacklistare la prima persona
											Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
											List<Tuple2<String,String>> listBannedDomainPairs =
													blacklist_persone.get(first_person);
											if (listBannedDomainPairs == null) {
												listBannedDomainPairs = new ArrayList<>();
											}
											listBannedDomainPairs.add(bannedDomainPairs);
											blacklist_persone.put(first_person, listBannedDomainPairs);
											firstPersonBanned = true;
											//do successo alla seconda persona
											successiSecondaPersona++;
										}
										if (esito ==2) {
											//devo blacklistare la seconda persona
											Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
											List<Tuple2<String,String>> listBannedDomainPairs =
													blacklist_persone.get(second_person);
											if (listBannedDomainPairs == null) {
												listBannedDomainPairs = new ArrayList<>();
											}
											listBannedDomainPairs.add(bannedDomainPairs);
											blacklist_persone.put(second_person, listBannedDomainPairs);
											//do successo alla prima persona
											successiPrimaPersona++;
										}

										if (esito == 0) {
											//un buon quadrato è stato trovato.
											// segnarsi un +1 per ogni dominio
											//TODO
//											int successiDom1 = dominio2successi.get(domain1);
//											successiDom1++;
//											dominio2successi.put(domain1, successiDom1);
//											int successiDom2 = dominio2successi.get(domain2);
//											successiDom2++;
//											dominio2successi.put(domain2, successiDom2);


											int successiDoms = dominio2successi.get(domain1+"_"+domain2);
											successiDoms++;
											dominio2successi.put(domain1+"_"+domain2, successiDoms);

											// magari limita l'apprendimento con una pagina di
											//una certa persona
											//tipo quando hai avuto 10 successi con questa persona,
											//"blacklista" anche lei
											if (successiPrimaPersona >= successiPersona) {
												System.out.println("sufficienti successi con p1");
												//devo blacklistare la prima persona
												Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
												List<Tuple2<String,String>> listBannedDomainPairs =
														blacklist_persone.get(first_person);
												if (listBannedDomainPairs == null) {
													listBannedDomainPairs = new ArrayList<>();
												}
												listBannedDomainPairs.add(bannedDomainPairs);
												blacklist_persone.put(first_person, listBannedDomainPairs);
												firstPersonBanned = true;
											}

											if (successiSecondaPersona >= successiPersona) {
												System.out.println("sufficienti successi con p2");
												//devo blacklistare la seconda persona
												Tuple2<String,String> bannedDomainPairs = new Tuple2<>(domain1,domain2);
												List<Tuple2<String,String>> listBannedDomainPairs =
														blacklist_persone.get(second_person);
												if (listBannedDomainPairs == null) {
													listBannedDomainPairs = new ArrayList<>();
												}
												listBannedDomainPairs.add(bannedDomainPairs);
												blacklist_persone.put(second_person, listBannedDomainPairs);
											}
											//quando per ogni dominio si arriva a 3
											//puoi uscire dall'apprendimento
											//											if (sufficientiSuccessi(dominio2successi))
											//												fine_apprendimento = true;
										}

										// potremmo fare un contatore per la prima persona,
										//tale che dopo, boh, 10 prove, comunque va via?
									} //fine if documenti esistono
								} //fineif controllo blacklist per seconda persona
							} //fine for per seconda persona
						}//fine  if controllo blacklist per prima persona
					}//fine for per prima persona
				}
			}
		} //fine scorrimento domini


		//calcolo, per ogni xpath di ogni coppia, quanti domini riesce a raggiungere
		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
			for (int j=i+1;j<SourceInput.getSorgenti().size();j++) {
				//così scorro i repository senza problemi
				String domain1 = SourceInput.getSorgenti().get(i);
				String domain2 = SourceInput.getSorgenti().get(j);
				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
				//ora mi scorro tutti le coppie del repository corrente
				if (currentRepository != null) {
					Iterator<PairMatching> matchingsIt = currentRepository.getMatchings().iterator();
					while (matchingsIt.hasNext()) {
						PairMatching currentMatching = matchingsIt.next();
						Xpath firstElement = currentMatching.getXpath1();
						String domain_firstElement = domain1;
						List<String> dominiEsplorati_firstElement = new ArrayList<>();
						dominiEsplorati_firstElement.add(domain1);
						dominiEsplorati_firstElement.add(domain2);
						dominiEsplorati_firstElement = 
								calculateReachableDomains(firstElement, domain_firstElement, dominiEsplorati_firstElement);

						currentMatching.setDominiRaggiungibili(firstElement, dominiEsplorati_firstElement);

						Xpath secondElement = currentMatching.getXpath2();
						String domain_secondElement = domain2;
						List<String> dominiEsplorati_secondElement = new ArrayList<>();
						dominiEsplorati_secondElement.add(domain1);
						dominiEsplorati_secondElement.add(domain2);
						dominiEsplorati_secondElement = 
								calculateReachableDomains(secondElement, domain_secondElement, dominiEsplorati_secondElement);

						currentMatching.setDominiRaggiungibili(secondElement, dominiEsplorati_secondElement);
					}
				}
			}
		}

		//		stampiamo i collegamenti
		//		XpathApplier xapplier = XpathApplier.getInstance();
		//		//		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		//
		//		//stampo il contenuto dei repository
		//		// cancellare questo pezzo di codice quando non serve più la stampa
		//		//scorro i domini
		//		//stampo il contenuto dei repository
		//		// cancellare questo pezzo di codice quando non serve più la stampa
		//		//scorro i domini
		//		for(int k=1;k<=4;k++) {
		//			for (int k2=k+1;k2<=5;k2++) {
		//				int domain1 = k;
		//				int domain2 = k2;
		//				// il path
		//				PrintWriter testPrinter = new PrintWriter("pairMatchings"+domain1+"_"+domain2+".csv", "UTF-8");
		//
		//				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
		//
		//				//scorro le persone
		//				//				for (int p=1;p<=7;p++) {
		//				//
		//				//					WebPageDocument w1 = personDomain2document.get(new Tuple2<>(p,domain1));
		//				//					WebPageDocument w2 = personDomain2document.get(new Tuple2<>(p,domain2));
		//				//
		//				//					if (w1 != null && w2 != null) {
		//				//						Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();
		//				//						Iterator<PairMatching> matchingIt = matchings2votes.keySet().iterator();
		//				//						while (matchingIt.hasNext()) {
		//				//							PairMatching currentPair = matchingIt.next();
		//				//							//**persona
		//				//							testPrinter.print("Persona "+p+";");
		//				//							//**xpath1
		//				//							testPrinter.print(currentPair.getXpath1().getXpath()+";");
		//				//							//**contenuto del segmento identificato applicando xpath1 al documento 1
		//				//							NodeList nl1 = xapplier.getNodes(currentPair.getXpath1().getXpath(), 
		//				//									w1.getDocument_jsoup());
		//				//							if (nl1.getLength() != 0) {
		//				//								testPrinter.print(nl1.item(0).getTextContent().replaceAll(";", "")
		//				//										.replaceAll("\n", "")+";");
		//				//							}
		//				//							else	{ //l'xpath non ha restituito nessun segmento
		//				//								testPrinter.print("--;");
		//				//							}
		//				//							//**domini raggiungibili da xpath1
		//				//							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath1()+" ("+
		//				//									currentPair.getDominiRaggiungibiliDaXpath1().size()+")"+";");
		//				//							//**voto
		//				//							//							testPrinter.print(matchings2votes.get(currentPair)+",");
		//				//							testPrinter.print(new DecimalFormat("#.##").format(matchings2votes.get(currentPair))
		//				//									+";");
		//				//							//**domini raggiungibili da xpath2
		//				//							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath2()+" ("+
		//				//									currentPair.getDominiRaggiungibiliDaXpath2().size()+")"+";");
		//				//							//**contenuto del segmento* identificato applicando xpath2 al documento 2
		//				//							NodeList nl2 = xapplier.getNodes(currentPair.getXpath2().getXpath(),
		//				//									w2.getDocument_jsoup());
		//				//							if (nl2.getLength() != 0) {
		//				//								testPrinter.print(nl2.item(0).getTextContent().replaceAll(";", "")
		//				//										.replaceAll("\n", "")+";");
		//				//							}
		//				//							else	{ //l'xpath non ha restituito nessun segmento
		//				//								testPrinter.print("--;");
		//				//							}
		//				//							//**xpath2
		//				//							testPrinter.println(currentPair.getXpath2().getXpath());
		//				//
		//				//						} //fine while matchings
		//				//					} //fine if d1 e d2 exist
		//				//					testPrinter.println();
		//				//				} //fine for persone
		//				testPrinter.close();
		//			}
		//
		//		} //fine scorrimento domini/		



		//ora elimino dalle repository le coppie che
		//-non hanno il voto massimo
		//-hanno il voto massimo ma una bassa raggiungibilità degli altri domini (si guarda l'elemento non corrente della coppia)
		//scorro le repository
		Map<Tuple2<String,String>,Set<PairMatching>> finalMap = new HashMap<>();

		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
			for (int j=i+1;j<SourceInput.getSorgenti().size();j++) {
				String domain1 = SourceInput.getSorgenti().get(i);
				String domain2 = SourceInput.getSorgenti().get(j);
				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
				if (currentRepository != null) {
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
									currentRepository, false);
							checkedElements1.add(currentPair.getXpath1());
						}

						if (!checkedElements2.contains(currentPair.getXpath2())) {
							//stesso procedimento, ma con il secondo elemento della coppia
							bestPair2 = createListWithMaxPairs(currentPair.getXpath2(),
									currentRepository, true);
							checkedElements2.add(currentPair.getXpath2());
						}

						if (bestPair1 != null)
							finalListOfRepository.add(bestPair1);
						if (bestPair2 != null)
							finalListOfRepository.add(bestPair2);
					}
					Tuple2<String,String> domains = new Tuple2<>(domain1,domain2);
					finalMap.put(domains, finalListOfRepository);
				}
			}
		}

		return finalMap;
	} //fine main

	public static List<String> calculateReachableDomains(Xpath currentElement,
			String currentElementDomain, List<String> reachedDomains) {
		//		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		List<String> maxReachedDomains = new ArrayList<>(reachedDomains);

		for (int dom=0;dom<SourceInput.getSorgenti().size();dom++) {
			String domS = SourceInput.getSorgenti().get(dom);
			if (!reachedDomains.contains(domS)) {
				PairMatchingRepository currentRepository;
				boolean isDomAtRight = false;
				currentRepository = pmr.getRepository(currentElementDomain, domS);
				if (currentRepository != null) {
					isDomAtRight = true;
				}
				else {
					currentRepository = pmr.getRepository(domS, currentElementDomain);
					if (currentRepository != null) {
						isDomAtRight = false;
					}
				}
				if (currentRepository != null) {
					List<PairMatching> pairsWithCurrentElement = currentRepository
							.getPairsWith(currentElement, !isDomAtRight);
					for (int i=0;i<pairsWithCurrentElement.size();i++) {
						List<String> currentReachedDomains = new ArrayList<>(reachedDomains);
						currentReachedDomains.add(domS);
						if (isDomAtRight) {
							currentReachedDomains = 
									calculateReachableDomains(pairsWithCurrentElement.get(i).getXpath2(),
											domS, currentReachedDomains);
						}
						else {
							currentReachedDomains = 
									calculateReachableDomains(pairsWithCurrentElement.get(i).getXpath1(),
											domS, currentReachedDomains);
						}
						if (currentReachedDomains.size() > maxReachedDomains.size()) {
							maxReachedDomains = currentReachedDomains;
						}
					}
				} 
			} 
		} 
		return maxReachedDomains;
	}


	public static PairMatching createListWithMaxPairs(Xpath currentPath, 
			PairMatchingRepository currentRepository, boolean isElementRight) {

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

	public static Map<String,List<WebPage>> selectDomainsAndGetPagesWithUniqueName() {
		MongoFacade facade = new MongoFacade("web_search_pages");
		Map<String,List<WebPage>> domain2pages = new HashMap<>();
		DomRepToClean drtc = DomRepToClean.getInstance();
		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
			Source currentSource = facade.getSourceWithId(SourceInput.getSorgenti().get(i));
			drtc.addDomain(currentSource);
			SourceRep.addSource(currentSource);
			List<WebPage> pagesOfCurrentSource = WebPageSelector.getPageWithUniqueName(currentSource);
			domain2pages.put(currentSource.getId().toString(), pagesOfCurrentSource);
		}
		return domain2pages;
	}

	//	public static void inizializzaLista() {
	//		idSorgenti.add("5750678b3387e31f516fa1c7");
	//		idSorgenti.add("5750678b3387e31f516fa1d0");
	//		idSorgenti.add("5750678b3387e31f516fa1ca");
	//		idSorgenti.add("5750678b3387e31f516fa1cd");
	//		idSorgenti.add("5750678a3387e31f516fa185");
	//	}

	//restituisce il documento con il dominio richiesto, o null se non presente
	public static WebPage getWP(Set<Tuple2<String,WebPage>> documents, String idDomain) {
		Iterator<Tuple2<String,WebPage>> pagesIt = documents.iterator();
		while (pagesIt.hasNext()) {
			Tuple2<String,WebPage> currentTuple = pagesIt.next();
			if (currentTuple._1().equals(idDomain)) {
				return currentTuple._2();
			}
		}
		return null;
	}

	public static boolean mapContains(Map<String,List<Tuple2<String,String>>> blacklist_persone,
			String second_person, String domain1, String domain2) {
		List<Tuple2<String,String>> domains = blacklist_persone.get(second_person);
		if (domains != null) {
			for (int i=0;i<domains.size();i++) {
				Tuple2<String,String> currentDomainPair = domains.get(i);
				if (currentDomainPair._1().equals(domain1) && currentDomainPair._2().equals(domain2))
					return true;
				if (currentDomainPair._1().equals(domain2) && currentDomainPair._2().equals(domain1))
					return true;
			}
		}
		return false;
	}

	public static void inizializzaMappaSuccessi(Map<String,Integer> dominio2successi) {
		//TODO
		for (int i=0;i<SourceInput.getSorgenti().size();i++) {
			for (int j=i+1;j<SourceInput.getSorgenti().size();j++) {
				dominio2successi.put(SourceInput.getSorgenti().get(i)+"_"+SourceInput.getSorgenti().get(j), 0);
			}
		}

		//				for (int i=0;i<SourceInput.getSorgenti().size();i++) {
		//						dominio2successi.put(SourceInput.getSorgenti().get(i), 0);
		//				}
	}

	//	public static boolean sufficientiSuccessi(Map<String,Integer> dominio2successi) {
	//		//quando per ogni dominio si arriva a 3
	//		//puoi uscire dall'apprendimento
	//		Iterator<String> domIt = dominio2successi.keySet().iterator();
	//		while (domIt.hasNext()) {
	//			String curDom = domIt.next();
	//			int successi = dominio2successi.get(curDom);
	//			// stringa speciale per pagine bianche perché è poco gestibile
	//			if (curDom.equals("5750678a3387e31f516fa185")) {
	//				if (successi < sufficientiSuccessi_pb)
	//					return false;
	//			}
	//			if (successi < sufficientiSuccessi)
	//				return false;
	//		}
	//		return true;
	//	}

	//controllo se i domini specificati hanno già abbastanza successi (5)
	public static boolean sufficientiSuccessi(Map<String,Integer> dominio2successi,
			String dom1, String dom2) {
		//TODO modificato controllo
		//		int successi1 = dominio2successi.get(dom1);
		//		int successi2 = dominio2successi.get(dom2);
		//		if (successi2 >= successiDominio && successi1 >= successiDominio) {
		//			System.out.println("sufficienti successi con "+dom1+" e "+dom2);
		//			return true;
		//		}

		int successi = dominio2successi.get(dom1+"_"+dom2);
		if (successi >= successiDominio) {
			System.out.println("sufficienti successi con "+dom1+" e "+dom2);
			return true;
		}

		return false;
	}
}

