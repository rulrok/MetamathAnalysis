package Graph.Algorithms;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Reuel
 */
public class TarjanManualDFS extends AbstractStrongConnectedComponentsAlgorithm {

    /**
     * HelperNode is an antificial node linking to everyone else in the graph.
     */
    private int N;
    private Node x;
    private Stack<Node> L;
    private List<List<Node>> components;
    private Set<Long> T;
    private Map<Long, Integer> low;
    private Map<Long, Integer> dfsnum;

    
    public TarjanManualDFS(GraphDatabaseService graph, Node initialNode, RelationshipType... relationship){
        this(graph, initialNode);
    }
    
    public TarjanManualDFS(GraphDatabaseService graph, Node initialNode) {
        super(graph, initialNode);
    }

    @Override
    public void configure() {

        x = initialNode;
        low = new TreeMap<>();
        dfsnum = new TreeMap<>();
        components = new LinkedList<>();
    }

    @Override
    public List<List<Node>> execute() {

            /* initialize a counter N to zero */
            N = 0;

            /*  initialize list L to empty */
            L = new Stack<>();

            /* build directed tree T, initially a single vertex {x} */
            T = new HashSet<>();
            T.add(x.getId());

            /* visit(x) */
            visit(x);
            
            return components;

    }

    private void visit(Node p) {

        /* add p to L */
        L.push(p);

        /* dfsnum(p) = N */
        dfsnum.put(p.getId(), N);

        /* increment N */
        N++;

        /* low(p) = dfsnum(p) */
        low.put(p.getId(), dfsnum.get(p.getId()));

        Iterable<Relationship> relationships;
        relationships = (relationshipTypes != null) ? p.getRelationships(relationshipTypes) : p.getRelationships();

        /* for each edge p->q */
        for (Relationship p_q : relationships) {
            
            /* if q is not already in T */
            Node q = p_q.getEndNode();
            if (!T.contains(q.getId())) {
                /* add p->q to T */
                T.add(q.getId());
                /* visit(q) */
                visit(q);
                /* low(p) = min(low(p), low(q)) */
                low.put(p.getId(), Math.min(low.get(p.getId()), low.get(q.getId())));
            } else { /* else */
                /* low(p) = min(low(p), dfsnum(q)) */
                low.put(p.getId(), Math.min(low.get(p.getId()), dfsnum.get(q.getId())));
            }

        }
        
        List<Node> currentComponent = new LinkedList<>();
        
        /* if low(p)=dfsnum(p) */
        int lowlink = low.get(p.getId());
        int index = dfsnum.get(p.getId());
        if (lowlink == index) {
            Node v;
            /* repeat */
            do {
                /* remove last element v from L */
                v = L.pop();
                /* output v */
                currentComponent.add(v);
                /* remove v from G */
                v.delete();
                
                /* until v=p */
            } while (v.getId() != p.getId());
            
            components.add(currentComponent);
        }
    }
}
