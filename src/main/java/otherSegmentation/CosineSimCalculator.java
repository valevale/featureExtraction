package otherSegmentation;
//package segmentation;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.apache.lucene.document.Document;
//import org.apache.lucene.queryparser.classic.ParseException;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//
//import lib.utils.NodeUtils;
//import model.Segment;
//
//public class CosineSimCalculator {
//
//	private String indexDir = "segmentIndex";
//	private SegmentIndexer indexer;
//	private SegmentSearcher searcher;
//	
//	public CosineSimCalculator() {
//		
//	}
//	
////	public void getTopSimilarSegments(Segment seg, List<Segment> segments) {
////	      try {
////	    	 this.createIndex(segments);
////	         this.search(seg);
////	      } catch (IOException e) {
////	         e.printStackTrace();
////	      } catch (ParseException e) {
////	         e.printStackTrace();
////	      }
////	   }
//
//	private void createIndex(List<Segment> segments) throws IOException{
//		indexer = new SegmentIndexer(indexDir);
//		int numIndexed;
////		long startTime = System.currentTimeMillis();	
//		numIndexed = indexer.createIndex(segments);
////		long endTime = System.currentTimeMillis();
//		indexer.close();
//		System.out.println(numIndexed+" File indexed");		
////		System.out.println(numIndexed+" File indexed, time taken: "
////				+(endTime-startTime)+" ms");		
//	}
//
////	public TopDocs search(Segment searchSegment, String indDir) throws IOException, ParseException{
////		searcher = new SegmentSearcher(indDir);
//////		long startTime = System.currentTimeMillis();
////		
////		String query = NodeUtils.getNodesContent(searchSegment.getNodes());
////		
////		TopDocs hits = searcher.search(query);
//////		long endTime = System.currentTimeMillis();
////		
////		return hits;
////
//////		System.out.println(hits.totalHits +
//////				" documents found");
//////		for(ScoreDoc scoreDoc : hits.scoreDocs) {
//////			Document doc = searcher.getDocument(scoreDoc);
//////			System.out.println("Segment: "
//////					+ doc.get("segmentContent"));
//////		}
////	}
//}
