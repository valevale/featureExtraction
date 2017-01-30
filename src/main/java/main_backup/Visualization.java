package main_backup;
/*
 * (C) Copyright 2013-2017, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.xml.xpath.XPathExpressionException;

import org.jgrapht.*;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;

import model.MyWeightEdge;
import model.PairMatching;
import model.PairMatchingRepository;
import model.PairMatchingRepositoryRepository;
import model.RelevantInformation;
import model.WebPageDocument;
import scala.Tuple2;

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on
 * JGraphAdapterDemo.
 *
 * @since July 9, 2013
 */
public class Visualization
extends JApplet
{
	static double parameterTextFusion = -1;
	static String path = "/home/valentina/workspace_nuovo/DataFusion/testGenericXpath/persone/";
	
	private static final long serialVersionUID = 2202072534703043194L;
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

	private JGraphXAdapter<RelevantInformation, MyWeightEdge> jgxAdapter;

	/**
	 * An alternative starting point for this demo, to also allow running this applet as an
	 * application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		
		Visualization applet = new Visualization();
		applet.init();

		JFrame frame = new JFrame();
		frame.getContentPane().add(applet);
		frame.setTitle("JGraphT Adapter to JGraph Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init()
	{
		String d1Folder = path+"p1/";
		
		Map<Integer,WebPageDocument> domain2document = new HashMap<>();

		for(int j=1;j<=5;j++) {
			String currentFolder = d1Folder;
			String dPath = currentFolder+"orig"+j+".html";
			File d = new File(dPath);
			if (d.exists()) {
				WebPageDocument w = null;
				try {
					w = new WebPageDocument(
							new File(path+"p1/"+"orig"+j+".html"), 
							j, path+"p1/", 
							parameterTextFusion, j);
				} catch (Exception e) {
					e.printStackTrace();
				}
				domain2document.put(j, w);
			}
		}
		
		// create a JGraphT graph
		ListenableUndirectedWeightedGraph<RelevantInformation, MyWeightEdge> g =
				new ListenableUndirectedWeightedGraph<RelevantInformation, MyWeightEdge>(MyWeightEdge.class);

		Map<Tuple2<Integer, Integer>, Set<PairMatching>> matchings = null;
		try {
			matchings = PairMatchingMaker.getMainMatchings();
		} catch (Exception e1) {
			e1.printStackTrace();
		}


		PairMatchingRepositoryRepository pmr = PairMatchingRepositoryRepository.getInstance();

		// create a visualization using JGraph, via an adapter


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
					RelevantInformation node1=null;
					try {
						node1 = new RelevantInformation(domain1, currentPair.getXpath1(), domain2document.get(domain1));
					} catch (XPathExpressionException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					RelevantInformation node2=null;
					try {
						node2 = new RelevantInformation(domain2, currentPair.getXpath2(), domain2document.get(domain2));
					} catch (XPathExpressionException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

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

					MyWeightEdge e = g.addEdge(node1, node2); 

					float voto = matchings2votes.get(currentPair);

					g.setEdgeWeight(e, voto);
				}

			}
		}


		//ho creato il grafo

		//prendo un sottografo

		ConnectivityInspector<RelevantInformation, MyWeightEdge> inspector = new ConnectivityInspector<>(g);
		List<Set<RelevantInformation>> semanticSubGraphList = inspector.connectedSets();

		Set<RelevantInformation> nodi = semanticSubGraphList.get(0);

		ListenableUndirectedWeightedGraph<RelevantInformation, MyWeightEdge> s =
				new ListenableUndirectedWeightedGraph<RelevantInformation, MyWeightEdge>(MyWeightEdge.class);

		Iterator<RelevantInformation> it = nodi.iterator();
		while (it.hasNext()) {
			RelevantInformation n = it.next();
			if (s.containsVertex(n))
				n = getVertex(s, n);
			else
				s.addVertex(n);

			Iterator<MyWeightEdge> edgesOfnIt = g.edgesOf(n).iterator();
			while (edgesOfnIt.hasNext()) {
				MyWeightEdge currentEdge = edgesOfnIt.next();

				RelevantInformation source = g.getEdgeSource(currentEdge);
				RelevantInformation target = g.getEdgeTarget(currentEdge);

				if (s.containsVertex(source))
					source = getVertex(s, source);
				else
					s.addVertex(source);

				if (s.containsVertex(target))
					target = getVertex(s, target);
				else
					s.addVertex(target);

				MyWeightEdge existingEdge = s.getEdge(source, target);
				if (existingEdge == null) {
					MyWeightEdge e = s.addEdge(source, target); 

					double voto = g.getEdgeWeight(currentEdge);

					s.setEdgeWeight(e, voto);
				}
			}
		}

		//		jgxAdapter = new JGraphXAdapter<>(g);
		jgxAdapter = new JGraphXAdapter<>(s);

		getContentPane().add(new mxGraphComponent(jgxAdapter));
		resize(DEFAULT_SIZE);

		mxOrganicLayout layout1 = new mxOrganicLayout(jgxAdapter);
		layout1.execute(jgxAdapter.getDefaultParent());
		
//		mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
//		layout.execute(jgxAdapter.getDefaultParent());
//		layout.setRadius(1);

		// that's all there is to it!...
	}

	private static RelevantInformation getVertex(UndirectedGraph<RelevantInformation, MyWeightEdge> g,
			RelevantInformation n) {
		Iterator<RelevantInformation> nodesIt = g.vertexSet().iterator();
		while (nodesIt.hasNext()) {
			RelevantInformation currentNode = nodesIt.next();
			if (n.equals(currentNode))
				return currentNode;
		}
		return null;
	}
}

// End JGraphXAdapterDemo.java