package Graph.Algorithms;

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
public class GraphToTxt {

    GraphDatabaseService graph;
    String outputFilePath;

    public GraphToTxt(GraphDatabaseService graph, String outputFilePath) {
        this.graph = graph;
        this.outputFilePath = outputFilePath;
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
        Map<Long, Set<Long>> relationships = new HashMap<>();
        try (Transaction tx = graph.beginTx()) {

            /*
             * Count nodes
             */
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            try (PrintWriter printWriter = new PrintWriter(outputNodes)) {
                for (Node node : allNodes) {
                    long id = node.getId();
                    Object name = node.getProperty("name", "NO_NAME");
                    printWriter.printf("%d\t%s\n", id, name);
                    nodesCount++;
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
            allRelationships.forEach((Relationship relationship) -> {

                if (relationship.isType(relationshipType)) {

                    Node startNode = relationship.getStartNode();
                    Node endNode = relationship.getEndNode();
                    long k = startNode.getId();
                    long v = endNode.getId();

                    if (!relationships.containsKey(k)) {
                        relationships.put(k, new LinkedHashSet<>());
                    }

                    relationships.get(k).add(v);

                }

            });

            //Make sure we don't modify the original graph
            tx.failure();
        } catch (Exception ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        /*
         * Print elements to a file
         */
        try (PrintWriter printWriter = new PrintWriter(outputGraph)) {
            printWriter.println(nodesCount);
            relationships.forEach((Long sourceNode, Set<Long> hashset) -> {
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

}
