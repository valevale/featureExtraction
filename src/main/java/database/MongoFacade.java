package database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import model.BlacklistElement;
import model.Page;
import model.Source;

public class MongoFacade {
	String dbName;
	final Morphia morphia = new Morphia();

	public MongoFacade(String dbName) {
		this.dbName=dbName;
	}

	/* nLimit: numero di pagine che si vogliono recuperare
	 * nOffset: offset */
	public List<Page> getPages(int nLimit, int nOffset) {
		Datastore datastore = getDatastore();
		Query<Page> query = datastore.createQuery(Page.class).offset(nOffset).limit(nLimit);
		return query.asList();
	}

	/* nLimit: numero di pagine che si vogliono recuperare
	 * nOffset: offset */
	public List<Source> getSources(int nLimit, int nOffset) {
		//System.out.println("getting sources");
		Datastore datastore = getDatastore();
		Query<Source> query = datastore.createQuery(Source.class).retrievedFields(false, "discoveredPages").offset(nOffset).limit(nLimit);
		List<Source> s = query.asList();
		//System.out.println("sources retrieved");
		return s;
	}

	/* idSite: id del sito da cui prendere le pagine del suo campi 'discoveredPages'
	 * limit: numero di pagine da recuperare 
	 * restituisce la lista di pagine richieste */
	public List<Page> getSourcePages(String idSite, int limit) {
		List<Page> retrievedPages = new ArrayList<>();
		//la proiezione traduce il seguente comando:
		//db.sources.find({"_id": ObjectId("5750678a3387e31f516fa1ab")}, {discoveredPages: {$slice: N}});
		BasicDBObject projection = new BasicDBObject("discoveredPages", new BasicDBObject("$slice", limit));
		ObjectId id = new ObjectId(idSite);
		BasicDBObject selection = new BasicDBObject("_id", id);
		BasicDBObject filter = new BasicDBObject();
		filter.append("_id", id);

		//Query per ottenere il sito con solo le prime N pagine
		List<DBObject> sourcesRetrieved = getDatastore().getCollection(Source.class)
				.find(selection, projection).toArray();

		//parsing della lista per ottenere le id delle pagine
		DBObject singleSource = sourcesRetrieved.get(0);
		BasicDBList slicedDiscoveredPages = (BasicDBList) singleSource.get("discoveredPages");
		for (int i = 0; i<slicedDiscoveredPages.size(); i++) {
			JSONObject singlePage = new JSONObject(slicedDiscoveredPages.get(i).toString());
			String pageId = singlePage.get("$id").toString();

			Page page = getPageWithId(pageId);

			retrievedPages.add(page);
		}
		return retrievedPages;
	}

	/*dato un certo id, restituisce la pagina con quell'id */
	public Page getPageWithId(String id) {
		ObjectId i = new ObjectId(id);
		return getDatastore().get(Page.class, i);
	}

	/* data una certa pagina, restituisce il sito Source che contiene, nel campo array "discoveredPages",
	 * quella pagina */
	public Source getSite(Page page) {
		return getDatastore().createQuery(Source.class)
				.field("discoveredPages").hasThisElement(page)
				.retrievedFields(false, "discoveredPages")
				.get();
	}

	/* aggiorna entrambe le liste
	 * */
	public void updateBlackListes(Source site, HashSet<BlacklistElement> blacklistedTxt, HashSet<BlacklistElement> blacklistedImg) {
		Datastore datastore = getDatastore();
		UpdateOperations<Source> ops;        
		Query<Source> updateQuery = datastore.createQuery(Source.class).field("_id").equal(site.getId());	    
		ops = datastore.createUpdateOperations(Source.class).set("blacklistedTxt", blacklistedTxt);
		datastore.update(updateQuery, ops);
		ops = datastore.createUpdateOperations(Source.class).set("blacklistedImg", blacklistedImg);
		datastore.update(updateQuery, ops);
	}

	/* dato un sito e una blackList, aggiorna quel sito con quella blacklist */
	public void updateBlackList(Source site, HashSet<BlacklistElement> blacklist) {
		Datastore datastore = getDatastore();
		UpdateOperations<Source> ops;
		Query<Source> updateQuery = datastore.createQuery(Source.class).field("_id").equal(site.getId());
		ops = datastore.createUpdateOperations(Source.class).set("blacklistedTxt", blacklist);
		datastore.update(updateQuery, ops);
	}
	
	/* data una pagina, controlla se quella pagina è monitorata */
	public boolean isPageMonitored(Page page) {
		Source site = getSite(page);
		return site.getMonitored();
	}

	/* iteratore per la collezione pages */
	public Iterator<Page> pageIterator() {
		return getDatastore().createQuery(Page.class).iterator();
	}

	/* fornisce il datastore per effettuare le operazioni su mongo */
	private Datastore getDatastore() {
		morphia.mapPackage("model");
		Datastore datastore = morphia.createDatastore(new MongoClient(), dbName);
		//datastore.ensureIndexes();
		return datastore;
	}
}
