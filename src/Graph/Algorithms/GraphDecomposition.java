package Graph.Algorithms;

import Graph.Algorithms.Evaluators.SinkEvaluator;
import Graph.Label;
import java.util.ArrayList;
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

    public GraphDecomposition(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public List<List<Node>> execute(Node initialNode, DecompositionTarget direction) {

        ArrayList<List<Node>> components = new ArrayList<>();

        TraversalDescription traversalDescription = graph.traversalDescription();
        Node zerothNode = initialNode;

        if (direction == DecompositionTarget.SINK) {
            TraversalDescription bfs = traversalDescription
                    .breadthFirst()
                    .order(BranchOrderingPolicies.PREORDER_BREADTH_FIRST)
                    .evaluator(new SinkEvaluator());

            bfs.traverse(zerothNode).forEach((Path path) -> {
                Node endNode = path.endNode();
                System.out.println(endNode.getDegree(Direction.INCOMING));
            });

        } else if (direction == DecompositionTarget.SOURCE) {

        }

        return components;
    }

}
