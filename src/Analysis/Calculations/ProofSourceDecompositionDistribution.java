package Analysis.Calculations;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.GraphFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author reuel
 */
public class ProofSourceDecompositionDistribution {

    public static void main(String[] args) throws IOException {

        /*
         * Get the graph
         */
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();

        /*
         * Decompose the graph into sources
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graph);
        List<List<Node>> sourceLayers = decomposition.decomposeIntoSources();

        Map<String, Integer> theoremsLayerMap = new HashMap<>(20_000);

        int layerCount = 1;
        for (List<Node> layer : sourceLayers) {
            try (Transaction tx = graph.beginTx()) {
                for (Node n : layer) {
                    String nodeName = n.getProperty("name").toString();

                    theoremsLayerMap.put(nodeName, layerCount);
                }
            }
            layerCount++;
        }

        Map<String, List<Integer>> proofLayerDistribution = new HashMap<>();
        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            allNodes.forEach(node -> {
                /*
                 * Find the initial node
                 */
                String nodeName = node.getProperty("name").toString();

                proofLayerDistribution.put(nodeName, new ArrayList<>(150));

                /*
                 * Find nodes used to prove it
                 */
                node.getRelationships(Direction.INCOMING).forEach(rel -> {
                    Node proofStepNode = rel.getStartNode();
                    String proofName = proofStepNode.getProperty("name").toString();
                    Integer layer = theoremsLayerMap.getOrDefault(proofName, -1);

                    List<Integer> proofList = proofLayerDistribution.get(nodeName);
                    proofList.add(layer);
                    proofLayerDistribution.put(nodeName, proofList);
                });

            });

        }

        try (FileWriter fw = new FileWriter("proof_layers_distribution.txt")) {
            proofLayerDistribution.entrySet().stream().forEach((Map.Entry<String, List<Integer>> entry) -> {
                try {
                    String theorem = entry.getKey();
                    List<Integer> proofList = entry.getValue();

                    Collections.sort(proofList);

                    fw.write(theorem);
                    fw.write(System.lineSeparator());

                    StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());

                    if (proofList.isEmpty()) {
                        stringJoiner.add("-1");
                    }
                    proofList.stream().forEach((i) -> {
                        stringJoiner.add(Integer.toString(i));
                    });

                    fw.write(stringJoiner.toString());

                    fw.write(System.lineSeparator());
                    fw.write(System.lineSeparator());
                    fw.write(System.lineSeparator());
                } catch (IOException ex) {
                    Logger.getLogger(ProofSourceDecompositionDistribution.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

    }
}
