package segmentation;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.apache.commons.io.IOUtils;

public class Main {

//	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/";
//
//	static File webPagesPath = new File(path + "webpages.txt");
//	static double range = 0.2;
//	static String pathResult = path+"testResult2.csv";

	public static void main(String[] args) throws Exception {

		String file = IOUtils.toString(new FileReader(Configuration.getInput()));

		String[] folders = file.split("\n");

		PrintWriter printer = new PrintWriter(Configuration.getPath()+Configuration.getOutput(), "UTF-8");
		printer.println("threshold precisionMatchings recallMatchings avgPrecisions avgRecalls d1 d2 d1d2");
		printer.close();


		for (int i=0; i< folders.length; i++) {

			String folder = folders[i];

			System.out.println("****Current folder: ");
			System.out.println(folder);

			//struttura interna: in ogni cartella ci sono un totale [2-4] pagine web da confrontare tra loro
			for (int j=1; j<4; j++) {
				for (int k=j+1; k<=10; k++) {
					//TODO gestisci il fatto che non ci sia
					if (new File(folder+"matching"+j+k+".txt").exists()) {
						System.out.println("---Evaluating: "+j+ " & "+k);
						RelevantSegmentsFinder.findRelevantSegments(folder, j, k, Configuration.getRange());
					}
				}
			}
		}

		printer = new PrintWriter(Configuration.getPath()+"testAverageResult.csv", "UTF-8");
		printer.println("threshold precisionMatchings recallMatchings avgPrecisions avgRecalls");

		//non contiamo la prima riga coi titoli
		NumbersStorer storer = NumbersStorer.getInstance();
		for (double threshold=0; threshold<=1; threshold=threshold+Configuration.getRange()) {
			printer.println(
					new DecimalFormat("#.#").format(threshold).replace(",", ".") + " " +
							storer.getAverageOf(threshold, "pm") + " " +
							storer.getAverageOf(threshold, "rm") + " " +
							storer.getAverageOf(threshold, "ap") + " " +
							storer.getAverageOf(threshold, "ar")
					);
		}

		printer.close();

	}
}
