package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

public class BlockCreator {
	
	final static double parameter = 1.5;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		//http://www.repubblica.it/economia/2016/09/26/news/rinnovabili_entro_il_2050_meta_dei_cittadini_ue_si_produrra_da_solo_l_elettricita_-148511922/?ref=HREC1-4
		//http://www.ilfattoquotidiano.it/blog/ctroilo/
		String html = IOUtils.toString(new FileReader(new File("/home/valentina/Scrivania/troilo.html")));
		Document doc = Jsoup.parse(html);
		Element body = doc.body();
		List<Node> blocks = new ArrayList<>();
		body.traverse(new NodeVisitor() {
			public void head(Node node, int depth) {
				//System.out.println("Entering tag: " + node.nodeName() + ",  childs: " + node.childNodeSize());
				if (node.childNodeSize() == 0 && node.nodeName().equals("#text")
						&& !node.toString().equals(" ")) {
					//System.out.println(node.toString());
					blocks.add(node);
				}
			}
			public void tail(Node node, int depth) {
				//System.out.println("Exiting tag: " + node.nodeName() + ", " + depth + ", childs: " + node.childNodeSize());
			}
		});
		//TODO forse al posto di int è meglio il Node
		Map<Integer,List<String>> block2lines = new HashMap<>();
		for(int i=0;i<blocks.size();i++) {
			Node block = blocks.get(i);
			List<String> lines = new ArrayList<>();
			String blockToSeparate = block.toString();
			while (blockToSeparate.length()>40) {
				lines.add(blockToSeparate.substring(0, 40));
				blockToSeparate = blockToSeparate.substring(40, blockToSeparate.length());
			}
			//inserimento del pezzettino rimanente
			lines.add(blockToSeparate);
			block2lines.put(i, lines);

//			int density = calculateDensity(lines);

//			System.out.println(density + "\n______________________________________");
		}

		//ora facciamo la fusion
		//TODO anche qui al posto di int ci vanno i nodi

		boolean loop = true;
		while (loop) {
			loop = false;
			//scorro i blocchi, partendo dal secondo
			int dimensioneOriginaria = block2lines.size();
			for (int i=1; i<dimensioneOriginaria; i++) {
				//prendo i primi due che trovo
				List<String> blockLinesPrev = block2lines.get(i-1);
				List<String> blockLines = block2lines.get(i);
				List<String> blockLinesNext = block2lines.get(i+1);
				System.out.println("");
				if (blockLinesNext != null && calculateDensity(blockLinesPrev) == calculateDensity(blockLinesNext)
						&& calculateDensity(blockLines) < calculateDensity(blockLinesPrev)) {
					blockLinesPrev.addAll(blockLines);
					blockLinesPrev.addAll(blockLinesNext);
					block2lines.put((i+1), blockLinesPrev);
					block2lines.remove(i-1);
					block2lines.remove(i);
					i++;
					loop = true;
				}
				else {
					int delta = calculateDifference(calculateDensity(blockLinesPrev), calculateDensity(blockLines));
					if (delta < parameter) {
						blockLinesPrev.addAll(blockLines);
						block2lines.put(i, blockLinesPrev);
						block2lines.remove(i-1);
						loop = true;
					}
				}
			}
			//qui tolgo i buchi della mappa...
			block2lines = removeGaps(block2lines, dimensioneOriginaria);
		}
		System.out.println("**************************************************");
		//stampiamo i risultati
		for (int i=0; i<block2lines.size();i++) {
			if (block2lines.get(i) != null) {
				List<String> lines = block2lines.get(i);
				for(int j=0;j<lines.size();j++) {
					String line = lines.get(j);
					System.out.println(line);
				}
				System.out.println("_________________");
			}
		}
	}

	public static int calculateDensity(List<String> lines) {

		int tokens = 0;

		//		System.out.println(spaceCount);
		//		System.out.println(lines.size());
		//		if (lines.size()==1) System.out.println(spaceCount / lines.size());
		//		else System.out.println(spaceCount / (lines.size()-1));
		if (lines.size()==1) {
			for (int j=0;j<lines.size();j++) {
				String line = lines.get(j);
//				System.out.println(line);
				tokens = tokens + calculateTokens(line);
//				System.out.println(calculateTokens(line));
			};
			return (tokens);
		}
		else {
			for (int j=0;j<lines.size()-1;j++) {
				String line = lines.get(j);
//				System.out.println(line);
				tokens = tokens + calculateTokens(line);
//				System.out.println(calculateTokens(line));
			};
			return (tokens / (lines.size()-1));
		}
	}

	public static int calculateDifference(int a, int b) {
		if (a-b > 0) return a-b;
		else return b-a;
	}
	
	//TODO magari migliorare con stemming, o le parole divise in due...
	public static int calculateTokens(String text) {
		String trimmed = text.trim();
		int words = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
		return words;
	}

	public static Map<Integer,List<String>> removeGaps(Map<Integer,List<String>> map, int dim) {
		Map<Integer,List<String>> newMap = new HashMap<>();
		int j=0;
		for (int i=0; i<dim; i++) {
			if (map.get(i) != null) {
				newMap.put(j, map.get(i));
				j++;
			}
		}
		return newMap;
	}
}
