package segmentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import configurations.Configurator;
import lucene.SegmentIndexer;
import lucene.SegmentSearcher;
import model.Segment;
import model.WebPageDocument;
import scala.Tuple2;

public class TopSegmentsFinder {

	private static TopSegmentsFinder instance = null;

	public static TopSegmentsFinder getInstance() {
		if (instance == null)
			instance = new TopSegmentsFinder();
		return instance;
	}

	private TopSegmentsFinder() {
	}

	public List<Tuple2<Segment,List<Tuple2<Segment,Float>>>> findTopSegments_new(String indexPath, 
			WebPageDocument firstDocument, WebPageDocument secondDocument) throws Exception {
		double threshold = Configurator.getCosSimThreshold();
		//troviamo i segmenti corrispondenti
		Set<Segment> firstPageSegments = firstDocument.getSegments();
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		//**confrontare i segmenti con coseno similarità
		//indicizza la seconda pagina
		SegmentIndexer indexer = new SegmentIndexer(indexPath);
		//indicizzo i segmenti della seconda pagina
		int nRes = indexer.createIndex(secondPageSegments);
		System.out.println("totale segmenti indicizzati: "+nRes);
		indexer.close();

		SegmentSearcher searcher = new SegmentSearcher(indexPath);

		Iterator<Segment> firstPageSegmentsIterator = firstPageSegments.iterator();

		List<Tuple2<Segment,List<Tuple2<Segment,Float>>>> segment_relevantSegments = new ArrayList<>();
		while (firstPageSegmentsIterator.hasNext()) {
			Segment	firstPageSeg = firstPageSegmentsIterator.next();
			List<Tuple2<Segment,Float>> relevantSegments_scores = new ArrayList<>();
			TopDocs hits = null;
			try {
				//cerco ogni segmento della prima pagina nell'indice
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hits != null && hits.totalHits > 0) {
				for(ScoreDoc scoreDoc : hits.scoreDocs) {
					//se supera la soglia di coseno similarità
					if (scoreDoc.score >= threshold) {
						org.apache.lucene.document.Document lucDoc = null;
						try {
							lucDoc = searcher.getDocument(scoreDoc);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//rilevanza+1 al segmento della prima pagina
						firstPageSeg.setRelevance(firstPageSeg.getRelevance()+1);
						//setto la rilevanza dei segmenti del secondo documento
						Segment seg_secondDocument = secondDocument.getSegmentByXpath(lucDoc.get("segmentPath"));
						seg_secondDocument.setRelevance(seg_secondDocument.getRelevance()+1);
						relevantSegments_scores.add(new Tuple2<>(seg_secondDocument,scoreDoc.score));
					}
				}
			}
			//salvo i risultati
			segment_relevantSegments.add(
					new Tuple2<Segment,List<Tuple2<Segment,Float>>>(firstPageSeg,relevantSegments_scores));
		}

		return segment_relevantSegments;
	}

	public List<Tuple2<Segment, TopDocs>> findTopSegments(String indexPath, 
			WebPageDocument firstDocument, WebPageDocument secondDocument) throws Exception {

		//troviamo i segmenti corrispondenti
		Set<Segment> firstPageSegments = firstDocument.getSegments();
		Set<Segment> secondPageSegments = secondDocument.getSegments();

		//**confrontare i segmenti con coseno similarità
		//indicizza la seconda pagina
		SegmentIndexer indexer = new SegmentIndexer(indexPath);
		//indicizzo i segmenti della seconda pagina
		int nRes = indexer.createIndex(secondPageSegments);
		System.out.println("totale segmenti indicizzati: "+nRes);
		indexer.close();

		SegmentSearcher searcher = new SegmentSearcher(indexPath);

		Iterator<Segment> firstPageSegmentsIterator = firstPageSegments.iterator();

		List<Tuple2<Segment,TopDocs>> segment2hits = new ArrayList<>();
		while (firstPageSegmentsIterator.hasNext()) {
			Segment	firstPageSeg = firstPageSegmentsIterator.next();
			TopDocs hits = null;
			try {
				//cerco ogni segmento della prima pagina nell'indice
				hits = searcher.search(firstPageSeg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (hits != null && hits.totalHits > 0) {
				//salvo i risultati

				segment2hits.add(new Tuple2<Segment,TopDocs>(firstPageSeg,hits));
			}
		}

		return segment2hits;
	}


	public void setRelevances(List<Tuple2<Segment, TopDocs>> segment2hits,
			WebPageDocument wpd_second, String indexPath) throws IOException {
		double threshold = Configurator.getCosSimThreshold();
		SegmentSearcher searcher = new SegmentSearcher(indexPath);
		for (int j=0; j<segment2hits.size(); j++) {
			Segment seg = segment2hits.get(j)._1();
			TopDocs hits = segment2hits.get(j)._2();
			//per ogni risultato
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				//se supera la soglia di coseno similarità
				if (scoreDoc.score >= threshold) {
					org.apache.lucene.document.Document lucDoc = null;
					try {
						lucDoc = searcher.getDocument(scoreDoc);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//rilevanza+1 al segmento della prima pagina
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
