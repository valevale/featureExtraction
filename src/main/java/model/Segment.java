package model;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Document;
import org.w3c.dom.NodeList;

import segmentation.XPathMaker;

public class Segment {

	private NodeList nodes;
	private int relevance;
	private String xPath;
	
	public Segment() {
	}

	public Segment(String xPath, Document document) throws XPathExpressionException {
		this.xPath = xPath;
		XPathMaker xpMaker = XPathMaker.getInstance();
		this.nodes = xpMaker.getNodes(xPath, document);
	}
	
	public NodeList getNodes() {
		return this.nodes;
	}
	
	public void setNode(NodeList nodes) {
		this.nodes=nodes;
	}
	
	public int getRelevance() {
		return this.relevance;
	}
	
	public void setRelevance(int relevance) {
		this.relevance=relevance;
	}
	
	public String getXPath() {
		return this.xPath;
	}
	
	public void setXPath(String xPath) {
		this.xPath=xPath;
	}
	
	
}
