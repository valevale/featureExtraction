package model;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.NodeList;

import xpath.utils.XpathApplier;
import xpath.utils.XpathExtractor;

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

	/*page: la pagina
	 * path e par: parametri per pulire la pagina (andranno sostituiti con un processo di pulizia del db) TODO*/
	public List<String> getContentInformation(WebPage page, String path, int par) throws Exception {
		String cleanedHTML = Jsoup.clean(page.getHtml(), Whitelist.relaxed()
				.addAttributes(":all", "class", "id"));
		Document document_jsoup = Jsoup.parse(cleanedHTML);
		System.out.println(document_jsoup);
		System.out.println("___________________________________******_____________________________");
		clean(document_jsoup,path+"p1/",par);
		System.out.println(document_jsoup);
		List<String> contentInformations = new ArrayList<>();
		for (int i=0;i<this.profileInformations.size();i++) {
			RelevantInformation info = this.profileInformations.get(i);
			System.out.println(info.getXpath().getXpath());
			NodeList nl = xapplier.getNodes(info.getXpath().getXpath(), 
					document_jsoup);
			String currentContent;
			if (nl.getLength() != 0) {
				System.out.println("YEEEE");
				currentContent = nl.item(0).getTextContent();
			}
			else	{ //l'xpath non ha restituito nessun segmento
				System.out.println("nuuuuuu :<");
				currentContent = "--";
			}
			contentInformations.add(currentContent);
		}
		return contentInformations;
	}
	
	private void clean(Document doc, String cartella, int par) throws Exception {
		List<Document> usedPagesForCleaning = new ArrayList<>();

//		System.out.println("Creazione lista di pagine da utilizzare per la pulizia del template");
		for (int i=1; i<=5;i++) {
			try {
				usedPagesForCleaning.add(Jsoup.parse(IOUtils.toString(
						new FileReader(new File(cartella+"pag"+par+"_"+i+".html")))));
			}
			catch (Exception e) {
				System.out.println("Errore pagina "+i + ": " + e);
			}
		}
		XpathExtractor xpextractor = XpathExtractor.getInstance();
		xpextractor.clean(doc, usedPagesForCleaning);
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
