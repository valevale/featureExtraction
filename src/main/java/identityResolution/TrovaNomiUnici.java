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
import lib.utils.MapUtils;
import model.Source;
import model.WebPage;
import scala.Tuple2;

public class TrovaNomiUnici {

	static List<Tuple2<String,Map<String,Integer>>> listaHost2Intervalli;
	static MongoFacade facade;
	//	static Set<Tuple2<String,String>> ancora2paginaConAncoraUnicaNellaSorgente;
	static Map<Tuple2<String,String>,List<String>> sorgente_id_url2listaAncoreUniche;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		listaHost2Intervalli = new ArrayList<>();
		//		ancora2paginaConAncoraUnicaNellaSorgente = new HashSet<>();
		sorgente_id_url2listaAncoreUniche = new HashMap<>();
		System.out.println("********INIZIO");
		String path = "testGenericXpath/persone/";
		facade = new MongoFacade("web_search_pages");
		//prendo un dominio
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1c7");
		calcolaIntervalli(currentSource);
		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1d0");
		calcolaIntervalli(currentSource);
		currentSource = facade.getSourceWithId("575067b33387e31f516face0");
		calcolaIntervalli(currentSource);
		currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		calcolaIntervalli(currentSource);
		currentSource = facade.getSourceWithId("5750678a3387e31f516fa185");
		calcolaIntervalli(currentSource);
		System.out.println("stampo la mappa degli intervalli");
		Iterator<Source> sourceIt = facade.sourceIterator();
		int c=1;
		while (sourceIt.hasNext() && c<=40) {
			System.out.println("----------------------"+c+"-----------------------");
			currentSource = sourceIt.next();
			if (currentSource.getPages().size() >= 1500) {
				calcolaIntervalli(currentSource);
				c++;
			}
		}
		//stampo questa mappa
		//prima la ordino
		File dir = new File(path+"analisi_dataset_ancore");
		dir.mkdir();
		String currentPath = path+"analisi_dataset_ancore/";
		PrintWriter testPrinterMap = new PrintWriter(currentPath+"distribuzione_ancore_.txt", "UTF-8");

		for (int i=0; i<listaHost2Intervalli.size(); i++) {
			testPrinterMap.println(listaHost2Intervalli.get(i)._1());
			Map<String,Integer> intervalli = listaHost2Intervalli.get(i)._2();
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
		//		Iterator<Tuple2<String,String>> paginaConUnicaAncoraIt = ancora2paginaConAncoraUnicaNellaSorgente.iterator();
		//		//scorro queste pagine.
		//		while (paginaConUnicaAncoraIt.hasNext()) {
		//			Tuple2<String,String> tuplaCorrente = paginaConUnicaAncoraIt.next();
		//			String ancora = tuplaCorrente._1();
		//			String idPagina = tuplaCorrente._2();
		//			//per ogni ancora unica noi vogliamo 
		//		}
		//ALLORA, PER OGNI SORGENTE SAI QUALI SONO QUELLE ANCORE UNICHE, HAI UNA LISTAPER SORGENTE
		//PER OGNI LISTA, VEDI OGNI ANCORA IN QUANTE ALTRE LISTE SI TROVA
		//devi scorrere i domini
		//per ogni pagina di dominio, vedi la sua ancora.
		//se è l'unica pagina con quell'ancora
		//insomma per ogni soorgente hai la lista delle pag
		//se si trova nelle mappe dei nomi unici,
		//aggiungi lì un +1
		Map<String,Integer> ancoraUnica2numeroDominiInCuiEUnica = new HashMap<>();
		Map<String,List<String>> ancoraUnica2urlDominiInCuiEUnica = new HashMap<>();
		//per riempire questa mappa scorro tutte le liste (una lista per dominio visitato)
		//di ancora unica
		//per ogni ancora vedo nelle altre liste se esiste quell'ancora
		//per ogni altra lista in cui la trovo, aggiungo +1 per il conteggio dei domini
		//in cui l'ancora è unica
		//notare che questo conteggio parte sempre da 1, perché l'ancora è unica almeno
		//nella lista di partenza
		//continuando a scorrere le liste, se trovo un'ancora già presente nella mappa, la ignoro
		//l'ho già analizzata

		//scorro le liste
		Iterator<Tuple2<String,String>> sorgentiConAncoraUnicaIt = sorgente_id_url2listaAncoreUniche
				.keySet().iterator();
		while(sorgentiConAncoraUnicaIt.hasNext()) {
			Tuple2<String,String> sorgenteConAncoraUnica = sorgentiConAncoraUnicaIt.next();
			List<String> ancoreUnicheDellaSorgenteCorrente = sorgente_id_url2listaAncoreUniche.get(
					sorgenteConAncoraUnica);
			//scorro la lista corrente
			for (int i=0; i<ancoreUnicheDellaSorgenteCorrente.size(); i++) {
				String ancoraUnicaCorrente = ancoreUnicheDellaSorgenteCorrente.get(i);
				//controllo che l'ancora non sia stata già analizzata
				if (!ancoraUnica2numeroDominiInCuiEUnica.containsKey(ancoraUnicaCorrente)) {
					//per ogni ancora vado a cercare nelle altre sorgente (gli altri elementi della lista)
					//se è presente
					//quindi, scorro le altre sorgenti
					//					int domini = calcolaNumeroDiDominiInCuiEPresenteAncora(ancoraUnicaCorrente,
					//							sorgente2listaAncoreUniche);
					List<String> listaDomini = calcolaDominiInCuiEPresenteAncora(ancoraUnicaCorrente,
							sorgente_id_url2listaAncoreUniche);
					ancoraUnica2numeroDominiInCuiEUnica.put(ancoraUnicaCorrente, listaDomini.size());
					ancoraUnica2urlDominiInCuiEUnica.put(ancoraUnicaCorrente, listaDomini);
				}
			}
		}

		PrintWriter statPrinter = new PrintWriter(
				currentPath+"statistiche_ancore_uniche.txt", "UTF-8");
		PrintWriter statPrinterDet = new PrintWriter(
				currentPath+"statistiche_ancore_uniche_DETTAGLI.txt", "UTF-8");
		PrintWriter statPrinterDet2 = new PrintWriter(
				currentPath+"statistiche_ancore_uniche_solo_domini_interessanti.txt", "UTF-8");

		statPrinter.println("NUMERO DI ANCORE UNICHE: "+ancoraUnica2numeroDominiInCuiEUnica.size());
		statPrinter.println();

		statPrinterDet.println("NUMERO DI ANCORE UNICHE: "+ancoraUnica2numeroDominiInCuiEUnica.size());
		statPrinterDet.println();

		statPrinterDet2.println("NUMERO DI ANCORE UNICHE: "+ancoraUnica2numeroDominiInCuiEUnica.size());
		statPrinterDet2.println();

		//ordina la mappa per valore
		Map<String,Integer> ancora2domini_sorted = 
				MapUtils.sortByValue(ancoraUnica2numeroDominiInCuiEUnica);

		Iterator<String> ancoreIt = ancora2domini_sorted.keySet().iterator();
		while(ancoreIt.hasNext()) {
			String ancoraCorrente = ancoreIt.next();
			statPrinter.println("\""+ancoraCorrente+"\" è un nome unico in "+
					ancora2domini_sorted.get(ancoraCorrente)+" domini.");

			statPrinterDet.println("\""+ancoraCorrente+"\" è un nome unico in "+
					ancora2domini_sorted.get(ancoraCorrente)+" domini.");

			statPrinterDet2.println("\""+ancoraCorrente+"\" è un nome unico in "+
					ancora2domini_sorted.get(ancoraCorrente)+" domini.");
			//fai un altro file di analisi in cui mostri anche gli url dei domini
			//è uguale, solo con questo dettaglio in più
			//dettagli
			List<String> listaDomini = ancoraUnica2urlDominiInCuiEUnica.get(ancoraCorrente);
			for (int i=0;i<listaDomini.size();i++) {
				statPrinterDet.println(listaDomini.get(i));
				if (listaDomini.get(i).equals("www.paginebianche.it")
						|| listaDomini.get(i).equals("www.misterimprese.it")
						|| listaDomini.get(i).equals("www.cylex.it")
						|| listaDomini.get(i).equals("www.inelenco.com")
						|| listaDomini.get(i).equals("www.icitta.it")
						) {
					statPrinterDet2.print(listaDomini.get(i)+"_");
				}
			}
			statPrinterDet.println();
			statPrinterDet2.println();
		}
		statPrinterDet2.close();
		statPrinterDet.close();
		statPrinter.close();
	}

	//dovrebbe restituire una lista di dimensione almeno pari a 1
	public static List<String> calcolaDominiInCuiEPresenteAncora(String ancora, 
			Map<Tuple2<String,String>,List<String>> sorgente2listaAncoreUniche) {
		List<String> domini = new ArrayList<>();
		Iterator<Tuple2<String,String>> sorgentiConAncoraUnicaIt = sorgente2listaAncoreUniche
				.keySet().iterator();
		while(sorgentiConAncoraUnicaIt.hasNext()) {
			Tuple2<String,String> sorgenteConAncoraUnica = sorgentiConAncoraUnicaIt.next();
			List<String> ancoreUnicheDellaSorgenteCorrente = sorgente2listaAncoreUniche.get(
					sorgenteConAncoraUnica);
			//per ogni lista cerco l'ancora
			if (ancoreUnicheDellaSorgenteCorrente.contains(ancora)) {
				domini.add(sorgenteConAncoraUnica._2());
			}
		}
		return domini;
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
		List<String> ancoreUnicheDellaSorgente = new ArrayList<>();
		while (ancoreIt.hasNext()) {
			String ancoraCorrente = ancoreIt.next();
			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
			aggiornaMappa(intervalli,numeroPagineConAncora);
			if (numeroPagineConAncora == 1) {
				//				ancora2paginaConAncoraUnicaNellaSorgente.add(
				//						new Tuple2<>(ancoraCorrente,ancora2pagine.get(ancoraCorrente).get(0)));
				ancoreUnicheDellaSorgente.add(ancoraCorrente);
			}
		}
		listaHost2Intervalli.add(new Tuple2<>(currentSource.getHost(),intervalli));
		sorgente_id_url2listaAncoreUniche.put(
				new Tuple2<>(currentSource.getId().toString(),currentSource.getHost()),
				ancoreUnicheDellaSorgente);
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
