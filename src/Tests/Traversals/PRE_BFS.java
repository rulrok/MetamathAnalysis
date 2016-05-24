package Tests.Traversals;

import Graph.GraphFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 *
 * @author Reuel
 */
public class PRE_BFS {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeTestGraphJ();

        try (Transaction tx = graph.beginTx()) {

            TraversalDescription traversalDescription = graph
                    .traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST);

            Node eNode = graph.getNodeById(4);
            PrintTraversal.printResults(traversalDescription, eNode);

        }
    }

}
