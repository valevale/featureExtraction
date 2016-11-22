package segmentation;

import java.io.File;

public class Configuration {
	private static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/";
	private static File webPages = new File(path + "webpageProva.txt");
	private static double range = 0.2;
	private static String result = "testResultProva.csv";
	
	public static String getPath() {
		return path;
	}
	
	public static File getInput() {
		return webPages;
	}
	
	public static String getOutput() {
		return result;
	}
	
	public static double getRange() {
		return range;
	}
}
