package Graph.Algorithms;

import java.util.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class KosarajuSCC extends AbstractStrongConnectedComponentsAlgorithm {

    public KosarajuSCC(GraphDatabaseService graph, Node initialNode) {
        super(graph, initialNode);
    }

    public KosarajuSCC(GraphDatabaseService graph, Node initialNode, RelationshipType... relationshipTypes) {
        super(graph, initialNode, relationshipTypes);
    }

    Stack<Node> L;
    Map<Long, Integer> f;

    @Override
    public void configure() {
        components = new LinkedList<>();
        f = new HashMap<>();
        L = new Stack<>();
    }

    @Override
    public List<List<Node>> execute() {
        ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
        Map<Long, List<Long>> adjacencyList = new HashMap<>();
        allNodes.forEach(node -> {
            adjacencyList.put(node.getId(), new LinkedList<>());
            node.getRelationships(Direction.OUTGOING).forEach(relationship -> {
                adjacencyList.get(relationship.getStartNode().getId())
                        .add(relationship.getEndNode().getId());
            });
        });

        List<List<Long>> scc = scc(adjacencyList);

        scc.forEach((List<Long> component) -> {
            List<Node> componentList = new LinkedList<>();
            component.forEach((Long nodeId) -> {
                componentList.add(graph.getNodeById(nodeId));
            });
            components.add(componentList);
        });

        return components;
    }

    private List<List<Long>> scc(Map<Long, List<Long>> graph) {
        int n = graph.size();
        Map<Long, Boolean> used = new HashMap<>(n);
        List<Long> order = new ArrayList<>();
        for (long i = 0; i < n; i++) {
            if (!used.getOrDefault(i, false)) {
                dfs(graph, used, order, i);
            }
        }

        Map<Long, List<Long>> reverseGraph = new HashMap<>();
        for (long i = 0; i < n; i++) {
            reverseGraph.put(i, new ArrayList<>());
        }
        for (long i = 0; i < n; i++) {
            for (long j : graph.get(i)) {
                reverseGraph.get(j).add(i);
            }
        }

        List<List<Long>> components = new ArrayList<>();
        used.replaceAll((node, isUsed) -> {
            return false;
        });
        Collections.reverse(order);

        for (long u : order) {
            if (!used.get(u)) {
                List<Long> component = new ArrayList<>();
                dfs(reverseGraph, used, component, u);
                components.add(component);
            }
        }

        return components;
    }

    private void dfs(Map<Long, List<Long>> graph, Map<Long, Boolean> used, List<Long> res, long u) {
        used.put(u, true);
        for (long v : graph.get(u)) {
            if (!used.getOrDefault(v, false)) {
                dfs(graph, used, res, (int) v);
            }
        }
        res.add(u);
    }

}
