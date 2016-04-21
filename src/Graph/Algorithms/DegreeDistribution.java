package Graph.Algorithms;

import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class DegreeDistribution {

    private Map<Integer, Double> outterDegrees;
    private Map<Integer, Double> innerDegrees;
    private Map<Integer, Double> allDegrees;
    
    private final GraphDatabaseService graph;

    public DegreeDistribution(GraphDatabaseService graph) {
        this.graph = graph;
    }
    
    
}
