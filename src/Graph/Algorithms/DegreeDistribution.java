package Graph.Algorithms;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class DegreeDistribution {

    private final Map<Integer, Integer> outerDegrees;
    private final Map<Integer, Integer> innerDegrees;
    private final Map<Integer, Integer> allDegrees;
    private final GraphDatabaseService graph;

    public DegreeDistribution(GraphDatabaseService graph) {
        this.graph = graph;

        outerDegrees = new TreeMap<>();
        innerDegrees = new TreeMap<>();
        allDegrees = new TreeMap<>();
    }

    public void calculate() {

        outerDegrees.clear();
        innerDegrees.clear();
        allDegrees.clear();

        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();

            //TODO: Improve this!!!
            allNodes.forEach((Node node) -> {
                int allDegree = node.getDegree();
                allDegrees.put(allDegree, allDegrees.getOrDefault(allDegree, 0) + 1);

                int innerDegree = node.getDegree(Direction.INCOMING);
                innerDegrees.put(innerDegree, innerDegrees.getOrDefault(innerDegree, 0) + 1);

                int outerDegree = node.getDegree(Direction.OUTGOING);
                outerDegrees.put(outerDegree, outerDegrees.getOrDefault(outerDegree, 0) + 1);
            });
            
            tx.failure();
        }
    }

    public Map<Integer, Integer> getOuterDegrees() {

        return Collections.unmodifiableMap(outerDegrees);
    }

    public Map<Integer, Integer> getInnerDegrees() {
        return Collections.unmodifiableMap(innerDegrees);
    }

    public Map<Integer, Integer> getAllDegrees() {
        return Collections.unmodifiableMap(allDegrees);
    }

}
