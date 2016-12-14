package main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.search.TopDocs;
import org.jsoup.nodes.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lib.utils.DocumentUtils;
import model.Segment;
import model.WebPageDocument;
import scala.Tuple2;
import segmentation.RelevantSegmentsFinder;
import test.ConfigurationTestCosSimThreshold;
import xpath.utils.XpathApplier;

public class Main {

	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/testGenericXpath/";
	static int n1 = 1;
	static int n2 = 2;
	static int n3 = 3;
	static int n4 = 4;
	static int parN1 = 1;
	static int parN2 = 2;
	static double parameterTextFusion = -1;

	public static void main(String[] args) throws Exception {

		String file = IOUtils.toString(new FileReader(new File(path + "webpages.txt")));

		String[] folders = file.split("\n");

		for (int k=0; k<folders.length;k++) {
			String cartella = folders[k];
			
			System.out.println("CARTELLA CORRENTE: "+cartella);

			String d1Path = cartella+"orig"+n1+".html";
			String d2Path = cartella+"orig"+n2+".html";

			String d3Path = cartella+"orig"+n3+".html";
			String d4Path = cartella+"orig"+n4+".html";


			File d1 = new File(d1Path);
			File d2 = new File(d2Path);

			File d3 = new File(d3Path);
			File d4 = new File(d4Path);

			if (d1.exists() && d2.exists() && d3.exists() && d4.exists())
				System.out.println("Trovati documenti");

			String indexPath = cartella+"segmentIndex";

			File indexFolder = new File(indexPath);
			String[]entries = indexFolder.list();

			//eliminazione dell'indice
			if (entries != null) {
				System.out.println("deleting previous index");
				for(String s: entries){
					File currentFile = new File(indexFolder.getPath(),s);
					currentFile.delete();
				}
			}

			//passo 1: prendere la pagina da segmentare

			//TODO costrutto al momento necessario, col db sarà diverso
			WebPageDocument firstDocument = new WebPageDocument(d1, parN1, cartella, parameterTextFusion);

			//TODO costrutto al momento necessario, col db sarà diverso
			WebPageDocument secondDocument = new WebPageDocument(d2, parN2, cartella, parameterTextFusion);

			List<Tuple2<Segment, TopDocs>> segment2hits =
					RelevantSegmentsFinder.findRelevantSegments(cartella, firstDocument, secondDocument,
							n1, n2);

			//TODO vedi se puoi fare meglio l'architettura
			RelevantSegmentsFinder.setRelevances(segment2hits);

			//stesso procedimento, con un'altra coppia di pagine degli stessi domini
			//eliminazione dell'indice
//			if (entries != null) {
//				System.out.println("deleting previous index");
//				for(String s: entries){
//					File currentFile = new File(indexFolder.getPath(),s);
//					currentFile.delete();
//				}
//			}

			WebPageDocument thirdDocument = new WebPageDocument(d3, parN1, cartella, parameterTextFusion);

			WebPageDocument fourthDocument = new WebPageDocument(d4, parN2, cartella, parameterTextFusion);

			List<Tuple2<Segment, TopDocs>> segment2hits_secondCouple =
					RelevantSegmentsFinder.findRelevantSegments(cartella, thirdDocument, fourthDocument,
							n3, n4);

			//TODO vedi se puoi fare meglio l'architettura
			RelevantSegmentsFinder.setRelevances(segment2hits_secondCouple);


			//creo, per efficienza, una lista di soli segmenti rilevanti, per il primo documento della prima
			//coppia e della seconda coppia
			List<Segment> relevantSegments_firstDocument = new ArrayList<>();

			for (int i=0; i<segment2hits.size(); i++) {
				Segment currentSegment = segment2hits.get(i)._1();
				if (currentSegment.getRelevance() > 0) {
					relevantSegments_firstDocument.add(currentSegment);
				}
			}

			List<Segment> relevantSegments_thirdDocument = new ArrayList<>();

			for (int i=0; i<segment2hits_secondCouple.size(); i++) {
				Segment currentSegment = segment2hits_secondCouple.get(i)._1();
				if (currentSegment.getRelevance() > 0) {
					relevantSegments_thirdDocument.add(currentSegment);
				}
			}

			//applico una versione per una alla pagina 3 (dalla più specifica)
			//quando una versione restituisce 1 e 1 solo segmento rilevante, mi fermo e accetto
			//quell'xpath per quel segmento
			Document doc3 = thirdDocument.getDocument();

			List<String> genericXpaths = new ArrayList<>();

			for (int i=0; i<relevantSegments_firstDocument.size(); i++) {
				Segment currentRelevantSegment = relevantSegments_firstDocument.get(i);
				//ogni segmento rilevante ha diritto all'oggetto Xpath associato
//				System.out.println("Creating xpath versions for segment "+(i+1)+" of "+relevantSegments_firstDocument.size());
				currentRelevantSegment.makeXpathVersions();
				int specificityParameter = 0;
				boolean onlyOneSegmentFound = false;
				while(specificityParameter <= 5 && !onlyOneSegmentFound) {
//					System.out.println("Parametro specificità "+specificityParameter);
					String currentXpath = currentRelevantSegment
							.getXpathVersions().getPathBySpecificity(specificityParameter);
					//se corrisponde a 1 unico segmento RILEVANTE
					if (isARelevantSegment(currentXpath, doc3, relevantSegments_thirdDocument)) {
						onlyOneSegmentFound = true;
						//sovrascrivo l'xpath assoluto
						currentRelevantSegment.setXPath(currentXpath, specificityParameter);
						System.out.println("YEEE aggiungo");
						if (specificityParameter > 0) System.out.println("SUPER SUPER!! " + specificityParameter);
						genericXpaths.add(currentXpath);
					}
					else
						specificityParameter++;
				}
				
				if (genericXpaths.size()==0) System.out.println("Non ho aggiunto nulla :(");

				//metto gli xpath nella pagina
				firstDocument.setGenericXPaths(genericXpaths);

				//TODO metto tutti i segmenti nel dominio!!!

				//poi coloriamo la prima pagina con i matching rilevanti
				//memorizziamo anche il tipo di specificità? perché no

				XpathApplier xapplier = XpathApplier.getInstance();
				org.w3c.dom.Document w3cFirstDocument = xapplier
						.color(new HashSet<>(genericXpaths), firstDocument.getDocument());
				if (w3cFirstDocument != null) {
					PrintWriter testPrinter = new PrintWriter(cartella+"GenericSegments.html", "UTF-8");
					testPrinter.println(DocumentUtils.getStringFromDocument(w3cFirstDocument));
					testPrinter.close();
				}
				
				org.w3c.dom.Document firstDocumentWithRelevance = xapplier
						.colorRelevance(firstDocument.getSegments(), firstDocument.getDocument());
				if (firstDocumentWithRelevance != null) {
					PrintWriter testPrinter = new PrintWriter(cartella+"Relevance1.html", "UTF-8");
					testPrinter.println(DocumentUtils.getStringFromDocument(firstDocumentWithRelevance));
					testPrinter.close();
				}
				
				org.w3c.dom.Document thirdDocumentWithRelevance = xapplier
						.colorRelevance(thirdDocument.getSegments(), thirdDocument.getDocument());
				if (thirdDocumentWithRelevance != null) {
					PrintWriter testPrinter = new PrintWriter(cartella+"Relevance3.html", "UTF-8");
					testPrinter.println(DocumentUtils.getStringFromDocument(thirdDocumentWithRelevance));
					testPrinter.close();
				}
			}//fine if d1 & d2 exists
		} //fine folder
	} //fine main

	private static boolean isARelevantSegment(String xpath, Document document, List<Segment> relevantSegments) throws XPathExpressionException, IOException, ParserConfigurationException {
//		System.out.println(xpath);
		if (xpath.equals("")) 
			return false;
		XpathApplier xapplier = XpathApplier.getInstance();
		NodeList xpathNodes = xapplier.getNodes(xpath, document);
//		System.out.println("nodi matchati "+ xpathNodes.getLength());
		if (xpathNodes.getLength()!=1)
			return false;
		Node xpathNode = xpathNodes.item(0);
		//controlliamo che il nodo restituito corrisponda a un segmento rilevante del documento 3
		for (int i=0;i<relevantSegments.size();i++) {
			Segment relevantSegment = relevantSegments.get(i);
			if (xpathNode.isEqualNode(relevantSegment.getW3cNodes().item(0)))
				return true;
		}
		//nessun matching
		return false;
	}
}

