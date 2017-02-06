package main_backup;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;

import model.RelevantInformation;
import model.WebPageDocument;
import scala.Tuple2;
import xpath.utils.XpathApplier;

public class Main {

	static double parameterTextFusion = -1;
	static String path = "testGenericXpath/persone/";

	public static void main(String[] args) throws Exception {

		String d1Folder = path+"p1/";
		
		//per una questione di efficienza, memorizzo qui i web page documenti
		Map<Integer,WebPageDocument> domain2document = new HashMap<>();

		for(int j=1;j<=5;j++) {
			String currentFolder = d1Folder;
			String dPath = currentFolder+"orig"+j+".html";
			File d = new File(dPath);
			if (d.exists()) {
				WebPageDocument w = new WebPageDocument(
						new File(path+"p1/"+"orig"+j+".html"), 
						j, path+"p1/", 
						parameterTextFusion, j);
				domain2document.put(j, w);
			}
		}


		Map<Integer,List<Tuple2<Integer,List<RelevantInformation>>>> informations = 
				SegmentGraphGenerator.getInformations();

		XpathApplier xapplier = XpathApplier.getInstance();

		printThisList(informations.get(1),xapplier, "Weight", domain2document);
//		printThisList(informations.get(2),xapplier, "Length", domain2document);
//		printThisList(informations.get(3),xapplier, "MinCut", domain2document);
	}

	//	public static List<Integer> ordina(List<Integer> list) {
	//
	//		for (int i=0; i<list.size(); i++) {
	//			int valore = list.get(i);
	//			int j = i-1;
	//			while (j>=0 && list.get(j) > valore) {
	//				list.set(j+1, list.get(j));
	//				j--;
	//				list.set(j+1, valore);
	//			}
	//
	//		}
	//		return list;
	//	}

	public static void printThisList(List<Tuple2<Integer,List<RelevantInformation>>> list, XpathApplier xapplier, String type,
			Map<Integer,WebPageDocument> domain2document) throws XPathExpressionException, IOException {
		
		PrintWriter testPrinter = new PrintWriter(path+"schedaPersonaPROVA"+type+".csv", "UTF-8");

		testPrinter.println("Path numero;Dominio 1;Contenuto 1;Dominio 2;Contenuto 2;Dominio 3;Contenuto 3;Dominio 4;Contenuto 4;Dominio 5;Contenuto 5");

		for (int i=0; i<list.size(); i++) {
//			System.out.println("NUOVO PATH");
//			System.out.println("la lista era grande così: "+list.get(i)._2().size());
			List<RelevantInformation> currentPathOrdered = order(list.get(i)._2());
//			System.out.println("la lista è grande così: "+currentPathOrdered.size());
//			System.out.println(currentPathOrdered);
			testPrinter.print(list.get(i)._1()+";");
//			System.out.println(currentPathOrdered);
			for (int j=0; j<currentPathOrdered.size(); j++) {
				//prendo l'i-esimo elemento
				RelevantInformation currentSegment = currentPathOrdered.get(j);
				if (currentSegment != null) {
					//se il dominio è pari a quello corrente del csv stampi
					//					System.out.println("dominio "+currentSegment.getDomain());
					//					System.out.println("d "+d);
					//					System.out.println("j "+j);
					//				if (currentSegment.getDomain() == d) {
					//e stampi tutto
//					System.out.println(currentSegment.getDomain());
					//**numero path
					WebPageDocument w = domain2document.get(j+1);
					//**xpath
					testPrinter.print(currentSegment.getXpath().getXpath()+";");
					//**contenuto del segmento identificato applicando xpath al documento
					NodeList nl = xapplier.getNodes(currentSegment.getXpath().getXpath(), 
							w.getDocument_jsoup());
					if (nl.getLength() != 0) {
						testPrinter.print(nl.item(0).getTextContent().replaceAll(";", "")
								.replaceAll("\n", "")+";");
					}
					else	{ //l'xpath non ha restituito nessun segmento
						testPrinter.print("--;");
					}
					//avanzi con entrambi i contatori
					//					j++;
					//					d++;
				}
				//se il dominio non è pari a quello corrente del csv (il dominio è 5 e csv sta a 3, per esempio)
				else {
					//nel csv stampi campi vuoti
					testPrinter.print("XX;");
					testPrinter.print("XX;");
					//avanzi col dominio del csv, ma non con il contatore della lista
					//					d++;
				}
				//			}
				//			else {
				//				testPrinter.print("XX;");
				//				testPrinter.print("XX;");
				//				j++;
				//			}
			}
			testPrinter.println();
		}
		testPrinter.close();
	}
	
	
	public static List<RelevantInformation> order(List<RelevantInformation> list) {

		RelevantInformation[] nodesArray = new RelevantInformation[5];

		for (int i=0; i<list.size(); i++) {
			RelevantInformation currentNode = list.get(i);
			int spotForNode = currentNode.getDomain() -1;
			nodesArray[spotForNode] = currentNode;
		}

		return Arrays.asList(nodesArray);
	}

	//	public static List<SegmentGraphNode> order(List<SegmentGraphNode> list) {
	//
	//		for (int i=0; i<list.size(); i++) {
	//			SegmentGraphNode nodo = list.get(i);
	//			int valore = list.get(i).getDomain();
	//			int j = i-1;
	//			while (j>=0 && list.get(j).getDomain() > valore) {
	//				list.set(j+1, list.get(j));
	//				j--;
	//				list.set(j+1, nodo);
	//			}
	//
	//		}
	//		return list;
	//	}
}
