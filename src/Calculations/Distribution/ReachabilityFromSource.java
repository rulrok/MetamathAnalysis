package Calculations.Distribution;

import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.BranchOrderingPolicies;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;

/**
 *
 * @author Reuel
 */
public class ReachabilityFromSource {

    private final GraphDatabaseService graph;
    private final Map<String, List<Integer>> calculations;
    private boolean reverseGraph;
    private RelationshipType relationshipType;

    public ReachabilityFromSource(GraphDatabaseService graph) {
        this.graph = graph;
        this.calculations = new TreeMap<>();
    }

    public ReachabilityFromSource reverseGraph() {
        this.reverseGraph = true;

        return this;
    }

    public Map<String, List<Integer>> calculate(Label startNodeType, RelationshipType relationshipType) {

        try (Transaction tx = graph.beginTx()) {

            ResourceIterator<Node> axioms = graph.findNodes(startNodeType);

            return calculate(axioms, relationshipType);
        }
    }

    public Map<String, List<Integer>> calculate(Node startNode, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {
            TraversalDescription traverser = prepareTraverser(relationshipType);
            traverse(traverser, startNode);
        }

        return calculations;
    }

    public Map<String, List<Integer>> calculate(Node[] nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {
            TraversalDescription traverser = prepareTraverser(relationshipType);
            for (Node node : nodes) {
                traverse(traverser, node);
            }
        }

        return calculations;
    }

    public Map<String, List<Integer>> calculate(Iterator<Node> nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {

            TraversalDescription traverser = prepareTraverser(relationshipType);

            for (; nodes.hasNext();) {
                Node node = nodes.next();

                System.out.println("Going from node: " + node.getProperty("Name"));
                traverse(traverser, node);

            }
            tx.failure();
        }

        return calculations;
    }

    private void traverse(TraversalDescription traverser, Node node) {
        Traverser traverse = traverser.traverse(node);
        traverse.nodes().forEach(n -> {
            System.out.println("\t" + n.getProperty("Name") + "\t" + n.getLabels().toString());
        });
    }

    private TraversalDescription prepareTraverser(RelationshipType relationshipType) {
        TraversalDescription traverser = graph.traversalDescription()
                .depthFirst()
                .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .relationships(relationshipType, Direction.OUTGOING);

        if (reverseGraph) {
            traverser = traverser.reverse();
        }

        return traverser;
    }

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeTestGraphJ();
        ReachabilityFromSource reachabilityFromSource = new ReachabilityFromSource(graph);

        try (Transaction tx = graph.beginTx()) {

            Node gNode = graph.getNodeById(6);

            Map<String, List<Integer>> calculate = reachabilityFromSource.calculate(gNode, RelTypes.SUPPORTS);
        }
    }

}
