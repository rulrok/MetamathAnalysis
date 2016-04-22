package Graph.Algorithms;

import Graph.Algorithms.Evaluators.SinkEvaluator;
import Graph.Label;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.tooling.GlobalGraphOperations;

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

    public List<List<Node>> execute(DecompositionTarget direction, List<Node> initialNodes) {

        LinkedList<Node> initialNodesList = new LinkedList<>(initialNodes);

        TraversalDescription traversalDescription = graph.traversalDescription();

        if (direction == DecompositionTarget.SINK) {
            TraversalDescription bfs = traversalDescription
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SinkEvaluator());

            do {
                List<Node> component = new LinkedList<>();
                bfs.traverse(initialNodesList).forEach((Path path) -> {
                    Node endNode = path.endNode();
                    endNode.getRelationships().forEach(relationship -> {
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
                    break;
                }
            } while (true);

        } else if (direction == DecompositionTarget.SOURCE) {

        }

        return components;
    }

}
