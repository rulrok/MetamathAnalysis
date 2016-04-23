package Graph.Algorithms;

import Graph.Algorithms.Evaluators.SinkEvaluator;
import Graph.Algorithms.Evaluators.SourceEvaluator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 *
 * @author Reuel
 */
public class GraphDecomposition {

    private final GraphDatabaseService graph;
    private ArrayList<List<Node>> components;

    public GraphDecomposition(GraphDatabaseService graph) {
        this.graph = graph;
        configure();
    }

    private void configure() {
        components = new ArrayList<>();
    }

    public List<List<Node>> execute(DecompositionTarget decompositionTarget, List<Node> initialNodes) {

        if (initialNodes.isEmpty() || initialNodes.contains(null)) {
            return components;
        }

        //Create a copy of the passed list so we don't change the outsite list
        LinkedList<Node> initialNodesList = new LinkedList<>(initialNodes);

        TraversalDescription bfs = graph.traversalDescription().breadthFirst();

        if (decompositionTarget == DecompositionTarget.SINK) {
            bfs = bfs
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SinkEvaluator());

            try (Transaction tx = graph.beginTx()) {

                do {
                    List<Node> component = new LinkedList<>();
                    bfs.traverse(initialNodesList).forEach((Path path) -> {
                        Node endNode = path.endNode();
                        endNode.getRelationships(Direction.INCOMING).forEach(relationship -> {
                            relationship.delete();
                        });
                        component.add(endNode);
                        initialNodesList.removeIf((Node node) -> {
                            return endNode.equals(node);
                        });
                        endNode.delete();

                    });
                    if (!component.isEmpty()) {
                        components.add(component);
                    } else {
                        component.addAll(initialNodes);
                        components.add(component);
                        break;
                    }
                } while (true);
                tx.failure();
            }
        } else if (decompositionTarget == DecompositionTarget.SOURCE) {
            bfs = bfs
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SourceEvaluator());

        }

        return components;
    }

}
