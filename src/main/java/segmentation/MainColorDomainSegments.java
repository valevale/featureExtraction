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
//
//public class MainColorDomainSegments {
//
//	static final String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/angelo_amoroso_aragona/";
//	static final String n = "";
//	static final String fileName = "orig"+n;
//
//	public static void main(String[] args) throws Exception {
//
//		String html = IOUtils.toString(new FileReader(new File(cartella+fileName+".html")));
//		Document doc = Jsoup.parse(html);
//
//		Set<String> xPaths = Main.getXPathsFromDocument(doc, "", fileName);
//
//		XPathMaker xpMaker = XPathMaker.getInstance();
//		for (int i=1; i<=5; i++) {
//			html = IOUtils.toString(new FileReader(new File(cartella+"pag"+n+"_"+i+".html")));
//			doc = Jsoup.parse(html);
//			org.w3c.dom.Document test = xpMaker.color(xPaths, doc);
//			if (test != null) {
//				//			System.out.println(xPaths_secondPage.size());
//
//				PrintWriter testPrinter = new PrintWriter(cartella+"pag"+n+"Segm_"+i+"Alternativo.html", "UTF-8");
//				testPrinter.println(Main.getStringFromDocument(test));
//				testPrinter.close();
//			}
//		}
//		System.out.println("terminato!");
//	}
//}
