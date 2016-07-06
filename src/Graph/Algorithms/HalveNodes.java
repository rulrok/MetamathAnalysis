package Graph.Algorithms;

import Graph.Algorithms.Contracts.LabelFiltered;
import Graph.RelTypes;
import Utils.ResourceIteratorAggregator;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class HalveNodes implements LabelFiltered {

    private final GraphDatabaseService graph;
    private final List<Label> labelFilters = new ArrayList<>();

    public HalveNodes(GraphDatabaseService graph) {
        this.graph = graph;
    }

    @Override
    public HalveNodes addFilterLabel(Graph.Label label) {

        labelFilters.add(label);
        return this;

    }

    public void execute() {

        try (Transaction tx = graph.beginTx()) {

            //Get all nodes
            ResourceIteratorAggregator<Node> allNodes;

            if (labelFilters.isEmpty()) {
                allNodes = new ResourceIteratorAggregator<>(1);
                allNodes.addIterator(GlobalGraphOperations.at(graph).getAllNodes().iterator());
            } else {
                allNodes = new ResourceIteratorAggregator<>(labelFilters.size());
                labelFilters.stream().forEach((l) -> {
                    allNodes.addIterator(graph.findNodes(l));
                });
            }

            for (; allNodes.hasNext();) {
                Node node = allNodes.next();

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
            }

            //Save graph
            tx.success();
        }
    }
}
