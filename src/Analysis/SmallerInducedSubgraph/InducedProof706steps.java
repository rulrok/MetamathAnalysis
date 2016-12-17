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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.json.Path;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author reuel
 */
public class InducedProof706steps {

    private static final String THEOREMS_FIVE_INFORMATIONTXT = "theorems_five_information.txt";

    public static void main(String[] args) throws IOException {
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

        //See if the previous data exists
        List<String> alreadyParsedNodes = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(THEOREMS_FIVE_INFORMATIONTXT))) {
            String firstLine = scanner.nextLine();
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                String[] lineValues = nextLine.split(";");
                alreadyParsedNodes.add(lineValues[0]);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InducedProof706steps.class.getName()).log(Level.SEVERE, null, ex);
        }

        File file = new File(THEOREMS_FIVE_INFORMATIONTXT);
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(THEOREMS_FIVE_INFORMATIONTXT)) {
                fw.write("theorem;proof SIS source decomposition;reverse reachability;proof elements;source decomposition;proof steps");
                fw.write(System.lineSeparator());
            }
        }

        try (Transaction tx = graph.beginTx(); FileWriter fw = new FileWriter(file, true)) {
            ResourceIterator<Node> theorems = graph.findNodes(Label.THEOREM);

            for (; theorems.hasNext();) {
                Node currentTheorem = theorems.next();

                String theoremName = currentTheorem.getProperty("name").toString();

                if (alreadyParsedNodes.contains(theoremName)) {
                    continue;
                }

                fw.write(theoremName);
                fw.write(";");

                //Obtain theorem's proof theorems
                List<String> proofElements = new ArrayList<>();
                currentTheorem.getRelationships(Direction.INCOMING).forEach(r -> {
                    Node proofElement = r.getStartNode();
                    String proofElementName = proofElement.getProperty("name").toString();

                    proofElements.add(proofElementName);
                });

                //Prepare SIS algorithm
                GraphDatabaseService outputGraph = GraphFactory.makeGraph("db/SIS/" + theoremName, true);
                SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph, outputGraph);

                String[] names = new String[proofElements.size()];
                proofElements.toArray(names);

                msgi.execute(names);

                try {
                    //output graph decomposition
                    GraphDecomposition decomposition = new SimpleGraphDecomposition(outputGraph);
                    List<List<Node>> layers = decomposition.decomposeIntoSources();
                    final int proofLayerDecompositionSize = layers.size();
                    System.out.println("Proof SIS source decomposition: " + proofLayerDecompositionSize);
                    fw.write(Integer.toString(proofLayerDecompositionSize));
                    fw.write(";");

                    //Reverse reachability
                    ReachabilityFromNode reachabilityFromTheorem = new ReachabilityFromNode(graph);
                    Map<String, Integer> calculate = reachabilityFromTheorem
                            .addEvaluator(new AxiomEvaluator())
                            .reverseGraph()
                            .calculateFromNode(currentTheorem, RelType.SUPPORTS);

                    Collection<Integer> reverseRecheability = calculate.values();
                    final Integer reverseReachability = reverseRecheability.stream().findFirst().get();
                    System.out.println("Reverse recheability: " + reverseReachability);
                    fw.write(Integer.toString(reverseReachability));
                    fw.write(";");

                    //Proof elements quantity (same as indegree)
                    final int proofElementsSize = proofElements.size();
                    System.out.println("Theorem proof elements: " + proofElementsSize);
                    fw.write(Integer.toString(proofElementsSize));
                    fw.write(";");

                    //Layer which theorem lays on source decomposition
                    final Integer theoremSourceLayerValue = theoremSourceLayer.get(theoremName);
                    System.out.println("Theorem decomposition layer: " + theoremSourceLayerValue);
                    fw.write(Integer.toString(theoremSourceLayerValue));
                    fw.write(";");

                    //Number of steps on the proof
                    final Integer theoremProofStepsValue = theoremProofSteps.get(theoremName);
                    System.out.println("Theorem proof steps: " + theoremProofStepsValue);
                    fw.write(Integer.toString(theoremProofStepsValue));
                    fw.write(System.lineSeparator());

                    fw.flush();
                } catch (Exception e) {
                    System.out.println("Exception for theorem " + theoremName);
                    System.out.println(e);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(InducedProof706steps.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
