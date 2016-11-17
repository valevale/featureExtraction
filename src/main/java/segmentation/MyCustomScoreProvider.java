package segmentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	private IndexVocabulary indexVocabulary;
	//il vocabolario
	private Set<String> vocabulary;

	public MyCustomScoreProvider(AtomicReaderContext context, Query query) throws IOException {
		super(context);
		atomicReader = context.reader();
		MyCustomScoreProvider.query=query;
		//+1 per la query, è un documento in più
		N = atomicReader.maxDoc()+1;
		indexVocabulary = IndexVocabulary.getInstance(atomicReader);
		vocabulary = indexVocabulary.getVocabulary();
		//		System.out.println("NUOVO VOCABOLARIO! "+vocabulary.size());
		//TODO per velocizzare, crea il vettore di query qui
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
		Map<String, Float> f2 = getQueryWeights(atomicReader, query);

		//dalla mappa si ricavano i vettori
		RealVector v1 = toRealVector(f1);
		//		System.out.println( "V1: " +v1 );
		RealVector v2 = toRealVector(f2);
		//		System.out.println( "V2: " +v2 );

		//calcolo della coseno similarità
		float dotProduct = (float) v1.dotProduct(v2);
		//		System.out.println( "Dot: " + dotProduct);
		//		System.out.println( "V1_norm: " + v1.getNorm() + ", V2_norm: " + v2.getNorm() );
		float normalization = (float) (v1.getNorm() * v2.getNorm());
		//		System.out.println( "Norm: " + normalization);
//		System.out.println("COS SIM: "+ (dotProduct / normalization));
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
//			int docFreq = termsEnum.docFreq();
			
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
			//			System.out.printf("Term: %s - tf: %d, df: %d, idf: %f, w: %f\n", term, tf, df, idf, w);
		}

		//		System.out.println( "Printing docFrequencies:" );
		//		printMap(docFrequencies);
		//
		//		System.out.println( "Printing termFrequencies:" );
		//		printMap(termFrequencies);

		//		System.out.println( "Printing if/idf weights:" );
		//		printMapDouble(tf_Idf_Weights);
		return tf_Idf_Weights;
	}

	
	/*metodo che permette, analogamente, la costruzione di una mappa <termine,tf-idf>
	 * per ogni termine della query*/
	private Map<String, Float> getQueryWeights(IndexReader reader, Query query)
			throws IOException {
		HashSet<Term> vector = new HashSet<Term>();
		//TODO vedi come estrae i termini dalla query, se oltre
		//alle (), elimina altro
		query.extractTerms(vector);

		Map<String, Float> docFrequencies = new HashMap<>();
		Map<String, Float> termFrequencies = new HashMap<>();
		Map<String, Float> tf_Idf_Weights = new HashMap<>();

		//itera sul vettore per avere il vettore di ogni termine presente nel documento
		Iterator<Term> it = vector.iterator();
		//scorro ogni termine del documento
		while (it.hasNext()) {
			Term term = it.next();
			String text = term.text();
			//            int docFreq = termsEnum.docFreq();
			
			//metto nella mappa delle frequenze il termine e quante volte compare
			//tra i documenti, in quel campo specifico

			//problema: il termine non compare nel reader. quindi qui, se la frequenza è zero, sostituiscila con
			//1, cioè l'unico documento in cui compare il termine: la query
			if (reader.docFreq(term) == 0) {
				//				System.out.println("metto dentro questo numero "+termFreqInDoc(text, query.toString("segmentContent")));
				//				if (termFreqInDoc(text, query.toString("segmentContent")) == 0) {
				////					System.out.println("query\n"+query.toString("segmentContent"));
				////					
				////					System.out.println("vettore");
				////					vector.forEach(v -> {
				////						System.out.print(v.text()+ " ");
				////					});
				//					termFreqInDoc(text, query.toString("segmentContent"));
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//					System.out.println("STA INTRODUCENDO 0");
				//				}
				//				docFrequencies.put(text, termFreqInDoc(text, query.toString("segmentContent")));
				docFrequencies.put(text, (float) 1);
			}
			else
				docFrequencies.put(text, (float) (reader.docFreq(term)+1));


			//ora, per ogni termine, vedo quante volte compare nel documento
			termFrequencies.put(text, termFreqInDoc(text, query.toString("segmentContent")));

			//infine aggiungo ai termini quello esaminato
			//come vediamo terms è un set
			//noi partiamo da un set che già contiene tutti i termini dell'indice
			//due idee:
			//-aggiungiamo i termini in più -> quindi terms.add è presente
			//in toRealVector del documento è tutto a posto
			//la cosa che va fatta, AL TERMINE DELLA QUERY
			//è che bisogna rimuovere i termini aggiunti
			//si può memorizzare nell'oggetto terms un set originale, e sostituirlo al current

			//-prendi i termini e aggiungilo in un nuovo set!!! (così sono separati)

			vocabulary.add(text);


			//-NON aggiungiamo i termini in più -> qui non fai niente, in toRealVector della query
			//pure, perché si scorre il vettore dei termini dell'indice
			//le parole della query che non appartengono a terms vengono semplicemente ignorate
			//è come se contassero zero, non avessero peso. e credo vada bene...
			//NO, perché:
			//query: ciao giuseppe
			//documento: ciao
			//similarità: 1
		}

		for ( String termin : docFrequencies.keySet() ) {
			float tf = termFrequencies.get(termin);
			float df = docFrequencies.get(termin);
			float idf = (float) ( 1 + Math.log(N) - Math.log(df) );
			float w = tf * idf;
			tf_Idf_Weights.put(termin, w);
			//			System.out.printf("Term: %s - tf: %f, df: %f, idf: %f, w: %f\n", termin, tf, df, idf, w);
		}

		//		System.out.println( "Printing docFrequencies:" );
		//		printMap(docFrequencies);
		//
		//		System.out.println( "Printing termFrequencies:" );
		//		printMap(termFrequencies);
		//
		//		System.out.println( "Printing if/idf weights:" );
		//		printMapDouble(tf_Idf_Weights);
		return tf_Idf_Weights;
	}

	/* dato un termine e il documento di cui fa parte, calola la frequenza (normalizzata)
	 * di quel termine nel documento */
	public static float termFreqInDoc(String term, String doc) {
		//				System.out.println(term);
		//				System.out.println(doc);
		int count = 0;
		//		String cleaned = doc.replaceAll("[^a-zA-Z0-9.' ]+", "");
		String cleaned = doc.replaceAll("[()]", " ");
		//		System.out.println("cleaned\n"+cleaned);
		String trimmed = cleaned.trim();
		String[] words = trimmed.split("\\s+");
		for(int i=0;i<words.length;i++) {
			String w = words[i].toLowerCase();
			if (w.equals(term)) count++;
		}
		//				System.out.println(count);
		//				System.out.println(maxTermFreq(words));
		//				System.out.println("freq: "+count / maxTermFreq(words));
		return count / maxTermFreq(words);
	}

	/*restituisce la frequenza, nel documento, del termine più frequente*/
	public static float maxTermFreq(String[] words) {
		List<Integer> frequencies = new ArrayList<Integer>();
		for(int i=0;i<words.length;i++) {
			String w = words[i].toLowerCase();
			frequencies.add(getFreq(w, words));
		}
		return (float) Collections.max(frequencies);
	}

	/*dato un elemento, calcola quante volte compare nell'array*/
	public static int getFreq(String word, String[] words) {
		int count = 0;
		for(int i=0;i<words.length;i++) {
			String w = words[i].toLowerCase();
			if (w.equals(word)) count++;
		}
		return count;
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
