package Graph.Algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
        File outputFile = new File(outputFilePath);

        try {
            outputFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cannot create file");
            return false;
        }

        if (!outputFile.canWrite()) {
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
            for (Node node : allNodes) {
                nodesCount++;
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
        try (PrintWriter printWriter = new PrintWriter(outputFile)) {
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
