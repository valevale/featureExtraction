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
//public class CleanPageSegmentation {
//	public static void getSimilarity(Page p1, Page p2) throws Exception {
//
//		//passo 1: prendere la pagina da segmentare
//
//		String html = page1.getHtml();
//
//
//		Document doc = Jsoup.parse(html);
//
//		//passo 2: pulire la pagina
//
//		Document docClean = cleanHTMLTree(doc);
//
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
//		source.setXpath(xPaths);
//
//		//passo 5: ripetere [1-4] su un altro dominio
//
//		String html = page2.getHtml();
//
//
//		Document doc = Jsoup.parse(html);
//		Document docClean = cleanHTMLTree(doc);
//
//		List<Node> segments = WebPageSegmentation.segment(docClean);
//		XPathMaker xpMaker = XPathMaker.getInstance();
//		Set<String> xPaths = new HashSet<>();
//		segments.forEach(segment -> {
//			xPaths.add(xpMaker.calculateXPath(segment));
//		});
//
//		source.setXpath(xPaths);
//
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
//				Tuple2<Segment,Segment> reverse_pair = 
//						new Tuple2<Segment,Segment>(segment2,segment1);
//				if (!pairs2similarity.containsKey(reverse_pair)) {
//
//					//6.2 : calcolo della similarità
//
//					double similarity = Jaccard.similarity(firstSegmentContent, secondSegmentContent);
//
//					Tuple2<String,String> pair = 
//							new Tuple2<String,String>(segment1,segment2);
//
//					//memorizzo in una mappa il risultato
//					pairs2similarity.put(pair, similarity);
//					if (similarity >= 0.5) {
//						segment1.setRelevance(segment1.getRelevance()+1);
//						segment2.setRelevance(segment2.getRelevance()+1);
//					}
//				}
//			}
//		}
//
//	}
//}
