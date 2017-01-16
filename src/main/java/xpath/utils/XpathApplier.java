package xpath.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

//import org.jdom2.input.DOMBuilder;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Segment;
import model.Xpath;

//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;

public class XpathApplier {

	private static XpathApplier instance = null;

	public static XpathApplier getInstance() {
		if (instance == null)
			instance = new XpathApplier();
		return instance;
	}

	private XpathApplier() {
	}

	//	public String resolveXPath(String path, Document document) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		NodeList nl = getNodes(path, document);
	//		String output = "";
	//
	//		//		System.out.println(path);
	//
	//		for (int i = 0; i < nl.getLength(); i++) {
	//
	//			output = output + nl.item(i).getTextContent();
	//		}
	//
	//		return output;
	//	}

	//	public List<String> getNodeTypeOf(String path, Document document) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		NodeList nl = getNodes(path, document);
	//		List<String> output = new ArrayList<>();
	//
	//		for (int i = 0; i < nl.getLength(); i++) {
	//			output.add(nl.item(i).getNodeName());
	//		}
	//		return output;
	//	}
	//ORIGINALE!!!!
	public NodeList getNodes(String path, Document document) throws XPathExpressionException, IOException {

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
		//		XPathFactory xPathfactory = XPathFactory.newInstance();
		//		XPath xpathObj = xPathfactory.newXPath();
		//		XPathExpression expr = null;
		//		try {
		//			expr = xpathObj.compile(path);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
		//		NodeList nl = null;
		//		try {
		//			nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}


		return nl;
	}

	//PROVA CON W3C
	//	public NodeList getNodes(String path, org.w3c.dom.Document w3doc) throws XPathExpressionException, IOException {
	//
	//		//prova per i caratteri non validi
	//		//document.outputSettings().charset("UTF-8");
	//
	//		XPathFactory xPathfactory = XPathFactory.newInstance();
	//		XPath xpathObj = xPathfactory.newXPath();
	//		XPathExpression expr = xpathObj.compile(path);
	//		NodeList nl = null;
	//		try {
	//
	//
	//			//			org.w3c.dom.Document w3doc = DBuilder.jsoup2DOM(document);
	//			nl = (NodeList) expr.evaluate(w3doc, XPathConstants.NODESET);
	//		}
	//		catch (Exception e) {
	//			System.out.println("Errore durante la conversione del documento, da jsoup a w3c: "+e);
	//			e.printStackTrace();
	//		}
	//		//		XPathFactory xPathfactory = XPathFactory.newInstance();
	//		//		XPath xpathObj = xPathfactory.newXPath();
	//		//		XPathExpression expr = null;
	//		//		try {
	//		//			expr = xpathObj.compile(path);
	//		//		} catch (Exception e) {
	//		//			e.printStackTrace();
	//		//		}
	//		//		NodeList nl = null;
	//		//		try {
	//		//			nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
	//		//		} catch (Exception e) {
	//		//			e.printStackTrace();
	//		//		}
	//
	//
	//		return nl;
	//	}

	//	public NodeList getNodes(String path, Document document) throws XPathExpressionException, IOException, ParserConfigurationException {
	//
	//		TagNode tagNode = new HtmlCleaner().clean(document.html());
	//		org.w3c.dom.Document doc = new DomSerializer(
	//				new CleanerProperties()).createDOM(tagNode);
	//
	//		XPath xpathObj = XPathFactory.newInstance().newXPath();
	//
	//		XPathExpression expr = xpathObj.compile(path);
	//		
	//		NodeList nl = null;
	//		try {
	//
	//			nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	//		}
	//		catch (Exception e) {
	//			System.out.println("Errore durante la conversione del documento, da jsoup a w3c: "+e);
	//			e.printStackTrace();
	//		}
	//		//		XPathFactory xPathfactory = XPathFactory.newInstance();
	//		//		XPath xpathObj = xPathfactory.newXPath();
	//		//		XPathExpression expr = null;
	//		//		try {
	//		//			expr = xpathObj.compile(path);
	//		//		} catch (Exception e) {
	//		//			e.printStackTrace();
	//		//		}
	//		//		NodeList nl = null;
	//		//		try {
	//		//			nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
	//		//		} catch (Exception e) {
	//		//			e.printStackTrace();
	//		//		}
	//
	//		//		System.out.println(nl.getLength());
	//		return nl;
	//	}

	//	public org.w3c.dom.Document color_alt(Set<String> paths, Document document) throws XPathExpressionException, ParserConfigurationException {
	//
	//		TagNode tagNode = new HtmlCleaner().clean(document.html());
	//		org.w3c.dom.Document doc = new DomSerializer(
	//				new CleanerProperties()).createDOM(tagNode);
	//
	//		XPath xpathObj = XPathFactory.newInstance().newXPath();
	//
	//		paths.forEach(path -> {
	//			NodeList nl=null;
	//			try {
	//				nl = (NodeList) xpathObj.evaluate(path, 
	//						doc, XPathConstants.NODESET);
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//			colorNodeList(nl, doc, path);
	//		});
	//
	//		return doc;
	//	}

	public org.w3c.dom.Document color(Set<Xpath> paths, Document document) throws XPathExpressionException {

		org.w3c.dom.Document domDocument = prepareDoc(document);
		if (domDocument == null) {return null;}
		paths.forEach(path -> {
			String xpath = path.getXpath();
			//			NodeList nl = null;
			//			try {
			//				nl = getNodes(path, document);
			//			} catch (Exception e) {
			//				e.printStackTrace();
			//			}
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpathObj = xPathfactory.newXPath();
			XPathExpression expr = null;
			try {
				expr = xpathObj.compile(xpath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			NodeList nl = null;
			try {
				nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
			} catch (Exception e) {
				e.printStackTrace();
			}

			colorNodeList(nl, domDocument, xpath);
		});

		return domDocument;
	}

	public void color_iter(Xpath path, float voto, org.w3c.dom.Document domDocument) throws XPathExpressionException {

		String xpath = path.getXpath();
		//			NodeList nl = null;
		//			try {
		//				nl = getNodes(path, document);
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpathObj = xPathfactory.newXPath();
		XPathExpression expr = null;
		try {
			expr = xpathObj.compile(xpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NodeList nl = null;
		try {
			nl = (NodeList) expr.evaluate(domDocument, XPathConstants.NODESET);
		} catch (Exception e) {
			e.printStackTrace();
		}

		colorNodeList_iter(nl, domDocument, xpath, voto);

	}

	public org.w3c.dom.Document colorRelevance(Set<Segment> segments, Document document) throws XPathExpressionException {

		org.w3c.dom.Document domDocument = prepareDoc(document);
		if (domDocument == null) {return null;}
		segments.forEach(segment -> {

			//			NodeList nl = null;
			//			try {
			//				nl = getNodes(segment.getXPath(), document);
			//			} catch (Exception e) {
			//				e.printStackTrace();
			//			}

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpathObj = xPathfactory.newXPath();
			XPathExpression expr = null;
			try {
				expr = xpathObj.compile(segment.getAbsoluteXPath());
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
			//			colorNodeListRelevance(nl, segment.getRelevance());
		});

		return domDocument;
	}

	//questo metodo è usato per permettere la colorazione del dom in modo coerente
	//per cui viene chiamato il metodo più volte di fila sullo stesso documento
	//e non chiamate su due documenti diversi in modo alternato
	public org.w3c.dom.Document prepareDoc(Document document) throws XPathExpressionException {
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

	private static void colorNodeList_iter(NodeList nl, org.w3c.dom.Document domDocument, String path, float voto) {
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String currentStyle = e.getAttribute("style");
			if (currentStyle != "") {
				int votoCorrente = currentStyle.charAt(7);
				float totaleVotiCorrente = votoCorrente+voto;
				if (totaleVotiCorrente > 9)
					e.setAttribute("style", "border:10px solid green");
				else
					e.setAttribute("style", "border:"+totaleVotiCorrente+"px solid green");
				e.setAttribute("path", path);
			}
			else {
				if (voto > 9)
					e.setAttribute("style", "border:10px solid green");
				else
					e.setAttribute("style", "border:"+voto+"px solid green");
				e.setAttribute("path", path);
			}
		}
	}

	private static void colorNodeList(NodeList nl, org.w3c.dom.Document domDocument, String path) {
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			e.setAttribute("style", "border:5px solid blue");
			e.setAttribute("path", path);
		}
	}

	private static void colorNodeListRelevance(NodeList nl,org.w3c.dom.Document domDocument, int relevance) {
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			e.setAttribute("style", "border:"+(relevance)+"px solid green");
		}
	}


	public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException {
		String a = "border:5px solid green";
		System.out.println(a.charAt(7));
		//		System.out.println(a.charAt(a.length()-2) >='0' &&
		//				a.charAt(a.length()-2) <='9');

		//		String cartella = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/claudio_amoroso/";
		//		String html = IOUtils.toString(new FileReader(new File(cartella+"orig.html")));
		//		///html/*[2]/*[1]/div/div/*[5]/div/div/*[3]/*[1]/*[4]/*[2]/*[2]/*[6]/*[2]
		//		String expression = "/html/*[2]/*[1]/div/div/*[5]/div/div/*[3]/*[1]/*[4]/*[2]/*[2]/*[6]/*[2]";
		//
		//
		//		Document document = Jsoup.parse(html);
		//		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		//		//		document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
		//
		//		XPathFactory xPathfactory = XPathFactory.newInstance();
		//		XPath xpathObj = xPathfactory.newXPath();
		//		XPathExpression expr = xpathObj.compile(expression);
		//		NodeList nl = (NodeList) expr.evaluate(new W3CDom().fromJsoup(document), XPathConstants.NODESET);
		//
		//		System.out.println(nl.getLength());
		//
		//		for (int i = 0; i < nl.getLength(); i++) {
		//			System.out.println(nl.item(i).getNodeName());
		//			System.out.println(nl.item(i).getTextContent());
		//		}
	}

}


