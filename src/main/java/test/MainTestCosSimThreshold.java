//package test;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.PrintWriter;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.commons.io.IOUtils;
//import org.apache.lucene.search.TopDocs;
//
//import model.Segment;
//import scala.Tuple2;
//import segmentation.TopSegmentsFinder;
//
//public class MainTestCosSimThreshold {
//
//	public static void main(String[] args) throws Exception {
//
//		String file = IOUtils.toString(new FileReader(ConfigurationTestCosSimThreshold.getInput()));
//
//		String[] folders = file.split("\n");
//
//		PrintWriter printer = new PrintWriter(ConfigurationTestCosSimThreshold.getPath()+ConfigurationTestCosSimThreshold.getResultFileName(), "UTF-8");
//		printer.println("threshold,precisionMatchings,recallMatchings,avgPrecisions,avgRecalls,d1,d2,d1d2");
//		printer.close();
//
//
//		for (int i=0; i< folders.length; i++) {
//
//			String folder = folders[i];
//
//			System.out.println("****Current folder: ");
//			System.out.println(folder);
//
//			//struttura interna: in ogni cartella ci sono un totale [2-4] pagine web da confrontare tra loro
//			for (int j=1; j<4; j++) {
//				for (int k=j+1; k<=10; k++) {
//					
//					if (new File(folder+"matching"+j+k+".txt").exists()) {
//						System.out.println("---Evaluating: "+j+ " & "+k);
//						List<Tuple2<Segment, TopDocs>> segment2hits = 
//								TopSegmentsFinder.findRelevantSegments_old(folder, j, k, j, k, ConfigurationTestCosSimThreshold.getSegmentationGrainParameter());
//						MatchingTester.testCosineSimilarityThreshold(segment2hits, folder, j, k,
//								ConfigurationTestCosSimThreshold.getRange(), 
//								ConfigurationTestCosSimThreshold.getResultFileName());
//					}
//				}
//			}
//		}
//
//		printer = new PrintWriter(ConfigurationTestCosSimThreshold.getPath()+ConfigurationTestCosSimThreshold.getAverageResulFileName(), "UTF-8");
//		printer.println("threshold,precisionMatchings,recallMatchings,F-MeasureMatchings,avgPrecisions,avgRecalls,avgF-Measure");
//
//		//non contiamo la prima riga coi titoli
//		NumbersStorer storer = NumbersStorer.getInstance();
//		for (double threshold=0; threshold<=1; threshold=threshold+ConfigurationTestCosSimThreshold.getRange()) {
//			printer.println(
//					new DecimalFormat("#.#").format(threshold).replace(",", ".") + "," +
//							storer.getAverageOf(threshold, "pm") + "," +
//							storer.getAverageOf(threshold, "rm") + "," +
//							storer.getFMeasureOf(threshold, "pm", "rm") + "," +
//							storer.getAverageOf(threshold, "ap") + "," +
//							storer.getAverageOf(threshold, "ar") + "," +
//							storer.getFMeasureOf(threshold, "ap", "ar")
//					);
//		}
//
//		printer.close();
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		Date date = new Date();
//		System.out.println("Task ended at "+dateFormat.format(date));
//
//	}
//}
