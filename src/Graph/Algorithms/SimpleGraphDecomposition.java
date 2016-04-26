package Graph.Algorithms;

import Graph.Algorithms.Contracts.GraphDecomposition;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

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
        if (decompositionTarget == DecompositionTarget.SINK) {

        } else if (decompositionTarget == DecompositionTarget.SOURCE) {

        }
        return components;
    }

}
