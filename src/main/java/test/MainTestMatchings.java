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
///*test che verifica che, data una coppia di domini, al variare della persona, i matching
// * contengono le stesse informazioni */
//public class MainTestMatchings {
//	public static void main(String[] args) throws Exception {
//		double threshold = ConfigurationTestMatchings.getThreshold();
//		String file = IOUtils.toString(new FileReader(ConfigurationTestMatchings.getInput()));
//		
//		String[] folders = file.split("\n");
//		
//		PrintWriter printer = new PrintWriter(ConfigurationTestMatchings.getPath()+ConfigurationTestMatchings.getResultFileName(), "UTF-8");
//		printer.println("threshold,precision,recall,F-Measure,d1,d2,d1d2");
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
//							RelevantSegmentsFinder.findRelevantSegments(folder, j, (j+1), 1, 2, ConfigurationTestMatchings.getSegmentationGrainParameter());
//					MatchingTester.testMatchings(segment2hits, folder, j, (j+1), threshold,
//							ConfigurationTestMatchings.getResultFileName());
//				}
//			}
//		}
//		
//		printer = new PrintWriter(ConfigurationTestMatchings.getPath()+ConfigurationTestMatchings.getAverageResulFileName(), "UTF-8");
//		printer.println("threshold,precision,recall,F-Measure");
//
//		NumbersStorer storer = NumbersStorer.getInstance();
//		printer.println(
//				new DecimalFormat("#.#").format(threshold).replace(",", ".") + "," +
//						storer.getAverageOf(threshold, "p") + "," +
//						storer.getAverageOf(threshold, "r") + "," +
//						storer.getFMeasureOf(threshold, "p", "r")
//				);
//
//		printer.close();
//		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//		Date date = new Date();
//		System.out.println("Task ended at "+dateFormat.format(date));
//		
//		
//	}
//	
//}
