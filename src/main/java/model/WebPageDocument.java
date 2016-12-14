package model;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Whitelist;

import lib.utils.NodeUtils;
import scala.Tuple2;
import xpath.utils.XpathExtractor;

public class WebPageDocument {

	private Set<Segment> segments;
	private Document document_jsoup;
	//TODO potresti trasformarlo in un oggetto xpath?
	private Set<Tuple2<String,Node>> xPaths_nodes;
	private Set<String> xPaths;
	private List<String> genericXpaths;
	
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
		this.xPaths_nodes = xpextractor.getXPathsFromDocument(this.document_jsoup,
				parameter, folder, parameterTextFusion);
		this.segments = extractSegments(this.xPaths_nodes, this.document_jsoup);
//		System.out.println(this.segments.size());
		this.xPaths = extractXPaths(this.xPaths_nodes);
		this.genericXpaths = null;
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
	
	public Document getDocument() {
		return this.document_jsoup;
	}
	
	public void setDocument(Document doc) {
		this.document_jsoup = doc;
	}
	
	public Set<String> getXPaths() {
		return this.xPaths;
	}
	
	public void setXPaths(Set<String> xPaths) {
		this.xPaths = xPaths;
	}
	
	public List<String> getGenericXPaths() {
		return this.genericXpaths;
	}
	
	public void setGenericXPaths(List<String> xPaths) {
		this.genericXpaths = xPaths;
	}
	
	/* Dato un set di stringhe rappresentanti xPath e un documento Jsoup,
	 * restituisce un set di porzioni del documento (segmenti), ovvero insiemi di nodi
	 * dell'albero html, corrispondenti a quegli xPath
	 * */
	private Set<Segment> extractSegments(Set<Tuple2<String,Node>> xPaths_nodes, Document document) {

//		System.out.println("QUI "+xPaths_nodes.size());
		
		Set<Segment> segments = new HashSet<>();

		xPaths_nodes.forEach(xPath_node -> {

			try {
				//controllo che impedisce di aggiungere segmenti il cui contenuto è irrilevante
				//(composto da un solo carattere)
//				System.out.println(xPath_node._1());
//				System.out.println(xPath_node._2());
				Segment toAdd = new Segment(xPath_node._1(), xPath_node._2(), this);
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
	
	private Set<String> extractXPaths(Set<Tuple2<String,Node>> xPaths_nodes) {

		Set<String> xPaths = new HashSet<>();

		xPaths_nodes.forEach(xPath_node -> {
			String xpath = xPath_node._1();
			xPaths.add(xpath);
		});

		return xPaths;
	}
}
