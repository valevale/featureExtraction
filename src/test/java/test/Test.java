package test;

import java.util.List;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.gazetteer.GeoName;

import nlp.ClavinFacade;

public class Test {
	
	
	public static void main(String[] args) throws ClavinException {
		ClavinFacade cf = ClavinFacade.getInstance();
		
		String prova = "Milano, Torino";
		
		List<GeoName> geonames = cf.resolveLocation(prova);
		
		geonames.forEach(geoname -> {
			System.out.println(geoname.toString());
		});
		
		System.out.println("done.");
	}


}
