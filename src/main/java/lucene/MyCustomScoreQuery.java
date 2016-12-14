package lucene;

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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

/* Query customizzata per il calcolo della coseno similarità
 * Si affida a uno score provider costumizzato per il calcolo dello score
 * */

public class MyCustomScoreQuery extends CustomScoreQuery {

	//il vocabolario
	private Set<String> vocabulary;

	//oggetto che memorizza il vocabolario del corpus
	private IndexVocabulary indexVocabulary;
	
	//il numero totale dei documenti del corpus (+ la query)
	private static int N;
	
	
	public MyCustomScoreQuery(Query subQuery) {
		super(subQuery);
	}

	@Override
	public CustomScoreProvider getCustomScoreProvider(final AtomicReaderContext atomicContext) throws IOException {
		//atomicReaderContext permette l'esplorazione dell'indice.
		//TODO pensa se è il caso di mettere qui il calcolo del vettore query e del vocabolario.
		
		//INIZIO MODIFICA
		AtomicReader atomicReader;
		atomicReader = atomicContext.reader();
		//+1 per la query, è un documento in più
		N = atomicReader.maxDoc()+1;
		indexVocabulary = IndexVocabulary.getInstance(atomicReader);
		vocabulary = indexVocabulary.getVocabulary();
		Map<String, Float> f2 = getQueryWeights(atomicReader, getSubQuery());
		RealVector v2 = toRealVector(f2);
		//FINE MODIFICA
//		return new MyCustomScoreProvider(atomicContext, getSubQuery());
		return new MyCustomScoreProvider(atomicContext, getSubQuery(), v2, vocabulary);
	}
	
	
	/*metodo che permette, analogamente, la costruzione di una mappa <termine,tf-idf>
	 * per ogni termine della query*/
	private Map<String, Float> getQueryWeights(IndexReader reader, Query query)
			throws IOException {
		HashSet<Term> vector = new HashSet<Term>();
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
}
