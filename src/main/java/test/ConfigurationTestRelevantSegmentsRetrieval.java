package test;

import java.io.File;

public class ConfigurationTestRelevantSegmentsRetrieval {
	private static String path = "/home/valentina/workspace_nuovo/FeatureExtractor/";
	private static File webPagesPathsFile = new File(path + "webpages_domini.txt");
	private static String resultFileName = "testSegmentsRetrievalResult_domini.csv";
	private static double segmentationGrainParameter = -1;
	private static double threshold = 0.6;
	private static String averageResultFileName = "testSegmentsAverageRetrievalResult_domini.csv";
	
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
	
	public static double getThreshold() {
		return threshold;
	}
	public static String getAverageResulFileName() {
		return averageResultFileName;
	}
}
