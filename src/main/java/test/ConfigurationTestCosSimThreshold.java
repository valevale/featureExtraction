package test;

import java.io.File;

/*qui sono indicati tutti i parametri da settare per l'avvio della suite di test*/
public class ConfigurationTestCosSimThreshold {
	private static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/";
	private static File webPagesPathsFile = new File(path + "webpages_grossa.txt");
	private static double rangeThreshold = 0.2;
	private static String resultFileName = "testResult_grossa.csv";
	private static String averageResultFileName = "testAverageResult_grossa.csv";
	//grossa: 3
	//fine: -1
	private static double segmentationGrainParameter = 3;
	
	public static String getPath() {
		return path;
	}
	
	public static double getSegmentationGrainParameter() {
		return segmentationGrainParameter;
	}
	
	public static File getInput() {
		return webPagesPathsFile;
	}
	
	public static String getResultFileName() {
		return resultFileName;
	}
	
	public static String getAverageResulFileName() {
		return averageResultFileName;
	}
	
	public static double getRange() {
		return rangeThreshold;
	}
}
