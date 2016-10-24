package Analysis.Calculations.Distribution.Reachability;

import Graph.Algorithms.Decomposition.Evaluators.AxiomEvaluator;
import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelType;
import Utils.ExportMapToTXT;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Reuel
 */
public class ReverseReachabilityFromSinksToAxioms {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        List<Node> originalSinks = new SimpleGraphDecomposition(graph)
                .getSinks();

        Map<String, Integer> calculate = reachabilityFromSource
                .evaluator(new AxiomEvaluator())
                .reverseGraph()
                .calculate(originalSinks.iterator(), RelType.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

        ExportMapToTXT.export("reverse_reach_distribution_sinks_to_axioms", calculate, new String[]{"id", "name", "count"});

    }

}
