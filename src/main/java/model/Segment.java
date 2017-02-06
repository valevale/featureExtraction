package model;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.jsoup.nodes.Node;
import org.w3c.dom.NodeList;

import xpath.utils.XpathApplier;
import xpath.utils.XpathMaker;

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
}
