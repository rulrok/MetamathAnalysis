package Graph.Algorithms;

import Graph.GraphFactory;
import Graph.RelTypes;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class HalveNodes {

    private final GraphDatabaseService graph;

    public HalveNodes(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public void execute() {

        try (Transaction tx = graph.beginTx()) {

            //Get all nodes
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            allNodes.forEach(node -> {

                //Create the node clone
                //TODO get all labels if necessary on future implementations
                Node newNode = graph.createNode(node.getLabels().iterator().next());
                newNode.setProperty("name", node.getProperty("name") + "'");

                //Link the original node with the clone
                node.createRelationshipTo(newNode, RelTypes.SUPPORTS);

                //All outgoing relationships
                Iterable<Relationship> outRels = node.getRelationships(Direction.OUTGOING);
                outRels.forEach(outRel -> {

                    //Link the newNode with the end node of the relationship
                    Node otherNode = outRel.getOtherNode(node);
                    newNode.createRelationshipTo(otherNode, outRel.getType());
                    
                    outRel.delete();
                });

            });

            //Save graph
            tx.success();
        }
    }

    public static void main(String[] args) {
        GraphDatabaseService graphCopy = GraphFactory.copyGraph("db/metamath", "db/halved_graph");

        HalveNodes halveNodes = new HalveNodes(graphCopy);

        halveNodes.execute();
    }

}
