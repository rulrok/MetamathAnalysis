package Graph.Algorithms;

import java.util.Collections;
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

    public Map<Integer, Double> getOutterDegrees() {

        return Collections.unmodifiableMap(outterDegrees);
    }

    public Map<Integer, Double> getInnerDegrees() {
        return Collections.unmodifiableMap(innerDegrees);
    }

    public Map<Integer, Double> getAllDegrees() {
        return Collections.unmodifiableMap(allDegrees);
    }

}
