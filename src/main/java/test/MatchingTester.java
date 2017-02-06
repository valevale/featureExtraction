//package test;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.xml.xpath.XPathExpressionException;
//
//import java.util.Map.Entry;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopDocs;
//
//import lib.utils.DocumentUtils;
//import lucene.SegmentSearcher;
//import model.Segment;
//import model.WebPageDocument;
//import scala.Tuple2;
//import xpath.utils.XpathApplier;
//
//public class MatchingTester {
//
//	public static void testCosineSimilarityThreshold(List<Tuple2<Segment, TopDocs>> segment2hits, String cartella, int n1, int n2, 
//			double range, String resultFinalName) throws XPathExpressionException, Exception {
////		double range = ConfigurationTestCosSimThreshold.getRange();
//		String d1Path = cartella+"orig"+n1+".html";
//		String d2Path = cartella+"orig"+n2+".html";
//		String indexPath = cartella+"segmentIndex";
//		SegmentSearcher searcher = new SegmentSearcher(indexPath);
//		for (double i=0; i<=1; i=i+range) {
//
//			System.out.println("Threshold: "+i);
//			//mappa per la verifica
//			Map<String,Set<String>> matchings = new HashMap<>();
//			Set<Segment> segmentsToColor = new HashSet<>();
//			for (int j=0; j<segment2hits.size(); j++) {
//				Segment seg = segment2hits.get(j)._1();
//				TopDocs hits = segment2hits.get(j)._2();
//				for(ScoreDoc scoreDoc : hits.scoreDocs) {
//					if (scoreDoc.score >= i) {
//						org.apache.lucene.document.Document lucDoc = null;
//						try {
//							lucDoc = searcher.getDocument(scoreDoc);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						seg.setRelevance(seg.getRelevance()+1);
//						segmentsToColor.add(seg);
//						//memorizziamo in una mappa il matching dell'algoritmo
//						Set<String> matches;
//						if (!matchings.containsKey((seg.getAbsoluteXPath()))) {
//							matches = new HashSet<>();
//						}
//						else {
//							matches = matchings.get(seg.getAbsoluteXPath());
//						}
//						matches.add(lucDoc.get("segmentPath"));
//						matchings.put(seg.getAbsoluteXPath(), matches);
//					}
//				}
//			}
//			//testing
//			System.out.println("Evaluation of results...");
//			//crea la cartella di log se non esiste
//			new File(cartella+"/testLogs").mkdirs();
//			test(matchings, cartella+"matching"+n1+n2+".txt", cartella+"/testLogs/log"+n1+n2+"_"+i+".txt",
//					d1Path, d2Path, i, resultFinalName);
//			System.out.println("End Evaluation of results.");
//
//			System.out.println("coloring relevance");
//			//crea la cartella di relevance se non esiste
//			//TODO dovresti farlo da altre parti...
//			new File(cartella+"/relevances").mkdirs();
//			XpathApplier xpapplier = XpathApplier.getInstance();
//			//			org.w3c.dom.Document coloredRelevance = xpMaker.colorRelevance(segmentsToColor, doc_firstPage);
//			org.w3c.dom.Document coloredRelevance = xpapplier.colorRelevance(segmentsToColor, 
//					new WebPageDocument(new File(d1Path)).getDocument_jsoup());
//			PrintWriter testPrinter = new PrintWriter(cartella+"/relevances/relevance"+n1+"-"+n2+"_"+i+".html", "UTF-8");
//			testPrinter.println(DocumentUtils.getStringFromDocument(coloredRelevance));
//			testPrinter.close();
//
//			//setta a 0 la rilevanza dei segmenti colorati, per le successive iterazioni
//			segmentsToColor.forEach(segment -> {
//				segment.setRelevance(0);
//			});
//
//			System.out.println("End");
//		}
//	}
//
//	public static void testRelevanceMatchings(List<Tuple2<Segment, TopDocs>> segment2hits, String cartella, int n1, int n2, 
//			double threshold, String resultFinalName) throws XPathExpressionException, Exception {
//
//		String d1Path = cartella+"orig"+n1+".html";
//		String d2Path = cartella+"orig"+n2+".html";
//		String indexPath = cartella+"segmentIndex";
//		SegmentSearcher searcher = new SegmentSearcher(indexPath);
//
//		//mappa per la verifica
//		Map<String,Set<String>> matchings = new HashMap<>();
//		Set<Segment> segmentsToColor = new HashSet<>();
//		for (int j=0; j<segment2hits.size(); j++) {
//			Segment seg = segment2hits.get(j)._1();
//			TopDocs hits = segment2hits.get(j)._2();
//			for(ScoreDoc scoreDoc : hits.scoreDocs) {
//				if (scoreDoc.score >= threshold) {
//					org.apache.lucene.document.Document lucDoc = null;
//					try {
//						lucDoc = searcher.getDocument(scoreDoc);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					seg.setRelevance(seg.getRelevance()+1);
//					segmentsToColor.add(seg);
//					//memorizziamo in una mappa il matching dell'algoritmo
//					Set<String> matches;
//					if (!matchings.containsKey((seg.getAbsoluteXPath()))) {
//						matches = new HashSet<>();
//					}
//					else {
//						matches = matchings.get(seg.getAbsoluteXPath());
//					}
//					matches.add(lucDoc.get("segmentPath"));
//					matchings.put(seg.getAbsoluteXPath(), matches);
//				}
//			}
//		}
//		//testing
//		System.out.println("Evaluation of results...");
//		//crea la cartella di log se non esiste
//		new File(cartella+"/testLogs").mkdirs();
//		test(matchings, cartella+"matching"+n1+n2+".txt", cartella+"/testLogs/log"+n1+n2+"_"+threshold+".txt",
//				d1Path, d2Path, threshold, resultFinalName);
//		System.out.println("End Evaluation of results.");
//
//		System.out.println("coloring relevance");
//		//crea la cartella di relevance se non esiste
//		//TODO dovresti farlo da altre parti...
//		new File(cartella+"/relevances").mkdirs();
//		XpathApplier xpapplier = XpathApplier.getInstance();
//		//			org.w3c.dom.Document coloredRelevance = xpMaker.colorRelevance(segmentsToColor, doc_firstPage);
//		org.w3c.dom.Document coloredRelevance = xpapplier.colorRelevance(segmentsToColor, 
//				new WebPageDocument(new File(d1Path)).getDocument_jsoup());
//		PrintWriter testPrinter = new PrintWriter(cartella+"/relevances/relevance"+n1+"-"+n2+"_"+threshold+".html", "UTF-8");
//		testPrinter.println(DocumentUtils.getStringFromDocument(coloredRelevance));
//		testPrinter.close();
//
//		//setta a 0 la rilevanza dei segmenti colorati, per le successive iterazioni
//		segmentsToColor.forEach(segment -> {
//			segment.setRelevance(0);
//		});
//
//		System.out.println("End");
//	}
//
//	public static void testMatchings(List<Tuple2<Segment, TopDocs>> segment2hits, String cartella, int n1, int n2, 
//			double threshold, String resultFileName) throws XPathExpressionException, Exception {
//
//		//crea la cartella di log se non esiste
//		new File(cartella+"/testLogs").mkdirs();
//
//		PrintWriter printer = new PrintWriter(cartella+"/testLogs/log"+n1+n2+"_"+threshold+".txt");
//
//		//estraiamo la ground truth dal file
//		String file = IOUtils.toString(new FileReader(cartella+"matching"+n1+n2+".txt"));
//
//		Set<String> segmentsWithMatchingsGT = new HashSet<>(Arrays.asList(file.split("\n")));
//
//		//lista che contiene i segmenti con matching ottenuti dall'algoritmo
//		Set<String> segmentsWithMatchingsR = new HashSet<>();
//
//		for (int j=0; j<segment2hits.size(); j++) {
//			if (segment2hits.get(j)._2().scoreDocs[0].score >=threshold) {
//				Segment seg = segment2hits.get(j)._1();
//				segmentsWithMatchingsR.add(seg.getAbsoluteXPath());
//			}
//		}
//		Set<String> truePositives = new HashSet<>(segmentsWithMatchingsGT);
//		truePositives.retainAll(segmentsWithMatchingsR); //true positives
//
//		printer.println("GT: "+ segmentsWithMatchingsGT+"\n size: "+ segmentsWithMatchingsGT.size());
//		printer.println("R: "+ segmentsWithMatchingsR+"\n size: "+ segmentsWithMatchingsR.size());
//
//		printer.println("True positives: "+ truePositives+"\n size: "+ truePositives.size());
//
//		float precision = (float) truePositives.size() / segmentsWithMatchingsR.size();
//
//		float recall =  (float) truePositives.size() / segmentsWithMatchingsGT.size();
//
//		printer.println("Matchings precision: tp ("+ truePositives.size() +") / R ("+ segmentsWithMatchingsR.size()+")"+
//				" = "+precision);
//
//		printer.println("Matching recall: tp ("+ truePositives.size() +") / GT ("+ segmentsWithMatchingsGT.size()+")"+
//				" = "+recall);
//
//		printer.close();
//
//		float fMeasure = (float) 2*((precision*recall)/(precision+recall));
//
//		String d1Path = cartella+"orig"+n1+".html";
//		String d2Path = cartella+"orig"+n2+".html";
//
//		PrintWriter csvPrinter = new PrintWriter(new FileWriter(new File(resultFileName), true));
//		csvPrinter.println(new DecimalFormat("#.#").format(threshold).replace(",", ".") +
//				"," +precision+ "," +recall+"," +
//				fMeasure+","+
//				d1Path+","+d2Path+
//				","+(d1Path.substring(60, d1Path.length()-5)+d2Path.substring(d2Path.length()-6, d2Path.length()-5)));
//		csvPrinter.close();
//
//		//salvare per il calcolo delle medie
//		NumbersStorer storer = NumbersStorer.getInstance();
//		storer.add(threshold, precision, recall);
//	}
//
//
//
//	public static void test(Map<String,Set<String>> realMatchings, String pathFileGroundTruth, String path4log,
//			String d1, String d2, double threshold, String resultFinalName) throws FileNotFoundException, IOException {
//
//		PrintWriter printer = new PrintWriter(path4log, "UTF-8");
//
//		Map<String,Set<String>> groundTruthMatchings = extractMatchings(pathFileGroundTruth);
//
//		//queste liste contengono tutti i matching
//		//lista composta da tuple: ogni tupla rappresenta un matching
//		//il primo elemento è un segmento della prima pagina
//		//il secondo elemento è un segmento della seconda pagina
//		Set<Tuple2<String,String>> listMatchingsGT = new HashSet<>();
//		Set<Tuple2<String,String>> listMatchingsR = new HashSet<>();
//
//		//queste liste raccolgono precision e recall di ogni gruppo di matching
//		//per il calcolo della media
//		List<Float> precisions = new ArrayList<>();
//		List<Float> recalls = new ArrayList<>();
//
//		Iterator<Entry<String, Set<String>>> iterGT = groundTruthMatchings.entrySet().iterator();
//		while (iterGT.hasNext()) {
//
//			Entry<String, Set<String>> entry = iterGT.next();
//
//			//mettiamo l'entry nella lista dei matching di GT,
//			//per il successivo calcolo di precision e recall
//			Iterator<String> matchesIt = entry.getValue().iterator();
//			while (matchesIt.hasNext()) {
//				String singleMatch = matchesIt.next();
//				listMatchingsGT.add(new Tuple2<String,String>(entry.getKey(),singleMatch));
//			}
//
//			printer.println("Valutiamo l'entry: "+entry.getKey());
//			if (realMatchings.containsKey(entry.getKey())) {
//				Set<String> matchesR = realMatchings.get(entry.getKey());
//
//				Set<String> matchesGT = entry.getValue();
//				Set<String> truePositives = new HashSet<>(entry.getValue());
//				truePositives.retainAll(matchesR); //true positives
//
//				printer.println("GT: "+ matchesGT+"\n size: "+ matchesGT.size());
//				printer.println("R: "+ matchesR+"\n size: "+ matchesR.size());
//
//				printer.println("True positives: "+ truePositives+"\n size: "+ truePositives.size());
//
//				float precision = (float) truePositives.size() / matchesR.size();
//
//				float recall =  (float) truePositives.size() / matchesGT.size();
//
//				precisions.add(precision);
//				recalls.add(recall);
//
//
//				printer.println("Precision: tp ("+ truePositives.size() +") / R ("+ matchesR.size()+")"+
//						" = "+precision);
//
//				printer.println("Recall: tp ("+ truePositives.size() +") / GT ("+ matchesGT.size()+")"+
//						" = "+recall);
//			}
//			else { //R non ha trovato matching per quel segmento. perciò precision e recall vengono calcolate zero
//
//				printer.println("No gruppo di matching in R");
//
//				float precision = 1;
//
//				float recall =  0;
//
//				precisions.add(precision);
//				recalls.add(recall);
//
//				printer.println("Precision: "+precision);
//
//				printer.println("Recall: "+recall);
//			}
//
//			printer.println("_______________");
//		}
//		Iterator<Entry<String, Set<String>>> iterR = realMatchings.entrySet().iterator();
//		while (iterR.hasNext()) {
//
//			Entry<String, Set<String>> entry = iterR.next();
//
//			//mettiamo l'entry nella lista dei matching di R,
//			//per il successivo calcolo di precision e recall
//			Iterator<String> matchesIt = entry.getValue().iterator();
//			while (matchesIt.hasNext()) {
//				String singleMatch = matchesIt.next();
//				listMatchingsR.add(new Tuple2<String,String>(entry.getKey(),singleMatch));
//			}
//
//			if (!groundTruthMatchings.containsKey(entry.getKey())) {
//
//				printer.println("Valutiamo l'entry: "+entry.getKey());
//				printer.println("Non è presente in GT");
//
//				float precision = 0;
//
//				float recall =  1;
//
//				precisions.add(precision);
//				recalls.add(recall);
//
//				printer.println("Precision: "+precision);
//
//				printer.println("Recall: "+recall);
//			}
//			printer.println("_______________");
//		}
//
//		//calcolare media
//		printer.println("Calculation of averages");
//
//		float sumPrecisions = 0;
//		for (int i=0; i<precisions.size();i++) {
//			sumPrecisions = sumPrecisions + precisions.get(i);
//		}
//		float avgPrecisions = (float) sumPrecisions / precisions.size();
//
//		printer.println("avg precision: "+ avgPrecisions);
//
//		float sumRecalls = 0;
//		for (int i=0; i<recalls.size();i++) {
//			sumRecalls = sumRecalls + recalls.get(i);
//		}
//		float avgRecalls = (float) sumRecalls / recalls.size();
//
//		printer.println("avg recall: "+ avgRecalls);
//
//
//		//calcolo della precision e recall in base ai matching
//		Set<Tuple2<String,String>> truePositives = new HashSet<>(listMatchingsGT);
//		truePositives.retainAll(listMatchingsR);
//
//		float precisionMatchings;
//		float recallMatchings;
//
//		printer.println("Calculation of Precision and Recall based on matching");
//
//		if (listMatchingsR.size() == 0) {
//
//			precisionMatchings = 1;
//			recallMatchings = 0;
//
//			printer.println("R vuoto");
//
//		}
//
//		else {
//
//			precisionMatchings = (float) truePositives.size() / listMatchingsR.size();
//			recallMatchings = (float) truePositives.size() / listMatchingsGT.size();
//		}
//
//		//		if (listMatchingsGT.size() == 0) {
//		//			printer.println("GT vuoto");
//		//
//		//			printer.println("Matchings precision: "+ 0);
//		//
//		//			printer.println("Matching recall: "+1);
//		//		}
//
//		printer.println("Matchings precision: tp ("+ truePositives.size() +") / R ("+ listMatchingsR.size()+")"+
//				" = "+precisionMatchings);
//
//		printer.println("Matching recall: tp ("+ truePositives.size() +") / GT ("+ listMatchingsGT.size()+")"+
//				" = "+recallMatchings);
//
//		printer.close();
//
//		float avgFMeasure = (float) 2*((avgPrecisions*avgRecalls)/(avgPrecisions+avgRecalls));
//		float fMeasureMatchings = (float) 2*((precisionMatchings*recallMatchings)/(precisionMatchings+recallMatchings));
//
//		PrintWriter csvPrinter = new PrintWriter(new FileWriter(new File(resultFinalName), true));
//		csvPrinter.println(new DecimalFormat("#.#").format(threshold).replace(",", ".") +
//				"," +precisionMatchings+ "," +recallMatchings+"," +
//				fMeasureMatchings+","+
//				avgPrecisions+","+avgRecalls+
//				","+avgFMeasure+","+
//				d1+","+d2+
//				","+(d1.substring(60, d1.length()-5)+d2.substring(d2.length()-6, d2.length()-5)));
//		csvPrinter.close();
//
//		//salvare per il calcolo delle medie
//		NumbersStorer storer = NumbersStorer.getInstance();
//		storer.add(threshold, precisionMatchings, recallMatchings, avgPrecisions, avgRecalls);
//	}
//
//
//	/* il file di ingresso è così strutturato:
//	 * è diviso da 3 \n in gruppi di matching 
//	 * ogni gruppo di matching è formato da diversi xpath, separati da un \n
//	 * il primo xpath fa parte della prima pagina, i restanti xpath del gruppo
//	 * corrispondono ai matching avuti con la seconda pagina */
//	public static Map<String,Set<String>> extractMatchings(String pathFile) throws FileNotFoundException, IOException {
//		String file = IOUtils.toString(new FileReader(new File(pathFile)));
//		//suddivido prima i gruppi di matching
//		String[] matchingGroups = file.split("\n\n");
//		//ora lavoro su ogni gruppo
//		Map<String,Set<String>> groundTruthMatchings = new HashMap<>();
//		//		System.out.println(matchingGroups.length);
//		for (int i=0;i<matchingGroups.length;i++) {
//			//			System.out.println(i + " " +matchingGroups[i]);
//			String[] xpaths = matchingGroups[i].split("\n");
//			Set<String> matches = new HashSet<>();
//			for (int j=1;j<xpaths.length;j++) {
//				matches.add(xpaths[j]);
//			}
//			groundTruthMatchings.put(xpaths[0], matches);
//		}
//		return groundTruthMatchings;
//	}
//
//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		Map<String,Set<String>> groundTruthMatchings = 
//				extractMatchings("/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/"+
//						"annamaria_bagnasco/matching12.txt");
//		String folder = "/home/valentina/workspace_nuovo/FeatureExtractor/pages4test"+
//				"/annamaria_bagnasco/orig2.html";
//		System.out.println(folder.substring(60, folder.length()-5));
//		System.out.println(groundTruthMatchings.size());
//		StringBuilder sb = new StringBuilder();
//		Iterator<Entry<String, Set<String>>> iter = groundTruthMatchings.entrySet().iterator();
//		while (iter.hasNext()) {
//			Entry<String, Set<String>> entry = iter.next();
//			sb.append(entry.getKey());
//			sb.append('=').append('"');
//			sb.append(entry.getValue());
//			sb.append('"');
//			if (iter.hasNext()) {
//				sb.append(',').append(' ').append('\n').append('\n');
//			}
//		}
//
//		System.out.println(sb.toString());
//	}
//
//}
