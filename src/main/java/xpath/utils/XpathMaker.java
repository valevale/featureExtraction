package xpath.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

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

	//dato un nodo, restituisce il suo xpath
//	public String calculateXPath(Node node, Document doc) throws XPathExpressionException, IOException, ParserConfigurationException {
//		List<String> path = new ArrayList<>();
//		path = buildStrongPath(node, path, doc);
//		//procedura per rimuovere gli indici superflui
//		for (int i=0; i<path.size(); i++) {
//			String currentNode = path.get(i);
//			if (currentNode.length()>1 &&
//					currentNode.charAt(currentNode.length()-2) >= '0' && 
//					currentNode.charAt(currentNode.length()-2) <= '9') {  //se possiede un indice
//				if (isUselessIndex(path, doc, i)) {
//					//						System.out.println("INDICE "+ i +" INUTILE");
//					currentNode = currentNode.substring(0, currentNode.indexOf('['));
//					path.set(i, currentNode);
//				}
//			}
//		}
//		return fromListToXpath(path);
//	}

	//	public String calculateXPath_test(Node node, Document doc, String buildingParameter) throws XPathExpressionException, IOException, ParserConfigurationException {
	//		List<String> path = new ArrayList<>();
	//		if (buildingParameter.equals("normal"))
	//			path = buildPathAlternative(node, path);
	//		if (buildingParameter.equals("strong")) {
	//			path = buildStrongPath(node, path, doc);
	//			//procedura per rimuovere gli indici superflui
	//			for (int i=0; i<path.size(); i++) {
	//				String currentNode = path.get(i);
	//				if (currentNode.length()>1 &&
	//						currentNode.charAt(currentNode.length()-2) >= '0' && 
	//						currentNode.charAt(currentNode.length()-2) <= '9') {  //se possiede un indice
	//					if (isUselessIndex(path, doc, i)) {
	//						//						System.out.println("INDICE "+ i +" INUTILE");
	//						currentNode = currentNode.substring(0, currentNode.indexOf('['));
	//						path.set(i, currentNode);
	//					}
	//				}
	//			}
	//		}
	//		return fromListToXpath(path);
	//	}

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

	/* restituisce true se il nodo del path indicato dall'indice
	 * si rivela essere superfluo, ovvero se rimuovendolo, l'xpath che si deriva
	 * restituisce sempre 1 e 1 solo nodo */
//	public boolean isUselessIndex(List<String> path, Document doc, int index) throws XPathExpressionException, IOException, ParserConfigurationException {
//		List<String> newPath = new ArrayList<>(path);
//
//		newPath.set(index, newPath.get(index).substring(0, newPath.get(index).indexOf('[')));
//
//		//controllo se restituisce 1
//		String xPath = fromListToXpath(newPath);
//		//			System.out.println(xPath);
//		NodeList nl = xpapplier.getNodes(xPath, doc);
//		return nl.getLength()==1;
//	}


	/*versione della creazione dell'xpath molto generica, che usa * al posto dei nomi dei tag*/
	//	public List<String> buildPath(Node node, List<String> list) {
	//		//		System.out.println("il nodo: "+node);
	//		if (node.parent() == null) {
	//			return list;
	//		}
	//		else {
	//			//			System.out.println("NODENAME DEL NODO CORRENTE "+node.nodeName());
	//			if (!node.nodeName().equals("#text")) {
	//				//controllo se ha fratelli
	//				if (node.parent().childNodeSize() > 1
	//						&& !node.nodeName().equals("html")) {
	//					int i=0;
	//					int j=1;
	//					while (i<node.parent().childNodeSize()) {				//scorro i fratelli
	//						//						System.out.println("IL FIGLIO"+ node.parent().childNode(i).toString()
	//						//								+ "\n e NODENAME: "+ node.parent().childNode(i).nodeName());
	//						if (!Pattern.matches("^#.*", node.parent().childNode(i).nodeName())
	//								//!node.parent().childNode(i).toString().equals(" ")
	//								//&& !node.parent().childNode(i).toString().equals(" &gt; ")
	//								//&& 
	//								//!node.parent().childNode(i).nodeName().equals("#text")
	//								) {
	//							//							System.out.println("NON E' UNO SPAZIO BIANCO! è il figlio "+ j);
	//							if (node.parent().childNode(i).equals(node)) {	
	//								//								System.out.println("e' il nodo corrente!");
	//								//tra i fratelli cerco il nodo in questione, per assegnargli il giusto indice
	//								list.add(0, "*["+(j)+"]");
	//								list = buildPath(node.parent(), list);
	//								return list;
	//							}
	//							j++;
	//						}
	//						i++;
	//					}
	//				}
	//				//non ha fratelli
	//				else {
	//					//					System.out.println("NON HA FRATELLI");
	//					list.add(0, node.nodeName().toString());
	//					list = buildPath(node.parent(), list);
	//					return list;
	//				}
	//			}
	//			//			System.out.println("E' #text");
	//			//			System.out.println();
	//		}
	//		return buildPath(node.parent(), list);
	//	}

	/*versione più robusta, che non utilizza * nei nomi dei tag*/
	public List<String> buildAbsolutePath(Node node, List<String> list) {
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
					//					System.out.println("NON HA FRATELLI");
					list.add(0, node.nodeName().toString()+"[1]");
					list = buildAbsolutePath(node.parent(), list);
					return list;
				}
			}
			//			System.out.println("E' #text");
			//			System.out.println();
		}
		return buildAbsolutePath(node.parent(), list);
	}

	/*versione ancora più robusta, aggiunta dei path relativi*/
	public List<String> buildStrongPath(Node node, List<String> list, Document doc) throws XPathExpressionException, IOException, ParserConfigurationException {
		//		System.out.println("il nodo: "+node);
		if (node.parent() == null) {
			return list;
		}
		else {
			//			System.out.println("NODENAME DEL NODO CORRENTE "+node.nodeName());
			if (!node.nodeName().equals("#text")) {
				String nodeToCheck = isNodoUnico(node, list, doc);
				if (list.size()!=0 && nodeToCheck!=null) {
					//						System.out.println("E' UNICO");
					list.add(0, nodeToCheck);
					return list;
				}
				//					System.out.println("NON E' UNICO");
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
								list = buildStrongPath(node.parent(), list, doc);
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
					list = buildStrongPath(node.parent(), list, doc);
					return list;
				}
			}
			//			System.out.println("E' #text");
			//			System.out.println();
		}
		return buildStrongPath(node.parent(), list, doc);
	}

	/* metodo che controlla se un nodo è unico nel DOM:
	 * viene generato un path relativo che parte da quel nodo.
	 * si verifica quindi l'xpath generato.
	 * se restituisce 1 e 1 solo nodo, allora è un nodo unico. altrimenti non lo è.*/
	public String isNodoUnico(Node node, List<String> sonsList, Document doc) throws XPathExpressionException, IOException, ParserConfigurationException {

		//			System.out.println("SARA' UNICO?");
		List<String> nodesList = new ArrayList<>(sonsList);
		nodesList.add(0, node.nodeName());
		//		System.out.println(sonsList.size());
		String xPath = fromListToXpath(nodesList);
		//			System.out.println("primo path: "+xPath);
		NodeList nl = xpapplier.getNodes(xPath, doc);
		if(nl.getLength()==1) {
			//				System.out.println("fine metodo true1");
			return node.nodeName();
		}
		//attributi
		else {
			//				System.out.println(nl.getLength()+" non unico");
			if (node.hasAttr("class")){
				//					System.out.println("HA CLASS");
				nodesList = new ArrayList<>(sonsList);
				nodesList.add(0, node.nodeName()+"[@class]");
				xPath = fromListToXpath(nodesList);
				//					System.out.println("il path con CLASS "+xPath);
				nl = xpapplier.getNodes(xPath, doc);
				//					System.out.println(nl.getLength());
				if(nl.getLength()==1) {
					//						System.out.println("fine metodo true2");
					return node.nodeName()+"[@class]";
				}
				//valori
				nodesList = new ArrayList<>(sonsList);
				nodesList.add(0, node.nodeName()+"[@class='"+node.attr("class")+"']");
				xPath = fromListToXpath(nodesList);
				//					System.out.println("il path con CLASS valore "+xPath);
				nl = xpapplier.getNodes(xPath, doc);
				//					System.out.println(nl.getLength());
				if(nl.getLength()==1) {
					//						System.out.println("fine metodo true3");
					return node.nodeName()+"[@class='"+node.attr("class")+"']";
				}
			}

			else if (node.hasAttr("id")) {
				//					System.out.println("HA ID");
				nodesList = new ArrayList<>(sonsList);
				nodesList.add(0, node.nodeName()+"[@id]");
				xPath = fromListToXpath(nodesList);
				//					System.out.println("il path con ID "+xPath);
				nl = xpapplier.getNodes(xPath, doc);
				//					System.out.println(nl.getLength());
				if(nl.getLength()==1) {
					//						System.out.println("fine metodo true4");
					return node.nodeName()+"[@id]";
				}
				//valori
				nodesList = new ArrayList<>(sonsList);
				nodesList.add(0, node.nodeName()+"[@id='"+node.attr("id")+"']");
				xPath = fromListToXpath(nodesList);
				//					System.out.println("il path con ID valore "+xPath);
				nl = xpapplier.getNodes(xPath, doc);
				//					System.out.println(nl.getLength());
				if(nl.getLength()==1) {
					//						System.out.println("fine metodo true5");
					return node.nodeName()+"[@id='"+node.attr("id")+"']";
				}

			}
		}
		//			System.out.println("fine metodo false");
		return null;
	}


	public static void main(String[] args) {
		String x = "pddd[edwfefewef0]";
		System.out.println(x.indexOf('['));
		System.out.println(x.substring(0, x.indexOf('[')));
	}
}
