package Tests;

import Graph.Algorithms.Decomposition.Evaluators.SourceEvaluator;
import Graph.Algorithms.GraphNodeRemover;
import Graph.GraphFactory;
import Tests.Traversals.PrintTraversal;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

/**
 *
 * @author Reuel
 */
public class NodeRemovalTraversal {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeTestGraphJ();

        GraphNodeRemover gnr = new GraphNodeRemover(graph);
        gnr = gnr.addCustomFilter(n -> n.getProperty("name").toString().equalsIgnoreCase("e"));
        gnr.execute();

        try (Transaction tx = graph.beginTx()) {

            TraversalDescription traversalDescription = graph.traversalDescription()
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                    .uniqueness(Uniqueness.NODE_GLOBAL)
                    .evaluator(new SourceEvaluator());

            Node aNode = graph.getNodeById(0);
            Node bNode = graph.getNodeById(1);
            Node cNode = graph.getNodeById(2);
            Node gNode = graph.getNodeById(3);
            Node iNode = graph.getNodeById(8);
            PrintTraversal.printResults(traversalDescription,aNode, bNode, cNode, gNode, iNode);
        }
    }
}
