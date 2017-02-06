//package other;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.xpath.XPathExpressionException;
//
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
////import org.jsoup.nodes.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import lib.utils.DocumentUtils;
//import lucene.SegmentSearcher;
//import model.DomainSource;
//import model.Segment;
//import model.WebPageDocument;
//import model.Xpath;
//import scala.Tuple2;
//import segmentation.TopSegmentsFinder;
//import xpath.utils.XpathApplier;
//
//public class DomainsWrapper {
//
//	//	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/testGenericXpath/";
//	//	static int n1 = 1;
//	//	static int n2 = 2;
//	//	static int n3 = 3;
//	//	static int n4 = 4;
//	//	static int parN1 = 1;
//	//	static int parN2 = 2;
//	//	static double parameterTextFusion = -1;
//
//	public static void getSegmentsFrom(WebPageDocument firstDocument,
//			WebPageDocument secondDocument, WebPageDocument thirdDocument,
//			WebPageDocument fourthDocument, String cartella_primaPersona, String cartella_secondaPersona,
//			int n1, int n2, int n3, int n4) throws Exception {
//		//
//		//		String file = IOUtils.toString(new FileReader(new File(path + "webpages.txt")));
//		//
//		//		String[] folders = file.split("\n");
//		//
//		TopSegmentsFinder finder = TopSegmentsFinder.getInstance();
//		//
//		//		for (int k=0; k<folders.length;k++) {
//		//			String cartella = folders[k];
//		//
//		//			System.out.println("CARTELLA CORRENTE: "+cartella);
//		//
//		//			String d1Path = cartella+"orig"+n1+".html";
//		//			String d2Path = cartella+"orig"+n2+".html";
//		//
//		//			String d3Path = cartella+"orig"+n3+".html";
//		//			String d4Path = cartella+"orig"+n4+".html";
//		//
//		//
//		//			File d1 = new File(d1Path);
//		//			File d2 = new File(d2Path);
//		//
//		//			File d3 = new File(d3Path);
//		//			File d4 = new File(d4Path);
//		//
//		//			if (d1.exists() && d2.exists() && d3.exists() && d4.exists()) {
//		//				System.out.println("Trovati documenti");
//		//
//		String indexPathDominio1 = cartella_primaPersona+"segmentIndex";
//
//		File indexFolder = new File(indexPathDominio1);
//		String[]entries = indexFolder.list();
//
//
//		//eliminazione dell'indice
//		if (entries != null) {
//			System.out.println("deleting previous index");
//			for(String s: entries){
//				File currentFile = new File(indexFolder.getPath(),s);
//				currentFile.delete();
//			}
//		}
//
//
//		//passo 1: prendere la pagina da segmentare
//
//		List<Tuple2<Segment, TopDocs>> segment2hits_primaPersona =
//				finder.findTopSegments(cartella_primaPersona, firstDocument, secondDocument,
//						n1, n2);
//
//		finder.setRelevances(segment2hits_primaPersona, secondDocument, indexPathDominio1);
//
//		//gli index sono stati messi ARBITRARIAMENTE nelle due cartelle, una vale l'altra
//		String indexPathDominio2 = cartella_secondaPersona+"segmentIndex";
//
//		//stesso procedimento, con un'altra coppia di pagine degli stessi domini
//		//eliminazione dell'indice
//		indexFolder = new File(indexPathDominio2);
//		entries = indexFolder.list();
//
//		if (entries != null) {
//			System.out.println("deleting previous index");
//			for(String s: entries){
//				File currentFile = new File(indexFolder.getPath(),s);
//				currentFile.delete();
//			}
//		}
//
//		List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona =
//				finder.findTopSegments(cartella_secondaPersona, thirdDocument, fourthDocument,
//						n3, n4);
//
//		finder.setRelevances(segment2hits_secondaPersona, fourthDocument, indexPathDominio2);
//
//
//		//creo, per efficienza, una lista di soli segmenti rilevanti, per il primo documento della prima
//		//coppia e della seconda coppia
//		//		List<Segment> relevantSegments_firstDocument = new ArrayList<>();
//		//		for (int i=0; i<segment2hits_primaPersona.size(); i++) {
//		//			Segment currentSegment = segment2hits_primaPersona.get(i)._1();
//		//			if (currentSegment.getRelevance() > 0) {
//		//				if (getGenericXpath(currentSegment, firstDocument) == null) {
//		//					relevantSegments_firstDocument.add(currentSegment);
//		//				}
//		//			}
//		//		}
//
//		//		List<Segment> relevantSegments_secondDocument = new ArrayList<>();
//		//		Iterator<Segment> secondDocumentSegmentsIt = secondDocument.getSegments().iterator();
//		//		while (secondDocumentSegmentsIt.hasNext()) {
//		//			Segment currentSegment = secondDocumentSegmentsIt.next();
//		//			if (currentSegment.getRelevance() > 0) {
//		//				if (getGenericXpath(currentSegment, secondDocument) == null){
//		//					relevantSegments_secondDocument.add(currentSegment);
//		//				}
//		//			}
//		//		}
//
//		List<Segment> relevantSegments_thirdDocument = new ArrayList<>();
//		for (int i=0; i<segment2hits_secondaPersona.size(); i++) {
//			Segment currentSegment = segment2hits_secondaPersona.get(i)._1();
//			if (currentSegment.getRelevance() > 0) {
//				//				if (getGenericXpath(currentSegment, thirdDocument) == null){
//				relevantSegments_thirdDocument.add(currentSegment);
//				//				}
//			}
//		}
//
//		List<Segment> relevantSegments_fourthDocument = new ArrayList<>();
//		Iterator<Segment> fourthDocumentSegmentsIt = fourthDocument.getSegments().iterator();
//		while (fourthDocumentSegmentsIt.hasNext()) {
//			Segment currentSegment = fourthDocumentSegmentsIt.next();
//			if (currentSegment.getRelevance() > 0) {
//				//				if (getGenericXpath(currentSegment, fourthDocument) == null){
//				relevantSegments_fourthDocument.add(currentSegment);
//				//				}
//			}
//		}
//
//		double threshold = 0.6;
//		SegmentSearcher searcher = new SegmentSearcher(indexPathDominio1);
//		for (int j=0; j<segment2hits_primaPersona.size(); j++) {
//			Segment seg = segment2hits_primaPersona.get(j)._1();
//			TopDocs hits = segment2hits_primaPersona.get(j)._2();
//			for(ScoreDoc scoreDoc : hits.scoreDocs) {
//				if (scoreDoc.score >= threshold) {
//					org.apache.lucene.document.Document lucDoc = null;
//					try {
//						lucDoc = searcher.getDocument(scoreDoc);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					Segment seg_secondDocument = secondDocument.getSegmentByXpath(lucDoc.get("segmentPath"));
//					//setto le rilevanze
//					//					seg.setRelevance(seg.getRelevance()+1);
//					//					seg_secondDocument.setRelevance(seg_secondDocument.getRelevance()+1);
//					//aggiungiamo collegamento
//					addLink(seg, seg_secondDocument, thirdDocument, fourthDocument,
//							relevantSegments_thirdDocument, relevantSegments_fourthDocument,
//							segment2hits_secondaPersona, indexPathDominio2);
//				}
//			}
//		}
//
//		//processo di generalizzazione del dominio 1
//		//applico una versione per una alla pagina 3 (dalla più specifica)
//		//quando una versione restituisce 1 e 1 solo segmento rilevante, mi fermo e accetto
//		//quell'xpath per quel segmento
//		//		Document doc3 = thirdDocument.getDocument();
//		//
//		//		Set<Xpath> genericXpaths = new HashSet<>();
//		//
//		//		for (int i=0; i<relevantSegments_firstDocument.size(); i++) {
//		//			Segment currentRelevantSegment = relevantSegments_firstDocument.get(i);
//		//			//ogni segmento rilevante ha diritto all'oggetto Xpath associato
//		//			//				System.out.println("Creating xpath versions for segment "+(i+1)+" of "+relevantSegments_firstDocument.size());
//		//
//		//			currentRelevantSegment.makeXpathVersions();
//		//			int specificityParameter = 0;
//		//			boolean onlyOneSegmentFound = false;
//		//			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//		//				//					System.out.println("Parametro specificità "+specificityParameter);
//		//				Xpath currentXpath = (new Xpath(currentRelevantSegment.getJsoupNode(),currentRelevantSegment
//		//						.getXpathVersions().getPathBySpecificity(specificityParameter)));
//		//				//se corrisponde a 1 unico segmento RILEVANTE
//		//				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
//		//					onlyOneSegmentFound = true;
//		//					//sovrascrivo l'xpath assoluto
//		//					currentRelevantSegment.setXPath(currentXpath, specificityParameter);
//		//					genericXpaths.add(currentXpath);
//		//				}
//		//				else
//		//					specificityParameter++;
//		//			}
//		//
//		//			if (genericXpaths.size()==0) System.out.println("Non ho aggiunto nulla :(");
//		//		}
//		//metto gli xpath nella pagina
//		//TODO sono solo quelli NUOVI trovati... forse potresti aggiungere anche quelli trovati...?
//		//		firstDocument.setGenericXPaths(genericXpaths);
//
//		//più che metterli tutti, devi aggiungere il controllo che non metti "doppioni"
//		//				firstDocument.getSource().setGenericXpaths(genericXpaths);
//		//		firstDocument.getSource().addGenericXpaths(genericXpaths);
//
//
//		//poi coloriamo la prima pagina con i matching rilevanti
//		//memorizziamo anche il tipo di specificità? perché no
//
//		XpathApplier xapplier = XpathApplier.getInstance();
//		//				org.w3c.dom.Document w3cFirstDocument = xapplier
//		//						.color(new HashSet<>(genericXpaths), firstDocument.getDocument());
//		//				if (w3cFirstDocument != null) {
//		//					PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"GenericSegments.html", "UTF-8");
//		//					testPrinter.println(DocumentUtils.getStringFromDocument(w3cFirstDocument));
//		//					testPrinter.close();
//		//				}
//		//
//		org.w3c.dom.Document firstDocumentWithRelevance = xapplier
//				.colorRelevance(firstDocument.getSegments(), firstDocument.getDocument_jsoup());
//		if (firstDocumentWithRelevance != null) {
//			PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n1+".html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(firstDocumentWithRelevance));
//			testPrinter.close();
//		}
//
//		org.w3c.dom.Document thirdDocumentWithRelevance = xapplier
//				.colorRelevance(thirdDocument.getSegments(), thirdDocument.getDocument_jsoup());
//		if (thirdDocumentWithRelevance != null) {
//			PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n3+".html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(thirdDocumentWithRelevance));
//			testPrinter.close();
//		}
//
//
//		//ora proviamo a fare la stessa cosa per il dominio 2
//
//		//		Document doc4 = fourthDocument.getDocument();
//		//
//		//		genericXpaths = new HashSet<>();
//		//
//		//		for (int i=0; i<relevantSegments_secondDocument.size(); i++) {
//		//			Segment currentRelevantSegment = relevantSegments_secondDocument.get(i);
//		//			//ogni segmento rilevante ha diritto all'oggetto Xpath associato
//		//			//				System.out.println("Creating xpath versions for segment "+(i+1)+" of "+relevantSegments_firstDocument.size());
//		//
//		//			currentRelevantSegment.makeXpathVersions();
//		//			int specificityParameter = 0;
//		//			boolean onlyOneSegmentFound = false;
//		//			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//		//				//					System.out.println("Parametro specificità "+specificityParameter);
//		//				Xpath currentXpath = (new Xpath(currentRelevantSegment.getJsoupNode(),currentRelevantSegment
//		//						.getXpathVersions().getPathBySpecificity(specificityParameter)));
//		//				//se corrisponde a 1 unico segmento RILEVANTE
//		//				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
//		//					onlyOneSegmentFound = true;
//		//					//sovrascrivo l'xpath assoluto
//		//					currentRelevantSegment.setXPath(currentXpath, specificityParameter);
//		//					genericXpaths.add(currentXpath);
//		//				}
//		//				else
//		//					specificityParameter++;
//		//			}
//		//
//		//			if (genericXpaths.size()==0) System.out.println("Non ho aggiunto nulla :(");
//		//
//		//		}
//		//		//metto gli xpath nella pagina
//		//		secondDocument.setGenericXPaths(genericXpaths);
//		//
//		//		//più che metterli tutti, devi aggiungere il controllo che non metti "doppioni"
//		//		//				secondDocument.getSource().setGenericXpaths(genericXpaths);
//		//		secondDocument.getSource().addGenericXpaths(genericXpaths);
//
//
//
//		//poi coloriamo la prima pagina con i matching rilevanti
//		//memorizziamo anche il tipo di specificità? perché no
//
//
//		//				org.w3c.dom.Document w3cSecondDocument = xapplier
//		//						.color(new HashSet<>(genericXpaths), secondDocument.getDocument());
//		//				if (w3cSecondDocument != null) {
//		//					PrintWriter testPrinter = new PrintWriter(cartella+"GenericSegments2.html", "UTF-8");
//		//					testPrinter.println(DocumentUtils.getStringFromDocument(w3cSecondDocument));
//		//					testPrinter.close();
//		//				}
//		//
//		org.w3c.dom.Document secondDocumentWithRelevance = xapplier
//				.colorRelevance(secondDocument.getSegments(), secondDocument.getDocument_jsoup());
//		if (secondDocumentWithRelevance != null) {
//			PrintWriter testPrinter = new PrintWriter(cartella_primaPersona+"Relevance"+n2+".html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(secondDocumentWithRelevance));
//			testPrinter.close();
//		}
//
//		org.w3c.dom.Document fourthDocumentWithRelevance = xapplier
//				.colorRelevance(fourthDocument.getSegments(), fourthDocument.getDocument_jsoup());
//		if (fourthDocumentWithRelevance != null) {
//			PrintWriter testPrinter = new PrintWriter(cartella_secondaPersona+"Relevance"+n4+".html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(fourthDocumentWithRelevance));
//			testPrinter.close();
//		}
//
//		//			}//fine if d1 & d2 exists
//		//		} //fine folder
//	} //fine main
//
//	//di tutti i segmenti rilevanti, ora devo eliminare quelli che, per il dominio, esistono già
//	//ciò vuol dire che applico gli xpath generici del dominio sulle nuove pagine
//	//(dominio 1 per le pagine 1 e 3, dominio 2 per le pagine 2 e 4)
//	//ogni xpath restituisce 0,1,n nodi. se almeno 1, controllo se tale nodo E' PRESENTE tra
//	//i segmenti rilevanti
//	//SE SÌ ignoro quel segmento. cioè lo tolgo dall'insieme dei segmenti rilevanti
//	//SE NO tengo quel segmento nel set dei segmenti rilevanti
//	public static Xpath getGenericXpath(Segment segment, WebPageDocument webPageDocument) throws XPathExpressionException, IOException {
//		DomainSource domain = webPageDocument.getSource();
//		Set<Xpath> domainGenericXpath = domain.getGenericXpaths();
//		XpathApplier xapplier = XpathApplier.getInstance();
//		Iterator<Xpath> genericXpathIt = domainGenericXpath.iterator();
//		while (genericXpathIt.hasNext()) {
//			Xpath currentGenericXpath = genericXpathIt.next();
//			String path = currentGenericXpath.getXpath();
//			NodeList nl = xapplier.getNodes(path, webPageDocument.getDocument_jsoup());
//			//noi ne vogliamo 1 e 1 solo
//			if (nl.getLength() == 1) {
//				if (areEqualNodes(nl.item(0), segment.getW3cNodes().item(0))) {
//					//					System.out.println("*****************ottimo!!!************************");
//					return currentGenericXpath;
//				}
//			}
//		}
//		//		System.out.println("----------------nuovo!!!------------------");
//		return null;
//	}
//
//	private static boolean isARelevantSegment(String xpath, WebPageDocument document, List<Segment> relevantSegments) throws XPathExpressionException, IOException, ParserConfigurationException {
//		//		System.out.println(xpath);
//		if (xpath.equals("")) 
//			return false;
//		XpathApplier xapplier = XpathApplier.getInstance();
//		NodeList xpathNodes = xapplier.getNodes(xpath, document.getDocument_jsoup());
//		//		System.out.println("nodi matchati "+ xpathNodes.getLength());
//		if (xpathNodes.getLength()!=1)
//			return false;
//		Node xpathNode = xpathNodes.item(0);
//		//controlliamo che il nodo restituito corrisponda a un segmento rilevante del documento 3
//		for (int i=0;i<relevantSegments.size();i++) {
//			Segment relevantSegment = relevantSegments.get(i);
//			if (areEqualNodes(xpathNode, relevantSegment.getW3cNodes().item(0))) {
//				return true;
//			}
//		}
//		//nessun matching
//		return false;
//	}
//
//	/*controlla che i segmenti siano presenti in un matching*/
//	private static boolean isARelevantMatching(String xpath1, WebPageDocument doc1,
//			String xpath2,WebPageDocument doc2,
//			List<Tuple2<Segment, TopDocs>> segment2hits, String indexPath)
//					throws XPathExpressionException, IOException, ParserConfigurationException {
//		//		System.out.println(xpath);
//		if (xpath1.equals("") || xpath1.equals("")) 
//			return false;
//		XpathApplier xapplier = XpathApplier.getInstance();
//		NodeList xpathNodes1 = xapplier.getNodes(xpath1, doc1.getDocument_jsoup());
//		NodeList xpathNodes2 = xapplier.getNodes(xpath2, doc2.getDocument_jsoup());
//		//		System.out.println("nodi matchati "+ xpathNodes.getLength());
//		if (xpathNodes1.getLength()!=1 || xpathNodes2.getLength()!=1)
//			return false;
//		Node xpathNode1 = xpathNodes1.item(0);
//		Node xpathNode2 = xpathNodes2.item(0);
//
//		for (int i=0;i<segment2hits.size();i++) {
//			Tuple2<Segment, TopDocs> segment2hit = segment2hits.get(i);
//			if (areEqualNodes(xpathNode1, segment2hit._1().getW3cNodes().item(0))) {
//				//				System.out.println("TROVATO 1° SEGMENTO");
//				//abbiamo individuato il segmento del primo documento, ora dobbiamo trovare il segmento
//				//del secondo documento
//				SegmentSearcher searcher = new SegmentSearcher(indexPath);
//				TopDocs hits = segment2hit._2();
//				for(ScoreDoc scoreDoc : hits.scoreDocs) {
//					if (scoreDoc.score >= 0.6) {
//						org.apache.lucene.document.Document lucDoc = null;
//						try {
//							lucDoc = searcher.getDocument(scoreDoc);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						//setto la rilevanza dei segmenti del secondo documento
//						Segment seg_secondDocument = doc2.getSegmentByXpath(lucDoc.get("segmentPath"));
//						if (areEqualNodes(xpathNode2, seg_secondDocument.getW3cNodes().item(0))) {
//							//							System.out.println("TROVATO 2° SEGMENTO");
//							return true;
//						}
//					}
//				}
//				//nessun matching nelle hits
//				return false;
//			}
//		}
//		//nessun matching
//		return false;
//	}
//	
//	public static boolean areEqualNodes(Node n1, Node n2) {
//		if (n1 != null) {
//			if (n1.isEqualNode(n2)) {
//				//							if (n1.getParentNode().isEqualNode(n2.getParentNode())) {
//				if (areEqualNodes(n1.getParentNode(),n2.getParentNode())) {
//					Node siblingN1 = n1.getNextSibling();
//					Node siblingN2 = n2.getNextSibling();
//					//					if (siblingN1 != null)
//					//						System.out.println("fratello1 "+ siblingN1.getTextContent());
//					//					else
//					//						System.out.println("fratello1 null");
//					//					if (siblingN2 != null)
//					//						System.out.println("fratello2 "+ siblingN2.getTextContent());
//					//					else
//					//						System.out.println("fratello2 null");
//					//					if (siblingN1 != null && siblingN2 != null) 
//					//						System.out.println(siblingN1.isEqualNode(siblingN2));
//					if ((siblingN1 == null && siblingN2 != null)
//							|| (siblingN1 != null && siblingN2 == null)) {
//						//						System.out.println("Uno dei due fratelli è nullo");
//						return false;
//					}
//					if (siblingN1 == null && siblingN2 == null) {
//						//						System.out.println("Entrambi i fratelli sono nulli");
//						return true;
//					}
//					//					if (siblingN1.isEqualNode(siblingN2) && areEqualNodes(siblingN1, siblingN2)) {
//					if (siblingN1.isEqualNode(siblingN2)) {
//						List<Node> nextSiblingsN1 = new ArrayList<>();
//						getNextSiblings(n1, nextSiblingsN1);
//						List<Node> nextSiblingsN2 = new ArrayList<>();
//						getNextSiblings(n2, nextSiblingsN2);
//						if (areEqualListOfNodes(nextSiblingsN1, nextSiblingsN2)) {
//							List<Node> previousSiblingsN1 = new ArrayList<>();
//							getPreviousSiblings(n1, previousSiblingsN1);
//							List<Node> previousSiblingsN2 = new ArrayList<>();
//							getPreviousSiblings(n2, previousSiblingsN2);
//							if (areEqualListOfNodes(previousSiblingsN1, previousSiblingsN2)) {
//								//							System.out.println("I fratelli sono uguali");
//								return true;
//							}
//						}
//					}
//				}
//			}
//			return false;
//		}
//		return n2 == null;
//	}
//
//	//riempie la lista passata con i fratelli di destra del nodo, NON compreso il nodo stesso
//	public static void getNextSiblings(Node n, List<Node> nextSiblingsList) {
//		Node nextSibling = n.getNextSibling();
//		if (nextSibling == null)
//			return;
//		nextSiblingsList.add(nextSibling);
//		getNextSiblings(nextSibling, nextSiblingsList);
//	}
//
//	//riempie la lista passata con i fratelli di sinistra del nodo, NON compreso il nodo stesso
//	public static void getPreviousSiblings(Node n, List<Node> previousSiblingsList) {
//		Node prevSibling = n.getPreviousSibling();
//		if (prevSibling == null)
//			return;
//		previousSiblingsList.add(prevSibling);
//		getNextSiblings(prevSibling, previousSiblingsList);
//	}
//	
//	//confronta se due liste hanno gli stessi nodi
//	public static boolean areEqualListOfNodes(List<Node> list1, List<Node> list2) {
//		if (list1.size() != list2.size()) return false;
//		for (int i=0; i<list1.size(); i++) {
//			Node n1 = list1.get(i);
//			Node n2 = list2.get(i);
//			if (!(n1.isEqualNode(n2)))
//				return false;
//		}
//		return true;
//	}
//
//	//più che altro, ogni volta che supera la soglia,
//	//PRIMA controlli che per quel segmento non sia già stato generato un xpath generico
//	//SE SÌ allora metti quello nella coppia collegamento
//	//SE NO generi un xpath generico
//	private static void addLink(Segment firstSegment, Segment secondSegment,
//			WebPageDocument doc3, WebPageDocument doc4,
//			List<Segment> relevantSegments_thirdDocument,
//			List<Segment> relevantSegments_fourthDocument,
//			List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona,
//			String indexPath) throws XPathExpressionException, IOException, ParserConfigurationException {
//		//creazione degli xpath generici
//		//controllo che i due segmenti non abbiano già un xpath generico
//		Xpath genericXpath_firstSegment = getGenericXpath(firstSegment, firstSegment.getDocument());
//		if (genericXpath_firstSegment == null) {
//			//generi un xpath generico
//			firstSegment.makeXpathVersions();
//			int specificityParameter = 0;
//			boolean onlyOneSegmentFound = false;
//			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//				//					System.out.println("Parametro specificità "+specificityParameter);
//				Xpath currentXpath = (new Xpath(firstSegment.getJsoupNode(),firstSegment
//						.getXpathVersions().getPathBySpecificity(specificityParameter)));
//				//se corrisponde a 1 unico segmento RILEVANTE
//				if (isARelevantSegment(currentXpath.getXpath(), doc3, relevantSegments_thirdDocument)) {
//					onlyOneSegmentFound = true;
//					//sovrascrivo l'xpath assoluto
//					firstSegment.setXPath(currentXpath, specificityParameter);
//					firstSegment.getDocument().getSource().addGenericXpath(currentXpath);
//					genericXpath_firstSegment = currentXpath;
//				}
//				else
//					specificityParameter++;
//			}
//		}
//		Xpath genericXpath_secondSegment = getGenericXpath(secondSegment, secondSegment.getDocument());
//		if (genericXpath_secondSegment == null) {
//			//generi un xpath generico
//			secondSegment.makeXpathVersions();
//			int specificityParameter = 0;
//			boolean onlyOneSegmentFound = false;
//			while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//				//					System.out.println("Parametro specificità "+specificityParameter);
//				Xpath currentXpath = (new Xpath(secondSegment.getJsoupNode(),secondSegment
//						.getXpathVersions().getPathBySpecificity(specificityParameter)));
//				//se corrisponde a 1 unico segmento RILEVANTE
//				if (isARelevantSegment(currentXpath.getXpath(), doc4, relevantSegments_fourthDocument)) {
//					onlyOneSegmentFound = true;
//					//sovrascrivo l'xpath assoluto
//					secondSegment.setXPath(currentXpath, specificityParameter);
//					secondSegment.getDocument().getSource().addGenericXpath(currentXpath);
//					genericXpath_secondSegment = currentXpath;
//				}
//				else
//					specificityParameter++;
//			}
//		}
//		if (genericXpath_firstSegment != null && genericXpath_secondSegment != null) {
//			//CONTROLLO AGGIUNTIVO: i segmenti ottenuti da questi xpath generici sono stati matchati
//			//per alta coseno similarità nell'insieme segment2hits_secondaPersona
//			if (isARelevantMatching(genericXpath_firstSegment.getXpath(), doc3, 
//					genericXpath_secondSegment.getXpath(), doc4, 
//					segment2hits_secondaPersona, indexPath)) {
//				//				System.out.println("SONO QUIIII");
//				//una volta che hai i generici di entrambi, crei collegamento
//				//		Matching m = new Matching(firstSegment.getDocument().getSource(), genericXpath_firstSegment,
//				//				secondSegment.getDocument().getSource(), genericXpath_secondSegment);
//				MatchingRepository mr = MatchingRepository.getInstance();
//				mr.addMatching(firstSegment.getDocument().getSource(), genericXpath_firstSegment,
//						secondSegment.getDocument().getSource(), genericXpath_secondSegment);
//				//				System.out.println(mr.getMatchings().size());
//			}
//		}
//	}
//}
//
