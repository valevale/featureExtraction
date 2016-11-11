package otherSegmentation;
//package segmentation;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.Terms;
//import org.apache.lucene.index.TermsEnum;
//import org.apache.lucene.util.BytesRef;
//
//public class VectorGenerator {
//	DocVector[] docVector;
//	private Map<String,Integer> allterms;
//	Integer totalNoOfDocumentInIndex;
//	IndexReader indexReader;
//
//	public VectorGenerator(String indexDirectoryPath) throws IOException
//	{
//		allterms = new HashMap<>();
//		indexReader = IndexOpener.GetIndexReader(indexDirectoryPath);
//		totalNoOfDocumentInIndex = IndexOpener.TotalDocumentInIndex(indexDirectoryPath);
//		docVector = new DocVector[totalNoOfDocumentInIndex];
//	}
//
//	public void GetAllTerms(String indexDirectoryPath) throws IOException
//	{
//		AllTerms allTerms = new AllTerms(indexDirectoryPath);
//		allTerms.initAllTerms();
//		allterms = allTerms.getAllTerms();
//	}
//
//	public DocVector[] GetDocumentVectors() throws IOException {
//		for (int docId = 0; docId<totalNoOfDocumentInIndex; docId++) {
//			Terms vector = indexReader.getTermVector(docId, "segmentContent");
//			System.out.println(docId + " term vector "+vector);
//			if (vector != null) {
//				TermsEnum termsEnum = null;
//				termsEnum = vector.iterator(termsEnum);
//				BytesRef text = null;            
//				docVector[docId] = new DocVector(allterms);            
//				while ((text = termsEnum.next()) != null) {
//					String term = text.utf8ToString();
//					int freq = (int) termsEnum.totalTermFreq();
//					docVector[docId].setEntry(term, freq);
//				}
//				docVector[docId].normalize();
//			}    
//		}
//		indexReader.close();
//		return docVector;
//	}
//}
