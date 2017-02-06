package lib.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

public class NodeUtils {

	public static String getNodesContent(NodeList nl) {
		String content = "";

		for (int i = 0; i < nl.getLength(); i++) {

			content = content + nl.item(i).getTextContent();
		}

		return content;
	}

	public static List<String> getNodesTypeOf(NodeList nl) throws XPathExpressionException {
		List<String> types = new ArrayList<>();

		for (int i = 0; i < nl.getLength(); i++) {
			types.add(nl.item(i).getNodeName());
		}

		return types;
	}

	public static void removeComments(Node node) {
		for (int i = 0; i < node.childNodes().size();) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment"))
				child.remove();
			else {
				removeComments(child);
				i++;
			}
		}
	}
	
	
}


