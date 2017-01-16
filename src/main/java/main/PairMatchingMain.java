package main;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;
import xpath.utils.XpathApplier;

/* questo main serve per un task esplorativo in cui cerchiamo di capire se vogliamo dei Matching
 * numerosi o dei cluster di Matching (non avremo un singolo matching sdoppiato)*/
public class PairMatchingMain {
	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/testGenericXpath/persone/";
	static int n1 = 1;
	static int n2 = 2;
	static int n3 = 3;
	static int n4 = 4;
	static int parN1 = 1;
	static int parN2 = 2;
	static double parameterTextFusion = -1;

	public static void main(String[] args) throws Exception {

		//fase di raccolta dei matching
		for(int k=1;k<=4;k++) {
			for (int k2=k+1;k2<=5;k2++) {
				//				int domain1 = k;
				//				int domain2 = k+1;
				int domain1 = k;
				int domain2 = k2;

				for (int i=1;i<=7;i++) {
					for (int j=(i+1);j<=7;j++) {


						System.out.println("***"+i+" "+j);

						String d1Folder = path+"p"+i+"/";
						String d2Folder = path+"p"+i+"/";
						String d3Folder = path+"p"+j+"/";
						String d4Folder = path+"p"+j+"/";

						String d1Path = d1Folder+"orig"+domain1+".html";
						String d2Path = d2Folder+"orig"+domain2+".html";

						String d3Path = d3Folder+"orig"+domain1+".html";
						String d4Path = d4Folder+"orig"+domain2+".html";


						System.out.println("d1: "+d1Path);
						System.out.println("d2: "+d2Path);
						System.out.println("d3: "+d3Path);
						System.out.println("d4: "+d4Path);


						File d1 = new File(d1Path);
						File d2 = new File(d2Path);

						File d3 = new File(d3Path);
						File d4 = new File(d4Path);

						if (d1.exists() && d2.exists() && d3.exists() && d4.exists()) {
							System.out.println("Trovati documenti");

							WebPageDocument firstDocument = new WebPageDocument(d1, domain1, d1Folder, 
									parameterTextFusion, domain1);
							WebPageDocument secondDocument = new WebPageDocument(d2, domain2, d2Folder, 
									parameterTextFusion, domain2);
							WebPageDocument thirdDocument = new WebPageDocument(d3, domain1, d3Folder, 
									parameterTextFusion, domain1);
							WebPageDocument fourthDocument = new WebPageDocument(d4, domain2, d4Folder, 
									parameterTextFusion, domain2);

							DomainsWrapper_pairMatching.getSegmentsFrom(firstDocument, secondDocument, 
									thirdDocument, fourthDocument, d1Folder, d3Folder, domain1, domain2, domain1, domain2);
						}
					}
				}
			}
		}

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
							calcolaDominiRaggiungibili(firstElement, domain_firstElement, dominiEsplorati_firstElement);
					
					currentMatching.setDominiRaggiungibili(firstElement, dominiEsplorati_firstElement);
					
					Xpath secondElement = currentMatching.getXpath2();
					int domain_secondElement = j;
					List<Integer> dominiEsplorati_secondElement = new ArrayList<>();
					dominiEsplorati_secondElement.add(i);
					dominiEsplorati_secondElement.add(j);
					dominiEsplorati_secondElement = 
							calcolaDominiRaggiungibili(secondElement, domain_secondElement, dominiEsplorati_secondElement);
					
					currentMatching.setDominiRaggiungibili(secondElement, dominiEsplorati_secondElement);
				}
			}
		}

		//stampiamo i collegamenti... T^T
		XpathApplier xapplier = XpathApplier.getInstance();
		//DA QUI MODIFICHI...

		//per una questione di efficienza, memorizzo qui i web page documenti
		Map<Tuple2<Integer,Integer>,WebPageDocument> personDomain2document = new HashMap<>();
		for(int p=1;p<=7;p++){
			for(int j=1;j<=5;j++) {
				String currentFolder = path+"p"+p+"/";
				String dPath = currentFolder+"orig"+j+".html";
				File d = new File(dPath);
				if (d.exists()) {
					WebPageDocument w = new WebPageDocument(
							new File(path+"p"+p+"/"+"orig"+j+".html"), 
							j, path+"p"+p+"/", 
							parameterTextFusion, j);
					personDomain2document.put(new Tuple2<>(p,j), w);
				}
			}
		}

		//scorro i domini
		for(int k=1;k<=4;k++) {
			for (int k2=k+1;k2<=5;k2++) {
				int domain1 = k;
				int domain2 = k2;
				PrintWriter testPrinter = new PrintWriter(path+"pairMatchings"+domain1+"_"+domain2+".csv", "UTF-8");

				PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);

				//scorro le persone
				for (int p=1;p<=7;p++) {

					WebPageDocument w1 = personDomain2document.get(new Tuple2<>(p,domain1));
					WebPageDocument w2 = personDomain2document.get(new Tuple2<>(p,domain2));

					if (w1 != null && w2 != null) {
						Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();
						Iterator<PairMatching> matchingIt = matchings2votes.keySet().iterator();
						while (matchingIt.hasNext()) {
							PairMatching currentPair = matchingIt.next();
							//**persona
							testPrinter.print("Persona "+p+";");
							//**xpath1
							testPrinter.print(currentPair.getXpath1().getXpath()+";");
							//**contenuto del segmento identificato applicando xpath1 al documento 1
							NodeList nl1 = xapplier.getNodes(currentPair.getXpath1().getXpath(), 
									w1.getDocument_jsoup());
							if (nl1.getLength() != 0) {
								testPrinter.print(nl1.item(0).getTextContent().replaceAll(";", "")
										.replaceAll("\n", "")+";");
							}
							else	{ //l'xpath non ha restituito nessun segmento
								testPrinter.print("--;");
							}
							//**domini raggiungibili da xpath1
							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath1()+" ("+
									currentPair.getDominiRaggiungibiliDaXpath1().size()+")"+";");
							//**voto
							//							testPrinter.print(matchings2votes.get(currentPair)+",");
							testPrinter.print(new DecimalFormat("#.##").format(matchings2votes.get(currentPair))
									+";");
							//**domini raggiungibili da xpath2
							testPrinter.print(currentPair.getDominiRaggiungibiliDaXpath2()+" ("+
									currentPair.getDominiRaggiungibiliDaXpath2().size()+")"+";");
							//**contenuto del segmento* identificato applicando xpath2 al documento 2
							NodeList nl2 = xapplier.getNodes(currentPair.getXpath2().getXpath(),
									w2.getDocument_jsoup());
							if (nl2.getLength() != 0) {
								testPrinter.print(nl2.item(0).getTextContent().replaceAll(";", "")
										.replaceAll("\n", "")+";");
							}
							else	{ //l'xpath non ha restituito nessun segmento
								testPrinter.print("--;");
							}
							//**xpath2
							testPrinter.println(currentPair.getXpath2().getXpath());

							//coloriamo i due segmenti dei due documenti
							//							xapplier.color_iter(currentPair.getXpath1(), matchings2votes.get(currentPair), w1.getDocument_w3c());
							//							xapplier.color_iter(currentPair.getXpath2(), matchings2votes.get(currentPair), w2.getDocument_w3c());

						} //fine while matchings
					} //fine if d1 e d2 exist
					testPrinter.println();
				} //fine for persone
				testPrinter.close();
			}

		} //fine scorrimento domini


		//		for (int dom = 1; dom <=5; dom++) {
		//			for (int p=1;p<=7;p++) {
		//				Tuple2<Integer,Integer> person_domain = new Tuple2<>(p,dom);
		//				WebPageDocument currentDocument = personDomain2document.get(person_domain);
		//				if (currentDocument != null) {
		//					PrintWriter color_printer = new PrintWriter(path+"p"+p+"/"+"orig"+dom+"matchingSegmentsFILTRATO.html", "UTF-8");
		//					String doc = DocumentUtils.getStringFromDocument(currentDocument.getDocument_w3c());
		//					color_printer.println(doc);
		//					color_printer.close();
		//				}
		//			}
		//		} //fine 

		//coloro i documenti
		//		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		//		for (int dom = 1; dom <=5; dom++) {
		//			for (int otherDom = 1 ; otherDom <=5 ; otherDom++) {
		//				if (dom != otherDom) {
		//					PairMatchingRepository currentRepository;
		//					boolean isOtherMajor;
		//					if (otherDom > dom) {
		//						currentRepository = pmr.getRepository(dom, otherDom);
		//						isOtherMajor = true;
		//					}
		//					else {
		//						currentRepository = pmr.getRepository(otherDom, dom);
		//						isOtherMajor = false;
		//					}
		//
		//					for (int p=1;p<=7;p++) {
		//						Tuple2<Integer,Integer> person_domain = new Tuple2<>(p,dom);
		//						WebPageDocument currentDocument = personDomain2document.get(person_domain);
		//						if (currentDocument != null) {
		//							//ora coloro iterativamente il documento, con le xpath del repository corrente
		//							Set<PairMatching> matchings = currentRepository.getMatchings();
		//							Iterator<PairMatching> matchingsIt = matchings.iterator();
		//							while (matchingsIt.hasNext()) {
		//								PairMatching currentMatching = matchingsIt.next();
		//								if (isOtherMajor) {
		//									//ci interessa il primo elemento della coppia
		//									Xpath currentXpath = currentMatching.getXpath1();
		//									xapplier.color_iter(currentXpath, currentDocument.getDocument_w3c());
		//								}
		//								else {
		//									//ci interessa il secondo elemento della coppia
		//									Xpath currentXpath = currentMatching.getXpath2();
		//									xapplier.color_iter(currentXpath, currentDocument.getDocument_w3c());
		//								}
		//							} //fine scorrimento matching della repository corrente
		//						}
		//					} //fine ciclo persone
		//				}
		//			} //fine scorrimento altro dominio
		//		} //fine scorrimento dominio



		//		MatchingRepository mr = MatchingRepository.getInstance();
		//		List<Matching> matchings = mr.getMatchings();
		//		PrintWriter testPrinter = new PrintWriter(path+"finalMatchingsXpaths_L.csv", "UTF-8");
		//		System.out.println(matchings.size());
		//		DomainsRepository dr = DomainsRepository.getInstance();
		//		for (int p=1;p<=7;p++) {
		//			//per una questione di efficienza, memorizzo qui i web page documenti
		//			Map<Integer,WebPageDocument> domain2document = new HashMap<>();
		//			for(int j=1;j<=5;j++) {
		//				String currentFolder = path+"p"+p+"/";
		//				String dPath = currentFolder+"orig"+j+".html";
		//				File d = new File(dPath);
		//				if (d.exists()) {
		//					WebPageDocument w = new WebPageDocument(
		//							new File(path+"p"+p+"/"+"orig"+j+".html"), 
		//							j, path+"p"+p+"/", 
		//							parameterTextFusion, j);
		//					domain2document.put(j, w);
		//				}
		//			}
		//			for (int i=0; i<matchings.size(); i++) {
		//				Matching currentMatching = matchings.get(i);
		//				Map<DomainSource,Xpath> mapMatchings = currentMatching.getMatching();
		//				for(int j=1;j<=5;j++) {
		//					//			Iterator<DomainSource> matchingsIt = mapMatchings.keySet().iterator();
		//					//			while (matchingsIt.hasNext()) {
		//					//faccio scorrere i domini
		//					//				DomainSource currentDomain = matchingsIt.next();
		//					DomainSource currentDomain = dr.getDomain(j);
		//					Xpath currentXpath = mapMatchings.get(currentDomain);
		//
		//					//qui raccogli, con if che identificano il dominio j, tutti gli xpath in un set
		//					//un set per ogni dominio
		//					//					if (!domain2xpaths.containsKey(j)) {
		//					//						domain2xpaths.put(j, new HashSet<>());
		//					//					}
		//					//					domain2xpaths.get(j).add(currentXpath);
		//
		//					if (currentXpath != null) {
		//						//				System.out.println("Dominio "+currentDomain.getParameter());
		//						//				System.out.println(currentXpath.getXpath());
		//						String currentFolder = path+"p"+p+"/";
		//						String d1Path = currentFolder+"orig"+currentDomain.getParameter()+".html";
		//						File d1 = new File(d1Path);
		//						if (d1.exists()) {
		//							//							WebPageDocument w = new WebPageDocument(new File(path+"p"+p+"/"+"orig"+currentDomain.getParameter()+".html"), 
		//							//									currentDomain.getParameter(), path+"p"+p+"/", 
		//							//									parameterTextFusion, currentDomain.getParameter());
		//							WebPageDocument w = domain2document.get(j);
		//							NodeList nl = xapplier.getNodes(currentXpath.getXpath(), w.getDocument_jsoup());
		//							if (nl.getLength() != 0) {
		//								//						System.out.println(nl.item(0).getTextContent().replaceAll(",", "")+",");
		//								testPrinter.print(nl.item(0).getTextContent().replaceAll(",", "")
		//										.replaceAll("\n", "")+",");
		//							}
		//							else	{ //nessun matching, lascio spazio vuoto 
		//								//						System.out.println(" ,");
		//								testPrinter.print("--,");
		//							}
		//
		//							//coloro il segmento
		//							xapplier.color_iter(currentXpath, w.getDocument_w3c());
		//						}
		//						else  { //nessun documento, quindi per quella persona lasciamo uno spazio vuoto
		//							//					System.out.println("Dominio "+currentDomain.getParameter());
		//							//					System.out.println(" ,");
		//							testPrinter.print("--,");
		//						}
		//					}
		//					else {//non c'è nel matching corrente il dominio corrente
		//						testPrinter.print("--,");
		//					}
		//
		//				}
		//				testPrinter.println();
		//				//			System.out.println("______________________________");
		//			}
		//			testPrinter.println();
		//			//ora prendo i documenti dalla mappa e li stampo
		//			for (int j=1;j<=5;j++) {
		//				WebPageDocument w = domain2document.get(j);
		//				if (w != null) {
		//					PrintWriter printer = new PrintWriter(path+"p"+p+"/"+"orig"+j+"matchingSegmentsL.html", "UTF-8");
		//					String doc = DocumentUtils.getStringFromDocument(w.getDocument_w3c());
		//					printer.println(doc);
		//					printer.close();
		//				}
		//			}
		//		}
		//		testPrinter.close();
		//
		//		//alla fine delle stampe otterrai i set completi e li applicherai, facendo un nuovo ciclo sulle
		//		//persone, a ogni documento
		//
	} //fine main

	public static List<Integer> calcolaDominiRaggiungibili(Xpath elementoCorrente, int dominioElementoCorrente,
			List<Integer> dominiEsplorati) {
		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		List<Integer> dominiEsploratiMax = new ArrayList<>(dominiEsplorati);
		//sappiamo che i domini sono 5, quindi conosciamo l'upper bound di questo for
		//TODO in generale, dovresti inventarti qualcos'altro
		for (int dom=1; dom<=5; dom++) {
			if (!dominiEsplorati.contains(dom)) {
				PairMatchingRepository currentRepository;
				boolean isDom_aDestra;
				if (dominioElementoCorrente < dom) {
					currentRepository = pmr.getRepository(dominioElementoCorrente, dom);
					isDom_aDestra = true;
				}
				else {
					currentRepository = pmr.getRepository(dom, dominioElementoCorrente);
					isDom_aDestra = false;
				}
				List<PairMatching> pairsWithElementoCorrente = currentRepository
						.getPairsWith(elementoCorrente, !isDom_aDestra);
				for (int i=0;i<pairsWithElementoCorrente.size();i++) {
					List<Integer> dominiEsploratiAggiornato = new ArrayList<>(dominiEsplorati);
					dominiEsploratiAggiornato.add(dom);
					if (isDom_aDestra) {
						dominiEsploratiAggiornato = 
								calcolaDominiRaggiungibili(pairsWithElementoCorrente.get(i).getXpath2(),
								dom, dominiEsploratiAggiornato);
					}
					else {
						dominiEsploratiAggiornato = 
								calcolaDominiRaggiungibili(pairsWithElementoCorrente.get(i).getXpath1(),
								dom, dominiEsploratiAggiornato);
					}
					if (dominiEsploratiAggiornato.size() > dominiEsploratiMax.size()) {
						dominiEsploratiMax = dominiEsploratiAggiornato;
					}
				} //end for
			} //end if(!dominiEsplorati.contains(dom))
		} //end for
		return dominiEsploratiMax;
	}
}

