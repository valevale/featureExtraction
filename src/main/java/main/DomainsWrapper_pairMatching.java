package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lib.utils.DocumentUtils;
import lib.utils.NodeW3cUtils;
import lucene.SegmentSearcher;
import model.DomainSource;
import model.PairMatchingRepositoryRepository;
import model.Segment;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;
import segmentation.TopSegmentsFinder;
import xpath.utils.XpathApplier;

/* estraiamo da un documento, in comparazione con altri 3 (uno della stessa persona di un altro dominio,
 * uno di un'altra persona con lo stesso dominio, e un ultimo della stessa altra persona e dello stesso
 * altro dominio), le coppie di segmenti del documento 1 e 2 simili per coseno similarità
 * le coppie vengono messe in un repository e si calcola il loro voto, basato sulla coseno smilarità */
public class DomainsWrapper_pairMatching {

	public static void getSegmentsFrom(WebPageDocument firstDocument,
			WebPageDocument secondDocument, WebPageDocument thirdDocument,
			WebPageDocument fourthDocument, String cartella_primaPersona, String cartella_secondaPersona,
			int n1, int n2, int n3, int n4) throws Exception {

		TopSegmentsFinder finder = TopSegmentsFinder.getInstance();

		String indexPathDominio1 = cartella_primaPersona+"segmentIndex";

		//TODO andrebbe tolto NO non va tolto e cerca di capire perché!
		File indexFolder = new File(indexPathDominio1);
		String[]entries = indexFolder.list();

		//eliminazione dell'indice
		if (entries != null) {
			for(String s: entries){
				File currentFile = new File(indexFolder.getPath(),s);
				currentFile.delete();
			}
		}

		//passo 1: prendere la pagina da segmentare

		List<Tuple2<Segment, TopDocs>> segment2hits_primaPersona =
				finder.findTopSegments(cartella_primaPersona, firstDocument, secondDocument,
						n1, n2);

		//TODO questo set relevance dovrebbe essere a un livello di astrazione più basso, cioè incorporato
		//in finder
		finder.setRelevances(segment2hits_primaPersona, secondDocument, indexPathDominio1);

		//TODO anche questo
		//gli index sono stati messi ARBITRARIAMENTE nelle due cartelle, una vale l'altra
		String indexPathDominio2 = cartella_secondaPersona+"segmentIndex";

		//stesso procedimento, con un'altra coppia di pagine degli stessi domini
		//eliminazione dell'indice
		indexFolder = new File(indexPathDominio2);
		entries = indexFolder.list();


		if (entries != null) {
			for(String s: entries){
				File currentFile = new File(indexFolder.getPath(),s);
				currentFile.delete();
			}
		}

		List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona =
				finder.findTopSegments(cartella_secondaPersona, thirdDocument, fourthDocument,
						n3, n4);

		finder.setRelevances(segment2hits_secondaPersona, fourthDocument, indexPathDominio2);


		List<Segment> relevantSegments_thirdDocument = new ArrayList<>();
		for (int i=0; i<segment2hits_secondaPersona.size(); i++) {
			Segment currentSegment = segment2hits_secondaPersona.get(i)._1();
			if (currentSegment.getRelevance() > 0) {
				relevantSegments_thirdDocument.add(currentSegment);
			}
		}

		List<Segment> relevantSegments_fourthDocument = new ArrayList<>();
		Iterator<Segment> fourthDocumentSegmentsIt = fourthDocument.getSegments().iterator();
		while (fourthDocumentSegmentsIt.hasNext()) {
			Segment currentSegment = fourthDocumentSegmentsIt.next();
			if (currentSegment.getRelevance() > 0) {
				relevantSegments_fourthDocument.add(currentSegment);
			}
		}

		//TODO in un file di configurazione
		double threshold = 0.6;

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
					addLink(seg, seg_secondDocument, thirdDocument, fourthDocument, scoreDoc.score,
							relevantSegments_thirdDocument, relevantSegments_fourthDocument,
							segment2hits_secondaPersona, indexPathDominio2);
				}
			}
		}

		//poi coloriamo la prima pagina con i matching rilevanti
		XpathApplier xapplier = XpathApplier.getInstance();

		org.w3c.dom.Document firstDocumentWithRelevance = xapplier
				.colorRelevance(firstDocument.getSegments(), firstDocument.getDocument_jsoup());
		if (firstDocumentWithRelevance != null) {
			PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n1+".html", "UTF-8");
			testPrinter.println(DocumentUtils.getStringFromDocument(firstDocumentWithRelevance));
			testPrinter.close();
		}

		org.w3c.dom.Document thirdDocumentWithRelevance = xapplier
				.colorRelevance(thirdDocument.getSegments(), thirdDocument.getDocument_jsoup());
		if (thirdDocumentWithRelevance != null) {
			PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n3+".html", "UTF-8");
			testPrinter.println(DocumentUtils.getStringFromDocument(thirdDocumentWithRelevance));
			testPrinter.close();
		}

		//ora proviamo a fare la stessa cosa per il dominio 2
		org.w3c.dom.Document secondDocumentWithRelevance = xapplier
				.colorRelevance(secondDocument.getSegments(), secondDocument.getDocument_jsoup());
		if (secondDocumentWithRelevance != null) {
			PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n2+".html", "UTF-8");
			testPrinter.println(DocumentUtils.getStringFromDocument(secondDocumentWithRelevance));
			testPrinter.close();
		}

		org.w3c.dom.Document fourthDocumentWithRelevance = xapplier
				.colorRelevance(fourthDocument.getSegments(), fourthDocument.getDocument_jsoup());
		if (fourthDocumentWithRelevance != null) {
			PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n4+".html", "UTF-8");
			testPrinter.println(DocumentUtils.getStringFromDocument(fourthDocumentWithRelevance));
			testPrinter.close();
		}
	} //fine main

	//ogni volta che supera la soglia,
	//PRIMA controlli che per quel segmento non sia già stato generato un xpath generico
	//SE SÌ allora metti quello nella coppia collegamento
	//SE NO generi un xpath generico
	private static void addLink(Segment firstSegment, Segment secondSegment,
			WebPageDocument doc3, WebPageDocument doc4, float score,
			List<Segment> relevantSegments_thirdDocument,
			List<Segment> relevantSegments_fourthDocument, 
			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
			String indexPath) throws XPathExpressionException, IOException, ParserConfigurationException {
		//creazione degli xpath generici
		//OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
		if (genericXpath_firstSegment == null) {
			//generi un xpath generico
			firstSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = (new Xpath(firstSegment.getJsoupNode(),firstSegment
						.getXpathVersions().getPathBySpecificity(specificityParameter),
						firstSegment.getDocument().getIdDomain(),specificityParameter));
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					firstSegment.setXPath(currentXpath);
					//TODO lo aggiungo qui, quindi PRIMA del controllo finale. dovresti farlo dopo
					firstSegment.getDocument().getSource().addGenericXpath(currentXpath);
					genericXpath_firstSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
		if (genericXpath_secondSegment == null) {
			//generi un xpath generico
			secondSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = (new Xpath(secondSegment.getJsoupNode(),secondSegment
						.getXpathVersions().getPathBySpecificity(specificityParameter)
						,secondSegment.getDocument().getIdDomain(),specificityParameter));
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
					onlyOneSegmentFound = true;
					//sovrascrivo l'xpath assoluto
					secondSegment.setXPath(currentXpath);
					secondSegment.getDocument().getSource().addGenericXpath(currentXpath);
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
				//una volta che hai i generici di entrambi, crei collegamento
				PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
				pmr.addMatching(genericXpath_firstSegment, firstSegment.getDocument().getSource().getParameter(),
						genericXpath_secondSegment, secondSegment.getDocument().getSource().getParameter(), score);
			}
		}
	}
	
	public static Tuple2<Xpath, Xpath> getXpaths(Segment firstSegment, Segment secondSegment,
			WebPageDocument doc3, WebPageDocument doc4, float score,
			List<Segment> relevantSegments_thirdDocument,
			List<Segment> relevantSegments_fourthDocument, 
			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
			String indexPath) throws XPathExpressionException, IOException, ParserConfigurationException {
		//creazione degli xpath generici
		//OTTIMIZZAZIONE: controllo che i due segmenti non abbiano già un xpath generico
		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
		if (genericXpath_firstSegment == null) {
			//generi un xpath generico
			firstSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = (new Xpath(firstSegment.getJsoupNode(),firstSegment
						.getXpathVersions().getPathBySpecificity(specificityParameter),
						firstSegment.getDocument().getIdDomain(),specificityParameter));
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
					onlyOneSegmentFound = true;
					
					
					genericXpath_firstSegment = currentXpath;
				}
				else
					specificityParameter++;
			}
		}
		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
		if (genericXpath_secondSegment == null) {
			//generi un xpath generico
			secondSegment.makeXpathVersions();
			int specificityParameter = 0;
			boolean onlyOneSegmentFound = false;
			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
				Xpath currentXpath = (new Xpath(secondSegment.getJsoupNode(),secondSegment
						.getXpathVersions().getPathBySpecificity(specificityParameter)
						,secondSegment.getDocument().getIdDomain(),specificityParameter));
				//se corrisponde a 1 unico segmento RILEVANTE
				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
					onlyOneSegmentFound = true;
					
					
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
				
				//li restituisci
				return new Tuple2<>(genericXpath_firstSegment,genericXpath_secondSegment);
			}
		}
		return null;
	}

	//di tutti i segmenti rilevanti, ora devo eliminare quelli che, per il dominio, esistono già
	//ciò vuol dire che applico gli xpath generici del dominio sulle nuove pagine
	//(dominio 1 per le pagine 1 e 3, dominio 2 per le pagine 2 e 4)
	//ogni xpath restituisce 0,1,n nodi. se almeno 1, controllo se tale nodo E' PRESENTE tra
	//i segmenti rilevanti
	//SE SÌ ignoro quel segmento. cioè lo tolgo dall'insieme dei segmenti rilevanti
	//SE NO tengo quel segmento nel set dei segmenti rilevanti
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

	private static boolean isARelevantSegment(String xpath, WebPageDocument document, List<Segment> relevantSegments) throws XPathExpressionException, IOException, ParserConfigurationException {
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

	/*controlla che i segmenti siano presenti in un matching*/
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
						//setto la rilevanza dei segmenti del secondo documento
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
}
