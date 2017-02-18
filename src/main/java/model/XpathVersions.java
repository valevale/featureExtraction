package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import lib.utils.XpathApplier;
import lib.utils.XpathMaker;

public class XpathVersions {

	//TODO vedi se è necessario davvero memorizzarlo come oggetto
	private Segment segment;
	private String absoluteXpath;
	private String xpath1;
	private String xpath2;
	private String xpath3;
	private String xpath4;
	private String xpath5;
	private XpathMaker xmaker;

	public XpathVersions(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		this.xmaker=XpathMaker.getInstance();
		this.segment=segment;
		this.absoluteXpath=segment.getAbsoluteXPath().getXpath();
		this.xpath1=makeXpath(segment, 1);
		this.xpath2=makeXpath(segment, 2);
		this.xpath3=makeXpath(segment, 3);
		this.xpath4=makeXpath(segment, 4);
		this.xpath5=makeXpath(segment, 5);
	}

	public Segment getSegment() {
		return this.segment;
	}

	public String getPathBySpecificity(int specificityParameter) {
		switch (specificityParameter) {
		case 0:  return getAbsoluteXpath();
		case 1:  return getXpath1();
		case 2:  return getXpath2();
		case 3:  return getXpath3();
		case 4:  return getXpath4();
		case 5:  return getXpath5();
		default: return "";
		}
	}

	public String getAbsoluteXpath() {
		return this.absoluteXpath;
	}

	public String getXpath1() {
		return this.xpath1;
	}

	public String getXpath2() {
		return this.xpath2;
	}

	public String getXpath3() {
		return this.xpath3;
	}

	public String getXpath4() {
		return this.xpath4;
	}

	public String getXpath5() {
		return this.xpath5;
	}

	private String makeXpath(Segment segment, int specificityParameter) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		switch (specificityParameter) {
		case 1:  path = xmaker.buildPath1(node, path); break;
		case 2:  path = xmaker.buildPath2(node, path); break;
		case 3:  path = xmaker.buildPath3(node, path); break;
		case 4:  path = xmaker.buildPath4(node, path); break;
		case 5:  path = xmaker.buildPath5(node, path); break;
		default: return "";
		}

		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		//il controllo si basa sui nodi w3c ricevuti dai due xpath
		//il segmento si basa sull'xpath assoluto
		String xpath = xmaker.fromListToXpath(path);
		if (givesSameSegment(xpath, this.segment))
			return xpath;
		else
			return "";
	}
	//
	//	private String makeXpath2(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		Node node = segment.getJsoupNode();
	//		List<String> path = new ArrayList<>();
	//		path = buildPath2(node, path);
	//		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
	//		//rappresentare
	//		if (givesSameSegment(fromListToXpath(path), this.segment))
	//			return fromListToXpath(path);
	//		else
	//			return "";
	//	}
	//
	//	private String makeXpath3(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		Node node = segment.getJsoupNode();
	//		List<String> path = new ArrayList<>();
	//		path = buildPath3(node, path);
	//		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
	//		//rappresentare
	//		if (givesSameSegment(fromListToXpath(path), this.segment))
	//			return fromListToXpath(path);
	//		else
	//			return "";
	//	}
	//
	//	private String makeXpath4(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		Node node = segment.getJsoupNode();
	//		List<String> path = new ArrayList<>();
	//		path = buildPath4(node, path);
	//		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
	//		//rappresentare
	//		if (givesSameSegment(fromListToXpath(path), this.segment))
	//			return fromListToXpath(path);
	//		else
	//			return "";
	//	}
	//
	//	private String makeXpath5(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		Node node = segment.getJsoupNode();
	//		List<String> path = new ArrayList<>();
	//		path = buildPath5(node, path);
	//		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
	//		//rappresentare
	//		if (givesSameSegment(fromListToXpath(path), this.segment))
	//			return fromListToXpath(path);
	//		else
	//			return "";
	//	}



	//data un xpath verifica che corrisponda al segmento dell'oggetto Xpath stesso
	private boolean givesSameSegment(String xpath, Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		XpathApplier xapplier = XpathApplier.getInstance();
		//		System.out.println(xpath);
		NodeList xpathNodes = xapplier.getNodes(xpath, segment.getDocument().getDocument_jsoup());
		//il numero di nodi è necessariamente 1
		if (xpathNodes.getLength()==1)
			return (xpathNodes.item(0).isEqualNode(segment.getW3cNodes().item(0)));
		else
			return false;
		//		SUPPONIAMO CHE vogliamo che matchi almeno con quello che abbiamo, poi altri
		//		for (int i=0; i<xpathNodes.getLength(); i++) {
		//			if (xpathNodes.item(i).isEqualNode(segment.getW3cNodes().item(0)))
		//				return true;
		//		}
		//		return false;
	}

	//	/*metodo per controllare se l'xpath applicato alla pagina restituisce solo segmenti rilevanti */
	//	private boolean controllo2(String xpath) throws 
	//	XPathExpressionException, IOException, ParserConfigurationException {
	//		boolean controllo = true;
	//		//prendo i segmenti rilevanti della pagina
	//		Set<Segment> pageSegments = this.segment.getDocument().getSegments();
	//		Set<Segment> relevantPageSegments = new HashSet<>();
	//		pageSegments.forEach(currentPageSegment -> {
	//			if (currentPageSegment.getRelevance()>0) {
	//				relevantPageSegments.add(currentPageSegment);
	//			}
	//		});
	//
	//		XpathApplier xapplier = XpathApplier.getInstance();
	//		NodeList xpathNodes = xapplier.getNodes(xpath, this.segment.getDocument().getDocument());
	//		//scorriamo i nodi ricevuti. ognuno deve matchare con un segmento rilevante del documento
	//		for(int i=0;i<xpathNodes.getLength();i++) {
	//			org.w3c.dom.Node n = xpathNodes.item(i);
	//			Iterator<Segment> itRelevantPageSegments = relevantPageSegments.iterator();
	//			//in questo ciclo n deve matchare con almeno uno
	//			boolean matched = false;
	//			while (itRelevantPageSegments.hasNext()) {
	//				NodeList relevantPageNodes = itRelevantPageSegments.next().getW3cNodes();
	//				//qui sappiamo che il nodo è uno solo...
	//				for(int j=0;j<relevantPageNodes.getLength();j++) {
	//					if (n==relevantPageNodes.item(j))
	//						matched=true;
	//				}
	//			}
	//			//se uno dei nodi di xpathNodes non trova matching, il controllo è tutto falso
	//			if (!matched)
	//				controllo=false;
	//		}
	//		return controllo;
	//	}
}
