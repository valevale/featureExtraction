package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

import scala.Tuple2;

public class BlockCreatorNode {

	final static double parameter = 2;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		PrintWriter textPrinter = new PrintWriter("troiloNew.txt", "UTF-8");
		//http://www.repubblica.it/economia/2016/09/26/news/rinnovabili_entro_il_2050_meta_dei_cittadini_ue_si_produrra_da_solo_l_elettricita_-148511922/?ref=HREC1-4
		//http://www.ilfattoquotidiano.it/blog/ctroilo/
		//http://www.paginebianche.it/roma/claudio-avv-amoroso-d'aragona.agjjhajaef
		String html = IOUtils.toString(new FileReader(new File("/home/valentina/Scrivania/troilo.html")));
		Document doc = Jsoup.parse(html);
		Element body = doc.body();
		List<Tuple2<Node,Integer>> blocks = new ArrayList<>();
		body.traverse(new NodeVisitor() {
			public void head(Node node, int depth) {
				//System.out.println("Entering tag: " + node.nodeName() + ",  childs: " + node.childNodeSize());
				if (node.childNodeSize() == 0 && node.nodeName().equals("#text")
						&& !node.toString().equals(" ")) {
					//System.out.println(node.toString());
					Tuple2<Node,Integer> t = new Tuple2<Node,Integer>(node,depth);
					blocks.add(t);
				}
			}
			public void tail(Node node, int depth) {
				//System.out.println("Exiting tag: " + node.nodeName() + ", " + depth + ", childs: " + node.childNodeSize());
			}
		});

		//INIZIALIZZAZIONE DELLE MAPPE
		//devo dividere i nodi in linee
		Map<Integer,List<String>> block2lines = new HashMap<>();
		Map<Integer,Tuple2<Node,Integer>> block2node = new HashMap<>();
		for(int i=0;i<blocks.size();i++) {
			//prendo il nodo
			Tuple2<Node,Integer> t = blocks.get(i);
			Node block = t._1();
			List<String> lines = new ArrayList<>();
			String blockToSeparate = block.toString();
			while (blockToSeparate.length()>40) {
				lines.add(blockToSeparate.substring(0, 40));
				blockToSeparate = blockToSeparate.substring(40, blockToSeparate.length());
			}
			//inserimento del pezzettino rimanente
			lines.add(blockToSeparate);
			block2lines.put(i, lines);
			//inserimento del nodo, con la sua profondità
			block2node.put(i, t);
		}

		//ora facciamo la fusion

		boolean loop = true;
		while (loop) {
			loop = false;
			//scorro i blocchi, partendo dal secondo
			//le due mappe devono avere sempre la stessa dimensione
			int dimensioneOriginaria = block2lines.size();
			for (int i=1; i<dimensioneOriginaria; i++) {
				//le operazioni vengono fatte sulle linee
				//i cambiamenti (rimozioni e fusioni) devono essere ripercossi sulla mappa dei nodi
				List<String> blockLinesPrev = block2lines.get(i-1);
				List<String> blockLines = block2lines.get(i);
				List<String> blockLinesNext = block2lines.get(i+1);
				//i nodi
				Tuple2<Node,Integer> blockNodePrev = block2node.get(i-1);
				Tuple2<Node,Integer> blockNode = block2node.get(i);
				Tuple2<Node,Integer> blockNodeNext = block2node.get(i+1);
				

				System.out.println("________nuovi blocchi_________");
				for(int j=0;j<blockLinesPrev.size();j++) {
					String line = blockLinesPrev.get(j);
					System.out.println(line);
				}
				System.out.println("--");
				for(int j=0;j<blockLines.size();j++) {
					String line = blockLines.get(j);
					System.out.println(line);
				}
				
				if (blockLinesNext != null && calculateDensity(blockLinesPrev) == calculateDensity(blockLinesNext)
						&& calculateDensity(blockLines) < calculateDensity(blockLinesPrev)) {
					blockLinesPrev.addAll(blockLines);
					blockLinesPrev.addAll(blockLinesNext);
					//FONDI I TRE BLOCCHI e poi fai put
					Tuple2<Node,Integer> ancestorTemp = findLowestCommonAncestor(blockNodePrev,blockNode);

					Tuple2<Node,Integer> ancestor = findLowestCommonAncestor(ancestorTemp,blockNodeNext);


					//CONTROLLO FINALE PRIMA DELL'INSERIMENTO:
					//se l'ancestore ottenuto ha una differenza di profondità
					//con uno dei due blocchi (facciamo quello con profondità minore...)
					//che NON supera una certa soglia (per ora direi che l'ancestore non deve
					//avere profondità [0-3])
					//allora puoi fondere i blocchi
					if (ancestor._2() > 3) {

						block2lines.put((i+1), blockLinesPrev);
						block2node.put((i+1), ancestor);


						block2lines.remove(i-1);
						block2lines.remove(i);
						//RIMUOVI I DUE NODI
						block2node.remove(i-1);
						block2node.remove(i);
						i++;
						loop = true;
					}
				}
				else {
					int delta = calculateDifference(calculateDensity(blockLinesPrev), calculateDensity(blockLines));
					if (delta < parameter) {
						System.out.println("entrato nel caso delta");
						//FONDI I due BLOCCHI e poi fai put

						//					System.out.println("VEDIAMO SE RIUSCIAMO A BECCARE L'ANCESTORE!");
						//					System.out.println(blockNodePrev);
						//					System.out.println(blockNode);


						Tuple2<Node,Integer> ancestor = findLowestCommonAncestor(blockNodePrev,blockNode);
						//					System.out.println(ancestor.toString().substring(0, 100));
						

						//CONTROLLO FINALE PRIMA DELL'INSERIMENTO:
						//se l'ancestore ottenuto ha una differenza di profondità
						//con uno dei due blocchi (facciamo quello con profondità minore...)
						//che NON supera una certa soglia (per ora direi che l'ancestore non deve
						//avere profondità [0-3])
						//allora puoi fondere i blocchi
						
						System.out.println(ancestor._2());
						

						
						if (ancestor._2() > 3) {
							System.out.println("è possibile fonderli");


							blockLinesPrev.addAll(blockLines);
							
							block2lines.put(i, blockLinesPrev);
							block2node.put(i, ancestor);

							block2lines.remove(i-1);
							block2node.remove(i-1);

							loop = true;
							
							for(int j=0;j<blockLinesPrev.size();j++) {
								String line = blockLinesPrev.get(j);
								System.out.println(line);
							}
							
						}
					System.out.println();
					}
					
				}
			}
			//qui tolgo i buchi della mappa...
			block2lines = removeGaps(block2lines, dimensioneOriginaria);
			block2node = removeGapsNodes(block2node, dimensioneOriginaria);
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

		for (int i=0; i<block2node.size();i++) {
			if (block2node.get(i) != null) {
				Tuple2<Node, Integer> node = block2node.get(i);
				if (node._2() > 1) textPrinter.println(node);
				textPrinter.println("_________________");
			}
		}
		textPrinter.close();
	}


	public static int calculateDensity(List<String> lines) {
		int tokens = 0;
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

	public static Map<Integer,Tuple2<Node,Integer>> removeGapsNodes(Map<Integer,Tuple2<Node,Integer>> map, int dim) {
		Map<Integer,Tuple2<Node,Integer>> newMap = new HashMap<>();
		int j=0;
		for (int i=0; i<dim; i++) {
			if (map.get(i) != null) {
				newMap.put(j, map.get(i));
				j++;
			}
		}
		return newMap;
	}

	public static Tuple2<Node,Integer> findLowestCommonAncestor(Tuple2<Node,Integer> ta, Tuple2<Node,Integer> tb)
			throws FileNotFoundException, IOException {
		Tuple2<Node,Integer> ancestor = null;

		Node a = ta._1();
		Node b = tb._1();

		int depthA = ta._2();
		//		System.out.println("profondità di a " + depthA);

		int depthB = tb._2();
		//		System.out.println("profondità di b " + depthB);


		if (depthA > depthB) {
			//			System.out.println("sono in uno dei minori");
			//a si trova più in basso, quindi va fatto salire
			//prendi il parent con la stessa profondità e usa quello come nodo di confronto
			boolean stillBasso = true;

			while (stillBasso) {
				a = a.parent();
				depthA--;
				//per il caso in cui si stanno confrontando parent e figlio
				if (a.equals(b)) {
//					System.out.println("caso speciale parent-figlio!");
					ancestor = new Tuple2<Node,Integer>(b,depthB);
					return ancestor;
				}
				if (depthA == depthB) {
					stillBasso = false;
				}
			}
		}
		else if (depthB > depthA) {
			//			System.out.println("sono in uno dei minori B");
			//stessa cosa ma con b
			boolean stillBasso = true;

			while (stillBasso) {
				b = b.parent();
				depthB--;
				if (b.equals(a)) {
					//					System.out.println("caso speciale parent-figlio!");
					ancestor = new Tuple2<Node,Integer>(a,depthA);
					return ancestor;
				}
				if (depthA == depthB) {
					stillBasso = false;
				}
			}
		}
		//ora per forza hanno la stessa profondità
		if (depthA == depthB) {
			if (a.parent().equals(b.parent())) {
				//				System.out.println("caso base!");
				ancestor = new Tuple2<Node,Integer>(a.parent(),depthA-1);
			}
			else {
				//				System.out.println("rilancio...");
				//				if (a.parent().toString().length()>100) System.out.println(a.parent().toString().substring(0, 100));
				//				else System.out.println(a.parent());
				//				if (b.parent().toString().length()>100) System.out.println(b.parent().toString().substring(0, 100));
				//				else System.out.println(b.parent());
				ancestor = findLowestCommonAncestor(new Tuple2<Node,Integer>(a.parent(),depthA-1),
						new Tuple2<Node,Integer>(b.parent(),depthB-1));
			}
			//controlla il parent
			//se hanno parent diverso, rilancia trova l'ancestore del parent e restituiscilo
			//se il parent è uguale (caso base) allora restituiscilo
		}
		return ancestor;
	}
}
