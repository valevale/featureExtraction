package lib.utils;

import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
import java.io.StringWriter;
//import java.lang.reflect.Array;
//import java.security.acl.Owner;
import java.util.*;

import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.apache.commons.io.IOUtils;
//import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
//import org.w3c.dom.NodeList;

import model.BlacklistElement;


public class CleanHTMLTree {
	/*
	public static void main(String[] args) throws Exception {
		String[] _5Documents = {"/home/valerio/Sites/www.albinoarmani.com/it/vino.php?id=53&terr=valdadige",
				"/home/valerio/Sites/www.albinoarmani.com/it/vino.php?id=54&terr=vallagarina",
				"/home/valerio/Sites/www.albinoarmani.com/it/vino.php?id=55&terr=valdadige",
				"/home/valerio/Sites/www.albinoarmani.com/it/vino.php?id=56&terr=valdadige",
		"/home/valerio/Sites/www.albinoarmani.com/it/vino.php?id=57&terr=valdadige" };

		String doc = IOUtils.toString(new FileReader(new File(_5Documents[0])));
		Document parse = Jsoup.parse(doc);
		org.w3c.dom.Document docW3C = DOMBuilder.jsoup2DOM(parse);

		List<String> text1Doc = getHTMLElementsText(parse);
		System.out.println(text1Doc);

		for (int i = 1; i < 5; i++) {
			String doctemp = IOUtils.toString(new FileReader(new File(_5Documents[i])));
			Document parsetemp = Jsoup.parse(doctemp);
			//org.w3c.dom.Document docW3Ctemp = DOMBuilder.jsoup2DOM(parsetemp);
			travers(parsetemp, text1Doc);
			printSite(parsetemp);
			//printSite(docW3Ctemp);
		}
	}
	
	*/

	//dato un documento stampa il sito
	public static void printSite(Document doc) throws Exception {
		Scanner scanner = new Scanner(doc.outerHtml());
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("out")));
		String line ="";
		while (scanner.hasNext()) {
			line = scanner.next();
			writer.write(line+"\n");
		}
		writer.close();
		scanner.close();
	}
	
	//dato un altro tipo di documento, stampa il sito
	public static void printSite(org.w3c.dom.Document doc) throws IOException, TransformerException {
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		System.out.println("XML IN String format is: \n" + writer.toString());
	}

	//dato un nodo, una lista di documenti documenti e immagini, ....
	public static void travers(Node node, HashSet<BlacklistElement> _1doc, HashSet<BlacklistElement> imgs) {
		if (node != null) {
			//se il nodo ha src
            if (node.hasAttr("src")) {
                Element element = (Element) node;
                if (imgs.contains(new BlacklistElement(element.attr("src"), element.tagName(), element.parent().tagName()))) {
                    node.attr("src","");
                }
            }
            if (!(node instanceof TextNode)) {
                try {
                    Element element = (Element) node;
                    String ele = element.text();
//                String nodeText = ((TextNode) node).text();
                    if (_1doc.contains(new BlacklistElement(element.text(), element.tagName(), element.parent().tagName()))) {
                        cleanTextNodeElements(node);
                    }
                } catch (Exception e) {
                }
            }
            node.childNodes().forEach(node1 -> travers(node1, _1doc, imgs));
        }
	}

    public static void cleanElements(Elements elements) {
        if (elements != null)
            elements.forEach(element -> cleanTextNodeElements(element));
    }

    public static void cleanTextNodeElements(Node node) {
        for (int i = 0; i < node.childNodeSize(); i++)
            cleanTextNodeElements(node.childNode(i));

        if (node instanceof TextNode)
            ((TextNode) node).text("");
    }



	public static boolean cleanTable(Node node) {
		if (node != null) {
			for (int i = 0; i < node.childNodeSize(); i++) {
				boolean flag = cleanTable(node.childNode(i));
				if (flag)
					i--;
			}

			if (node instanceof TextNode) {
				((TextNode) node).text("");
			}

			return false;
		}
		return false;
	}

	public static HashSet<BlacklistElement> getHTMLElementsImg(Node doc) {
		HashSet<BlacklistElement> imgResults = new HashSet<>();
		if (doc.childNodeSize() > 0) {
			//NodeList childs = doc.getChildNodes();
			for (int i = 0; i < doc.childNodeSize(); i++) {
				HashSet<BlacklistElement> lists = getHTMLElementsImg(doc.childNode(i));
				imgResults.addAll(lists);
			}
		}
		if (doc.hasAttr("src")) {
            Element e = (Element)doc;
            BlacklistElement blacklistElement = new BlacklistElement(e.attr("src"), e.tagName(), e.parent().tagName());
			imgResults.add(blacklistElement);
		}
		return imgResults;
	}
	
	public static HashSet<BlacklistElement> getHTMLElementsText(Node doc) {
		HashSet<BlacklistElement> textResults = new HashSet<>();
		if (doc.childNodeSize() > 0) {
			for (int i = 0; i < doc.childNodeSize(); i++) {
				HashSet<BlacklistElement> lists = getHTMLElementsText(doc.childNode(i));
				textResults.addAll(lists);
			}
		}
		if (doc instanceof TextNode) {
            Element element = (Element) doc.parent();
            BlacklistElement blacklistElement = new BlacklistElement(element.text(), element.nodeName(), element.parent().nodeName());
            textResults.add(blacklistElement);
		}
		return textResults;
	}

//    public static ArrayList<HashSet<BlacklistElement>> getHTMLElementsText(Node doc) {
//        HashSet<BlacklistElement> textResults = new HashSet<>();
//        HashSet<BlacklistElement> imgResults = new HashSet<>();
//
//        if (doc.childNodeSize() > 0) {
//            //NodeList childs = doc.getChildNodes();
//            for (int i = 0; i < doc.childNodeSize(); i++) {
//                ArrayList<HashSet<BlacklistElement>> lists = getHTMLElementsText(doc.childNode(i));
//                textResults.addAll(lists.get(0));
//                imgResults.addAll(lists.get(1));
//            }
//        }
//        if (doc instanceof TextNode) {
//            TextNode n = (TextNode)doc;
//            BlacklistElement blacklistElement = new BlacklistElement(n.text());
//            textResults.add(blacklistElement);
//        }
//
//        if (doc.hasAttr("src")) {
//            BlacklistElement blacklistElement = new BlacklistElement(doc.attr("src"), doc.);
//            imgResults.add(doc.attr("src"));
//        }
//
//        ArrayList<HashSet<String>> result = new ArrayList<>();
//        result.add(textResults);
//        result.add(imgResults);
//        return result;
//    }

}
