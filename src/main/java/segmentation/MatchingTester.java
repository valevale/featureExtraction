package segmentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import scala.Tuple2;

public class MatchingTester {

	public static void test(Map<String,Set<String>> realMatchings, String pathFileGroundTruth, String path4log,
			String d1, String d2, double threshold) throws FileNotFoundException, IOException {

		PrintWriter printer = new PrintWriter(path4log, "UTF-8");

		Map<String,Set<String>> groundTruthMatchings = extractMatchings(pathFileGroundTruth);

		//queste liste contengono tutti i matching
		//lista composta da tuple: ogni tupla rappresenta un matching
		//il primo elemento è un segmento della prima pagina
		//il secondo elemento è un segmento della seconda pagina
		Set<Tuple2<String,String>> listMatchingsGT = new HashSet<>();
		Set<Tuple2<String,String>> listMatchingsR = new HashSet<>();

		//queste liste raccolgono precision e recall di ogni gruppo di matching
		//per il calcolo della media
		List<Float> precisions = new ArrayList<>();
		List<Float> recalls = new ArrayList<>();

		Iterator<Entry<String, Set<String>>> iterGT = groundTruthMatchings.entrySet().iterator();
		while (iterGT.hasNext()) {

			Entry<String, Set<String>> entry = iterGT.next();

			//mettiamo l'entry nella lista dei matching di GT,
			//per il successivo calcolo di precision e recall
			Iterator<String> matchesIt = entry.getValue().iterator();
			while (matchesIt.hasNext()) {
				String singleMatch = matchesIt.next();
				listMatchingsGT.add(new Tuple2<String,String>(entry.getKey(),singleMatch));
			}

			printer.println("Valutiamo l'entry: "+entry.getKey());
			if (realMatchings.containsKey(entry.getKey())) {
				Set<String> matchesR = realMatchings.get(entry.getKey());

				Set<String> matchesGT = entry.getValue();
				Set<String> truePositives = new HashSet<>(entry.getValue());
				truePositives.retainAll(matchesR); //true positives

				printer.println("GT: "+ matchesGT+"\n size: "+ matchesGT.size());
				printer.println("R: "+ matchesR+"\n size: "+ matchesR.size());

				printer.println("True positives: "+ truePositives+"\n size: "+ truePositives.size());

				float precision = (float) truePositives.size() / matchesR.size();

				float recall =  (float) truePositives.size() / matchesGT.size();

				precisions.add(precision);
				recalls.add(recall);


				printer.println("Precision: tp ("+ truePositives.size() +") / R ("+ matchesR.size()+")"+
						" = "+precision);

				printer.println("Recall: tp ("+ truePositives.size() +") / GT ("+ matchesGT.size()+")"+
						" = "+recall);
			}
			else { //R non ha trovato matching per quel segmento. perciò precision e recall vengono calcolate zero

				printer.println("No gruppo di matching in R");

				float precision = 1;

				float recall =  0;

				precisions.add(precision);
				recalls.add(recall);

				printer.println("Precision: "+precision);

				printer.println("Recall: "+recall);
			}

			printer.println("_______________");
		}
		Iterator<Entry<String, Set<String>>> iterR = realMatchings.entrySet().iterator();
		while (iterR.hasNext()) {

			Entry<String, Set<String>> entry = iterR.next();

			//mettiamo l'entry nella lista dei matching di R,
			//per il successivo calcolo di precision e recall
			Iterator<String> matchesIt = entry.getValue().iterator();
			while (matchesIt.hasNext()) {
				String singleMatch = matchesIt.next();
				listMatchingsR.add(new Tuple2<String,String>(entry.getKey(),singleMatch));
			}

			if (!groundTruthMatchings.containsKey(entry.getKey())) {

				printer.println("Valutiamo l'entry: "+entry.getKey());
				printer.println("Non è presente in GT");

				float precision = 0;

				float recall =  1;

				precisions.add(precision);
				recalls.add(recall);

				printer.println("Precision: "+precision);

				printer.println("Recall: "+recall);
			}
			printer.println("_______________");
		}

		//calcolare media
		printer.println("Calculation of averages");

		float sumPrecisions = 0;
		for (int i=0; i<precisions.size();i++) {
			sumPrecisions = sumPrecisions + precisions.get(i);
		}
		float avgPrecisions = (float) sumPrecisions / precisions.size();

		printer.println("avg precision: "+ avgPrecisions);

		float sumRecalls = 0;
		for (int i=0; i<recalls.size();i++) {
			sumRecalls = sumRecalls + recalls.get(i);
		}
		float avgRecalls = (float) sumRecalls / recalls.size();

		printer.println("avg recall: "+ avgRecalls);


		//calcolo della precision e recall in base ai matching
		Set<Tuple2<String,String>> truePositives = new HashSet<>(listMatchingsGT);
		truePositives.retainAll(listMatchingsR);

		float precisionMatchings;
		float recallMatchings;

		printer.println("Calculation of Precision and Recall based on matching");

		if (listMatchingsR.size() == 0) {

			precisionMatchings = 1;
			recallMatchings = 0;

			printer.println("R vuoto");

		}

		else {

			precisionMatchings = (float) truePositives.size() / listMatchingsR.size();
			recallMatchings = (float) truePositives.size() / listMatchingsGT.size();
		}

		//		if (listMatchingsGT.size() == 0) {
		//			printer.println("GT vuoto");
		//
		//			printer.println("Matchings precision: "+ 0);
		//
		//			printer.println("Matching recall: "+1);
		//		}

		printer.println("Matchings precision: tp ("+ truePositives.size() +") / R ("+ listMatchingsR.size()+")"+
				" = "+precisionMatchings);

		printer.println("Matching recall: tp ("+ truePositives.size() +") / GT ("+ listMatchingsGT.size()+")"+
				" = "+recallMatchings);

		printer.close();

		PrintWriter csvPrinter = new PrintWriter(new FileWriter(new File("testResult.csv"), true));
		csvPrinter.println(new DecimalFormat("#.#").format(threshold)+ " " +precisionMatchings
				+ " " +recallMatchings+" " +avgPrecisions+" "+avgRecalls+" "+d1+" "+d2);
		csvPrinter.close();
		
	}


		/* il file di ingresso è così strutturato:
		 * è diviso da 3 \n in gruppi di matching 
		 * ogni gruppo di matching è formato da diversi xpath, separati da un \n
		 * il primo xpath fa parte della prima pagina, i restanti xpath del gruppo
		 * corrispondono ai matching avuti con la seconda pagina */
		public static Map<String,Set<String>> extractMatchings(String pathFile) throws FileNotFoundException, IOException {
			String file = IOUtils.toString(new FileReader(new File(pathFile)));
			//suddivido prima i gruppi di matching
			String[] matchingGroups = file.split("\n\n");
			//ora lavoro su ogni gruppo
			Map<String,Set<String>> groundTruthMatchings = new HashMap<>();
			//		System.out.println(matchingGroups.length);
			for (int i=0;i<matchingGroups.length;i++) {
				//			System.out.println(i + " " +matchingGroups[i]);
				String[] xpaths = matchingGroups[i].split("\n");
				Set<String> matches = new HashSet<>();
				for (int j=1;j<xpaths.length;j++) {
					matches.add(xpaths[j]);
				}
				groundTruthMatchings.put(xpaths[0], matches);
			}
			return groundTruthMatchings;
		}

		public static void main(String[] args) throws FileNotFoundException, IOException {
			Map<String,Set<String>> groundTruthMatchings = 
					extractMatchings("/home/valentina/workspace_nuovo/FeatureExtractor/pages4test/zNewTest/"+
							"annamaria_bagnasco/matching12.txt");
			System.out.println(groundTruthMatchings.size());
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, Set<String>>> iter = groundTruthMatchings.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Set<String>> entry = iter.next();
				sb.append(entry.getKey());
				sb.append('=').append('"');
				sb.append(entry.getValue());
				sb.append('"');
				if (iter.hasNext()) {
					sb.append(',').append(' ').append('\n').append('\n');
				}
			}

			System.out.println(sb.toString());
		}

	}
