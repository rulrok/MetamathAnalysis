package Graph.Algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
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

    public boolean execute() {
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

        try {
            PrintWriter printWriter = new PrintWriter(outputFile);

            int nodesCount = 0;
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            for (Node node : allNodes) {
                nodesCount++;
            }
            printWriter.println(nodesCount);
            Iterable<Relationship> allRelationships = GlobalGraphOperations.at(graph).getAllRelationships();
            allRelationships.forEach((Relationship relationship) -> {
                printWriter.println(relationship.getStartNode().getId() + "\t" + relationship.getEndNode().getId());
                relationship.delete();
            });

            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

}
