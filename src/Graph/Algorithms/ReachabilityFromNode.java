package Graph.Algorithms;

import Graph.Label;
import java.util.ArrayList;
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
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
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
    private final List<Evaluator> evaluatorList;

    public ReachabilityFromNode(GraphDatabaseService graph) {
        this.graph = graph;
        this.calculations = new TreeMap<>();
        this.evaluatorList = new ArrayList<>();
        evaluatorList.add(Evaluators.excludeStartPosition());
    }

    public ReachabilityFromNode reverseGraph() {
        this.reverseGraph = true;

        return this;
    }

    /**
     * By default, Evaluators.excludeStartPosition() is used.
     *
     * @param evaluator
     * @return
     */
    public ReachabilityFromNode addEvaluator(Evaluator evaluator) {
        evaluatorList.add(evaluator);

        return this;
    }

    public Map<String, Integer> calculate(Label startNodeType, RelationshipType relationshipType) {

        try (Transaction tx = graph.beginTx()) {

            ResourceIterator<Node> initialNodes = graph.findNodes(startNodeType);

            return calculate(initialNodes, relationshipType);
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

    public Map<String, Integer> calculate(Iterator<Node> initialNodes, RelationshipType relationshipType) {
        try (Transaction tx = graph.beginTx()) {

            for (; initialNodes.hasNext();) {
                Node node = initialNodes.next();
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
        int nodesCount = 0;
        for (; iterator.hasNext();) {
            Node node = iterator.next();
//            System.out.println("\t" + node.getProperty("name") + "\t" + n.getLabels().toString());
            ++nodesCount;
        }

        Object nodeId = startNode.getId();
        Object nodeName = startNode.getProperty("name");
        String key = String.join("\t", String.valueOf(nodeId), String.valueOf(nodeName));
        calculations.put(key, nodesCount);
    }

    private TraversalDescription prepareTraverser(RelationshipType relationshipType) {
        TraversalDescription traverser = graph.traversalDescription()
                .depthFirst()
                .order(BranchOrderingPolicies.PREORDER_DEPTH_FIRST)
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .relationships(relationshipType, Direction.OUTGOING);
        for (Evaluator eval : evaluatorList) {
            traverser = traverser.evaluator(eval);
        }

        if (reverseGraph) {
            traverser = traverser.reverse();
        }

        return traverser;
    }

}
