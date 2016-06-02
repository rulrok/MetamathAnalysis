package Graph.Algorithms.Contracts;

import Graph.Algorithms.Decomposition.DecompositionTarget;
import java.util.List;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public interface GraphDecomposition {

    @Deprecated
    List<List<Node>> execute(DecompositionTarget decompositionTarget, List<Node> initialNodes);

    /*
     * Decompose the graph into sinks until no node remains.
     * Leaves the graph untouched.
     */
    List<List<Node>> decomposeIntoSinks();

    /*
     * Decompose the graph into sources until no node remains.
     * Leaves the graph untouched.
     */
    List<List<Node>> decomposeIntoSources();

    /*
     * Find the sinks of a graph and return them. Leaves the graph untouched.
     */
    List<Node> getSinks();

    /*
     * Find the source of a graph and return them. Leaves the graph untouched.
     */
    List<Node> getSources();

}
