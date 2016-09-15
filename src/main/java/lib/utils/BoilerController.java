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

	public List<PageEntry> getUsedPagesForCleaning() {
		return this.usedPagesForCleaning;
	}
	
	public void setUsedPagesForCleaning(List<PageEntry> usedPagesForCleaning) {
		this.usedPagesForCleaning = usedPagesForCleaning;
	}

	/*prende una pagina e individua il template da eliminare*/
	@SuppressWarnings("rawtypes")
	public String boilPages(PageEntry pageToClean) throws Exception {
		//passo 1. prendo N pagine da pulire
		String source = pageToClean.getCrawlingId();
		Iterator<PageEntry> iterator = facade.pageEntryIterator(source);
		int counter=0;
		
		//ora iteriamo fino a un certo numero le pagine, facendo attenzione che non ci abbiano restituito la
		//pageEntry che già abbiamo!
		try {
			while (iterator.hasNext() && counter<numberCleanedPages) {
				System.out.println("NUMERO DI PAGINE PRESE "+counter);
				PageEntry page = iterator.next();
				if (!page.getId().equals(pageToClean.getId())) {
					this.usedPagesForCleaning.add(page);
					counter++;
				}
			}
		} finally {
			((MorphiaIterator) iterator).close();
		}

		//TODO refactoring del codice: parametrizza questi make e get, non è difficile
		//		HashSet<BlacklistElement> blacklistedTxt = getBlackListTxt(pageToClean);
		//		HashSet<BlacklistElement> blacklistedImg = getBlackListImg(pageToClean);
		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(pageToClean, "txt");
		HashSet<BlacklistElement> blacklistedImg = makeBlackList(pageToClean, "img");

		//ora, con le due liste, posso filtrare la mia pagina
		Document documentToClean = Jsoup.parse(pageToClean.getPage().getBody());
		CleanHTMLTree.travers(documentToClean, blacklistedTxt, blacklistedImg);
		Document docToCleanParsed = Jsoup.parse(documentToClean.text());
		
		//TODO rimettilo!
		//this.usedPagesForCleaning.clear();
		
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
	public HashSet<BlacklistElement> makeBlackList(PageEntry pageToClean, String parameter) throws Exception {

		HashSet<BlacklistElement> blacklist = new HashSet<>();
		if (this.usedPagesForCleaning.size() > 1) {
			PageEntry page = this.usedPagesForCleaning.get(0);
			Document document = Jsoup.parse(page.getPage().getBody());
			if (parameter.equals("txt"))
				blacklist = CleanHTMLTree.getHTMLElementsText(document);
			else //img
				blacklist = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				document = Jsoup.parse(page.getPage().getBody());
				HashSet<BlacklistElement> temp;
				if (parameter.equals("txt"))
					temp = CleanHTMLTree.getHTMLElementsText(document);
				else //img
					temp = CleanHTMLTree.getHTMLElementsImg(document);
				blacklist.retainAll(temp);
				temp.clear();
			}
			//TODO poi fallo!!!
			//facade.updateBlackList(site, blacklistedTxt);	
			return blacklist;
		}
		return blacklist;
	}

	/* dato un sito Source, crea e memorizza la blacklistTxt, salvandola anche nel database */
	//	@SuppressWarnings("rawtypes")
	//	public HashSet<BlacklistElement> makeBlackListImg(PageEntry pageToClean) throws Exception {
	//		HashSet<BlacklistElement> blacklistedImg = new HashSet<>();
	//		if (this.usedPagesForCleaning.size() > 1) {
	//			PageEntry page = this.usedPagesForCleaning.get(0);
	//			Document document = Jsoup.parse(page.getPage().getBody());
	//			blacklistedImg = CleanHTMLTree.getHTMLElementsImg(document);
	//
	//			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
	//				page = this.usedPagesForCleaning.get(i);
	//				document = Jsoup.parse(page.getPage().getBody());
	//				HashSet<BlacklistElement> tempImg = CleanHTMLTree.getHTMLElementsImg(document);
	//				blacklistedImg.retainAll(tempImg);
	//				tempImg.clear();
	//			}
	//			//TODO poi fallo!!!
	//			//IMMAGINE NON TXT facade.updateBlackList(site, blacklistedTxt);	
	//			return blacklistedImg;
	//		}
	//		return blacklistedImg;
	//	}
}
