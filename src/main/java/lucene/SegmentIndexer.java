package lucene;

import java.io.File;
import java.io.IOException;
import java.util.Set;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import lib.utils.NodeUtils;
import model.Segment;

/* Classe che si occupa dell'ndicizzazione dei segmenti */

public class SegmentIndexer {
	
	private IndexWriter writer;

	/*costruttore che richied come input il path della cartella dove verrà creato l'indice*/
	public SegmentIndexer(String indexDirectoryPath) throws IOException{
		Directory indexDirectory = 
				FSDirectory.open(new File(indexDirectoryPath));
		//utilizziamo un analyzer italiano
		Analyzer analyzer = new ItalianAnalyzer(Version.LUCENE_4_9);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		writer = new IndexWriter(indexDirectory, iwc);
	}

	public void close() throws CorruptIndexException, IOException{
		writer.close(); 
	}

	/* Dato un segmento, restituisce il documento di lucene 
	 * (metodo di supporto per la creazione dell'indice) */
	private Document getDocument(Segment segment) throws IOException{
		Document document = new Document();
		//
		//	      //index file contents
		//	      Field contentField = new Field(LuceneConstants.CONTENTS, 
		//	         new FileReader(file));
		//	      //index file name
		//	      Field fileNameField = new Field(LuceneConstants.FILE_NAME,
		//	         file.getName(),
		//	         Field.Store.YES,Field.Index.NOT_ANALYZED);
		//	      //index file path
		//	      Field filePathField = new Field(LuceneConstants.FILE_PATH,
		//	         file.getCanonicalPath(),
		//	         Field.Store.YES,Field.Index.NOT_ANALYZED);
		
		FieldType fieldType = new FieldType();
        fieldType.setIndexed(true);
        fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setTokenized(true);

		String content = NodeUtils.getNodesContent(segment.getW3cNodes());

		//si memorizza l'xPath e il contenuto del segmento
		document.add(new Field("segmentPath", segment.getAbsoluteXPath(), StringField.TYPE_STORED));
		document.add(new Field("segmentContent", content, fieldType));

		return document;
	}   

	/* Dato un segmento, si indicizza 
	 * (matodo di supporto) */
	private void indexSegment(Segment s) throws IOException{
		//	      System.out.println("Indexing "+file.getCanonicalPath());
		Document document = getDocument(s);
		writer.addDocument(document);
	}

	/* Si crea l'indice a partire da un set di segmenti 
	 * restituisce il numero di documenti indicizzati */
	public int createIndex(Set<Segment> secondPageSegments) 
			throws IOException{

		for (Segment s : secondPageSegments) {
			indexSegment(s);
		}
		
		return writer.numDocs();
	}
}
