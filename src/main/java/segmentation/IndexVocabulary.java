package segmentation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/* Questa classe permette di possedere un unico vocabolario di termini,
 * per il calcolo della coseno similarità
 * Si tratta di un singleton. Viene generata una nuova istanza quando
 * viene fornito al costruttore un nuovo IndexReader (che dovrebbe corrispondere a un nuovo indice)
 * */

public class IndexVocabulary {

	private static IndexVocabulary instance = null;
	private Set<String> vocabulary;
	private static IndexReader indexReader = null;

	public static IndexVocabulary getInstance(IndexReader ir) throws IOException {
		if (instance == null)
			instance = new IndexVocabulary(ir);
		else
			if (!ir.equals(indexReader))
				instance = new IndexVocabulary(ir);
		return instance;
	}

	private IndexVocabulary(IndexReader ir) throws IOException {
		IndexVocabulary.indexReader=ir;
		getTerms();
	}

	public Set<String> getVocabulary() {
		Set<String> voc = new HashSet<String>(this.vocabulary);
		return voc;
	}

	/* creo il vocabolario, partendo dall'indexReader */
	private void getTerms() throws IOException {
		//ora mi scorro ogni documento
		vocabulary = new HashSet<String>();
		for (int docId = 0; docId<indexReader.maxDoc(); docId++) {
			Terms vector = indexReader.getTermVector(docId, "segmentContent");
			if (vector != null) {
				TermsEnum termsEnum = null;
				termsEnum = vector.iterator(termsEnum);
				BytesRef text = null;
				while ((text = termsEnum.next()) != null) {
					String term = text.utf8ToString();
					vocabulary.add(term);
				}
			}
		}
	}
}
