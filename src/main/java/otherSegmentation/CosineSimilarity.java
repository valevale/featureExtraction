package otherSegmentation;
//package segmentation;
//
//public class CosineSimilarity {
//
//	//	public static void main(String[] args) {
//	//		Indexer index = new Indexer();
//	//		index.index();
//	//		VectorGenerator vectorGenerator = new VectorGenerator();
//	//		vectorGenerator.GetAllTerms();       
//	//		DocVector[] docVector = vectorGenerator.GetDocumentVectors(); // getting document vectors
//	//		for(int i = 0; i < docVector.length; i++)
//	//		{
//	//			double cosineSimilarity = CosineSimilarity.CosineSimilarity(docVector[1], docVector[i]);
//	//			System.out.println("Cosine Similarity Score between document 0 and "+i+"  = " + cosineSimilarity);
//	//		} 
//	//	}
//
//	public static double getCosineSimilarity(DocVector d1,DocVector d2) {
//		double cosinesimilarity;
//		try {
//			cosinesimilarity = (d1.vector.dotProduct(d2.vector))
//					/ (d1.vector.getNorm() * d2.vector.getNorm());
//		} catch (Exception e) {
//			//			System.out.println("errore "+e);
//			//			System.out.println("d1 "+d1);
//			//			System.out.println("d2 "+d2);
//			return 0.0;
//		}
//		return cosinesimilarity;
//	}
//
//}
