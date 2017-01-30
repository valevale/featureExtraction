package model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class MyWeightEdge extends DefaultWeightedEdge {
		  /**
	 * 
	 */
	private static final long serialVersionUID = 7164716020562770332L;

		/**
	 * 
	 */

		@Override
		  public String toString() {
			double w = getWeight();
			if (w >= 9)
				return Double.toString(getWeight());
			else
				return "";
		  }
}
