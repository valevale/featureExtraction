package model;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;

import lib.utils.XpathApplier;

public class RelevantInformation {

	private String domain;
	private Xpath xpath;
	private InformationsMatching matching;
	//serve per la visualizzazione
	private String content;
	
	public RelevantInformation(String domain, Xpath xpath) {
		this.domain = domain;
		this.xpath = xpath;
	}
	
	//serve per la visualizzazione
	public RelevantInformation(String domain, Xpath xpath, WebPageDocument w) throws XPathExpressionException, IOException {
		this.domain = domain;
		this.xpath = xpath;
		XpathApplier xapplier = XpathApplier.getInstance();
		NodeList nl = xapplier.getNodes(xpath.getXpath(), 
				w.getDocument_jsoup());
		if (nl.getLength() != 0) {
			this.content=nl.item(0).getTextContent();
		}
		else	{ //l'xpath non ha restituito nessun segmento
			this.content="--";
		}
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public Xpath getXpath() {
		return this.xpath;
	}
	
	public InformationsMatching getMatching() {
		return this.matching;
	}
	
	public void setMatching(InformationsMatching matching) {
		this.matching=matching;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
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
		RelevantInformation other = (RelevantInformation) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (xpath == null) {
			if (other.xpath != null)
				return false;
		} else if (!xpath.equals(other.xpath))
			return false;
		return true;
	}

	@Override
	  public String toString() {
	    return this.domain+"_"+this.content;
	  }
	
}
