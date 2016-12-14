package test;
//package segmentation;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.PrintWriter;
//import java.util.Set;
//
//
//import org.apache.commons.io.IOUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.safety.Whitelist;
//
//import lib.utils.DocumentUtils;
//import xpath.utils.XpathApplier;
//import xpath.utils.XpathExtractor;
//
//public class MainColorDomainSegments {
//
//	static final String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/";
//	//	static final String n = "";
//	//	static final String fileName = "orig"+n;
//
//	public static void main(String[] args) throws Exception {
//		String html = IOUtils.toString(new FileReader(new File("/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/"+
//				"orig1.html")));
//		html = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class", "id"));
//		Document doc = Jsoup.parse(html);
//		XpathExtractor xpathextractor = XpathExtractor.getInstance();
//		XpathApplier xpathapplier =XpathApplier.getInstance();
//
//		Set<String> xPathsNormal = xpathextractor.getXPathsFromDocument_test(doc, (double) -1, "normal");
//		Set<String> xPathsStrong = xpathextractor.getXPathsFromDocument_test(doc, (double) -1, "strong");
//
//		org.w3c.dom.Document testOrigN = xpathapplier.color(xPathsNormal, doc);
//		if (testOrigN != null) {
//			//			System.out.println(xPaths_secondPage.size());
//
//			PrintWriter testPrinter = new PrintWriter(cartella+"coloratoNormal.html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(testOrigN));
//			testPrinter.close();
//		}
//		org.w3c.dom.Document testOrigS = xpathapplier.color(xPathsStrong, doc);
//		if (testOrigS != null) {
//			//			System.out.println(xPaths_secondPage.size());
//
//			PrintWriter testPrinter = new PrintWriter(cartella+"coloratoStrong.html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(testOrigS));
//			testPrinter.close();
//		}
//
//		for (int i=1; i<=5; i++) {
//			html = IOUtils.toString(new FileReader(new File(cartella+"pag1_"+i+".html")));
//			html = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class", "id"));
//			doc = Jsoup.parse(html);
//			org.w3c.dom.Document testN = xpathapplier.color(xPathsNormal, doc);
//			if (testN != null) {
//				//			System.out.println(xPaths_secondPage.size());
//
//				PrintWriter testPrinter = new PrintWriter(cartella+"pag1Segm_"+i+"Normal.html", "UTF-8");
//				testPrinter.println(DocumentUtils.getStringFromDocument(testN));
//				testPrinter.close();
//			}
//			org.w3c.dom.Document testS = xpathapplier.color(xPathsStrong, doc);
//			if (testS != null) {
//				//			System.out.println(xPaths_secondPage.size());
//
//				PrintWriter testPrinter = new PrintWriter(cartella+"pag1Segm_"+i+"Strong.html", "UTF-8");
//				testPrinter.println(DocumentUtils.getStringFromDocument(testS));
//				testPrinter.close();
//			}
//		}
//		System.out.println("terminato!");
//	}
//}
