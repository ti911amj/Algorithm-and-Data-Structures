// O. Bittel;
// 2.8.2023

package aufgabe2.graph;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;


/**
 * Klasse zur Analyse von Web-Sites.
 *
 * @author Oliver Bittel
 * @since 30.10.2023
 */
public class AnalyzeWebSite {
    public static void main(String[] args) throws IOException {
        // Graph aus Website erstellen und ausgeben:
        //DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("C:\\HTWG_Module\\AlgoDat\\Assignments\\src\\aufgabe2\\data\\WebSiteKlein");
        DirectedGraph<String> webSiteGraph = buildGraphFromWebSite("C:\\HTWG_Module\\AlgoDat\\Assignments\\src\\aufgabe2\\data\\WebSiteGross");
        System.out.println("Anzahl Seiten: \t" + webSiteGraph.getNumberOfVertexes());
        System.out.println("Anzahl Links: \t" + webSiteGraph.getNumberOfEdges());
        //System.out.println(webSiteGraph);

        // Starke Zusammenhangskomponenten berechnen und ausgeben
        StrongComponents<String> sc = new StrongComponents<>(webSiteGraph);
        System.out.println(sc.numberOfComp());
        //System.out.println(sc);

        // Page Rank ermitteln und Top-100 ausgeben
        pageRank(webSiteGraph);
    }

    /**
     * Liest aus dem Verzeichnis dirName alle Web-Seiten und
     * baut aus den Links einen gerichteten Graphen.
     *
     * @param dirName Name eines Verzeichnis
     * @return gerichteter Graph mit Namen der Web-Seiten als Knoten und Links als gerichtete Kanten.
     */
    private static DirectedGraph buildGraphFromWebSite(String dirName) throws IOException {
        File webSite = new File(dirName);
        DirectedGraph<String> webSiteGraph = new AdjacencyListDirectedGraph();

        for (File f : webSite.listFiles()) {
            String from = f.getName();
            LineNumberReader in = new LineNumberReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("href")) {
                    String[] s_arr = line.split("\"");
                    String to = s_arr[1];
                    webSiteGraph.addEdge(from, to);
                }
            }
        }
        return webSiteGraph;
    }

    /**
     * pageRank ermittelt Gewichte (Ranks) von Web-Seiten
     * aufgrund ihrer Link-Struktur und gibt sie aus.
     *
     * @param g gerichteter Graph mit Web-Seiten als Knoten und Links als Kanten.
     */
    private static <V> void pageRank(DirectedGraph<V> g) {
        int nI = 10;
        double alpha = 0.5;

        // Definiere und initialisiere rankTable:
        Map<V, Double> rank = new HashMap<>();
        Map<V, Double> newRank = new HashMap<>();
        for (V v : g.getVertexSet()) {
            rank.put(v, 1.0); // Startwert für alle Knoten
        }

        // Iteration:
        for (int i = 0; i < nI; i++) {
            for (V v : g.getVertexSet()) {
                double r = (1 - alpha); // Basiswert
                for (V u : g.getPredecessorVertexSet(v)) {
                    int outDeg = g.getOutDegree(u);
                    if (outDeg > 0) {
                        r += alpha * (rank.get(u) / outDeg);
                    }
                }
                newRank.put(v, r);
            }
            // Ränge aktualisieren
            for (V v : g.getVertexSet()) {
                rank.put(v, newRank.get(v));
            }
        }

        // Rank Table ausgeben (nur für data/WebSiteKlein):
        if (g.getNumberOfVertexes() <= 20) {
            System.out.println("PageRanks:");
            for (Map.Entry<V, Double> e : rank.entrySet()) {
                System.out.printf("%s: %.4f%n", e.getKey(), e.getValue());
            }
        } else {
            // Nach Ranks sortieren Top 100 ausgeben (nur für data/WebSiteGross):
            System.out.println("Top 100 Seiten nach PageRank:");
            List<Map.Entry<V, Double>> sortedRanks = new ArrayList<>(rank.entrySet());
            sortedRanks.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

            for (int i = 0; i < Math.min(100, sortedRanks.size()); i++) {
                Map.Entry<V, Double> e = sortedRanks.get(i);
                System.out.printf("%d. %s: %.5f%n", i + 1, e.getKey(), e.getValue());
            }

            // Top-Seite mit ihren Vorgängern und Ranks ausgeben:
            Map.Entry<V, Double> top = sortedRanks.get(0);
            V topPage = top.getKey();
            System.out.println("\nTop-Seite: " + topPage);
            System.out.printf("PageRank: %.5f%n", top.getValue());
            System.out.println("Vorgängerseiten:");

            for (V pred : g.getPredecessorVertexSet(topPage)) {
                System.out.printf("  %s (Rank: %.5f)%n", pred, rank.get(pred));
            }
        }
    }
}
