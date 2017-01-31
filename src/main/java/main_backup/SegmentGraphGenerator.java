package main_backup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.RelevantInformation;
import scala.Tuple2;

public class SegmentGraphGenerator {

	public static Map<Integer,List<Tuple2<Integer,List<RelevantInformation>>>> getInformations() throws Exception {
		
		SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g = createGraph();

		//ho creato il grafo
		//ora mi prendo i sottografi connessi. sono un insieme di set di vertici
		ConnectivityInspector<RelevantInformation, DefaultWeightedEdge> inspector = new ConnectivityInspector<>(g);
		List<Set<RelevantInformation>> semanticSubGraphList = inspector.connectedSets();

		//		System.out.println("Componenti connesse: "+semanticSubGraphList.size());

		DirectedGraph<RelevantInformation, DefaultWeightedEdge> gDirected = convertUndirectedGraph(g);

		List<Tuple2<Integer,List<RelevantInformation>>> maxWeightPaths = new ArrayList<>();
		List<Tuple2<Integer,List<RelevantInformation>>> longestPaths = new ArrayList<>();

		Map<Integer,List<Tuple2<Integer,List<RelevantInformation>>>> result = new HashMap<>();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("creazione grafo: "+timestamp);
		//per ogni sottografo
		for (int i=0; i<semanticSubGraphList.size(); i++) {
			Set<RelevantInformation> currentSemanticSubGraph = semanticSubGraphList.get(i);

			System.out.println("dimensione sottografo "+(i+1)+": "+currentSemanticSubGraph.size());

			List<GraphPath<RelevantInformation,DefaultWeightedEdge>> transversalPaths = selectTransversalPaths(
					currentSemanticSubGraph, gDirected);

			//**ALGORITMO PIU' PESANTE
			//lista che contiene le due path più pesanti
			//			List<GraphPath<SegmentGraphNode,DefaultWeightedEdge>> maxWeightPaths = new ArrayList<>();
			//prima path
			GraphPath<RelevantInformation,DefaultWeightedEdge> firstMaxWeightedPath = getMaxWeightedPath(
					transversalPaths, g);
			List<RelevantInformation> usedVertexForPaths = firstMaxWeightedPath.getVertexList();
			maxWeightPaths.add(new Tuple2<Integer,List<RelevantInformation>>(1,firstMaxWeightedPath.getVertexList()));
			//individuo i nodi appartenenti al cammino
			//possibilità:
			//-creiamo un nuovo grafo senza quei nodi
			//-rigeneriamo i cammini (o meglio li riprendo, non li ricalcolo, spezzando il codice)
			//e NON prendo i cammini che contengono anche un solo nodo di quelli
			//proviamo con questa seconda strada
			//cerchiamo altre 5 paths
			int pathRaccolte = 1;
			boolean ancoraAltriCammini = true;
			while (pathRaccolte <=5 && ancoraAltriCammini){
				List<GraphPath<RelevantInformation,DefaultWeightedEdge>> transversalPathsFiltered = new ArrayList<>();
				for (int j=0;j<transversalPaths.size();j++) {
					GraphPath<RelevantInformation,DefaultWeightedEdge> currentPath = transversalPaths.get(j);
					List<RelevantInformation> vertexOfCurrentPath = currentPath.getVertexList();
					//verifica se currentPath e firstMaxWeightedPath hanno nodi in comune
					boolean vertexInCommon = false;
					for (int k=0; k<vertexOfCurrentPath.size() && !vertexInCommon; k++) {
						if (usedVertexForPaths.contains(vertexOfCurrentPath.get(k)))
							vertexInCommon = true;
					}
					if (!vertexInCommon)
						transversalPathsFiltered.add(currentPath);
				}
				//seleziono quello massimo, secondo cammino
				if (!transversalPathsFiltered.isEmpty()) {
					pathRaccolte++;
					GraphPath<RelevantInformation,DefaultWeightedEdge> newMaxWeightedPath = getMaxWeightedPath(
							transversalPathsFiltered, g);
					List<RelevantInformation> vertexOfNewMaxWeightedPath = newMaxWeightedPath.getVertexList();
					maxWeightPaths.add(new Tuple2<Integer,List<RelevantInformation>>((pathRaccolte),vertexOfNewMaxWeightedPath));
					usedVertexForPaths.addAll(vertexOfNewMaxWeightedPath);
				}
				else {
					ancoraAltriCammini = false;
				}
			}
			
			
		}

		timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("weight: "+timestamp);

		//per ogni sottografo
		for (int i=0; i<semanticSubGraphList.size(); i++) {
			Set<RelevantInformation> currentSemanticSubGraph = semanticSubGraphList.get(i);
			//**ORA FACCIAMO L'ALGORITMO DEL PIU' LUNGO
			List<GraphPath<RelevantInformation,DefaultWeightedEdge>> transversalPaths = selectTransversalPaths(
					currentSemanticSubGraph, gDirected);
			//			List<GraphPath<SegmentGraphNode,DefaultWeightedEdge>> longestPaths = new ArrayList<>();
			//prima path
			GraphPath<RelevantInformation,DefaultWeightedEdge> firstLongestPath = getLongestPath(transversalPaths, g);
			List<RelevantInformation> usedVertexForPaths = firstLongestPath.getVertexList();
			longestPaths.add(new Tuple2<Integer,List<RelevantInformation>>(1,firstLongestPath.getVertexList()));
			//individuo i nodi appartenenti al cammino
			//possibilità:
			//-creiamo un nuovo grafo senza quei nodi
			//-rigeneriamo i cammini (o meglio li riprendo, non li ricalcolo, spezzando il codice)
			//e NON prendo i cammini che contengono anche un solo nodo di quelli
			//proviamo con questa seconda strada
//			List<GraphPath<SegmentGraphNode,DefaultWeightedEdge>> transversalPathsFiltered = new ArrayList<>();
//			for (int j=0;j<transversalPaths.size();j++) {
//				GraphPath<SegmentGraphNode,DefaultWeightedEdge> currentPath = transversalPaths.get(j);
//				List<SegmentGraphNode> vertexOfCurrentPath = currentPath.getVertexList();
//				//verifica se currentPath e firstMaxWeightedPath hanno nodi in comune
//				boolean vertexInCommon = false;
//				for (int k=0; k<vertexOfCurrentPath.size() && !vertexInCommon; k++) {
//					if (vertexOfFirstLongestPath.contains(vertexOfCurrentPath.get(k)))
//						vertexInCommon = true;
//				}
//				if (!vertexInCommon) 
//					transversalPathsFiltered.add(currentPath);
//			}
//			//seleziono quello massimo
//			if (!transversalPathsFiltered.isEmpty()) {
//				GraphPath<SegmentGraphNode,DefaultWeightedEdge> secondLongestPath = getLongestPath(
//						transversalPathsFiltered);
//				List<SegmentGraphNode> vertexOfSecondLongestPath = secondLongestPath.getVertexList();
//				longestPaths.add(new Tuple2<Integer,List<SegmentGraphNode>>(2,vertexOfSecondLongestPath));
//			}
			int pathRaccolte = 1;
			boolean ancoraAltriCammini = true;
			while (pathRaccolte <=5 && ancoraAltriCammini){
				List<GraphPath<RelevantInformation,DefaultWeightedEdge>> transversalPathsFiltered = new ArrayList<>();
				for (int j=0;j<transversalPaths.size();j++) {
					GraphPath<RelevantInformation,DefaultWeightedEdge> currentPath = transversalPaths.get(j);
					List<RelevantInformation> vertexOfCurrentPath = currentPath.getVertexList();
					//verifica se currentPath e firstMaxWeightedPath hanno nodi in comune
					boolean vertexInCommon = false;
					for (int k=0; k<vertexOfCurrentPath.size() && !vertexInCommon; k++) {
						if (usedVertexForPaths.contains(vertexOfCurrentPath.get(k)))
							vertexInCommon = true;
					}
					if (!vertexInCommon)
						transversalPathsFiltered.add(currentPath);
				}
				//seleziono quello massimo, secondo cammino
				if (!transversalPathsFiltered.isEmpty()) {
					pathRaccolte++;
					GraphPath<RelevantInformation,DefaultWeightedEdge> newLongestPath = getLongestPath(
							transversalPathsFiltered, g);
					List<RelevantInformation> vertexOfNewMaxWeightedPath = newLongestPath.getVertexList();
					longestPaths.add(new Tuple2<Integer,List<RelevantInformation>>((pathRaccolte),vertexOfNewMaxWeightedPath));
					usedVertexForPaths.addAll(vertexOfNewMaxWeightedPath);
				}
				else {
					ancoraAltriCammini = false;
				}
			}
			
		}

		timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("longest: "+timestamp);

		List<Tuple2<Integer,List<RelevantInformation>>> minCutPaths = new ArrayList<>();

		//per ogni sottografo
		for (int i=0; i<semanticSubGraphList.size(); i++) {
			Set<RelevantInformation> currentSemanticSubGraph = semanticSubGraphList.get(i);

			Set<RelevantInformation> path = minCutAlg(currentSemanticSubGraph, g);

			List<RelevantInformation> listVertex = new ArrayList<>(path);

			minCutPaths.add(new Tuple2<Integer,List<RelevantInformation>>(1,listVertex));
		}

		timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("mincut: "+timestamp);

		result.put(1, maxWeightPaths);
		result.put(2, longestPaths);
		result.put(3, minCutPaths);
		//cosa facciamo ritornare?
		//i soli vertici
		//		return new Tuple2<List<List<SegmentGraphNode>>,List<List<SegmentGraphNode>>>(maxWeightPaths,longestPaths);
		return result;
	}

	public static Set<RelevantInformation> minCutAlg(Set<RelevantInformation> currentSemanticSubGraph,
			SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g) {
		//		System.out.println("nuova chiamata");

		Set<RelevantInformation> transversalSet = currentSemanticSubGraph;

		Tuple2<RelevantInformation,RelevantInformation> conflict = getConflict(transversalSet);

		ConnectivityInspector<RelevantInformation, DefaultWeightedEdge> subInspector = new ConnectivityInspector<>(g);
		List<Set<RelevantInformation>> newConnectedComponents = subInspector.connectedSets();
		//		System.out.println("componenti connesse di partenza: "+newConnectedComponents.size());

		//queste 2 variabili non so se vanno bene qui, ma ci proviamo

		while (conflict != null) {
			//			System.out.println("nuovo ciclo");
			//taglio
			//			System.out.println("dimensione transversal set "+transversalSet.size());
			//**FORD-FULKERSON
			MinimumSTCutAlgorithm<RelevantInformation, DefaultWeightedEdge> minCutAlg = 
					new EdmondsKarpMFImpl<RelevantInformation, DefaultWeightedEdge>(g,	MaximumFlowAlgorithmBase.DEFAULT_EPSILON);

			minCutAlg.calculateMinCut(conflict._1(), conflict._2());

			//ora conosco il taglio
			Set<DefaultWeightedEdge> cutEdges = minCutAlg.getCutEdges();

			//rimuovo quegli archi
			g.removeAllEdges(cutEdges);
			//			System.out.println("il taglio è andato a buon fine?"+fatto);

			//ora questo sottografo è composto da più componenti connesse, le individuo
			subInspector = new ConnectivityInspector<>(g);
			newConnectedComponents = subInspector.connectedSets();

			//			System.out.println("nuove componenti connesse: "+newConnectedComponents.size());

			//da qui funziona


			//individuo le sole componenti connesse che mi interessano:
			//quelle che contengono i nodi di currentSemanticSubGraph
			List<Set<RelevantInformation>> connectedComponentsOfCurrentSemanticSubgraph = 
					getOnlyCCOfCurrentSemanticSubgraph(currentSemanticSubGraph, newConnectedComponents);


			//			System.out.println("insiemi che hanno i vecchi nodi: "+connectedComponentsOfCurrentSemanticSubgraph.size());

			//ipotesi
			//prendo la componente connessa più grande
			transversalSet = getBiggestCC(connectedComponentsOfCurrentSemanticSubgraph);

			//ripeto
			conflict = getConflict(transversalSet);
			//			return minCutRecAlg(biggestCC,gDirected);
		}
		//caso base: no conflitti
		//		else {
		//			return currentSemanticSubGraph;
		//		}
		//		System.out.println("SONO USCITO!");
		return transversalSet;
	}
	
	public static SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> createGraph() throws Exception {

		Map<Tuple2<Integer,Integer>,Set<PairMatching>> matchings = PairMatchingMaker.getMainMatchings();


		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("preparazione: "+timestamp);

		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();
		//creo il grafo
		SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//faccio un repository alla volta
		for(int k=1;k<=4;k++) {
			for (int k2=k+1;k2<=5;k2++) {
				int domain1 = k;
				int domain2 = k2;

				PairMatchingRepository currentRepository = pmr.getRepository(domain1, domain2);
				Map<PairMatching,Float> matchings2votes = currentRepository.getMatchings2vote();

				Set<PairMatching> currentSet = matchings.get(new Tuple2<>(domain1,domain2));
				Iterator<PairMatching> matchIt = currentSet.iterator();
				while(matchIt.hasNext()) {
					PairMatching currentPair = matchIt.next();
					//					if (currentPair.getXpath1().getXpath().equals(
					//							"//html[1]/body[1]/div[1]/div[3]/div[1]/a[2]")
					//							&& domain1==1) {
					//						System.out.println("1 ECCOLO!");
					//						System.out.println("Sta con "+domain2+" "+currentPair.getXpath2().getXpath());
					//						
					//					}
					//					if (currentPair.getXpath1().getXpath().equals(
					//							"//html[1]/body[1]/div[3]/div[2]/div[1]/div[1]/ul[1]/li[1]/span[1]/a[1]")
					//							&& domain1==4) {
					//						System.out.println("4 ECCOLO!");
					//						System.out.println("Sta con "+domain2+" "+currentPair.getXpath2().getXpath());
					//						
					//					}
					//creo i nodi
					RelevantInformation node1 = new RelevantInformation(domain1, currentPair.getXpath1());
					RelevantInformation node2 = new RelevantInformation(domain2, currentPair.getXpath2());

					//controllo che il vertice non esista già
					//se non esiste lo metto nel grafo
					if (g.containsVertex(node1))
						node1 = getVertex(g, node1);
					else
						g.addVertex(node1);

					if (g.containsVertex(node2))
						node2 = getVertex(g, node2);
					else
						g.addVertex(node2);

					DefaultWeightedEdge e = g.addEdge(node1, node2); 

					float voto = matchings2votes.get(currentPair);

					g.setEdgeWeight(e, voto);


				}
			}
		}

		System.out.println("dimensione grafo: "+g.vertexSet().size());
		return g;
	}

	public static Set<RelevantInformation> getBiggestCC(List<Set<RelevantInformation>> connectedComponents) {
		Set<RelevantInformation> biggestCC = connectedComponents.get(0);
		for (int i=1;i<connectedComponents.size();i++) {
			Set<RelevantInformation> currentCC = connectedComponents.get(i);
			if (currentCC.size() > biggestCC.size()) {
				biggestCC = currentCC;
			}
		}
		return biggestCC;
	}

	public static List<Set<RelevantInformation>> getOnlyCCOfCurrentSemanticSubgraph(Set<RelevantInformation> currentSemanticSubGraph,
			List<Set<RelevantInformation>> newConnectedComponents) {
		//filtro le componenti connesse che non contengono neanche un nodo del sottografo semantico
		//scorro tutte le componenti connesse
		List<Set<RelevantInformation>> connectedComponentOfCurrentSemanticSubgraph = new ArrayList<>();

		for (int i=0;i<newConnectedComponents.size();i++) {
			//componente connessa corrente
			Set<RelevantInformation> currentCC = newConnectedComponents.get(i);
			//se l'itersezione con il sottografo semantico è maggiore di zero, allora prendo la componente
			Set<RelevantInformation> intersection = new HashSet<>(currentCC);
			intersection.retainAll(currentSemanticSubGraph);
			//			System.out.println("inters numero "+(i+1)+": "+intersection.size());
			if (intersection.size() > 0) {
				connectedComponentOfCurrentSemanticSubgraph.add(currentCC);
			}
		}
		return connectedComponentOfCurrentSemanticSubgraph;
	}

	/* di tutti i cammini, prendo quello di lunghezza massima (numero di archi maggiore)
	 * a parità di peso, prendo il cammino di peso massimo (somma del peso degli archi massima)
	 * a parità, ne prendo uno qualsiasi*/
	private static GraphPath<RelevantInformation,DefaultWeightedEdge> getLongestPath(
			List<GraphPath<RelevantInformation,DefaultWeightedEdge>> allTransversalPaths,
			SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g) {
		//inizializziamo con il primo elemento
		GraphPath<RelevantInformation,DefaultWeightedEdge> longestPath = allTransversalPaths.get(0);
		for (int i=0;i<allTransversalPaths.size();i++) {
			GraphPath<RelevantInformation,DefaultWeightedEdge> currentPath = allTransversalPaths.get(i);
			if (currentPath.getLength() > longestPath.getLength()) {
				longestPath = currentPath;
			}
			if (currentPath.getLength() == longestPath.getLength()) {
				double currentPathWeight = getWeight(currentPath, g);
				double currentLongestPathWeight = getWeight(longestPath, g);
				if (currentPathWeight > currentLongestPathWeight) {
					longestPath = currentPath;
				}
			}
		}
		return longestPath;
	}

	/* di tutti i cammini, prendo quello di peso massimo (somma del peso degli archi massima)
	 * a parità di peso, prendo il cammino più lungo (numero di archi maggiore)
	 * a parità, ne prendo uno qualsiasi*/
	private static GraphPath<RelevantInformation,DefaultWeightedEdge> getMaxWeightedPath(
			List<GraphPath<RelevantInformation,DefaultWeightedEdge>> allTransversalPaths, 
			SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g) {
		//inizializziamo con il primo elemento
		GraphPath<RelevantInformation,DefaultWeightedEdge> maxWeightedPath = allTransversalPaths.get(0);
		for (int i=1;i<allTransversalPaths.size();i++) {
			GraphPath<RelevantInformation,DefaultWeightedEdge> currentPath = allTransversalPaths.get(i);
			double currentPathWeight = getWeight(currentPath, g);
			double currentMaxPathWeight = getWeight(maxWeightedPath, g);
			System.out.println("current "+currentPath.getWeight()+" & max: "+maxWeightedPath.getWeight());
			System.out.println("currentNEW "+currentPathWeight+" & maxNEW: "+currentMaxPathWeight);
			if (currentPathWeight > currentMaxPathWeight) {
				maxWeightedPath = currentPath;
			}
			if (currentPathWeight == currentMaxPathWeight) {
				System.out.println("currentL "+currentPath.getLength()+" & maxL: "+maxWeightedPath.getLength());
				if (currentPath.getLength() > maxWeightedPath.getLength()) {
					maxWeightedPath = currentPath;
				}
			}
		}
		return maxWeightedPath;
	}
	
	public static double getWeight(GraphPath<RelevantInformation,DefaultWeightedEdge> path,
			SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g) {
		double w = 0;
		
		List<DefaultWeightedEdge> edges = path.getEdgeList();
		
		for (int i=0;i<edges.size();i++) {
			DefaultWeightedEdge currentEdge = edges.get(i);
			w = w + g.getEdgeWeight(currentEdge);
		}
		
		return w;
	}

	private static List<GraphPath<RelevantInformation,DefaultWeightedEdge>> selectTransversalPaths(
			Set<RelevantInformation> currentSemanticSubGraph, 
			DirectedGraph<RelevantInformation, DefaultWeightedEdge> gDirected) {
		//TODO magari puoi dividere i passaggi in più metodi
		//ottengo tutti i cammini di lunghezza massimo 4
		AllDirectedPaths<RelevantInformation, DefaultWeightedEdge> directedPathsGenerator =
				new AllDirectedPaths<RelevantInformation, DefaultWeightedEdge>(gDirected);

		//NOTA qui teoricamente i cammini che si creano POTREBBERO contenere nodi non appartenenti
		//all'insieme di partenza e arrivo
		//tuttavia ciò che gli forniamo è una componente connessa massimale, quindi
		//non verranno inclusi nodi al di fuori di loro
		List<GraphPath<RelevantInformation,DefaultWeightedEdge>> allPaths = directedPathsGenerator.getAllPaths(
				currentSemanticSubGraph, currentSemanticSubGraph, true, 4);

		//da questi inizio a togliere quelli che contengono due vertici con la stessa etichetta dominio
		//TODO forse è meglio se per ora fai una mappa co ste liste e poi, fuori dal for, fai sto lavoro
		//meno oneroso?

		List<GraphPath<RelevantInformation,DefaultWeightedEdge>> transversalPaths =
				new ArrayList<>();

		for (int j=0;j<allPaths.size();j++) {
			GraphPath<RelevantInformation,DefaultWeightedEdge> currentPath = allPaths.get(j);
			List<RelevantInformation> vertexList = currentPath.getVertexList();
			List<Integer> visitedDomains = new ArrayList<>();
			boolean isTransversal = true;
			for (int k=0;k<vertexList.size() && isTransversal;k++) {
				RelevantInformation currentVertex = vertexList.get(k);
				int currentDomain = currentVertex.getDomain();
				if (visitedDomains.contains(currentDomain)) {
					isTransversal = false;
				}
				visitedDomains.add(currentDomain);
			}
			if (isTransversal)
				transversalPaths.add(currentPath);
		}

		//ora selezioni
		return transversalPaths;
	}

	private static DirectedGraph<RelevantInformation, DefaultWeightedEdge> convertUndirectedGraph(
			SimpleWeightedGraph<RelevantInformation, DefaultWeightedEdge> g) {
		DirectedMultigraph<RelevantInformation, DefaultWeightedEdge> gDirected = 
				new DirectedMultigraph<RelevantInformation, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//aggiungo tutti i nodi del grafo di partenza a quello di arrivo
		Iterator<RelevantInformation> nodesIt = g.vertexSet().iterator();
		while (nodesIt.hasNext()) {
			RelevantInformation currentNode = nodesIt.next();
			gDirected.addVertex(currentNode);
		}

		//per ogni vertice del grafo di partenza, ne aggiungo due in quello di arrivo
		Iterator<DefaultWeightedEdge> edgesIt = g.edgeSet().iterator();
		while (edgesIt.hasNext()) {
			DefaultWeightedEdge currentEdge = edgesIt.next();

			DefaultWeightedEdge we1 = gDirected.addEdge(g.getEdgeSource(currentEdge),
					g.getEdgeTarget(currentEdge));
			gDirected.setEdgeWeight(we1, g.getEdgeWeight(currentEdge));

			DefaultWeightedEdge we2 = gDirected.addEdge(g.getEdgeTarget(currentEdge),
					g.getEdgeSource(currentEdge));
			gDirected.setEdgeWeight(we2, g.getEdgeWeight(currentEdge));
		}

		return gDirected;
	}

	private static RelevantInformation getVertex(UndirectedGraph<RelevantInformation, DefaultWeightedEdge> g,
			RelevantInformation n) {
		Iterator<RelevantInformation> nodesIt = g.vertexSet().iterator();
		while (nodesIt.hasNext()) {
			RelevantInformation currentNode = nodesIt.next();
			if (n.equals(currentNode))
				return currentNode;
		}
		return null;
	}

	public static RelevantInformation containsXpath(Set<RelevantInformation> currentSemanticSubGraph,String xpath) {
		Iterator<RelevantInformation> it = currentSemanticSubGraph.iterator();
		while(it.hasNext()) {
			RelevantInformation n = it.next();
			if (n.getXpath().getXpath().equals(xpath))
				return n;
		}
		return null;
	}

	public static Tuple2<RelevantInformation,RelevantInformation> getConflict(Set<RelevantInformation> nodes) {

		Map<Integer,RelevantInformation> domain2node = new HashMap<>();

		Iterator<RelevantInformation> it = nodes.iterator();
		while(it.hasNext()) {
			RelevantInformation currentNode = it.next();
			int currentDomain = currentNode.getDomain();
			if (!domain2node.containsKey(currentDomain)) {
				domain2node.put(currentDomain, currentNode);
			}
			else {
				RelevantInformation conflictNode = domain2node.get(currentDomain);
				return new Tuple2<RelevantInformation,RelevantInformation>(conflictNode,currentNode);
			}
		}
		return null;
	}
}
