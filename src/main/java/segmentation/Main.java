package segmentation;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import lib.utils.CleanHTMLTree;
import lib.utils.MapUtils;
import lib.utils.NodeUtils;
import model.BlacklistElement;
import model.Segment;

public class Main {

	static final String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/silvia_pelucchi/";
	static final String indexPath = cartella+"segmentIndex";


	static final String n1 = "2";
	static final String n2 = "1";

	public static void main(String[] args) throws Exception {

		//passo 1: prendere la pagina da segmentare
		String html_firstPage = IOUtils.toString(new FileReader(new File(cartella+"orig"+n1+".html")));
		Document doc_firstPage = Jsoup.parse(html_firstPage);

		System.out.println("Estrazione degli xpath dei segmenti del primo documento");
		Set<String> xPaths_firstPage = getXPathsFromDocument(doc_firstPage, n1);

		String html_secondPage = IOUtils.toString(new FileReader(new File(cartella+"orig"+n2+".html")));
		Document doc_secondPage = Jsoup.parse(html_secondPage);

		System.out.println("Estrazione degli xpath dei segmenti del secondo documento");
		Set<String> xPaths_secondPage = getXPathsFromDocument(doc_secondPage, n2);


		//troviamo i segmenti corrispondenti
		System.out.println("Generazione degli oggetti Segment per il primo documento");
		Set<Segment> firstPageSegments = getSegments(xPaths_firstPage, doc_firstPage);

		System.out.println("Generazione degli oggetti Segment per il secondo documento");
		Set<Segment> secondPageSegments = getSegments(xPaths_secondPage, doc_secondPage);

		//passo 6: confrontare i segmenti con coseno similarità

		//6.1 : indicizza la seconda pagina
		SegmentIndexer indexer = new SegmentIndexer(indexPath);
		int nRes = indexer.createIndex(secondPageSegments);
		System.out.println("totale segmenti indicizzati: "+nRes);
		indexer.close();
		SegmentSearcher searcher = new SegmentSearcher(indexPath);
		File indexFolder = new File(indexPath);
		PrintWriter resPrinter = new PrintWriter(cartella+"resCOSIM"+n1+"-"+n2+".txt", "UTF-8");

		//6.2 : per ogni segmento della prima pagina, trovi i primi 10 segmenti simili
		Iterator<Segment> segmentIterator = firstPageSegments.iterator();
		Map<Segment, Float> seg2maxSim = new HashMap<>();
		while (segmentIterator.hasNext()) {
			Segment	firstPageSeg = segmentIterator.next();
//			System.out.println(NodeUtils.getNodesContent(firstPageSeg.getNodes())+"\n--\n");
			TopDocs hits = null;
			try {
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("search completed: "+ hits.totalHits);
			if (hits.totalHits > 0) {

				seg2maxSim.put(firstPageSeg, hits.scoreDocs[0].score);
			}
		}
		//ordina la mappa in base alla similarità
		seg2maxSim = MapUtils.sortByValue(seg2maxSim);
		Set<Segment> keySet = seg2maxSim.keySet();
		segmentIterator = keySet.iterator();
		//		float maxScore = seg2maxSim.get(segmentIterator.next());
		segmentIterator = keySet.iterator();
		while (segmentIterator.hasNext()) {
			Segment	firstPageSeg = segmentIterator.next();
			TopDocs hits = null;
			try {
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("search completed: "+ hits.totalHits);
			if (hits.totalHits > 0) {
				if (hits.scoreDocs[0].score >= 0) {
					resPrinter.println(NodeUtils.getNodesContent(firstPageSeg.getNodes()));
					resPrinter.println("---");
					for(ScoreDoc scoreDoc : hits.scoreDocs) {
						//TODO qui modifichi la soglia
						if (scoreDoc.score >= 0.3) {
							org.apache.lucene.document.Document lucDoc = null;
							try {
								lucDoc = searcher.getDocument(scoreDoc);
							} catch (Exception e) {
								e.printStackTrace();
							}
							resPrinter.println("-");
							resPrinter.println(scoreDoc.score);
							resPrinter.println(lucDoc.get("segmentContent"));
							resPrinter.println(lucDoc.get("segmentPath"));
							firstPageSeg.setRelevance(firstPageSeg.getRelevance()+1);
							//così si prende invece l'xPath
							//					Segment seg = new Segment(lucDoc.get("segmentPath"), doc);
						}
					}
					resPrinter.println("__________________");
				}
			}
		}

		resPrinter.close();

		//eliminazione dell'indice
		System.out.println("deleting index");
		String[]entries = indexFolder.list();
		for(String s: entries){
			File currentFile = new File(indexFolder.getPath(),s);
			currentFile.delete();
		}

		XPathMaker xpMaker = XPathMaker.getInstance();
		
		System.out.println("coloring relevance");
		org.w3c.dom.Document coloredRelevance = xpMaker.colorRelevance(firstPageSegments, doc_firstPage);
		PrintWriter testPrinter = new PrintWriter(cartella+"relevance"+n1+"-"+n2+".html", "UTF-8");
		testPrinter.println(getStringFromDocument(coloredRelevance));
		testPrinter.close();

		System.out.println("coloring segments");
		
		org.w3c.dom.Document segmentedFirstPage = xpMaker.color(xPaths_firstPage, doc_firstPage);
		testPrinter = new PrintWriter(cartella+"orig"+n1+"Segmented.html", "UTF-8");
		testPrinter.println(getStringFromDocument(segmentedFirstPage));
		testPrinter.close();
		
		org.w3c.dom.Document segmentedPage = xpMaker.color(xPaths_secondPage, doc_secondPage);
		testPrinter = new PrintWriter(cartella+"orig"+n2+"Segmented.html", "UTF-8");
		testPrinter.println(getStringFromDocument(segmentedPage));
		testPrinter.close();
	}

	/* Prende come input un documento w3c e lo converte in stringa leggibile */
	public static String getStringFromDocument(org.w3c.dom.Document doc)
	{
		try
		{
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		}
		catch(TransformerException ex)
		{
			ex.printStackTrace();
			return null;
		}
	} 


	/* Prende come input un documento jsoup da pulire, assieme a una lista di documenti
	 * che vengono usati per la pulizia
	 * e restituisce un documento pulito del template comune alle pagine */
	public static Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {

		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(docToClean, "txt", usedPagesForCleaning);
		HashSet<BlacklistElement> blacklistedImg = makeBlackList(docToClean, "img", usedPagesForCleaning);

		CleanHTMLTree.travers(docToClean, blacklistedTxt, blacklistedImg);
		return docToClean;
	}


	public static HashSet<BlacklistElement> makeBlackList(Document docToClean, String parameter, List<Document> usedPagesForCleaning) throws Exception {

		HashSet<BlacklistElement> blacklist = new HashSet<>();
		if (usedPagesForCleaning.size() > 1) {
			Document document = usedPagesForCleaning.get(0);
			if (parameter.equals("txt"))
				blacklist = CleanHTMLTree.getHTMLElementsText(document);
			else //img
				blacklist = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<usedPagesForCleaning.size();i++) {
				document = usedPagesForCleaning.get(i);
				HashSet<BlacklistElement> temp;
				if (parameter.equals("txt"))
					temp = CleanHTMLTree.getHTMLElementsText(document);
				else //img
					temp = CleanHTMLTree.getHTMLElementsImg(document);
				blacklist.retainAll(temp);
			}
			return blacklist;
		}
		return blacklist;
	}

	/* dato un documento, restituisce gli xpath dei segmenti che lo compongono
	 * richiede anche un massimo di 5 pagine per la pulizia del template */
	public static Set<String> getXPathsFromDocument(Document doc, String par) throws Exception {

		//pulire la pagina

		List<Document> usedPagesForCleaning = new ArrayList<>();

		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=1; i<=5;i++) {
			try {
				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
						new FileReader(new File(cartella+"pag"+par+"_"+i+".html")))));
			}
			catch (Exception e) {
				System.out.println("Errore pagina "+i + ": " + e);
			}
		}
		Document docClean = clean(doc, usedPagesForCleaning);

		//segmentazione

		List<Node> nodes_segments = WebPageSegmentation.segment(docClean);

		//estrazione degli xpath dei segmenti

		//TODO la lista degli xPath deve essere associata al dominio
		XPathMaker xpMaker = XPathMaker.getInstance();
		Set<String> xPaths = new HashSet<>();
		nodes_segments.forEach(node_segment -> {
			xPaths.add(xpMaker.calculateXPath(node_segment));
		});

		return xPaths;
	}


	/* Dato un set di stringhe rappresentanti xPath e un documento Jsoup,
	 * restituisce un set di porzioni del documento (segmenti), ovvero insiemi di nodi
	 * dell'albero html, corrispondenti a quegli xPath
	 * */
	public static Set<Segment> getSegments(Set<String> xPaths, Document document) {

		Set<Segment> segments = new HashSet<>();

		xPaths.forEach(xPath -> {

			try {
				//controllo che impedisce di aggiungere segmenti il cui contenuto è irrilevante
				//(composto da un solo carattere)
				Segment toAdd = new Segment(xPath, document);
				String content = NodeUtils.getNodesContent(toAdd.getNodes());
				String cleaned = content.replaceAll("[^a-zA-Z0-9]+", "");
				if (cleaned.length() > 1) {
					segments.add(toAdd);
				}
			} catch (Exception e) {
				System.out.println("Errore durante la generazione di un segmento "+e);
			}
		});

		return segments;
	}

}
