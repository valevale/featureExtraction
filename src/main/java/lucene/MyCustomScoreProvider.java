package lucene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;

/* Calcola uno score personalizzato: coseno similarità
 * Si esamina ogni documento dell'indice
 * Per ogni documento si calcolano i vettori del documento stesso e della query
 * I vettori sono pesati con tf-idf
 * La query viene considerata come documento appartenente (temporaneamente) al corpus
 * */

public class MyCustomScoreProvider extends CustomScoreProvider{

	//il puntatore che permette di esaminare i documenti indicizzati su cui fare la ricerca
	private static AtomicReader atomicReader;
	//la query corrente
	private static Query query;
	//il numero totale dei documenti del corpus (+ la query)
	private static int N;
	//oggetto che memorizza il vocabolario del corpus
//	private IndexVocabulary indexVocabulary;
	//il vocabolario
	private Set<String> vocabulary;
	private RealVector v2;
	
	public MyCustomScoreProvider(AtomicReaderContext context, Query query, RealVector queryVector, Set<String> vocab) throws IOException {
		super(context);
		atomicReader = context.reader();
		//+1 per la query, è un documento in più
		N = atomicReader.maxDoc()+1;
		vocabulary = vocab;
		v2 = queryVector;
		MyCustomScoreProvider.query=query;
	}

	/* qui devo calcolare il mio score
	 * si accede al singolo documento
	 * 
	 * e se ne calcola la similarità con la query */
	@Override
	public float customScore(int doc, float subQueryScore, float valSrcScore)
			throws IOException {

		//calcolo di mappe <termine,tf-idf> per documento e query
		Map<String, Float> f1 = getDocumentWeights(atomicReader, doc);

		//dalla mappa si ricavano i vettori
		RealVector v1 = toRealVector(f1);

		//calcolo della coseno similarità
		float dotProduct = (float) v1.dotProduct(v2);
		float normalization = (float) (v1.getNorm() * v2.getNorm());
		return (float) (dotProduct / normalization);

	}


	/*dato un IndexReader (indice che permette di accere ai documenti indicizzati) 
	 * e l'id del documento corrente che si sta esaminando, costruisce una mappa
	 * dei termini e dei pesi tf-idf*/
	private Map<String, Float> getDocumentWeights(IndexReader reader, int docId)
			throws IOException {
		
		Terms vector = reader.getTermVector(docId, "segmentContent");
		Map<String, Integer> docFrequencies = new HashMap<>();
		Map<String, Integer> termFrequencies = new HashMap<>();
		Map<String, Float> tf_Idf_Weights = new HashMap<>();
		TermsEnum termsEnum = null;
		DocsEnum docsEnum = null;

		//itera sul vettore per avere il vettore di ogni termine presente nel documento
		termsEnum = vector.iterator(termsEnum);
		BytesRef text = null;
		//scorro ogni termine del documento
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			
			//metto nella mappa delle frequenze il termine e quante volte compare
			//tra i documenti, in quel campo specifico
			docFrequencies.put(term, reader.docFreq( new Term( "segmentContent", term ) ));

			docsEnum = termsEnum.docs(null, null);
			while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
				//vedo quanta frequenza ha il termine nel documento
				termFrequencies.put(term, docsEnum.freq());
			}
		}

		for ( String term : docFrequencies.keySet() ) {
			int tf = termFrequencies.get(term);
			int df = docFrequencies.get(term);

			//controllo nella query: se anche in questa è presente il termine, df aumenta di 1
			if (queryContains(term)) df++;


			float idf = (float) ( 1 + Math.log(N) - Math.log(df) );
			float w = tf * idf;
			tf_Idf_Weights.put(term, w);
			}

		return tf_Idf_Weights;
	}

	/* a partire da una mappa <termine, tf-idf> costruisce un vettore */
	public RealVector toRealVector(Map<String, Float> map) throws IOException {
		//il vettore ha dimensione di tutti i termini
		RealVector vector = new ArrayRealVector(vocabulary.size());
		
		int i = 0;
		float value = 0;
		
		for (String term : vocabulary) { //di tutti i termini

			if ( map.containsKey(term) ) { //se il documento corrente contiene il termine
				value = map.get(term); //allora come valore gli dai quello del termine
			}
			else {
				value = 0; //altrimenti zero
			}
			vector.setEntry(i++, value); //quindi metti nel vettore quel valore
		}
		return vector;
	}

	/* dato un termine, verifica che la query lo contenga */
	private static boolean queryContains(String term) {
		String textQuery = query.toString("segmentContent");
		String trimmed = textQuery.trim();
		String[] words = trimmed.split("\\s+");
		for (int i=0; i<words.length;i++) {
			if (words[i].equals(term)) 
				return true;
		}
		return false;
	}

	
	/*stampe per debugging*/
	
	public static void printMap(Map<String, Integer> map) {
		for ( String key : map.keySet() ) {
			System.out.println( "Term: " + key + ", value: " + map.get(key) );
		}
	}

	public static void printMapDouble(Map<String, Float> map) {
		for ( String key : map.keySet() ) {
			System.out.println( "Term: " + key + ", value: " + map.get(key) );
		}
	}
}
