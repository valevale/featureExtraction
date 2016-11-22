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
import org.jsoup.safety.Whitelist;

import lib.utils.CleanHTMLTree;
import lib.utils.MapUtils;
import lib.utils.NodeUtils;
import model.BlacklistElement;
import model.Segment;
import scala.Tuple2;

public class RelevantSegmentsFinder {

	//	static final String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/zNewTest/"+
	//			"claudio_amoroso/";
	//	static final String indexPath = cartella+"segmentIndex";
	//
	//	static final String n1 = "1";
	//	static final String n2 = "2";
	//	static File indexFolder = new File(indexPath);
	//	static String[]entries = indexFolder.list();
	//
	//	static final double threshold = 1;

	//	public static void main(String[] args) throws Exception {
	public static void findRelevantSegments(String cartella, int n1, int n2, double range) throws Exception {

		String d1Path = cartella+"orig"+n1+".html";
		String d2Path = cartella+"orig"+n2+".html";
		
		File d1 = new File(d1Path);
		File d2 = new File(d2Path);

		if (d1.exists() && d2.exists()) {

			String indexPath = cartella+"segmentIndex";
			File indexFolder = new File(indexPath);
			String[]entries = indexFolder.list();

			//eliminazione dell'indice
			if (entries != null) {
				System.out.println("deleting previous index");
				for(String s: entries){
					File currentFile = new File(indexFolder.getPath(),s);
					currentFile.delete();
				}
			}


			//passo 1: prendere la pagina da segmentare
			String html_firstPage = IOUtils.toString(new FileReader(d1));
			String cleanedHTML_firstPage = Jsoup.clean(html_firstPage, Whitelist.relaxed());
			
			Document doc_firstPage = Jsoup.parse(cleanedHTML_firstPage);

			System.out.println("Estrazione degli xpath dei segmenti del primo documento");
			Set<String> xPaths_firstPage = getXPathsFromDocument(doc_firstPage, n1, cartella);

			String html_secondPage = IOUtils.toString(new FileReader(d2));
			String cleanedHTML_secondPage = Jsoup.clean(html_secondPage, Whitelist.relaxed());
			Document doc_secondPage = Jsoup.parse(cleanedHTML_secondPage);

			System.out.println("Estrazione degli xpath dei segmenti del secondo documento");
			Set<String> xPaths_secondPage = getXPathsFromDocument(doc_secondPage, n2, cartella);



			//troviamo i segmenti corrispondenti
			System.out.println("Generazione degli oggetti Segment per il primo documento");
			Set<Segment> firstPageSegments = getSegments(xPaths_firstPage, doc_firstPage);

			System.out.println("Generazione degli oggetti Segment per il secondo documento");
			Set<Segment> secondPageSegments = getSegments(xPaths_secondPage, doc_secondPage);


			XPathMaker xpMaker = XPathMaker.getInstance();

			System.out.println("coloring segments");

			org.w3c.dom.Document segmentedFirstPage = xpMaker.color(xPaths_firstPage, doc_firstPage);
			PrintWriter testPrinter = new PrintWriter(cartella+"orig"+n1+"Segmented.html", "UTF-8");
			testPrinter.println(getStringFromDocument(segmentedFirstPage));
			testPrinter.close();

			org.w3c.dom.Document segmentedPage = xpMaker.color(xPaths_secondPage, doc_secondPage);
			testPrinter = new PrintWriter(cartella+"orig"+n2+"Segmented.html", "UTF-8");
			testPrinter.println(getStringFromDocument(segmentedPage));
			testPrinter.close();



			//passo 6: confrontare i segmenti con coseno similarità

			//6.1 : indicizza la seconda pagina
			SegmentIndexer indexer = new SegmentIndexer(indexPath);
			int nRes = indexer.createIndex(secondPageSegments);
			System.out.println("totale segmenti indicizzati: "+nRes);
			indexer.close();
			SegmentSearcher searcher = new SegmentSearcher(indexPath);
			PrintWriter resPrinter = new PrintWriter(cartella+"resCOSIM"+n1+"-"+n2+".txt", "UTF-8");

			//6.2 : per ogni segmento della prima pagina, trovi i primi 10 segmenti simili
			Iterator<Segment> segmentIterator = firstPageSegments.iterator();
			//permette di ordinare i risultati in base allo score ottenuto
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
				//			System.out.println("search completed: "+ hits.totalHits);
				if (hits.totalHits > 0) {
					seg2maxSim.put(firstPageSeg, hits.scoreDocs[0].score);
				}
			}

			List<Tuple2<Segment,TopDocs>> segment2hits = new ArrayList<>();


			//ordina la mappa in base alla similarità
			seg2maxSim = MapUtils.sortByValue(seg2maxSim);
			Set<Segment> keySet = seg2maxSim.keySet();
			segmentIterator = keySet.iterator();

			//questa seconda ricerca ci permette di avere i risultati in ordine di score decrescente
			//TODO quando non servirà controllare in dettaglio, andrà tolta questa doppia ricerca
			segmentIterator = keySet.iterator();
			while (segmentIterator.hasNext()) {
				Segment	firstPageSeg = segmentIterator.next();
				TopDocs hits = null;
				try {
					hits = searcher.search(firstPageSeg);
					segment2hits.add(new Tuple2<Segment,TopDocs>(firstPageSeg,hits));
				} catch (Exception e) {
					e.printStackTrace();
				}
				//			System.out.println("search completed: "+ hits.totalHits);
				if (hits.totalHits > 0) {
					//TODO qui metti la soglia vera. qui crei tot documenti, uno per ogni threshold
					//				if (hits.scoreDocs[0].score >= 0) {
					resPrinter.println(NodeUtils.getNodesContent(firstPageSeg.getNodes()));
					resPrinter.println("---");
					for(ScoreDoc scoreDoc : hits.scoreDocs) {
						//TODO qui modifichi la soglia
						//					if (scoreDoc.score >= threshold) {
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
						//					firstPageSeg.setRelevance(firstPageSeg.getRelevance()+1);
						//					//memorizziamo in una mappa il matching dell'algoritmo
						//					Set<String> matches;
						//					if (!matchings.containsKey((firstPageSeg.getXPath()))) {
						//						matches = new HashSet<>();
						//					}
						//					else {
						//						matches = matchings.get(firstPageSeg.getXPath());
						//					}
						//					matches.add(lucDoc.get("segmentPath"));
						//					matchings.put(firstPageSeg.getXPath(), matches);
					}
				}
				resPrinter.println("__________________");
			}
			//			}
			//		}


			resPrinter.close();

			for (double i=0; i<=1; i=i+range) {
				System.out.println("Threshold: "+i);
				//mappa per la verifica
				Map<String,Set<String>> matchings = new HashMap<>();
				List<Segment> segmentsToColor = new ArrayList<>();
				for (int j=0; j<segment2hits.size(); j++) {
					Segment seg = segment2hits.get(j)._1();
					TopDocs hits = segment2hits.get(j)._2();
					for(ScoreDoc scoreDoc : hits.scoreDocs) {
						if (scoreDoc.score >= i) {
							org.apache.lucene.document.Document lucDoc = null;
							try {
								lucDoc = searcher.getDocument(scoreDoc);
							} catch (Exception e) {
								e.printStackTrace();
							}
							seg.setRelevance(seg.getRelevance()+1);
							segmentsToColor.add(seg);
							//memorizziamo in una mappa il matching dell'algoritmo
							Set<String> matches;
							if (!matchings.containsKey((seg.getXPath()))) {
								matches = new HashSet<>();
							}
							else {
								matches = matchings.get(seg.getXPath());
							}
							matches.add(lucDoc.get("segmentPath"));
							matchings.put(seg.getXPath(), matches);
						}
					}
				}


				//testing
				System.out.println("Evaluation of results...");
				//crea la cartella di log se non esiste
				//TODO dovresti farlo da altre parti...
				new File(cartella+"/testLogs").mkdirs();
				MatchingTester.test(matchings, cartella+"matching"+n1+n2+".txt", cartella+"/testLogs/log"+n1+n2+"_"+i+".txt",
						d1Path, d2Path, i);
				System.out.println("End Evaluation of results.");

				System.out.println("coloring relevance");
				//crea la cartella di relevance se non esiste
				//TODO dovresti farlo da altre parti...
				new File(cartella+"/relevances").mkdirs();
				org.w3c.dom.Document coloredRelevance = xpMaker.colorRelevance(firstPageSegments, doc_firstPage);
				testPrinter = new PrintWriter(cartella+"/relevances/relevance"+n1+"-"+n2+"_"+i+".html", "UTF-8");
				testPrinter.println(getStringFromDocument(coloredRelevance));
				testPrinter.close();

				//setta a 0 la rilevanza dei segmenti colorati, per le successive iterazioni
				segmentsToColor.forEach(segment -> {
					segment.setRelevance(0);
				});

				System.out.println("End");
			}

			//ora stampiamo i segmenti delle due pagine
			testPrinter = new PrintWriter(cartella+"Segments"+n1+".txt", "UTF-8");
			for(Segment seg : firstPageSegments){
				testPrinter.println(seg.getXPath());
				testPrinter.println(NodeUtils.getNodesContent(seg.getNodes()));
				testPrinter.println("________________________________________________________________________");
			};
			testPrinter.close();

			testPrinter = new PrintWriter(cartella+"Segments"+n2+".txt", "UTF-8");
			for(Segment seg : secondPageSegments){
				testPrinter.println(seg.getXPath());
				testPrinter.println(NodeUtils.getNodesContent(seg.getNodes()));
				testPrinter.println("________________________________________________________________________");
			};
			testPrinter.close();
		}
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
	public static Set<String> getXPathsFromDocument(Document doc, int par, String cartella) throws Exception {

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
