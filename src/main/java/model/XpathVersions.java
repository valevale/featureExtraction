package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import xpath.utils.XpathApplier;

//TODO sposta i metodi di creazione degli xpath in xpath maker
public class XpathVersions {

	private Segment segment;
	private String absoluteXpath;
	private String xpath1;
	private String xpath2;
	private String xpath3;
	private String xpath4;
	private String xpath5;

	public XpathVersions(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		this.segment=segment;
		this.absoluteXpath=segment.getAbsoluteXPath();
		this.xpath1=makeXpath1(segment);
		this.xpath2=makeXpath2(segment);
		this.xpath3=makeXpath3(segment);
		this.xpath4=makeXpath4(segment);
		this.xpath5=makeXpath5(segment);
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

	//xpath assoluto solo con tag
	private String makeXpath1(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		path = buildPath1(node, path);
		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		//il controllo si basa sui nodi w3c ricevuti dai due xpath
		//il segmento si basa sull'xpath assoluto
		if (givesSameSegment(fromListToXpath(path), this.segment))
			return fromListToXpath(path);
		else
			return "";
	}

	private String makeXpath2(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		path = buildPath2(node, path);
		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		if (givesSameSegment(fromListToXpath(path), this.segment))
			return fromListToXpath(path);
		else
			return "";
	}

	private String makeXpath3(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		path = buildPath3(node, path);
		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		if (givesSameSegment(fromListToXpath(path), this.segment))
			return fromListToXpath(path);
		else
			return "";
	}

	private String makeXpath4(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		path = buildPath4(node, path);
		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		if (givesSameSegment(fromListToXpath(path), this.segment))
			return fromListToXpath(path);
		else
			return "";
	}

	private String makeXpath5(Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		Node node = segment.getJsoupNode();
		List<String> path = new ArrayList<>();
		path = buildPath5(node, path);
		//controllo che, se applicato sullo stesso documento, ritorni il segmento stesso che dovrebbe
		//rappresentare
		if (givesSameSegment(fromListToXpath(path), this.segment))
			return fromListToXpath(path);
		else
			return "";
	}

	//xpath assoluto senza indici, class o id; solo tag
	public List<String> buildPath1(Node node, List<String> list) {
		if (node.parent() == null) {
			return list;
		}
		else {
			if (!node.nodeName().equals("#text")) {
				list.add(0, node.nodeName().toString());
				list = buildPath1(node.parent(), list);
				return list;
			}
		}
		return buildPath1(node.parent(), list);
	}

	//xpath che parte dall'ultimo id, poi con indici
	public List<String> buildPath2(Node node, List<String> list) {
		if (node.parent() == null) {
			return list;
		}
		else {
			if (!node.nodeName().equals("#text")) {
				if (node.hasAttr("id")) {
					list.add(0, node.nodeName()+"[@id='"+node.attr("id")+"']");
					return list;
				}
				//controllo se ha fratelli
				if (node.parent().childNodeSize() > 1
						&& !node.nodeName().equals("html")) {
					int i=0;
					int j=1;
					while (i<node.parent().childNodeSize()) {				//scorro i fratelli
						if (!Pattern.matches("^#.*", node.parent().childNode(i).nodeName())
								&& node.parent().childNode(i).nodeName().equals(node.nodeName())) {
							if (node.parent().childNode(i).equals(node)) {	
								//tra i fratelli cerco il nodo in questione, per assegnargli il giusto indice
								list.add(0, node.nodeName()+"["+(j)+"]");
								list = buildPath2(node.parent(), list);
								return list;
							}
							j++;
						}
						i++;
					}
				}
				//non ha fratelli
				else {
					list.add(0, node.nodeName().toString()+"[1]");
					list = buildPath2(node.parent(), list);
					return list;
				}
			}
		}
		return buildPath2(node.parent(), list);
	}

	//xpath troncato all'ultimo id, con class, senza indice
	public List<String> buildPath3(Node node, List<String> list) {
		if (node.parent() == null) {
			return list;
		}
		else {
			if (!node.nodeName().equals("#text")) {
				if (node.hasAttr("id")) {
					list.add(0, node.nodeName()+"[@id='"+node.attr("id")+"']");
					return list;
				}
				//controllo se ha l'attributo class
				if (node.hasAttr("class")) {
					list.add(0, node.nodeName()+"[@class='"+node.attr("class")+"']");
					list = buildPath3(node.parent(), list);
					return list;
				}
				//non ha l'attributo class
				else {
					list.add(0, node.nodeName().toString());
					list = buildPath3(node.parent(), list);
					return list;
				}
			}
		}
		return buildPath3(node.parent(), list);
	}

	//xpath assoluto con id se c'è, altrimenti con class se c'è, altrimenti indice
	public List<String> buildPath4(Node node, List<String> list) {
		if (node.parent() == null) {
			return list;
		}
		else {
			if (!node.nodeName().equals("#text")) {
				//controllo se ha id
				if (node.hasAttr("id")) {
					list.add(0, node.nodeName()+"[@id='"+node.attr("id")+"']");
					list = buildPath4(node.parent(), list);
					return list;
				}
				else if (node.hasAttr("class")) {
					list.add(0, node.nodeName()+"[@class='"+node.attr("class")+"']");
					list = buildPath4(node.parent(), list);
					return list;
				}
				//controllo se ha fratelli
				else if (node.parent().childNodeSize() > 1
						&& !node.nodeName().equals("html")) {
					int i=0;
					int j=1;
					while (i<node.parent().childNodeSize()) {				//scorro i fratelli
						if (!Pattern.matches("^#.*", node.parent().childNode(i).nodeName())
								&& node.parent().childNode(i).nodeName().equals(node.nodeName())) {
							if (node.parent().childNode(i).equals(node)) {	
								//tra i fratelli cerco il nodo in questione, 
								//per assegnargli il giusto indice
								list.add(0, node.nodeName()+"["+(j)+"]");
								list = buildPath4(node.parent(), list);
								return list;
							}
							j++;
						}
						i++;
					}
				}
				//non ha fratelli
				else {
					list.add(0, node.nodeName().toString()+"[1]");
					list = buildPath4(node.parent(), list);
					return list;
				}
			}
		}
		return buildPath4(node.parent(), list);
	}

	//xpath troncato al primo id, poi class senza indice
	public List<String> buildPath5(Node node, List<String> list) {
		//prima ottengo la lista dei nodi fino a html
		Node currentNode = node;
		List<Node> pathNodes = new ArrayList<>();

		while (currentNode != null) {
			//salto il primo nodo che è un #text e l'ultimo che è un #document
			if (!currentNode.nodeName().equals("#text") && !currentNode.nodeName().equals("#document")) {
				pathNodes.add(0, currentNode);
			}
			currentNode = currentNode.parent();
		}

		//ora scorro la lista
		boolean idFound = false;

		for (int i=0; i<pathNodes.size();i++) {
			Node n = pathNodes.get(i);
			if (!idFound) {
				//fino a che non trovo l'id
				if (n.hasAttr("id")) {
					idFound = true;
					list.add(n.nodeName()+"[@id='"+n.attr("id")+"']");
				}
				//se non trovo l'id non aggiungo nulla alla lista e proseguo
			}
			else {	//id già trovato
				//controllo se ha class
				if (n.hasAttr("class")) {
					list.add(n.nodeName()+"[@class='"+n.attr("class")+"']");
				}
				else {	//altrimenti aggiungo il solo tag, senza indice
					list.add(n.nodeName());
				}
			}
		}
		//se la lista è vuota, "path assoluto" con class senza indice
		if (list.isEmpty()) {
			for (int i=0; i<pathNodes.size();i++) {
				Node n = pathNodes.get(i);
				//controllo se ha class
				if (n.hasAttr("class")) {
					list.add(n.nodeName()+"[@class='"+n.attr("class")+"']");
				}
				else {	//altrimenti aggiungo il solo tag, senza indice
					list.add(n.nodeName());
				}
			}
		}
		return list;
	}

	public static String fromListToXpath(List<String> path) {
		String xPath;
		if (path.get(0).equals("html"))	//nessun nodo unico, il path sarà assoluto
			xPath = "/";
		else	//path relativo
			xPath = "//";
		for(int i=0;i<path.size()-1;i++) {
			xPath = xPath + path.get(i)+"/";
		}
		xPath = xPath + path.get(path.size()-1);
		return xPath;
	}

	//data un xpath verifica che corrisponda al segmento dell'oggetto Xpath stesso
	private boolean givesSameSegment(String xpath, Segment segment) throws XPathExpressionException, IOException, ParserConfigurationException {
		XpathApplier xapplier = XpathApplier.getInstance();
//		System.out.println(xpath);
		NodeList xpathNodes = xapplier.getNodes(xpath, segment.getDocument().getDocument());
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
