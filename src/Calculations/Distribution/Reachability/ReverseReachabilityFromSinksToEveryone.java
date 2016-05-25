package Calculations.Distribution.Reachability;

import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelTypes;
import Utils.ExportMapToTXT;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class ReverseReachabilityFromSinksToEveryone {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        List<Node> originalSinks = new SimpleGraphDecomposition(graph)
                .getSinks();

        Map<String, Integer> calculate = reachabilityFromSource
                .reverseGraph()
                .calculate(originalSinks.iterator(), RelTypes.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

        ExportMapToTXT.export("reverse_reach_distribution_sinks_to_everyone", calculate, new String[]{"id", "name", "count"});

    }

}
