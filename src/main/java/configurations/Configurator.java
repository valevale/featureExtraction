package configurations;


public class Configurator {
	private static double segmentationGrainParameter = -1;
	private static double cosSimThreshold = 0.6;
	private static String indexesPath = "segmentIndexes/";
	
	public static double getSegmentationParameter() {
		return segmentationGrainParameter;
	}
	
	public static double getCosSimThreshold() {
		return cosSimThreshold;
	}
	
	public static String getIndexesPath() {
		return indexesPath;
	}
}
