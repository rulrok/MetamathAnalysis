package Graph.Algorithms;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class DegreeDistribution {

    private final Map<Integer, Integer> outterDegrees;
    private final Map<Integer, Integer> innerDegrees;
    private final Map<Integer, Integer> allDegrees;
    private final GraphDatabaseService graph;

    public DegreeDistribution(GraphDatabaseService graph) {
        this.graph = graph;

        outterDegrees = new TreeMap<>();
        innerDegrees = new TreeMap<>();
        allDegrees = new TreeMap<>();
    }

    public void calculate() {

        outterDegrees.clear();
        innerDegrees.clear();
        allDegrees.clear();

        ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

        //TODO: Improve this!!!
        allNodes.forEach((Node node) -> {
            int allDegree = node.getDegree();
            allDegrees.put(allDegree, allDegrees.getOrDefault(allDegree, 0) + 1);

            int innerDegree = node.getDegree(Direction.INCOMING);
            innerDegrees.put(innerDegree, innerDegrees.getOrDefault(innerDegree, 0) + 1);

            int outterDegree = node.getDegree(Direction.OUTGOING);
            outterDegrees.put(outterDegree, outterDegrees.getOrDefault(outterDegree, 0) + 1);
        });
    }

    public Map<Integer, Integer> getOutterDegrees() {

        return Collections.unmodifiableMap(outterDegrees);
    }

    public Map<Integer, Integer> getInnerDegrees() {
        return Collections.unmodifiableMap(innerDegrees);
    }

    public Map<Integer, Integer> getAllDegrees() {
        return Collections.unmodifiableMap(allDegrees);
    }

}
