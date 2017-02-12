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
import java.util.SortedSet;
import java.util.TreeSet;

import database.MongoFacade;
import model.Source;
import model.WebPage;
import scala.Tuple2;

public class TrovaNomiUnici {

	static List<Tuple2<String,Map<String,Integer>>> listaHost2Intervalli;
	static MongoFacade facade;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		listaHost2Intervalli = new ArrayList<>();
		System.out.println("********INIZIO");
		String path = "testGenericXpath/persone/";
		facade = new MongoFacade("web_search_pages");
		//prendo un dominio
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
		calcolaIntervalli(currentSource);
		facade.getSourceWithId("5750678b3387e31f516fa1d0");
		calcolaIntervalli(currentSource);
		facade.getSourceWithId("575067b33387e31f516face0");
		calcolaIntervalli(currentSource);
		facade.getSourceWithId("5750678b3387e31f516fa1cd");
		calcolaIntervalli(currentSource);
		facade.getSourceWithId("5750678a3387e31f516fa185");
		calcolaIntervalli(currentSource);
		System.out.println("stampo la mappa degli intervalli");
		//stampo questa mappa
		//prima la ordino
		File dir = new File(path+"analisi_dataset_ancore");
		dir.mkdir();
		String currentPath = path+"analisi_dataset_ancore/";
		PrintWriter testPrinterMap = new PrintWriter(currentPath+"distribuzione_ancore_"+
				"5750678b3387e31f516fa1cd"+".txt", "UTF-8");

		for (int i=0; i<listaHost2Intervalli.size(); i++) {
			testPrinterMap.println(listaHost2Intervalli.get(i)._1());
			Map<String,Integer> intervalli = listaHost2Intervalli.get(i)._2();
			testPrinterMap.println();
			SortedSet<String> keys = new TreeSet<String>(intervalli.keySet());
			Iterator<String> intervalliIt = keys.iterator();
			while (intervalliIt.hasNext()) {
				String intervalloCorrente = intervalliIt.next();
				testPrinterMap.println(intervalloCorrente+","+intervalli.get(intervalloCorrente));
			}
			testPrinterMap.println();
		}
		testPrinterMap.close();
		//poi, di quelli con 1 solo nome, vado a vedere nelle altre sorgenti (tutte? volendo...)
		//la sua frequenza
		//seleziono solo quelle con 1 sola frequenza (per esempio)

	}

	public static void calcolaIntervalli(Source currentSource) {
		
		System.out.println("********PRESO LA SORGENTE");
		//mappa: ancora - id delle pagine con quell'ancora
		Map<String,List<String>> ancora2pagine = new HashMap<>();
		//scorro le sue pagine
		for (int j=0;j<currentSource.getPages().size();j++) {
			if ((j+1)%100==0)
				System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
			WebPage currentPage = currentSource.getPages().get(j);
			//			if (facade.isValidated(currentPage)) {
			String ancora = currentPage.getQuery().getQuery();
			List<String> pagineConAncora = ancora2pagine.get(ancora);
			if (pagineConAncora == null) {
				//non è presente l'ancora nella mappa. creo
				pagineConAncora = new ArrayList<>();
			}
			pagineConAncora.add(currentPage.getId().toString());
			ancora2pagine.put(ancora, pagineConAncora);
			//			}
		}
		//da qui possiamo fare diverse cose
		//una distribuzione
		//in questa mappa vedo intervallo - numero di ancore con un numero di pagine che rientra
		//in quell'intervallo
		Map<String,Integer> intervalli = new HashMap<>();
		riempiMappa(intervalli);
		//scorro la vecchia mappa e aggiorno questa nuova
		Iterator<String> ancoreIt = ancora2pagine.keySet().iterator();
		//		List<String> pagineConAncoraUnica = new ArrayList<>();
		while (ancoreIt.hasNext()) {
			String ancoraCorrente = ancoreIt.next();
			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
			aggiornaMappa(intervalli,numeroPagineConAncora);
			//			if (numeroPagineConAncora == 1) {
			//				pagineConAncoraUnica.add(ancora2pagine.get(ancoraCorrente).get(0));
			//			}
		}
		listaHost2Intervalli.add(new Tuple2<>(currentSource.getHost(),intervalli));
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
