package Analysis.Calculations.Distribution.Reachability;

import Graph.Algorithms.Decomposition.SimpleGraphDecomposition;
import Graph.Algorithms.ReachabilityFromNode;
import Graph.GraphFactory;
import Graph.RelType;
import Utils.ExportMapToTXT;
import Utils.HistogramUtils;
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
        GraphDatabaseService graph = GraphFactory.makeNoUserboxesNoJunkAxiomTheoremMetamathGraph();
        ReachabilityFromNode reachabilityFromSource = new ReachabilityFromNode(graph);

        List<Node> originalSinks = new SimpleGraphDecomposition(graph)
                .getSinks();

        Map<String, Integer> calculate = reachabilityFromSource
                .reverseGraph()
                .calculateFromNodes(originalSinks.iterator(), RelType.SUPPORTS);
        final String OUTPUT = "reverse_reach_distribution_sinks_to_everyone";

        ExportMapToTXT.export(OUTPUT, calculate, new String[]{"id", "name", "count"});

        Map<Integer, Integer> histogram = HistogramUtils.CreateHistogramFromMapBasedOn(calculate);

        ExportMapToTXT.export(OUTPUT.concat("_histogram"), histogram);

    }

}
