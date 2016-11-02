package Tests;

import Graph.Algorithms.Contracts.GraphDecomposition;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.Algorithms.RemoveIsolatedNodes;
import Graph.GraphFactory;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author reuel
 */
public class Decomposition {

    public static void main(String[] args) {

        GraphDatabaseService graph = GraphFactory.makeTestGraphJ();

        RemoveIsolatedNodes isolatedNodes = new RemoveIsolatedNodes(graph);
        isolatedNodes.execute();

        GraphDecomposition decomposition = new SimpleGraphDecomposition(graph);
        List<List<Node>> decomposeIntoSinks = decomposition.decomposeIntoSinks();
        
        List<Node> sinks = decomposition.getSinks();
        
        List<List<Node>> decomposeIntoSources = decomposition.decomposeIntoSources();
        List<Node> sources = decomposition.getSources();
        

        System.out.println("");
    }

}
