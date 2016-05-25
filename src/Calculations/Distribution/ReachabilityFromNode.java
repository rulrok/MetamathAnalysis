package Calculations.Distribution;

import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import Utils.ExportMapToTXT;
import java.util.Iterator;
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
import org.neo4j.graphdb.traversal.Uniqueness;

/**
 * Calculate the reachability distribution
 * 
 * @author Reuel
 */
public class ReachabilityFromNode {

    private final GraphDatabaseService graph;
    private final Map<String, Integer> calculations;
    private boolean reverseGraph;

    public ReachabilityFromNode(GraphDatabaseService graph) {
        this.graph = graph;
        this.calculations = new TreeMap<>();
    }

    public ReachabilityFromNode reverseGraph() {
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
            traverse(startNode, relationshipType);

            tx.failure();
        }

        return calculations;
    }

    public Map<String, Integer> calculate(Node[] nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {

            for (Node node : nodes) {
                traverse(node, relationshipType);
            }

            tx.failure();
        }

        return calculations;
    }

    public Map<String, Integer> calculate(Iterator<Node> nodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {

            for (; nodes.hasNext();) {
                Node node = nodes.next();
                traverse(node, relationshipType);
            }
            tx.failure();
        }

        return calculations;
    }

    private void traverse(Node startNode, RelationshipType relationshipType) {
        TraversalDescription traverser = prepareTraverser(relationshipType);

//        System.out.println("Going from node: " + startNode.getProperty("name"));
        ResourceIterator<Node> iterator = traverser.traverse(startNode).nodes().iterator();
        int counter = 0;
        for (; iterator.hasNext();) {
            Node node = iterator.next();
//            System.out.println("\t" + node.getProperty("name") + "\t" + n.getLabels().toString());
            ++counter;
        }

        Object nodeId = startNode.getId();
        Object nodeName = startNode.getProperty("name");
        String key = String.join("\t", String.valueOf(nodeId), String.valueOf(nodeName));
        calculations.put(key, counter);
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
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        Map<String, Integer> calculate = reachabilityFromSource.calculate(Label.AXIOM, RelTypes.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

        ExportMapToTXT.export("reach_distribution_sources_everybody", calculate, new String[]{"id", "axiom name", "count"});

    }

}
