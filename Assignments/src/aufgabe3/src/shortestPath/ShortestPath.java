// O. Bittel;
// 25.3.2021; jetzt mit IndexMinPq
// 30.06.2024; Anpassung auf ungerichtete Graphen

package aufgabe3.src.shortestPath;

import aufgabe3.src.sim.SYSimulation;
import aufgabe3.src.undirectedGraph.UndirectedGraph;

import java.util.*;


// ...

/**
 * Kürzeste Wege in Graphen mit A*- und Dijkstra-Verfahren.
 * @author Oliver Bittel
 * @since 30.06.2024
 * @param <V> Knotentyp.
 */
public class ShortestPath<V> {
	
	SYSimulation sim = null;
	Map<V, Double> dist;        // Distanz für jeden Knoten
	Map<V, V> pred;                // Vorgänger für jeden Knoten
	IndexMinPQ<V, Double> cand;    // Kandidaten als PriorityQueue PQ
	Heuristic<V> heur;             // Heuristik
	List<V> path;
	UndirectedGraph<V> graph;
	double distance;


	/**
	 * Konstruiert ein Objekt, das im Graph g k&uuml;rzeste Wege 
	 * nach dem A*-Verfahren berechnen kann.
	 * Die Heuristik h schätzt die Kosten zwischen zwei Knoten ab.
	 * Wird h = null gewählt, dann ist das Verfahren identisch 
	 * mit dem Dijkstra-Verfahren.
	 * @param g Gerichteter Graph
	 * @param h Heuristik. Falls h == null, werden kürzeste Wege nach
	 * dem Dijkstra-Verfahren gesucht.
	 */
	public ShortestPath(UndirectedGraph<V> g, Heuristic<V> h) {
		path = new ArrayList<>();
		dist = new HashMap<>();
		pred = new HashMap<>();
		cand = new IndexMinPQ<>();
		heur = h;
		graph = g;
		distance = 0.0;
	}

	/**
	 * Diese Methode sollte nur verwendet werden, 
	 * wenn kürzeste Wege in Scotland-Yard-Plan gesucht werden.
	 * Es ist dann ein Objekt für die Scotland-Yard-Simulation zu übergeben.
	 * <p>
	 * Ein typische Aufruf für ein SYSimulation-Objekt sim sieht wie folgt aus:
	 * <blockquote><pre>
	 *    if (sim != null)
	 *       sim.visitStation((Integer) v, Color.blue);
	 * </pre></blockquote>
	 * @param sim SYSimulation-Objekt.
	 */
	public void setSimulator(SYSimulation sim) {
		this.sim = sim;
	}

	/**
	 * Sucht den kürzesten Weg von Starknoten s zum Zielknoten g.
	 * <p>
	 * Falls die Simulation mit setSimulator(sim) aktiviert wurde, wird der Knoten,
	 * der als nächstes aus der Kandidatenliste besucht wird, animiert.
	 * @param s Startknoten
	 * @param g Zielknoten
	 *
	 */


	public void searchShortestPath(V s, V g) {
		path.clear();
		cand.clear();
		dist.clear();
		pred.clear();

		for (V v : graph.getVertexSet()) {
			dist.put(v, Double.POSITIVE_INFINITY);
			pred.put(v, null);
		}
		dist.put(s, 0.0);

		if (heur == null) {
			searchShortestPathDijkstra(s, g);
		} else {
			searchShortestPathAStar(s, g);
		}

		distance = dist.get(g);
		V v = g;
		while (v != null) {
			path.addFirst(v);
			v = pred.get(v);
		}

		if (sim != null) {
			for (int i = 0; i < path.size() - 1; i++) {
				sim.drive((Integer) path.get(i), (Integer) path.get(i + 1), java.awt.Color.red);
			}
		}
	}
	private void searchShortestPathDijkstra(V s, V g) {
		cand.add(s, 0.0);

		while (!cand.isEmpty()) {
			V v = cand.removeMin();
			System.out.println("Besuche Knoten " + v + " mit d = " + dist.get(v));
			if (sim != null)
				sim.visitStation((Integer) v, java.awt.Color.blue);
			if (v.equals(g)) {
				return;
			}
			for (V w : graph.getNeighborSet(v)) {
				if (dist.get(w) == Double.POSITIVE_INFINITY) {
					pred.put(w, v);
					dist.put(w, dist.get(v) + graph.getWeight(v, w));
					cand.add(w, dist.get(w));
				} else if (dist.get(v) + graph.getWeight(v, w) < dist.get(w)) {
					pred.put(w, v);
					dist.put(w, dist.get(v) + graph.getWeight(v, w));
					cand.change(w, dist.get(w));
				}
			}
		}
	}
	private boolean searchShortestPathAStar(V s, V g) {

		cand.add(s, 0 + heur.estimatedCost(s, g));

		while (!cand.isEmpty()) {
			V v = cand.removeMin();
			System.out.println("Besuche Knoten " + v + " mit d = " + dist.get(v));
			if (sim != null)
				sim.visitStation((Integer) v, java.awt.Color.blue);
			if (v.equals(g)) {
				return true;
			}
			for (V w : graph.getNeighborSet(v)) {
				if (dist.get(w) == Double.POSITIVE_INFINITY) {
					pred.put(w, v);
					dist.put(w, dist.get(v) + graph.getWeight(v, w));
					cand.add(w, dist.get(w) + heur.estimatedCost(w, g));
				} else if (dist.get(v) + graph.getWeight(v, w) < dist.get(w)) {
					pred.put(w, v);
					dist.put(w, dist.get(v) + graph.getWeight(v, w));
					cand.change(w, dist.get(w) + heur.estimatedCost(w, g));
				}
			}
		}
		return false;
	}



	/**
	 * Liefert einen kürzesten Weg von Startknoten s nach Zielknoten g.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return kürzester Weg als Liste von Knoten.
	 */
	public List<V> getShortestPath() {
		if (pred.isEmpty()) {
			throw new IllegalArgumentException("Kein kürzester Weg berechnet.");
		}
		return path;
	}

	/**
	 * Liefert die Länge eines kürzesten Weges von Startknoten s nach Zielknoten g zurück.
	 * Setzt eine erfolgreiche Suche von searchShortestPath(s,g) voraus.
	 * @throws IllegalArgumentException falls kein kürzester Weg berechnet wurde.
	 * @return Länge eines kürzesten Weges.
	 */
	public double getDistance() {
		if (dist.isEmpty()) {
			throw new IllegalArgumentException("Kein Abstand berechnet.");
		}
		return distance;
	}

}
