package Graph.Algorithms;

import Graph.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanderBuilder;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.*;

/**
 *
 * @author Reuel
 */
public class StronglyConnectedComponents {

    private final GraphDatabaseService graph;

    /**
     * HelperNode is an antificial node linking to everyone else in the graph.
     */
    private Node helperNode;
    private TraversalDescription dfsTD;
    private int N;
    private List<Node> L;
    private Map<Long, Integer> low;
    private Map<Long, Integer> dfsnum;

    public StronglyConnectedComponents(GraphDatabaseService graph) {
        this.graph = graph;

        configure();
    }

    private void configure() {

        dfsTD = graph.traversalDescription()
                .depthFirst()
                .relationships(RelTypes.SUPPORTS, Direction.BOTH)
                .relationships(HelperRel.HELPER, Direction.OUTGOING);
        
        low = new TreeMap<>();
        dfsnum = new TreeMap<>();
    }

    /**
     * Creates a helper relationship
     */
    private enum HelperRel implements RelationshipType {
        HELPER
    }

    public void execute() {

        try (Transaction tx = graph.beginTx()) {

            /* make a new vertex x with edges x->v for all v */
            helperNode = graph.createNode();
            ResourceIterator<Node> axiomNodes = graph.findNodes(Label.AXIOM);
            for (; axiomNodes.hasNext();) {
                Node axiom = axiomNodes.next();
                helperNode.createRelationshipTo(axiom, HelperRel.HELPER);
            }

            /* initialize a counter N to zero */
            N = 0;

            /*  initialize list L to empty */
            L = new ArrayList<>();
            
            /* build directed tree T, initially a single vertex {x} */
            
            
            /* visit(x) */
            for (Path position : dfsTD.traverse(helperNode)) {
                visit(position.endNode());
            }

            tx.failure();
        }

    }
    
    private void visit (Node p){
        
        /* add p to L */
        L.add(p);
        
        /* dfsnum(p) = N */
        dfsnum.put(p.getId(), N );
        
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
        for (Relationship relationship : p.getRelationships()) {
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
    }
}
