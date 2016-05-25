package Calculations.Distribution.Reachability;

import Graph.Algorithms.Decomposition.Evaluators.SinkEvaluator;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelTypes;
import Utils.ExportMapToTXT;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ReachabilityFromAxiomsToSinks {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeDefaultMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        Map<String, Integer> calculate = reachabilityFromSource
                .evaluator(new SinkEvaluator())
                .calculate(Label.AXIOM, RelTypes.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

        ExportMapToTXT.export("reach_distribution_axioms_to_sink", calculate, new String[]{"id", "axiom name", "count"});

    }
}
