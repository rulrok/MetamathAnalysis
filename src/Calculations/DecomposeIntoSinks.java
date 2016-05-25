package Calculations;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.GraphFactory;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class DecomposeIntoSinks {

    public static void main(String[] args) {

        GraphDatabaseService graphDb = GraphFactory.makeDefaultMetamathGraph();

        /*
         * Decompose the graph into sinks
         */
        GraphDecomposition decomposition = new SimpleGraphDecomposition(graphDb);
        List<List<Node>> sinks = decomposition.decomposeIntoSinks();

        System.out.print("Total number of sink components: ");
        System.out.println(sinks.size());
    }
}
