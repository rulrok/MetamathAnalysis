package Graph.Algorithms.Decomposition;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.GraphFactory;
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
 * Simple algorithm that do the job in the stupidest way. For each time it scans
 * for a layer of nodes, it runs all over the graph to check which node is a
 * sink or source, depending on the chosen operation. Thus, it is slow!
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
    @Deprecated
    public List<List<Node>> execute(DecompositionTarget decompositionTarget, List<Node> initialNodes) {

        if (decompositionTarget == DecompositionTarget.SINK) {

            decomposeIntoSinks();

        } else if (decompositionTarget == DecompositionTarget.SOURCE) {
            decomposeIntoSources();

        }
        return components;
    }

    @Override
    public List<List<Node>> decomposeIntoSinks() {
        try (Transaction tx = graph.beginTx()) {
            List<Node> component;
            do {
                component = new LinkedList<>();
                ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
                for (ResourceIterator<Node> iterator = allNodes.iterator(); iterator.hasNext();) {
                    Node node = iterator.next();

                    if (node.getDegree(Direction.INCOMING) > 0 && node.getDegree(Direction.OUTGOING) == 0) {
                        component.add(node);
                    }
                }

                if (component.size() > 0) {
                    for (Node node : component) {
                        node.getRelationships().forEach(relationship -> {
                            relationship.delete();
                        });
                        node.delete();
                    }
                    components.add(component);
                } else {
                    break;
                }
            } while (true);

            tx.failure();
        }

        return components;
    }

    @Override
    public List<List<Node>> decomposeIntoSources() {
        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            List<Node> component;
            do {
                component = new LinkedList<>();

                for (ResourceIterator<Node> iterator = allNodes.iterator(); iterator.hasNext();) {
                    Node node = iterator.next();

                    if (node.getDegree(Direction.OUTGOING) > 0 && node.getDegree(Direction.INCOMING) == 0) {
                        component.add(node);

                    }
                }

                if (component.size() > 0) {
                    for (Node node : component) {
                        node.getRelationships().forEach(relationship -> {
                            relationship.delete();
                        });
                        node.delete();
                    }
                    components.add(component);
                } else {
                    break;
                }
            } while (true);
            tx.failure();
        }

        return components;
    }

    @Override
    public List<Node> getSinks() {
        ArrayList<Node> sinks = new ArrayList<>();
        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            allNodes.forEach((Node node) -> {
                if (node.getDegree(Direction.INCOMING) > 0 && node.getDegree(Direction.OUTGOING) == 0) {
                    sinks.add(node);
                }
            });
            tx.failure();
        }

        return sinks;
    }

    @Override
    public List<Node> getSources() {
        ArrayList<Node> sources = new ArrayList<>();
        try (Transaction tx = graph.beginTx()) {
            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            allNodes.forEach((Node node) -> {
                if (node.getDegree(Direction.INCOMING) == 0 && node.getDegree(Direction.OUTGOING) > 0) {
                    sources.add(node);
                }
            });
            tx.failure();
        }

        return sources;
    }

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        SimpleGraphDecomposition sgd = new SimpleGraphDecomposition(graph);

        try (Transaction tx = graph.beginTx()) {

            System.out.println("Sinks:");
            sgd.getSinks().forEach(sink -> {
                System.out.println("\t" + sink.getProperty("name"));
            });

            System.out.println("Sources:");
            sgd.getSources().forEach(sink -> {
                System.out.println("\t" + sink.getProperty("name"));
            });
        }
    }

}
