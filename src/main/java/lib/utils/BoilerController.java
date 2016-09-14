package lib.utils;

import lib.utils.CleanHTMLTree;
import model.BlacklistElement;
import model.Page;
import model.Source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import database.MongoFacade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BoilerController {
	private List<Page> usedPagesForCleaning;
	//numero di pagine utilizzare per la pulizia
	private int numberCleanedPages = 5;
	MongoFacade facade = new MongoFacade("profiles_development");

	public BoilerController() {
	}
	
	/*prende un sito e individua il template da eliminare*/
	/*public String boilPages(Page pageToClean, Source site) throws Exception {
		String idSource = site.getId().toString();
		this.usedPagesForCleaning = new ArrayList<>();
		Document documentToClean = Jsoup.parse(pageToClean.getHtml());
		// prendere le prime N pagine del site
		this.usedPagesForCleaning = facade.getSourcePages(idSource,numberCleanedPages);
		if (this.usedPagesForCleaning.size() > 1) {
			System.out.println(this.usedPagesForCleaning.size());

			Page page = this.usedPagesForCleaning.get(0);
			System.out.println("2 "+page.toPrettyString());
			Document document = Jsoup.parse(page.getHtml());
			HashSet<BlacklistElement> blacklistedTxt = CleanHTMLTree.getHTMLElementsText(document);
			HashSet<BlacklistElement> blacklistedImg = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				System.out.println("2 "+page.toPrettyString());
				document = Jsoup.parse(page.getHtml());
				HashSet<BlacklistElement> tempTxt = CleanHTMLTree.getHTMLElementsText(document);
				HashSet<BlacklistElement> tempImg = CleanHTMLTree.getHTMLElementsImg(document);
				//mantiene in blacklistedTxt solo gli elementi contenuti in tempTxt, gli altri li scarta
				blacklistedTxt.retainAll(tempTxt);
				blacklistedImg.retainAll(tempImg);
				tempTxt.clear();
				tempImg.clear();
			}

			//        System.out.println(blacklistedTxt.size());
			//        System.out.println(blacklistedImg.size());

			// salvi in due field dell'oggetto site l'oggetto blacklistedTxt e blacklistedImg
			facade.updateBlackListes(site, blacklistedTxt, blacklistedImg);


			//ora, con le due liste, posso filtrare la mia pagina


			CleanHTMLTree.travers(documentToClean, blacklistedTxt, blacklistedImg);

			//per pulire un po' di heap di java
			blacklistedTxt.clear();
			blacklistedImg.clear();

			//TODO QUESTE DUE RIGHE QUI SONO SOLO PER DEBUGGING, POI CANCELLALE ASSIEME ALLA STAMPA AL RETURN DI SOTTO
			Document docToCleanParsed = Jsoup.parse(documentToClean.text());
			return docToCleanParsed.text();
		} //end if

		Document docToCleanParsed = Jsoup.parse(documentToClean.text());

		return docToCleanParsed.text();

		//return facade.getSourceWithId(site.getId().toString());
	} */

	/*prende una pagina e individua il template da eliminare*/
	public String boilPages(Page pageToClean) throws Exception {

		HashSet<BlacklistElement> blacklistedTxt = getBlackListTxt(pageToClean);
		HashSet<BlacklistElement> blacklistedImg = getBlackListImg(pageToClean);

		//ora, con le due liste, posso filtrare la mia pagina
		Document documentToClean = Jsoup.parse(pageToClean.getHtml());
		CleanHTMLTree.travers(documentToClean, blacklistedTxt, blacklistedImg);
		Document docToCleanParsed = Jsoup.parse(documentToClean.text());
		
		return docToCleanParsed.text();
	}
	
	/*data una pagina, restituisce la blacklist*/
	public HashSet<BlacklistElement> getBlackListTxt(Page page) throws Exception {
		Source site = facade.getSite(page);
		if (site.getBlacklistedTxt() == null) {
			return makeBlackListTxt(site);
		}
		return site.getBlacklistedTxt();
	}
	
	/*data una pagina, restituisce la blacklist*/
	public HashSet<BlacklistElement> getBlackListImg(Page page) throws Exception {
		Source site = facade.getSite(page);
		if (site.getBlacklistedImg() == null) {
			return makeBlackListImg(site);
		}
		return site.getBlacklistedImg();
	}

	/* dato un sito Source, crea e memorizza la blacklistTxt, salvandola anche nel database */
	public HashSet<BlacklistElement> makeBlackListTxt(Source site) throws Exception {
		String idSource = site.getId().toString();
		this.usedPagesForCleaning = new ArrayList<>();
		this.usedPagesForCleaning = facade.getSourcePages(idSource,numberCleanedPages);
		HashSet<BlacklistElement> blacklistedTxt = new HashSet<>();
		if (this.usedPagesForCleaning.size() > 1) {
			Page page = this.usedPagesForCleaning.get(0);
			Document document = Jsoup.parse(page.getHtml());
			blacklistedTxt = CleanHTMLTree.getHTMLElementsText(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				document = Jsoup.parse(page.getHtml());
				HashSet<BlacklistElement> tempTxt = CleanHTMLTree.getHTMLElementsText(document);
				blacklistedTxt.retainAll(tempTxt);
				tempTxt.clear();
			}
			facade.updateBlackList(site, blacklistedTxt);	
			return blacklistedTxt;
		}
		return blacklistedTxt;
	}

	public HashSet<BlacklistElement> makeBlackListImg(Source site) throws Exception {
		String idSource = site.getId().toString();
		this.usedPagesForCleaning = new ArrayList<>();
		this.usedPagesForCleaning = facade.getSourcePages(idSource,numberCleanedPages);
		HashSet<BlacklistElement> blacklistedImg = new HashSet<>();

		if (this.usedPagesForCleaning.size() > 1) {
			Page page = this.usedPagesForCleaning.get(0);
			Document document = Jsoup.parse(page.getHtml());
			blacklistedImg = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<this.usedPagesForCleaning.size();i++) {
				page = this.usedPagesForCleaning.get(i);
				document = Jsoup.parse(page.getHtml());
				HashSet<BlacklistElement> tempImg = CleanHTMLTree.getHTMLElementsImg(document);
				blacklistedImg.retainAll(tempImg);
				tempImg.clear();
			}
			facade.updateBlackList(site, blacklistedImg);	
			return blacklistedImg;
		}
		return blacklistedImg;
	}
}
