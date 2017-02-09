package identityResolution;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.NodeList;

import database.MongoFacade;
import lucene.SegmentSearcher;
import main.DomainsWrapper_pairMatching;
import model.DomainSource;
import model.DomainsRepository;
import model.Segment;
import model.Source;
import model.WebPage;
import model.WebPageDocument;
import model.Xpath;
import scala.Tuple2;
import segmentation.DocumentCleaner;
import segmentation.TopSegmentsFinder;
import xpath.utils.XpathApplier;

public class Main {

	/* in questo metodo proviamo a filtrare via un po' di omonimi
	 * -segmento le pagine
	 * -individuo i segmenti rilevanti (>threshold)
	 * -generalizzo le xpath
	 * -applico le xpath generalizzate del dominio a TUTTE le pagine del dominio
	 * -per ogni xpath ottengo dei segmenti
	 * -guardo la distribuzione di similarità di quei segmenti (ovvero quanti valori simili ho)
	 * ++se aggiungi pagine di altri domini da confrontare con la pagina di partenza,
	 * 		aggiungi semplicemente nuove xpath 
	 * 		quello che puoi rischiare è di non prendere in considerazione segmenti importanti
	 * 		(per esempio se nessuno degli altri domini ha email, ma quello di partenza sì,
	 * 		comunque non riuscirai mai a considerare quel segmento email)
	 * ++ATTENZIONE l'algoritmo corrente non filtra via alcun segmento ritenuto rilevante
	 * 		quindi se c'è viene ritenuto per 1 caso che un segmento sia rilevante, quando
	 * 		in verità non lo è, nulla permette di scartarlo.
	 * 		si potrebbero filtrare tali segmenti con un sistema di voting*/
	public static void main(String[] args) throws Exception {
		//prendo le pagine
		String path = "testGenericXpath/persone/";
		double parameterTextFusion = -1;
		Map<Tuple2<Integer,Integer>,WebPageDocument> personDomain2document = new HashMap<>();
		for(int p=1;p<=7;p++){
			for(int j=4;j<=5;j++) {
				String currentFolder = path+"p"+p+"/";
				String dPath = currentFolder+"orig"+j+".html";
				File d = new File(dPath);
				if (d.exists()) {
					WebPageDocument w = new WebPageDocument(
							new File(path+"p"+p+"/"+"orig"+j+".html"), 
							j, path+"p"+p+"/", 
							parameterTextFusion, j);
					personDomain2document.put(new Tuple2<>(p,j), w);
				}
			}
		}

		for(int k=1;k<=4;k++) {
			for (int k2=k+1;k2<=5;k2++) {
				int domain1 = k;
				int domain2 = k2;
				//TODO rimetti 5!!
				if (domain1==4 
						//						|| domain1==5 
						|| domain2==4 
						//						|| domain2==5
						) {
					for (int p1=1;p1<=7;p1++) {
						for (int p2=(p1+1);p2<=7;p2++) {

							//prendo 4 documenti
							WebPageDocument wfirstPerson_firstDomain = personDomain2document.get(
									new Tuple2<Integer,Integer>(p1,domain1));
							WebPageDocument wfirstPerson_secondDomain = personDomain2document.get(
									new Tuple2<Integer,Integer>(p1,domain2));
							WebPageDocument wsecondPerson_firstDomain = personDomain2document.get(
									new Tuple2<Integer,Integer>(p2,domain1));
							WebPageDocument wsecondPerson_secondDomain = personDomain2document.get(
									new Tuple2<Integer,Integer>(p2,domain2));

							if (wfirstPerson_firstDomain != null && wfirstPerson_secondDomain != null && wsecondPerson_firstDomain != null && wsecondPerson_secondDomain != null) {
								System.out.println("Persona1: "+p1+", Persona2: "+p2+"\nDominio1: "+domain1+", Dominio2: "+domain2);
								//TODO cambia i cammini!!
								String path_folder1 = path+"p"+p1+"/";
								String path_folder2 = path+"p"+p2+"/";
								//segmenti rilevanti
								TopSegmentsFinder finder = TopSegmentsFinder.getInstance();

								String indexPathDominio1 = path_folder1+"segmentIndex";
								File indexFolder = new File(indexPathDominio1);
								String[]entries = indexFolder.list();

								//eliminazione dell'indice
								if (entries != null) {
									for(String s: entries){
										File currentFile = new File(indexFolder.getPath(),s);
										currentFile.delete();
									}
								}

								List<Tuple2<Segment, TopDocs>> segment2hits_primaPersona =
										finder.findRelevantSegments(path_folder1,
												wfirstPerson_firstDomain, wfirstPerson_secondDomain,
												domain1, domain2);

								String indexPathDominio2 = path_folder2+"segmentIndex";
								indexFolder = new File(indexPathDominio2);
								entries = indexFolder.list();

								//eliminazione dell'indice
								if (entries != null) {
									for(String s: entries){
										File currentFile = new File(indexFolder.getPath(),s);
										currentFile.delete();
									}
								}

								List<Tuple2<Segment, TopDocs>> segment2hits_secondaPersona =
										finder.findRelevantSegments(path_folder2,
												wsecondPerson_firstDomain, wsecondPerson_secondDomain,
												domain1, domain2);

								//xpath, il quadrato
								//per farlo non vogliamo il repository delle coppie
								//vogliamo solo salvare per ogni dominio le xpath generiche (vogliamo storare anche il voto? chissà)

								//creo le liste di segmenti rilevanti
								List<Segment> relevantSegments_thirdDocument = new ArrayList<>();
								for (int i=0; i<segment2hits_secondaPersona.size(); i++) {
									Segment currentSegment = segment2hits_secondaPersona.get(i)._1();
									if (currentSegment.getRelevance() > 0) {
										relevantSegments_thirdDocument.add(currentSegment);
									}
								}

								List<Segment> relevantSegments_fourthDocument = new ArrayList<>();
								Iterator<Segment> fourthDocumentSegmentsIt = wsecondPerson_secondDomain.getSegments().iterator();
								while (fourthDocumentSegmentsIt.hasNext()) {
									Segment currentSegment = fourthDocumentSegmentsIt.next();
									if (currentSegment.getRelevance() > 0) {
										relevantSegments_fourthDocument.add(currentSegment);
									}
								}


								//TODO in un file di configurazione
								double threshold = 0.6;

								//cerco nella prima persona
								SegmentSearcher searcher = new SegmentSearcher(path_folder1+
										"segmentIndex");
								for (int j=0; j<segment2hits_primaPersona.size(); j++) {
									Segment seg = segment2hits_primaPersona.get(j)._1();
									TopDocs hits = segment2hits_primaPersona.get(j)._2();
									for(ScoreDoc scoreDoc : hits.scoreDocs) {
										if (scoreDoc.score >= threshold) {
											org.apache.lucene.document.Document lucDoc = null;
											try {
												lucDoc = searcher.getDocument(scoreDoc);
											} catch (Exception e) {
												e.printStackTrace();
											}
											Segment seg_secondDocument = wfirstPerson_secondDomain.getSegmentByXpath(lucDoc.get("segmentPath"));
											Tuple2<Xpath, Xpath> xpaths = DomainsWrapper_pairMatching.getXpaths(
													seg, seg_secondDocument,
													wsecondPerson_firstDomain, wsecondPerson_secondDomain,
													scoreDoc.score,
													relevantSegments_thirdDocument, relevantSegments_fourthDocument,
													segment2hits_secondaPersona, path_folder2+"segmentIndex");
											if (xpaths != null) {
												Xpath xp1 = xpaths._1();
												Xpath xp2 = xpaths._2();
												//una volta che hai i generici di entrambi
												//sovrascrivo l'xpath assoluto
												seg.setXPath(xp1);
												//sovrascrivo l'xpath assoluto
												seg_secondDocument.setXPath(xp2);
												//li aggiungi alla source
												seg.getDocument().getSource().addGenericXpath(xp1);
												seg_secondDocument.getDocument().getSource().addGenericXpath(xp2);
											}
										}
									}
								} //fine for
							}
						}
					}
				} //fine if

			}
		}
		//ora abbiamo le xpath
		System.out.println("XPATH RACCOLTE");
		//		for (int i=4;i<=5;i++) {
		//			DomainsRepository dmr = DomainsRepository.getInstance();
		//			DomainSource domain = dmr.getDomain(i);
		//			Set<Xpath> genXpaths = domain.getGenericXpaths();
		//			Iterator<Xpath> xpIt = genXpaths.iterator();
		//			System.out.println("ORA STAMPO");
		//			while (xpIt.hasNext()) {
		//				Xpath currentXpath = xpIt.next();
		//				System.out.println(currentXpath.getIdDomain()+" "+currentXpath.getXpath());
		//			}
		//		}


		//applichiamo le xpath ai documenti
		//PER ORA LO FACCIAMO SOLO COL DOMINIO 4 MA POI ANCHE COL 5 DEVI FARLO
		//prendo la sorgente
		//scorro i documenti
		//scorro le xpath
		//creo una mappa per ogni xpath / prendo la mappa di quella xpath
		//estraggo il valore
		//metto in una mappa il valore estratto. se già esiste (case insensitive) incremento il contatore
		DomainsRepository dmr = DomainsRepository.getInstance();
		DomainSource domain = dmr.getDomain(4);
		Set<Xpath> genXpaths = domain.getGenericXpaths();
		MongoFacade facade = new MongoFacade("web_search_pages");
		XpathApplier xapplier = XpathApplier.getInstance();
		//TODO fare anche col dominio 5
		Source currentSource = facade.getSourceWithId("5750678b3387e31f516fa1cd");
		Map<Xpath,Map<String,Integer>> xpath2value2frequency = new HashMap<>();
		for (int j=0;j<currentSource.getPages().size();j++) {
			if ((j+1)%100==0)
				System.out.println("*****pagina numero: "+(j+1)+"/"+currentSource.getPages().size());
			WebPage currentPage = currentSource.getPages().get(j);
			String cleanedHTML = Jsoup.clean(currentPage.getHtml(), Whitelist.relaxed()
					.addAttributes(":all", "class", "id"));
			Document document_jsoup = Jsoup.parse(cleanedHTML);
			clean(document_jsoup,path+"p1/",4);
			//scorro le xpath
			Iterator<Xpath> xpIt = genXpaths.iterator();
			while (xpIt.hasNext()) {
				Xpath currentXpath = xpIt.next();

				NodeList nl = xapplier.getNodes(currentXpath.getXpath(), 
						document_jsoup);

				if (nl.getLength() != 0) {
					String currentContent = nl.item(0).getTextContent();
					//prendo la mappa di questa xpath
					Map<String,Integer> values2frequencies = xpath2value2frequency.get(currentXpath);
					if (values2frequencies == null) {
						//la creo
						values2frequencies = new HashMap<>();
					}
					Integer frequency = values2frequencies.get(currentContent);
					if (frequency != null) {
						//se già contiene quel valore, incrementiamo la frequenza
						frequency = frequency+1;
					}
					else {
						//il valore non esiste, nuova ennupla con frequenza 1
						frequency = 1;
					}
					values2frequencies.put(currentContent, frequency);
					xpath2value2frequency.put(currentXpath, values2frequencies);
				}
			}
		} // ho finito di scorrere i documenti
		System.out.println("STAMPO I RISULTATI");
		//per ogni xpath faccio un documento
		//prima una cartella
		File dir = new File(path+"distribuzione_valori_dei_segmenti");
		dir.mkdir();
		//TODO per un altro dominio fanne un altro
		String currentPath = path+"distribuzione_valori_dei_segmenti/";
		Iterator<Xpath> it = xpath2value2frequency.keySet().iterator();
		int c=1;
		//questo documento tiene traccia della corrispondenza xpath_numero identificativo degli altri file csv
		PrintWriter testPrinterMap = new PrintWriter(currentPath+"mappaId_Xpath.csv", "UTF-8");

		while (it.hasNext()) {
			Xpath xpath = it.next();

			System.out.println("Xpath numero "+c);

			testPrinterMap.println(c+","+xpath.getXpath());

			//mappa per un'altra stampa: frequenze aggregate2numeroValoriConQuellaFrequenza
			Map<String,Integer> frequenza2valori = new HashMap<>();

			riempiMappa(frequenza2valori);

			//per ogni xpath creo un csv
			PrintWriter testPrinter = new PrintWriter(currentPath+"distrubuzioneValoriSegmento"+c+".csv", "UTF-8");

			//scorro e stampo ogni valore_frequenza
			Map<String,Integer> currentMap = xpath2value2frequency.get(xpath);
			Iterator<String> valuesIterator = currentMap.keySet().iterator();
			while (valuesIterator.hasNext()) {
				String currentValue = valuesIterator.next();

				testPrinter.println(currentValue+","+currentMap.get(currentValue));

				//e aggiorniamo la mappa frequenza2valori
				aggiornaMappa(frequenza2valori, currentMap.get(currentValue));
			}

			testPrinter.close();

			//ora stampiamo le frequenze aggregate
			System.out.println("Stampo frequenze di xpath numero "+c);
			PrintWriter testPrinterAggr = new PrintWriter(currentPath+"frequenzeAggregate"+c+".csv", "UTF-8");
			Iterator<String> freqIt = frequenza2valori.keySet().iterator();
			while (freqIt.hasNext()) {
				String freq = freqIt.next();
				testPrinterAggr.println(freq+","+frequenza2valori.get(freq));
			}
			testPrinterAggr.close();

			c++;
		}
		testPrinterMap.close();

		System.out.println("fine");
	}

	private static void riempiMappa(Map<String,Integer> frequenza2valori) {
		//valori possibili (idea)
		//1,2-10,11-100,101-500,501-1000,1001-5000,5001-10000,oltre 10000
		frequenza2valori.put("1", 0);
		frequenza2valori.put("2-10", 0);
		frequenza2valori.put("11-100", 0);
		frequenza2valori.put("101-1000", 0);
		frequenza2valori.put("1001-5000", 0);
		frequenza2valori.put("5001-10000", 0);
		frequenza2valori.put("oltre 10000", 0);
	}

	private static void aggiornaMappa(Map<String,Integer> frequenza2valori, int frequenza) {
		if (frequenza ==1)
			frequenza2valori.replace("1", frequenza2valori.get("1")+1);
		if (frequenza >=2 && frequenza <=10)
			frequenza2valori.replace("2-10", frequenza2valori.get("2-10")+1);
		if (frequenza >=11 && frequenza <=100)
			frequenza2valori.replace("11-100", frequenza2valori.get("11-100")+1);
		if (frequenza >=101 && frequenza <=1000)
			frequenza2valori.replace("101-1000", frequenza2valori.get("101-1000")+1);
		if (frequenza >=1001 && frequenza <=5000)
			frequenza2valori.replace("1001-5000", frequenza2valori.get("1001-5000")+1);
		if (frequenza >=5001 && frequenza <=10000)
			frequenza2valori.replace("5001-10000", frequenza2valori.get("5001-10000")+1);
		if (frequenza >=10001)
			frequenza2valori.replace("oltre 10000", frequenza2valori.get("oltre 10000")+1);
	}

	private static void clean(Document doc, String cartella, int par) throws Exception {
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
		DocumentCleaner docCleaner = DocumentCleaner.getInstance();
		docCleaner.clean(doc, usedPagesForCleaning);
	}
}
