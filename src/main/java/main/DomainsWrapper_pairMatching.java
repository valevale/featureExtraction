package main;

import java.io.File;
import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jsoup.nodes.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import configurations.Configurator;
import lib.utils.DocumentUtils;
//import lib.utils.DocumentUtils;
import lib.utils.NodeW3cUtils;
import lib.utils.XpathApplier;
import lucene.SegmentSearcher;
import model.DomainSource;
import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.Segment;
import model.Source;
import model.WebPage;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;
import segmentation.TopSegmentsFinder;

/* estraiamo da un documento, in comparazione con altri 3 (uno della stessa persona di un altro dominio,
 * uno di un'altra persona con lo stesso dominio, e un ultimo della stessa altra persona e dello stesso
 * altro dominio), le coppie di segmenti del documento 1 e 2 simili per coseno similarità
 * le coppie vengono messe in un repository e si calcola il loro voto, basato sulla coseno smilarità */
public class DomainsWrapper_pairMatching {


	/* restituisce:
	 * 0 se è andato tutto a buon fine
	 * 1 se va blacklistata la prima persona
	 * 2 se va blacklistata la seconda persona
	 * */
	public static int getSegmentsFrom_server(WebPageDocument firstDocument,
			WebPageDocument secondDocument, String ancora_p1,
			WebPageDocument thirdDocument, WebPageDocument fourthDocument, String ancora_p2,
			boolean id_found)
					throws Exception {
		
		//TODO togli quando è senza cache!
		//creo la cache
		Map<String,Boolean> xpath2isIdentificativa = new HashMap<>();

		TopSegmentsFinder finder = TopSegmentsFinder.getInstance();

		String path = Configurator.getIndexesPath();

		String indexPathDominio1 = path+ancora_p1+"/segmentIndex";

		File dir = new File(indexPathDominio1);
		dir.mkdirs();
		File indexFolder = new File(indexPathDominio1);
		String[]entries = indexFolder.list();

		//eliminazione dell'indice
		if (entries != null) {
			for(String s: entries){
				File currentFile = new File(indexFolder.getPath(),s);
				currentFile.delete();
			}
		}

		//trovo i segmenti rilevanti per la prima persona (per pagina 1 e pagina 2, matchati)
		List<Tuple2<Segment, TopDocs>> segment2hits_primaPersona =
				finder.findRelevantSegments(indexPathDominio1, firstDocument, secondDocument);

		//gli index sono stati messi ARBITRARIAMENTE nelle due cartelle, una vale l'altra
		String indexPathDominio2 = path+ancora_p2+"/segmentIndex";

		//stesso procedimento, con un'altra coppia di pagine degli stessi domini
		//eliminazione dell'indice
		dir = new File(indexPathDominio2);
		dir.mkdirs();
		indexFolder = new File(indexPathDominio2);
		entries = indexFolder.list();

		if (entries != null) {
			for(String s: entries){
				File currentFile = new File(indexFolder.getPath(),s);
				currentFile.delete();
			}
		}

		//trovo i segmenti rilevanti per la seconda persona (per pagina 1 e pagina 2, matchati)
		List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona =
				finder.findRelevantSegments(indexPathDominio2, thirdDocument, fourthDocument);

		//raccolgo i segmenti rilevanti del terzo documento (seconda persona - primo dominio)
		List<Segment> relevantSegments_thirdDocument = new ArrayList<>();
		for (int i=0; i<segment2hits_secondaPersona.size(); i++) {
			Segment currentSegment = segment2hits_secondaPersona.get(i)._1();
			if (currentSegment.getRelevance() > 0) {
				relevantSegments_thirdDocument.add(currentSegment);
			}
		}

		//raccolgo i segmenti rilevanti del quarto documento (seconda persona - secondo dominio)
		List<Segment> relevantSegments_fourthDocument = new ArrayList<>();
		Iterator<Segment> fourthDocumentSegmentsIt = fourthDocument.getSegments().iterator();
		while (fourthDocumentSegmentsIt.hasNext()) {
			Segment currentSegment = fourthDocumentSegmentsIt.next();
			if (currentSegment.getRelevance() > 0) {
				relevantSegments_fourthDocument.add(currentSegment);
			}
		}



		double threshold = Configurator.getCosSimThreshold();

		if (segment2hits_primaPersona.size() != 0 && segment2hits_secondaPersona.size() != 0) {
			//prendo i segmenti rilevanti della prima persona
			//per ogni matching che supera la soglia, provo a aggiungere il matching al repository
			//TODO teoricamente tutto questo metodo è molto inefficiente. per ora lascialo così, ma va migliorato
			//parlo di getSegmentsFrom_server
			//per esempio puoi salvarti la mappa dei matching, invece di fare sto segments2hits, ovunque
			SegmentSearcher searcher = new SegmentSearcher(indexPathDominio1);
			for (int j=0; j<segment2hits_primaPersona.size(); j++) {
				Segment seg = segment2hits_primaPersona.get(j)._1();
				TopDocs hits = segment2hits_primaPersona.get(j)._2();
				for(ScoreDoc scoreDoc : hits.scoreDocs) {
					if (scoreDoc.score >= threshold) {
						org.apache.lucene.document.Document lucDoc = null;
						try {
							lucDoc = searcher.getDocument(scoreDoc);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Segment seg_secondDocument = secondDocument.getSegmentByXpath(lucDoc.get("segmentPath"));

						//TODO 1 con controllo
//						id_found = addLink_CC(seg, seg_secondDocument, thirdDocument, fourthDocument, scoreDoc.score,
//								relevantSegments_thirdDocument, relevantSegments_fourthDocument,
//								segment2hits_secondaPersona, indexPathDominio2, id_found);
						
						//TODO 2 con controllo e cache
						id_found = addLink_CCconCache(seg, seg_secondDocument, thirdDocument, fourthDocument, scoreDoc.score,
								relevantSegments_thirdDocument, relevantSegments_fourthDocument,
								segment2hits_secondaPersona, indexPathDominio2, id_found,
								xpath2isIdentificativa);

						//TODO 3 senza controllo
						//						addLink_SC(seg, seg_secondDocument, thirdDocument, fourthDocument, scoreDoc.score,
						//								relevantSegments_thirdDocument, relevantSegments_fourthDocument,
						//								segment2hits_secondaPersona, indexPathDominio2);
						//						id_found = true;
						//fine cambiamento
					}
				}
			}
		}
		if (!id_found) {
			//caso in cui non sia stata trovata alcuna pagina identificativa
			//scarta il repository temporaneo
			PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
			pmr.destroy_tempRep();

			//memorizzo il numero di matching delle persone
			int countMatching_p1 = countMatchings(segment2hits_primaPersona);
			int countMatching_p2 = countMatchings(segment2hits_secondaPersona);
			if (countMatching_p1 < countMatching_p2) {
				//blacklisto la prima persona
				return 1;
			}
			else {
				//blacklisto la seconda persona
				return 2;
			}
		}

		return 0;
	} //fine main

	private static boolean addLink_CCconCache(Segment firstSegment, Segment secondSegment,
			WebPageDocument doc3, WebPageDocument doc4, float score,
			List<Segment> relevantSegments_thirdDocument,
			List<Segment> relevantSegments_fourthDocument, 
			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
			String indexPath, boolean id_found, Map<String,Boolean> xpath2isIdentificativa) throws Exception {
		//creazione degli xpath generici
		//OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
		//PRIMA controlli che per quel segmento non sia già stato generato un xpath generico
		//SE SÌ allora metti quello nella coppia collegamento
		//SE NO generi un xpath generico
		//prima persona - primo dominio
		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
		if (genericXpath_firstSegment == null) {
			//generi un xpath generico
			firstSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(firstSegment.getJsoupNode(),
						firstSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						firstSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE nel terzo documento
				//(sarebbe seconda persona - primo dominio)
				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					firstSegment.setXPath(currentXpath);
					//					firstSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_firstSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		//stesso procedimento per seconda persona - primo dominio
		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
		if (genericXpath_secondSegment == null) {
			//generi un xpath generico
			secondSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(secondSegment.getJsoupNode(),
						secondSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						secondSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					secondSegment.setXPath(currentXpath);
					//					secondSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_secondSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		if (genericXpath_firstSegment != null && genericXpath_secondSegment != null) {
			//CONTROLLO AGGIUNTIVO: i segmenti ottenuti da questi xpath generici sono stati matchati
			//per alta coseno similarità nell'insieme segment2hits_secondaPersona
			if (isARelevantMatching(genericXpath_firstSegment.getXpath(), doc3, 
					genericXpath_secondSegment.getXpath(), doc4, 
					segment2hits_secondaPersona, indexPath)) {
				if (id_found) {
					PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
					//aggiungo le xpath al dominio
					firstSegment.getDocument().getSource().addGenericXpath(genericXpath_firstSegment);
					secondSegment.getDocument().getSource().addGenericXpath(genericXpath_secondSegment);
					//aggiungo il matching al repository
					pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
							genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
				}
				else {
					// qui controllo id
					// qui modifichi
					
					if (isXpathIdentificativo_conCache(genericXpath_firstSegment, xpath2isIdentificativa)
							|| isXpathIdentificativo_conCache(genericXpath_secondSegment, xpath2isIdentificativa)){
						
						
						//						System.out.println("ALLELUJA");
						PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
						//setto che ho trovato l'identificativo
						id_found = true;
						//aggiungo le xpath al dominio
						firstSegment.getDocument().getSource().addGenericXpath(genericXpath_firstSegment);
						secondSegment.getDocument().getSource().addGenericXpath(genericXpath_secondSegment);
						//aggiungo anche le xpath generiche del repository al dominio
						PairMatchingRepository temp_repository = pmr.getTempRepository();
						Map<PairMatching,Float> matchings2vote = temp_repository.getMatchings2vote();
						Iterator<PairMatching> it = matchings2vote.keySet().iterator();
						while (it.hasNext()) {
							PairMatching currentMatching = it.next();
							Xpath xpath1 = currentMatching.getXpath1();
							Xpath xpath2 = currentMatching.getXpath2();
							firstSegment.getDocument().getSource().addGenericXpath(xpath1);
							secondSegment.getDocument().getSource().addGenericXpath(xpath2);
						}
						//aggiungo il matching al repository
						pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
								genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
						//aggiungo anche gli altri matching del repository temporaneo al repository corretto
						pmr.moveTempRepMatchings(firstSegment.getDocument().getSource().getParameter(),
								secondSegment.getDocument().getSource().getParameter());
						//poi distruggi il repository temporaneo
						pmr.destroy_tempRep();
					}
					else {
						//aggiungo il matching a un repository temporaneo
						PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
						pmr.addMatching_tempRep(genericXpath_firstSegment, genericXpath_secondSegment, score);

					}
				}
			}
		}
		return id_found;
	}
	
	
	private static boolean addLink_CC(Segment firstSegment, Segment secondSegment,
			WebPageDocument doc3, WebPageDocument doc4, float score,
			List<Segment> relevantSegments_thirdDocument,
			List<Segment> relevantSegments_fourthDocument, 
			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
			String indexPath, boolean id_found) throws Exception {
		//creazione degli xpath generici
		//OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
		//PRIMA controlli che per quel segmento non sia già stato generato un xpath generico
		//SE SÌ allora metti quello nella coppia collegamento
		//SE NO generi un xpath generico
		//prima persona - primo dominio
		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
		if (genericXpath_firstSegment == null) {
			//generi un xpath generico
			firstSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(firstSegment.getJsoupNode(),
						firstSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						firstSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE nel terzo documento
				//(sarebbe seconda persona - primo dominio)
				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					firstSegment.setXPath(currentXpath);
					//					firstSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_firstSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		//stesso procedimento per seconda persona - primo dominio
		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
		if (genericXpath_secondSegment == null) {
			//generi un xpath generico
			secondSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(secondSegment.getJsoupNode(),
						secondSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						secondSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					secondSegment.setXPath(currentXpath);
					//					secondSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_secondSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		if (genericXpath_firstSegment != null && genericXpath_secondSegment != null) {
			//CONTROLLO AGGIUNTIVO: i segmenti ottenuti da questi xpath generici sono stati matchati
			//per alta coseno similarità nell'insieme segment2hits_secondaPersona
			if (isARelevantMatching(genericXpath_firstSegment.getXpath(), doc3, 
					genericXpath_secondSegment.getXpath(), doc4, 
					segment2hits_secondaPersona, indexPath)) {
				if (id_found) {
					PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
					//aggiungo le xpath al dominio
					firstSegment.getDocument().getSource().addGenericXpath(genericXpath_firstSegment);
					secondSegment.getDocument().getSource().addGenericXpath(genericXpath_secondSegment);
					//aggiungo il matching al repository
					pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
							genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
				}
				else {
					// qui controllo id
					// qui modifichi
					
					if (isXpathIdentificativo(genericXpath_firstSegment)
							|| isXpathIdentificativo(genericXpath_secondSegment)){
						//						System.out.println("ALLELUJA");
						PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
						//setto che ho trovato l'identificativo
						id_found = true;
						//aggiungo le xpath al dominio
						firstSegment.getDocument().getSource().addGenericXpath(genericXpath_firstSegment);
						secondSegment.getDocument().getSource().addGenericXpath(genericXpath_secondSegment);
						//aggiungo anche le xpath generiche del repository al dominio
						PairMatchingRepository temp_repository = pmr.getTempRepository();
						Map<PairMatching,Float> matchings2vote = temp_repository.getMatchings2vote();
						Iterator<PairMatching> it = matchings2vote.keySet().iterator();
						while (it.hasNext()) {
							PairMatching currentMatching = it.next();
							Xpath xpath1 = currentMatching.getXpath1();
							Xpath xpath2 = currentMatching.getXpath2();
							firstSegment.getDocument().getSource().addGenericXpath(xpath1);
							secondSegment.getDocument().getSource().addGenericXpath(xpath2);
						}
						//aggiungo il matching al repository
						pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
								genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
						//aggiungo anche gli altri matching del repository temporaneo al repository corretto
						pmr.moveTempRepMatchings(firstSegment.getDocument().getSource().getParameter(),
								secondSegment.getDocument().getSource().getParameter());
						//poi distruggi il repository temporaneo
						pmr.destroy_tempRep();
					}
					else {
						//aggiungo il matching a un repository temporaneo
						PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
						pmr.addMatching_tempRep(genericXpath_firstSegment, genericXpath_secondSegment, score);

					}
				}
			}
		}
		return id_found;
	}

	private static void addLink_SC(Segment firstSegment, Segment secondSegment,
			WebPageDocument doc3, WebPageDocument doc4, float score,
			List<Segment> relevantSegments_thirdDocument,
			List<Segment> relevantSegments_fourthDocument, 
			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
			String indexPath) throws XPathExpressionException, IOException, ParserConfigurationException {
		//creazione degli xpath generici
		//OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
		//PRIMA controlli che per quel segmento non sia già stato generato un xpath generico
		//SE SÌ allora metti quello nella coppia collegamento
		//SE NO generi un xpath generico
		//prima persona - primo dominio
		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
		if (genericXpath_firstSegment == null) {
			//generi un xpath generico
			firstSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(firstSegment.getJsoupNode(),
						firstSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						firstSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE nel terzo documento
				//(sarebbe seconda persona - primo dominio)
				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					firstSegment.setXPath(currentXpath);
					//					firstSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_firstSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		//stesso procedimento per seconda persona - primo dominio
		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
		if (genericXpath_secondSegment == null) {
			//generi un xpath generico
			secondSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = new Xpath(secondSegment.getJsoupNode(),
						secondSegment.getXpathVersions().getPathBySpecificity(specificityParameter),
						secondSegment.getDocument().getIdDomain(),
						specificityParameter);
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					secondSegment.setXPath(currentXpath);
					//					secondSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_secondSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		if (genericXpath_firstSegment != null && genericXpath_secondSegment != null) {
			//CONTROLLO AGGIUNTIVO: i segmenti ottenuti da questi xpath generici sono stati matchati
			//per alta coseno similarità nell'insieme segment2hits_secondaPersona
			if (isARelevantMatching(genericXpath_firstSegment.getXpath(), doc3, 
					genericXpath_secondSegment.getXpath(), doc4, 
					segment2hits_secondaPersona, indexPath)) {
				// è qui che devi vedere se aggiungere il matching. lo fai solo se almeno una delle coppie è
				//identificativa in almeno uno dei domini
				//devo vedere se almeno uno. se almeno uno allora aggiungi tutti, altrimenti non aggiungi nessuno
				//mi serve un repository temporaneo
				firstSegment.getDocument().getSource().addGenericXpath(genericXpath_firstSegment);
				secondSegment.getDocument().getSource().addGenericXpath(genericXpath_secondSegment);
				//una volta che hai i generici di entrambi, crei collegamento
				PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
				pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
						genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
			}
		}
	}



	//dato un segmento e un documento (o meglio, il suo dominio), controllo se
	//esiste per il dominio una xpath generica che restituisce proprio il segmento
	//altrimenti restituisce null
	//in altre parole, vedo se esiste un'xpath generica del dominio del documento, tale che
	//riesce a individuare il segmento richiesto
	public static Xpath getGenericXpath(Segment segment, WebPageDocument webPageDocument) throws XPathExpressionException, IOException {
		DomainSource domain = webPageDocument.getSource();
		Set<Xpath> domainGenericXpath = domain.getGenericXpaths();
		XpathApplier xapplier = XpathApplier.getInstance();
		Iterator<Xpath> genericXpathIt = domainGenericXpath.iterator();
		while (genericXpathIt.hasNext()) {
			Xpath currentGenericXpath = genericXpathIt.next();
			String path = currentGenericXpath.getXpath();
			NodeList nl = xapplier.getNodes(path, webPageDocument.getDocument_jsoup());
			//noi ne vogliamo 1 e 1 solo
			if (nl.getLength() == 1) {
				if (NodeW3cUtils.areEqualNodes(nl.item(0), segment.getW3cNodes().item(0))) {
					return currentGenericXpath;
				}
			}
		}
		return null;
	}

	//data una xpath, un documento e una lista di suoi segmenti rilevanti
	//restituisce true se quella xpath, se applicata al documento, restituisce un segmento
	//e quel segmento è rilevante (appartiene alla lista dei segmenti rilevanti)
	private static boolean isARelevantSegment(String xpath, WebPageDocument document, List<Segment> relevantSegments)
			throws XPathExpressionException, IOException, ParserConfigurationException {
		if (xpath.equals("")) 
			return false;
		XpathApplier xapplier = XpathApplier.getInstance();
		NodeList xpathNodes = xapplier.getNodes(xpath, document.getDocument_jsoup());
		if (xpathNodes.getLength()!=1)
			return false;
		Node xpathNode = xpathNodes.item(0);
		//controlliamo che il nodo restituito corrisponda a un segmento rilevante del documento 3
		for (int i=0;i<relevantSegments.size();i++) {
			Segment relevantSegment = relevantSegments.get(i);
			if (NodeW3cUtils.areEqualNodes(xpathNode, relevantSegment.getW3cNodes().item(0))) {
				return true;
			}
		}
		//nessun matching
		return false;
	}

	/*controlla che i segmenti siano presenti in un matching nei documenti */
	private static boolean isARelevantMatching(String xpath1, WebPageDocument doc1,
			String xpath2,WebPageDocument doc2,
			List<Tuple2<Segment, TopDocs>> segment2hits, String indexPath)
					throws XPathExpressionException, IOException, ParserConfigurationException {

		if (xpath1.equals("") || xpath1.equals("")) 
			return false;

		XpathApplier xapplier = XpathApplier.getInstance();
		NodeList xpathNodes1 = xapplier.getNodes(xpath1, doc1.getDocument_jsoup());
		NodeList xpathNodes2 = xapplier.getNodes(xpath2, doc2.getDocument_jsoup());

		if (xpathNodes1.getLength()!=1 || xpathNodes2.getLength()!=1)
			return false;

		Node xpathNode1 = xpathNodes1.item(0);
		Node xpathNode2 = xpathNodes2.item(0);

		for (int i=0;i<segment2hits.size();i++) {
			Tuple2<Segment, TopDocs> segment2hit = segment2hits.get(i);
			if (NodeW3cUtils.areEqualNodes(xpathNode1, segment2hit._1().getW3cNodes().item(0))) {
				//abbiamo individuato il segmento del primo documento, ora dobbiamo trovare il segmento
				//del secondo documento
				SegmentSearcher searcher = new SegmentSearcher(indexPath);
				TopDocs hits = segment2hit._2();
				for(ScoreDoc scoreDoc : hits.scoreDocs) {
					if (scoreDoc.score >= 0.6) {
						org.apache.lucene.document.Document lucDoc = null;
						try {
							lucDoc = searcher.getDocument(scoreDoc);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Segment seg_secondDocument = doc2.getSegmentByXpath(lucDoc.get("segmentPath"));
						if (NodeW3cUtils.areEqualNodes(xpathNode2, seg_secondDocument.getW3cNodes().item(0))) {
							return true;
						}
					}
				}
				//nessun matching nelle hits
				return false;
			}
		}
		//nessun matching
		return false;
	}

	public static int countMatchings(List<Tuple2<Segment, TopDocs>> segment2hits) {
		int count = 0;
		double threshold = Configurator.getCosSimThreshold();
		for (int j=0; j<segment2hits.size(); j++) {
			TopDocs hits = segment2hits.get(j)._2();
			//per ogni risultato
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				//se supera la soglia di coseno similarità
				if (scoreDoc.score >= threshold) {
					//un matching
					count++;
				}
			}
		}
		return count;
	}

	public static boolean isXpathIdentificativo(Xpath genericXpath) throws Exception {
		System.out.println("CONTROLLO CHE XPATH SIA SIGNIFICATIVO");
		String idSource = genericXpath.getIdDomain();
		// qui è meglio un repository
		//		MongoFacade facade = new MongoFacade("web_search_pages");
		//		Source source = facade.getSourceWithId(idSource);
		Source source = SourceRep.getSource(idSource);
		Map<String,Integer> contenuto2volte = new HashMap<>();
		int numeroPagineSenzaContenuto = 0;
		//sarebbe bello analizzarle tutte, ma ci vuole troppo tempo, quindi farò le primo 100 :/
		//		for (int j=0;j<source.getPages().size();j++) {
		for (int j=0;j<100;j++) {
			if ((j+1)%10==0)
				System.out.print("*****pagina numero: "+(j+1)+"/100");
			//per ogni pagina, applico la xpath
			WebPage currentPage = source.getPages().get(j);
			Document doc = DocumentUtils.prepareDocument(currentPage.getHtml(), idSource);
			XpathApplier xapplier = XpathApplier.getInstance();
			NodeList nl = xapplier.getNodes(genericXpath.getXpath(), doc);
			//come ci organizziamo?
			//ciò che devo memorizzare
			//-totale pagine
			//-numero di "--" raccolti
			//-numero di valori unici
			//creo una mappa contenuto_numero di volte incontrato
			//se il numero di volte incontrato supera 3, tolgo l'elemento dalla mappa
			if (nl.getLength() != 0) {
				String currentContent = nl.item(0).getTextContent();
				Integer volte = contenuto2volte.get(currentContent);
				if (volte == null)
					volte = 0;
				volte++;
				if (volte <= 3)
					contenuto2volte.put(currentContent, volte);
				else
					contenuto2volte.remove(currentContent);
			}
			else	{ //l'xpath non ha restituito nessun segmento
				numeroPagineSenzaContenuto++;
			}
		}
		//ora valutiamo
		//se il numero di pagine senza contenuto è minore del 50%
		System.out.println("numeroPagineSenzaContenuto "+numeroPagineSenzaContenuto);
		System.out.println("contenuto "+contenuto2volte.size());
		if (numeroPagineSenzaContenuto < (100/2)) {
			//se il numero di valori unici è maggiore del 65%
			if (contenuto2volte.size() >= (65)) {
				//				System.out.println("significativo!!");
				return true;
			}
		}
		//		System.out.println("non significativo");
		return false;
	}
	
	public static boolean isXpathIdentificativo_conCache(Xpath genericXpath,
			Map<String,Boolean> xpath2isIdentificativa) throws Exception {
		System.out.println("CONTROLLO CHE XPATH SIA SIGNIFICATIVO");
		String idSource = genericXpath.getIdDomain();
		
		//NOVITÀ: controllo se è già presente in cache
		if (xpath2isIdentificativa.containsKey(genericXpath.getXpath())) {
			System.out.println("Contiene: "+xpath2isIdentificativa.get(genericXpath.getXpath()));
			return xpath2isIdentificativa.get(genericXpath.getXpath());
		}
		
		// qui è meglio un repository
		//		MongoFacade facade = new MongoFacade("web_search_pages");
		//		Source source = facade.getSourceWithId(idSource);
		Source source = SourceRep.getSource(idSource);
		Map<String,Integer> contenuto2volte = new HashMap<>();
		int numeroPagineSenzaContenuto = 0;
		//sarebbe bello analizzarle tutte, ma ci vuole troppo tempo, quindi farò le primo 100 :/
		//		for (int j=0;j<source.getPages().size();j++) {
		for (int j=0;j<100;j++) {
			if ((j+1)%10==0)
				System.out.print("*****pagina numero: "+(j+1)+"/100");
			//per ogni pagina, applico la xpath
			WebPage currentPage = source.getPages().get(j);
			Document doc = DocumentUtils.prepareDocument(currentPage.getHtml(), idSource);
			XpathApplier xapplier = XpathApplier.getInstance();
			NodeList nl = xapplier.getNodes(genericXpath.getXpath(), doc);
			//come ci organizziamo?
			//ciò che devo memorizzare
			//-totale pagine
			//-numero di "--" raccolti
			//-numero di valori unici
			//creo una mappa contenuto_numero di volte incontrato
			//se il numero di volte incontrato supera 3, tolgo l'elemento dalla mappa
			if (nl.getLength() != 0) {
				String currentContent = nl.item(0).getTextContent();
				Integer volte = contenuto2volte.get(currentContent);
				if (volte == null)
					volte = 0;
				volte++;
				if (volte <= 3)
					contenuto2volte.put(currentContent, volte);
				else
					contenuto2volte.remove(currentContent);
			}
			else	{ //l'xpath non ha restituito nessun segmento
				numeroPagineSenzaContenuto++;
			}
		}
		//ora valutiamo
		//se il numero di pagine senza contenuto è minore del 50%
		System.out.println("numeroPagineSenzaContenuto "+numeroPagineSenzaContenuto);
		System.out.println("contenuto "+contenuto2volte.size());
		if (numeroPagineSenzaContenuto < (100/2)) {
			//se il numero di valori unici è maggiore del 65%
			if (contenuto2volte.size() >= (65)) {
				//				System.out.println("significativo!!");
				xpath2isIdentificativa.put(genericXpath.getXpath(), true);
				return true;
			}
		}
		//		System.out.println("non significativo");
		xpath2isIdentificativa.put(genericXpath.getXpath(), false);
		return false;
	}

}



//public static Tuple2<Xpath, Xpath> getXpaths(Segment firstSegment, Segment secondSegment,
//WebPageDocument doc3, WebPageDocument doc4, float score,
//List<Segment> relevantSegments_thirdDocument,
//List<Segment> relevantSegments_fourthDocument, 
//List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
//String indexPath) throws XPathExpressionException, IOException, ParserConfigurationException {
////creazione degli xpath generici
////OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
//Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
//if (genericXpath_firstSegment == null) {
////generi un xpath generico
//firstSegment.makeXpathVersions();
//int specificityParameter = 0;
//boolean onlyOneSegmentFound = false;
//while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//	Xpath currentXpath = (new Xpath(firstSegment.getJsoupNode(),firstSegment
//			.getXpathVersions().getPathBySpecificity(specificityParameter),
//			firstSegment.getDocument().getIdDomain(),specificityParameter));
//	//se corrisponde a 1 unico segmento RILEVANTE
//	if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
//		onlyOneSegmentFound = true;
//
//
//		genericXpath_firstSegment = currentXpath;
//	}
//	else
//		specificityParameter++;
//}
//}
//Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
//if (genericXpath_secondSegment == null) {
////generi un xpath generico
//secondSegment.makeXpathVersions();
//int specificityParameter = 0;
//boolean onlyOneSegmentFound = false;
//while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//	Xpath currentXpath = (new Xpath(secondSegment.getJsoupNode(),secondSegment
//			.getXpathVersions().getPathBySpecificity(specificityParameter)
//			,secondSegment.getDocument().getIdDomain(),specificityParameter));
//	//se corrisponde a 1 unico segmento RILEVANTE
//	if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
//		onlyOneSegmentFound = true;
//
//
//		genericXpath_secondSegment = currentXpath;
//	}
//	else
//		specificityParameter++;
//}
//}
//if (genericXpath_firstSegment != null && genericXpath_secondSegment != null) {
////CONTROLLO AGGIUNTIVO: i segmenti ottenuti da questi xpath generici sono stati matchati
////per alta coseno similarità nell'insieme segment2hits_secondaPersona
//if (isARelevantMatching(genericXpath_firstSegment.getXpath(), doc3, 
//		genericXpath_secondSegment.getXpath(), doc4, 
//		segment2hits_secondaPersona, indexPath)) {
//
//	//li restituisci
//	return new Tuple2<>(genericXpath_firstSegment,genericXpath_secondSegment);
//}
//}
//return null;
//}

//public static void getSegmentsFrom(WebPageDocument firstDocument,
//WebPageDocument secondDocument, WebPageDocument thirdDocument,
//WebPageDocument fourthDocument, String cartella_primaPersona, String cartella_secondaPersona,
//int n1, int n2, int n3, int n4) throws Exception {
//
//TopSegmentsFinder finder = TopSegmentsFinder.getInstance();
//
//String indexPathDominio1 = cartella_primaPersona+"segmentIndex";
//
//// andrebbe tolto NO non va tolto e cerca di capire perché!
//File indexFolder = new File(indexPathDominio1);
//String[]entries = indexFolder.list();
//
////eliminazione dell'indice
//if (entries != null) {
//for(String s: entries){
//	File currentFile = new File(indexFolder.getPath(),s);
//	currentFile.delete();
//}
//}
//
////passo 1: prendere la pagina da segmentare
//
//List<Tuple2<Segment, TopDocs>> segment2hits_primaPersona =
//	finder.findTopSegments(cartella_primaPersona, firstDocument, secondDocument,
//			n1, n2);
//
//// questo set relevance dovrebbe essere a un livello di astrazione più basso, cioè incorporato
////in finder
//finder.setRelevances(segment2hits_primaPersona, secondDocument, indexPathDominio1);
//
//// anche questo
////gli index sono stati messi ARBITRARIAMENTE nelle due cartelle, una vale l'altra
//String indexPathDominio2 = cartella_secondaPersona+"segmentIndex";
//
////stesso procedimento, con un'altra coppia di pagine degli stessi domini
////eliminazione dell'indice
//indexFolder = new File(indexPathDominio2);
//entries = indexFolder.list();
//
//
//if (entries != null) {
//for(String s: entries){
//	File currentFile = new File(indexFolder.getPath(),s);
//	currentFile.delete();
//}
//}
//
//List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona =
//	finder.findTopSegments(cartella_secondaPersona, thirdDocument, fourthDocument,
//			n3, n4);
//
//finder.setRelevances(segment2hits_secondaPersona, fourthDocument, indexPathDominio2);
//
//
//List<Segment> relevantSegments_thirdDocument = new ArrayList<>();
//for (int i=0; i<segment2hits_secondaPersona.size(); i++) {
//Segment currentSegment = segment2hits_secondaPersona.get(i)._1();
//if (currentSegment.getRelevance() > 0) {
//	relevantSegments_thirdDocument.add(currentSegment);
//}
//}
//
//List<Segment> relevantSegments_fourthDocument = new ArrayList<>();
//Iterator<Segment> fourthDocumentSegmentsIt = fourthDocument.getSegments().iterator();
//while (fourthDocumentSegmentsIt.hasNext()) {
//Segment currentSegment = fourthDocumentSegmentsIt.next();
//if (currentSegment.getRelevance() > 0) {
//	relevantSegments_fourthDocument.add(currentSegment);
//}
//}
//
//// in un file di configurazione
//double threshold = 0.6;
//
//SegmentSearcher searcher = new SegmentSearcher(indexPathDominio1);
//for (int j=0; j<segment2hits_primaPersona.size(); j++) {
//Segment seg = segment2hits_primaPersona.get(j)._1();
//TopDocs hits = segment2hits_primaPersona.get(j)._2();
//for(ScoreDoc scoreDoc : hits.scoreDocs) {
//	if (scoreDoc.score >= threshold) {
//		org.apache.lucene.document.Document lucDoc = null;
//		try {
//			lucDoc = searcher.getDocument(scoreDoc);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Segment seg_secondDocument = secondDocument.getSegmentByXpath(lucDoc.get("segmentPath"));
//		addLink(seg, seg_secondDocument, thirdDocument, fourthDocument, scoreDoc.score,
//				relevantSegments_thirdDocument, relevantSegments_fourthDocument,
//				segment2hits_secondaPersona, indexPathDominio2);
//	}
//}
//}
//
////poi coloriamo la prima pagina con i matching rilevanti
//XpathApplier xapplier = XpathApplier.getInstance();
//
//org.w3c.dom.Document firstDocumentWithRelevance = xapplier
//	.colorRelevance(firstDocument.getSegments(), firstDocument.getDocument_jsoup());
//if (firstDocumentWithRelevance != null) {
//PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n1+".html", "UTF-8");
//testPrinter.println(DocumentUtils.getStringFromDocument(firstDocumentWithRelevance));
//testPrinter.close();
//}
//
//org.w3c.dom.Document thirdDocumentWithRelevance = xapplier
//	.colorRelevance(thirdDocument.getSegments(), thirdDocument.getDocument_jsoup());
//if (thirdDocumentWithRelevance != null) {
//PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n3+".html", "UTF-8");
//testPrinter.println(DocumentUtils.getStringFromDocument(thirdDocumentWithRelevance));
//testPrinter.close();
//}
//
////ora proviamo a fare la stessa cosa per il dominio 2
//org.w3c.dom.Document secondDocumentWithRelevance = xapplier
//	.colorRelevance(secondDocument.getSegments(), secondDocument.getDocument_jsoup());
//if (secondDocumentWithRelevance != null) {
//PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n2+".html", "UTF-8");
//testPrinter.println(DocumentUtils.getStringFromDocument(secondDocumentWithRelevance));
//testPrinter.close();
//}
//
//org.w3c.dom.Document fourthDocumentWithRelevance = xapplier
//	.colorRelevance(fourthDocument.getSegments(), fourthDocument.getDocument_jsoup());
//if (fourthDocumentWithRelevance != null) {
//PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n4+".html", "UTF-8");
//testPrinter.println(DocumentUtils.getStringFromDocument(fourthDocumentWithRelevance));
//testPrinter.close();
//}
//} //fine main
