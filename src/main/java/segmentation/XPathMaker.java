package segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
//import org.jdom2.input.DOMBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lib.utils.DBuilder;
import model.Segment;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;

public class XPathMaker {

	private static XPathMaker instance = null;

	public static XPathMaker getInstance() {
		if (instance == null)
			instance = new XPathMaker();
		return instance;
	}

	private XPathMaker() {
	}

	//dato un nodo, restituisce il suo xpath
	public String calculateXPath(Node node) {
		List<String> path = new ArrayList<>();
		//TODO qui cambi il modo in cui costruisci gli xpath
		path = buildPathAlternative(node,path);
		String xPath = "/";
		for(int i=0;i<path.size()-1;i++) {
			xPath = xPath + path.get(i)+"/";
		}
		xPath = xPath + path.get(path.size()-1);
		return xPath;
	}

	public List<String> buildPath(Node node, List<String> list) {
		//		System.out.println("il nodo: "+node);
		if (node.parent() == null) {
			return list;
		}
		else {
			//			System.out.println("NODENAME DEL NODO CORRENTE "+node.nodeName());
			if (!node.nodeName().equals("#text")) {
				//controllo se ha fratelli
				if (node.parent().childNodeSize() > 1
						&& !node.nodeName().equals("html")) {
					int i=0;
					int j=1;
					while (i<node.parent().childNodeSize()) {				//scorro i fratelli
						//						System.out.println("IL FIGLIO"+ node.parent().childNode(i).toString()
						//								+ "\n e NODENAME: "+ node.parent().childNode(i).nodeName());
						if (!Pattern.matches("^#.*", node.parent().childNode(i).nodeName())
								//!node.parent().childNode(i).toString().equals(" ")
								//&& !node.parent().childNode(i).toString().equals(" &gt; ")
								//&& 
								//!node.parent().childNode(i).nodeName().equals("#text")
								) {
							//							System.out.println("NON E' UNO SPAZIO BIANCO! è il figlio "+ j);
							if (node.parent().childNode(i).equals(node)) {	
								//								System.out.println("e' il nodo corrente!");
								//tra i fratelli cerco il nodo in questione, per assegnargli il giusto indice
								list.add(0, "*["+(j)+"]");
								list = buildPath(node.parent(), list);
								return list;
							}
							j++;
						}
						i++;
					}
				}
				//non ha fratelli
				else {
					//					System.out.println("NON HA FRATELLI");
					list.add(0, node.nodeName().toString());
					list = buildPath(node.parent(), list);
					return list;
				}
			}
			//			System.out.println("E' #text");
			//			System.out.println();
		}
		return buildPath(node.parent(), list);
	}
	
	
	public List<String> buildPathAlternative(Node node, List<String> list) {
		//		System.out.println("il nodo: "+node);
		if (node.parent() == null) {
			return list;
		}
		else {
			//			System.out.println("NODENAME DEL NODO CORRENTE "+node.nodeName());
			if (!node.nodeName().equals("#text")) {
				//controllo se ha fratelli
				if (node.parent().childNodeSize() > 1
						&& !node.nodeName().equals("html")) {
					int i=0;
					int j=1;
					while (i<node.parent().childNodeSize()) {				//scorro i fratelli
						//						System.out.println("IL FIGLIO"+ node.parent().childNode(i).toString()
						//								+ "\n e NODENAME: "+ node.parent().childNode(i).nodeName());
						if (!Pattern.matches("^#.*", node.parent().childNode(i).nodeName())
								&& node.parent().childNode(i).nodeName().equals(node.nodeName())) {
							//							System.out.println("NON E' UNO SPAZIO BIANCO! è il figlio "+ j);
							if (node.parent().childNode(i).equals(node)) {	
								//								System.out.println("e' il nodo corrente!");
								//tra i fratelli cerco il nodo in questione, per assegnargli il giusto indice
								list.add(0, node.nodeName()+"["+(j)+"]");
								list = buildPathAlternative(node.parent(), list);
								return list;
							}
							j++;
						}
						i++;
					}
				}
				//non ha fratelli
				else {
					//					System.out.println("NON HA FRATELLI");
					list.add(0, node.nodeName().toString());
					list = buildPathAlternative(node.parent(), list);
					return list;
				}
			}
			//			System.out.println("E' #text");
			//			System.out.println();
		}
		return buildPathAlternative(node.parent(), list);
	}



	public String resolveXPath(String path, Document document) throws XPathExpressionException {
		NodeList nl = getNodes(path, document);
		String output = "";

		//		System.out.println(path);

		for (int i = 0; i < nl.getLength(); i++) {

			output = output + nl.item(i).getTextContent();
		}

		return output;
	}

	public List<String> getNodeTypeOf(String path, Document document) throws XPathExpressionException {
		NodeList nl = getNodes(path, document);
		List<String> output = new ArrayList<>();

		for (int i = 0; i < nl.getLength(); i++) {
			output.add(nl.item(i).getNodeName());
		}

		return output;
	}


	public NodeList getNodes(String path, Document document) throws XPathExpressionException {
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		//prova per i caratteri non validi
		//document.outputSettings().charset("UTF-8");

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpathObj = xPathfactory.newXPath();
		XPathExpression expr = xpathObj.compile(path);
		NodeList nl = null;
		
		try {
			org.w3c.dom.Document w3doc = new W3CDom().fromJsoup(document);

//			org.w3c.dom.Document w3doc = DBuilder.jsoup2DOM(document);
			nl = (NodeList) expr.evaluate(w3doc, XPathConstants.NODESET);
		}
		catch (Exception e) {
			System.out.println("Errore durante la conversione del documento, da jsoup a w3c: "+e);
			e.printStackTrace();
		}

		return nl;
	}

	//TODO sposta i metodi da qualche altra parte?
	//TODO un nuovo metodo migliore, che come input ha la LISTA degli xpath e il documento di jsoup
	//così sei certa che li colori bene
	//puoi fare proprio il metodo colora!!!
	public org.w3c.dom.Document color(Set<String> paths, Document document) throws XPathExpressionException {

		org.w3c.dom.Document domDocument = prepareDoc(document);
		if (domDocument == null) {return null;}
		paths.forEach(path -> {

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpathObj = xPathfactory.newXPath();
			XPathExpression expr = null;
			try {
				expr = xpathObj.compile(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
			NodeList nl = null;
			try {
				nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
			} catch (Exception e) {
				e.printStackTrace();
			}

			colorNodeList(nl, domDocument, path);
		});

		return domDocument;
	}
	
	public org.w3c.dom.Document colorRelevance(Set<Segment> segments, Document document) throws XPathExpressionException {

		org.w3c.dom.Document domDocument = prepareDoc(document);
		if (domDocument == null) {return null;}
		segments.forEach(segment -> {

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpathObj = xPathfactory.newXPath();
			XPathExpression expr = null;
			try {
				expr = xpathObj.compile(segment.getXPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			NodeList nl = null;
			try {
				nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
			} catch (Exception e) {
				e.printStackTrace();
			}

			colorNodeListRelevance(nl, domDocument, segment.getRelevance());
		});

		return domDocument;
	}

	//questo metodo è usato per permettere la colorazione del dom in modo coerente
	//per cui viene chiamato il metodo più volte di fila sullo stesso documento
	//e non chiamate su due documenti diversi in modo alternato
	private org.w3c.dom.Document prepareDoc(Document document) throws XPathExpressionException {
		//		if (jsoupDocument == null || !jsoupDocument.equals(document)) {
		//			jsoupDocument = document;
		//			document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		//			domDocument = new W3CDom().fromJsoup(document);
		//			return domDocument;
		//		}
		//		return domDocument;
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		org.w3c.dom.Document domDoc = null;
		try {domDoc = new W3CDom().fromJsoup(document);}
		catch (Exception e) {System.out.println("Errore: "+e);}
		return domDoc;
	}

	public static void colorNodeList(NodeList nl, org.w3c.dom.Document domDocument, String path) {
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			e.setAttribute("style", "border:5px solid red");
			e.setAttribute("path", path);
		}
	}
	
	public static void colorNodeListRelevance(NodeList nl, org.w3c.dom.Document domDocument, int relevance) {
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			e.setAttribute("style", "border:"+(relevance)+"px solid green");
		}
	}


	public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {
		String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/claudio_amoroso/";
		String html = IOUtils.toString(new FileReader(new File(cartella+"orig.html")));
		///html/*[2]/*[1]/div/div/*[5]/div/div/*[3]/*[1]/*[4]/*[2]/*[2]/*[6]/*[2]
		String expression = "/html/*[2]/*[1]/div/div/*[5]/div/div/*[3]/*[1]/*[4]/*[2]/*[2]/*[6]/*[2]";


		Document document = Jsoup.parse(html);
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		//		document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpathObj = xPathfactory.newXPath();
		XPathExpression expr = xpathObj.compile(expression);
		NodeList nl = (NodeList) expr.evaluate(new W3CDom().fromJsoup(document), XPathConstants.NODESET);

		System.out.println(nl.getLength());

		for (int i = 0; i < nl.getLength(); i++) {
			System.out.println(nl.item(i).getNodeName());
			System.out.println(nl.item(i).getTextContent());
		}
	}
}


