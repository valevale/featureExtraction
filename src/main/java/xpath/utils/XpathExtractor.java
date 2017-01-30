package xpath.utils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import lib.utils.CleanHTMLTree;
import model.BlacklistElement;
import model.Xpath;
import segmentation.WebPageSegmentator;

public class XpathExtractor {

	XpathApplier xpapplier;
	XpathMaker xpmaker;
	WebPageSegmentator segmentator;
	
	private static XpathExtractor instance = null;

	public static XpathExtractor getInstance() {
		if (instance == null)
			instance = new XpathExtractor();
		return instance;
	}

	private XpathExtractor() {
		xpapplier = XpathApplier.getInstance();
		xpmaker = XpathMaker.getInstance();
		segmentator = WebPageSegmentator.getInstance();
	}
	
	/* dato un documento, restituisce gli xpath dei segmenti che lo compongono
	 * richiede anche un massimo di 5 pagine per la pulizia del template */
//	public Set<Tuple2<String,Node>> getXPathsFromDocument(Document doc, int par,
//			String cartella, double parameterTextFusion) throws Exception {
	
	public Set<Xpath> getXPathsFromDocument(Document doc, int par,
			String cartella, double parameterTextFusion) throws Exception {
		//pulire la pagina

		List<Document> usedPagesForCleaning = new ArrayList<>();

//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=1; i<=5;i++) {
			try {
				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
						new FileReader(new File(cartella+"pag"+par+"_"+i+".html")))));
			}
			catch (Exception e) {
				System.out.println("Errore pagina "+i + ": " + e);
			}
		}
		Document docClean = clean(doc, usedPagesForCleaning);

		//segmentazione

//		List<Node> nodes_segments = WebPageSegmentation.segment(doc, parameterTextFusion);
		List<Node> nodes_segments = segmentator.segment(docClean, parameterTextFusion);

		//estrazione degli xpath dei segmenti

		//TODO la lista degli xPath deve essere associata al dominio
		Set<Xpath> xPaths = new HashSet<>();
//		nodes_segments.forEach(node_segment -> {
		for (int i=0;i<nodes_segments.size();i++) {
			try {
				xPaths.add(new Xpath(nodes_segments.get(i),
						xpmaker.calculateAbsoluteXPath(nodes_segments.get(i), doc),par));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		});

		return xPaths;
	}
	
	

	/* Prende come input un documento jsoup da pulire, assieme a una lista di documenti
	 * che vengono usati per la pulizia
	 * e restituisce un documento pulito del template comune alle pagine */
	public static Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {

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
	
//	public Set<String> getXPathsFromDocument_test(Document doc, double parameterTextFusion, String parameterXpathMaker) throws Exception {
//
//		//segmentazione
//
//		List<Node> nodes_segments = WebPageSegmentation.segment(doc, parameterTextFusion);
//
//		//estrazione degli xpath dei segmenti
//
//		//TODO la lista degli xPath deve essere associata al dominio
//		Set<String> xPaths = new HashSet<>();
////		nodes_segments.forEach(node_segment -> {
//		for (int i=0;i<nodes_segments.size();i++) {
//			try {
//				xPaths.add(xpmaker.calculateXPath_test(nodes_segments.get(i), doc, parameterXpathMaker));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
////		});
//
//		return xPaths;
//	}
}
