package Graph.Algorithms;

import Graph.Algorithms.Contracts.GraphDecomposition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class SimpleGraphDecomposition implements GraphDecomposition {

    private final GraphDatabaseService graph;
    private ArrayList<List<Node>> components;

    public SimpleGraphDecomposition(GraphDatabaseService graph) {
        this.graph = graph;
        configure();
    }

    private void configure() {
        components = new ArrayList<>();
    }

    @Override
    public List<List<Node>> execute(DecompositionTarget decompositionTarget, List<Node> initialNodes) throws Exception {

        try (Transaction tx = graph.beginTx()) {
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            if (decompositionTarget == DecompositionTarget.SINK) {

                decomposeIntoSinks(allNodes);

            } else if (decompositionTarget == DecompositionTarget.SOURCE) {
                
                decomposeIntoSources(allNodes);
                
            }
            tx.failure();
        }
        return components;
    }

    private void decomposeIntoSinks(ResourceIterable<Node> allNodes) {
        List<Node> component;
        do {
            component = new LinkedList<>();

            for (ResourceIterator<Node> iterator = allNodes.iterator(); iterator.hasNext();) {
                Node node = iterator.next();

                if (node.getDegree(Direction.INCOMING) > 0 && node.getDegree(Direction.OUTGOING) == 0) {
                    component.add(node);
                    node.getRelationships().forEach(relationship -> {
                        relationship.delete();
                    });
                    node.delete();
                }
            }

            if (component.size() > 0) {
                components.add(component);
            } else {
                break;
            }
        } while (true);
    }

    private void decomposeIntoSources(ResourceIterable<Node> allNodes) {
        List<Node> component;
        do {
            component = new LinkedList<>();

            for (ResourceIterator<Node> iterator = allNodes.iterator(); iterator.hasNext();) {
                Node node = iterator.next();

                if (node.getDegree(Direction.OUTGOING) > 0 && node.getDegree(Direction.INCOMING) == 0) {
                    component.add(node);
                    node.getRelationships().forEach(relationship -> {
                        relationship.delete();
                    });
                    node.delete();
                }
            }

            if (component.size() > 0) {
                components.add(component);
            } else {
                break;
            }
        } while (true);
    }

}
