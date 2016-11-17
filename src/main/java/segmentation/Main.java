package segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

public class Main {

	static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/";

	static File webPagesPath = new File(path + "webpages.txt");

	public static void main(String[] args) throws Exception {
		
		String file = IOUtils.toString(new FileReader(webPagesPath));

		String[] folders = file.split("\n");
		
		PrintWriter printer = new PrintWriter(path+"testResult.csv", "UTF-8");
		printer.println("threshold precisionMatchings recallMatchings avgPrecisions avgRecalls d1 d2");
		printer.close();
		
		
		for (int i=0; i< folders.length; i++) {
			
			String folder = folders[i];
			
			System.out.println("****Current folder: ");
			System.out.println(folder);
			
			//struttura interna: in ogni cartella ci sono un totale [2-4] pagine web da confrontare tra loro
			for (int j=1; j<4; j++) {
				for (int k=j+1; k<=4; k++) {
					//TODO gestisci il fatto che non ci sia
					System.out.println("---Evaluating: "+j+ " & "+k);
					RelevantSegmentsFinder.findRelevantSegments(folder, j, k, 0.2);
				}
			}
		}
		
	}
}
