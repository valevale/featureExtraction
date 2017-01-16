package model;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import lib.utils.NodeUtils;
import xpath.utils.XpathExtractor;

public class WebPageDocument {

	private Set<Segment> segments;
	private Document document_jsoup;
	private Set<Xpath> xpaths;
//	private Set<Tuple2<String,Node>> xPaths_nodes;
//	private Set<String> xPaths;
//	private Set<Xpath> genericXpaths;
	private DomainSource source;
	private org.w3c.dom.Document document_w3c;
	
	public WebPageDocument(File html_document) throws Exception {
		String htmlDocumentString = IOUtils.toString(new FileReader(html_document));
		htmlDocumentString = Jsoup.clean(htmlDocumentString, Whitelist.relaxed().addAttributes(":all", "class", "id"));
		this.document_jsoup = Jsoup.parse(htmlDocumentString);
	}
	
	public WebPageDocument(File html_document, int parameter, String folder, double parameterTextFusion) throws Exception {
		String htmlDocumentString = IOUtils.toString(new FileReader(html_document));
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		this.document_jsoup = Jsoup.parse(cleanedHTML);
		XpathExtractor xpextractor = XpathExtractor.getInstance();
		this.xpaths = xpextractor.getXPathsFromDocument(this.document_jsoup,
				parameter, folder, parameterTextFusion);
		this.segments = extractSegments(this.xpaths, this.document_jsoup);
//		System.out.println(this.segments.size());
//		this.xPaths = extractXPaths(this.xpaths);
//		this.genericXpaths = null;
//		this.source = DomainFacotyr.getDomain(parameter);
	}
	
	public WebPageDocument(File html_document, int parameter, String folder,
			double parameterTextFusion, int sourceParameter) throws Exception {
		String htmlDocumentString = IOUtils.toString(new FileReader(html_document));
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		this.document_jsoup = Jsoup.parse(cleanedHTML);
		XpathExtractor xpextractor = XpathExtractor.getInstance();
		this.xpaths = xpextractor.getXPathsFromDocument(this.document_jsoup,
				parameter, folder, parameterTextFusion);
		this.segments = extractSegments(this.xpaths, this.document_jsoup);
//		this.genericXpaths = null;
		DomainsRepository domRep = DomainsRepository.getInstance();
		this.source = domRep.createDomain(sourceParameter);
		this.document_jsoup.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		this.document_w3c = new W3CDom().fromJsoup(this.document_jsoup);
	}
	
	public Segment getSegmentByXpath(String xpath) {
		Iterator<Segment> segIterator = this.segments.iterator();
		while (segIterator.hasNext()) {
			Segment segment = segIterator.next();
			if (segment.getAbsoluteXPath().equals(xpath)) return segment;
		}
		System.out.println("Error getting segment by xpath: couldn't find any segment");
		return null;
	}
	
	public Set<Segment> getSegments() {
		return this.segments;
	}
	
	public void setSegments(Set<Segment> segments) {
		this.segments = segments;
	}
	
	public Document getDocument_jsoup() {
		return this.document_jsoup;
	}
	
	public org.w3c.dom.Document getDocument_w3c() {
		return this.document_w3c;
	}
	
//	public void setDocument(Document doc) {
//		this.document_jsoup = doc;
//	}
	
	public Set<Xpath> getXPaths() {
		return this.xpaths;
	}
	
//	public Set<Xpath> getGenericXPaths() {
//		return this.genericXpaths;
//	}
	
//	public void setGenericXPaths(Set<Xpath> xPaths) {
//		this.genericXpaths = xPaths;
//	}
	
	public DomainSource getSource() {
		return this.source;
	}
	
	/* Dato un set di stringhe rappresentanti xPath e un documento Jsoup,
	 * restituisce un set di porzioni del documento (segmenti), ovvero insiemi di nodi
	 * dell'albero html, corrispondenti a quegli xPath
	 * */
	private Set<Segment> extractSegments(Set<Xpath> xPaths, Document document) {

//		System.out.println("QUI "+xPaths_nodes.size());
		
		Set<Segment> segments = new HashSet<>();

		xPaths.forEach(xPath -> {

			try {
				//controllo che impedisce di aggiungere segmenti il cui contenuto è irrilevante
				//(composto da un solo carattere)
//				System.out.println(xPath_node._1());
//				System.out.println(xPath_node._2());
				Segment toAdd = new Segment(xPath.getXpath(), xPath.getNode(), this);
				String content = NodeUtils.getNodesContent(toAdd.getW3cNodes());
//				System.out.println(content);
				String cleaned = content.replaceAll("[^a-zA-Z0-9]+", "");
				if (cleaned.length() > 1) {
					segments.add(toAdd);
				}
			} catch (Exception e) {
				System.out.println("Errore durante la generazione di un segmento "+e);
			}
		});

		return segments;
	}
	
//	private Set<String> extractXPaths(Set<Xpath> xPaths) {
//
//		Set<String> xPaths = new HashSet<>();
//
//		xPaths.forEach(xPath_node -> {
//			String xpath = xPath_node._1();
//			xPaths.add(xpath);
//		});
//
//		return xPaths;
//	}
}
