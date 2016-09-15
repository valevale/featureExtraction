package lib.utils;

import lib.utils.CleanHTMLTree;
import model.BlacklistElement;
import model.PageEntry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mongodb.morphia.query.MorphiaIterator;

import database.MongoFacade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class BoilerController {
	private List<PageEntry> usedPagesForCleaning = new ArrayList<>();
	//numero di pagine utilizzare per la pulizia
	private int numberCleanedPages = 5;
	MongoFacade facade = new MongoFacade("crawler_db");

	public BoilerController() {
	}

	/*prende una pagina e individua il template da eliminare*/
	public String boilPages(PageEntry pageToClean) throws Exception {

		//TODO refactoring del codice: parametrizza questi make e get, non è difficile
//		HashSet<BlacklistElement> blacklistedTxt = getBlackListTxt(pageToClean);
//		HashSet<BlacklistElement> blacklistedImg = getBlackListImg(pageToClean);
		HashSet<BlacklistElement> blacklistedTxt = makeBlackListTxt(pageToClean);
		HashSet<BlacklistElement> blacklistedImg = makeBlackListImg(pageToClean);

		//ora, con le due liste, posso filtrare la mia pagina
		Document documentToClean = Jsoup.parse(pageToClean.getPage().getBody());
		CleanHTMLTree.travers(documentToClean, blacklistedTxt, blacklistedImg);
		Document docToCleanParsed = Jsoup.parse(documentToClean.text());
		
		return docToCleanParsed.text();
	}
	
	/*data una pagina, restituisce la blacklist*/
//	public HashSet<BlacklistElement> getBlackListTxt(PageEntry page) throws Exception {
//		Source site = facade.getSite(page);
//		if (site.getBlacklistedTxt() == null) {
//			return makeBlackListTxt(site);
//		}
//		return site.getBlacklistedTxt();
//	}
//	
//	/*data una pagina, restituisce la blacklist*/
//	public HashSet<BlacklistElement> getBlackListImg(PageEntry page) throws Exception {
//		Source site = facade.getSite(page);
//		if (site.getBlacklistedImg() == null) {
//			return makeBlackListImg(site);
//		}
//		return site.getBlacklistedImg();
//	}

	/* dato un sito Source, crea e memorizza la blacklistTxt, salvandola anche nel database */
	@SuppressWarnings("rawtypes")
	public HashSet<BlacklistElement> makeBlackListTxt(PageEntry pageToClean) throws Exception {
		//passo 1. prendo N pagine da pulire
		String source = pageToClean.getCrawlingId();
		Iterator<PageEntry> iterator = facade.pageEntryIterator(source);
		
		//ora iteriamo fino a un certo numero le pagine, facendo attenzione che non ci abbiano restituito la
		//pageEntry che già abbiamo!
		try {
			int i=0;
			while (iterator.hasNext() && i<=numberCleanedPages && iterator.next() != null) {
				PageEntry page = iterator.next();
				if (page.getId() != pageToClean.getId()) {
					this.usedPagesForCleaning.add(page);
					i++;
				}
			}
		} finally {
			((MorphiaIterator) iterator).close();
		}
		
		HashSet<BlacklistElement> blacklistedTxt = new HashSet<>();
		if (this.usedPagesForCleaning.size() > 1) {
			PageEntry page = this.usedPagesForCleaning.get(0);
			Document document = Jsoup.parse(page.getPage().getBody());
			blacklistedTxt = CleanHTMLTree.getHTMLElementsText(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				document = Jsoup.parse(page.getPage().getBody());
				HashSet<BlacklistElement> tempTxt = CleanHTMLTree.getHTMLElementsText(document);
				blacklistedTxt.retainAll(tempTxt);
				tempTxt.clear();
			}
			//TODO poi fallo!!!
			//facade.updateBlackList(site, blacklistedTxt);	
			return blacklistedTxt;
		}
		return blacklistedTxt;
	}
	
	/* dato un sito Source, crea e memorizza la blacklistTxt, salvandola anche nel database */
	@SuppressWarnings("rawtypes")
	public HashSet<BlacklistElement> makeBlackListImg(PageEntry pageToClean) throws Exception {
		//passo 1. prendo N pagine da pulire
		String source = pageToClean.getCrawlingId();
		Iterator<PageEntry> iterator = facade.pageEntryIterator(source);
		
		//ora iteriamo fino a un certo numero le pagine, facendo attenzione che non ci abbiano restituito la
		//pageEntry che già abbiamo!
		try {
			int i=0;
			while (iterator.hasNext() && i<=numberCleanedPages) {
				PageEntry page = iterator.next();
				if (page.getId() != pageToClean.getId()) {
					this.usedPagesForCleaning.add(page);
					i++;
				}
			}
		} finally {
			((MorphiaIterator) iterator).close();
		}
		
		HashSet<BlacklistElement> blacklistedImg = new HashSet<>();
		if (this.usedPagesForCleaning.size() > 1) {
			PageEntry page = this.usedPagesForCleaning.get(0);
			Document document = Jsoup.parse(page.getPage().getBody());
			blacklistedImg = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				document = Jsoup.parse(page.getPage().getBody());
				HashSet<BlacklistElement> tempImg = CleanHTMLTree.getHTMLElementsImg(document);
				blacklistedImg.retainAll(tempImg);
				tempImg.clear();
			}
			//TODO poi fallo!!!
			//IMMAGINE NON TXT facade.updateBlackList(site, blacklistedTxt);	
			return blacklistedImg;
		}
		return blacklistedImg;
	}
}
