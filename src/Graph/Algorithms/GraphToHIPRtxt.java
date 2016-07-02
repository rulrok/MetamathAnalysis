package Graph.Algorithms;

import Graph.GraphFactory;
import Graph.Label;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class GraphToHIPRtxt {

    GraphDatabaseService graph;

    public GraphToHIPRtxt(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public boolean execute(String outputFilePath) {

        File outputGraph = new File(outputFilePath);
        try {
            outputGraph.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GraphToTxt.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Cannot create file");
            return false;
        }

        if (!outputGraph.canWrite()) {
            System.err.println("Cannot write into the file");
            return false;
        }

        try (Transaction tx = graph.beginTx()) {

            /*
             * Prepare a stringbuilder to hold the information about the arcs
             */
            StringBuilder arcsInfo = new StringBuilder();

            /*
             * Count the nodes
             */
            int nodesCount = 0;
            for (Node n : GlobalGraphOperations.at(graph).getAllNodes()) {
                nodesCount++;
            }

            /*
             * Count the arcs and prepare the output string
             */
            int arcsCount = 0;
            for (Relationship r : GlobalGraphOperations.at(graph).getAllRelationships()) {
                arcsCount++;

                Node startNode = r.getStartNode();
                Node endNode = r.getEndNode();

                //Nodes ids start at 0 at least
                long startNodeId = startNode.getId();
                long endNodeId = endNode.getId();

                //e.g., a 1 2 5
                arcsInfo.append("a ").append(startNodeId).append(' ').append(endNodeId).append(" 1\n");
            }

            Node S = graph.findNode(Label.UNKNOWN, "name", "S");
            Node T = graph.findNode(Label.UNKNOWN, "name", "T");

            try (PrintWriter printWriter = new PrintWriter(outputGraph)) {
                printWriter.printf("p max %d %d\n", nodesCount, arcsCount);
                printWriter.printf("n %d s\n", S.getId());
                printWriter.printf("n %d t\n", T.getId());

                printWriter.append(arcsInfo);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(GraphToHIPRtxt.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Make sure we don't modify the original graph
            tx.failure();
        }

        return true;
    }

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.copyGraph("db/metamath", "db/super_metamath");

        SuperSinkSuperSource ssss = new SuperSinkSuperSource(graph);
        ssss.execute();

        GraphToHIPRtxt gthipr = new GraphToHIPRtxt(graph);

        gthipr.execute("grafo_HIPR.txt");
    }
}
