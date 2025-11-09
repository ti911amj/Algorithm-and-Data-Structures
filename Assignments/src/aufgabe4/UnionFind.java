package aufgabe4;

// O. Bittel
// 20.02.2025

// package TelNetApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Klasse für Union-Find-Strukturen.
 * Unterstützt die effiziente Verwaltung einer Partionierung einer Menge
 * (disjunkte Zerlegung in Teilmengen).
 *
 * Union-Find-Struktur mit Pfadkompression und Union-by-Rank.
 *
 * Im Durchschnitt haben unionByRank und findWithCompression praktisch eine Laufzeit von O(1).
 *
 * @author Oliver Bittel
 * @since 24.01.2025
 */
public class UnionFind<T> {
	// ...

	private final Map<T, T> parent = new HashMap<>();
	private final Map<T, Integer> rank = new HashMap<>();
	private int size;

	/**
	 * Legt eine neue Union-Find-Struktur mit allen 1-elementigen Teilmengen von s an.
	 * @param s Menge von Elementen, für die eine Partionierung verwaltet werden soll.
	 *
	 */
	public UnionFind(Set<T> s) {
		for (T e : s) {
			parent.put(e, e); // jedes Element ist sein eigener Repräsentant
			rank.put(e, 0); // Anfangsrang ist 0
		}
		this.size = s.size(); // Anzahl der Teilmengen ist gleich der Anzahl der Elemente
	}

	/**
     * Liefert den Repräsentanten der Teilmenge zurück, zu der e gehört.
	 * Pfadkompression wird angewendet.
     * @param e Element
     * @throws IllegalArgumentException falls e nicht in der Partionierung vorkommt.
     * @return Repräsentant der Teilmenge, zu der e gehört.
     */
	public T find(T e) {
		// ...
		if (!parent.containsKey(e)){
			throw new IllegalArgumentException("Element " + e + "ist nicht enthalten");
		}
		if  (!e.equals(parent.get(e))) {
			// Pfadkompression
			parent.put(e, find(parent.get(e)));
		}
		return parent.get(e);
	}


	/**
     * Vereinigt die beiden Teilmengen, die e1 bzw. e2 enthalten.
	 * Die Vereinigung wird nur durchgeführt,
	 * falls die beiden Mengen unterschiedlich sind.
	 * Es wird union-by-rank durchgeführt.
     * @param e1 Element.
	 * @param e2 Element.
	 * @throws IllegalArgumentException falls e1 und e2 keine Elemente der Union-Find-Struktur sind.
     */
	public void union(T e1, T e2) {
		// ...
		if (!parent.containsKey(e1) || !parent.containsKey(e2)) {
			throw new IllegalArgumentException("Elemente " + e1 + " und " + e2 + " sind nicht enthalten");
		}

		T root1 = find(e1);
		T root2 = find(e2);

		if (root1.equals(root2)) {
			return; // e1 und e2 sind bereits in der gleichen Teilmenge
		}

		int rank1 = rank.get(root1);
		int rank2 = rank.get(root2);

		if (rank1 < rank2) {
			parent.put(root1, root2); // root1 wird untergeordnet
		} else if (rank1 > rank2) {
			parent.put(root2, root1); // root2 wird untergeordnet
		} else {
			parent.put(root2, root1); // beliebig, hier root2 wird untergeordnet
			rank.put(root1, rank1 + 1); // Rang von root1 erhöht
		}

		size--; // Anzahl der Teilmengen verringern
	}

	/**
	 * Ausgabe der Union-Find-Struktur zu Testzwecken.
	 */
	public void print() {
		// ...
		System.out.println("Union-Find Struktur:");
		for (T element : parent.keySet()) {
			System.out.println("Element: " + element + ", Repräsentant: " + find(element) + ", Rang: " + rank.get(find(element)));
		}
		System.out.println("Anzahl der Teilmengen: " + size);
		System.out.println("-----------------------------");
	}

	/**
	 * Liefert die Anzahl der Teilmengen in der Partitionierung zurück.
	 * @return Anzahl der Teilmengen.
	 */
	public int size() {
		// ...
		return size;
	}

    public static void main(String[] args) {
		Set<Integer> s = Set.of(1,2,3,4,5,6,7,8,9,10);
		UnionFind<Integer> uf = new UnionFind<>(s);
		uf.union(1,2);
		uf.print();
		uf.union(3,4);
		uf.print();
		uf.union(2,4);
		uf.print();
		uf.find(4);
		uf.print();
		uf.union(9,10);
		uf.union(7,8);
		uf.print();
		uf.union(7,9);
		uf.print();
		uf.union(1,7);
		uf.print();
		uf.find(10);
		uf.print();
	}
}
