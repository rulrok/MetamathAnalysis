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

    
    public TarjanManualDFS(GraphDatabaseService graph, Node initialNode, Relationship... relationship){
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

        /*  for each edge p->q
                if q is not already in T
                {
                    add p->q to T
                    visit(q)
                    low(p) = min(low(p), low(q))
                } else 
                    low(p) = min(low(p), dfsnum(q)) 
         */
        Iterable<Relationship> relationships;
        relationships = (relationshipTypes != null) ? p.getRelationships(relationshipTypes) : p.getRelationships();

        for (Relationship p_q : relationships) {
            
            Node q = p_q.getEndNode();
            if (!T.contains(q.getId())) {
                T.add(q.getId());
                visit(q);
                low.put(p.getId(), Math.min(low.get(p.getId()), low.get(q.getId())));
            } else {
                low.put(p.getId(), Math.min(low.get(p.getId()), dfsnum.get(q.getId())));
            }

        }

        /*     if low(p)=dfsnum(p)
        {
        output "component:"
        repeat
            remove last element v from L
            output v
            remove v from G
        until v=p
        } */
        List<Node> currentComponent = new LinkedList<>();
        int lowlink = low.get(p.getId());
        int index = dfsnum.get(p.getId());
        if (lowlink == index) {
            Node v;
            do {
                v = L.pop();
                currentComponent.add(v);
                v.delete();
            } while (v.getId() != p.getId());
            components.add(currentComponent);
        }
    }
}
