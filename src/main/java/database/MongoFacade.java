package database;


import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;


import com.mongodb.MongoClient;

import model.PageEntry;
import model.QueryEntry;
import model.Source;
import model.WebPage;

public class MongoFacade {
	String dbName;
	final Morphia morphia = new Morphia();

	public MongoFacade(String dbName) {
		this.dbName=dbName;
	}

	/* nLimit: numero di pagine che si vogliono recuperare
	 * nOffset: offset */
	public List<PageEntry> getPages(int nLimit, int nOffset) {
		Datastore datastore = getDatastore();
		Query<PageEntry> query = datastore.createQuery(PageEntry.class).offset(nOffset).limit(nLimit);
		return query.asList();
	}
	/* iteratore per la collezione pages */
	public Iterator<PageEntry> pageEntryIterator() {
		return getDatastore().createQuery(PageEntry.class).iterator();
	}
	
	/* iteratore per la collezione sources */
	public Iterator<Source> sourceIterator() {
		return getDatastore().createQuery(Source.class).iterator();
	}
	
	public Iterator<QueryEntry> queryEntryIterator() {
		return getDatastore().createQuery(QueryEntry.class).iterator();
	}
	
	/* iteratore per la collezione pages con un certo crawling_id */
	public Iterator<PageEntry> pageEntryIterator(String crawling_id) {
		return getDatastore().createQuery(PageEntry.class).field("crawling_id").equal(crawling_id).iterator();
	}

	/* iteratore per la collezione pages con un certo crawling_id */
	public Iterator<QueryEntry> getQueries() {
		return getDatastore().createQuery(QueryEntry.class).iterator();
	}
	
	/*dato un certo id, restituisce la pagina con quell'id */
	public PageEntry getPageEntryWithId(String id) {
		ObjectId i = new ObjectId(id);
		return getDatastore().get(PageEntry.class, i);
	}
	
	/* iteratore per la collezione pages */
	public PageEntry getPageWithUrl(String url) {
		return getDatastore().createQuery(PageEntry.class).field("page.url").equal(url).get();
	}
	
	/*dato un certo id, restituisce la pagina con quell'id */
	public WebPage getWebPageWithId(String id) {
		ObjectId i = new ObjectId(id);
		return getDatastore().get(WebPage.class, i);
	}
	
	/*dato un certo id, restituisce il dominio con quell'id */
	public Source getSourceWithId(String id) {
		ObjectId i = new ObjectId(id);
		return getDatastore().get(Source.class, i);
	}

	// per ora non storiamo niente nel db, ciò che dobbiamo capire è se per
	//ogni pagina dobbiamo memorizzare la lista? credo di sì, ma riflettici
//	/* aggiorna entrambe le liste
//	 * */
//	public void updateBlackListes(Source site, HashSet<BlacklistElement> blacklistedTxt, HashSet<BlacklistElement> blacklistedImg) {
//		Datastore datastore = getDatastore();
//		UpdateOperations<Source> ops;        
//		Query<Source> updateQuery = datastore.createQuery(Source.class).field("_id").equal(site.getId());	    
//		ops = datastore.createUpdateOperations(Source.class).set("blacklistedTxt", blacklistedTxt);
//		datastore.update(updateQuery, ops);
//		ops = datastore.createUpdateOperations(Source.class).set("blacklistedImg", blacklistedImg);
//		datastore.update(updateQuery, ops);
//	}
//
//	/* dato un sito e una blackList, aggiorna quel sito con quella blacklist */
//	public void updateBlackList(Source site, HashSet<BlacklistElement> blacklist) {
//		Datastore datastore = getDatastore();
//		UpdateOperations<Source> ops;
//		Query<Source> updateQuery = datastore.createQuery(Source.class).field("_id").equal(site.getId());
//		ops = datastore.createUpdateOperations(Source.class).set("blacklistedTxt", blacklist);
//		datastore.update(updateQuery, ops);
//	}
	
	public Iterator<WebPage> getWebPagesWithQueryId(String id) {
		Query<WebPage> q = getDatastore().find(WebPage.class)
				.field("query")
				.equal(getDatastore().get(QueryEntry.class,new ObjectId(id)));
		return q.iterator();
	}
	
	public boolean isValidated(WebPage webpage) {
		//prendi la source di quella pagina web
		Query<Source> q = getDatastore().find(Source.class)
				.field("discoveredPages").equal(getDatastore().get(WebPage.class,webpage.getId()));
		return (q.asList().size()>0);
	}

	/* fornisce il datastore per effettuare le operazioni su mongo */
	private Datastore getDatastore() {
		morphia.mapPackage("model");
		Datastore datastore = morphia.createDatastore(new MongoClient(), dbName);
		//datastore.ensureIndexes();
		return datastore;
	}
}
