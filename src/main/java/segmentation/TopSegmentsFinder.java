package segmentation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import lib.utils.DocumentUtils;
import lib.utils.MapUtils;
import lib.utils.NodeUtils;
import lucene.SegmentIndexer;
import lucene.SegmentSearcher;
import model.Segment;
import model.WebPageDocument;
import scala.Tuple2;
import xpath.utils.XpathApplier;

public class TopSegmentsFinder {

	private static TopSegmentsFinder instance = null;
	
	public static TopSegmentsFinder getInstance() {
		if (instance == null)
			instance = new TopSegmentsFinder();
		return instance;
	}
	
	private TopSegmentsFinder() {
	}

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

	public void setRelevances(List<Tuple2<Segment, TopDocs>> segment2hits,
			WebPageDocument wpd_second, String indexPath) throws IOException {
		double threshold = 0.6;
		SegmentSearcher searcher = new SegmentSearcher(indexPath);
		for (int j=0; j<segment2hits.size(); j++) {
			Segment seg = segment2hits.get(j)._1();
			TopDocs hits = segment2hits.get(j)._2();
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				if (scoreDoc.score >= threshold) {
					org.apache.lucene.document.Document lucDoc = null;
					try {
						lucDoc = searcher.getDocument(scoreDoc);
					} catch (Exception e) {
						e.printStackTrace();
					}
					seg.setRelevance(seg.getRelevance()+1);
					//setto la rilevanza dei segmenti del secondo documento
					Segment seg_secondDocument = wpd_second.getSegmentByXpath(lucDoc.get("segmentPath"));
					seg_secondDocument.setRelevance(seg_secondDocument.getRelevance()+1);
				}
			}
		}
	}

	/* cartella: l'ambiente su cui operare
	 * n1: numero identificativo di una delle due pagine da confrontare (la prima)
	 * n2: numero identificativo di una delle due pagine da confrontare (la seconda)
	 * parN1: numero identificativo delle pagine necessarie per pulire il template di una delle due pagine da confrontare (la prima)
	 * parN2: numero identificativo delle pagine necessarie per pulire il template di una delle due pagine da confrontare (la seconda)
	 * parameterTextFusion: parametro per granularità di segmentazione
	 * */
	//	public static List<Tuple2<Segment, TopDocs>> findRelevantSegments(String cartella, 
	//			int n1, int n2, int parN1, int parN2, double parameterTextFusion) 
	//					throws Exception {

	public List<Tuple2<Segment, TopDocs>> findTopSegments(String cartella, 
			WebPageDocument firstDocument, WebPageDocument secondDocument, int n1, int n2) 
					throws Exception {

		String indexPath = cartella+"segmentIndex";
		
		//troviamo i segmenti corrispondenti
		System.out.println("Generazione degli oggetti Segment per il primo documento");
		Set<Segment> firstPageSegments = firstDocument.getSegments();

		System.out.println("Generazione degli oggetti Segment per il secondo documento");
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		XpathApplier xpapplier = XpathApplier.getInstance();

		System.out.println("coloring segments");
		org.w3c.dom.Document segmentedFirstPage = xpapplier.color(firstDocument.getXPaths(), firstDocument.getDocument());
		PrintWriter testPrinter = new PrintWriter(cartella+"orig"+n1+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedFirstPage));
		testPrinter.close();

		org.w3c.dom.Document segmentedPage = xpapplier.color(secondDocument.getXPaths(), secondDocument.getDocument());
		testPrinter = new PrintWriter(cartella+"orig"+n2+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedPage));
		testPrinter.close();

		//confrontare i segmenti con coseno similarità

		//indicizza la seconda pagina
		SegmentIndexer indexer = new SegmentIndexer(indexPath);
		int nRes = indexer.createIndex(secondPageSegments);
		System.out.println("totale segmenti indicizzati: "+nRes);
		indexer.close();
		SegmentSearcher searcher = new SegmentSearcher(indexPath);
		PrintWriter resPrinter = new PrintWriter(cartella+"resCOSIM"+n1+"-"+n2+".txt", "UTF-8");

		//per ogni segmento della prima pagina, trovi i primi 10 segmenti simili
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
				resPrinter.println(NodeUtils.getNodesContent(firstPageSeg.getW3cNodes()));
				resPrinter.println("---");
				for(ScoreDoc scoreDoc : hits.scoreDocs) {
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
				}
			}
			resPrinter.println("__________________");
		}

		resPrinter.close();

		//ora stampiamo i segmenti delle due pagine
		testPrinter = new PrintWriter(cartella+"Segments"+n1+".txt", "UTF-8");
		for(Segment seg : firstPageSegments){
			testPrinter.println(seg.getAbsoluteXPath());
			testPrinter.println(NodeUtils.getNodesContent(seg.getW3cNodes()));
			testPrinter.println("________________________________________________________________________");
		};
		testPrinter.close();

		testPrinter = new PrintWriter(cartella+"Segments"+n2+".txt", "UTF-8");
		for(Segment seg : secondPageSegments){
			testPrinter.println(seg.getAbsoluteXPath());
			testPrinter.println(NodeUtils.getNodesContent(seg.getW3cNodes()));
			testPrinter.println("________________________________________________________________________");
		};
		testPrinter.close();
		return segment2hits;
	}
	
	
	public static List<Tuple2<Segment, TopDocs>> findRelevantSegments_old(String cartella, 
				int n1, int n2, int parN1, int parN2, double parameterTextFusion) 
						throws Exception {

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
		//			String html_firstPage = IOUtils.toString(new FileReader(d1));
		//			String cleanedHTML_firstPage = Jsoup.clean(html_firstPage, Whitelist.relaxed());
		//			
		//			Document doc_firstPage = Jsoup.parse(cleanedHTML_firstPage);

					WebPageDocument firstDocument = new WebPageDocument(d1, parN1, cartella, parameterTextFusion);

//					System.out.println("Estrazione degli xpath dei segmenti del primo documento");
//					Set<String> xPaths_firstPage = getXPathsFromDocument(firstDocument.getDocument(), parN1, cartella);


		//			String html_secondPage = IOUtils.toString(new FileReader(d2));
//					String cleanedHTML_secondPage = Jsoup.clean(html_secondPage, Whitelist.relaxed());
//					Document doc_secondPage = Jsoup.parse(cleanedHTML_secondPage);

					WebPageDocument secondDocument = new WebPageDocument(d2, parN2, cartella, parameterTextFusion);

		//			System.out.println("Estrazione degli xpath dei segmenti del secondo documento");
		//			Set<String> xPaths_secondPage = getXPathsFromDocument(secondDocument.getDocument(), parN2, cartella);


		//troviamo i segmenti corrispondenti
		System.out.println("Generazione degli oggetti Segment per il primo documento");
		Set<Segment> firstPageSegments = firstDocument.getSegments();

		System.out.println("Generazione degli oggetti Segment per il secondo documento");
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		XpathApplier xpapplier = XpathApplier.getInstance();

		System.out.println("coloring segments");
		org.w3c.dom.Document segmentedFirstPage = xpapplier.color(firstDocument.getXPaths(), firstDocument.getDocument());
		PrintWriter testPrinter = new PrintWriter(cartella+"orig"+n1+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedFirstPage));
		testPrinter.close();

		org.w3c.dom.Document segmentedPage = xpapplier.color(secondDocument.getXPaths(), secondDocument.getDocument());
		testPrinter = new PrintWriter(cartella+"orig"+n2+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedPage));
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
				//				if (hits.scoreDocs[0].score >= 0) {
				resPrinter.println(NodeUtils.getNodesContent(firstPageSeg.getW3cNodes()));
				resPrinter.println("---");
				for(ScoreDoc scoreDoc : hits.scoreDocs) {
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

		//ora stampiamo i segmenti delle due pagine
		testPrinter = new PrintWriter(cartella+"Segments"+n1+".txt", "UTF-8");
		for(Segment seg : firstPageSegments){
			testPrinter.println(seg.getAbsoluteXPath());
			testPrinter.println(NodeUtils.getNodesContent(seg.getW3cNodes()));
			testPrinter.println("________________________________________________________________________");
		};
		testPrinter.close();

		testPrinter = new PrintWriter(cartella+"Segments"+n2+".txt", "UTF-8");
		for(Segment seg : secondPageSegments){
			testPrinter.println(seg.getAbsoluteXPath());
			testPrinter.println(NodeUtils.getNodesContent(seg.getW3cNodes()));
			testPrinter.println("________________________________________________________________________");
		};
		testPrinter.close();
		return segment2hits;
				}
		return null;
	}
}
