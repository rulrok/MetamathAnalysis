package Graph.Algorithms;

import Graph.Algorithms.Decomposition.Evaluators.IsolatedNodeEvaluator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class RemoveIsolatedNodes {

    private final GraphDatabaseService graph;
    private final IsolatedNodeEvaluator isolatedNodeEvaluator;

    public RemoveIsolatedNodes(GraphDatabaseService graph) {
        this.graph = graph;
        this.isolatedNodeEvaluator = new IsolatedNodeEvaluator();
    }

    public void execute() {

        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            graph.traversalDescription()
                    .breadthFirst()
                    .evaluator(isolatedNodeEvaluator)
                    .traverse(allNodes)
                    .forEach((Path path) -> {
                        Node startNode = path.startNode();

                        startNode.delete();
                    });
            tx.success();
        }
    }
}
