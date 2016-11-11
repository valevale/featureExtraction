package otherSegmentation;
//package segmentation;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Node;
//
//import lib.utils.CleanHTMLTree;
//import lib.utils.MapUtils;
//import lib.utils.NodeUtils;
//import model.BlacklistElement;
//import model.Segment;
//
//public class Main3 {
//
//	static final String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/angelo_amoroso_aragona/";
//	static final String indexPath = cartella+"segmentIndex";
//
//	static final String n = "1-2";
//
//	static final String n1 = "orig";
//	static final String n2 = "orig2";
//
//	public static void main(String[] args) throws Exception {
//
//
//		//passo 1: prendere la pagina da segmentare
//		String html_firstPage = IOUtils.toString(new FileReader(new File(cartella+n1+".html")));
//		Document doc_firstPage = Jsoup.parse(html_firstPage);
//
//		Set<String> xPaths_firstPage = getXPathsFromDocument(doc_firstPage, "", n1);
//
//
//		String html_secondPage = IOUtils.toString(new FileReader(new File(cartella+n2+".html")));
//		Document doc_secondPage = Jsoup.parse(html_secondPage);
//
//		Set<String> xPaths_secondPage = getXPathsFromDocument(doc_secondPage, "2", n2);
//
//
//		//troviamo i segmenti corrispondenti
//		Set<Segment> firstPageSegments = getSegments(xPaths_firstPage, doc_firstPage);
//
//		Set<Segment> secondPageSegments = getSegments(xPaths_secondPage, doc_secondPage);
//
//		//passo 6: confrontare i segmenti con coseno similarità
//
//		//6.1 : crea vettori per i segmenti della prima pagina e indicizzali
//
//		firstPageSegments.addAll(secondPageSegments);
//		
//		SegmentIndexer indexer = new SegmentIndexer(indexPath);
//		int nRes = indexer.createIndex(firstPageSegments);
//		System.out.println("totale segmenti indicizzati per 1: "+nRes);
////		VectorGenerator vectorGenerator_firstPage = new VectorGenerator(indexPath1);
////		vectorGenerator_firstPage.GetAllTerms(indexPath1);
////		DocVector[] docVector_firstPage = vectorGenerator_firstPage.GetDocumentVectors();
//
//		//6.2 : ripeti con la pagina 2
//
////		SegmentIndexer indexer_secondPage = new SegmentIndexer(indexPath2);
////		nRes = indexer.createIndex(secondPageSegments);
//		indexer.close();
////		System.out.println("totale segmenti indicizzati per 2: "+nRes);
//		VectorGenerator vectorGenerator = new VectorGenerator(indexPath);
//		vectorGenerator.GetAllTerms(indexPath);
//		DocVector[] docVector = vectorGenerator.GetDocumentVectors();
//
//		//calcola coseno similarità
//
//		PrintWriter resPrinter = new PrintWriter(cartella+"resCosSim"+n+".txt", "UTF-8");
//
////		for (int i = 0; i < docVector.length; i++) {
////			System.out.println(i + " " + docVector[i]);
////		}
//		
//		for(int i = 0; i < docVector.length; i++) {
//			DocVector vector_firstPage = docVector[i];
//			resPrinter.println(vector_firstPage);
//			for(int j = 0; j < docVector.length; j++) {
//				DocVector vector_secondPage = docVector[j];
//				double cosineSimilarity = CosineSimilarity.getCosineSimilarity(vector_firstPage, vector_secondPage);
//				if (cosineSimilarity > 0) {
//					resPrinter.println(cosineSimilarity);
//					resPrinter.println(vector_secondPage);
//					resPrinter.println("-----");
//				}
//			} 
//
//			resPrinter.println("__________________________________");
//		}
//
//
//
//
//
//		//
//		//			//6.1 : indicizza la seconda pagina
//		//			SegmentIndexer indexer = new SegmentIndexer(indexPath);
//		//			int nRes = indexer.createIndex(secondPageSegments);
//		//			System.out.println("totale segmenti indicizzati: "+nRes);
//		//			indexer.close();
//		//			SegmentSearcher searcher = new SegmentSearcher(indexPath);
//		//			File indexFolder = new File(indexPath);
//		//			PrintWriter resPrinter = new PrintWriter(cartella+"res"+n+".txt", "UTF-8");
//		//
//		//
//		//			//6.2 : per ogni segmento della prima pagina, trovi i primi 10 segmenti simili
//		//			Iterator<Segment> segmentIterator = firstPageSegments.iterator();
//		//			Map<Segment, Float> seg2maxSim = new HashMap<>();
//		//			while (segmentIterator.hasNext()) {
//		//				Segment	firstPageSeg = segmentIterator.next();
//		//				System.out.println(NodeUtils.getNodesContent(firstPageSeg.getNodes())+"\n--\n");
//		//				TopDocs hits = null;
//		//				try {
//		//					hits = searcher.search(firstPageSeg);
//		//				} catch (Exception e) {
//		//					e.printStackTrace();
//		//				}
//		//				if (hits.totalHits > 0) {
//		//					seg2maxSim.put(firstPageSeg, hits.scoreDocs[0].score);
//		//				}
//		//			}
//		//
//		//			//ordina la mappa in base alla similarità
//		//			seg2maxSim = MapUtils.sortByValue(seg2maxSim);
//		//
//		//			Set<Segment> keySet = seg2maxSim.keySet();
//		//			segmentIterator = keySet.iterator();
//		//			while (segmentIterator.hasNext()) {
//		//				Segment	firstPageSeg = segmentIterator.next();
//		//				TopDocs hits = null;
//		//				try {
//		//					hits = searcher.search(firstPageSeg);
//		//				} catch (Exception e) {
//		//					e.printStackTrace();
//		//				}
//		//				if (hits.totalHits > 0) {
//		//					if (hits.scoreDocs[0].score >= 0.5) {
//		//						resPrinter.println(NodeUtils.getNodesContent(firstPageSeg.getNodes()));
//		//						resPrinter.println("---");
//		//						for(ScoreDoc scoreDoc : hits.scoreDocs) {
//		//							//TODO vedi se piuttosto che i top documents, puoi prendere quelli con soglia
//		//							//maggiore di un tot
//		//							if (scoreDoc.score >= 0.5) {
//		//								org.apache.lucene.document.Document lucDoc = null;
//		//								try {
//		//									lucDoc = searcher.getDocument(scoreDoc);
//		//								} catch (Exception e) {
//		//									e.printStackTrace();
//		//								}
//		//								resPrinter.println("-");
//		//								resPrinter.println(scoreDoc.score);
//		//								resPrinter.println(lucDoc.get("segmentContent"));
//		//								resPrinter.println(lucDoc.get("segmentPath"));
//		//								//così si prende invece l'xPath
//		//								//					Segment seg = new Segment(lucDoc.get("segmentPath"), doc);
//		//							}
//		//						}
//		//						resPrinter.println("__________________");
//		//					}
//		//				}
//		//			}
//		//
//		//
//
//
//
//		resPrinter.close();
//
//
//		File indexFolder_firstPage = new File(indexPath);
////		File indexFolder_secondPage = new File(indexPath2);
//		//eliminazione dell'indice
//		String[]entries_firstPage = indexFolder_firstPage.list();
//		for(String s: entries_firstPage){
//			File currentFile = new File(indexFolder_firstPage.getPath(),s);
//			currentFile.delete();
//		}
//
////		String[]entries_secondPage = indexFolder_secondPage.list();
////		for(String s: entries_secondPage){
////			File currentFile = new File(indexFolder_secondPage.getPath(),s);
////			currentFile.delete();
////		}
//
//		//TEST FINALE PER VEDERE SE COLORA GLI XPATH
//		//			XPathMaker xpMaker = XPathMaker.getInstance();
//		//			org.w3c.dom.Document test = xpMaker.color(xPaths_secondPage, doc_secondPage);
//		//			System.out.println(xPaths_secondPage.size());
//		//
//		//			PrintWriter testPrinter = new PrintWriter(cartella+"test.html", "UTF-8");
//		//			testPrinter.println(getStringFromDocument(test));
//		//			testPrinter.close();
//
//	}
//
//	//method to convert Document to String
//	public static String getStringFromDocument(org.w3c.dom.Document doc)
//	{
//		try
//		{
//			DOMSource domSource = new DOMSource(doc);
//			StringWriter writer = new StringWriter();
//			StreamResult result = new StreamResult(writer);
//			TransformerFactory tf = TransformerFactory.newInstance();
//			Transformer transformer = tf.newTransformer();
//			transformer.transform(domSource, result);
//			return writer.toString();
//		}
//		catch(TransformerException ex)
//		{
//			ex.printStackTrace();
//			return null;
//		}
//	} 
//
//
//
//	public static Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {
//
//		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(docToClean, "txt", usedPagesForCleaning);
//		HashSet<BlacklistElement> blacklistedImg = makeBlackList(docToClean, "img", usedPagesForCleaning);
//
//		CleanHTMLTree.travers(docToClean, blacklistedTxt, blacklistedImg);
//		return docToClean;
//	}
//
//
//	public static HashSet<BlacklistElement> makeBlackList(Document docToClean, String parameter, List<Document> usedPagesForCleaning) throws Exception {
//
//		HashSet<BlacklistElement> blacklist = new HashSet<>();
//		if (usedPagesForCleaning.size() > 1) {
//			Document document = usedPagesForCleaning.get(0);
//			if (parameter.equals("txt"))
//				blacklist = CleanHTMLTree.getHTMLElementsText(document);
//			else //img
//				blacklist = CleanHTMLTree.getHTMLElementsImg(document);
//
//			for (int i=1; i<usedPagesForCleaning.size();i++) {
//				document = usedPagesForCleaning.get(i);
//				HashSet<BlacklistElement> temp;
//				if (parameter.equals("txt"))
//					temp = CleanHTMLTree.getHTMLElementsText(document);
//				else //img
//					temp = CleanHTMLTree.getHTMLElementsImg(document);
//				blacklist.retainAll(temp);
//			}
//			return blacklist;
//		}
//		return blacklist;
//	}
//
//	public static Set<String> getXPathsFromDocument(Document doc, String parameter_pag, String par) throws Exception {
//
//		//pulire la pagina
//
//		List<Document> usedPagesForCleaning = new ArrayList<>();
//		for (int i=1; i<=5;i++) {
//			usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
//					new FileReader(new File(cartella+"pag"+parameter_pag+"_"+i+".html")))));
//		}
//		Document docClean = clean(doc, usedPagesForCleaning);
//
//		//segmentazione
//
//		List<Node> nodes_segments = WebPageSegmentation.segment(docClean,cartella+par+"Segmented.html");
//
//		//estrazione degli xpath dei segmenti
//
//		//TODO la lista degli xPath deve essere associata al dominio
//		XPathMaker xpMaker = XPathMaker.getInstance();
//		Set<String> xPaths = new HashSet<>();
//		nodes_segments.forEach(node_segment -> {
//			xPaths.add(xpMaker.calculateXPath(node_segment));
//		});
//
//		return xPaths;
//	}
//
//
//	public static Set<Segment> getSegments(Set<String> xPaths, Document document) {
//
//		Set<Segment> segments = new HashSet<>();
//
//		xPaths.forEach(xPath -> {
//
//			try {
//				//controllo che impedisce di aggiungere segmenti il cui contenuto è irrilevante
//				//(composto da un solo carattere)
//				Segment toAdd = new Segment(xPath, document);
//				String content = NodeUtils.getNodesContent(toAdd.getNodes());
//				String cleaned = content.replaceAll("[^a-zA-Z0-9]+", "");
//				if (cleaned.length() > 2) {
//					segments.add(toAdd);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//
//		return segments;
//	}
//}
