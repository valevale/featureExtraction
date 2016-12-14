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
//import segmentation.RelevantSegmentsFinder;
//
///*Con questo test si verifica che, variando la persona, per una coppia di pagine web dii due stessi domini
// * vengono sempre recuperate informazioni rilevanti
// * (per rilevanti si intendono informazioni che si trovano su entrambe le pagine web) */
//
//public class MainTestRelevantSegmentsRetrieval {
//	public static void main(String[] args) throws Exception {
//
//		double threshold = ConfigurationTestRelevantSegmentsRetrieval.getThreshold();
//		String file = IOUtils.toString(new FileReader(ConfigurationTestRelevantSegmentsRetrieval.getInput()));
//
//		String[] folders = file.split("\n");
//
//		PrintWriter printer = new PrintWriter(ConfigurationTestRelevantSegmentsRetrieval.getPath()+ConfigurationTestRelevantSegmentsRetrieval.getResultFileName(), "UTF-8");
//		printer.println("threshold,precisionMatchings,recallMatchings,F-MeasureMatchings,avgPrecisions,avgRecalls,avgF-Measure,d1,d2,d1d2");
//		printer.close();
//
//		for (int i=0; i< folders.length; i++) {
//
//			String folder = folders[i];
//
//			System.out.println("****Current folder: ");
//			System.out.println(folder);
//
//			//struttura interna: in ogni cartella ci sono un totale [2-4] pagine web da confrontare tra loro
//			for (int j=1; j<10; j=j+2) {
//				if (new File(folder+"matching"+j+(j+1)+".txt").exists()) {
//					System.out.println("---Evaluating: "+j+ " & "+(j+1));
//					List<Tuple2<Segment, TopDocs>> segment2hits =
//							RelevantSegmentsFinder.findRelevantSegments(folder, j, (j+1), 1, 2, ConfigurationTestRelevantSegmentsRetrieval.getSegmentationGrainParameter());
//					MatchingTester.testRelevanceMatchings(segment2hits, folder, j, (j+1), threshold,
//							ConfigurationTestRelevantSegmentsRetrieval.getResultFileName());
//				}
//			}
//		}
//
//		printer = new PrintWriter(ConfigurationTestRelevantSegmentsRetrieval.getPath()+ConfigurationTestRelevantSegmentsRetrieval.getAverageResulFileName(), "UTF-8");
//		printer.println("threshold,precisionMatchings,recallMatchings,F-MeasureMatchings,avgPrecisions,avgRecalls,avgF-Measure");
//
//		NumbersStorer storer = NumbersStorer.getInstance();
//		printer.println(
//				new DecimalFormat("#.#").format(threshold).replace(",", ".") + "," +
//						storer.getAverageOf(threshold, "pm") + "," +
//						storer.getAverageOf(threshold, "rm") + "," +
//						storer.getFMeasureOf(threshold, "pm", "rm") + "," +
//						storer.getAverageOf(threshold, "ap") + "," +
//						storer.getAverageOf(threshold, "ar") + "," +
//						storer.getFMeasureOf(threshold, "ap", "ar")
//				);
//
//		printer.close();
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		Date date = new Date();
//		System.out.println("Task ended at "+dateFormat.format(date));
//
//	}
//}
