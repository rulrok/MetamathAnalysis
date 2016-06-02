package Graph.Algorithms.SCC;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 * Adapted from here https://sites.google.com/site/indy256/algo/scc_tarjan
 *
 * @author Reuel
 */
public class TarjanSCC extends AbstractStrongConnectedComponentsAlgorithm {

    private int time;
    private Stack<Node> stack;
    private Set<Long> visited;
    private Map<Long, Integer> lowlink;
    private Map<Long, Integer> dfsnum;

    public TarjanSCC(GraphDatabaseService graph, Node initialNode, RelationshipType... relationshipTypes) {
        this(graph, initialNode);
        this.relationshipTypes = relationshipTypes;
    }

    public TarjanSCC(GraphDatabaseService graph, Node initialNode) {
        super(graph, initialNode);
    }

    @Override
    public void configure() {
        lowlink = new TreeMap<>();
        dfsnum = new TreeMap<>();
        components = new LinkedList<>();
        time = 0;
        stack = new Stack<>();
        visited = new HashSet<>();
    }

    @Override
    public List<List<Node>> execute() {

        try (Transaction tx = graph.beginTx()) {

            /* visit(x) */
            visit(initialNode);

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            allNodes.forEach(node -> {
                if (!visited.contains(node.getId())) {
                    visit(node);
                }
            });
            
            tx.failure();
        }
        return components;

    }

    private void visit(Node p) {

        stack.push(p);
        visited.add(p.getId());
        dfsnum.put(p.getId(), time);
        time++;

        lowlink.put(p.getId(), dfsnum.get(p.getId()));

        boolean isComponentRoot = true;

        Iterable<Relationship> relationships;
        relationships = (relationshipTypes != null) ? p.getRelationships(relationshipTypes) : p.getRelationships();

        for (Relationship p_q : relationships) {
            Node q = p_q.getEndNode();
            if (!visited.contains(q.getId())) {
                visit(q);
            }
            if (lowlink.get(p.getId()) > lowlink.get(q.getId())) {
                lowlink.put(p.getId(), lowlink.get(q.getId()));
                isComponentRoot = false;
            }

        }

        if (isComponentRoot) {
            List<Node> currentComponent = new LinkedList<>();

            Node x;
            do {
                x = stack.pop();
                currentComponent.add(x);
                lowlink.put(x.getId(), Integer.MAX_VALUE);
            } while (x.getId() != p.getId());

            components.add(currentComponent);
        }
    }
}
