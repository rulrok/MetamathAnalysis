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
    private final Map<String, Integer> calculations;
    private boolean reverseGraph;

    public ReachabilityFromSource(GraphDatabaseService graph) {
        this.graph = graph;
        this.calculations = new TreeMap<>();
    }

    public ReachabilityFromSource reverseGraph() {
        this.reverseGraph = true;

        return this;
    }

    public Map<String, Integer> calculate(Label startNodeType, RelationshipType relationshipType) {

        try (Transaction tx = graph.beginTx()) {

            ResourceIterator<Node> axioms = graph.findNodes(startNodeType);

            return calculate(axioms, relationshipType);
        }
    }

    public Map<String, Integer> calculate(Node startNode, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {
            TraversalDescription traverser = prepareTraverser(relationshipType);
            traverse(traverser, startNode);

            tx.failure();
        }

        return calculations;
    }

    public Map<String, Integer> calculate(Node[] nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {
            TraversalDescription traverser = prepareTraverser(relationshipType);
            for (Node node : nodes) {
                traverse(traverser, node);
            }

            tx.failure();
        }

        return calculations;
    }

    public Map<String, Integer> calculate(Iterator<Node> nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {

            TraversalDescription traverser = prepareTraverser(relationshipType);

            for (; nodes.hasNext();) {
                Node node = nodes.next();
                traverse(traverser, node);

            }
            tx.failure();
        }

        return calculations;
    }

    private void traverse(TraversalDescription traverser, Node startNode) {
        Traverser traverse = traverser.traverse(startNode);

//        System.out.println("Going from node: " + startNode.getProperty("name"));
        ResourceIterator<Node> iterator = traverse.nodes().iterator();
        int counter = 0;
        for (; iterator.hasNext();) {
            Node node = iterator.next();
//            System.out.println("\t" + node.getProperty("name") + "\t" + n.getLabels().toString());
            ++counter;
        }

        Object nodeName = startNode.getProperty("name");
        calculations.put((String) nodeName, counter);
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
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        ReachabilityFromSource reachabilityFromSource = new ReachabilityFromSource(graph);

        try (Transaction tx = graph.beginTx()) {

            Map<String, Integer> calculate = reachabilityFromSource.calculate(Label.AXIOM, RelTypes.SUPPORTS);
            calculate.forEach((string, integer) -> {
                System.out.println(string + ":\t" + integer);
            });

        }
    }

}
