package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import lib.utils.DocumentUtils;
import main.SourceRep;

public class CleanedPagesRepository {

	static Map<String,List<Document>> domains2cleanedPages = new HashMap<>();

	public static void clean(List<String> idSorgenti) throws Exception {
		DomRepToClean drtc = DomRepToClean.getInstance();
		for (int i=0;i<idSorgenti.size();i++) {
			List<Document> cleanedPages = new ArrayList<>();
			Source s = SourceRep.getSource(idSorgenti.get(i));
			drtc.addDomain(s);
			//prendo le prime 100 pagine
			for (int j=0;j<100;j++) {
//				if ((j+1)%10==0)
//					System.out.print("*****pagina numero: "+(j+1)+"/100");
				//per ogni pagina, applico la xpath
				WebPage currentPage = s.getPages().get(j);
				Document doc = DocumentUtils.prepareDocument(currentPage.getHtml(), idSorgenti.get(i));
				cleanedPages.add(doc);
			}
			domains2cleanedPages.put(idSorgenti.get(i), cleanedPages);
		}
	}
	
	public static List<Document> getCleanedpages(String idDom) {
		return domains2cleanedPages.get(idDom);
	}

}
