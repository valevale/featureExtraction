package otherSegmentation;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class MySimilarity extends DefaultSimilarity {
//	@Override
//	public float idf(long docFreq, long numDocs) {
//		return 1.0f;
//	}
//
//	@Override
//	public float tf(float freq) {
//		return 1.0f;
//	}
//
//	@Override
//	public float coord(int overlap, int maxOverlap) {
//		return 1.0f;
//	}

	@Override
	public float lengthNorm(FieldInvertState state) {
		return state.getBoost();
	}

//	@Override
//	public float queryNorm(float sumOfSquaredWeights) {
//		return 1.0f;
//	}
} 
