package lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import lib.utils.NodeUtils;
import model.Segment;

/* Classe che si occupa di effettuare delle query su un indice di segmenti */

public class SegmentSearcher {

	private IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;

	/* Il costruttore chiede come input il path della cartella dov'è memorizzato l'indice*/
	public SegmentSearcher(String indexDirectoryPath) 
			throws IOException{
		IndexReader reader = 
				DirectoryReader.open(FSDirectory.open(new File(indexDirectoryPath)));
		indexSearcher = new IndexSearcher(reader);
		//analyser italiano
		Analyzer analyzer = new ItalianAnalyzer(Version.LUCENE_4_9);
		queryParser = new QueryParser(Version.LUCENE_4_9, "segmentContent", analyzer);
	}

	/* Dato un segmento, viene elaborata una query estraendo il suo contenuto
	 * e viene restituito un insieme di risultati della ricerca */
	public TopDocs search(Segment searchSegment) 
			throws IOException, ParseException{
		String searchQuery = NodeUtils.getNodesContent(searchSegment.getW3cNodes());

		try {
			query = queryParser.parse(QueryParser.escape(searchQuery));
		}
		catch (Exception e) {
			searchQuery = searchQuery.substring(0,1000);
			query = queryParser.parse(QueryParser.escape(searchQuery));
		}

		//Viene utilizzata una query costumizzata per permettere
		//la ricerca basata sulla metrica della coseno similarità
		CustomScoreQuery customQuery = new MyCustomScoreQuery(query);
		TopDocs topDocs = indexSearcher.search(customQuery, 30);

		return topDocs;
	}

	/* Dato un oggetto 'risultato', rstituisce il documento di lucene corrispondente
	 * Si utilizza per ottenere il contenuto del documento */
	public Document getDocument(ScoreDoc scoreDoc) 
			throws CorruptIndexException, IOException{
		return indexSearcher.doc(scoreDoc.doc);	
	}
}
