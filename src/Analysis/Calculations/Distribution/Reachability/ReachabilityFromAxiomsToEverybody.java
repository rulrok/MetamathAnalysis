package Analysis.Calculations.Distribution.Reachability;

import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.Label;
import Graph.RelType;
import Utils.ExportMapToTXT;
import Utils.HistogramUtils;
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
        final String OUTPUT = "reach_distribution_axioms_to_everybody";

        ExportMapToTXT.export(OUTPUT, calculate, new String[]{"id", "axiom name", "count"});

        Map<Integer, Integer> histogram = HistogramUtils.CreateHistogramFromMapBasedOn(calculate);
        ExportMapToTXT.export(OUTPUT.concat("_histogram"), histogram);
    }
}
