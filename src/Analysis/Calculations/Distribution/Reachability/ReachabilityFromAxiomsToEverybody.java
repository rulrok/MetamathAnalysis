package Analysis.Calculations.Distribution.Reachability;

import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelType;
import Utils.ExportMapToTXT;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Reuel
 */
public class ReachabilityFromAxiomsToEverybody {

    public static void main(String[] args) {
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        Map<String, Integer> calculate = reachabilityFromSource.calculate(Label.AXIOM, RelType.SUPPORTS);
//        calculate.forEach((key, value) -> {
//            System.out.println(key + "\t" + value);
//        });

        ExportMapToTXT.export("reach_distribution_axioms_to_everybody", calculate, new String[]{"id", "axiom name", "count"});

    }
}
