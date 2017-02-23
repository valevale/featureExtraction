package model;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import lib.utils.XpathApplier;
import lib.utils.XpathMaker;

public class Segment {

	private NodeList w3c_node;
	private Node jsoup_node;
	private int relevance;
	//xpath assoluto
	private Xpath absoluteXpath;
	//xpath generalizzato
	private Xpath xpath;
	private WebPageDocument document;
	private XpathVersions xpathVersions = null;
	
	public Segment() {
	}
	

	public Segment(Node node, WebPageDocument document) throws XPathExpressionException, IOException, ParserConfigurationException {
		this.jsoup_node = node;
		this.document = document;
		XpathMaker xpmaker = XpathMaker.getInstance();
		this.absoluteXpath = new Xpath(node,
				xpmaker.calculateAbsoluteXPath(node, document.getDocument_jsoup()),document.getIdDomain(),0);
		this.xpath=this.absoluteXpath;
		XpathApplier xpapplier = XpathApplier.getInstance();
		//TODO a cosa serve? potremmo fare approccio lazy, cioè lo creiamo solo se serve
		this.w3c_node = xpapplier.getNodes(this.absoluteXpath.getXpath(), document.getDocument_jsoup());
//		this.xpath_specificity = new Tuple2<Xpath,Integer>(new Xpath(this.jsoup_node, this.absoluteXpath.getXpath(), this.document.getIdDomain()),0);
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
	
	public Xpath getAbsoluteXPath() {
		return this.absoluteXpath;
	}
	
	public void setAbsoluteXPath(Xpath xPath) {
		this.absoluteXpath=xPath;
	}
	
	public Xpath getXPath() {
		return this.xpath;
	}
	
	public void setXPath(Xpath xPath) {
		this.xpath=xPath;
	}
	
	public WebPageDocument getDocument() {
		return this.document;
	}
	
	public XpathVersions getXpathVersions() {
		return this.xpathVersions;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((absoluteXpath == null) ? 0 : absoluteXpath.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
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
		Segment other = (Segment) obj;
		if (absoluteXpath == null) {
			if (other.absoluteXpath != null)
				return false;
		} else if (!absoluteXpath.equals(other.absoluteXpath))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		return true;
	}
	
	
}
