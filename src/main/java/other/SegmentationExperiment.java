//package other;
//
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import org.apache.commons.io.IOUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Node;
//
//import lib.utils.CleanHTMLTree;
//import model.BlacklistElement;
//import segmentation.WebPageSegmentation;
//import segmentation.XPathMaker;
//
//public class SegmentationExperiment {
//
//	public static void main(String[] args) throws Exception {
//		//passo 1: prendere la pagina da segmentare
//		String html = IOUtils.toString(new FileReader(new File("/home/valentina/Scrivania/pages4test/avvocato2.html")));
//		Document doc = Jsoup.parse(html);
//		//passo 2: pulire la pagina
//		List<Document> usedPagesForCleaning = new ArrayList<>();
//		for (int i=1; i<=5;i++) {
//			usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
//					new FileReader(new File("/home/valentina/Scrivania/pages4test/avv_"+i+".html")))));
//		}
//		Document docClean = clean(doc, usedPagesForCleaning);
//		//passo 3: segmentazione
//		List<Node> segments = WebPageSegmentation.segment(docClean);
//		//passo 4: estrazione degli xpath dei segmenti
//		XPathMaker xpMaker = XPathMaker.getInstance();
//		List<String> xPaths = new ArrayList<>();
//		for (int i=0; i<segments.size();i++) {
//			xPaths.add(xpMaker.calculateXPath(segments.get(i)));
////			System.out.println(i);
////			System.out.println(xpMaker.calculateXPath(segments.get(i)));
////			System.out.println("**********");
//		}
//		//TODO poi set
//		for (int i=0; i<xPaths.size();i++) {
//			System.out.println(i);
//			System.out.println(xPaths.get(i));
//			System.out.println("**********");
//		}
//				
//	}
//
//	public static Document clean(Document docToClean, List<Document> usedPagesForCleaning) throws Exception {
//
//		HashSet<BlacklistElement> blacklistedTxt = makeBlackList(docToClean, "txt", usedPagesForCleaning);
//		HashSet<BlacklistElement> blacklistedImg = makeBlackList(docToClean, "img", usedPagesForCleaning);
//
//
//		CleanHTMLTree.travers(docToClean, blacklistedTxt, blacklistedImg);
//		return docToClean;
//	}
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
