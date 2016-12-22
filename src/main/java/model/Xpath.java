package model;

import org.jsoup.nodes.Node;

public class Xpath {

	private String xpath;
	private Node node;
	
	public Xpath(Node node, String xpath){
		this.node=node;
		this.xpath=xpath;
	}
	
	public Node getNode() {
		return this.node;
	}
	
	public String getXpath() {
		return this.xpath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xpath == null) ? 0 : xpath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Xpath other = (Xpath) obj;
		if (xpath == null) {
			if (other.xpath != null)
				return false;
		} else if (!xpath.equals(other.xpath))
			return false;
		return true;
	}
	
}
