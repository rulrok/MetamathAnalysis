package Graph.Algorithms;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphToTxt implements LabelFiltered {

    GraphDatabaseService graph;
    String outputFilePath;
    private final List<Label> labelFilters = new ArrayList<>();

    public GraphToTxt(GraphDatabaseService graph, String outputFilePath) {
        this.graph = graph;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public GraphToTxt addFilterLabel(Label label) {
        labelFilters.add(label);
        return this;
    }

    public boolean execute(RelationshipType relationshipType) {
        File outputGraph = new File(outputFilePath);
        Path outputDir = outputGraph.toPath().toAbsolutePath().getParent();
        File outputNodes = new File(outputDir.toString() + File.separator + "grafo_nomes.txt");

        try {
            outputGraph.createNewFile();
            outputNodes.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cannot create file");
            return false;
        }

        if (!outputGraph.canWrite()) {
            System.err.println("Cannot write into the file");
            return false;
        }

        int nodesCount = 0;
        Map<Long, Set<Long>> relationshipsToPrint = new HashMap<>();
        try (Transaction tx = graph.beginTx()) {

            /*
             * Count nodes
             */
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            try (PrintWriter printWriter = new PrintWriter(outputNodes)) {
                for (Node node : allNodes) {

                    if (!labelFilters.isEmpty()) {
                        boolean labelFound = false;
                        for (Label l : labelFilters) {
                            labelFound |= node.hasLabel(l);
                        }
                        if (!labelFound) {
                            continue;
                        }
                    }

                    long id = uniqueSequencialId(node.getId());
                    Object name = node.getProperty("name", "NO_NAME");
                    printWriter.printf("%d\t%s\n", id, name);
//                    nodesCount++;
                }

                printWriter.flush();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            /*
             * Get relationships
             */
            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();
            for (Relationship relationship : allRelationships) {

                //Filter relationships
                if (!relationship.isType(relationshipType)) {
                    continue;
                }
                Node startNode = relationship.getStartNode();
                Node endNode = relationship.getEndNode();

                //Filter nodes
                if (!labelFilters.isEmpty()) {

                    boolean startMatch = false, endMatch = false;
                    for (Label l : labelFilters) {
                        startMatch |= startNode.hasLabel(l);
                        endMatch |= endNode.hasLabel(l);
                    }
                    if (!startMatch || !endMatch) {
                        continue;
                    }
                }

                long k = uniqueSequencialId(startNode.getId());
                long v = uniqueSequencialId(endNode.getId());

                if (!relationshipsToPrint.containsKey(k)) {
                    relationshipsToPrint.put(k, new LinkedHashSet<>());
                }

                relationshipsToPrint.get(k).add(v);

            }

            //Make sure we don't modify the original graph
            tx.failure();
        } catch (Exception ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        /*
         * Print elements to a file
         */
        nodesCount = (int) guid;
        try (PrintWriter printWriter = new PrintWriter(outputGraph)) {
            printWriter.println(nodesCount);
            relationshipsToPrint.forEach((Long sourceNode, Set<Long> hashset) -> {
                hashset.forEach((Long destNode) -> {
                    printWriter.println(sourceNode + "\t" + destNode);
                });
            });

            printWriter.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;

    }

    private long guid = 0;
    private final Map<Long, Long> inputs = new HashMap<>();

    /**
     * For the HIPR library, the index of node numbers needs to be sequencial.
     * This function associates a unique sequencial value for a given
     * non-sequencial number.
     *
     * @param input
     * @return
     */
    private long uniqueSequencialId(long input) {

        if (inputs.containsKey(input)) {
            return inputs.get(input);
        } else {
            inputs.put(input, ++guid);
            return guid;
        }
    }

}
