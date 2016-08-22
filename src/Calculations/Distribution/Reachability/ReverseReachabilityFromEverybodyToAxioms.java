package Calculations.Distribution.Reachability;

import Graph.Algorithms.Decomposition.Evaluators.AxiomEvaluator;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelType;
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
public class ReverseReachabilityFromEverybodyToAxioms {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        try (Transaction tx = graph.beginTx()) {

            ResourceIterable<Node> allNodes = GlobalGraphOperations.at(graph).getAllNodes();
            Map<String, Integer> calculate = reachabilityFromSource
                    .evaluator(new AxiomEvaluator())
                    .reverseGraph()
                    .calculate(allNodes.iterator(), RelType.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

            ExportMapToTXT.export("reverse_reach_distribution_everyone_to_axioms", calculate, new String[]{"id", "name", "count"});
        }

    }

}
