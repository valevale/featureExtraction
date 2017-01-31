package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.NodeList;

import xpath.utils.XpathApplier;

public class Profile {

	private int idDomain;
	private String idDbDomain;
	private List<RelevantInformation> profileInformations;
	XpathApplier xapplier = XpathApplier.getInstance();

	public Profile(int idDomain) {
		this.idDomain=idDomain;
		this.profileInformations = new ArrayList<>();
		//MAPPA ID
		//icittà -> 1_5750678b3387e31f516fa1c7
		//cylex -> 2_5750678b3387e31f516fa1d0
		//inelenco -> 3_575067b33387e31f516face0
		//misterimprese -> 4_5750678b3387e31f516fa1cd
		//paginebianche -> 5_5750678a3387e31f516fa185
		if (idDomain == 1)
			this.idDbDomain="5750678b3387e31f516fa1c7";
		else if (idDomain == 2)
			this.idDbDomain="5750678b3387e31f516fa1d0";
		else if (idDomain == 3)
			this.idDbDomain="575067b33387e31f516face0";
		else if (idDomain == 4)
			this.idDbDomain="5750678b3387e31f516fa1cd";
		else if (idDomain == 5)
			this.idDbDomain="5750678a3387e31f516fa185";
	}

	public int getIdDomain() {
		return this.idDomain;
	}

	public String getIdDbDomain() {
		return this.idDbDomain;
	}

	public List<RelevantInformation> getProfileInformations() {
		return this.profileInformations;
	}

	//TODO anche identificativo
	public void addInformation(RelevantInformation info) {
		this.profileInformations.add(info);
	}

	public List<String> getContentInformation(WebPage page) throws XPathExpressionException, IOException {
		String cleanedHTML = Jsoup.clean(page.getHtml(), Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document_jsoup = Jsoup.parse(cleanedHTML);
		List<String> contentInformations = new ArrayList<>();
		for (int i=0;i<this.profileInformations.size();i++) {
			RelevantInformation info = this.profileInformations.get(i);
			NodeList nl = xapplier.getNodes(info.getXpath().getXpath(), 
					document_jsoup);
			String currentContent;
			if (nl.getLength() != 0) {
				currentContent = nl.item(0).getTextContent();
			}
			else	{ //l'xpath non ha restituito nessun segmento
				currentContent = "--";
			}
			contentInformations.add(currentContent);
		}
		//TODO identificativo del path!!
		return contentInformations;
	}
	
	/*mi fornisce i codici dei path*/
	public List<String> getMatchingInformation() {
		List<String> matchingInformations = new ArrayList<>();
		for (int i=0;i<this.profileInformations.size();i++) {
			RelevantInformation info = this.profileInformations.get(i);
			matchingInformations.add(info.getMatching().getIdPath());
		}
		return matchingInformations;
	}
}
