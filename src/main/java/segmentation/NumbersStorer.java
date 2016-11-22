package segmentation;

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;

public class NumbersStorer {

	private static NumbersStorer instance = null;
	private static Map<String,Tuple2<Float,Float>> thresAndType2numbers = null;


	public static NumbersStorer getInstance() {
		if (instance == null)
			instance = new NumbersStorer();
		return instance;
	}

	private NumbersStorer() {
		thresAndType2numbers = new HashMap<>();
	}
	
	public void add(double threshold, float precisionMatchings, float recallMatchings, float avgPrecisions, float avgRecalls) {
		if (!thresAndType2numbers.containsKey(String.valueOf(threshold)+"pm")) 
			thresAndType2numbers.put(String.valueOf(threshold)+"pm", new Tuple2<>((float) 0, (float) 0));
		thresAndType2numbers.put(String.valueOf(threshold)+"pm", 
				new Tuple2<>(thresAndType2numbers.get(String.valueOf(threshold)+"pm")._1()+precisionMatchings,
						thresAndType2numbers.get(String.valueOf(threshold)+"pm")._2()+1));
		
		if (!thresAndType2numbers.containsKey(String.valueOf(threshold)+"rm")) 
			thresAndType2numbers.put(String.valueOf(threshold)+"rm", new Tuple2<>((float) 0, (float) 0));
		thresAndType2numbers.put(String.valueOf(threshold)+"rm", 
				new Tuple2<>(thresAndType2numbers.get(String.valueOf(threshold)+"rm")._1()+recallMatchings,
						thresAndType2numbers.get(String.valueOf(threshold)+"rm")._2()+1));
		
		if (!thresAndType2numbers.containsKey(String.valueOf(threshold)+"ap")) 
			thresAndType2numbers.put(String.valueOf(threshold)+"ap", new Tuple2<>((float) 0, (float) 0));
		thresAndType2numbers.put(String.valueOf(threshold)+"ap", 
				new Tuple2<>(thresAndType2numbers.get(String.valueOf(threshold)+"ap")._1()+avgPrecisions,
						thresAndType2numbers.get(String.valueOf(threshold)+"ap")._2()+1));
		
		if (!thresAndType2numbers.containsKey(String.valueOf(threshold)+"ar")) 
			thresAndType2numbers.put(String.valueOf(threshold)+"ar", new Tuple2<>((float) 0, (float) 0));
		thresAndType2numbers.put(String.valueOf(threshold)+"ar", 
				new Tuple2<>(thresAndType2numbers.get(String.valueOf(threshold)+"ar")._1()+avgRecalls,
						thresAndType2numbers.get(String.valueOf(threshold)+"ar")._2()+1));	}
	
	public float getAverageOf(double threshold, String parameter) {
		float sum = thresAndType2numbers.get(String.valueOf(threshold)+parameter)._1();
		float totalRows = thresAndType2numbers.get(String.valueOf(threshold)+parameter)._2();
		return (float) sum/totalRows;
	}
}
