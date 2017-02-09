package segmentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

	/* cartella: l'ambiente su cui operare
	 * n1: numero identificativo di una delle due pagine da confrontare (la prima)
	 * n2: numero identificativo di una delle due pagine da confrontare (la seconda)
	 * parN1: numero identificativo delle pagine necessarie per pulire il template di una delle due pagine da confrontare (la prima)
	 * parN2: numero identificativo delle pagine necessarie per pulire il template di una delle due pagine da confrontare (la seconda)
	 * parameterTextFusion: parametro per granularità di segmentazione
	 * */
	//TODO il parametro di segmentazione, così come la threshold della coseno similarità, vanno messe in una
	//classe a parte!
	//e anche le cartelle degli indici!
	public List<Tuple2<Segment, TopDocs>> findTopSegments(String cartella, 
			WebPageDocument firstDocument, WebPageDocument secondDocument, int n1, int n2) 
					throws Exception {

		String indexPath = cartella+"segmentIndex";
//		File indexFolder = new File(indexPath);
//		String[]entries = indexFolder.list();
//
//		//eliminazione dell'indice
//		if (entries != null) {
//			for(String s: entries){
//				File currentFile = new File(indexFolder.getPath(),s);
//				currentFile.delete();
//			}
//		}
		//troviamo i segmenti corrispondenti
		Set<Segment> firstPageSegments = firstDocument.getSegments();
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		XpathApplier xpapplier = XpathApplier.getInstance();

		//colorazione segmenti. da rimuovere
		org.w3c.dom.Document segmentedFirstPage = xpapplier.color(firstDocument.getXPaths(), firstDocument.getDocument_jsoup());
		PrintWriter testPrinter = new PrintWriter(cartella+"orig"+n1+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedFirstPage));
		testPrinter.close();

		org.w3c.dom.Document segmentedPage = xpapplier.color(secondDocument.getXPaths(), secondDocument.getDocument_jsoup());
		testPrinter = new PrintWriter(cartella+"orig"+n2+"Segmented.html", "UTF-8");
		testPrinter.println(DocumentUtils.getStringFromDocument(segmentedPage));
		testPrinter.close();

		//**confrontare i segmenti con coseno similarità
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
		//ci serve solo per una stampa ordinata! poi si può togliere.. teoricamente anche ora
		Map<Segment, Float> seg2maxSim = new HashMap<>();
		while (segmentIterator.hasNext()) {
			Segment	firstPageSeg = segmentIterator.next();
			TopDocs hits = null;
			try {
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hits.totalHits > 0) {
				seg2maxSim.put(firstPageSeg, hits.scoreDocs[0].score);
			}
		}

		List<Tuple2<Segment,TopDocs>> segment2hits = new ArrayList<>();

		//ordina la mappa in base alla similarità, poi si può togliere
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

	public void setRelevances(List<Tuple2<Segment, TopDocs>> segment2hits,
			WebPageDocument wpd_second, String indexPath) throws IOException {
		//TODO in un file di configurazione
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
	
	/*unione dei due metodi di sopra*/
	public List<Tuple2<Segment, TopDocs>> findRelevantSegments(String cartella, 
			WebPageDocument firstDocument, WebPageDocument secondDocument, int n1, int n2) 
					throws Exception {
		List<Tuple2<Segment, TopDocs>> segment2hits =
				findTopSegments(cartella, firstDocument, secondDocument,
						n1, n2);
		setRelevances(segment2hits, secondDocument, cartella+"segmentIndex");
		return segment2hits;
	}
}
