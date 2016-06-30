package Graph.Algorithms;

import Graph.GraphFactory;
import java.util.ArrayList;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class SmallerInducedSubgraph {

    private final GraphDatabaseService graph;

    public SmallerInducedSubgraph(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public void execute(String... nodeNames) {
        try (Transaction tx = graph.beginTx()) {
            ArrayList<Node> nodes = new ArrayList<>(nodeNames.length);

            for (String nodeName : nodeNames) {
                for (Label label : GlobalGraphOperations.at(graph).getAllLabels()) {
                    Node foundNode = graph.findNode(label, "name", nodeName);
                    if (foundNode != null) {
                        nodes.add(foundNode);
                        break;
                    }
                }
            }

            System.out.println("");
        }
    }

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        SmallerInducedSubgraph msgi = new SmallerInducedSubgraph(graph);
        msgi.execute(
                "fmpt",
                "dvhlvec",
                "dvhlveclem",
                "frfnom",
                "pwuninel",
                "occllem",
                "ltxrlt",
                "occl",
                "1nn",
                "fvex"
        );
    }

}
