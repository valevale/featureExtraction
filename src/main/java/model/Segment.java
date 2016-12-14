package model;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import scala.Tuple2;
import xpath.utils.XpathApplier;

public class Segment {

	private NodeList w3c_node;
	private Node jsoup_node;
	private int relevance;
	//xpath assoluto
	private String absoluteXpath;
	private Tuple2<String,Integer> xpath_specificity;
	private WebPageDocument document;
	private XpathVersions xpathVersions = null;
	
	public Segment() {
	}

	public Segment(String xPath, Node node, WebPageDocument document) throws XPathExpressionException, IOException, ParserConfigurationException {
		this.absoluteXpath = xPath;
		XpathApplier xpapplier = XpathApplier.getInstance();
		this.w3c_node = xpapplier.getNodes(xPath, document.getDocument());
		if (this.w3c_node.getLength() == 0) System.out.println("RESTITUITO 0");
		this.jsoup_node = node;
		this.document = document;
		this.xpath_specificity = new Tuple2<String,Integer>(this.absoluteXpath,0);
	}
	
	public void makeXpathVersions() throws XPathExpressionException, IOException, ParserConfigurationException {
		this.xpathVersions = new XpathVersions(this);
	}
	
	public NodeList getW3cNodes() {
		return this.w3c_node;
	}
	
	public Node getJsoupNode() {
		return this.jsoup_node;
	}
	
	public int getRelevance() {
		return this.relevance;
	}
	
	public void setRelevance(int relevance) {
		this.relevance=relevance;
	}
	
	public String getAbsoluteXPath() {
		return this.absoluteXpath;
	}
	
	public void setAbsoluteXPath(String xPath) {
		this.absoluteXpath=xPath;
	}
	
	public String getXPath() {
		return this.xpath_specificity._1();
	}
	
	public void setXPath(String xPath, int specificity) {
		this.xpath_specificity= new Tuple2<>(xPath,specificity);
	}
	
	public WebPageDocument getDocument() {
		return this.document;
	}
	
	public XpathVersions getXpathVersions() {
		return this.xpathVersions;
	}
}
