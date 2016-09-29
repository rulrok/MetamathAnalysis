package Tests;

import Graph.GraphFactory;
import Graph.Label;
import Graph.RelType;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;

/**
 *
 * @author Reuel
 */
public class DFS_merlem1_component {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();

        try (Transaction tx = graph.beginTx()) {

            Node axMeredith = graph.findNode(Label.AXIOM, "name", "ax-meredith");

            int[] iarr = new int[]{0};
            graph.traversalDescription()
                    .depthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .relationships(RelType.SUPPORTS, Direction.OUTGOING)
                    .traverse(axMeredith)
                    .nodes()
                    .forEach((Node n) -> {
                        String nodeName = n.getProperty("name").toString();
                        iarr[0]++;
                        System.out.print(iarr[0] + "\t");
                        System.out.println(nodeName);
                    });
            tx.failure();
        }
    }
}
