package Tests;

import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Reuel
 */
public class ReacheabilityFromNode {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeTestGraphJ();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        try (Transaction tx = graph.beginTx()) {

            Node eNode = graph.getNodeById(4);

            Map<String, Integer> calculate = reachabilityFromSource.calculate(eNode, RelTypes.SUPPORTS);
            //Should yeld 10 of reachability
            calculate.forEach((key, value) -> {
                System.out.println(key + "\t" + value);
            });
        }
    }

}
