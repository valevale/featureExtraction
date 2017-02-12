package identityResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import database.MongoFacade;
import model.Source;
import model.WebPage;

public class TrovaNomiUnici {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("********INIZIO");
		String path = "testGenericXpath/persone/";
		MongoFacade facade = new MongoFacade("web_search_pages");
		//prendo un dominio
		//questo è il 4
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		System.out.println("********PRESO LA SORGENTE");
		//mappa: ancora - id delle pagine con quell'ancora
		Map<String,List<String>> ancora2pagine = new HashMap<>();
		//scorro le sue pagine
		for (int j=0;j<currentSource.getPages().size();j++) {
			if ((j+1)%100==0)
				System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
			WebPage currentPage = currentSource.getPages().get(j);
			if (facade.isValidated(currentPage)) {
				String ancora = currentPage.getQuery().getQuery();
				List<String> pagineConAncora = ancora2pagine.get(ancora);
				if (pagineConAncora == null) {
					//non è presente l'ancora nella mappa. creo
					pagineConAncora = new ArrayList<>();
				}
				pagineConAncora.add(currentPage.getId().toString());
				ancora2pagine.put(ancora, pagineConAncora);
			}
		}
		//da qui possiamo fare diverse cose
		//una distribuzione
		//in questa mappa vedo intervallo - numero di ancore con un numero di pagine che rientra
		//in quell'intervallo
		Map<String,Integer> intervalli = new HashMap<>();
		riempiMappa(intervalli);
		//scorro la vecchia mappa e aggiorno questa nuova
		Iterator<String> ancoreIt = ancora2pagine.keySet().iterator();
		List<String> pagineConAncoraUnica = new ArrayList<>();
		while (ancoreIt.hasNext()) {
			String ancoraCorrente = ancoreIt.next();
			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
			aggiornaMappa(intervalli,numeroPagineConAncora);
			if (numeroPagineConAncora == 1) {
				pagineConAncoraUnica.add(ancora2pagine.get(ancoraCorrente).get(0));
			}
		}
		System.out.println("stampo la mappa degli intervalli");
		//stampo questa mappa
		File dir = new File(path+"analisi_dataset_ancore");
		dir.mkdir();
		String currentPath = path+"analisi_dataset_ancore/";
		PrintWriter testPrinterMap = new PrintWriter(currentPath+"distribuzione_ancore_"+
				"5750678b3387e31f516fa1cd"+".csv", "UTF-8");
		Iterator<String> intervalliIt = intervalli.keySet().iterator();
		while (intervalliIt.hasNext()) {
			String intervalloCorrente = intervalliIt.next();
			testPrinterMap.println(intervalloCorrente+","+intervalli.get(intervalloCorrente));
		}
		testPrinterMap.close();
		//poi, di quelli con 1 solo nome, vado a vedere nelle altre sorgenti (tutte? volendo...)
		//la sua frequenza
		//seleziono solo quelle con 1 sola frequenza (per esempio)

	}

	public static void riempiMappa(Map<String,Integer> mappa) {
		//valori possibili (idea)
		mappa.put("A)1", 0);
		mappa.put("B)2-5", 0);
		mappa.put("C)6-10", 0);
		mappa.put("D)11-100", 0);
		mappa.put("E)101-1000", 0);
		mappa.put("F)1001-10000", 0);
		mappa.put("G)oltre 10000", 0);
	}

	private static void aggiornaMappa(Map<String,Integer> mappa, int frequenza) {
		if (frequenza ==1)
			mappa.replace("A)1", mappa.get("A)1")+1);
		if (frequenza >=2 && frequenza <=5)
			mappa.replace("B)2-5", mappa.get("B)2-5")+1);
		if (frequenza >=6 && frequenza <=10)
			mappa.replace("C)6-10", mappa.get("C)6-10")+1);
		if (frequenza >=11 && frequenza <=100)
			mappa.replace("D)11-100", mappa.get("D)11-100")+1);
		if (frequenza >=101 && frequenza <=1000)
			mappa.replace("E)101-1000", mappa.get("E)101-1000")+1);
		if (frequenza >=1001 && frequenza <=10000)
			mappa.replace("F)1001-10000", mappa.get("F)1001-10000")+1);
		if (frequenza >=10001)
			mappa.replace("G)oltre 10000", mappa.get("G)oltre 10000")+1);
	}

}
