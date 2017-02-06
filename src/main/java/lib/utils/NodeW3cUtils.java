package lib.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class NodeW3cUtils {


	public static boolean areEqualNodes(Node n1, Node n2) {
		if (n1 != null) {
			if (n1.isEqualNode(n2)) {
				if (areEqualNodes(n1.getParentNode(),n2.getParentNode())) {
					Node siblingN1 = n1.getNextSibling();
					Node siblingN2 = n2.getNextSibling();
					if ((siblingN1 == null && siblingN2 != null)
							|| (siblingN1 != null && siblingN2 == null)) {
						return false;
					}
					if (siblingN1 == null && siblingN2 == null) {
						return true;
					}
					if (siblingN1.isEqualNode(siblingN2)) {
						List<Node> nextSiblingsN1 = new ArrayList<>();
						getNextSiblings(n1, nextSiblingsN1);
						List<Node> nextSiblingsN2 = new ArrayList<>();
						getNextSiblings(n2, nextSiblingsN2);
						if (areEqualListOfNodes(nextSiblingsN1, nextSiblingsN2)) {
							List<Node> previousSiblingsN1 = new ArrayList<>();
							getPreviousSiblings(n1, previousSiblingsN1);
							List<Node> previousSiblingsN2 = new ArrayList<>();
							getPreviousSiblings(n2, previousSiblingsN2);
							if (areEqualListOfNodes(previousSiblingsN1, previousSiblingsN2)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		return n2 == null;
	}

	//riempie la lista passata con i fratelli di destra del nodo, NON compreso il nodo stesso
	public static void getNextSiblings(Node n, List<Node> nextSiblingsList) {
		Node nextSibling = n.getNextSibling();
		if (nextSibling == null)
			return;
		nextSiblingsList.add(nextSibling);
		getNextSiblings(nextSibling, nextSiblingsList);
	}

	//riempie la lista passata con i fratelli di sinistra del nodo, NON compreso il nodo stesso
	public static void getPreviousSiblings(Node n, List<Node> previousSiblingsList) {
		Node prevSibling = n.getPreviousSibling();
		if (prevSibling == null)
			return;
		previousSiblingsList.add(prevSibling);
		getNextSiblings(prevSibling, previousSiblingsList);
	}

	//confronta se due liste hanno gli stessi nodi
	public static boolean areEqualListOfNodes(List<Node> list1, List<Node> list2) {
		if (list1.size() != list2.size()) return false;
		for (int i=0; i<list1.size(); i++) {
			Node n1 = list1.get(i);
			Node n2 = list2.get(i);
			if (!(n1.isEqualNode(n2)))
				return false;
		}
		return true;
	}
}
