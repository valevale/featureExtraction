package segmentation;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import lib.utils.CleanHTMLTree;
import model.BlacklistElement;
import model.DomRepToClean;
import model.Source;

public class DocumentCleaner {
	private static DocumentCleaner instance = null;

	public static DocumentCleaner getInstance() {
		if (instance == null)
			instance = new DocumentCleaner();
		return instance;
	}

	private DocumentCleaner() {
	}
	
	/* dato un documento, lo pulisce dal template
	 * richiede anche un massimo di 5 pagine per la pulizia del template */
	public Document removeTemplate(Document document, int sourcePar,
			String cartella) throws Exception {
		List<Document> usedPagesForCleaning = new ArrayList<>();

//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=1; i<=5;i++) {
			try {
				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
						new FileReader(new File(cartella+"pag"+sourcePar+"_"+i+".html")))));
			}
			catch (Exception e) {
				System.out.println("Errore pagina non trovata"+i + ": " + e);
			}
		}
		return clean(document, usedPagesForCleaning);
		
	}
	
	/* dato un documento, lo pulisce dal template
	 * richiede anche un massimo di 5 pagine per la pulizia del template */
	public Document removeTemplate_server(Document document, String source) throws Exception {
		List<Document> usedPagesForCleaning = new ArrayList<>();
		DomRepToClean drtc = DomRepToClean.getInstance();
//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=0; i<=4;i++) {
			try {
				usedPagesForCleaning.addAll(drtc.getPagesToClean(source));
			}
			catch (Exception e) {
				System.out.println("Errore pagina non trovata"+i + ": " + e);
			}
		}
		return clean(document, usedPagesForCleaning);
	}
	
	/* dato un documento, lo pulisce dal template
	 * richiede anche un massimo di 5 pagine per la pulizia del template */
	public Document removeTemplate_server(Document document, Source source) throws Exception {
		List<Document> usedPagesForCleaning = new ArrayList<>();

//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=0; i<=4;i++) {
			try {
//				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
//						new FileReader(new File(cartella+"pag"+sourcePar+"_"+i+".html")))));
				usedPagesForCleaning.add(Jsoup.parse(source.getPages().get(i).getHtml()));
			}
			catch (Exception e) {
				System.out.println("Errore pagina non trovata"+i + ": " + e);
			}
		}
		return clean(document, usedPagesForCleaning);
	}
	
	/* Prende come input un documento jsoup da pulire, assieme a una lista di documenti
	 * che vengono usati per la pulizia
	 * e restituisce un documento pulito del template comune alle pagine */
	public Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {

		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(docToClean, "txt", usedPagesForCleaning);
		HashSet<BlacklistElement> blacklistedImg = makeBlackList(docToClean, "img", usedPagesForCleaning);

		CleanHTMLTree.travers(docToClean, blacklistedTxt, blacklistedImg);
		return docToClean;
	}

	public static HashSet<BlacklistElement> makeBlackList(Document docToClean, String parameter, List<Document> usedPagesForCleaning) throws Exception {

		HashSet<BlacklistElement> blacklist = new HashSet<>();
		if (usedPagesForCleaning.size() > 1) {
			Document document = usedPagesForCleaning.get(0);
			if (parameter.equals("txt"))
				blacklist = CleanHTMLTree.getHTMLElementsText(document);
			else //img
				blacklist = CleanHTMLTree.getHTMLElementsImg(document);

			for (int i=1; i<usedPagesForCleaning.size();i++) {
				document = usedPagesForCleaning.get(i);
				HashSet<BlacklistElement> temp;
				if (parameter.equals("txt"))
					temp = CleanHTMLTree.getHTMLElementsText(document);
				else //img
					temp = CleanHTMLTree.getHTMLElementsImg(document);
				blacklist.retainAll(temp);
			}
			return blacklist;
		}
		return blacklist;
	}
}
