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

import configurations.Configurator;
import segmentation.DocumentCleaner;
import segmentation.SegmentExtractor;

public class WebPageDocument {

	private Set<Segment> segments;
	private Document document_jsoup;
	private DomainSource dSource;
	private org.w3c.dom.Document document_w3c;
	//	private int idDomain;
	//	private String idDomain;
	//TODO vedi se puoi metterla da qualche parte
	//	private Source source;
	private String idWebPage;

	public WebPageDocument(File html_document, int cleaningSourceParameter, String folder,
			double parameterTextFusion, int sourceParameter) throws Exception {
		//		this.idDomain=""+sourceParameter;
		this.document_jsoup = prepareDocument(html_document, cleaningSourceParameter, folder);
		this.document_w3c = new W3CDom().fromJsoup(this.document_jsoup);
		SegmentExtractor xpextractor = SegmentExtractor.getInstance();
		this.segments = xpextractor.extractSegments(this, parameterTextFusion);

		DomainsRepository domRep = DomainsRepository.getInstance();
		this.dSource = domRep.createDomain(""+sourceParameter);
	}

	public WebPageDocument(WebPage webpage, Source source) throws Exception {
		this.document_jsoup = prepareDocument_server(webpage.getHtml(), source);
		this.document_w3c = new W3CDom().fromJsoup(this.document_jsoup);
		DomainsRepository domRep = DomainsRepository.getInstance();
		this.dSource = domRep.createDomain(source.getId().toString());
		this.idWebPage = webpage.getId().toString();
		SegmentExtractor xpextractor = SegmentExtractor.getInstance();
		this.segments = xpextractor.extractSegments(this, Configurator.getSegmentationParameter());
	}

	//TODO metti in una configurazione questi parametri
	public WebPageDocument(WebPage webpage, String source) throws Exception {
		this.document_jsoup = prepareDocument_server(webpage.getHtml(), source);
		this.document_w3c = new W3CDom().fromJsoup(this.document_jsoup);
		DomainsRepository domRep = DomainsRepository.getInstance();
		this.dSource = domRep.createDomain(source);
		this.idWebPage = webpage.getId().toString();
		SegmentExtractor xpextractor = SegmentExtractor.getInstance();
		this.segments = xpextractor.extractSegments(this, Configurator.getSegmentationParameter());
	}

	//solo per prove
	public String getIdPage() {
		return this.idWebPage;
	}

	public Segment getSegmentByXpath(String xpath) {
		Iterator<Segment> segIterator = this.segments.iterator();
		while (segIterator.hasNext()) {
			Segment segment = segIterator.next();
			if (segment.getAbsoluteXPath().getXpath().equals(xpath)) return segment;
		}
		System.out.println("Error getting segment by xpath: couldn't find any segment");
		return null;
	}

	public Set<Segment> getSegments() {
		return this.segments;
	}

	public String getIdDomain() {
		//		return this.idDomain;
		return this.dSource.getParameter();
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

	public Set<Xpath> getXPaths() {
		Set<Xpath> xpaths = new HashSet<>();
		Iterator<Segment> segIt = this.segments.iterator();
		while (segIt.hasNext()) {
			Segment s = segIt.next();
			xpaths.add(s.getAbsoluteXPath());
		}
		return xpaths;
	}

	public DomainSource getSource() {
		return this.dSource;
	}

	/*pulisce la pagina*/
	private Document prepareDocument(File html_document, int sourceParameter,
			String folder) throws Exception {
		String htmlDocumentString = IOUtils.toString(new FileReader(html_document));
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document = Jsoup.parse(cleanedHTML);
		DocumentCleaner docCleaner = DocumentCleaner.getInstance();
		Document documentCleaned = docCleaner.removeTemplate(document,
				sourceParameter, folder);
		return documentCleaned;
	}

	/*pulisce la pagina*/
	private Document prepareDocument_server(String html_document, Source source) throws Exception {
		String htmlDocumentString = html_document;
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document = Jsoup.parse(cleanedHTML);
		DocumentCleaner docCleaner = DocumentCleaner.getInstance();
		Document documentCleaned = docCleaner.removeTemplate_server(document, source);
		return documentCleaned;
	}

	/*pulisce la pagina*/
	private Document prepareDocument_server(String html_document, String source) throws Exception {
		String htmlDocumentString = html_document;
		String cleanedHTML = Jsoup.clean(htmlDocumentString, Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document = Jsoup.parse(cleanedHTML);
		DocumentCleaner docCleaner = DocumentCleaner.getInstance();
		Document documentCleaned = docCleaner.removeTemplate_server(document, source);
		return documentCleaned;
	}

}
