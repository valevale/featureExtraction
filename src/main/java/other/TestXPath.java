package other;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import lib.utils.DocumentUtils;
import model.Segment;
import model.WebPageDocument;
import scala.Tuple2;
import xpath.utils.XpathApplier;
import xpath.utils.XpathExtractor;

public class TestXPath {

	public static void main(String[] args) throws Exception {
		
		File file = new File("/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/"+
				"orig1.html");
		String html = IOUtils.toString(new FileReader(file));
		
		WebPageDocument wp = new WebPageDocument(file, 1, "/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/", -1);
		
//		Whitelist wl = new Whitelist();
//		html = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class", "id"));
//		wl.removeTags("script");
//		html = Jsoup.clean(html, wl);
		Document doc = Jsoup.parse(html);
//		doc.select("a").remove();
//		NodeUtils.removeComments(doc);
//		PrintWriter printer = new PrintWriter("/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/pulito.html", "UTF-8");
//		printer.println(doc);
//		printer.close();
//		XpathExtractor xpextractor = XpathExtractor.getInstance();
//		Set<Tuple2<String, Node>> xPaths_nodes = xpextractor.getXPathsFromDocument(doc, 1, "/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/", -1);
//		Iterator<Tuple2<String, Node>> it = xPaths_nodes.iterator();
//		Tuple2<String, Node> xpath_node = it.next();
		
//		XpathApplier xpapplier = XpathApplier.getInstance();
//		org.w3c.dom.Document test = xpapplier.color_alt(xPaths, doc);
////		org.w3c.dom.Document test = xpapplier.color(xPaths, doc);
//		if (test != null) {
//			//			System.out.println(xPaths_secondPage.size());
//
//			PrintWriter testPrinter = new PrintWriter("/home/valentina/workspace_nuovo/FeatureExtractor/testXpath/colorato.html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(test));
//			testPrinter.close();
//		}
//		String xpath = xpath_node._1();
//		Segment seg = new Segment(xpath, xpath_node._2(), doc);
//		System.out.println(xpath);
		Iterator<Segment> segIt = wp.getSegments().iterator();
		Segment seg = segIt.next();
		String xpath = seg.getAbsoluteXPath();
		System.out.println(xpath);
		XpathApplier xapplier = XpathApplier.getInstance();
		NodeList xpathNodes = xapplier.getNodes(xpath, doc);
		NodeList nl = seg.getW3cNodes();
		System.out.println(xpathNodes.getLength());
		System.out.println(xpathNodes.item(0).getTextContent());
		System.out.println(nl.getLength());
		System.out.println(nl.item(0).getTextContent());
		System.out.println(nl.item(0).isEqualNode((xpathNodes.item(0))));
		//TODO A FINE TEST RIMETTI LA PULIZIA DEL DOCUMENTO!
	}
}
