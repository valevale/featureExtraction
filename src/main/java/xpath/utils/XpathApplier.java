package xpath.utils;

import java.io.IOException;
import java.util.Set;

import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import model.Segment;
import model.Xpath;

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

	public NodeList getNodes(String path, Document document) throws XPathExpressionException, IOException {

		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpathObj = xPathfactory.newXPath();
		XPathExpression expr = xpathObj.compile(path);
		NodeList nl = null;
		try {
			org.w3c.dom.Document w3doc = new W3CDom().fromJsoup(document);
			nl = (NodeList) expr.evaluate(w3doc, XPathConstants.NODESET);
		}
		catch (Exception e) {
			System.out.println("Errore durante la conversione del documento, da jsoup a w3c: "+e);
			e.printStackTrace();
		}

		return nl;
	}

	
	public org.w3c.dom.Document color(Set<Xpath> paths, Document document) throws XPathExpressionException {

		org.w3c.dom.Document domDocument = prepareDoc(document);
		if (domDocument == null) {return null;}
		paths.forEach(path -> {
			String xpath = path.getXpath();
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

			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpathObj = xPathfactory.newXPath();
			XPathExpression expr = null;
			try {
				expr = xpathObj.compile(segment.getAbsoluteXPath().getXpath());
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
	public org.w3c.dom.Document prepareDoc(Document document) throws XPathExpressionException {
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

}


