package otherSegmentation;
//package segmentation;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.commons.io.IOUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Node;
//
//import lib.utils.CleanHTMLTree;
//import lib.utils.Jaccard;
//import lib.utils.MapUtils;
//import lib.utils.NodeUtils;
//import model.BlacklistElement;
//import model.Segment;
//import scala.Tuple2;
//
//public class Main {
//
//	public static void main(String[] args) throws Exception {
//
//		String n = "1-3";
//
//		//passo 1: prendere la pagina da segmentare
//
//		String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/angelo_amoroso_aragona/";
//		String html = IOUtils.toString(new FileReader(new File(cartella+"orig.html")));
//		Document doc = Jsoup.parse(html);
//
//		//passo 2: pulire la pagina
//
//		List<Document> usedPagesForCleaning = new ArrayList<>();
//		for (int i=1; i<=5;i++) {
//			usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
//					new FileReader(new File(cartella+"pag_"+i+".html")))));
//		}
//		Document docClean = clean(doc, usedPagesForCleaning);
//
//		//passo 3: segmentazione
//
//		List<Node> segments = WebPageSegmentation.segment(docClean);
//		//passo 4: estrazione degli xpath dei segmenti
//
//		//TODO la lista degli xPath deve essere associata al dominio
//		XPathMaker xpMaker = XPathMaker.getInstance();
//		Set<String> xPaths = new HashSet<>();
//		segments.forEach(segment -> {
//			xPaths.add(xpMaker.calculateXPath(segment));
//		});
//
//		//passo 5: ripetere [1-4] su un altro dominio
//
//		String second_html = IOUtils.toString(new FileReader(new File(cartella+"orig4.html")));
//		Document second_doc = Jsoup.parse(second_html);
//		List<Document> second_usedPagesForCleaning = new ArrayList<>();
//		for (int i=1; i<=5;i++) {
//			second_usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
//					new FileReader(new File(cartella+"pag2_"+i+".html")))));
//		}
//		Document second_docClean = clean(second_doc, second_usedPagesForCleaning);
//		List<Node> second_segments = WebPageSegmentation.segment(second_docClean);
//		Set<String> second_xPaths = new HashSet<>();
//		second_segments.forEach(segment -> {
//			second_xPaths.add(xpMaker.calculateXPath(segment));
//		});
//
//		Map<Tuple2<String,String>,Double> pairs2similarity = new HashMap<>();
//
//		//passo 6: confrontare i segmenti con similarità di jaccard
//
//		//6.1 : due for annidati per confrontare le coppie di segmenti
//
//		Iterator<String> it = xPaths.iterator();
//		while (it.hasNext()) {
//			String xp1 = it.next();
//			Segment segment1 = new Segment(xp1, doc);
//			String firstSegmentContent = NodeUtils.getNodesContent(segment1.getNodes());
//			Iterator<String> it2 = second_xPaths.iterator();
//			while (it2.hasNext()) {
//				String xp2 = it2.next();
//				Segment segment2 = new Segment(xp2, second_doc);
//				String secondSegmentContent = NodeUtils.getNodesContent(segment2.getNodes());
//
//				//test per evitare di esaminare coppie già viste
//				Tuple2<String,String> reverse_pair = 
//						new Tuple2<String,String>(secondSegmentContent,firstSegmentContent);
//				if (!pairs2similarity.containsKey(reverse_pair)) {
//
//					//6.2 : calcolo della similarità
//
//					double similarity = Jaccard.similarity(firstSegmentContent, secondSegmentContent);
//
//					Tuple2<String,String> pair = 
//							new Tuple2<String,String>(firstSegmentContent,secondSegmentContent);
//
//					//memorizzo in una mappa il risultato
//					pairs2similarity.put(pair, similarity);
//				}
//			}
//		}
//
//		pairs2similarity = MapUtils.sortByValue(pairs2similarity);
//
//		//passo 7: visionare i risultati della similarità
//
//		PrintWriter similarityPrinter = new PrintWriter(cartella+"sim"+n+".txt", "UTF-8");
//		Set<Tuple2<String,String>> pairs = pairs2similarity.keySet();
//		Iterator<Tuple2<String,String>> iter = pairs.iterator();
//		while (iter.hasNext()) {
//			Tuple2<String,String> pair = iter.next();
//			if (pairs2similarity.get(pair) > 0) {
//				similarityPrinter.println(pairs2similarity.get(pair));
//				similarityPrinter.println(pair._1());
//				similarityPrinter.println("---------------------");
//				similarityPrinter.println(pair._2());
//				similarityPrinter.println("_______________________________________________________");
//			}
//		}
//
//		similarityPrinter.close();
//	}
//
//
//
//	public static Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {
//
//		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(docToClean, "txt", usedPagesForCleaning);
//		HashSet<BlacklistElement> blacklistedImg = makeBlackList(docToClean, "img", usedPagesForCleaning);
//
//		CleanHTMLTree.travers(docToClean, blacklistedTxt, blacklistedImg);
//		return docToClean;
//	}
//
//
//	public static HashSet<BlacklistElement> makeBlackList(Document docToClean, String parameter, List<Document> usedPagesForCleaning) throws Exception {
//
//		HashSet<BlacklistElement> blacklist = new HashSet<>();
//		if (usedPagesForCleaning.size() > 1) {
//			Document document = usedPagesForCleaning.get(0);
//			if (parameter.equals("txt"))
//				blacklist = CleanHTMLTree.getHTMLElementsText(document);
//			else //img
//				blacklist = CleanHTMLTree.getHTMLElementsImg(document);
//
//			for (int i=1; i<usedPagesForCleaning.size();i++) {
//				document = usedPagesForCleaning.get(i);
//				HashSet<BlacklistElement> temp;
//				if (parameter.equals("txt"))
//					temp = CleanHTMLTree.getHTMLElementsText(document);
//				else //img
//					temp = CleanHTMLTree.getHTMLElementsImg(document);
//				blacklist.retainAll(temp);
//			}
//			return blacklist;
//		}
//		return blacklist;
//	}
//}
