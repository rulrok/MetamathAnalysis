package Analysis.SmallerInducedSubgraph;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.Evaluators.AxiomEvaluator;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.Algorithms.SmallerInducedSubgraph;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author reuel
 */
public class InducedProof706steps {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeGraph("db/metamath-nouserboxes-axiom-theorem-nojunk");

        //Map the values of theorem name and its source decomposition layer number
        Map<String, Integer> theoremSourceLayer = new HashMap<>(18000);
        GraphDecomposition sourceDecomposition = new SimpleGraphDecomposition(graph);
        List<List<Node>> allLayers = sourceDecomposition.decomposeIntoSources();
        try (Transaction tx = graph.beginTx()) {
            int layerCount = 0;
            for (List<Node> layer : allLayers) {
                layerCount++;
                for (Node n : layer) {
                    String name = n.getProperty("name").toString();
                    theoremSourceLayer.put(name, layerCount);
                }
            }
        }

        //Map the number of proof steps of theorem
        Map<String, Integer> theoremProofSteps = new HashMap<>(18000);
        try (Scanner scanner = new Scanner(new File("theorems_steps_crawl.txt"))) {
            String firstLine = scanner.nextLine();
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                String[] lineValues = nextLine.split(";");
                String theoremName = lineValues[1];
                String theoremSteps = lineValues[2];
                theoremProofSteps.put(theoremName, Integer.parseInt(theoremSteps));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(InducedProof706steps.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Theorem
        String nodeName = "2503lem2";

        Node currentTheorem;

        //Obtain theorem's proof theorems
        List<String> proofElements = new ArrayList<>();
        try (Transaction tx = graph.beginTx()) {
            currentTheorem = graph.findNode(Label.THEOREM, "name", nodeName);

            currentTheorem.getRelationships(Direction.INCOMING).forEach(r -> {
                Node proofElement = r.getStartNode();
                String proofElementName = proofElement.getProperty("name").toString();

                proofElements.add(proofElementName);
            });
        }

        //Prepare SIS algorithm
        GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/SIS_2503lem2", true);
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

        String[] names = new String[proofElements.size()];
        proofElements.toArray(names);

        msgi.execute(names);

        //output graph decomposition
        GraphDecomposition decomposition = new SimpleGraphDecomposition(outputGraph);
        List<List<Node>> layers = decomposition.decomposeIntoSources();
        System.out.println("Proof SIS source decomposition: " + layers.size());

        //Reverse reachability
        ReachabilityFromNode reachabilityFromTheorem = new ReachabilityFromNode(graph);
        Map<String, Integer> calculate = reachabilityFromTheorem
                .addEvaluator(new AxiomEvaluator())
                .reverseGraph()
                .calculateFromNode(currentTheorem, RelType.SUPPORTS);

        Collection<Integer> reverseRecheability = calculate.values();
        System.out.println("Reverse recheability: " + reverseRecheability.stream().findFirst().get());

        System.out.println("Theorem proof elements: " + proofElements.size());

        System.out.println("Theorem decomposition layer: " + theoremSourceLayer.get(nodeName));

        System.out.println("Theorem proof steps: " + theoremProofSteps.get(nodeName));
    }
}
