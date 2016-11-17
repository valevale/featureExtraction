package segmentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;
import org.xml.sax.SAXException;

import scala.Tuple2;

public class WebPageSegmentation {

	final static double parameter = -1;

	public static List<Node> segment(Document doc) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException {

		Element body = doc.body();

		List<Tuple2<Node,Integer>> blocks = new ArrayList<>();

		/*percorro l'elemento e ottengo tutti i nodi foglia di testo
		 * questi sono i blocchi elementari*/
		body.traverse(new NodeVisitor() {
			public void head(Node node, int depth) {
				if (node.childNodeSize() == 0 && node.nodeName().equals("#text")
						&& !node.toString().equals(" ")
						&& !node.toString().equals("")
						&& !cercaAncestore(node, "script")) {
					Tuple2<Node,Integer> t = new Tuple2<Node,Integer>(node,depth);
					blocks.add(t);
				}
			}
			public void tail(Node node, int depth) {
			}
		});

		Map<Integer,List<String>> block2lines = new HashMap<>();
		Map<Integer,Tuple2<Node,Integer>> block2node = new HashMap<>();

		/*si processa ogni blocco e si aggiungono linee e nodi in rispettive mappe
		 * ogni mappa serve per uno scopo diverso, per determinare le condizioni
		 * di fusione di due blocchi
		 * block2lines permette di determinare la densità del testo
		 * block2node permette di fare ragionamenti con la struttura ad albero dell'html */
		for(int i=0;i<blocks.size();i++) {
			Tuple2<Node,Integer> t = blocks.get(i);
			Node block = t._1();
			List<String> lines = new ArrayList<>();
			String blockToSeparate = block.toString();
			while (blockToSeparate.length()>40) {
				lines.add(blockToSeparate.substring(0, 40));
				blockToSeparate = blockToSeparate.substring(40, blockToSeparate.length());
			}
			lines.add(blockToSeparate);
			block2lines.put(i, lines);
			block2node.put(i, t);
		}


		/*algoritmo di fusione dei blocchi*/
		boolean loop = true;
		while (loop) {
			loop = false;
			int dimensioneOriginaria = block2lines.size();
			for (int i=1; i<dimensioneOriginaria; i++) {
				List<String> blockLinesPrev = block2lines.get(i-1);
				List<String> blockLines = block2lines.get(i);
				List<String> blockLinesNext = block2lines.get(i+1);
				Tuple2<Node,Integer> blockNodePrev = block2node.get(i-1);
				Tuple2<Node,Integer> blockNode = block2node.get(i);
				Tuple2<Node,Integer> blockNodeNext = block2node.get(i+1);

				//				System.out.println((blockNodePrev.toString().length() < 200) ? blockNodePrev : blockNodePrev.toString().substring(0, 200)+", "+blockNodePrev._2());
				//				System.out.println((blockNode.toString().length() < 200) ? blockNode : blockNode.toString().substring(0, 200)+", "+blockNode._2());
				//				if (blockNodeNext!=null) System.out.println((blockNodeNext.toString().length() < 200) ? blockNodeNext : blockNodeNext.toString().substring(0, 200)+", "+blockNodeNext._2());

				//fusione smooth
				if (blockLinesNext != null && calculateDensity(blockLinesPrev) == calculateDensity(blockLinesNext)
						&& calculateDensity(blockLines) < calculateDensity(blockLinesPrev)) {

					//					System.out.println("SONO NELLA FUSIONE SMOOTH");

					Tuple2<Node,Integer> ancestorTemp = findLowestCommonAncestor(blockNodePrev,blockNode);

					Tuple2<Node,Integer> ancestor = findLowestCommonAncestor(ancestorTemp,blockNodeNext);


					Tuple2<Node,Integer> highest;

					if (blockNodePrev._2() > blockNode._2()) highest = blockNodePrev;
					else highest = blockNode;

					if ( blockNodeNext._2() > highest._2()) highest = blockNodeNext;

					//					System.out.println("HIGHEST" + ((highest.toString().length() < 200) ? highest : highest.toString().substring(0, 200))+", "+highest._2());
					//					System.out.println("ANCESTOR" + ((ancestor.toString().length() < 200) ? ancestor : ancestor.toString().substring(0, 200))+", "+ancestor._2());

					if (highest._2() - ancestor._2() <= 2) {

						//						System.out.println("POSSO FONDERLI");

						blockLinesPrev.addAll(blockLines);
						blockLinesPrev.addAll(blockLinesNext);

						block2lines.put((i+1), blockLinesPrev);
						block2node.put((i+1), ancestor);


						block2lines.remove(i-1);
						block2lines.remove(i);

						block2node.remove(i-1);
						block2node.remove(i);

						i++;
						loop = true;
					}
				}
				else {
					//int delta = calculateDifference(calculateDensity(blockLinesPrev), calculateDensity(blockLines));
					//					System.out.println("NO SMOOTH");
					int delta = Math.abs(calculateDensity(blockLinesPrev) - calculateDensity(blockLines));

					//fusione standard
					if (delta < parameter) {

						//						System.out.println("SONO ENTRATO NELLO STANDARD");

						Tuple2<Node,Integer> ancestor = findLowestCommonAncestor(blockNodePrev,blockNode);

						Tuple2<Node,Integer> highest;

						if (blockNodePrev._2() > blockNode._2()) highest = blockNodePrev;
						else highest = blockNode;


						//						System.out.println("HIGHEST" + ((highest.toString().length() < 200) ? highest : highest.toString().substring(0, 200))+", "+highest._2());
						//						System.out.println("ANCESTOR" + ((ancestor.toString().length() < 200) ? ancestor : ancestor.toString().substring(0, 200))+", "+ancestor._2());

						if (highest._2() - ancestor._2() <= 2) {

							//							System.out.println("POSSO FONDERLI");

							blockLinesPrev.addAll(blockLines);

							block2lines.put(i, blockLinesPrev);
							block2node.put(i, ancestor);

							block2lines.remove(i-1);
							block2node.remove(i-1);

							loop = true;
						}
					}

				} // end else
				//				System.out.println("-----");
				//				System.out.println("");
			} //end for
			block2lines = removeGaps(block2lines, dimensioneOriginaria);
			block2node = removeGapsNodes(block2node, dimensioneOriginaria);
		}
		//		System.out.println("**************************************************");

		//stampiamo i risultati
		//linee a console
		//		for (int i=0; i<block2lines.size();i++) {
		//			if (block2lines.get(i) != null) {
		//				List<String> lines = block2lines.get(i);
		//				System.out.println(i);
		//				for(int j=0;j<lines.size();j++) {
		//					String line = lines.get(j);
		//					System.out.println(line);
		//				}
		//				System.out.println("_________________");
		//			}
		//		}

		List<Node> segments = new ArrayList<>();
		//nodi html nel file di testo
		//xpath a console
		for (int i=0; i<block2node.size();i++) {
			if (block2node.get(i) != null) {
				Tuple2<Node, Integer> node = block2node.get(i);
				//				System.out.println(i);
				//List<String> list = new ArrayList<>();
				//				String xPath = calculateXPath(node._1());
				//				printlist(list);
				//				System.out.println(xPath);
				//				System.out.println();
//				textPrinter.println(i);
//				textPrinter.println(node);
//				textPrinter.println("_________________");

				segments.add(node._1());
			}
		}
		
		
//		
//		textPrinter.println(doc);
//
//		textPrinter.close();

		return segments;
	}


	//data una lista di linee, calcola la densità del testo
	public static int calculateDensity(List<String> lines) {
		int tokens = 0;
		if (lines.size()==1) {
			for (int j=0;j<lines.size();j++) {
				String line = lines.get(j);
				tokens = tokens + calculateTokens(line);
			};
			return (tokens);
		}
		else {
			for (int j=0;j<lines.size()-1;j++) {
				String line = lines.get(j);
				tokens = tokens + calculateTokens(line);
			};
			return (tokens / (lines.size()-1));
		}
	}

	//piccola funzione di supporto per il calcolo del valore assoluto 
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

		int depthB = tb._2();


		if (depthA > depthB) {
			boolean stillBasso = true;

			while (stillBasso) {
				a = a.parent();
				depthA--;
				if (a.equals(b)) {
					ancestor = new Tuple2<Node,Integer>(b,depthB);
					return ancestor;
				}
				if (depthA == depthB) {
					stillBasso = false;
				}
			}
		}
		else if (depthB > depthA) {
			boolean stillBasso = true;

			while (stillBasso) {
				b = b.parent();
				depthB--;
				if (b.equals(a)) {
					ancestor = new Tuple2<Node,Integer>(a,depthA);
					return ancestor;
				}
				if (depthA == depthB) {
					stillBasso = false;
				}
			}
		}
		if (depthA == depthB) {
			if (a.parent().equals(b.parent())) {
				ancestor = new Tuple2<Node,Integer>(a.parent(),depthA-1);
			}
			else {
				ancestor = findLowestCommonAncestor(new Tuple2<Node,Integer>(a.parent(),depthA-1),
						new Tuple2<Node,Integer>(b.parent(),depthB-1));
			}
		}
		return ancestor;
	}

	public static boolean cercaAncestore(Node node, String nodeName) {
		if (node.nodeName().equals(nodeName)) return true;
		if (node.parent() == null) {
			return false;
		}
		else {
			return cercaAncestore(node.parent(), nodeName);
		}
	}

//	/*prende i segmenti, per ognuno dei quali è associata una lista di nodi, e assegna un
//	 * colore, modificando lo style */
//	public static void color(List<Node> segments) throws ParserConfigurationException, SAXException, IOException, TransformerException {
//		segments.forEach(seg -> {
//			if (seg.nodeName().equals("#text"))
//				seg = seg.parent();
//			seg.attr("style", "border:5px solid red");
////			seg.attr("border-color", "red");
//		});
//		//		W3CDom w3cHelper = new W3CDom();
//		//		org.w3c.dom.Document w3cDoc = w3cHelper.fromJsoup(doc);
//		//		
//		//		segments.forEach(segment -> {
//		//			NodeList nl = segment.getNodes();
//		//			for(int i=0; i<nl.getLength();i++) {
//		//				Element e = (Element) nl.item(i);
//		//				e.setAttribute("border-color", "red");
//		//			}
//		//		});
//		//		style="border:5px solid black"
//		//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		//		Transformer transformer = transformerFactory.newTransformer();
//		//		DOMSource source = new DOMSource(w3cDoc);
//		//		StreamResult result = new StreamResult(new File("risultato.html"));
//		//		transformer.transform(source, result);
//	}

}
