package xpath.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class XpathMaker {

	XpathApplier xpapplier;

	private static XpathMaker instance = null;

	public static XpathMaker getInstance() {
		if (instance == null)
			instance = new XpathMaker();
		return instance;
	}

	private XpathMaker() {
		xpapplier = XpathApplier.getInstance();
	}

	//dato un nodo, restituisce il suo xpath assoluto
	public String calculateAbsoluteXPath(Node node, Document doc) throws XPathExpressionException, IOException, ParserConfigurationException {
		List<String> path = new ArrayList<>();
		path = buildAbsolutePath(node, path);
		return fromListToXpath(path);
	}

	public String fromListToXpath(List<String> path) {
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

	/*versione più robusta, che non utilizza * nei nomi dei tag*/
	public List<String> buildAbsolutePath(Node node, List<String> list) {
		if (node.parent() == null) {
			return list;
		}
		else {
			if (!node.nodeName().equals("#text")) {
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
								list = buildAbsolutePath(node.parent(), list);
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
					list = buildAbsolutePath(node.parent(), list);
					return list;
				}
			}
		}
		return buildAbsolutePath(node.parent(), list);
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
}
