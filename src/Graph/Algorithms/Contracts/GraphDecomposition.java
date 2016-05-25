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
    
    List<List<Node>> decomposeIntoSinks();
    
    List<List<Node>> decomposeIntoSources();
    
    
}
