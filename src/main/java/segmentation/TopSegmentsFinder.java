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

import configurations.Configurator;
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
	
//	public List<Tuple2<Segment, TopDocs>> findTopSegments() {
//		
//	}

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
	public List<Tuple2<Segment, TopDocs>> findTopSegments(String indexPath, 
			WebPageDocument firstDocument, WebPageDocument secondDocument) throws Exception {

		//troviamo i segmenti corrispondenti
		Set<Segment> firstPageSegments = firstDocument.getSegments();
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		//**confrontare i segmenti con coseno similarità
		//indicizza la seconda pagina
		SegmentIndexer indexer = new SegmentIndexer(indexPath);
		int nRes = indexer.createIndex(secondPageSegments);
		System.out.println("totale segmenti indicizzati: "+nRes);
		indexer.close();

		SegmentSearcher searcher = new SegmentSearcher(indexPath);

		Iterator<Segment> segmentIterator = firstPageSegments.iterator();

		List<Tuple2<Segment,TopDocs>> segment2hits = new ArrayList<>();
		while (segmentIterator.hasNext()) {
			Segment	firstPageSeg = segmentIterator.next();
			TopDocs hits = null;
			try {
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hits.totalHits > 0) {
				segment2hits.add(new Tuple2<Segment,TopDocs>(firstPageSeg,hits));
			}
		}

		return segment2hits;
	}

	public void setRelevances(List<Tuple2<Segment, TopDocs>> segment2hits,
			WebPageDocument wpd_second, String indexPath) throws IOException {
		//TODO in un file di configurazione
		double threshold = Configurator.getCosSimThreshold();
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
			WebPageDocument firstDocument, WebPageDocument secondDocument) 
					throws Exception {
		List<Tuple2<Segment, TopDocs>> segment2hits =
				findTopSegments(cartella, firstDocument, secondDocument);
		setRelevances(segment2hits, secondDocument, cartella);
		return segment2hits;
	}
}
