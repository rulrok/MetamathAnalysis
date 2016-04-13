package Graph.Algorithms;

import Graph.*;
import java.util.Iterator;
import java.util.Map;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
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

    public StronglyConnectedComponents(GraphDatabaseService graph) {
        this.graph = graph;

        configure();
    }

    private void configure() {

        dfsTD = graph.traversalDescription()
                .depthFirst()
                .relationships(RelTypes.SUPPORTS, Direction.BOTH)
                .relationships(HelperRel.HELPER, Direction.OUTGOING);
    }

    /**
     * Creates a helper relationship
     */
    private enum HelperRel implements RelationshipType {
        HELPER
    }

    public void execute() {

        try (Transaction tx = graph.beginTx()) {

            helperNode = graph.createNode();

            Result result = graph.execute("MATCH n RETURN n;");
            for (; result.hasNext();) {
                Map<String, Object> next = result.next();
                Node foundNode = (Node) next.values().toArray()[0];
                helperNode.createRelationshipTo(foundNode, HelperRel.HELPER);
            }
            
            for (Path position : dfsTD.traverse(helperNode)) {
                System.out.println(position.endNode());
            }

            tx.failure();
        }

    }
}
