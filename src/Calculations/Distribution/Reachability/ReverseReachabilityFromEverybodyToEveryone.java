package Calculations.Distribution.Reachability;

import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelTypes;
import Utils.ExportMapToTXT;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Reuel
 */
public class ReverseReachabilityFromEverybodyToEveryone {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            Map<String, Integer> calculate = reachabilityFromSource
                    .reverseGraph()
                    .calculate(allNodes.iterator(), RelTypes.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

            ExportMapToTXT.export("reach_distribution_everyone_to_everybody_reverse", calculate, new String[]{"id", "name", "count"});
        }

    }

}
