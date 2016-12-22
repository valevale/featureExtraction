package main;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NodeList;

import lib.utils.DocumentUtils;
import model.DomainSource;
import model.DomainsRepository;
import model.Matching;
import model.MatchingRepository;
import model.WebPageDocument;
import model.Xpath;
import xpath.utils.XpathApplier;


public class Main {
	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/testGenericXpath/persone/";
	static int n1 = 1;
	static int n2 = 2;
	static int n3 = 3;
	static int n4 = 4;
	static int parN1 = 1;
	static int parN2 = 2;
	static double parameterTextFusion = -1;

	public static void main(String[] args) throws Exception {

		//		String file = IOUtils.toString(new FileReader(new File(path + "webpages.txt")));
		//
		//		String[] folders = file.split("\n");

		//		for (int k=1; k<=2;k++) {
		//			String cartellaDominio1 = folders[k];
		//			for (int j=1; j<folders.length; j++) {
		for(int k=1;k<=4;k++) {
			int domain1 = k;
			int domain2 = k+1;

			for (int i=1;i<=7;i++) {
				for (int j=(i+1);j<=7;j++) {


					System.out.println("***"+i+" "+j);

					String d1Folder = path+"p"+i+"/";
					String d2Folder = path+"p"+i+"/";
					String d3Folder = path+"p"+j+"/";
					String d4Folder = path+"p"+j+"/";

					String d1Path = d1Folder+"orig"+domain1+".html";
					String d2Path = d2Folder+"orig"+domain2+".html";

					String d3Path = d3Folder+"orig"+domain1+".html";
					String d4Path = d4Folder+"orig"+domain2+".html";


					System.out.println("d1: "+d1Path);
					System.out.println("d2: "+d2Path);
					System.out.println("d3: "+d3Path);
					System.out.println("d4: "+d4Path);


					File d1 = new File(d1Path);
					File d2 = new File(d2Path);

					File d3 = new File(d3Path);
					File d4 = new File(d4Path);

					if (d1.exists() && d2.exists() && d3.exists() && d4.exists()) {
						System.out.println("Trovati documenti");

						WebPageDocument firstDocument = new WebPageDocument(d1, domain1, d1Folder, 
								parameterTextFusion, domain1);
						WebPageDocument secondDocument = new WebPageDocument(d2, domain2, d2Folder, 
								parameterTextFusion, domain2);
						WebPageDocument thirdDocument = new WebPageDocument(d3, domain1, d3Folder, 
								parameterTextFusion, domain1);
						WebPageDocument fourthDocument = new WebPageDocument(d4, domain2, d4Folder, 
								parameterTextFusion, domain2);

						DomainsWrapper_new.getSegmentsFrom(firstDocument, secondDocument, 
								thirdDocument, fourthDocument, d1Folder, d3Folder, domain1, domain2, domain1, domain2);
					}
				}
			}
		}
		//ora vediamo i risultati
		//		DomainsRepository dmrp = DomainsRepository.getInstance();
		//		DomainSource d1 = dmrp.getDomain(domain1);
		//		//		DomainSource d2 = dmrp.getDomain(domain2);
		//		Set<Xpath> xpaths1 = d1.getGenericXpaths();
		//		Iterator<Xpath> xpaths1It = xpaths1.iterator();
		//		WebPageDocument w1 = new WebPageDocument(new File(path+"p"+1+"/"+"orig"+domain1+".html"), domain1, path+"p"+1+"/", 
		//				parameterTextFusion, domain1);
		//		while (xpaths1It.hasNext()) {
		//			Xpath currentXpath = xpaths1It.next();
		//			String path = currentXpath.getXpath();
		//			XpathApplier xapplier = XpathApplier.getInstance();
		//			NodeList nl = xapplier.getNodes(path, w1.getDocument());
		//			//può succedere che alcune xpath diano 0 perché quel segmento può essere assente nella pagina
		//			System.out.println(path);
		//			System.out.println(nl.getLength());
		//			if (nl.getLength()!=0) {
		//				System.out.println(nl.item(0).getTextContent());
		//			}
		//		}


		//aaaaa da qui
		//scorro tutte le persone e prendo il dominio x
		//dominio 1
		int domain = 1;
		XpathApplier xapplier = XpathApplier.getInstance();
		DomainsRepository dmrp = DomainsRepository.getInstance();
		DomainSource dom1 = dmrp.getDomain(domain);
		Set<Xpath> xpaths1 = dom1.getGenericXpaths();
		for (int i=1;i<=7;i++) {
			String currentFolder = path+"p"+i+"/";
			String d1Path = currentFolder+"orig"+domain+".html";
			File d1 = new File(d1Path);
			if (d1.exists()) {
				WebPageDocument w = new WebPageDocument(new File(path+"p"+i+"/"+"orig"+domain+".html"), domain, path+"p"+1+"/", 
						parameterTextFusion, domain);
				org.w3c.dom.Document w3cDocument = xapplier
						.color(new HashSet<>(xpaths1), w.getDocument());
				if (w3cDocument != null) {
					PrintWriter testPrinter = new PrintWriter(currentFolder+"GenericSegments.html", "UTF-8");
					testPrinter.println(DocumentUtils.getStringFromDocument(w3cDocument));
					testPrinter.close();
				}


				//				Xpath currentXpath = xpaths1It.next();
				//				String path = currentXpath.getXpath();
				//				XpathApplier xapplier = XpathApplier.getInstance();
				//				NodeList nl = xapplier.getNodes(path, w.getDocument());
				//				//può succedere che alcune xpath diano 0 perché quel segmento può essere assente nella pagina
				//				System.out.println(path);
				//				System.out.println(nl.getLength());
				//				if (nl.getLength()!=0) {
				//					System.out.println(nl.item(0).getTextContent());
				//				}
			}
		} //fine for per le stampe

		//stampiamo i collegamenti... T^T
		MatchingRepository mr = MatchingRepository.getInstance();
		List<Matching> matchings = mr.getMatchings();
		PrintWriter testPrinter = new PrintWriter("AAAfinalMatchingsXpaths.csv", "UTF-8");
		System.out.println(matchings.size());
		DomainsRepository dr = DomainsRepository.getInstance();
		for (int p=1;p<=7;p++) {
			for (int i=0; i<matchings.size(); i++) {
				Matching currentMatching = matchings.get(i);
				Map<DomainSource,Xpath> mapMatchings = currentMatching.getMatching();
				for(int j=1;j<=5;j++) {
					//			Iterator<DomainSource> matchingsIt = mapMatchings.keySet().iterator();
					//			while (matchingsIt.hasNext()) {
					//faccio scorrere i domini
					//				DomainSource currentDomain = matchingsIt.next();
					DomainSource currentDomain = dr.getDomain(j);
					Xpath currentXpath = mapMatchings.get(currentDomain);
					if (currentXpath != null) {
						//				System.out.println("Dominio "+currentDomain.getParameter());
						//				System.out.println(currentXpath.getXpath());
						String currentFolder = path+"p"+p+"/";
						String d1Path = currentFolder+"orig"+currentDomain.getParameter()+".html";
						File d1 = new File(d1Path);
						if (d1.exists()) {
							WebPageDocument w = new WebPageDocument(new File(path+"p"+p+"/"+"orig"+currentDomain.getParameter()+".html"), 
									currentDomain.getParameter(), path+"p"+p+"/", 
									parameterTextFusion, currentDomain.getParameter());
							NodeList nl = xapplier.getNodes(currentXpath.getXpath(), w.getDocument());
							if (nl.getLength() != 0) {
								//						System.out.println(nl.item(0).getTextContent().replaceAll(",", "")+",");
								testPrinter.print(nl.item(0).getTextContent().replaceAll(",", "")
										.replaceAll("\n", "")+",");
							}
							else	{ //nessun matching, lascio spazio vuoto 
								//						System.out.println(" ,");
								testPrinter.print("--,");
							}
						}
						else  { //nessun documento, quindi per quella persona lasciamo uno spazio vuoto
							//					System.out.println("Dominio "+currentDomain.getParameter());
							//					System.out.println(" ,");
							testPrinter.print("--,");
						}
					}
					else {//non c'è nel matching corrente il dominio corrente
						testPrinter.print("--,");
					}

				}
				testPrinter.println();
				//			System.out.println("______________________________");
			}
			testPrinter.println();
		}
		testPrinter.close();
	}
}

