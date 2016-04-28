package Graph.Algorithms.Contracts;

import Graph.Algorithms.DecompositionTarget;
import java.util.List;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public interface GraphDecomposition {

    List<List<Node>> execute(DecompositionTarget decompositionTarget, List<Node> initialNodes);
    
}
