package segmentation;

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

/* Query customizzata per il calcolo della coseno similarità
 * Si affida a uno score provider costumizzato per il calcolo dello score
 * */

public class MyCustomScoreQuery extends CustomScoreQuery {

	public MyCustomScoreQuery(Query subQuery) {
		super(subQuery);
	}

	@Override
	public CustomScoreProvider getCustomScoreProvider(final AtomicReaderContext atomicContext) throws IOException {
		return new MyCustomScoreProvider(atomicContext, getSubQuery());
	}
}
