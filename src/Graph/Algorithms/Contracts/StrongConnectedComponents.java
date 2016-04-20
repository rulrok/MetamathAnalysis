package Graph.Algorithms.Contracts;

import java.util.List;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public interface StrongConnectedComponents {

    /**
     * Execute the algorithm and return a list of components.
     * 
     * @return 
     */
    List<List<Node>> execute();
    
}
