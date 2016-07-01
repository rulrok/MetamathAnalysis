package Graph.Algorithms;

import Graph.GraphFactory;
import Graph.RelTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
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
                Node newNode = graph.createNode();
                for (Label l : node.getLabels()) {
                    newNode.addLabel(l);
                }
                newNode.setProperty("name", node.getProperty("name") + "'");

                //All outgoing relationships
                Iterable<Relationship> outRels = node.getRelationships(Direction.OUTGOING);
                outRels.forEach(outRel -> {

                    //Link the newNode with the end node of the relationship
                    Node otherNode = outRel.getOtherNode(node);
                    newNode.createRelationshipTo(otherNode, outRel.getType());

                    outRel.delete();
                });

                //Link the original node with the clone
                node.createRelationshipTo(newNode, RelTypes.SUPPORTS);
            });

            //Save graph
            tx.success();
        }
    }
}
